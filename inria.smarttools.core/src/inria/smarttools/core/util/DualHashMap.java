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
// $Id: DualHashMap.java 2450 2008-08-29 08:44:53Z bboussema $
package inria.smarttools.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Describe class <code>DualHashMap</code> here.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2450 $
 */
public class DualHashMap {

	//
	//
	// Fields
	//
	//

	/**
	 * Describe variable <code>originalToMapped</code> here.
	 */
	protected Map<Object, Object> originalToMapped = new HashMap<Object, Object>();

	/**
	 * Describe variable <code>mappedToOriginal</code> here.
	 */
	protected Map<Object, Object> mappedToOriginal = new HashMap<Object, Object>();

	//
	//
	// Constructor
	//
	//

	/**
	 * Creates a new <code>DualHashMap</code> instance.
	 */
	public DualHashMap() {}

	//
	//
	// put / get
	//
	//

	/**
	 * Describe <code>put</code> method here.
	 * 
	 * @param original
	 *            an <code>Object</code> value
	 * @param mapped
	 *            an <code>Object</code> value
	 */
	public void put(final Object original, final Object mapped) {
		originalToMapped.put(original, mapped);
		mappedToOriginal.put(mapped, original);
	}

	/**
	 * Describe <code>getFromMapped</code> method here.
	 * 
	 * @param keyForOriginal
	 *            an <code>Object</code> value
	 * @return an <code>Object</code> value
	 */
	public Object getFromMapped(final Object keyForOriginal) {
		return originalToMapped.get(keyForOriginal);
	}

	/**
	 * Describe <code>getFromOriginal</code> method here.
	 * 
	 * @param keyForMapped
	 *            an <code>Object</code> value
	 * @return an <code>Object</code> value
	 */
	public Object getFromOriginal(final Object keyForMapped) {
		return mappedToOriginal.get(keyForMapped);
	}

	//
	//
	// I/O
	//
	//

	/**
	 * Return a short description of the DualHashMap object.
	 * 
	 * @return a value of the type 'String' : a string representation of
	 *         thisDualHashMap
	 */
	@Override
	public String toString() {
		return "DualHashMap : ";
	}

}
