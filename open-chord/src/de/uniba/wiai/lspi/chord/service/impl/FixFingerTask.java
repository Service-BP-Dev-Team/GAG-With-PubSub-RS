/***************************************************************************
 *                                                                         *
 *                            FixFingerTask.java                           *
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

import java.util.Random;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * Looks up the node for a certain ID and stores the reference of the
 * responsible node in the local finger table.
 * 
 * @author Sven Kaffille, Karsten Loesing
 * @version 1.0.5
 */
final class FixFingerTask implements Runnable {

	/**
	 * Instance of random generator for randomly picking another finger to fix.
	 */
	private final Random random = new Random();

	/**
	 * Parent object for invoking findSuccessor.
	 */
	private final NodeImpl parent;

	/**
	 * Object logger.
	 */
	private final Logger logger;

	/**
	 * Copy of the local node's ID for determining which ID to look up.
	 */
	private final ID localID;

	/**
	 * Reference on routing table.
	 */
	private final References references;

	/**
	 * Creates a new instance, but without starting a thread running it.
	 * 
	 * @param parent
	 *            Parent object for invoking findSuccessor.
	 * @param localID
	 *            Copy of the local node's ID for determining which ID to look
	 *            up.
	 * @param references
	 *            Reference on routing table.
	 * @throws NullPointerException
	 *             If either of the parameters has value <code>null</code>.
	 */
	FixFingerTask(final NodeImpl parent, final ID localID,
			final References references) {
		if (parent == null || localID == null || references == null) {
			throw new NullPointerException(
					"Neither parameter of constructor may be null!");
		}
		// June 21st, 2006. Corrected logger name from FixFingerTask.class to
		// actual version. sven
		logger = Logger
				.getLogger(FixFingerTask.class.getName() + "." + localID);
		logger.setParent(Activator.LOGGER);

		this.parent = parent;
		this.localID = localID;
		this.references = references;
	}

	public void run() {

		try {

			final int nextFingerToFix = random.nextInt(localID.getLength());
			// if (this.logger.isEnabledFor(DEBUG)) {
			logger.info("fixFingers tries to get finger for key "
					+ localID.addPowerOfTwo(nextFingerToFix).toString());
			// }

			// look up reference
			final ID lookForID = localID.addPowerOfTwo(nextFingerToFix);
			Node newReference;
			// try {
			newReference = parent.findSuccessor(lookForID);

			// add new reference to finger table, if not yet included
			if (newReference != null
					&& !references.containsReference(newReference)) {
				// if (this.logger.isEnabledFor(INFO)) {
				logger.info("Adding new reference "
						+ newReference.getNodeID().toString());
				// }
				references.addReference(newReference);
			}

			// if (this.logger.isEnabledFor(DEBUG)) {
			logger.info("Invocation of fix fingers was successful");
			// }

		} catch (final Exception e) {
			logger.warning("Unexpected Exception caught in FixFingerTask!");
		}
	}
}
