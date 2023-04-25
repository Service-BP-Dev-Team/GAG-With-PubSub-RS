/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
 * 
 *    This file is part of LogNet project
 *    <a href="http://www-sop.inria.fr/teams/lognet">LogNet</a>
 * 
 *     LogNet is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     LogNet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with LogNet; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: SocketChannelStrategy.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.channels;

import inria.lognet.virtpipes.Activator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents how to deal with TCP connections.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public abstract class SocketChannelStrategy extends ChannelStrategy {

	private final Map<SelectableChannel, List<ByteBuffer>> queues = new HashMap<SelectableChannel, List<ByteBuffer>>();

	public SocketChannelStrategy() {
		this(SelectionKey.OP_CONNECT);
	}

	public SocketChannelStrategy(final int initOps) {
		super(initOps);
	}

	@Override
	public final void accept(final SelectionKey key,
			final SelectableChannel channel) {}

	/**
	 * Tell that the framework has accepted a new client.
	 * 
	 * @param channel
	 *            TCP connection with the new client
	 */
	protected abstract void accepted(SocketChannel channel);

	@Override
	public final void connect(final SelectionKey key,
			final SelectableChannel channel) {
		final SocketChannel socketChannel = (SocketChannel) key.channel();

		// Finish the connection. If the connection
		// operation failed
		// this will raise an IOException.
		try {
			socketChannel.finishConnect();
		} catch (final IOException e) {
			// Cancel the channel's registration with
			// our selector
			Activator.LOGGER.throwing(getClass().getName(),
					"connect(SelectionKey, SelectableChannel)", e);
			key.cancel();
			return;
		}

		connected((SocketChannel) channel);
	}

	/**
	 * Notify that the connection asked with connect() is finished, you can send
	 * data.
	 * 
	 * @param channel
	 *            now fully connected
	 */
	protected abstract void connected(SocketChannel channel);

	@Override
	public final void read(final SelectionKey key,
			final SelectableChannel channel) {
		final SocketChannel socketChannel = (SocketChannel) key.channel();
		final ByteBuffer readBuffer = ByteBuffer.allocate(1048576);

		// Attempt to read off the
		// channel
		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
		} catch (final IOException e) {
			// The remote forcibly
			// closed the
			// connection, cancel
			// the selection key and
			// close the
			// channel.
			key.cancel();
			try {
				socketChannel.close();
			} catch (final IOException e1) {
				Activator.LOGGER.throwing(getClass().getName(),
						"read(SelectionKey, SelectableChannel)", e);
			}
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the
			// socket down
			// cleanly. Do the
			// same from our end and
			// cancel the
			// channel.
			try {
				key.channel().close();
			} catch (final IOException e) {
				Activator.LOGGER.throwing(getClass().getName(),
						"read(SelectionKey, SelectableChannel)", e);
			}
			key.cancel();
			return;
		}
		readBuffer.flip();
		read(socketChannel, readBuffer, numRead);
	}

	/**
	 * Data read through a TCP connection
	 * 
	 * @param socketChannel
	 * @param byteBuffer
	 * @param size
	 */
	protected abstract void read(SocketChannel socketChannel,
			ByteBuffer byteBuffer, int size);

	/**
	 * Call this when you want to send a data.
	 * 
	 * @param channel
	 * @param byteBuffer
	 */
	public final void send(final SelectableChannel channel,
			final ByteBuffer byteBuffer) {
		synchronized (queues) {
			List<ByteBuffer> queue = queues.get(channel);
			if (queue == null) {
				queue = new ArrayList<ByteBuffer>();
				queues.put(channel, queue);
			}
			queue.add(byteBuffer);
		}

		getChannelsPool().changeOperations(channel, SelectionKey.OP_WRITE);
	}

	@Override
	public final void write(final SelectionKey key,
			final SelectableChannel channel) {

		boolean more = false;
		final SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (queues) {
			final List<ByteBuffer> queue = queues.get(socketChannel);

			// Write until there's not more
			// data ...
			while (queue != null && !queue.isEmpty()) {
				final ByteBuffer buf = queue.get(0);
				try {
					socketChannel.write(buf);
				} catch (final IOException e) {
					try {
						socketChannel.close();
					} catch (final IOException e1) {
						Activator.LOGGER.throwing(getClass().getName(),
								"write(SelectionKey, SelectableChannel)", e);
					}
					return;
				}
				if (buf.remaining() > 0) {
					more = true;
					break;
				}
				queue.remove(0);
			}
		}

		getChannelsPool().changeOperations(socketChannel, SelectionKey.OP_READ);
		if (more) {
			getChannelsPool().changeOperations(socketChannel,
					SelectionKey.OP_WRITE);
		}
	}

}
