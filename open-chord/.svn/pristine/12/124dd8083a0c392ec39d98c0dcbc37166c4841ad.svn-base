/***************************************************************************
 *                                                                         *
 *                            ThreadEndpoint.java                          *
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * This represents the {@link Endpoint} for the protocol that can be used to
 * build a (local) chord network within one JVM.
 * 
 * @author sven
 * @version 1.0.5
 */
public final class ThreadEndpoint extends Endpoint {

	/**
	 * The logger for this instance.
	 */
	private Logger logger = null;

	/**
	 * Constant indicating that this has crashed.
	 */
	private final static int CRASHED = Integer.MAX_VALUE;

	/**
	 * The {@link Registry registry}of local endpoints.
	 */
	protected final Registry registry;

	/**
	 * Object to synchronize threads at. Used to block and wake up threads that
	 * are waiting for this endpoint to get into a state.
	 */
	private final Object lock = new Object();

	/**
	 * {@link List}of {@link InvocationListener listeners}that want to be
	 * notified if a method is invoked on this endpoint.
	 */
	protected List<InvocationListener> invocationListeners = null;

	/**
	 * Creates a new Endpoint for communication via Java Threads.
	 * 
	 * @param node1
	 *            The {@link Node}this endpoint invocates methods on.
	 * @param url1
	 *            The {@link URL}of this endpoint. The hostname of url is the
	 *            name of the node.
	 */
	public ThreadEndpoint(final Node node1, final URL url1) {
		super(node1, url1);
		logger = Logger.getLogger(ThreadEndpoint.class.getName() + "."
				+ node1.getNodeID());
		logger.setParent(Activator.LOGGER);
		invocationListeners = new LinkedList<InvocationListener>();
		registry = Registry.getRegistryInstance();
		logger.info(this + " initialised.");
	}

