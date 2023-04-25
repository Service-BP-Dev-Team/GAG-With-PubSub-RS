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
// $Id: ComponentsManager.java 2928 2009-11-28 09:01:21Z bboussema $
package inria.smarttools.componentsmanager;

import inria.smarttools.componentsmanager.connectto.ConnectToCommand;
import inria.smarttools.componentsmanager.connectto.ConnectToCommandFactory;
import inria.smarttools.componentsmanager.internal.STShutdowner;
import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.MethodCall;
import inria.smarttools.core.component.PropertyMap;
import inria.smarttools.core.util.STResources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * ComponentsManager
 * <p>
 * A special component dedicaced to manage component.
 * <p>
 * 
 * <pre>
 *    services :
 *      - loadJar          [ name, url ]
 *      - loadComponent    [ name, url, depends? ]
 *      - start component  [ id?, type ]
 *      - stop component   [ ref ]
 *      - sleep component  [ ref ]
 *      - wakeup component [ ref ] 
 *      - connectComponent [ ref1, ref2 ]
 *      - loadWorld 
 *   
 *    attributes :
 *      - repository
 *      - verbose
 *      - worldFile
 *   
 *    -------------------------------------------------------------------
 *   
 *    Attributs :
 *      id_src    [java.lang.String] :
 *      type_dest [java.lang.String] :
 *      id_dest   [java.lang.String] :
 *   
 *    Inputs :
 *      connectTo [id_src,type_dest,id_dest,ref_id] :
 *      quit  []					 :
 *      newComponent                                :
 *   
 *    ------------------------------------------------------------------
 * </pre>
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2928 $
 */
public class ComponentsManager extends AbstractContainer {

	private final Map<Long, ConnectToCommand> connectToCommands = new HashMap<Long, ConnectToCommand>();

	private LocalDS localDS;

	private final List<RemoteDS> remoteDSes = new ArrayList<RemoteDS>();

	private final List<ConnectToAll> connectToAlls = new Vector<ConnectToAll>();

	/**
	 * Registered SPY !
	 */
	private List<String> spies = null;

	public final String version = STResources.getProperty("SmartTools.Version");

	/**
	 * file that contain world description
	 */
	public World world = null;

