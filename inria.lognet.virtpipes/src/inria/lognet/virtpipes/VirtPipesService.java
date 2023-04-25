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
// $Id: VirtPipesService.java 2896 2009-07-23 07:20:06Z bboussema $
package inria.lognet.virtpipes;

import inria.lognet.channels.ChannelStrategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Classe principale du système VirtPipes.<br>
 * - On enregistre les tuyaux virtuels d'entrée dans VirtPipesService, qui va se
 * charger de publier la référence de ce tuyau.<br>
 * - On associe la référence de VirtPipesService aux tuyaux virtuels de sortie,
 * pour qu'ils puissent envoyer des messages.<br>
 * VirtPipesService se charge de recevoir et d'envoyer les messages, passant à
 * travers les tuyaux virtuel, à travers des canaux TCP.<br>
 * Les canaux TCP permettent d'atteindre d'autres VirtPipesServices qui
 * transmettront les messages des tuyaux virtuels aux bons tuyaux virtuels
 * d'entrée.<br>
 * VirtPipesService contient un mécanisme d'auto-connexion des canaux TCP.<br>
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2896 $
 */
public final class VirtPipesService {

	/**
	 * TCP Server socket to accept new remote VirtPipesServices to connect
	 */
	private final VirtPipesServiceServerSocket serverSocket;

	/**
	 * OSGi tracker of VirtPipesModules
	 */
	private final ServiceTracker virtPipesDsTracker;

	/**
	 * Current pending TCP connections to remote VirtPipesService.
	 */
	private final Map<UUID, SocketChannel> connecting = new HashMap<UUID, SocketChannel>();

	/**
	 * List of messages that needs to be sent.
	 */
	private final Map<UUID, List<VirtPipeMessage>> pendingVirtMessagesToBeSent = new HashMap<UUID, List<VirtPipeMessage>>();

	/**
	 * The VirtPipes that this instance reads and that any other computer can
	 * send data to them.
	 */
	private final Map<UUID, Set<VirtPipeInput>> virtPipeInputs = new HashMap<UUID, Set<VirtPipeInput>>();

	/**
	 * All enabled VirtPipesModules found as OSGi service by the tracker
	 */
	private final List<VirtPipesModule> virtPipesModules = new ArrayList<VirtPipesModule>();

	/**
	 * This VirtPipesService's own pipe, which identify VirtPipesService
	 */
	private final VirtPipesServiceVirtPipeInput vpi = new VirtPipesServiceVirtPipeInput(
			this);

