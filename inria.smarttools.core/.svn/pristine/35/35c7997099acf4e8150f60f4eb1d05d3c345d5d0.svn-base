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
// $Id: Input.java 2738 2009-02-24 13:36:10Z bboussema $
package inria.smarttools.core.component;

import java.util.Map;

/**
 * Input.java Created: Tue Mar 05 14:04:56 2002
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2738 $
 */
public class Input extends ServiceImpl implements Service {

	protected Binding binding;
	protected Map<String, Attribute> attributes;

	public Input(final String name, final Map<String, Attribute> attributes,
			final String methodName, final String doc) {
		super(name);
		this.attributes = attributes;
		this.methodName = methodName;
		documentation = doc;
	}

	public Binding getBinding() {
		return binding;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setBinding(final Binding v) {
		binding = v;
	}

	public void setAttributes(final Map<String, Attribute> v) {
		attributes = v;
	}

}