	/**
	 * Checks if this has crashed.
	 * 
	 * @throws CommunicationException
	 */
	private void checkIfCrashed() throws CommunicationException {
		if (getState() == ThreadEndpoint.CRASHED
				|| getState() < Endpoint.LISTENING) {
			logger.info(this + " has crashed. Throwing Exception.");
			throw new CommunicationException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#closeConnections()
	 */
	@Override
	protected void closeConnections() {
		registry.unbind(this);
		registry.removeProxiesInUseBy(getURL());
		/** state has changed. notify waiting threads */
		notifyWaitingThreads();
	}

	/**
	 * Method to emulate a crash of the node that this is the endpoint for. This
	 * method heavily relise on the internal structure of service layer
	 * implementation to make it possible to emulate a chord overlay network
	 * within one JVM. This method may cause problems at runtime.
	 */
	public void crash() {
		logger.info("crash() invoked!");
		registry.unbind(this);
		final List<ThreadProxy> proxies = registry.getProxiesInUseBy(getURL());
		if (proxies != null) {
			for (final ThreadProxy p : proxies) {
				p.invalidate();
			}
		}

		registry.removeProxiesInUseBy(getURL());
		setState(ThreadEndpoint.CRASHED);
		notifyWaitingThreads();
		/* kill threads of node (gefrickelt) */
		final ChordImpl impl = ChordImplAccess.fetchChordImplOfNode(node);
		final Field[] fields = impl.getClass().getDeclaredFields();
		logger.info(fields.length + " fields obtained from class "
				+ impl.getClass());
		for (final Field field : fields) {
			logger.info("Examining field " + field + " of node " + node);
			try {
				if (field.getName().equals("maintenanceTasks")) {
					field.setAccessible(true);

					final Object executor = field.get(impl);
					logger.info("Shutting down TaskExecutor " + executor + ".");
					final Method m = executor.getClass().getMethod("shutdown",
							new Class[0]);
					m.setAccessible(true);
					m.invoke(executor, new Object[0]);
				}
			} catch (final Throwable t) {
				logger.warning("Could not kill threads of node " + node);
				t.printStackTrace();
			}
		}
		Endpoint.endpoints.remove(url);
		invocationListeners = null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#entriesAcceptable()
	 */
	@Override
	protected void entriesAcceptable() {
		/** state has changed. notify waiting threads */
		notifyWaitingThreads();
	}

	/**
	 * Overwritten from {@link java.lang.Object}. Two ThreadEndpoints A and B
	 * are equal if they are endpoints for the node with the same name. (A.name
	 * == B.name).
	 * 
	 * @param obj
	 * @return <code>true</code> if this equals the provided <code>obj</code>.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ThreadEndpoint) {
			final ThreadEndpoint ep = (ThreadEndpoint) obj;
			final URL epURL = ep.getURL();
			return epURL.equals(getURL()) && ep.hashCode() == hashCode();
		} else {
			return false;
		}
	}

	/**
	 * @param key
	 * @return The successor of <code>key</code>.
	 * @throws CommunicationException
	 */
	public Node findSuccessor(final ID key) throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.LISTENING);
		/* delegate invocation to node. */
		notifyInvocationListeners(InvocationListener.FIND_SUCCESSOR);
		Node n = node.findSuccessor(key);
		if (n == node) {
			logger
					.info("Returned node is local node. Converting to 'remote' reference. ");
			final ThreadProxy t = new ThreadProxy(url, url);
			t.reSetNodeID(n.getNodeID());
			n = t;
		}
		notifyInvocationListenersFinished(InvocationListener.FIND_SUCCESSOR);
		return n;
	}

	/**
	 * @return Implementation of {@link Node#notify(Node)}. See documentation of
	 *         {@link Node}.
	 */
	public ID getNodeID() {
		return node.getNodeID();
	}

	/**
	 * Overwritten from {@link java.lang.Object}.
	 * 
	 * @return Overwritten from {@link java.lang.Object}.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @param entry
	 * @throws CommunicationException
	 */
	public void insertEntry(final Entry entry) throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.ACCEPT_ENTRIES);
		/* delegate invocation to node. */
		notifyInvocationListeners(InvocationListener.INSERT_ENTRY);
		node.insertEntry(entry);
		notifyInvocationListenersFinished(InvocationListener.INSERT_ENTRY);
	}

	/**
	 * @param entries
	 * @throws CommunicationException
	 */
	public void insertReplicas(final Set<Entry> entries)
			throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.LISTENING);
		notifyInvocationListeners(InvocationListener.INSERT_REPLICAS);
		node.insertReplicas(entries);
		notifyInvocationListenersFinished(InvocationListener.INSERT_REPLICAS);
	}

	/**
	 * @param predecessor
	 * @throws CommunicationException
	 */
	public void leavesNetwork(final Node predecessor)
			throws CommunicationException {
		checkIfCrashed();
		notifyInvocationListeners(InvocationListener.LEAVES_NETWORK);
		node.leavesNetwork(predecessor);
		notifyInvocationListenersFinished(InvocationListener.LEAVES_NETWORK);
	}

	/**
	 * @param potentialPredecessor
	 * @return Implementation of {@link Node#notify(Node)}. See documentation of
	 *         {@link Node}.
	 * @throws CommunicationException
	 */
	public List<Node> notify(final Node potentialPredecessor)
			throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.LISTENING);
		notifyInvocationListeners(InvocationListener.NOTIFY);
		logger.info("Invoking notify on local node " + node);
		final List<Node> n = node.notify(potentialPredecessor);
		logger.info("Notify resulted in " + n);

		for (final Node current : n) {
			if (current == node) {
				n.remove(current);

				logger
						.info("Returned node is local node. Converting to 'remote' reference. ");
				n.add(new ThreadProxy(url, url));
			}
		}
		notifyInvocationListenersFinished(InvocationListener.NOTIFY);
		return n;
	}

	/**
	 * @param potentialPredecessor
	 * @return Implementation of {@link Node#notify(Node)}. See documentation of
	 *         {@link Node}.
	 * @throws CommunicationException
	 */
	public RefsAndEntries notifyAndCopyEntries(final Node potentialPredecessor)
			throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.ACCEPT_ENTRIES);
		notifyInvocationListeners(InvocationListener.NOTIFY_AND_COPY);
		final RefsAndEntries refs = node
				.notifyAndCopyEntries(potentialPredecessor);
		final List<Node> nodes = refs.getRefs();

		for (final Node current : nodes) {
			if (current == node) {
				nodes.remove(current);

				logger
						.info("Returned node is local node. Converting to 'remote' reference. ");
				nodes.add(new ThreadProxy(url, url));
			}
		}
		notifyInvocationListenersFinished(InvocationListener.NOTIFY_AND_COPY);
		return new RefsAndEntries(nodes, refs.getEntries());
	}

	/**
	 * @param method
	 */
	private void notifyInvocationListeners(final int method) {
		for (final InvocationListener l : invocationListeners) {
			l.notifyInvocationOf(method);
		}
	}

	/**
	 * @param method
	 */
	private void notifyInvocationListenersFinished(final int method) {
		for (final InvocationListener l : invocationListeners) {
			l.notifyInvocationOfFinished(method);
		}
	}

	/**
	 * Notify threads waiting for monitor on lock.
	 */
	private void notifyWaitingThreads() {
		synchronized (lock) {
			logger.info("Notifying waiting threads.");
			lock.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#openConnections()
	 */
	@Override
	protected void openConnections() {
		/** state has changed. notify waiting threads */
		logger.info("openConnections()");
		notifyWaitingThreads();
		registry.bind(this);
	}

	/**
	 * @throws CommunicationException
	 */
	public void ping() throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.LISTENING);
		notifyInvocationListeners(InvocationListener.PING);
		node.ping();
		notifyInvocationListenersFinished(InvocationListener.PING);
	}

	/**
	 * @param listener
	 */
	public void register(final InvocationListener listener) {
		logger.info("register(" + listener + ")");
		// synchronized (this.invocationListeners) {
		invocationListeners.add(listener);
		// }
		logger
				.info("No. of invocation listeners "
						+ invocationListeners.size());
	}

	/**
	 * @param entry
	 * @throws CommunicationException
	 */
	public void removeEntry(final Entry entry) throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.ACCEPT_ENTRIES);
		/* delegate invocation to node. */
		notifyInvocationListeners(InvocationListener.REMOVE_ENTRY);
		node.removeEntry(entry);
		notifyInvocationListenersFinished(InvocationListener.REMOVE_ENTRY);
	}

	/**
	 * @param sendingNodeID
	 * @param entriesToRemove
	 * @throws CommunicationException
	 */
	public void removeReplicas(final ID sendingNodeID,
			final Set<Entry> entriesToRemove) throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.LISTENING);
		notifyInvocationListeners(InvocationListener.REMOVE_REPLICAS);
		node.removeReplicas(sendingNodeID, entriesToRemove);
		notifyInvocationListenersFinished(InvocationListener.REMOVE_REPLICAS);
	}

	/** ********************************************************** */
	/* START: Methods overwritten from java.lang.Object */
	/** ********************************************************** */

	/**
	 * @param id
	 * @return The retrieved entries.
	 * @throws CommunicationException
	 */
	public Set<Entry> retrieveEntries(final ID id)
			throws CommunicationException {
		checkIfCrashed();
		waitFor(Endpoint.ACCEPT_ENTRIES);
		notifyInvocationListeners(InvocationListener.RETRIEVE_ENTRIES);
		final Set<Entry> s = node.retrieveEntries(id);
		notifyInvocationListenersFinished(InvocationListener.RETRIEVE_ENTRIES);
		return s;
	}

	/**
	 * Overwritten from {@link java.lang.Object}.
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[ThreadEndpoint for node ");
		buffer.append(node);
		buffer.append(" with URL ");
		buffer.append(url);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * Wait for the endpoint to get into given state.
	 * 
	 * @param state_
	 *            The state to wait for.
	 * @throws CommunicationException
	 */
	private void waitFor(final int state_) throws CommunicationException {
		synchronized (lock) {
			while (getState() < state_) {
				try {
					logger.info(Thread.currentThread() + " waiting for state: "
							+ state_);
					lock.wait();
					if (state_ == ThreadEndpoint.CRASHED) {
						throw new CommunicationException(
								"Connection destroyed!");
					}
				} catch (final InterruptedException t) {
					logger.warning("Unexpected exception while waiting!");
				}
			}
		}
	}

	/** ********************************************************** */
	/* END: Methods overwritten from java.lang.Object */
	/** ********************************************************** */

}
