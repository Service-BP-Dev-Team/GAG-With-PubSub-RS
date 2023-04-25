/***************************************************************************
 *                                                                         *
 *                            SuccessorList.java                           *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *   			   			karsten.loesing@uni-bamberg.de                 *
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * Stores references on the next nodes in the Chord ring and provides methods
 * for querying and manipulating this list.
 * 
 * @author Karsten Loesing
 * @version 1.0.5
 */
final class SuccessorList {

	/**
	 * List storing the successor references in correct order.
	 */
	private List<Node> successors = null;

	/**
	 * Local node ID - initialized in constructor.
	 */
	private final ID localID;

	/**
	 * Maximum number of references - initialized in constructor.
	 */
	private final int capacity;

	/**
	 * Reference on parent object of type References.
	 */
	private final References references;

	/**
	 * Reference on Entries.
	 */
	private final Entries entries;

	/**
	 * Object logger.
	 */
	private final Logger logger;

	/**
	 * Creates an empty list of successors.
	 * 
	 * @param localID
	 *            This node's ID; is used for comparison operations of other
	 *            node references.
	 * @param numberOfEntries
	 *            Number of entries to be stored in this successor list.
	 * @param parent
	 *            Reference on this objects parent.
	 * @param entries
	 *            Reference on the entry repository for replication purposes.
	 */
	SuccessorList(final ID localID, final int numberOfEntries,
			final References parent, final Entries entries) {
		logger = Logger.getLogger(SuccessorList.class + "." + localID);
		logger.setParent(Activator.LOGGER);
		logger.info("Logger initialized.");
		if (localID == null || parent == null || entries == null) {
			final NullPointerException e = new NullPointerException(
					"Neither paremeter of this constructor may have value "
							+ "null!");
			logger.severe("Null pointer");
			throw e;
		}
		if (numberOfEntries < 1) {
			throw new IllegalArgumentException(
					"SuccessorList has to be at least of length 1! "
							+ numberOfEntries + "is not a valid value!");
		}
		this.localID = localID;
		capacity = numberOfEntries;
		successors = new LinkedList<Node>();
		references = parent;
		this.entries = entries;

	}

	/**
	 * Adds a successor references, preserving ordering of list elements.
	 * 
	 * @param nodeToAdd
	 *            Reference to be added.
	 * @throws NullPointerException
	 *             If node to add is <code>null</code>.
	 */
	final void addSuccessor(final Node nodeToAdd) {

		// check parameters
		if (nodeToAdd == null) {
			final NullPointerException e = new NullPointerException(
					"Parameter may not be null!");
			logger.severe("Null pointer");
			throw e;
		}

		// final boolean debug = logger.isEnabledFor(DEBUG);
		// final boolean info = logger.isEnabledFor(INFO);
		// is reference already contained in successor list?
		if (successors.contains(nodeToAdd)) {
			// if (debug) {
			logger.info("Reference to new node " + nodeToAdd.toString()
					+ " is not added to successor list, because it is "
					+ "already contained.");
			// }
			return;
		}

		// if new ID is between last ID in list and this node's own ID _AND_
		// successor list already has maximum allowed list, the new reference IS
		// NOT added!
		if (successors.size() >= capacity
				&& nodeToAdd.getNodeID().isInInterval(
						successors.get(successors.size() - 1).getNodeID(),
						localID)) {
			// do nothing
			// if (debug) {
			logger.info("Reference to new node " + nodeToAdd.toString()
					+ " is not added to successor list, because the "
					+ "list is already full and the new reference is "
					+ "further away from the local node than all other "
					+ "successors.");
			// }
			return;
		}

		// insert node to successors

		// insert before an existing element?
		boolean inserted = false;

		for (int i = 0; i < successors.size() && !inserted; i++) {
			if (nodeToAdd.getNodeID().isInInterval(localID,
					successors.get(i).getNodeID())) {
				successors.add(i, nodeToAdd);
				// if (info) {
				logger.info("Added new reference at position " + i);
				// }
				inserted = true;
			}
		}

		// insert at end if list not long enough
		if (!inserted) {
			successors.add(nodeToAdd);
			// if (info) {
			logger.info("Added new reference to end of list");
			// }
			inserted = true;
		}

		// determine ID range of entries this node is responsible for
		// and replicate them on new node
		ID fromID;
		final Node predecessor = references.getPredecessor();
		if (predecessor != null) {
			// common case: have a predecessor
			fromID = predecessor.getNodeID();
		} else {
			// have no predecessor
			// do I have any preceding node?
			final Node precedingNode = references
					.getClosestPrecedingNode(localID);
			if (precedingNode != null) {
				// use ID of preceding node
				fromID = precedingNode.getNodeID();
			} else {
				// use own ID (leads to replicating the whole ring); should not
				// happen
				fromID = localID;
			}
		}

		// replicate entries from determined ID up to local ID
		final ID toID = localID;
		final Set<Entry> entriesToReplicate = entries.getEntriesInInterval(
				fromID, toID);
		try {
			nodeToAdd.insertReplicas(entriesToReplicate);
			logger.info("Inserted replicas to new reference");
		} catch (final CommunicationException e) {
			logger.warning("Entries could not be replicated to node "
					+ nodeToAdd + "!");
		}

		// remove last element from this.successors, if maximum exceeded

		if (successors.size() > capacity) {
			final Node nodeToDelete = successors.get(successors.size() - 1);
			successors.remove(nodeToDelete);

			// determine ID range of entries this node is responsible
			// for and remove replicates of them from discarded successor
			// 
			// Could be replaced by a new method:
			// nodeToDelete.removeReplicas(fromID, toID);
			// Set<Entry> replicatedEntries = this.entries.getEntriesInInterval(
			// fromID, toID);
			try {
				// remove all replicas!
				nodeToDelete.removeReplicas(localID, new HashSet<Entry>());
				logger.info("Removed replicas from node " + nodeToDelete);
			} catch (final CommunicationException e) {
				logger.warning("Replicas of entries could not be removed "
						+ "from node " + nodeToDelete + "!");
			}

			// if (debug) {
			logger.info("If no other reference to node " + nodeToDelete
					+ " exists any more, it is disconnected.");
			// }
			references.disconnectIfUnreferenced(nodeToDelete);
		}

	}

