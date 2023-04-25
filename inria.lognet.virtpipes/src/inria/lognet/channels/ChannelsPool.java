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
// $Id: ChannelsPool.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.channels;

import inria.lognet.virtpipes.Activator;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Common use: 1 object of this class, 1 thread, that holds many
 * SelectableChannel. Simplify the use of Java NIO. Depending on the
 * ChannelStragy applied to a Channel, you can polymorph how you receive, send
 * information through a channel.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public final class ChannelsPool implements Runnable {

	private final class ChangeRequest {

		public static final int CHANGEOPS = 2;
		public static final int REGISTER = 1;

		public SelectableChannel channel;
		public int ops;
		public int type;

		public ChangeRequest(final SelectableChannel channel, final int type,
				final int ops) {
			if (channel == null) {
				throw new IllegalArgumentException();
			}
			this.channel = channel;
			this.type = type;
			this.ops = ops;
		}
	}

	private static final long origin = 0l;

	// A list of PendingChange instances
	private final List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

	// The selector we'll be monitoring
	private final Selector selector;

	private final Map<SelectableChannel, ChannelStrategy> strategies = new HashMap<SelectableChannel, ChannelStrategy>();

	private StringBuilder x;

	private StringBuilder y;

	public ChannelsPool() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}

	public ChannelsPool(final StringBuilder x, final StringBuilder y)
			throws IOException {
		this();
		this.x = x;
		this.y = y;
	}

	public final void addChannel(final SelectableChannel channel,
			final ChannelStrategy strategy) {
		synchronized (pendingChanges) {
			pendingChanges.add(new ChangeRequest(channel,
					ChangeRequest.REGISTER, strategy.getInitOps()));
		}
		synchronized (strategies) {
			strategies.put(channel, strategy);
		}
		strategy.setChannelsPool(this);
		selector.wakeup();
	}

	public final void addChannel(final SelectableChannel channel,
			final ChannelStrategy strategy, final int ops) {
		synchronized (pendingChanges) {
			pendingChanges.add(new ChangeRequest(channel,
					ChangeRequest.REGISTER, ops));
		}
		synchronized (strategies) {
			strategies.put(channel, strategy);
		}
		strategy.setChannelsPool(this);
		selector.wakeup();
	}

	public final void addChannels(
			final Map<SelectableChannel, ChannelStrategy> channels) {
		for (final SelectableChannel channel : channels.keySet()) {
			addChannel(channel, channels.get(channel));
		}
	}

	public final void changeOperations(final SelectableChannel channel,
			final int ops) {
		synchronized (pendingChanges) {
			pendingChanges.add(new ChangeRequest(channel,
					ChangeRequest.CHANGEOPS, ops));
		}
		selector.wakeup();
	}

	public final Set<SelectableChannel> moveAllChannels() {
		return new HashSet<SelectableChannel>();
	}

	public final Set<SelectableChannel> moveChannels(final int number) {
		return new HashSet<SelectableChannel>();
	}

	public void notifySelector() {
		selector.wakeup();
	}

	public final void run() {
		while (!Thread.currentThread().isInterrupted()) {
			// Process any pending changes
			synchronized (pendingChanges) {
				final Iterator<ChangeRequest> changes = pendingChanges
						.iterator();
				while (changes.hasNext()) {
					final ChangeRequest change = changes.next();

					switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							final SelectionKey key = change.channel
									.keyFor(selector);
							try {
								key.interestOps(change.ops);
							} catch (final CancelledKeyException e) {
								strategies.remove(key.channel());
							}
							break;
						case ChangeRequest.REGISTER:
							try {
								change.channel.register(selector, change.ops);
							} catch (final ClosedChannelException e) {
								//
							}
							break;
					}
				}
				pendingChanges.clear();
			}

			// Wait for an event one of the registered channels
			try {
				if (y != null) {
					y.append(System.nanoTime() - ChannelsPool.origin).append(
							",").append(0).append('\n');
				}
				selector.select();
				if (y != null) {
					y.append(System.nanoTime() - ChannelsPool.origin).append(
							",").append(1).append('\n');
				}
			} catch (final IOException e) {
				Activator.LOGGER.throwing(getClass().getName(), "run()", e);
			}

			int n = 0;
			// Iterate over the set of keys for which events are available
			final Iterator<SelectionKey> selectedKeys = selector.selectedKeys()
					.iterator();
			while (selectedKeys.hasNext()) {
				final SelectionKey key = selectedKeys.next();
				n++;
				selectedKeys.remove();

				if (!key.isValid()) {
					key.cancel();
					strategies.remove(key.channel());
					continue;
				}

				final ChannelStrategy strategy = strategies.get(key.channel());
				if (strategy != null) {
					try {
						if (key.isAcceptable()) {
							strategy.accept(key, key.channel());
						}
						if (key.isConnectable()) {
							strategy.connect(key, key.channel());
						}
						if (key.isReadable()) {
							strategy.read(key, key.channel());
						}
						if (key.isWritable()) {
							strategy.write(key, key.channel());
						}
					} catch (final CancelledKeyException e) {
						strategies.remove(key.channel());
						strategy.closed(key.channel());
					}
				} else {
					key.cancel();
				}
			}
			if (x != null) {
				x.append(System.nanoTime() - ChannelsPool.origin).append(",")
						.append(n).append('\n');
			}
		}
	}
}
