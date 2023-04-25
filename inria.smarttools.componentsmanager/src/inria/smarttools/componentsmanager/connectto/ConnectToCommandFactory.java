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
// $Id: ConnectToCommandFactory.java 2458 2008-10-06 06:42:03Z bboussema $
package inria.smarttools.componentsmanager.connectto;

import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.core.component.PropertyMap;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2458 $
 */
public class ConnectToCommandFactory {

	private static long id = 0;

	public static ConnectToCommand getConnectToCommand(
			final ComponentsManager cm, final String idSource,
			final String typeSource, final String idDestination,
			final String typeDestination, final String dc, final String tc,
			final String sc, final PropertyMap actions) {
		return new DefaultConnectToCommand(cm, idSource, typeSource,
				idDestination, typeDestination, dc, tc, sc, actions,
				ConnectToCommandFactory.id++);
	}
}
