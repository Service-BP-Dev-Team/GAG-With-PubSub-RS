/***************************************************************************
 *                                                                         *
 *                               NodeImpl.java                             *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *   			    		karsten.loesing@uni-bamberg.de                 *
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
package de.uniba.wiai.lspi.chord.service.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * Implements all operations which can be invoked remotely by other nodes.
 * 
 * @author Karsten Loesing
 * @version 1.0.5
 */
public final class NodeImpl extends Node {

	/**
	 * Endpoint for incoming communication.
	 */
	private Endpoint myEndpoint = null;

	/**
	 * Reference on local node.
	 */
	private final ChordImpl impl;

	/**
	 * Object logger. The name of the logger is the name of this class with the
	 * nodeID appended. The length of the nodeID depends on the number of bytes
	 * that are displayed when the ID is shown in Hex-Representation. See
	 * documentation of {@link ID}. E.g.
	 * de.uniba.wiai.lspi.chord.service.impl.NodeImpl.FF FF FF FF if the number
	 * of displayed Bytes of an ID is 4.
	 */
	private final Logger logger;

	/**
	 * Routing table (including finger table, successor list, and predecessor
	 * reference)
	 */
	private final References references;

	/**
	 * Repository for locally stored entries.
	 */
	private final Entries entries;

	/**
	 * Executor that executes insertion and removal of entries on successors of
	 * this node.
	 */
	private final Executor asyncExecutor;

	private final Lock notifyLock;

	/**
	 * Creates that part of the local node which answers remote requests by
	 * other nodes. Sole constructor, is invoked by ChordImpl only.
	 * 
	 * @param impl
	 *            Reference on ChordImpl instance which created this object.
	 * @param nodeID
	 *            This node's Chord ID.
	 * @param nodeURL
	 *            URL, on which this node accepts connections.
	 * @param references
	 *            Routing table of this node.
	 * @param entries
	 *            Repository for entries of this node.
	 * @throws IllegalArgumentException
	 *             If any of the parameter has value <code>null</code>.
	 */
	NodeImpl(final ChordImpl impl, final ID nodeID, final URL nodeURL,
			final References references, final Entries entries) {

		if (impl == null || nodeID == null || nodeURL == null
				|| references == null || entries == null) {
			throw new IllegalArgumentException(
					"Parameters of the constructor may not have a null value!");
		}

		logger = Logger.getLogger(NodeImpl.class.getName() + "."
				+ nodeID.toString());
		logger.setParent(Activator.LOGGER);

		this.impl = impl;
		asyncExecutor = impl.getAsyncExecutor();
		this.nodeID = nodeID;
		this.nodeURL = nodeURL;
		this.references = references;
		this.entries = entries;
		notifyLock = new ReentrantLock(true);

		// create endpoint for incoming connections
		myEndpoint = Endpoint.createEndpoint(this, nodeURL);
		myEndpoint.listen();
	}

