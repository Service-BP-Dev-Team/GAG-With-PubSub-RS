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
// $Id: SelectableVirtPipeInput.java 2891 2009-07-20 14:01:58Z bboussema $
package inria.lognet.virtpipes;

import inria.lognet.channels.ChannelStrategy;
import inria.lognet.channels.ChannelsPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Specific implementation of a VirtualPipe for a stream, not messages. You need
 * to be sure that only one SelectableVirtPipeOutput talks to only one
 * SelectableVirtPipeInput in your application model. Otherwise, it doesn't
 * work. It is registered into the ChannelsPool for the internal part, but you
 * need to read the other part with your own thread.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2891 $
 */
public class SelectableVirtPipeInput implements VirtPipeInput {

	private final Map<SelectableChannel, List<ByteBuffer>> queues = new HashMap<SelectableChannel, List<ByteBuffer>>();

	private final UUID id = UUID.randomUUID();

	private final Pipe pipe;

	private final ChannelsPool channelsPool;

	public SelectableVirtPipeInput(final VirtPipesService vps)
			throws IOException {
		channelsPool = vps.getServerSocket().getChannelsPool();
		pipe = Pipe.open();
		pipe.sink().configureBlocking(false);
		channelsPool.addChannel(pipe.sink(), new ChannelStrategy(
				SelectionKey.OP_WRITE) {

			@Override
			protected void accept(final SelectionKey key,
					final SelectableChannel channel) {}

			@Override
			protected void closed(final SelectableChannel channel) {}

			@Override
			protected void connect(final SelectionKey key,
					final SelectableChannel channel) {}

			@Override
			protected void read(final SelectionKey key,
					final SelectableChannel channel) {}

			@Override
			protected void write(final SelectionKey key,
					final SelectableChannel channel) {
				final SinkChannel socketChannel = (SinkChannel) key.channel();

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
								Activator.LOGGER
										.throwing(
												getClass().getName(),
												"write(SelectionKey, SelectableChannel)",
												e1);
							}
							return;
						}
						if (buf.remaining() > 0) {
							// ... or the socket's
							// buffer
							// fills up
							break;
						}
						queue.remove(0);
					}
				}
			}

		});
		vps.registerVirtPipe(this);
	}

	public void close() throws IOException {
		pipe.sink().close();
	}

	public UUID getId() {
		return id;
	}

	/**
	 * Use this to read information from this SelectableVirtPipeInput. You
	 * should use Java NIO to Java IO class.
	 * 
	 * @return SourceChannel of this VirtPipeInput
	 */
	public SourceChannel getSource() {
		return pipe.source();
	}

	public void read(final ByteBuffer buffer) {
		List<ByteBuffer> queue = queues.get(pipe.sink());
		if (queue == null) {
			queue = new LinkedList<ByteBuffer>();
			queues.put(pipe.sink(), queue);
		}
		queue.add(buffer);
		channelsPool.notifySelector();
	}

}
