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
// $Id: ServerSocketChannelStrategy.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.channels;

import inria.lognet.virtpipes.Activator;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Common for TCP server usage. It accepts clients, and associate them to a
 * SocketChannelStrategy passed.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public final class ServerSocketChannelStrategy extends ChannelStrategy {

	private final SocketChannelStrategy clientChannelStrategy;

	public ServerSocketChannelStrategy(
			final SocketChannelStrategy clientChannelStrategy) {
		super(SelectionKey.OP_ACCEPT);
		this.clientChannelStrategy = clientChannelStrategy;
	}

	@Override
	protected final void accept(final SelectionKey key,
			final SelectableChannel channel) {
		// For an accept to be pending the channel must
		// be a server
		// socket
		// channel.
		final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();

		// Accept the connection and make it
		// non-blocking
		SocketChannel socketChannel;
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
		} catch (final IOException e) {
			Activator.LOGGER.throwing(getClass().getName(),
					"accpet(SelectionKey, SelectableChannel)", e);
			return;
		}

		getChannelsPool().addChannel(socketChannel, clientChannelStrategy,
				SelectionKey.OP_READ);

		clientChannelStrategy.accepted(socketChannel);
	}

	@Override
	protected final void closed(final SelectableChannel channel) {
		Activator.LOGGER.info(channel + " closed");
	}

	@Override
	public final void connect(final SelectionKey key,
			final SelectableChannel channel) {}

	@Override
	public final void read(final SelectionKey key,
			final SelectableChannel channel) {}

	@Override
	public final void write(final SelectionKey key,
			final SelectableChannel channel) {}
}
