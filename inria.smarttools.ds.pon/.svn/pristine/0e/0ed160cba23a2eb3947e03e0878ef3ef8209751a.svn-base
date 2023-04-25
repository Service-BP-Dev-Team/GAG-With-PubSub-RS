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
// $Id: PiNetDS.java 2494 2008-11-26 08:30:30Z bboussema $
package inria.smarttools.ds.pon;

import inria.lognet.virtpipes.VirtPipesService;
import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.componentsmanager.ConnectToAll;
import inria.smarttools.componentsmanager.RemoteDS;
import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.ComponentDescriptionImpl;
import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.InOut;
import inria.smarttools.core.component.Input;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.PropertyMap;
import inria.smarttools.core.util.MulticastProvider;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import com.google.gson.Gson;

import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @revision $Revision: 2494 $
 */
public class PonDs implements RemoteDS, MulticastProvider {

	/**
	 * Tuple for Chord entries for the local and registered components
	 * 
	 * @author baptiste
	 */
	class Entry {

		public UUID cm;
		public UUID cmp;
		public ComponentDescription cdml;
	}

	/**
	 * The Key-Values Store
	 */
	private final Chord piNetwork;

	/**
	 * The communication channels manager
	 */
	private final VirtPipesService virtPipesService;

	/**
	 * Local CM
	 */
	private ComponentsManager cm;

	/**
	 * Used Remote components, indexed by their names
	 */
	private final Map<String, ContainerProxy> containerProxies = new HashMap<String, ContainerProxy>();

	/**
	 * List of awaiting notifications until the CM is set.
	 */
	private final List<String> awaitingNotifications = new Vector<String>();

	/**
	 * List of connectToAll delegation. (Not complete yet, miss the DHT model)
	 */
	private final List<ConnectToAll> connectToAlls = new Vector<ConnectToAll>();

	/**
	 * Structure of the multicast model, delegated from the local components
	 */
	private final Map<Container, Map<String, Set<ContainerProxy>>> multicastModel = new HashMap<Container, Map<String, Set<ContainerProxy>>>();

	/**
	 * Model of the connectivity, key of local components' name gives the list
	 * of remote components where this local component is connected
	 */
	private final Map<String, Set<String>> connections = new HashMap<String, Set<String>>();

	public PonDs(final Chord piNetwork, final VirtPipesService virtPipesService) {
		this.piNetwork = piNetwork;
		this.virtPipesService = virtPipesService;
	}

	/**
	 * Delegate the connectToAll by the CM. (Not complete yet, miss DHT model)
	 */
	public void addConnectToAll(final ConnectToAll connectToAll) {
		// add for further notifications
		connectToAlls.add(connectToAll);

		// do it for all started components now
		final Iterator<ContainerProxy> it = containerProxies.values()
				.iterator();
		while (it.hasNext()) {
			final ContainerProxy destination = it.next();
			if (destination.getContainerDescription().getComponentName()
					.equals(connectToAll.getTypeDestination())) {
				connectToOne(connectToAll.getIdSource(), destination
						.getIdName(), destination.getContainerDescription()
						.getComponentName(), null, new PropertyMap());
			}
		}
	}

	public void close() {
		containerProxies.clear();
		multicastModel.clear();
		connections.clear();
		awaitingNotifications.clear();
		connectToAlls.clear();
	}

