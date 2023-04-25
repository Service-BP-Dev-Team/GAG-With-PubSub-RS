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
 *     along with Foobar; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: VirtualPipesContainerProxy.java 2494 2008-11-26 08:30:30Z bboussema $
package inria.smarttools.ds.pon;

import inria.lognet.virtpipes.VirtPipeOutput;
import inria.lognet.virtpipes.VirtPipesService;
import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.UUID;

import com.google.gson.Gson;

/**
 * Proxy of a remote component, linked by a VirtualPipe, used by Unicast and
 * Multicast model.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @revision $Revision: 2494 $
 */
public class PonContainerProxy extends VirtPipeOutput implements ContainerProxy {

	private final ComponentDescription componentDescription;
	private final String idName;
	private final LinkedList<Message> messages = new LinkedList<Message>();

	public PonContainerProxy(final VirtPipesService virtPipesService,
			final UUID uuid, final String idName,
			final ComponentDescription componentDescription)
			throws IOException, Exception {
		super(virtPipesService, uuid);
		this.idName = idName;
		this.componentDescription = componentDescription;
		Activator.LOGGER.info("PON Proxy : " + idName + " > " + uuid + "\n"
				+ componentDescription.serialize());
	}

	public void disconnect(final ContainerProxy other) {
		final Message msg = new MessageImpl();
		msg.setName("disconnect");
		msg.setExpeditor(other);
		receive(msg);
	}

	@Override
	public void error(final Exception e) {}

	public ComponentDescription getContainerDescription() {
		return componentDescription;
	}

	public String getIdName() {
		return idName;
	}

	public void receive(final Message msg) {
		synchronized (messages) {
			messages.add(msg);
			messages.notify();
		}
	}

	public void run() {
		try{
		while (!Thread.interrupted()) {
			synchronized (messages) {
				if (messages.size() == 0) {
					try {
						messages.wait();
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Message msg;
			synchronized (messages) {
				msg = messages.getFirst();
				messages.removeFirst();
			}

			// Not necessary, but compatible with SimpleUDP et SimpleTCP
			if (idName.startsWith("ComponentsManager")) {
				msg.setAdresseeId("ComponentsManager");
				msg.setAdresseeType("componentsManager");
			} else {
				msg.setAdresseeId(getIdName());
				msg.setAdresseeType(getContainerDescription()
						.getComponentName());
			}

			Activator.LOGGER.fine("Send " + msg.getName() + " from "
					+ msg.getExpeditorId() + ":" + msg.getExpeditorType()
					+ " to " + msg.getAdresseeId() + ":"
					+ msg.getAdresseeType());

			// Serialization and Send
			super.send(ByteBuffer.wrap(new Gson().toJson(msg).getBytes()));
		}
		}
		catch(Exception e){
			Activator.LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * TODO auto-disconnect
	 */
	@Override
	public void virtPipeClosed() {}

	public void disconnect(String cmpName) {
		// TODO Auto-generated method stub
		
	}
}
