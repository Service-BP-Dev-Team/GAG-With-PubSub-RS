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
// $Id: ConnectToAll.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.componentsmanager;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public class ConnectToAll {

	private final String typeSource;
	private final String typeDestination;
	private final String idSource;

	public ConnectToAll(final String typeSource, final String typeDestination,
			final String idSource) {
		this.typeSource = typeSource;
		this.typeDestination = typeDestination;
		this.idSource = idSource;
	}

	public String getTypeSource() {
		return typeSource;
	}

	public String getTypeDestination() {
		return typeDestination;
	}

	public String getIdSource() {
		return idSource;
	}

	@Override
	public String toString() {
		return "ConnectToAll(" + typeSource + ", " + typeDestination + ", "
				+ idSource + ")";
	}
}
