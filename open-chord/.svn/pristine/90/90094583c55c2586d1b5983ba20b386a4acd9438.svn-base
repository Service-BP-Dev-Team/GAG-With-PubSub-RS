/***************************************************************************
 *                                                                         *
 *                            StabilizeTask.java                           *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                      karsten.loesing@uni-bamberg.de                 *
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

import java.util.List;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;

/**
 * Invokes notify method on successor.
 * 
 * @author Karsten Loesing, Sven Kaffille
 * @version 1.0.5
 */
final class StabilizeTask implements Runnable {

	/**
	 * Parent object for performing stabilization.
	 */
	private final NodeImpl parent;

	/**
	 * Reference on routing table.
	 */
	private final References references;

	private final Entries entries;

	/**
	 * Object logger.
	 */
	protected final static Logger logger = Logger.getLogger(StabilizeTask.class
			.getName());
	static {
		StabilizeTask.logger.setParent(Activator.LOGGER);
	}

	/**
	 * Creates a new instance, but without starting a thread running it.
	 * 
	 * @param parent
	 *            Parent object for performing stabilization.
	 * @param references
	 *            Reference on routing table.
	 * @throws NullPointerException
	 *             If either of the parameters is <code>null</code>.
	 */
	StabilizeTask(final NodeImpl parent, final References references,
			final Entries entries) {

		if (parent == null || references == null || entries == null) {
			throw new NullPointerException(
					"No argument to constructor may be null!");
		}

		this.parent = parent;
		this.references = references;
		this.entries = entries;
	}

	public void run() {
		try {

			// final boolean debugEnabled = StabilizeTask.logger
			// .isEnabledFor(DEBUG);
			// final boolean infoEnabled =
			// StabilizeTask.logger.isEnabledFor(INFO);

			// start of method
			// if (debugEnabled) {
			StabilizeTask.logger
					.info("Stabilize method has been invoked periodically");
			// }

			// determine successor
			final Node successor = references.getSuccessor();
			if (successor == null) {

				// nothing to stabilize
				// if (infoEnabled) {
				StabilizeTask.logger
						.info("Nothing to stabilize, as successor is null");
				return;
				// }

			} else {

				// notify successor and obtain its predecessor reference and
				// successor list
				List<Node> mySuccessorsPredecessorAndSuccessorList;
				try {
					/*
					 * NOTIFYING successor.
					 */
					mySuccessorsPredecessorAndSuccessorList = successor
							.notify(parent);
					// if (infoEnabled) {
					StabilizeTask.logger
							.info("Received response to notify request from "
									+ "successor" + successor.getNodeID());
					// }
				} catch (final CommunicationException e) {
					// if (debugEnabled) {
					StabilizeTask.logger.severe("Invocation of notify on node "
							+ successor.getNodeID()
							+ " was not successful due to a "
							+ "communication failure! Successor has "
							+ "failed during stabilization! "
							+ "Removing successor!");
					// }
					references.removeReference(successor);
					return;
				}

				/*
				 * 19.06.2007. sven Test if our successor has a different
				 * predecessor than this node.
				 */
				if (mySuccessorsPredecessorAndSuccessorList.size() > 0
						&& mySuccessorsPredecessorAndSuccessorList.get(0) != null) {
					if (!parent.getNodeID().equals(
							mySuccessorsPredecessorAndSuccessorList.get(0)
									.getNodeID())) {
						/*
						 * If it does not know us, we have to fetch all entries
						 * relevant for us.
						 */
						final RefsAndEntries refsAndEntries = successor
								.notifyAndCopyEntries(parent);
						mySuccessorsPredecessorAndSuccessorList = refsAndEntries
								.getRefs();
						/*
						 * and have to store them locally
						 */
						entries.addAll(refsAndEntries.getEntries());
					}
				}

				for (final Node newReference : mySuccessorsPredecessorAndSuccessorList) {
					references.addReference(newReference);

					// if (debugEnabled) {
					StabilizeTask.logger.info("Added new reference: "
							+ newReference);
					// }
				}
				// if (infoEnabled) {
				StabilizeTask.logger.info("Invocation of notify on node "
						+ successor.getNodeID() + " was successful");
				// }
			}
		} catch (final Exception e) {
			StabilizeTask.logger
					.severe("Unexpected Exception caught in StabilizeTask!");
			e.printStackTrace();
		}
	}
}
