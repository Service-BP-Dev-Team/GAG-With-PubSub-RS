/***************************************************************************
 *                                                                         *
 *                             ThreadProxy.java                            *
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
package de.uniba.wiai.lspi.chord.com.local;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.Proxy;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * This class represents a {@link Proxy} for the protocol that allows to be
 * build a (local) chord network within one JVM.
 * 
 * @author sven
 * @version 1.0.5
 */
public final class ThreadProxy extends Proxy {

	/**
	 * The logger for instances of this.
	 */
	private static final Logger logger = Logger.getLogger(ThreadProxy.class
			.getName());
	static {
		ThreadProxy.logger.setParent(Activator.LOGGER);
	}

	/**
	 * Reference to the {@link Registry registry}singleton.
	 */
	protected Registry registry = null;

	/**
	 * The {@link URL}of the node that created this proxy.
	 */
	protected URL creatorURL;

	/**
	 * Indicates if this proxy can be used for communication;
	 */
	protected boolean isValid = true;

	/**
	 * Indicates if this proxy has been used to make a invocation.
	 */
	protected boolean hasBeenUsed = false;

	/**
	 * The endpoint, to which this delegates method invocations.
	 */
	private ThreadEndpoint endpoint = null;

	/**
	 * Creates a Proxy for the <code>jchordlocal</code> protocol. The host name
	 * part of {@link URL url}is the name of the node in the
	 * <code>jchordlocal</code> protocol.
	 * 
	 * @param creatorURL1
	 * @param url
	 *            The {@link URL}of the node this proxy represents.
	 * @throws CommunicationException
	 */
	public ThreadProxy(final URL creatorURL1, final URL url)
			throws CommunicationException {
		super(url);
		registry = Registry.getRegistryInstance();
		creatorURL = creatorURL1;
		ThreadProxy.logger.info("Trying to get id of node.");
		final ThreadEndpoint endpoint_ = registry.lookup(nodeURL);
		ThreadProxy.logger.info("Found endpoint " + endpoint_);
		if (endpoint_ == null) {
			throw new CommunicationException();
		}
		nodeID = endpoint_.getNodeID();
	}

	/**
	 * @param creatorURL1
	 * @param url
	 * @param nodeID1
	 */
	private ThreadProxy(final URL creatorURL1, final URL url, final ID nodeID1) {
		super(url);
		registry = Registry.getRegistryInstance();
		nodeID = nodeID1;
		creatorURL = creatorURL1;
	}

	/**
	 * Method to check if this proxy is valid.
	 * 
	 * @throws CommunicationException
	 */
	private void checkValidity() throws CommunicationException {

		if (!isValid) {
			throw new CommunicationException("No valid connection!");
		}

		if (endpoint == null) {
			endpoint = registry.lookup(nodeURL);
			if (endpoint == null) {
				throw new CommunicationException();
			}
		}

		/*
		 * Ensure that node id is set, if has not been set before.
		 */
		getNodeID();

		if (!hasBeenUsed) {
			hasBeenUsed = true;
			Registry.getRegistryInstance().addProxyUsedBy(creatorURL, this);
		}
	}

	/**
	 * Creates a copy of this.
	 * 
	 * @param creatorUrl
	 *            The url of the node where this is being copied.
	 * @return The copy of this.
	 */
	private ThreadProxy cloneMeAt(final URL creatorUrl) {
		return new ThreadProxy(creatorUrl, nodeURL, nodeID);
	}

	@Override
	public void disconnect() {
	// TODO Auto-generated method stub

	}

	@Override
	public Node findSuccessor(final ID key) throws CommunicationException {
		checkValidity();
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		final Node succ = endpoint.findSuccessor(key);
		try {
			ThreadProxy.logger.info("Creating clone of proxy " + succ);
			final ThreadProxy temp = (ThreadProxy) succ;
			ThreadProxy.logger.info("Clone created");
			return temp.cloneMeAt(creatorURL);
		} catch (final Throwable t) {
			ThreadProxy.logger.severe("Exception during clone of proxy.");
			throw new CommunicationException(t);
		}
	}

