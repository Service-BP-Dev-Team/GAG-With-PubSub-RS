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
// $Id: Attribute.java 2738 2009-02-24 13:36:10Z bboussema $
package inria.smarttools.core.component;

/**
 * Attribut.java This class decribe Attribut for Component. An attribut is
 * composed by : - a name - a (java) type - a short description - a value - can
 * be mandatory / optional - exclusif with other atribut - constraining -
 * serializable Created: Tue Jan 29 10:23:11 2002
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2738 $
 */
public class Attribute {

	protected String name = null;

	protected String type = null;

	protected String ns = null;

	protected String description = null;

	protected String value = null;

	public Attribute() {
	//
	}

	public Attribute(final String name, final String ns, final String type,
			final String doc) {
		this.name = name;
		this.ns = ns;
		this.type = type;
		description = doc;
	}

	public Attribute(final String n, final String ns, final String t,
			final String d, final String value) {
		this(n, ns, t, d);
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getNs() {
		return ns;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setDescription(final String v) {
		description = v;
	}

	public void setName(final String v) {
		name = v;
	}

	public void setNs(final String v) {
		ns = v;
	}

	public void setType(final String v) {
		type = v;
	}

	public void setValue(final String v) {
		value = v;
	}

	@Override
	public String toString() {
		return "Attribut <" + name + "> of type <" + type + "> : "
				+ description;
	}

}
