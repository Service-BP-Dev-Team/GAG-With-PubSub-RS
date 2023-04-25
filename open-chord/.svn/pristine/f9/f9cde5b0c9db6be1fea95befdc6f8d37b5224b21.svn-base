/***************************************************************************
 *                                                                         *
 *                               Entries.java                              *
 *                            -------------------                          *
 *   date                 : 28.02.2005                                     *
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * Stores entries for the local node in a local hash table and provides methods
 * for accessing them. It IS allowed, that multiple objects of type
 * {@link Entry} with same {@link ID} are stored!
 * 
 * @author Karsten Loesing, Sven Kaffille
 * @version 1.0.5
 */

/*
 * 23.12.2006. Fixed synchronization. The Map<ID, Set<Entry>> entries must be
 * synchronized with a synchronized statement, when executing several methods
 * that depend on each other. This would also apply to the internal Set<Entry>
 * if it were not only used in the same synchronized statements for entries,
 * which than functions as a synchronization point. It must also be locked by a
 * synchronized statement, when iterating over it. TODO: What about fairness?
 * sven
 */
final class Entries {

	/**
	 * Object logger.
	 */
	private final static Logger logger = Logger.getLogger(Entries.class
			.getName());
	static {
		Entries.logger.setParent(Activator.LOGGER);
	}

	// private final static boolean debugEnabled = Entries.logger
	// .isEnabledFor(Logger.LogLevel.DEBUG);

	/**
	 * Local hash table for entries. Is synchronized, st. methods do not have to
	 * be synchronized.
	 */
	private Map<ID, Set<Entry>> entries = null;

	/**
	 * Creates an empty repository for entries.
	 */
	Entries() {
		entries = Collections.synchronizedMap(new TreeMap<ID, Set<Entry>>());
	}