	/**
	 * Last step for connecting a local component to a component proxy
	 * 
	 * @param source
	 * @param dest
	 */
	private void connectComponent(final Container source,
			final ContainerProxy dest) {
		{ // Internal model of connections
			Set<String> v = null;
			if (!connections.containsKey(dest.getIdName())) {
				v = new HashSet<String>();
				connections.put(dest.getIdName(), v);
			} else {
				v = connections.get(dest.getIdName());
			}
			v.add(source.getIdName());
			cm.logINFO("REMOTE newConnexion " + source.getIdName() + " -> "
					+ dest.getIdName());
		} // End of Internal model of connections

		source.connect(dest);

		{ // MultiCast model connection
			Map<String, Set<ContainerProxy>> sourceModel = multicastModel
					.get(source);
			if (sourceModel == null) {
				sourceModel = new HashMap<String, Set<ContainerProxy>>();
				multicastModel.put(source, sourceModel);
			}
			// Match source's outputs with dest's inputs (and inouts)
			final Map<String, Input> otherInputs = dest
					.getContainerDescription().getInputs();
			if (otherInputs != null) {
				final Iterator<String> iterOtherInputs = otherInputs.keySet()
						.iterator();
				final Collection<String> iterOuputs = source
						.getContainerDescription().getOutputs().keySet();
				while (iterOtherInputs.hasNext()) {
					final String inputName = iterOtherInputs.next();
					if (iterOuputs.contains(inputName)) {
						// logINFO("Connecting output : " + inputName + " to "
						// + dest);
						Set<ContainerProxy> v = null;
						if ((v = sourceModel.get(inputName)) == null) {
							v = new HashSet<ContainerProxy>();
							sourceModel.put(inputName, v);
						}
						v.add(dest);
					}
				}
			}
			final Map<String, InOut> otherInOuts = dest
					.getContainerDescription().getInOuts();
			if (otherInOuts != null) {
				final Iterator<String> iterOtherInOuts = otherInOuts.keySet()
						.iterator();
				final Collection<String> iterOuputs = source
						.getContainerDescription().getOutputs().keySet();
				while (iterOtherInOuts.hasNext()) {
					final String inOutName = iterOtherInOuts.next();
					if (iterOuputs.contains(inOutName)) {
						// logINFO("Connecting inOut : " + inOutName + " to "
						// + dest);
						Set<ContainerProxy> v = null;
						if ((v = sourceModel.get(inOutName)) == null) {
							v = new HashSet<ContainerProxy>();
							sourceModel.put(inOutName, v);
						}
						v.add(dest);
					}
				}
			}
		} // END of MultiCast model connection

		cm.sendWhenAvailable(new MessageImpl("newConnexion", new PropertyMap() {

			private static final long serialVersionUID = 1L;

			{
				put("srcNodeName", source.getIdName());
				put("destNodeName", dest.getIdName());
			}
		}, null));

		// Set<String> v = null;
		//
		// if (!connections.containsKey(dest.getIdName())) {
		// v = new HashSet<String>();
		// connections.put(dest.getIdName(), v);
		// } else {
		// v = connections.get(dest.getIdName());
		// }
		// v.add(source.getIdName());
		//
		// cm.logINFO("REMOTE newConnexion " + source.getIdName() + " -> "
		// + dest.getIdName());
		//
		// source.connect(dest);
	}

	/**
	 * CM's call for connecting a local component to a remote component with
	 * this protocol.
	 */
	public void connectToOne(final String idSource, final String idDestination,
			final String typeDestination, final String dc,
			final PropertyMap actions) {
		connectToRemote(idSource, typeDestination, idDestination, dc, actions);
	}

