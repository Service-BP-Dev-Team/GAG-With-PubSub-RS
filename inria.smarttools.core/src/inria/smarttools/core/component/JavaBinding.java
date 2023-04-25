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
// $Id: JavaBinding.java 2738 2009-02-24 13:36:10Z bboussema $
package inria.smarttools.core.component;

import java.util.Iterator;
import java.util.Map;

/**
 * Describe a jaba binding.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2738 $
 */
public class JavaBinding implements Binding {

	protected String method = null;

	protected Map<String, Attribute> attributes = null;

	public JavaBinding(final String toMethod,
			final Map<String, Attribute> attributes) {
		method = toMethod;
		this.attributes = attributes;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public String getMethod() {
		return method;
	}

	public void setAttributes(final Map<String, Attribute> v) {
		attributes = v;
	}

	public void setMethod(final String v) {
		method = v;
	}

	public String toJava() {
		String res = "";
		res += method + "(";
		for (final Iterator<Attribute> it = attributes.values().iterator(); it
				.hasNext();) {
			final Attribute attr = it.next();
			res += "(" + attr.getType() + ") ";
			if (attr.getValue() != null && !attr.getValue().equals("")) { // STRING
				// !
				res += "\"" + attr.getValue() + "\"";
			} else {
				res += attr.getName();
			}
			if (it.hasNext()) {
				res += ", ";
			}
		}
		res += ");";
		return res;
	}

}