	/**
	 * Get a reference to the {@link ThreadEndpoint endpoint} this proxy
	 * delegates methods to. If there is no endpoint a
	 * {@link CommunicationException exception} is thrown.
	 * 
	 * @return Reference to the {@link ThreadEndpoint endpoint} this proxy
	 *         delegates methods to.
	 * @throws CommunicationException
	 *             If there is no endpoint this exception is thrown.
	 */
	public ThreadEndpoint getEndpoint() throws CommunicationException {
		final ThreadEndpoint ep = registry.lookup(nodeURL);
		if (ep == null) {
			throw new CommunicationException();
		}
		return ep;
	}

	@Override
	public void insertEntry(final Entry entry) throws CommunicationException {
		checkValidity();
		ThreadProxy.logger.info("Trying to execute insert().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		endpoint.insertEntry(entry);
		ThreadProxy.logger.info("insert() executed");
	}

	@Override
	public void insertReplicas(final Set<Entry> entries)
			throws CommunicationException {
		checkValidity();
		ThreadProxy.logger.info("Trying to execute insertReplicas(" + entries
				+ ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		endpoint.insertReplicas(entries);
	}

	/**
	 * Invalidates this proxy.
	 */
	public void invalidate() {
		isValid = false;
		endpoint = null;
	}

	/**
	 * Test if this Proxy is valid.
	 * 
	 * @return <code>true</code> if this Proxy is valid.
	 */
	public boolean isValid() {
		return isValid;
	}

	@Override
	public void leavesNetwork(final Node predecessor)
			throws CommunicationException {
		checkValidity();

		final ThreadProxy predecessorProxy = new ThreadProxy(creatorURL,
				predecessor.getNodeURL());

		ThreadProxy.logger.info("Trying to execute leavesNetwork("
				+ predecessor + ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		endpoint.leavesNetwork(predecessorProxy);
	}

	@Override
	public List<Node> notify(final Node potentialPredecessor)
			throws CommunicationException {
		checkValidity();

		final ThreadProxy potentialPredecessorProxy = new ThreadProxy(
				creatorURL, potentialPredecessor.getNodeURL());

		ThreadProxy.logger.info("Trying to execute notify().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		final List<Node> nodes = endpoint.notify(potentialPredecessorProxy);
		final Node[] proxies = new Node[nodes.size()];
		try {
			int currentIndex = 0;
			// TODO Document why ThreadProxy instead of Node
			for (final Iterator<Node> i = nodes.iterator(); i.hasNext();) {
				final Object o = i.next();
				final ThreadProxy current = (ThreadProxy) o;
				proxies[currentIndex++] = current.cloneMeAt(creatorURL);
			}
		} catch (final Throwable t) {
			throw new CommunicationException(t);
		}
		return Arrays.asList(proxies);
	}

	@Override
	public RefsAndEntries notifyAndCopyEntries(final Node potentialPredecessor)
			throws CommunicationException {
		checkValidity();

		final ThreadProxy potentialPredecessorProxy = new ThreadProxy(
				creatorURL, potentialPredecessor.getNodeURL());

		ThreadProxy.logger.info("Trying to execute notifyAndCopyEntries().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		return endpoint.notifyAndCopyEntries(potentialPredecessorProxy);
	}

	@Override
	public void ping() throws CommunicationException {
		checkValidity();
		ThreadProxy.logger.info("Trying to execute ping().");
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		endpoint.ping();
	}

	@Override
	public void removeEntry(final Entry entry) throws CommunicationException {
		checkValidity();
		endpoint.removeEntry(entry);
	}

	@Override
	public void removeReplicas(final ID sendingNodeID,
			final Set<Entry> entriesToRemove) throws CommunicationException {
		checkValidity();
		ThreadProxy.logger.info("Trying to execute removeReplicas("
				+ entriesToRemove + ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		endpoint.removeReplicas(sendingNodeID, entriesToRemove);
	}

	void reSetNodeID(final ID id) {
		setNodeID(id);
	}

	@Override
	public Set<Entry> retrieveEntries(final ID id)
			throws CommunicationException {
		checkValidity();
		ThreadProxy.logger.info("Trying to execute retrieve().");
		ThreadProxy.logger.info("Found endpoint " + endpoint);
		return endpoint.retrieveEntries(id);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[ThreadProxy ");
		buffer.append(nodeURL);
		buffer.append("]");
		return buffer.toString();
	}

}
