/***************************************************************************
 *                                                                         *
 *                                 ID.java                                 *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
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
package de.uniba.wiai.lspi.chord.data;

import java.io.Serializable;

/**
 * Identifier for nodes and user-defined objects. New instances of this class
 * are created either when a node joins the network, or by the local node
 * inserting a user-defined object. Once created, an ID instance is
 * unmodifiable. IDs of same length can be compared as this class implements
 * java.lang.Comparable. IDs of different length cannot be compared.
 * 
 * @author Sven Kaffille, Karsten Loesing
 * @version 1.0.5
 */
public final class ID implements Comparable<ID>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6860626236168125168L;

	private static final int HEX = 2;

	// private static final int DEC = 1;

	// private static final int BIN = 0;

	/**
	 * The representation of an id returned as String when {@link #toString()}
	 * is invoked. Is intialized with help of property
	 * <code>de.uniba.wiai.lspi.chord.data.ID.displayed.representation</code>.
	 * Possible values: <br/>
	 * <br/>
	 * <code>0 = BIN</code>, binary<br/>
	 * <code>1 = DEC</code>, decimal<br/>
	 * <code>2 = HEX</code>, hexadecimal<br/>
	 */
	private static int displayedRepresentation = ID.HEX;

	// static initializer for displayedRepresentation
	static {
		final String property = System.getProperty(ID.class.getName()
				+ ".displayed.representation");

		if (property != null && property.length() > 0) {
			ID.displayedRepresentation = Integer.parseInt(property);
		}
	}

	/**
	 * The number of (highest) bytes of an id returned as String when
	 * {@link #toString()} is invoked. Is intialized with help of property
	 * <code>de.uniba.wiai.lspi.chord.data.ID.number.of.displayed.bytes</code>.
	 */
	private static int numberOfDisplayedBytes = Integer.MAX_VALUE;

	// static initializer for numberOfDisplayedBytes
	static {
		final String numberProperty = System.getProperty(ID.class.getName()
				+ ".number.of.displayed.bytes");

		if (numberProperty != null && numberProperty.length() > 0) {
			ID.numberOfDisplayedBytes = Integer.parseInt(numberProperty);
		}
	}

	/**
	 * The bytes representing the id.
	 */
	private final byte[] id;

	/**
	 * Creates a new ID consisting of the given byte[] array. The ID is assumed
	 * to have (ID.length * 8) bits. It must have leading zeros if its value has
	 * fewer digits than its maximum length.
	 * 
	 * @param id1
	 *            Byte array containing the ID.
	 */
	public ID(final byte[] id1) {
		id = new byte[id1.length];
		System.arraycopy(id1, 0, id, 0, id1.length);
	}

	/**
	 * Representation of this as a String.
	 */
	private transient String stringRepresentation = null;

	/**
	 * Returns a string of the decimal representation of this ID, including
	 * leading zeros.
	 * 
	 * @return Decimal string of ID
	 */
	@Override
	public final String toString() {
		if (stringRepresentation == null) {
			final int rep = ID.displayedRepresentation;
			switch (rep) {
				case 0:
					stringRepresentation = this
							.toBinaryString(ID.numberOfDisplayedBytes);
					break;
				case 1:
					stringRepresentation = this
							.toDecimalString(ID.numberOfDisplayedBytes);
					break;
				default:
					stringRepresentation = this
							.toHexString(ID.numberOfDisplayedBytes);
			}

		}
		return stringRepresentation;
	}

	/**
	 * Returns a string of the hexadecimal representation of the first
	 * <code>n</code> bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * @return Hex string of ID
	 */
	public final String toHexString(final int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
		final int displayBytes = Math
				.max(1, Math.min(numberOfBytes, id.length));

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			String block = Integer.toHexString(id[i] & 0xff).toUpperCase();

			// add leading zero to block, if necessary
			if (block.length() < 2) {
				block = "0" + block;
			}

			result.append(block + " ");
		}
		return result.toString();
	}

	/**
	 * Returns a string of the hexadecimal representation of this ID, including
	 * leading zeros.
	 * 
	 * @return Hex string of ID
	 */
	public final String toHexString() {
		return this.toHexString(id.length);
	}

	/**
	 * Returns a string of the decimal representation of the first
	 * <code>n</code> bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * @return Hex string of ID
	 */
	public final String toDecimalString(final int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
		final int displayBytes = Math
				.max(1, Math.min(numberOfBytes, id.length));

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			final String block = Integer.toString(id[i] & 0xff);

			result.append(block + " ");
		}
		return result.toString();
	}

	/**
	 * Returns a string of the decimal representation of this ID, including
	 * leading zeros.
	 * 
	 * @return Decimal string of ID
	 */
	public final String toDecimalString() {
		return this.toDecimalString(id.length);
	}

	/**
	 * Returns a string of the binary representation of the first <code>n</code>
	 * bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * @return Hex string of ID
	 */
	public final String toBinaryString(final int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
		final int displayBytes = Math
				.max(1, Math.min(numberOfBytes, id.length));

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			String block = Integer.toBinaryString(id[i] & 0xff);

			// add leading zero to block, if necessary
			while (block.length() < 8) {
				block = "0" + block;
			}

			result.append(block + " ");
		}
		return result.toString();
	}

	/**
	 * Returns a string of the binary representation of this ID, including
	 * leading zeros.
	 * 
	 * @return Binary string of ID
	 */
	public final String toBinaryString() {
		return this.toBinaryString(id.length);
	}

	/**
	 * Returns length of this ID measured in bits. ID length is determined by
	 * the length of the stored byte[] array, i.e. leading zeros have to be
	 * stored in the array.
	 * 
	 * @return Length of this ID measured in bits.
	 */
	public final int getLength() {
		return id.length * 8;
	}

	/**
	 * Calculates the ID which is 2^powerOfTwo bits greater than the current ID
	 * modulo the maximum ID and returns it.
	 * 
	 * @param powerOfTwo
	 *            Power of two which is added to the current ID. Must be a value
	 *            of the interval [0, length-1], including both extremes.
	 * @return ID which is 2^powerOfTwo bits greater than the current ID modulo
	 *         the maximum ID.
	 */
	public final ID addPowerOfTwo(final int powerOfTwo) {

		if (powerOfTwo < 0 || powerOfTwo >= (id.length * 8)) {
			throw new IllegalArgumentException(
					"The power of two is out of range! It must be in the interval "
							+ "[0, length-1]");
		}

		// copy ID
		final byte[] copy = new byte[id.length];
		System.arraycopy(id, 0, copy, 0, id.length);

		// determine index of byte and the value to be added
		int indexOfByte = id.length - 1 - (powerOfTwo / 8);
		final byte[] toAdd = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToAdd = toAdd[powerOfTwo % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] += valueToAdd;

			// reset value to 1 for possible overflow situation
			valueToAdd = 1;
		}
		// check for overflow - occurs if old value had a leading one, i.e. it
		// was negative, and new value has a leading zero, i.e. it is zero or
		// positive; indexOfByte >= 0 prevents running out of the array to the
		// left in case of going over the maximum of the ID space
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ID(copy);
	}

	/**
	 * Checks the given object for equality with this {@link ID}.
	 * 
	 * @param equalsTo
	 *            Object to check equality with this {@link ID}.
	 */
	@Override
	public final boolean equals(final Object equalsTo) {

		// check if given object has correct type
		if (equalsTo == null || !(equalsTo instanceof ID)) {
			return false;
		}

		// check if both byte arrays are equal by using the compareTo method
		return (compareTo((ID) equalsTo) == 0);

	}

	/**
	 * Compare current ID with the given object. If either the object is not a
	 * ID or both IDs' lengths do not match, a ClassCastException is thrown.
	 * Otherwise both IDs are compared byte by byte.
	 * 
	 * @return -1, 0, or 1, if this ID is smaller, same size, or greater than
	 *         the given object, respectively.
	 */
	public final int compareTo(final ID otherKey) throws ClassCastException {

		if (getLength() != otherKey.getLength()) {
			throw new ClassCastException(
					"Only ID objects with same length can be "
							+ "compared! This ID is " + id.length
							+ " bits long while the other ID is "
							+ otherKey.getLength() + " bits long.");
		}

		// compare value byte by byte
		final byte[] otherBytes = new byte[id.length];
		System.arraycopy(otherKey.id, 0, otherBytes, 0, id.length);

		for (int i = 0; i < id.length; i++) {
			if ((byte) (id[i] - 128) < (byte) (otherBytes[i] - 128)) {
				return -1; // this ID is smaller
			} else if ((byte) (id[i] - 128) > (byte) (otherBytes[i] - 128)) {
				return 1; // this ID is greater
			}
		}
		return 0;

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		int result = 19;
		for (int i = 0; i < id.length; i++) {
			result = 13 * result + id[i];
		}
		return result;
	}

	/**
	 * Checks if this ID is in the interval determined by the two given IDs.
	 * Neither of the boundary IDs is included in the interval. If both IDs
	 * match, the interval is assumed to span the whole ID ring.
	 * 
	 * @param fromID
	 *            Lower bound of interval.
	 * @param toID
	 *            Upper bound of interval.
	 * @return If this key is included in the given interval.
	 */
	public final boolean isInInterval(final ID fromID, final ID toID) {

		// both interval bounds are equal -> calculate out of equals
		if (fromID.equals(toID)) {
			// every ID is contained in the interval except of the two bounds
			return (!equals(fromID));
		}

		// interval does not cross zero -> compare with both bounds
		if (fromID.compareTo(toID) < 0) {
			return (compareTo(fromID) > 0 && compareTo(toID) < 0);
		}

		// interval crosses zero -> split interval at zero
		// calculate min and max IDs
		final byte[] minIDBytes = new byte[id.length];
		final ID minID = new ID(minIDBytes);
		final byte[] maxIDBytes = new byte[id.length];
		for (int i = 0; i < maxIDBytes.length; i++) {
			maxIDBytes[i] = -1;
		}
		final ID maxID = new ID(maxIDBytes);
		// check both splitted intervals
		// first interval: (fromID, maxID]
		return ((!fromID.equals(maxID) && compareTo(fromID) > 0 && compareTo(maxID) <= 0) ||
		// second interval: [minID, toID)
		(!minID.equals(toID) && compareTo(minID) >= 0 && compareTo(toID) < 0));
	}

}
