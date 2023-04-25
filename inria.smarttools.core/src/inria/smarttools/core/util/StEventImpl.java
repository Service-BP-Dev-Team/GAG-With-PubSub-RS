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
// $Id: StEventImpl.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.util;

import inria.smarttools.core.component.PropertyMap;

/**
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public abstract class StEventImpl implements StEvent {

	protected PropertyMap attributes = new PropertyMap();

	private String adressee;

	public StEventImpl() {
	//
	}

	public StEventImpl(final String adressee) {
		this.adressee = adressee;
	}

	public PropertyMap getAttributes() {
		return attributes;
	}

	public void setAttributes(final PropertyMap v) {
		attributes = v;
	}

	public String getAdressee() {
		return adressee;
	}

}
