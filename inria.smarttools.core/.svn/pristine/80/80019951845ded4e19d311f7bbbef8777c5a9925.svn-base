/*******************************************************************************
 * Copyright (c) 2000, 2007 INRIA.
 * 
 *    This file is part of SmartTools project
 *    <a href="http://www-sop.inria.fr/smartool/">SmartTools</a>
 * 
 *     SmartTools is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     SmartTools is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SmartTools; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: OrdHashMap.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.core.util;

import inria.smarttools.core.Activator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a HashMap that guaranties to keep objects in the same order you added
 * them to the table. Warning : this implementation does not overload all the
 * HashMap methods properly to keep the order. It only provides ordered
 * Enumerations for keys and values.
 */

public class OrdHashMap<K, V> extends HashMap<K, V> {

	protected class EnumOHElns<V2> implements Enumeration<V2> {

		private int keyindex;
		private final OrdHashMap<K, V2> oh;

		public EnumOHElns(final OrdHashMap<K, V2> oh) {
			this.oh = oh;
			keyindex = 0;
		}

		public boolean hasMoreElements() {
			return (keyindex < oh.size());
		}

		public V2 nextElement() {
			return oh.get(oh.getKeyAt(keyindex++));
		}
	}

	protected class EnumOHKeys<K2> implements Enumeration<K2> {

		private int keyindex;
		private final OrdHashMap<K2, V> oh;

		public EnumOHKeys(final OrdHashMap<K2, V> oh) {
			this.oh = oh;
			keyindex = 0;
		}

		public boolean hasMoreElements() {
			return (keyindex < oh.size());
		}

		public K2 nextElement() {
			return oh.getKeyAt(keyindex++);
		}
	}

	private static final long serialVersionUID = 1L;

	protected List<K> ord = new ArrayList<K>();

	public OrdHashMap() {
		super();
	}

	public OrdHashMap(final int initialCapacity) {
		super(initialCapacity);
	}

	public OrdHashMap(final int initialCapacity, final float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public OrdHashMap(final Map<? extends K, ? extends V> t) {
		super(t);
	}

	/**
	 * Removes all mappings from this map.
	 */
	@Override
	public void clear() {
		ord.clear();
		super.clear();
	}

	public OrdHashMap<K, V> deepClone() {
		final OrdHashMap<K, V> n = new OrdHashMap<K, V>();
		final Enumeration<K> enumeration = keys();
		while (enumeration.hasMoreElements()) {
			final K name = enumeration.nextElement();
			n.put(name, get(name));
		}
		return n;
	}

	/**
	 * Returns an ordered enumeration of the values contained in this OrdHashMap
	 * 
	 * @return An Enumeration of values
	 */
	public Enumeration<V> elements() {
		return new EnumOHElns<V>(this);
	}

	/**
	 * Returns the key located at the specified index.
	 * 
	 * @return A key
	 */
	public K getKeyAt(final int index) {
		return ord.get(index);
	}

	/**
	 * Returns the rank index of the key given in argument. Returns -1 if the
	 * key is not found.
	 */
	public int indexOf(final Object key) {
		return ord.indexOf(key);
	}

	/**
	 * Returns an ordered enumeration of the keys contained in this OrdHashMap
	 * 
	 * @return An Enumeration of keys
	 */
	public Enumeration<K> keys() {
		return new EnumOHKeys<K>(this);
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for this key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 */
	@Override
	public V put(final K key, final V value) {
		if (!containsKey(key)) {
			ord.add(key);
		}
		return super.put(key, value);
	}

	/**
	 * Removes the entry matching the argument key.
	 * 
	 * @return previous value associated with specified key, or null if there
	 *         was no mapping for key. A null return can also indicate that the
	 *         map previously associated null with the specified key.
	 */
	@Override
	public V remove(final Object key) {
		ord.remove(ord.indexOf(key));
		return super.remove(key);
	}

	public void trace() {
		final Enumeration<K> enumeration = keys();
		while (enumeration.hasMoreElements()) {
			final String name = (String) enumeration.nextElement();
			Activator.LOGGER.info("Trace -- OrdHashMap[" + name + "]="
					+ get(name));
		}
	}
}
