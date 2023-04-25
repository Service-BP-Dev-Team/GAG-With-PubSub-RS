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
// $Id: ConnectToCommand.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.componentsmanager.connectto;

import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.componentsmanager.LocalDS;
import inria.smarttools.componentsmanager.RemoteDS;
import inria.smarttools.core.component.PropertyMap;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public abstract class ConnectToCommand {

	protected PropertyMap actions;
	protected ComponentsManager cm;
	protected String dc;
	protected long id;
	protected String idDestination;
	protected String idSource;
	protected String sc;
	protected String tc;
	protected String typeDestination;
	protected String typeSource;

	public ConnectToCommand(final ComponentsManager cm, final String idSource,
			final String typeSource, final String idDestination,
			final String typeDestination, final String dc, final String tc,
			final String sc, final PropertyMap actions, final long id) {
		this.cm = cm;
		this.idSource = idSource;
		this.typeSource = typeSource;
		this.idDestination = idDestination;
		this.typeDestination = typeDestination;
		this.dc = dc;
		this.tc = tc;
		this.sc = sc;
		this.actions = actions;
		this.id = id;
	}

	abstract public void execute();

	public Long getId() {
		return id;
	}

	abstract public void giveResponse(String componentName, LocalDS localDS,
			RemoteDS remoteDS);

	@Override
	public String toString() {
		return super.toString() + " [" + id + "]";
	}

	abstract public void close();
}
