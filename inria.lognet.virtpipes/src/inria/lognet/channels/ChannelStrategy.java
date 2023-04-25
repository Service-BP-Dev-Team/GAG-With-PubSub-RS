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
// $Id: ChannelStrategy.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.channels;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Observable;

/**
 * Common polymorphism for ChannelsPool. You need to inherit this class or
 * subclasses, such as ServerSocketChannelStrategy for a TCP server socket and
 * SocketChannelStrategy for TCP client socket. It opens ChannelsPool to any
 * specific implementation by polymorphism. Normally, you specify one strategy
 * per purpose. A strategy can handle many channels.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public abstract class ChannelStrategy extends Observable {

	private ChannelsPool channelsPool = null;
	private final int initOps;

	public ChannelStrategy(final int initOps) {
		this.initOps = initOps;
	}

	protected abstract void accept(final SelectionKey key,
			final SelectableChannel channel);

	protected abstract void closed(SelectableChannel channel);

	protected abstract void connect(final SelectionKey key,
			final SelectableChannel channel);

	public final ChannelsPool getChannelsPool() {
		return channelsPool;
	}

	final int getInitOps() {
		return initOps;
	}

	protected abstract void read(final SelectionKey key,
			final SelectableChannel channel);

	public final void setChannelsPool(final ChannelsPool channelsPool) {
		if (channelsPool == null) {
			throw new IllegalArgumentException(
					"Cannot associate this Channel strategy to a null channelsPool");
		}
		if (!channelsPool.equals(this.channelsPool)) {
			if (this.channelsPool != null) {
				throw new IllegalStateException(
						"This Channel strategy has already been associated to a channelsPool");
			}
			this.channelsPool = channelsPool;
		}
	}

	protected abstract void write(final SelectionKey key,
			final SelectableChannel channel);

}