	/**
	 * Makes this endpoint accept entries by other nodes. Is invoked by
	 * ChordImpl only.
	 */
	final void acceptEntries() {
		myEndpoint.acceptEntries();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect() {
		myEndpoint.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Node findSuccessor(final ID key) {
		return impl.findSuccessor(key);
	}

	/**
	 * @return
	 */
	final Executor getAsyncExecutor() {
		return asyncExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void insertEntry(final Entry toInsert)
			throws CommunicationException {
		// if (logger.isEnabledFor(DEBUG)) {
		logger.info("Inserting entry with id " + toInsert.getId() + " at node "
				+ nodeID);
		// }

		// Possible, but rare situation: a new node has joined which now is
		// responsible for the id!
		if (references.getPredecessor() != null
				&& !toInsert.getId().isInInterval(
						references.getPredecessor().getNodeID(), nodeID)) {
			references.getPredecessor().insertEntry(toInsert);
			return;
		}

		// add entry to local repository
		entries.add(toInsert);

		// create set containing this entry for insertion of replicates at all
		// nodes in successor list
		final Set<Entry> newEntries = new HashSet<Entry>();
		newEntries.add(toInsert);

		// invoke insertReplicates method on all nodes in successor list
		final Set<Entry> mustBeFinal = new HashSet<Entry>(newEntries);
		for (final Node successor : references.getSuccessors()) {
			asyncExecutor.execute(new Runnable() {

				public void run() {
					try {
						successor.insertReplicas(mustBeFinal);
					} catch (final CommunicationException e) {
						// do nothing
					}
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void insertReplicas(final Set<Entry> replicatesToInsert) {
		entries.addAll(replicatesToInsert);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void leavesNetwork(final Node predecessor) {
		// if (logger.isEnabledFor(INFO)) {
		logger.info("Leaves network invoked; " + nodeID
				+ ". Updating references.");
		logger.info("New predecessor " + predecessor.getNodeID());
		// }
		// if (logger.isEnabledFor(DEBUG)) {
		logger.info("References before update: " + references.toString());
		// }
		references.removeReference(references.getPredecessor());
		// if (logger.isEnabledFor(DEBUG)) {
		logger.info("References after update: " + references.toString());
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Node> notify(final Node potentialPredecessor) {
		/*
		 * Mutual exclusion between notify and notifyAndCopyEntries. 17.03.2008.
		 * sven.
		 */
		notifyLock.lock();
		try {
			// the result will contain the list of successors as well as the
			// predecessor of this node
			final List<Node> result = new LinkedList<Node>();

			// add reference on predecessor as well as on successors to result
			if (references.getPredecessor() != null) {
				result.add(references.getPredecessor());
			} else {
				result.add(potentialPredecessor);
			}
			result.addAll(references.getSuccessors());

			// add potential predecessor to successor list and finger table and
			// set
			// it as predecessor if no better predecessor is available
			references.addReferenceAsPredecessor(potentialPredecessor);
			return result;
		} finally {
			notifyLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RefsAndEntries notifyAndCopyEntries(
			final Node potentialPredecessor) throws CommunicationException {
		/*
		 * Mutual exclusion between notify and notifyAndCopyEntries. 17.03.2008.
		 * sven.
		 */
		notifyLock.lock();
		try {
			// copy all entries which lie between the local node ID and the ID
			// of
			// the potential predecessor, including those equal to potential
			// predecessor
			final Set<Entry> copiedEntries = entries.getEntriesInInterval(
					nodeID, potentialPredecessor.getNodeID());

			return new RefsAndEntries(this.notify(potentialPredecessor),
					copiedEntries);
		} finally {
			notifyLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void ping() {
		// do nothing---returning of method is proof of live
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeEntry(final Entry entryToRemove)
			throws CommunicationException {

		// if (logger.isEnabledFor(DEBUG)) {
		logger.info("Removing entry with id " + entryToRemove.getId()
				+ " at node " + nodeID);
		// }

		// Possible, but rare situation: a new node has joined which now is
		// responsible for the id!
		if (references.getPredecessor() != null
				&& !entryToRemove.getId().isInInterval(
						references.getPredecessor().getNodeID(), nodeID)) {
			references.getPredecessor().removeEntry(entryToRemove);
			return;
		}

		// remove entry from repository
		entries.remove(entryToRemove);

		// create set containing this entry for removal of replicates at all
		// nodes in successor list
		final Set<Entry> entriesToRemove = new HashSet<Entry>();
		entriesToRemove.add(entryToRemove);

		// invoke removeReplicates method on all nodes in successor list
		final List<Node> successors = references.getSuccessors();
		final ID id = nodeID;
		for (final Node successor : successors) {
			asyncExecutor.execute(new Runnable() {

				public void run() {
					try {
						// remove only replica of removed entry
						successor.removeReplicas(id, entriesToRemove);
					} catch (final CommunicationException e) {
						// do nothing for the moment
					}
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeReplicas(final ID sendingNodeID,
			final Set<Entry> replicasToRemove) {
		if (replicasToRemove.size() == 0) {
			// remove all replicas in interval
			// final boolean debug = logger.isEnabledFor(DEBUG);
			// if (debug) {
			logger.info("Removing replicas. Current no. of entries: "
					+ entries.getNumberOfStoredEntries());
			// }
			/*
			 * Determine entries to remove. These entries are located between
			 * the id of the local peer and the argument sendingNodeID
			 */
			final Set<Entry> allReplicasToRemove = entries
					.getEntriesInInterval(nodeID, sendingNodeID);
			// if (debug) {
			logger.info("Replicas to remove " + allReplicasToRemove);
			logger.info("Size of replicas to remove "
					+ allReplicasToRemove.size());
			// }

			/*
			 * Remove entries
			 */
			entries.removeAll(allReplicasToRemove);

			// if (debug) {
			logger.info("Removed replicas??? Current no. of entries: "
					+ entries.getNumberOfStoredEntries());
			// }
		} else {
			// remove only replicas of given entry
			entries.removeAll(replicasToRemove);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<Entry> retrieveEntries(final ID id)
			throws CommunicationException {

		// Possible, but rare situation: a new node has joined which now is
		// responsible for the id!
		if (references.getPredecessor() != null
				&& !id.isInInterval(references.getPredecessor().getNodeID(),
						nodeID)) {
			logger.severe("The rare situation has occured at time "
					+ System.currentTimeMillis() + ", id to look up=" + id
					+ ", id of local node=" + nodeID + ", id of predecessor="
					+ references.getPredecessor().getNodeID());
			return references.getPredecessor().retrieveEntries(id);
		}

		// return entries from local repository
		// for this purpose create a copy of the Set in order to allow the
		// thread retrieving the entries to modify the Set without modifying the
		// internal Set of entries. sven
		return entries.getEntries(id);
	}

}
