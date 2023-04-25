/***************************************************************************
 *                                                                         *
 *                             FingerTable.java                            *
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * Stores references on the nodes in the finger table and provides methods for
 * querying and manipulating this table.
 * 
 * @author Karsten Loesing
 * @version 1.0.5
 */
final class FingerTable {

	/**
	 * ID of local node.
	 */
	private final ID localID;

	/**
	 * Finger table data.
	 */
	private final Node[] remoteNodes;

	/**
	 * Reference on parent object.
	 */
	private final References references;

	/**
	 * Object logger.
	 */
	private final Logger logger;

	/**
	 * Creates an initially empty finger table. The table size is determined by
	 * the given ID's length. A reference on the parent object of type
	 * References is stored for being able to determine and disconnect unused
	 * references after removing them from the table.
	 * 
	 * @param localID
	 *            ID of local node.
	 * @param references
	 *            Reference on parent object.
	 * @throws NullPointerException
	 *             If either of the parameters is <code>null</code>.
	 */
	FingerTable(final ID localID, final References references) {

		if (localID == null || references == null) {
			throw new NullPointerException(
					"Neither parameter of the constructor may contain a null "
							+ "value!");
		}

		logger = Logger.getLogger(FingerTable.class + "." + localID);
		logger.setParent(Activator.LOGGER);
		logger.info("Logger initialized.");

		this.references = references;
		this.localID = localID;
		remoteNodes = new Node[localID.getLength()];
	}

	/**
	 * Adds the given reference to all finger table entries of which the start
	 * index is in the interval (local node ID, new node ID) and of which the
	 * current entry is <code>null</code> or further away from the local node ID
	 * than the new node ID (ie. the new node ID is in the interval (local node
	 * ID, currently stored node ID) ).
	 * 
	 * @param proxy
	 *            Reference to be added to the finger table.
	 * @throws NullPointerException
	 *             If given reference is <code>null</code>.
	 */
	final void addReference(final Node proxy) {

		if (proxy == null) {
			final NullPointerException e = new NullPointerException(
					"Reference to add may not be null!");
			logger.severe("Null pointer");
			throw e;
		}

		// for logging
		int lowestWrittenIndex = -1;
		int highestWrittenIndex = -1;

		for (int i = 0; i < remoteNodes.length; i++) {

			final ID startOfInterval = localID.addPowerOfTwo(i);
			if (!startOfInterval.isInInterval(localID, proxy.getNodeID())) {
				break;
			}

			// for logging
			if (lowestWrittenIndex == -1) {
				lowestWrittenIndex = i;
			}
			highestWrittenIndex = i;

			if (getEntry(i) == null) {
				setEntry(i, proxy);
			} else if (proxy.getNodeID().isInInterval(localID,
					getEntry(i).getNodeID())) {
				final Node oldEntry = getEntry(i);
				setEntry(i, proxy);
				references.disconnectIfUnreferenced(oldEntry);
			}
		}

		// logging
		// if (logger.isEnabledFor(DEBUG)) {
		if (highestWrittenIndex == -1) {

			logger.info("addReference did not add the given reference, "
					+ "because it did not fit anywhere!");

		}
		// }
		// if (logger.isEnabledFor(INFO)) {
		if (highestWrittenIndex == lowestWrittenIndex) {

			logger.info("Added reference to finger table entry "
					+ highestWrittenIndex);

		} else {

			logger.info("Added reference to finger table entries "
					+ lowestWrittenIndex + " to " + highestWrittenIndex);

		}
		// }
	}

