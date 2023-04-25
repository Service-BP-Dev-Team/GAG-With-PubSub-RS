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
// $Id: AutoKeyHashMap.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.util;

import java.util.HashMap;

/**
 * AutoKeyHashMap.java A HashMap where key for each data value is a part of data
 * (method call) for example : Data data = new Data(); HashMap hashmap = new
 * HashMap(); hashmap.put(data.getName(), data); Can be re-write like this :
 * AutoKeyHashMap hm = new AutoKeyHashMap(new KeyAccessor() { public Object
 * getKey(Object o) { return ((Data) o).getName(); } }); hm.put(data);
 * 
 * @see inria.smarttools.core.util.KeyAccessor Created: Mon May 13 14:08:03 2002
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public class AutoKeyHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	protected KeyAccessor<K, V> access = null;

	/**
	 * Creates a new <code>AutoKeyHashMap</code> instance.
	 * 
	 * @param access
	 *            a <code>KeyAccessor</code> value
	 */
	public AutoKeyHashMap(final KeyAccessor<K, V> access) {
		super();
		this.access = access;
	}

	/**
	 * Put an object in the hashmap, key value is compute using <access> object.
	 * 
	 * @param o
	 *            an <code>Object</code> value
	 */
	public void put(final V o) {
		put(access.getKey(o), o);
	}

}// AutoKeyHashMap