	{
		/* DOCUMENT */
		List<MethodCall> methodCalls;
		methodCalls = calls.get("requestFile");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("requestFile", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				final Message message = getLocalDS().requestFile(
						parameters.getString("requestedFilename"));
				if (message != null) {
					sendWhenAvailable(message);
				}
				return null;
			}
		});
		methodCalls = calls.get("requestView");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("requestView", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				requestView(parameters.getString("requestedView"), parameters
						.getPropertyMap("arguments"));
				return null;
			}
		});
		/* END DOCUMENT */
		methodCalls = calls.get("exit");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("exit", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				exit();
				return null;
			}
		});
		methodCalls = calls.get("wakeupComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("wakeupComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().wakeupComponent(
						parameters.getString("componentName"));
				return null;
			}
		});
		methodCalls = calls.get("newComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("newComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().newComponentInstance(
						(Container) parameters.get("component"),
						parameters.getString("name"),
						parameters.getString("filename"));
				return null;
			}
		});
		methodCalls = calls.get("addComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("addComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().addComponent(parameters.getString("jarName"),
						parameters.getString("componentName"),
						parameters.getString("urlToJar"));
				return null;
			}
		});
		methodCalls = calls.get("addSpy");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("addSpy", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				addSpy(parameters.getString("componentId"), parameters
						.getPropertyMap("actions"));
				return null;
			}
		});
		methodCalls = calls.get("stopComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("stopComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().stopComponent(
						parameters.getString("componentName"));
				return null;
			}
		});
		methodCalls = calls.get("startComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("startComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().startComponent(
						parameters.getString("componentId"),
						parameters.getString("componentType"),
						parameters.getPropertyMap("actions"), true);
				return null;
			}
		});
		methodCalls = calls.get("sleepComponent");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("sleepComponent", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				getLocalDS().sleepComponent(
						parameters.getString("componentName"));
				return null;
			}
		});
		methodCalls = calls.get("serialize");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("serialize", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				Activator.LOGGER
						.info("Serialize : Parameters is " + parameters);
				serialize(parameters.getString("path"), parameters
						.getString("basename"));
				return null;
			}
		});
		methodCalls = calls.get("connectTo");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("connectTo", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				connectTo(parameters.getString("id_src"), parameters
						.getString("type_dest"), parameters
						.getString("id_dest"), parameters.getString("dc"),
						parameters.getString("tc"), parameters.getString("sc"),
						parameters.getPropertyMap("actions"));
				return null;
			}
		});
		methodCalls = calls.get("requestInitData");
		if (methodCalls == null) {
			methodCalls = new ArrayList<MethodCall>();
			calls.put("requestInitData", methodCalls);
		}
		methodCalls.add(new MethodCall() {

			public Object call(final ContainerProxy expeditor,
					final String expeditorId, final String expeditorType,
					final PropertyMap parameters) {
				buildResponseForInOut(expeditor, expeditorId, expeditorType,
						getContainerDescription().getInOuts().get(
								"requestInitData"), new PropertyMap() {

							private static final long serialVersionUID = 1L;

							{
								put("behaviors", getContainerDescription()
										.getBehavior());
							}
						});
				return null;
			}
		});
	}

	public ComponentsManager() {
		super("ComponentsManager",
				"/inria/smarttools/componentsmanager/resources/cm.cdml");
	}

	public void addRemoteDS(final RemoteDS remoteDS) {
		remoteDSes.add(remoteDS);
		remoteDS.setComponentsManager(this);
		final Iterator<ConnectToAll> it = connectToAlls.iterator();
		while (it.hasNext()) {
			remoteDS.addConnectToAll(it.next());
		}
	}

	private ContainerProxy addSpy(final String spy_id, final PropertyMap actions) {
		final Container spy = getLocalDS()
				.internalStartComponent("spy", spy_id);
		spy.switchOn();
		//
		if (actions != null) {
			final PropertyMap attributes = actions.getPropertyMap("attributes");
			if (attributes != null) {
				final Iterator<String> iter = attributes.stringsKeySet()
						.iterator();
				while (iter.hasNext()) {
					final String key = iter.next();
					final String type = attributes.getString(key).getClass()
							.getName(); // po
					// top
					spy.setAttribute(key, attributes.getString(key), type);
				}
			}
		}
		if (actions != null) {
			int j = 0;
			while (actions.getAfterConnect("afterConnect" + j) != null) {
				j++;
			}
		}

		spies.add(spy.getIdName());

		final ContainerProxy s = spy;
		for (final Container proxy : getLocalDS().getComponentsStarted()
				.values()) {
			if (s != proxy) {
				connectTo(proxy.getIdName(), s.getIdName(), s
						.getContainerDescription().getComponentName(), null,
						null, null, new PropertyMap());
			}
		}
		return spy;
	}

	/**
	 * IMPLEMENTATION OF SERVICE <connectTo> Connect a source component to an
	 * other (possibly not existing) component and send do some actions when
	 * done. 1/ recherche du composant destination - si pas existant, le creer
	 * et le connecter au Components Manager - si composant Document, identifier
	 * de maniere unique un chemin le fichier a ouvrir et preparer un message
	 * qui enverra aux listenenrs l'arbre de syntaxe construit. 2/ connexion
	 * source et destination 3/ Si destination nouvellement creer, le switchOn
	 * 4/ Envois eventuellement d'un message vers destination
	 * 
	 * @param tc
	 * @param idSource
	 *            a <code>String</code> value : id of source component (MUST
	 *            EXIST)
	 * @param typeDestination
	 *            a <code>String</code> value : type of dest component
	 * @param idDestination
	 *            a <code>String</code> value : id of dest component
	 * @param actions
	 *            a <code>Message</code> value : actions to do after connection
	 */
	public void connectTo(final String idSource, final String typeDestination,
			final String idDestination, final String dc, final String tc,
			final String sc, final PropertyMap actions) {
		// if (idDestination.contains("::")) {
		// String[] tab = idDestination.split("::");
		// if (tab[2].equals(getIpAddress())) {
		// ConnectToCommand connectToCommand = ConnectToCommandFactory
		// .getConnectToCommand(this, idSource, null,
		// idDestination, typeDestination, dc, tc, actions);
		// connectToCommands.put(connectToCommand.getId(),
		// connectToCommand);
		// connectToCommand.execute();
		// }
		// } else {
		final ConnectToCommand connectToCommand = ConnectToCommandFactory
				.getConnectToCommand(this, idSource, null, idDestination,
						typeDestination, dc, tc, sc, actions);
		connectToCommands.put(connectToCommand.getId(), connectToCommand);
		connectToCommand.execute();
		// }
	}

	@Override
	public void disconnect(final String cmpName) {
		super.disconnect(cmpName);
		if (cmpName != null) {
			getLocalDS().getComponentsStarted().remove(cmpName);

			final PropertyMap attr = new PropertyMap();

			attr.put("nodeName", cmpName);

			final Message m = new MessageImpl("removeComponent", attr, null);

			send(m);
		}
	}

	/**
	 * IMPLEMENTATION OF SERVICE <exit> Exit
	 */
	protected void exit() {
		logINFO("A component request a EXIT action");
		final Iterator<Container> it = getLocalDS().getComponentsStarted()
				.values().iterator();
		while (it.hasNext()) {
			final Container container = it.next();
			if (!container.equals(this)) {
				container.receive(new MessageImpl("shutdown",
						new PropertyMap(), null));
			}
		}
		shutdown();
		new Thread(new STShutdowner(), "STShutdowner").start();
		localDS = null;
		if (spies != null) {
			spies.clear();
		}
		spies = null;
		world = null;
	}

	public LocalDS getLocalDS() {
		return localDS;
	}

	public List<RemoteDS> getRemoteDSes() {
		return remoteDSes;
	}

	public List<String> getSpies() {
		return spies;
	}

	@Override
	public boolean isDisconnectable() {
		return false;
	}

	public void notifyComponent(final String componentName,
			final LocalDS localDS, final RemoteDS remoteDS,
			final Long responseId) {
		if (responseId == null) {
			final Map<Long, ConnectToCommand> clone = new HashMap<Long, ConnectToCommand>();
			synchronized (this) {
				final Iterator<Long> it = connectToCommands.keySet().iterator();
				while (it.hasNext()) {
					final Long key = it.next();
					clone.put(key, connectToCommands.get(key));
				}
			}
			final Iterator<ConnectToCommand> it = clone.values().iterator();
			while (it.hasNext()) {
				final ConnectToCommand connectToCommand = it.next();
				connectToCommand.giveResponse(componentName, localDS, remoteDS);
				connectToCommand.execute();
			}
		} else {
			if (connectToCommands.get(responseId) != null) {
				connectToCommands.get(responseId).giveResponse(componentName,
						localDS, remoteDS);
			}
		}
	}

	public void notifyConnectToAll(final ConnectToAll connectToAll) {
		connectToAlls.add(connectToAll);
		getLocalDS().addConnectToAll(connectToAll);
		final Iterator<RemoteDS> it = getRemoteDSes().iterator();
		while (it.hasNext()) {
			it.next().addConnectToAll(connectToAll);
		}
	}

	public void removeConnectToCommand(final Long id) {
		synchronized (connectToCommands) {
			if (connectToCommands.containsKey(id)) {
				connectToCommands.get(id).close();
				connectToCommands.remove(id);
			}
		}
	}

	public void removeRemoteDS(final RemoteDS remoteDS) {
		remoteDSes.remove(remoteDS);
		remoteDS.close();
	}

	/* DOCUMENT */
	private void requestView(final String requestedView,
			final PropertyMap arguments) {
		send(new MessageImpl("processLmlSkeleton", new PropertyMap() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("skeleton", requestedView);
				put("arguments", arguments);
			}
		}, null));
	}

	/* END DOCUMENT */

	@Override
	public void run() {

		spies = new ArrayList<String>();

		getLocalDS().getComponentsStarted().put(getIdName(), this);

		//
		// Tell that a new component (myself) is available.
		//
		sendWhenAvailable(new MessageImpl("newComponentAvailable",
				new PropertyMap() {

					private static final long serialVersionUID = 1L;

					{
						put("nodeName", "Component "
								+ getContainerDescription().getComponentName());
						put("nodeType", "rect");
						put("nodeColor", "orange");
					}
				}, null));
		sendWhenAvailable(new MessageImpl("newComponentInstanceAvailable",
				new PropertyMap() {

					private static final long serialVersionUID = 1L;

					{
						put("nodeName", getIdName());
						put("nodeType", "oval");
						put("nodeColor", "red");
					}
				}, null));
		sendWhenAvailable(new MessageImpl("newObjectAvailable",
				new PropertyMap() {

					private static final long serialVersionUID = 1L;

					{
						put("nodeName", "ComponentsRepository");
						put("nodeType", "rect");
						put("nodeColor", "red");
					}
				}, null));
		sendWhenAvailable(new MessageImpl("newConnexion", new PropertyMap() {

			private static final long serialVersionUID = 1L;

			{
				put("srcNodeName", getIdName());
				put("destNodeName", "ComponentsRepository");
			}
		}, null));
		sendWhenAvailable(new MessageImpl("newConnexion", new PropertyMap() {

			private static final long serialVersionUID = 1L;

			{
				put("srcNodeName", "Component "
						+ getContainerDescription().getComponentName());
				put("destNodeName", "ComponentsRepository");
			}
		}, null));

		world.play(this);

		super.run();
	}

	private void serialize(String path, final String base_name) {
		if (path != null && base_name != null) {
			if (!path.equals("") && !path.endsWith(File.separator)) {
				path = path + File.separator;
			}
			final String lml_file = path + "boot-" + base_name + ".lml";
			final Iterator<String> iter = getLocalDS().getComponentsStarted()
					.keySet().iterator();
			while (iter.hasNext()) {
				final ContainerProxy d = getLocalDS().getComponentsStarted()
						.get(iter.next());
				if (d.getContainerDescription().getComponentName()
						.equals("lml")) {
					d.receive(new MessageImpl("save", new PropertyMap() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						{
							put("filename", lml_file);
						}
					}, null));
				}
			}
			World.createWorld(path + "world-boot-" + base_name + ".xml",
					"file:" + lml_file, getLocalDS().getComponentsLoaded());
		}
	}

	public void setLocalDS(final LocalDS localDS) {
		Activator.LOGGER.config("Setting localDS " + localDS);
		this.localDS = localDS;
		localDS.setComponentsManager(this);
	}

	protected void setWorld(final World v) {
		world = v;
	}

}
