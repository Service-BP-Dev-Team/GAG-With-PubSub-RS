/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
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
// $Id: PropertyMap.java 2827 2009-04-21 09:08:51Z bboussema $
package inria.smarttools.core.component;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2827 $
 */
public class PropertyMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, Boolean> booleans = new HashMap<String, Boolean>();

	private final Map<String, Integer> integers = new HashMap<String, Integer>();

	private final Map<String, Long> longs = new HashMap<String, Long>();

	private final Map<String, Float> floats = new HashMap<String, Float>();

	private final Map<String, Double> doubles = new HashMap<String, Double>();

	private final Map<String, String> strings = new HashMap<String, String>();

	private final Map<String, PropertyMap> properties = new HashMap<String, PropertyMap>();

	private final Map<String, MessageImpl> messages = new HashMap<String, MessageImpl>();

	private transient final Map<String, Object> objects = new HashMap<String, Object>();

	public Set<Entry<String, Boolean>> booleansEntrySet() {
		return booleans.entrySet();
	}

	public Set<String> booleansKeySet() {
		return booleans.keySet();
	}

	public void clear() {
		objects.clear();
		integers.clear();
		booleans.clear();
		messages.clear();
		longs.clear();
		floats.clear();
		doubles.clear();
		properties.clear();
		strings.clear();
	}

	private void completeObjects() {
		if (objects.size() < messages.size() + booleans.size() + doubles.size()
				+ floats.size() + integers.size() + longs.size()
				+ properties.size() + strings.size()) {
			objects.putAll(messages);
			objects.putAll(booleans);
			objects.putAll(doubles);
			objects.putAll(floats);
			objects.putAll(integers);
			objects.putAll(longs);
			objects.putAll(properties);
			objects.putAll(strings);
		}
	}

	public boolean containsKey(final Object key) {
		completeObjects();
		return objects.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		completeObjects();
		return containsValue(value);
	}

	public Set<Entry<String, Double>> doublesEntrySet() {
		return doubles.entrySet();
	}

	public Set<String> doublesKeySet() {
		return doubles.keySet();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		completeObjects();
		return objects.entrySet();
	}

	public Set<Entry<String, Float>> floatsEntrySet() {
		return floats.entrySet();
	}

	public Set<String> floatssKeySet() {
		return floats.keySet();
	}

	public Object get(final Object key) {
		completeObjects();
		return objects.get(key);
	}

	public Object get(final String key) {
		Object value = objects.get(key);
		if (value == null) {
			value = booleans.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = integers.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = strings.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = longs.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = floats.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = doubles.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = properties.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
			value = messages.get(key);
			if (value != null) {
				objects.put(key, value);
				return value;
			}
		}
		return value;
	}

	public Message getAfterConnect(final String key) {
		return messages.get(key);
	}

	public Boolean getBoolean(final String key) {
		return booleans.get(key);
	}

	public Double getDouble(final String key) {
		return doubles.get(key);
	}

	public Float getFloat(final String key) {
		return floats.get(key);
	}

	public Integer getInteger(final String key) {
		return integers.get(key);
	}

	public Long getLong(final String key) {
		return longs.get(key);
	}

	public Map<String, String> getMap() {
		return strings;
	}

	public PropertyMap getPropertyMap(final String key) {
		return properties.get(key);
	}

	public String getString(final String key) {
		return strings.get(key);
	}

	public Set<Entry<String, Integer>> integersEntrySet() {
		return integers.entrySet();
	}

	public Set<String> integersKeySet() {
		return integers.keySet();
	}

	public boolean isEmpty() {
		completeObjects();
		return objects.isEmpty();
	}

	public Set<String> keySet() {
		completeObjects();
		return objects.keySet();
	}

	public Set<Entry<String, Long>> longsEntrySet() {
		return longs.entrySet();
	}

	public Set<String> longsKeySet() {
		return longs.keySet();
	}

	public Set<Entry<String, MessageImpl>> messagesEntrySet() {
		return messages.entrySet();
	}

	public Set<String> messagesKeySet() {
		return messages.keySet();
	}

	public Set<Entry<String, PropertyMap>> propertiesEntrySet() {
		return properties.entrySet();
	}

	public Set<String> propertiesKeySet() {
		return properties.keySet();
	}

	public Object put(final String key, final Object value) {
		if (!objects.containsKey(key)) {
			if (value instanceof Boolean) {
				booleans.put(key, (Boolean) value);
			} else if (value instanceof Long) {
				longs.put(key, (Long) value);
			} else if (value instanceof Double) {
				doubles.put(key, (Double) value);
			} else if (value instanceof Float) {
				floats.put(key, (Float) value);
			} else if (value instanceof Integer) {
				integers.put(key, (Integer) value);
			} else if (value instanceof String) {
				strings.put(key, (String) value);
			} else if (value instanceof PropertyMap) {
				properties.put(key, (PropertyMap) value);
			} else if (value instanceof MessageImpl) {
				messages.put(key, (MessageImpl) value);
			}
			return objects.put(key, value);
		}
		return null;
	}

	public void putAll(final Map<? extends String, ? extends Object> t) {
		for (final String key : t.keySet()) {
			put(key, t.get(key));
		}
	}

	public Object remove(final Object key) {
		messages.remove(key);
		booleans.remove(key);
		doubles.remove(key);
		floats.remove(key);
		integers.remove(key);
		longs.remove(key);
		properties.remove(key);
		strings.remove(key);
		return objects.remove(key);
	}

	public int size() {
		completeObjects();
		return objects.size();
	}

	public Set<Entry<String, String>> stringsEntrySet() {
		return strings.entrySet();
	}

	public Set<String> stringsKeySet() {
		return strings.keySet();
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder()
				.append("PropertyMap={");
		for (final String key : objects.keySet()) {
			buffer.append(key).append(" = \"").append(objects.get(key)).append(
					"\", ");
		}
		buffer.append("}");
		return buffer.toString();
	}

	public Collection<Object> values() {
		return objects.values();
	}
}