	/**
	 * Determines if the given reference is stored somewhere in the finger
	 * table.
	 * 
	 * @param newReference
	 *            Reference of which existence shall be determined.
	 * @throws NullPointerException
	 *             If reference to look for is <code>null</code>.
	 * @return <code>true</code>, if the given reference exists in the finger
	 *         table, or <code>false</code>, else.
	 */
	final boolean containsReference(final Node newReference) {
		if (newReference == null) {
			final NullPointerException e = new NullPointerException(
					"Reference to check must not be null!");
			logger.severe("Null pointer");
			throw e;
		}
		for (int i = 0; i < remoteNodes.length; i++) {
			if (newReference.equals(remoteNodes[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines closest preceding node of given id.
	 * 
	 * @param key
	 *            ID of which the closest preceding node shall be determined.
	 * @throws NullPointerException
	 *             If given key is null.
	 * @return Reference to the node which most closely precedes the given ID.
	 *         <code>null</code> if no node has been found.
	 */
	final Node getClosestPrecedingNode(final ID key) {
		if (key == null) {
			final NullPointerException e = new NullPointerException(
					"ID to determine the closest preceding node may not be "
							+ "null!");
			logger.severe("Null pointer");
			throw e;
		}
		// final boolean debug = logger.isEnabledFor(DEBUG);
		for (int i = remoteNodes.length - 1; i >= 0; i--) {
			if (remoteNodes[i] != null
					&& remoteNodes[i].getNodeID().isInInterval(localID, key)) {
				// if (debug) {
				logger.info("Closest preceding node for ID " + key + " is "
						+ remoteNodes[i].toString());
				// }
				return remoteNodes[i];
			}
		}

		// if (debug) {
		logger.info("There is no closest preceding node for ID " + key
				+ " -- returning null!");
		// }
		return null;
	}

	/**
	 * Returns a copy of the finger table entries.
	 * 
	 * @return Copy of finger table entries.
	 */
	final Node[] getCopyOfReferences() {
		logger.info("Returning copy of references.");

		final Node[] copy = new Node[remoteNodes.length];
		System.arraycopy(remoteNodes, 0, copy, 0, remoteNodes.length);
		return copy;
	}

	/**
	 * Returns the reference stored at the given index.
	 * 
	 * @param index
	 *            Index of entry to be returned.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If given index is not contained in the finger table.
	 * @return Reference stored at the given index.
	 */
	private final Node getEntry(final int index) {

		if (index < 0 || index >= remoteNodes.length) {
			final ArrayIndexOutOfBoundsException e = new ArrayIndexOutOfBoundsException(
					"getEntry was invoked with an index out of array "
							+ "bounds; index=" + index + ", length of array="
							+ remoteNodes.length);
			logger.severe("Out of bounds!");
			throw e;
		}

		return remoteNodes[index];
	}

	/**
	 * @param i
	 * @return The first (i+1) entries of finger table. If there are fewer then
	 *         i+1 entries only these are returned.
	 */
	final List<Node> getFirstFingerTableEntries(final int i) {
		final Set<Node> result = new HashSet<Node>();
		for (int j = 0; j < remoteNodes.length; j++) {
			if (getEntry(j) != null) {
				result.add(getEntry(j));
			}
			if (result.size() >= i) {
				break;
			}
		}
		return new ArrayList<Node>(result);
	}

	/**
	 * Removes all occurences of the given node from finger table.
	 * 
	 * @param node1
	 *            Reference to be removed from the finger table.
	 * @throws NullPointerException
	 *             If given reference is <code>null</code>.
	 */
	final void removeReference(final Node node1) {

		if (node1 == null) {
			final NullPointerException e = new NullPointerException(
					"removeReference cannot be invoked with value null!");
			logger.severe("Null pointer");
			throw e;
		}

		// for logging
		int lowestWrittenIndex = -1;
		int highestWrittenIndex = -1;

		// determine node reference with next larger ID than ID of node
		// reference to remove
		Node referenceForReplacement = null;
		for (int i = localID.getLength() - 1; i >= 0; i--) {
			final Node n = getEntry(i);
			if (node1.equals(n)) {
				break;
			}
			if (n != null) {
				referenceForReplacement = n;
			}
		}

		// remove reference(s)
		for (int i = 0; i < remoteNodes.length; i++) {
			if (node1.equals(remoteNodes[i])) {

				// for logging
				if (lowestWrittenIndex == -1) {
					lowestWrittenIndex = i;
				}
				highestWrittenIndex = i;

				if (referenceForReplacement == null) {
					unsetEntry(i);
				} else {
					setEntry(i, referenceForReplacement);
				}
			}
		}

		// try to add references of successor list to fill 'holes' in finger
		// table
		final List<Node> referencesOfSuccessorList = new ArrayList<Node>(
				references.getSuccessors());
		referencesOfSuccessorList.remove(node1);
		for (final Node referenceToAdd : referencesOfSuccessorList) {
			addReference(referenceToAdd);
		}

		// logging
		// if (logger.isEnabledFor(DEBUG)) {
		if (highestWrittenIndex == -1) {
			logger
					.info("removeReference did not remove the given reference, "
							+ "because it did not exist in finger table "
							+ "anywhere!");
		} else if (highestWrittenIndex == lowestWrittenIndex) {
			logger.info("Removed reference from finger table entry "
					+ highestWrittenIndex);
		} else {
			logger.info("Removed reference from finger table entries "
					+ lowestWrittenIndex + " to " + highestWrittenIndex);
		}
		// }

	}

	/**
	 * Sets one table entry to the given reference.
	 * 
	 * @param index
	 *            Index of table entry.
	 * @param proxy
	 *            Reference to store.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If given index is not contained in the finger table.
	 * @throws NullPointerException
	 *             If given reference is <code>null</code>.
	 */
	private final void setEntry(final int index, final Node proxy) {

		if (index < 0 || index >= remoteNodes.length) {
			final ArrayIndexOutOfBoundsException e = new ArrayIndexOutOfBoundsException(
					"setEntry was invoked with an index out of array "
							+ "bounds; index=" + index + ", length of array="
							+ remoteNodes.length);
			logger.severe("Out of bounds!");
			throw e;
		}

		if (proxy == null) {
			final NullPointerException e = new NullPointerException(
					"Reference to proxy may not be null!");
			logger.severe("Null pointer");
			throw e;
		}

		remoteNodes[index] = proxy;

		// if (logger.isEnabledFor(DEBUG)) {
		logger.info("Entry " + index + " set to " + proxy.toString());
		// }
	}

	/**
	 * Returns a formatted string representation of this finger table.
	 * 
	 * @return String representation containing one line per reference, together
	 *         with the annotation which table entries contain this reference.
	 */
	@Override
	public final String toString() {

		final StringBuilder result = new StringBuilder("Finger table:\n");

		int lastIndex = -1;
		ID lastNodeID = null;
		URL lastNodeURL = null;
		for (int i = 0; i < remoteNodes.length; i++) {
			final Node next = remoteNodes[i];
			if (next == null) {
				// row ended or did not even start
				if (lastIndex != -1 && lastNodeID != null) {
					// row ended
					result.append("  "
							+ lastNodeID
							+ ", "
							+ lastNodeURL
							+ " "
							+ (i - 1 - lastIndex > 0 ? "(" + lastIndex + "-"
									+ (i - 1) + ")" : "(" + (i - 1) + ")")
							+ "\n");
					lastIndex = -1;
					lastNodeID = null;
					lastNodeURL = null;
				} else {
					// null at beginning
				}
			} else if (lastNodeID == null) {
				// found first reference in a row
				lastIndex = i;
				lastNodeID = next.getNodeID();
				lastNodeURL = next.getNodeURL();
			} else if (!lastNodeID.equals(next.getNodeID())) {
				// found different reference in finger table
				result.append("  "
						+ lastNodeID
						+ ", "
						+ lastNodeURL
						+ " "
						+ (i - 1 - lastIndex > 0 ? "(" + lastIndex + "-"
								+ (i - 1) + ")" : "(" + (i - 1) + ")") + "\n");
				lastNodeID = next.getNodeID();
				lastNodeURL = next.getNodeURL();
				lastIndex = i;
			} else {
				// found next reference in a row
			}
		}

		// display last row
		if (lastNodeID != null && lastIndex != -1) {
			// row ended
			result.append("  "
					+ lastNodeID
					+ ", "
					+ lastNodeURL
					+ " "
					+ (remoteNodes.length - 1 - lastIndex > 0 ? "(" + lastIndex
							+ "-" + (remoteNodes.length - 1) + ")" : "("
							+ (remoteNodes.length - 1) + ")") + "\n");
			lastNodeID = null;
		}

		return result.toString();
	}

	/**
	 * Sets the reference at the given index to <code>null</code> and triggers
	 * to disconnect that node, if no other reference to it is kept any more.
	 * 
	 * @param index
	 *            Index of entry to be set to <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If given index is not contained in the finger table.
	 */
	private final void unsetEntry(final int index) {
		if (index < 0 || index >= remoteNodes.length) {
			final ArrayIndexOutOfBoundsException e = new ArrayIndexOutOfBoundsException(
					"unsetEntry was invoked with an index out of array "
							+ "bounds; index=" + index + ", length of array="
							+ remoteNodes.length);
			logger.severe("Out of bounds!");
			throw e;
		}

		// remember overwritten reference
		final Node overwrittenNode = getEntry(index);

		// set reference to null
		remoteNodes[index] = null;

		if (overwrittenNode == null) {
			logger.info("unsetEntry did not change anything, because "
					+ "entry was null before.");
		} else {
			// check if overwritten reference does not exist any more
			references.disconnectIfUnreferenced(overwrittenNode);
			// if (logger.isEnabledFor(DEBUG)) {
			logger.info("Entry set to null: index=" + index
					+ ", overwritten node=" + overwrittenNode.toString());
			// }
		}
	}
}