	/**
	 * Stores one entry to the local hash table.
	 * 
	 * @param entryToAdd
	 *            Entry to add to the repository.
	 * @throws NullPointerException
	 *             If entry to add is <code>null</code>.
	 */
	final void add(final Entry entryToAdd) {

		if (entryToAdd == null) {
			final NullPointerException e = new NullPointerException(
					"Entry to add may not be null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}

		Set<Entry> values;
		synchronized (entries) {
			if (entries.containsKey(entryToAdd.getId())) {
				values = entries.get(entryToAdd.getId());
			} else {
				values = new HashSet<Entry>();
				entries.put(entryToAdd.getId(), values);
			}
			values.add(entryToAdd);
		}
		// if (Entries.debugEnabled) {
		Entries.logger.info("Entry was added: " + entryToAdd);
		// }
	}

	/**
	 * Stores a set of entries to the local hash table.
	 * 
	 * @param entriesToAdd
	 *            Set of entries to add to the repository.
	 * @throws NullPointerException
	 *             If set reference is <code>null</code>.
	 */
	final void addAll(final Set<Entry> entriesToAdd) {

		if (entriesToAdd == null) {
			final NullPointerException e = new NullPointerException(
					"Set of entries to be added to the local hash table may "
							+ "not be null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}

		for (final Entry nextEntry : entriesToAdd) {
			add(nextEntry);
		}

		// if (Entries.debugEnabled) {
		Entries.logger.info("Set of entries of length " + entriesToAdd.size()
				+ " was added.");
		// }
	}

	/**
	 * Returns an unmodifiable map of all stored entries.
	 * 
	 * @return Unmodifiable map of all stored entries.
	 */
	final Map<ID, Set<Entry>> getEntries() {
		return Collections.unmodifiableMap(entries);
	}

	/**
	 * Returns a set of entries matching the given ID. If no entries match the
	 * given ID, an empty set is returned.
	 * 
	 * @param id
	 *            ID of entries to be returned.
	 * @throws NullPointerException
	 *             If given ID is <code>null</code>.
	 * @return Set of matching entries. Empty Set if no matching entries are
	 *         available.
	 */
	final Set<Entry> getEntries(final ID id) {

		if (id == null) {
			final NullPointerException e = new NullPointerException(
					"ID to find entries for may not be null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}
		synchronized (entries) {
			/*
			 * This has to be synchronized as the test if the map contains a set
			 * associated with id can succeed and then the thread may hand
			 * control over to another thread that removes the Set belonging to
			 * id. In that case this.entries.get(id) would return null which
			 * would break the contract of this method.
			 */
			if (entries.containsKey(id)) {
				final Set<Entry> entriesForID = entries.get(id);
				/*
				 * Return a copy of the set to avoid modification of Set stored
				 * in this.entries from outside this class. (Avoids also
				 * modifications concurrent to iteration over the Set by a
				 * client of this class.
				 */
				// if (Entries.debugEnabled) {
				Entries.logger.info("Returning entries " + entriesForID);
				// }
				return new HashSet<Entry>(entriesForID);
			}
		}
		// if (Entries.debugEnabled) {
		Entries.logger.info("No entries available for " + id
				+ ". Returning empty set.");
		// }
		return new HashSet<Entry>();
	}

	/**
	 * Returns all entries in interval, excluding lower bound, but including
	 * upper bound
	 * 
	 * @param fromID
	 *            Lower bound of IDs; entries matching this ID are NOT included
	 *            in result.
	 * @param toID
	 *            Upper bound of IDs; entries matching this ID ARE included in
	 *            result.
	 * @throws NullPointerException
	 *             If either or both of the given ID references have value
	 *             <code>null</code>.
	 * @return Set of matching entries.
	 */
	final Set<Entry> getEntriesInInterval(final ID fromID, final ID toID) {

		if (fromID == null || toID == null) {
			final NullPointerException e = new NullPointerException(
					"Neither of the given IDs may have value null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}

		final Set<Entry> result = new HashSet<Entry>();

		synchronized (entries) {
			for (final ID nextID : entries.keySet()) {
				if (nextID.isInInterval(fromID, toID)) {
					final Set<Entry> entriesForID = entries.get(nextID);
					for (final Entry entryToAdd : entriesForID) {
						result.add(entryToAdd);
					}
				}
			}
		}

		// add entries matching upper bound
		result.addAll(this.getEntries(toID));

		return result;
	}

	/**
	 * Returns the number of stored entries.
	 * 
	 * @return Number of stored entries.
	 */
	final int getNumberOfStoredEntries() {
		return entries.size();
	}

	/**
	 * Removes the given entry from the local hash table.
	 * 
	 * @param entryToRemove
	 *            Entry to remove from the hash table.
	 * @throws NullPointerException
	 *             If entry to remove is <code>null</code>.
	 */
	final void remove(final Entry entryToRemove) {

		if (entryToRemove == null) {
			final NullPointerException e = new NullPointerException(
					"Entry to remove may not be null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}

		synchronized (entries) {
			if (entries.containsKey(entryToRemove.getId())) {
				final Set<Entry> values = entries.get(entryToRemove.getId());
				values.remove(entryToRemove);
				if (values.size() == 0) {
					entries.remove(entryToRemove.getId());
				}
			}
		}
		// if (Entries.debugEnabled) {
		Entries.logger.info("Entry was removed: " + entryToRemove);
		// }
	}

	/**
	 * Removes the given entries from the local hash table.
	 * 
	 * @param toRemove
	 *            Set of entries to remove from local hash table.
	 * @throws NullPointerException
	 *             If the given set of entries is <code>null</code>.
	 */
	final void removeAll(final Set<Entry> toRemove) {

		if (toRemove == null) {
			final NullPointerException e = new NullPointerException(
					"Set of entries may not have value null!");
			Entries.logger.severe("Null pointer");
			throw e;
		}

		for (final Entry nextEntry : toRemove) {
			remove(nextEntry);
		}

		// if (Entries.debugEnabled) {
		Entries.logger.info("Set of entries of length " + toRemove.size()
				+ " was removed.");
		// }
	}

	/**
	 * Returns a formatted string of all entries stored in the local hash table.
	 * 
	 * @return String representation of all stored entries.
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("Entries:\n");
		for (final Map.Entry<ID, Set<Entry>> entry : entries.entrySet()) {
			result.append("  key = " + entry.getKey().toString() + ", value = "
					+ entry.getValue() + "\n");
		}
		return result.toString();
	}
}