	/**
	 * Implementation of how we search, create proxies and connect to remote
	 * components
	 * 
	 * @param src_id
	 *            ID of the local component
	 * @param dest_type
	 *            Type of the local component (not used)
	 * @param dest_id
	 *            ID of the remote component
	 * @param dc
	 *            if there is a dual-connect
	 * @param actions
	 *            messages played after this connect is done
	 */
	private synchronized void connectToRemote(final String src_id,
			final String dest_type, final String dest_id, final String dc,
			final PropertyMap actions) {
	
		// Look if these components are not already connected and dc != 3(dual end)
		if (connections.containsKey(dest_id)
				&& connections.get(dest_id).contains(src_id) && !dc.equals("3")) {
			return;
		}

		// fetch the local component
		ContainerProxy component_dest = null;
		final Container component_src = cm.getLocalDS().getComponentsStarted()
				.get(src_id);

		if (component_src == null) {
			cm.logERROR("Component " + src_id + " does not exist");
			return;
		}

		// fetch the proxy of the remote component
		component_dest = containerProxies.get(dest_id);
		Entry newEntry = null;
		if (component_dest == null) { // no proxy found
			// Search in the DHT, and create the proxy
			try {
				final Iterator<Serializable> it = piNetwork.retrieve(new Key() {

					public byte[] getBytes() {
						return ("smarttools:" + dest_id).getBytes();
					}

				}).iterator();
				if (it.hasNext()) {
					newEntry = extract((String) it.next());
					final UUID uuid = newEntry.cmp;
					// if (dest_id.startsWith("ComponentsManager")) {
					// final ComponentDescription cmpDesc = new
					// ComponentDescriptionImpl();
					// cmpDesc.process(new ByteArrayInputStream(getCm()
					// .getContainerDescription().serialize()
					// .getBytes()));
					//
					// component_dest = new PonContainerProxy(
					// virtPipesService, uuid, dest_id, cmpDesc);
					// notifyService(component_dest);
					// new Thread(component_dest, component_dest.getIdName()
					// + " PON proxy").start();
					// } else {
					final ComponentDescription cmpDesc = newEntry.cdml;
					component_dest = new PonContainerProxy(virtPipesService,
							uuid, dest_id, cmpDesc);
					notifyService(component_dest);
					new Thread(component_dest, component_dest.getIdName()
							+ " PON proxy").start();
					// }
					final ContainerProxy cmp = component_dest;
					cm.sendWhenAvailable(new MessageImpl(
							"newComponentInstanceAvailable", new PropertyMap() {

								private static final long serialVersionUID = 1L;

								{
									put("nodeName", cmp.getIdName());
									put("nodeType", "oval");
									put("nodeColor", "blue");
								}
							}, null));
				}
			} catch (final Exception e) {
				e.printStackTrace();
				Activator.LOGGER.severe("Unable to find component "+component_dest.getIdName()+" in Local OR Remote !");
				return; // Failure
			}
			// Success, continue
		}
		else{
			// To get the remote cm even if we allready have the proxy
			try {
				final Iterator<Serializable> it = piNetwork.retrieve(new Key() {

					public byte[] getBytes() {
						return ("smarttools:" + dest_id).getBytes();
					}

				}).iterator();
				if (it.hasNext()) {
					newEntry = extract((String) it.next());
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}

		}

		// the proxy is found or created, then connect the local component to
		// the proxy
		
		// If Dc == 3 then the dual connect is done 
		if(dc.equals("3")){		
			System.out.println("The dual connect is done !");
			((AbstractContainer) component_src).CheckNeighbors(component_dest);
			return;
		}
		else{
			connectComponent(component_src, component_dest);
			setAttributes(component_src, component_dest, actions);
		}

		// dual-connect (Normal)
		if (dc != null && dc.equals("1")) {
			if (component_dest != null) {
				Activator.LOGGER.fine("Sending Message connectTo to Remote CM...");
				createMessage(dest_id, component_src.getContainerDescription()
						.getComponentName(), src_id, actions, newEntry.cm );
			}
		}
		
		// Dual connect received : checkneighbours() and send confirmation of dual
		if (dc != null && dc.equals("2")) {
				if (component_dest != null) {
					((AbstractContainer) component_src).CheckNeighbors(component_dest);
					sendEndDual(dest_id, component_src.getContainerDescription()
							.getComponentName(), src_id, newEntry.cm);
					
				}
		}
	}

	/**
	 * Send a connectTo to the remote CM
	 * 
	 * @param src_id
	 * @param dest_type
	 * @param dest_id
	 * @param actions
	 * @param uuid
	 */
	private void createMessage(final String src_id, final String dest_type,
			final String dest_id, PropertyMap actions, final UUID uuid) {

		try{
			cm.logINFO("createMessage " + src_id + ", " + dest_type + ", "
					+ dest_id + ", " + actions + ">" + uuid);
	
			final PropertyMap attr = new PropertyMap();
	
			attr.put("id_src", src_id);
			attr.put("type_dest", dest_type);
			attr.put("id_dest", dest_id);
			attr.put("dc", "2");
			attr.put("sc", "OFF");
			if (actions == null) {
				actions = new PropertyMap();
			}
			attr.put("actions", actions);
	
			final Message m = new MessageImpl("connectTo", attr, null);
			m.setAdresseeId("ComponentsManager");
			m.setAdresseeType("componentsManager");
			m.setExpeditor(getCmProxy());
	
			// Serialization and send of this message
			Activator.getInstance().getVirtPipesService().getVirtPipeOutput(uuid)
					.send(ByteBuffer.wrap(new Gson().toJson(m).getBytes()));
		}
		catch(Exception e){
			Activator.LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	// Like create message but used to send end of dual connect
	private void sendEndDual(final String src_id, final String dest_type,
			final String dest_id, final UUID uuid) {

		try{	
			final PropertyMap attr = new PropertyMap();
	
			attr.put("id_src", src_id);
			attr.put("type_dest", dest_type);
			attr.put("id_dest", dest_id);
			attr.put("dc", "3");
			attr.put("sc", "OFF");
			attr.put("actions", new PropertyMap());
	
			final Message m = new MessageImpl("connectTo", attr, null);
			m.setAdresseeId("ComponentsManager");
			m.setAdresseeType("componentsManager");
			m.setExpeditor(getCmProxy());
	
			// Serialization and send of this message
			Activator.getInstance().getVirtPipesService().getVirtPipeOutput(uuid)
					.send(ByteBuffer.wrap(new Gson().toJson(m).getBytes()));
		}
		catch(Exception e){
			Activator.LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	// Method to extract information serialized in the DHT
	private Entry extract(final String serialized) {
		final Entry entry = new Entry();
		String cmpString = serialized.substring(serialized.indexOf('"') + 1);
		cmpString = cmpString.substring(0, cmpString.indexOf('"'));
		String cmString = serialized.substring(cmpString.length() + 4);
		cmString = cmString.substring(0, cmString.indexOf('"'));
		String cdmlString = serialized.substring(cmpString.length()
				+ cmString.length() + 7);
		cdmlString = cdmlString.substring(0, cdmlString.length() - 1);
		//System.out.println("  IMPORTANT  extract -> " + cmpString + " ; "+ cmString + " ; " + cdmlString);
		entry.cdml = new ComponentDescriptionImpl();
		try {
			entry.cdml.process(new ByteArrayInputStream(cdmlString.getBytes()));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		entry.cm = UUID.fromString(cmString);
		entry.cmp = UUID.fromString(cmpString);
		return entry;
	}

	/**
	 * @return local CM
	 */
	public ComponentsManager getCm() {
		return cm;
	}

	/**
	 * @return local CM
	 */
	public ContainerProxy getCmProxy() {
		return cm;
	}

	/**
	 * Method called by the CM for looking if this DS knows a component
	 */
	public void getComponent(final String type, final String id,
			final Long responseId) {
		try {
			if (containerProxies.containsKey(id)) { // look if we know it
				// already
				// notify the CM we have it
				cm.notifyComponent(id, null, this, responseId);
			} else { // find the answer in the DHT
				final Iterator<Serializable> it = piNetwork.retrieve(new Key() {

					public byte[] getBytes() {
						return ("smarttools:" + id).getBytes();
					}

				}).iterator();
				if (it.hasNext()) {
					final String client = (String) it.next();
					Activator.LOGGER.info("smarttools:" + id + " PiNET OK "
							+ client);
					// notify the CM we have it
					cm.notifyComponent(id, null, this, responseId);
				} else {
					// notify the CM we don't have it
					cm.notifyComponent(null, null, this, responseId);
				}
			}
		} catch (final Exception e) {
			// notify the CM we don't have it
			cm.notifyComponent(null, null, this, responseId);
		}
	}

	public Map<String, Set<String>> getConnections() {
		return connections;
	}

	public Map<String, ContainerProxy> getContainerProxies() {
		return containerProxies;
	}

	/**
	 * Method called by the CM for looking if this DS knows a component that is
	 * the type asked
	 */
	public void getFirstOfType(final String type, final Long responseId) {
		final Iterator<ContainerProxy> it = containerProxies.values()
				.iterator();
		while (it.hasNext()) {
			final ContainerProxy proxy = it.next();
			if (proxy.getContainerDescription().getComponentName().equals(type)) {
				cm.notifyComponent(proxy.getIdName(), null, this, responseId);
				return;
			}
		}
		cm.notifyComponent(null, null, this, responseId);
		// TODO DHT requests
	}

	/**
	 * Notification from the CM that a local component is disconnected to a
	 * proxy
	 */
	public void notifyDisconnection(final Container source,
			final ContainerProxy dest) {
		final Map<String, Set<ContainerProxy>> sourceModel = multicastModel
				.get(source);
		if (sourceModel != null) {
			for (final Set<ContainerProxy> set : sourceModel.values()) {
				set.remove(dest);
			}
		}
	}

	/**
	 * Local notification that a proxy appeared, has been created
	 * 
	 * @param proxy
	 *            newly created
	 */
	public synchronized void notifyService(final ContainerProxy proxy) {
		if (!containerProxies.containsKey(proxy.getIdName())) {
			Activator.LOGGER.info("REMOTE PROXY " + proxy.getIdName() + " "
					+ proxy.getContainerDescription().getComponentName());

			containerProxies.put(proxy.getIdName(), proxy);

			// Play connectToAlls delegation
			final Iterator<ConnectToAll> it = connectToAlls.iterator();
			while (it.hasNext()) {
				final ConnectToAll connectToAll = it.next();
				if (connectToAll.getTypeDestination().equals(
						proxy.getContainerDescription().getComponentName())) {
					Activator.LOGGER
							.fine("CONNECT TO ALL " + proxy.getIdName());
					connectToOne(connectToAll.getIdSource(), proxy.getIdName(),
							proxy.getContainerDescription().getComponentName(),
							null, new PropertyMap());
				}
			}
			if (cm != null) {
				// CM notification
				cm.notifyComponent(proxy.getIdName(), null, this, null);
			} else {
				awaitingNotifications.add(proxy.getIdName());
			}
		}
	}

	/**
	 * Multicast sent delegation, use the proxies (not efficient)
	 */
	public void send(final Container source, final Message msg) {
		final Map<String, Set<ContainerProxy>> sourceModel = multicastModel
				.get(source);
		if (sourceModel != null) {
			final Set<ContainerProxy> proxies = sourceModel.get(msg.getName());
			if (proxies != null) {
				for (final ContainerProxy proxy : proxies) {
					proxy.receive(msg);
				}
			}
		}
	}

	private void setAttributes(final Container component_src,
			final ContainerProxy component_dest, final PropertyMap actions) {

		if (actions != null) {
			int j = 0;
			String afterConnect = null;
			while ((afterConnect = actions.getString("afterConnect" + j)) != null) {
				final Message msg = actions.getAfterConnect(afterConnect);
				msg.setExpeditor(component_src);
				component_dest.receive(msg);
				j++;
			}
		}

		// component_src.releaseWaitingMsg();
	}

	public void setComponentsManager(final ComponentsManager cm) {
		this.cm = cm;
	}

	public void notifyDisconnection(Container abstractContainer, String cmpName) {
		// TODO Auto-generated method stub
		
	}

}
