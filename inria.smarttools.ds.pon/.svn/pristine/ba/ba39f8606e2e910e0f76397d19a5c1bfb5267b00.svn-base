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
// $Id: ContainerProxyReader.java 2494 2008-11-26 08:30:30Z bboussema $
package inria.smarttools.ds.pon;

import inria.lognet.virtpipes.VirtPipeInput;
import inria.lognet.virtpipes.VirtPipesService;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.MessageImpl;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

/**
 * End-point of the PonContainerProxy's VirtPipe, which reads the serialized
 * message. It deserializes the message and transmits it to the real component
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @revision $Revision: 2494 $
 */
public class PonContainerProxyReader implements VirtPipeInput {

	private final UUID id;

	private final ContainerProxy containerProxy;

	public PonContainerProxyReader(final ContainerProxy containerProxy,
			final VirtPipesService vps) {
		id = vps.generateUniqueId();
		this.containerProxy = containerProxy;
		Activator.LOGGER.info("PON Reader " + containerProxy.getIdName()
				+ " : " + id);
	}

	public UUID getId() {
		return id;
	}

	public void read(final ByteBuffer buffer) {
		final String msg = new String(buffer.array());
		Activator.LOGGER.info("Receiving " + msg);
		MessageImpl message;
		try {
			// JSON deserialization
			message = new Gson().fromJson(msg, MessageImpl.class);
			// set the expeditor's proxy, for easy return response
				
			if (message.getExpeditorId() != null && Activator.getInstance().getDS().getContainerProxies().containsKey(message.getExpeditorId()) ) {
				ContainerProxy exp=Activator.getInstance().getDS().getContainerProxies().get(message.getExpeditorId());
						message.setExpeditor(exp);
					 }
			// Message transmission
			containerProxy.receive(message);
		} catch (final Exception e1) {
			Activator.LOGGER.severe(e1.getMessage());
			e1.printStackTrace();
		}
	}

}