	/**
	 * Determines if the given reference is contained in this successor list.
	 * 
	 * @param nodeToLookup
	 *            Reference to look up.
	 * @throws NullPointerException
	 *             If node to look up is <code>null</code>.
	 * @return <code>true</code>, if reference is contained, and
	 *         <code>false</code>, else.
	 */
	final boolean containsReference(final Node nodeToLookup) {
		if (nodeToLookup == null) {
			final NullPointerException e = new NullPointerException(
					"Node to look up may not be null!");
			logger.severe("Null pointer");
			throw e;
		}
		return successors.contains(nodeToLookup);
	}

	/**
	 * @return the capacity
	 */
	final int getCapacity() {
		return capacity;
	}

	/**
	 * Returns closest preceding node of given ID.
	 * 
	 * @param idToLookup
	 *            ID of which closest preceding node is sought-after.
	 * @throws NullPointerException
	 *             If ID to look up is <code>null</code>.
	 * @return Reference on closest preceding node of given ID.
	 */
	final Node getClosestPrecedingNode(final ID idToLookup) {

		if (idToLookup == null) {
			final NullPointerException e = new NullPointerException(
					"ID to look up may not be null!");
			logger.severe("Null pointer");
			throw e;
		}

		for (int i = successors.size() - 1; i >= 0; i--) {
			final Node nextNode = successors.get(i);
			if (nextNode.getNodeID().isInInterval(localID, idToLookup)) {
				return nextNode;
			}
		}

		return null;
	}

	/**
	 * Returns the reference on the direct successor; may be <code>null</code>,
	 * if the list is empty.
	 * 
	 * @return Direct successor (or <code>null</code> if list is empty).
	 */
	final Node getDirectSuccessor() {
		if (successors.size() == 0) {
			return null;
		}
		return successors.get(0);
	}

	/**
	 * Returns an unmodifiable copy of the ordered successor list, starting with
	 * the closest following node.
	 * 
	 * @return Unmodifiable copy of successor list.
	 */
	final List<Node> getReferences() {
		return Collections.unmodifiableList(successors);
	}

	final int getSize() {
		return successors.size();
	}

	/**
	 * Removes a successor reference without leaving a gap in the list
	 * 
	 * @param nodeToDelete
	 *            Reference to be removed.
	 * @throws NullPointerException
	 *             If reference to remove is <code>null</code>.
	 */
	final void removeReference(final Node nodeToDelete) {
		if (nodeToDelete == null) {
			final NullPointerException e = new NullPointerException(
					"Reference to remove may not be null!");
			logger.severe("Null pointer");
			throw e;
		}
		successors.remove(nodeToDelete);

		// try to add references of finger table to fill 'hole' in successor
		// list
		final List<Node> referencesOfFingerTable = references
				.getFirstFingerTableEntries(capacity);
		referencesOfFingerTable.remove(nodeToDelete);
		for (final Node referenceToAdd : referencesOfFingerTable) {
			addSuccessor(referenceToAdd);
		}
	}

	/**
	 * Returns a string representation of this successor list.
	 * 
	 * @return String representation of list.
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("Successor List:\n");
		for (final Node next : successors) {
			result.append("  " + next.getNodeID().toString() + ", "
					+ next.getNodeURL() + "\n");
		}
		return result.toString();
	}

}
