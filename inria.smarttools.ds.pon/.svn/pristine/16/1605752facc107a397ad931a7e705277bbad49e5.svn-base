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
// $Id: VirtualPipesDsLogic.java 2494 2008-11-26 08:30:30Z bboussema $
package inria.smarttools.ds.pon;

import inria.lognet.virtpipes.VirtPipesService;
import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.ContainerProxy;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import de.uniba.wiai.lspi.chord.service.Key;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @revision $Revision: 2494 $
 */
public class PonDsLogic implements ServiceListener {

	/**
	 * DHT entries registered (own)
	 * 
	 * @author baptiste
	 */
	class ChordEntry {

		public Key key;
		public Serializable serializable;
	}

	/**
	 * String representation of the CM's VirtPipeInput's ID, mainly for
	 * receiving dual-connectTos.
	 */
	private String cmUuid = null;

	/**
	 * The VirtPipeInput readers linked to the local components
	 */
	private final Map<ContainerProxy, PonContainerProxyReader> readers = new HashMap<ContainerProxy, PonContainerProxyReader>();

	/**
	 * The registered information in the DHT
	 */
	private final Map<ContainerProxy, ChordEntry> chordEntries = new HashMap<ContainerProxy, ChordEntry>();

	/**
	 * Publish a local component in this network
	 * 
	 * @param containerProxy
	 */
	void registerContainerProxy(final ContainerProxy containerProxy) {
		if (containerProxy != null && containerProxy.getIdName() != null
				&& !containerProxy.getIdName().startsWith("ComponentsManager")) {
			Activator.LOGGER.info("Publish " + containerProxy.getIdName());
			if (containerProxy instanceof Container) {
				((Container) containerProxy).addMulticastProvider(Activator
						.getInstance().getDS());
			}
			UUID id = null;
			// VirtPipe
			final VirtPipesService vps = Activator.getInstance()
					.getVirtPipesService();
			final PonContainerProxyReader reader = new PonContainerProxyReader(
					containerProxy, vps);
			vps.registerVirtPipe(reader);
			readers.put(containerProxy, reader);
			id = reader.getId();
			// END VirtPipe
			// DHT
			try {
				final StringBuilder sb = new StringBuilder("\"");
				sb.append(id.toString());
				sb.append("\",\"");
				sb.append(cmUuid);
				sb.append("\",\"");
				sb.append(containerProxy.getContainerDescription().serialize());
				sb.append("\"");
				final ChordEntry chordEntry = new ChordEntry();
				chordEntry.key = new Key() {

					public byte[] getBytes() {
						return ("smarttools:" + containerProxy.getIdName())
								.getBytes();
					}

				};
				chordEntry.serializable = sb.toString();
				//Activator.LOGGER.fine("REGISTERING " + chordEntry.key + "->"+ chordEntry.serializable);
				chordEntries.put(containerProxy, chordEntry);
				Activator.getInstance().getDHT().insert(chordEntry.key,
						chordEntry.serializable);
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}
			// END DHT
		} else if (containerProxy != null && containerProxy.getIdName() != null
				&& containerProxy.getIdName().startsWith("ComponentsManager")) {
			// VP
			final VirtPipesService vps = Activator.getInstance()
					.getVirtPipesService();
			final PonContainerProxyReader reader = new PonContainerProxyReader(
					containerProxy, vps);
			vps.registerVirtPipe(reader);
			cmUuid = reader.getId().toString();
			// END VP
		}
	}

	/**
	 * Un-publish a component
	 * 
	 * @param containerProxy
	 */
	private void removeContainerProxy(final ContainerProxy containerProxy) {
		if (containerProxy != null && containerProxy.getIdName() != null
				&& !containerProxy.getIdName().startsWith("ComponentsManager")) {
			Activator.LOGGER.info("Unpublish " + containerProxy.getIdName());
			if (containerProxy instanceof Container) {
				((Container) containerProxy).removeMulticastProvider(Activator
						.getInstance().getDS());
			}
			try {
				// DHT
				final ChordEntry chordEntry = chordEntries.get(containerProxy);
				if (chordEntry != null) {
					Activator.getInstance().getDHT().remove(chordEntry.key,
							chordEntry.serializable);
				}
				// END DHT
				// VP
				final PonContainerProxyReader reader = readers
						.get(containerProxy);
				if (reader != null) {
					readers.remove(containerProxy);
					Activator.getInstance().getVirtPipesService().close(reader);
				}
				// END VP
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.
	 * ServiceEvent)
	 */
	public void serviceChanged(final ServiceEvent event) {
		if (Activator.getInstance().getContext() == null) {
			return;
		}
		final ServiceReference reference = event.getServiceReference();

		switch (event.getType()) {
			case ServiceEvent.REGISTERED: {
				final Object service = Activator.getInstance().getContext()
						.getService(reference);
				if (service instanceof ContainerProxy) {
					registerContainerProxy((ContainerProxy) service);
				}
				return;
			}
			case ServiceEvent.UNREGISTERING: {
				final Object service = Activator.getInstance().getContext()
						.getService(reference);
				if (service instanceof ContainerProxy) {
					removeContainerProxy((ContainerProxy) service);
				}
				return;
			}
		}
	}
}
