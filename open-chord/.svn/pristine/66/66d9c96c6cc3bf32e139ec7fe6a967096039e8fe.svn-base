/***************************************************************************
 *                                                                         *
 *                              Endpoint.java                              *
 *                            -------------------                          *
 *   date                 : 12.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                          karsten.loesing@uni-bamberg.de                 *
 *                                                                         *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   A copy of the license can be found in the license.txt file supplied   *
 *   with this software or at: http://www.gnu.org/copyleft/gpl.html        *
 *                                                                         *
 ***************************************************************************/
package de.uniba.wiai.lspi.chord.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.local.ThreadEndpoint;
import de.uniba.wiai.lspi.chord.com.socket.SocketEndpoint;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * <p>
 * This class represents an endpoint, which wraps a {@link Node}, so that other
 * nodes can connect to the node using a protocol.
 * </p>
 * <p>
 * This class must be extended by endpoints that support a certain protocol as
 * e.g. <code>RMI</code> or a protocol over <code>Sockets</code>.
 * </p>
 * <p>
 * This is the abstract class that has to be implemented by all Endpoints. An
 * Endpoint enables other chord peers to connect to a {@link Node node} with
 * help of a given protocol. Each node in a chord network has to have exactly
 * one endpoint.
 * </p>
 * <p>
 * For each protocol that shall be supported a separate endpoint has to be
 * implemented. To initialise endpoints for a {@link Node} an {@link URL} has to
 * be provided to the {@link #createEndpoint(Node, URL)} endpoint factory
 * method. This methods tries to determine the endpoint with help of the
 * protocol names defined by the url. Supported protocols can be found in the
 * {@link URL} class.
 * </p>
 * An Endpoint can be in three states:
 * <ul>
 * <li><code>STARTED</code>,</li>
 * <li><code>LISTENING</code>, and</li>
 * <li><code>ACCEPT_ENTRIES</code>.</li>
 * </ul>
 * <p>
 * In state <code>STARTED</code> the endpoint has been initialised but does not
 * listen to (possibly) incoming messages from the chord network. An endpoint
 * gets into this state if it is created with help of its constructor. <br/>
 * <br/>
 * In state <code>LISTENING</code> the endpoint accepts messages that are
 * received from the chord network to update the finger table or request the
 * predecessor or successor of the node of this endpoint. The transition to this
 * state is made by invocation of {@link #listen()}. <br/>
 * <br/>
 * In state <code>ACCEPT_ENTRIES</code>. This endpoint accepts messages from the
 * chord network, that request storage or removal of entries from the DHT. The
 * transition to this state is made by invocation of {@link #acceptEntries()}.
 * </p>
 * 
 * @author sven
 * @version 1.0.5
 */
public abstract class Endpoint {

	/**
	 * Logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(Endpoint.class
			.getName());
	/**
	 * Map containing all endpoints. Key: {@link URL}. Value:
	 * <code>Endpoint</code>.
	 */
	protected static final Map<URL, Endpoint> endpoints = new HashMap<URL, Endpoint>();

	// TODO: Create enum for state.
	/**
	 * Integer representation of state.
	 */
	public static final int STARTED = 0;

	/**
	 * Integer representation of state.
	 */
	public static final int LISTENING = 1;

	/**
	 * Integer representation of state.
	 */
	public static final int ACCEPT_ENTRIES = 2;

	/**
	 * Integer representation of state.
	 */
	public static final int DISCONNECTED = 3;

	/**
	 * Array containing names of methods only allowed to be invoked in state
	 * {@link #ACCEPT_ENTRIES}. Remember to eventually edit this array if you
	 * change the methods in interface {@link Node}. The method names contained
	 * in this array must be sorted!
	 */
	public static final List<String> METHODS_ALLOWED_IN_ACCEPT_ENTRIES;

	static {
		Endpoint.logger.setParent(Activator.LOGGER);
	}

	static {
		final String[] temp = new String[] { "insertEntry", "removeEntry",
				"retrieveEntries" };
		Arrays.sort(temp);
		final List<String> list = new ArrayList<String>(Arrays.asList(temp));
		METHODS_ALLOWED_IN_ACCEPT_ENTRIES = Collections.unmodifiableList(list);
	}

	/**
	 * Create the endpoints for the protocol given by <code>url</code>. An URL
	 * must have a known protocol. An endpoint for an {@link URL} can only be
	 * create once and then be obtained with help of
	 * {@link Endpoint#getEndpoint(URL)}. An endpoint for an url must again be
	 * created if the {@link Endpoint#disconnect()} has been invoked.
	 * 
	 * @param node
	 *            The node to which this endpoint delegates incoming requests.
	 * @param url
	 *            The URL under which <code>node</code> will be reachable by
	 *            other nodes.
	 * @return The endpoint created for <code>node</code> for the protocol
	 *         specified in <code>url</code>.
	 * @throws RuntimeException
	 *             This can occur if any error that cannot be handled by this
	 *             method occurs during endpoint creation.
	 */
	public static Endpoint createEndpoint(final Node node, final URL url) {

		synchronized (Endpoint.endpoints) {
			if (Endpoint.endpoints.containsKey(url)) {
				throw new RuntimeException("Endpoint already created!");
			}
			Endpoint endpoint = null;

			// TODO irgendwann �ber properties l�sen
			if (url == null) {
				throw new IllegalArgumentException("Url must not be null! ");
			}
			if (url.getProtocol().equals(
					URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL))) {

				endpoint = new SocketEndpoint(node, url);
			} else if (url.getProtocol().equals(
					URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL))) {

				endpoint = new ThreadEndpoint(node, url);
			} /*
			 * else if (url.getProtocol().equals(
			 * URL.KNOWN_PROTOCOLS.get(URL.RMI_PROTOCOL))) { endpoint = new
			 * RMIEndpoint(node, url); }
			 */else {
				// does not happen ??
				throw new IllegalArgumentException("Url does not contain a "
						+ "supported protocol " + "(" + url.getProtocol()
						+ ")!");
			}

			Endpoint.endpoints.put(url, endpoint);

			return endpoint;
		}
	}

	/**
	 * Get the <code>Endpoint</code> for the given <code>url</code>.
	 * 
	 * @param url
	 * @return The endpoint for provided <code>url</code>.
	 */
	public static Endpoint getEndpoint(final URL url) {
		synchronized (Endpoint.endpoints) {
			final Endpoint ep = Endpoint.endpoints.get(url);
			Endpoint.logger.info("Endpoint for URL " + url + ": " + ep);
			return ep;
		}
	}

	/**
	 * The current state of this endpoint.
	 */
	private volatile int state = -1;

	/**
	 * The {@link URL}that can be used to connect to this endpoint.
	 */
	protected URL url;

	/**
	 * The {@link Node node}on which this endpoint invokes methods.
	 */
	protected Node node;

	/**
	 * {@link EndpointStateListener listeners}interested in state changes of
	 * this endpoint.
	 */
	private final Set<EndpointStateListener> listeners = new HashSet<EndpointStateListener>();

	/**
	 * @param node1
	 *            The {@link Node} this is the Endpoint for.
	 * @param url1
	 *            The {@link URL} that describes the location of this endpoint.
	 */
	protected Endpoint(final Node node1, final URL url1) {
		Endpoint.logger.info("Endpoint for " + node1 + " with url " + url1
				+ "created.");
		node = node1;
		url = url1;
		state = Endpoint.STARTED;
	}

	/**
	 * Tell this endpoint that the node is now able to receive messages that
	 * request the storage and removal of entries.
	 */
	public final void acceptEntries() {
		Endpoint.logger.info("acceptEntries() called.");
		state = Endpoint.ACCEPT_ENTRIES;
		this.notify(state);
		entriesAcceptable();
	}

	/**
	 * This method has to be overwritten by sub classes and is invoked by
	 * {@link #disconnect()}to close all connections from the chord network.
	 */
	protected abstract void closeConnections();

	/**
	 * Remove a listener that listened for state changes of this endpoint.
	 * 
	 * @param listener
	 *            The listener instance to be removed.
	 */
	public final void deregister(final EndpointStateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Tell this endpoint to disconnect and close all connections. If this
	 * method has been invoked the endpoint must be not reused!!!
	 */
	public final void disconnect() {
		state = Endpoint.STARTED;
		Endpoint.logger.info("Disconnecting.");
		this.notify(state);
		closeConnections();
		synchronized (Endpoint.endpoints) {
			Endpoint.endpoints.remove(node.nodeURL);
		}
	}

	/**
	 * This method has to be overwritten by subclasses. It is called from
	 * {@link #acceptEntries()}to indicate that entries can now be accepted. So
	 * maybe if an endpoint queues incoming requests for storage or removal of
	 * entries this requests can be answered when endpoint changes it state to
	 * <code>ACCEPT_ENTRIES</code>.
	 */
	protected abstract void entriesAcceptable();

	/**
	 * @return Returns the node.
	 */
	public final Node getNode() {
		return node;
	}

	/**
	 * @return Returns the state.
	 */
	public final int getState() {
		return state;
	}

	/**
	 * Get the {@link URL}of this endpoint.
	 * 
	 * @return The {@link URL}that can be used to connect to this endpoint.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Tell this endpoint that it can listen to incoming messages from other
	 * chord nodes. TODO: This method may throw an exception when starting to
	 * listen for incoming connections.
	 */
	public final void listen() {
		state = Endpoint.LISTENING;
		this.notify(state);
		openConnections();
	}

	// TODO rename!!
	/**
	 * Method to notify listeners about a change in state of this endpoint.
	 * 
	 * @param s
	 *            The integer identifying the state to that the endpoint
	 *            switched. See {@link Endpoint#ACCEPT_ENTRIES},
	 *            {@link Endpoint#DISCONNECTED}, {@link Endpoint#LISTENING}, and
	 *            {@link Endpoint#STARTED}.
	 */
	protected void notify(final int s) {
		Endpoint.logger.info("notifying state change.");
		synchronized (listeners) {
			Endpoint.logger.info("Size of listeners = " + listeners.size());
			for (final EndpointStateListener listener : listeners) {
				listener.notify(s);
			}
		}
	}

	/**
	 * To implemented by sub classes. This method is called by {@link #listen()}
	 * to make it possible for other chord nodes to connect to the node on that
	 * this endpoint invocates methods. TODO: This method may throw an exception
	 * when starting to listen for incoming connections.
	 */
	protected abstract void openConnections();

	/**
	 * Register a listener that is notified when the state of this endpoint
	 * changes.
	 * 
	 * @param listener
	 *            The listener to register.
	 */
	public final void register(final EndpointStateListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param state1
	 *            The state to set.
	 */
	protected final void setState(final int state1) {
		state = state1;
		this.notify(state1);
	}

	/**
	 * Overwritten from {@link java.lang.Object}.
	 * 
	 * @return String representation of this endpoint.
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[Endpoint for ");
		buffer.append(node);
		buffer.append(" with URL ");
		buffer.append(url);
		return buffer.toString();
	}

}
