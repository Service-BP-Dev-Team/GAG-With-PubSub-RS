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
// $Id: ConnectToFirstCommand.java 2458 2008-10-06 06:42:03Z bboussema $
package inria.smarttools.componentsmanager.connectto;

import inria.smarttools.componentsmanager.Activator;
import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.componentsmanager.LocalDS;
import inria.smarttools.componentsmanager.RemoteDS;
import inria.smarttools.core.component.PropertyMap;

import java.util.List;
import java.util.Vector;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2458 $
 */
public class ConnectToFirstCommand extends ConnectToCommand {

	private class TupleLocalDS {

		String componentName;
		LocalDS localDS;

		public TupleLocalDS(final LocalDS localDS, final String componentName) {
			this.localDS = localDS;
			this.componentName = componentName;
		}
	}

	private class TupleRemoteDS {

		String componentName;
		RemoteDS remoteDS;

		public TupleRemoteDS(final RemoteDS remoteDS, final String componentName) {
			this.remoteDS = remoteDS;
			this.componentName = componentName;
		}
	}

	private TupleLocalDS possibleLocalDS = null;

	private final List<TupleRemoteDS> possibleRemoteDS = new Vector<TupleRemoteDS>();

	protected int responseCount;

	public ConnectToFirstCommand(final ComponentsManager cm,
			final String idSource, final String typeSource,
			final String idDestination, final String typeDestination,
			final String dc, final String tc, final String sc,
			final PropertyMap actions, final long id) {
		super(cm, idSource, typeSource, idDestination, typeDestination, dc, tc,
				sc, actions, id);
	}

	@Override
	public void execute() {
		Activator.LOGGER.info(toString() + " connectToFirstCommand.execute("
				+ typeSource + ", " + idSource + ", " + typeDestination + ", "
				+ idDestination + ", " + actions + ")");
		if (responseCount == 0) {
			if (possibleLocalDS != null) {
				possibleLocalDS.localDS.connectToOne(idSource, typeDestination,
						possibleLocalDS.componentName, actions);
				cm.removeConnectToCommand(id);
				return;
			}
			if (possibleRemoteDS.size() > 0) {
				possibleRemoteDS.get(0).remoteDS.connectToOne(idSource,
						possibleRemoteDS.get(0).componentName, typeDestination,
						dc, actions);
				cm.removeConnectToCommand(id);
				return;
			}
			responseCount++;
		}
	}

	@Override
	public void giveResponse(final String componentName, final LocalDS localDS,
			final RemoteDS remoteDS) {
		Activator.LOGGER.info(toString()
				+ " connectToFirstCommand.giveResponse(" + componentName + ", "
				+ localDS + ", " + remoteDS + ")");
		if (localDS != null && componentName != null) {
			possibleLocalDS = new TupleLocalDS(localDS, componentName);
		}
		if (remoteDS != null && componentName != null) {
			possibleRemoteDS.add(new TupleRemoteDS(remoteDS, componentName));
		}
		responseCount--;
		execute();
	}

	@Override
	public void close() {
		possibleLocalDS = null;
		possibleRemoteDS.clear();
	}

	@Override
	public String toString() {
		return "connectToFirstCmd";
	}
}
