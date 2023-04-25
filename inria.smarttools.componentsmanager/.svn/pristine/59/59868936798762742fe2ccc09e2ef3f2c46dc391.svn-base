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
// $Id: LocalDS.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.smarttools.componentsmanager;

import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.ContainerService;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.PropertyMap;

import java.util.Map;

/**
 * Interface of the local components module (LocaDSImpl), used by the CM.
 * 
 * @author Baptiste.Boussemart@inria.fr
 * @version $Revision: 2875 $
 */
public interface LocalDS {

	public void addComponent(String jarName, String componentName,
			String uriToJar);

	public void addConnectToAll(ConnectToAll connectToAll);

	public void connectToOne(String idSource, String typeSource,
			String idDestination, PropertyMap actions);

	public void getComponent(String type, String id, Long responseId);

	public Map<String, ContainerService> getComponentsLoaded();

	public Map<String, Container> getComponentsStarted();

	public Container getContainerProxy(String id);

	public void getFirstOfType(String type, Long responseId);

	public Container internalStartComponent(String instance_id, String type);

	public boolean isLocal(String idSource);

	public void newComponentInstance(Container c, String c_name,
			String file_name);

	public Message requestFile(String requestedFilename);

	public void setComponentsManager(ComponentsManager cm);

	public void sleepComponent(String instance_id);

	public Container startComponent(String componentId, String componentType,
			PropertyMap actions, boolean allInit);

	public void stopComponent(String instance_id);

	public void wakeupComponent(String instance_id);
}