	/**
	 * Creates a VirtPipesService without a DHT OpenChord support. Local and
	 * test usage only, as no exchange mecanism exists in this version.
	 * 
	 * @param i
	 * @throws IOException
	 */
	public VirtPipesService(final int port, final BundleContext context)
			throws IOException {
		serverSocket = new VirtPipesServiceServerSocket(this, port);
		(virtPipesDsTracker = new ServiceTracker(context, VirtPipesModule.class
				.getName(), new ServiceTrackerCustomizer() {

			public Object addingService(final ServiceReference reference) {
				final Object o = context.getService(reference);
				if (o instanceof VirtPipesModule) {
					((VirtPipesModule) o)
							.setVirtPipesService(VirtPipesService.this);
					addVirtPipesModule((VirtPipesModule) o);
				}
				return o;
			}

			public void modifiedService(final ServiceReference reference,
					final Object service) {}

			public void removedService(final ServiceReference reference,
					final Object service) {
				try {
					removeVirtPipesModule((VirtPipesModule) service);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

		})).open();
		{ // VirtPipeService's own VirtPipeInput
			registerVirtPipe(vpi);
		} // End VirtPipeService's own VirtPipeInput
	}

	private void addVirtPipesModule(final VirtPipesModule o) {
		synchronized (virtPipesModules) {
			virtPipesModules.add(o);
		}
		for (final UUID id : virtPipeInputs.keySet()) {
			if (virtPipeInputs.get(id) != null
					&& virtPipeInputs.get(id).size() > 0) {
				o.registerVirtPipeInput(id);
			}
		}
	}

	/**
	 * Close everything in this VirtPipesService
	 */
	public final void close() {
		if (serverSocket != null) {
			serverSocket.close();
		}
		pendingVirtMessagesToBeSent.clear();
		connecting.clear();
		virtPipeInputs.clear();
		virtPipesDsTracker.close();
		synchronized (virtPipesModules) {
			virtPipesModules.clear();
		}
	}

	/**
	 * Remove all VirtPipeInputs indexed by id.
	 * 
	 * @param id
	 */
	public final void close(final UUID id) {
		synchronized (virtPipeInputs) {
			virtPipeInputs.remove(id);
		}
		synchronized (virtPipesModules) {
			for (final VirtPipesModule vpMod : virtPipesModules) {
				vpMod.unregisterVirtPipe(id);
			}
		}
	}

	/**
	 * Remove the VirtPipeInput pipe.
	 * 
	 * @param pipe
	 */
	public final void close(final VirtPipeInput pipe) {
		synchronized (virtPipeInputs) {
			final Set<VirtPipeInput> vpis = virtPipeInputs.get(pipe.getId());
			if (vpis != null && vpis.size() > 1) {
				vpis.remove(pipe);
			} else {
				close(pipe.getId());
			}
		}
	}

	/**
	 * Try to Connect this VPS to a remote VPS
	 * 
	 * @param socketAddress
	 * @return
	 * @throws IOException
	 */
	public final SocketChannel connect(final InetSocketAddress socketAddress)
			throws IOException {
		final SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(socketAddress);
		serverSocket.getChannelsPool().addChannel(socketChannel, serverSocket,
				SelectionKey.OP_CONNECT);
		return socketChannel;
	}

	/**
	 * Try to connect to a remote VPS (vpsId) with the following IPs.
	 * 
	 * @param vpsId
	 *            the remote VirtPipesService's ID
	 * @param ips
	 *            list of Internet Addresses of the remote VPS we want to
	 *            connect
	 * @return a socket if succesful, otherwise null. This socket has to be put
	 *         in the 'connecting' map.
	 */
	private SocketChannel connect(final UUID vpsId,
			final List<InetSocketAddress> ips) {
		for (final InetSocketAddress address : ips) {
			if (address.getHostName().contains(":")) { // IPV6
				continue;
			}
			try {
				final SocketChannel socket = connect(address);
				Activator.LOGGER.info("NEW Connecting to " + address);
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (connecting.containsValue(socket)) {
							Activator.LOGGER
									.warning("TIMEOUT: Canceling connection to "
											+ address);
							try {
								socket.close();
							} catch (final IOException e) {
								e.printStackTrace();
							}
							final List<InetSocketAddress> listIps = new ArrayList<InetSocketAddress>(
									ips);
							// we remove the IP address we have just tested and
							// that failed.
							listIps.remove(address);
							// And we try again
							final SocketChannel s = connect(vpsId, listIps);
							if (s != null) {
								// very important, for the next TIMEOUT
								connecting.put(vpsId, s);
							}
						} else {
							Activator.LOGGER.fine("TIMEOUT: ok for " + address);
						}
					}

				}, 3000); // TIMEOUT of 3 seconds
				return socket;
			} catch (final IOException e) {
				Activator.LOGGER.throwing(getClass().getName(),
						"send(VirtPipeMessage)", e);
				continue;
			} catch (final UnresolvedAddressException e) {
				Activator.LOGGER.throwing(getClass().getName(),
						"send(VirtPipeMessage)", e);
				continue;
			}
		}
		Activator.LOGGER.warning("VirtPipes -> DHT : connect to " + ips
				+ " FAILED");
		return null;
	}

	/**
	 * Disconnect to a specified VPS
	 * 
	 * @param id
	 */
	public final void disconnect(final UUID id) {
		try {
			final SocketChannel channel = serverSocket.connections.get(id);
			serverSocket.activeClients.remove(channel);
			serverSocket.connections.remove(id);
			channel.close();
		} catch (final IOException e) {
			Activator.LOGGER.throwing(getClass().getName(), "disconnect()", e);
		}
	}

	/**
	 * Generate a unique UUID, verified to every VirtPipesModules
	 * 
	 * @return extremely unique UUID
	 */
	public UUID generateUniqueId() {
		while (true) {
			final UUID id = UUID.randomUUID();
			if (isUuidUnique(id)) {
				return id;
			}
		}
	}

	/**
	 * Useful for Swing JTable view
	 * 
	 * @return
	 */
	public final Object[] getActiveConnections() {
		return serverSocket.connections.keySet().toArray();
	}

	/**
	 * @return identité du VPS
	 */
	public final UUID getId() {
		return vpi.getId();
	}

	/**
	 * @return String representation of this VPS TCP ServerSocket
	 */
	public final String getIp() {
		return serverSocket.ip;
	}

	/**
	 * @return the VirtPipesInputs managed by this VPS
	 */
	public final Vector<UUID> getLocalVirtPipes() {
		final Vector<UUID> v = new Vector<UUID>();
		synchronized (virtPipeInputs) {
			for (final UUID id : virtPipeInputs.keySet()) {
				if (!id.equals(getId())) {
					v.add(id);
				}
			}
		}
		return v;
	}

	/**
	 * @return port of the TCP Server
	 */
	public final int getPort() {
		return serverSocket.serverChannel.socket().getLocalPort();
	}

	/**
	 * @return the strategy of the TCP Server, used by ChannelsPool
	 */
	public ChannelStrategy getServerSocket() {
		return serverSocket;
	}

	/**
	 * Méthode qui renvoi un VirtPipeOutput dont au moins un VirtPipeInput
	 * existe au bout.
	 */
	public final VirtPipeOutput getVirtPipeOutput(final UUID id) {
		if (virtPipeInputs.containsKey(id)) {
			return new VirtPipeOutputDefault(this, id);
		}
		synchronized (virtPipesModules) {
			for (final VirtPipesModule vpMod : virtPipesModules) {
				final VirtPipeOutput output = vpMod.getVirtPipeOutput(id);
				if (output != null) {
					return output;
				}
			}
		}
		Activator.LOGGER.warning("VirtPipesService : getVirtPipeOutput " + id
				+ " FAILURE");
		return null;
	}

	/**
	 * Check to every VirtPipesModules that this id is unique.
	 * 
	 * @param id
	 * @return true is very unique ID
	 */
	private boolean isUuidUnique(final UUID id) {
		synchronized (virtPipesModules) {
			for (final VirtPipesModule vpMod : virtPipesModules) {
				if (!vpMod.isUuidIsUnique(id)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * List of messages that couldn't be sent because of a new TCP connection to
	 * a VPS
	 * 
	 * @param vpsId
	 * @param msg
	 */
	private final void putInPendingMessages(final UUID vpsId,
			final VirtPipeMessage msg) {
		synchronized (pendingVirtMessagesToBeSent) {
			List<VirtPipeMessage> pendingMsgs = pendingVirtMessagesToBeSent
					.get(vpsId);
			if (pendingMsgs == null) {
				pendingMsgs = new LinkedList<VirtPipeMessage>();
				pendingVirtMessagesToBeSent.put(vpsId, pendingMsgs);
			}
			synchronized (pendingMsgs) {
				pendingMsgs.add(msg);
			}
		}
	}

	/**
	 * A VPS message is received, check the type, then transmit to the right
	 * VirtPipeInput
	 * 
	 * @param msg
	 * @param channel
	 */
	final void read(final VirtPipeMessage msg, final SocketChannel channel) {
		if (msg != null && channel != null && msg.getChannelId() != null) {
			switch (msg.getType()) {
				case VirtPipeMessage.INIT:
					Activator.LOGGER.info("VirtPipes run INIT "
							+ msg.getChannelId());
					synchronized (serverSocket.activeClients) {
						serverSocket.activeClients.put(channel, msg
								.getChannelId());
					}
					synchronized (serverSocket.connections) {
						serverSocket.connections.put(msg.getChannelId(),
								channel);
					}
					synchronized (connecting) {
						connecting.remove(msg.getChannelId());
					}
					sendPendingMessages(msg.getChannelId());
					break;
				case VirtPipeMessage.NORMAL:
					Activator.LOGGER.info("VirtPipes run NORMAL "
							+ msg.getChannelId());
					synchronized (virtPipeInputs) {
						if (virtPipeInputs.containsKey(msg.getChannelId())) {
							for (final VirtPipeInput vPipe : virtPipeInputs
									.get(msg.getChannelId())) {
								if (vPipe != null) {
									vPipe.read(msg.getBuf());
								} else {
									Activator.LOGGER
											.warning("VirtPipes run dispatch failed "
													+ msg.getChannelId());
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Enregistre un tuyau virtuel d'entrée, méthode importante pour les couches
	 * utilisatrices.
	 * 
	 * @param virtPipeInput
	 *            à enregistrer
	 */
	public final void registerVirtPipe(final VirtPipeInput virtPipeInput) {
		synchronized (virtPipeInputs) {
			Set<VirtPipeInput> ins = virtPipeInputs.get(virtPipeInput.getId());
			if (ins == null) {
				ins = new HashSet<VirtPipeInput>();
				virtPipeInputs.put(virtPipeInput.getId(), ins);
			}
			ins.add(virtPipeInput);
		}
		synchronized (virtPipesModules) {
			for (final VirtPipesModule vpMod : virtPipesModules) {
				vpMod.registerVirtPipeInput(virtPipeInput.getId());
			}
		}
	}

	/**
	 * Appelé par OSGi, lorsqu'un module d'information pour VirtPipes disparait.
	 * 
	 * @param virtPipesModule
	 *            à supprimer de la logique du VPS
	 */
	public void removeVirtPipesModule(final VirtPipesModule virtPipesModule) {
		synchronized (virtPipesModules) {
			virtPipesModules.remove(virtPipesModule);
		}
	}

	/**
	 * Méthode pour envoyer un message aux tuyaux virtuels d'entrée id sans à
	 * avoir à créer un VirtPipeOutput.
	 * 
	 * @param id
	 *            du VPI
	 * @param buf
	 *            message à envoyer
	 */
	public final void send(final UUID id, final ByteBuffer buf) {
		send(id, buf, null);
	}

	/**
	 * Méthode appelé par les VirtPipesOutput pour envoyer un message aux tuyaux
	 * virtuels d'entrée id.
	 * 
	 * @param id
	 *            du VPI
	 * @param buf
	 *            message à envoyer
	 * @param output
	 *            pour notifier d'une erreur
	 */
	public final void send(final UUID id, final ByteBuffer buf,
			final VirtPipeOutput output) {
		send(new VirtPipeMessage(VirtPipeMessage.NORMAL, id, buf), output,
				false);
	}

	/**
	 * Méthode d'envoi du message aux VPIs, de recherche de VPS distants, et
	 * d'auto-connexion aux VPS distants non connectés.
	 * 
	 * @param msg
	 * @param output
	 * @param pendingMessage
	 */
	private final void send(final VirtPipeMessage msg,
			final VirtPipeOutput output, final boolean pendingMessage) {
		boolean noVpi = true; // aucun VPI n'a été trouvé encore
		{ // local routing
			if (virtPipeInputs.containsKey(msg.getChannelId())) {
				// routage interne
				for (final VirtPipeInput virtPipeInput : virtPipeInputs.get(msg
						.getChannelId())) {
					virtPipeInput.read(msg.getBuf());
					noVpi = false; // ok au moins un VPI de trouvé
				}
			}
		} // end local routing
		{ // remote routing
			final Set<UUID> remoteVirtPipesServices = new HashSet<UUID>();
			// list of the remote VPS
			synchronized (virtPipesModules) {
				for (final VirtPipesModule vpMod : virtPipesModules) {
					// ask for each VP-Mod the VPS
					remoteVirtPipesServices.addAll(vpMod
							.getVirtPipesService(output.getId()));

				}
			}
			if (remoteVirtPipesServices.size() == 0) { // pas de remote VPS
				final Set<VirtPipeInput> vpis = virtPipeInputs.get(output
						.getId());
				if (vpis == null || vpis.size() == 0) {
					// tell the VirtPipeOutput that no
					// VPS is interested in its
					// message
					if (noVpi && output != null) { // si aucune transmission n'a
						// été faite
						output.virtPipeClosed(); // on notifie output
					}
				}
			} else {
				// on a au moins une transmission TCP à faire
				for (final UUID vpsId : remoteVirtPipesServices) {
					// send the Message to each VPS
					if (serverSocket.connections.containsKey(vpsId)) {
						// already connected to the VPS
						synchronized (pendingVirtMessagesToBeSent) {
							if (!pendingMessage
									&& pendingVirtMessagesToBeSent.get(vpsId) != null
									&& pendingVirtMessagesToBeSent.get(vpsId)
											.size() > 0) {
								// Have already other messages to send
								putInPendingMessages(vpsId, msg);
								sendPendingMessages(vpsId);
							} else {
								serverSocket.send(serverSocket.connections
										.get(vpsId), msg.format());
							}
						}
					} else if (!connecting.keySet().contains(vpsId)) {
						// try to connect as no attempt has been made
						synchronized (virtPipesModules) {
							for (final VirtPipesModule vpMod : virtPipesModules) {
								final SocketChannel socket = connect(vpsId,
										vpMod.getSocketAddresses(vpsId));
								if (socket != null) {
									// on met le message en attente d'envoi pour
									// ce VPS spécifiquement
									putInPendingMessages(vpsId, msg);
									// on met la socket dans la liste des
									// connexions en attentes
									connecting.put(vpsId, socket);
									break;
								}
							}
						}
					} else {
						// an attempt of connecting is going on.
						// on met en attente le message, à la suite des autres
						// possiblement.
						putInPendingMessages(vpsId, msg);
					}
				}
			}
		} // end remote routing
	}

	/**
	 * Méthode pour envoyer les messages en attente d'envoi pour tout un VPS
	 * distant.
	 * 
	 * @param vpsId
	 *            du VPS distant à qui on envoit les message.
	 */
	private void sendPendingMessages(final UUID vpsId) {
		synchronized (pendingVirtMessagesToBeSent) {
			final List<VirtPipeMessage> msgToSent = pendingVirtMessagesToBeSent
					.get(vpsId);
			if (msgToSent != null) {
				synchronized (msgToSent) {
					while (msgToSent.size() > 0) {
						final VirtPipeMessage msg = msgToSent.remove(0);
						if (msg != null) {
							Activator.LOGGER
									.info("Send pending VirtPipeMessage after INIT");
							serverSocket.send(serverSocket.connections
									.get(vpsId), msg.format());
						}
					}
				}
			}
		}
	}

	@Override
	public final String toString() {
		return "VirtPipesService["
				+ serverSocket.serverChannel.socket().getLocalPort() + "]";
	}

}
