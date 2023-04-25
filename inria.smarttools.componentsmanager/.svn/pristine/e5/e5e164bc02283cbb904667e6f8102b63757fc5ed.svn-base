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
// $Id: DefaultConnectToCommand.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.componentsmanager.connectto;

import inria.smarttools.componentsmanager.Activator;
import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.componentsmanager.ConnectToAll;
import inria.smarttools.componentsmanager.LocalDS;
import inria.smarttools.componentsmanager.RemoteDS;
import inria.smarttools.core.component.PropertyMap;

import java.util.Iterator;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public class DefaultConnectToCommand extends ConnectToCommand {

	private ConnectToCommand connectToCommand = null;

	public DefaultConnectToCommand(final ComponentsManager cm,
			final String idSource, final String typeSource,
			final String idDestination, final String typeDestination,
			final String dc, final String tc, final String sc,
			final PropertyMap actions, final long id) {
		super(cm, idSource, typeSource, idDestination, typeDestination, dc, tc,
				sc, actions, id);
	}

	private void connectToAll() {
		Activator.LOGGER.info(toString() + " connectToAll(" + typeSource + ", "
				+ idSource + ", " + typeDestination + ", " + idDestination
				+ ", " + dc + ")");
		// send requests
		cm.notifyConnectToAll(new ConnectToAll(typeSource, typeDestination,
				idSource));
	}

	private void connectToFirst() {
		// preparing responses handler
		connectToCommand = new ConnectToFirstCommand(cm, idSource, typeSource,
				idDestination, typeDestination, dc, tc, sc, actions, id) {

			{
				responseCount = cm.getRemoteDSes().size() + 1;
			}
		};

		// send requests
		Activator.LOGGER.info(toString() + " connectToFirst(" + typeSource
				+ ", " + idSource + ", " + typeDestination + ", "
				+ idDestination + ", " + dc + ")");
		cm.getLocalDS().getFirstOfType(typeDestination, getId());
		final Iterator<RemoteDS> it = cm.getRemoteDSes().iterator();
		while (it.hasNext()) {
			it.next().getFirstOfType(typeDestination, getId());
		}
	}

	private void connectToOne() {
		// preparing responses handler
		connectToCommand = new ConnectToOneCommand(cm, idSource, typeSource,
				idDestination, typeDestination, dc, tc, sc, actions, id) {

			{
				responseCount = cm.getRemoteDSes().size() + 1;
			}
		};

		// send requests
		Activator.LOGGER.info(toString() + " connectToOne(" + typeSource + ", "
				+ idSource + ", " + typeDestination + ", " + idDestination
				+ ", " + dc + ")");
		cm.getLocalDS().getComponent(typeDestination, idDestination, getId());
		final Iterator<RemoteDS> it = cm.getRemoteDSes().iterator();
		while (it.hasNext()) {
			it.next().getComponent(typeDestination, idDestination, getId());
		}
	}

	private void continueStrategy() {
		Activator.LOGGER.info(toString() + " continueStrategy(" + typeSource
				+ ", " + idSource + ", " + typeDestination + ", "
				+ idDestination + ", " + dc + ")");
		if (idDestination != null && !idDestination.equals("")) {
			connectToOne();
		} else if (typeDestination != null && !typeDestination.equals("")) {
			if (tc == null || !tc.equals("*")) {
				connectToFirst();
			} else {
				connectToAll();
			}
		} else {
			Activator.LOGGER
					.severe("DefaultConnectToCommand.continueStrategy(): type and id destination are null");
		}
	}

	@Override
	public void execute() {
		if (connectToCommand != null) {
			connectToCommand.execute();
		} else {
			Activator.LOGGER.info(toString() + " execute(" + typeSource + ", "
					+ idSource + ", " + typeDestination + ", " + idDestination
					+ ", " + dc + ", " + tc + ")");
			if (idSource != null && !idSource.equals("")) {
				if (cm.getLocalDS().isLocal(idSource)) {
					Activator.LOGGER.info("OK source id " + idSource);
					continueStrategy();
				} else {
					Activator.LOGGER.warning(idSource
							+ " is not a local instance (it may exist)");
				}
			} else {
				Activator.LOGGER.severe("Error : id of the source is null");
			}
		}
	}

	@Override
	public void giveResponse(final String componentName, final LocalDS localDS,
			final RemoteDS remoteDS) {
		if (connectToCommand != null) {
			connectToCommand.giveResponse(componentName, localDS, remoteDS);
		}
	}

	@Override
	public void close() {
		if (connectToCommand != null) {
			connectToCommand.close();
		}
		connectToCommand = null;
	}

	@Override
	public String toString() {
		return "ConnectToCmd";
	}
}
