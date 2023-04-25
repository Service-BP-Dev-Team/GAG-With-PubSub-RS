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
// $Id: RemoteDS.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.smarttools.componentsmanager;

import inria.smarttools.core.component.PropertyMap;

/**
 * Interface that every remote components module need to implement and be
 * published as an OSGi service of this class.
 * 
 * @author Baptiste.Boussemart@inria.fr
 * @version $Revision: 2875 $
 */
public interface RemoteDS {

	public void addConnectToAll(ConnectToAll connectToAll);

	public void close();

	public void connectToOne(String idSource, String idDestination,
			String typeDestination, String dc, PropertyMap actions);

	public void getComponent(String type, String id, Long responseId);

	public void getFirstOfType(String type, Long responseId);

	public void setComponentsManager(ComponentsManager cm);

}
