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
// $Id$
package inria.lognet.virtpipes;

import inria.lognet.channels.ChannelsPool;
import inria.lognet.channels.ServerSocketChannelStrategy;
import inria.lognet.channels.SocketChannelStrategy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stratégie d'acceptation de nouveaux clients, normalement des VirtPipesService
 * distants qui viennent se connecter au VPS local.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
class VirtPipesServiceServerSocket extends SocketChannelStrategy {

	/**
	 * Buffer of incomplete messages.
	 */
	final Map<SocketChannel, List<ByteBuffer>> incompletes = new HashMap<SocketChannel, List<ByteBuffer>>();

	final Map<SocketChannel, VirtPipeMessage> incompleteMessages = new HashMap<SocketChannel, VirtPipeMessage>();

	/**
	 * Defines the active connections to remote Peers.
	 */
	final Map<SocketChannel, UUID> activeClients = new HashMap<SocketChannel, UUID>();

	private final VirtPipesService vps;

	/**
	 * Defines the active connections to remote Peers.
	 */
	final Map<UUID, SocketChannel> connections = new HashMap<UUID, SocketChannel>();

	/**
	 * The channel that accepts any connection.
	 */
	final ServerSocketChannel serverChannel;

	/**
	 * JSON formatted of this VirtPipesService server socket addresses and port.
	 */
	final String ip;

	public VirtPipesServiceServerSocket(final VirtPipesService vps,
			final int port) throws IOException {
		this.vps = vps;
		{ // inria.lognet.channels, Java NIO, ServerSocket
			setChannelsPool(new ChannelsPool());
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			getChannelsPool().addChannel(serverChannel,
					new ServerSocketChannelStrategy(this));
		} // End inria.lognet.channels, Java NIO, ServerSocket
		{ // IPs (formatted like JSON)
			final StringBuilder myIpsBuilder = new StringBuilder();
			myIpsBuilder.append("{");
			for (final Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				final NetworkInterface ni = en.nextElement();
				for (final Enumeration<InetAddress> en2 = ni.getInetAddresses(); en2
						.hasMoreElements();) {
					final InetAddress address = en2.nextElement();
					myIpsBuilder.append("\"");
					myIpsBuilder.append(address.toString());
					myIpsBuilder.append("\"");
					if (en2.hasMoreElements()) {
						myIpsBuilder.append(",");
					}
				}
				if (en.hasMoreElements()) {
					myIpsBuilder.append(",");
				}
			}
			myIpsBuilder.append("},");
			myIpsBuilder.append(serverChannel.socket().getLocalPort());
			ip = myIpsBuilder.toString();
		} // End IPs (formatted like JSON)
	}

	@Override
	protected void accepted(final SocketChannel channel) {
		// une nouvelle connexion a été acceptée, alors on envoie son identité
		// (le ID du VPS local)
		send(channel, VirtPipeMessage.format(VirtPipeMessage.INIT, vps.getId()));
	}

	public void close() {
		// on ferme tout
		try {
			serverChannel.close();
		} catch (final IOException e1) {
			Activator.LOGGER.throwing(getClass().getName(), "close()", e1);
		}
		for (final SocketChannel socketChannel : activeClients.keySet()) {
			try {
				socketChannel.close();
			} catch (final IOException e) {
				Activator.LOGGER.throwing(getClass().getName(), "close()", e);
			}
		}
		activeClients.clear();
		for (final SocketChannel socketChannel : connections.values()) {
			try {
				socketChannel.close();
			} catch (final IOException e) {
				Activator.LOGGER.throwing(getClass().getName(), "close()", e);
			}
		}
		connections.clear();
		incompletes.clear();
	}

	@Override
	protected void closed(final SelectableChannel channel) {
		// notification qu'une connexion TCP a été fermé, on supprime les
		// références à cette connexion un peu partout
		synchronized (activeClients) {
			synchronized (connections) {
				final UUID id = activeClients.get(channel);
				activeClients.remove(channel);
				connections.remove(id);
			}
		}
	}

	@Override
	protected void connected(final SocketChannel channel) {
		// la demande de connexion venant de notre part à un VPS distant s'est
		// terminé. On est donc connecté et on envoit notre identité, le VpsId.
		send(channel, VirtPipeMessage.format(VirtPipeMessage.INIT, vps.getId()));
	}

	@Override
	protected void read(final SocketChannel socketChannel,
			final ByteBuffer byteBuffer, final int size) {
		// on lit tous les paquets venant des connexions TCP.
		// on reconstitue les messages qui ont pu être découpés.
		List<ByteBuffer> bufs = incompletes.get(socketChannel);
		if (bufs == null) {
			bufs = new LinkedList<ByteBuffer>();
			incompletes.put(socketChannel, bufs);
		}
		bufs.add(byteBuffer);
		// on tente de lire le message avec tous les morceaux qui ont déjà pu
		// être traités auparavant.
		final VirtPipeMessage msg = VirtPipeMessage.readVirtPipeMessage(bufs,
				incompleteMessages.get(socketChannel));
		if (msg != null) {
			if (msg.getBuf().remaining() == 0) {
				// message complet
				incompleteMessages.remove(socketChannel);
				// on le transmet enfin au VPS, pour qu'il l'interprète et si
				// c'est du type NORMAL, alors il le transmettra à tous les
				// tuyaux virtuels d'entrée idéntifiés par le ID du message.
				vps.read(msg, socketChannel);
				// on nettoie tout le travail
				bufs.clear();
				if (byteBuffer.remaining() > 0) {
					bufs.add(byteBuffer);
				}
			} else {
				// on n'a pas le message complètement, alors on le met en
				// attente
				incompleteMessages.put(socketChannel, msg);
			}
		}
	}

}
