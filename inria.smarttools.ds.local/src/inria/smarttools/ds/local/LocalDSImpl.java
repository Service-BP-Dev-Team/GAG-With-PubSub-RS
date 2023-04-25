/*******************************************************************************
 * Copyright (c) 2000, 2009 INRIA.
 * 
 *    This file is part of SmartTools project
 *    <a href="http://www-sop.inria.fr/teams/lognet/pon/">SmartTools</a>
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
// $Id: LocalDSImpl.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.ds.local;

import inria.smarttools.componentsmanager.Activator;
import inria.smarttools.componentsmanager.ComponentsManager;
import inria.smarttools.componentsmanager.ConnectToAll;
import inria.smarttools.componentsmanager.LocalDS;
import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.ContainerService;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.ParserDef;
import inria.smarttools.core.component.PropertyMap;
import inria.smarttools.core.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.Bundle;

/**
 * This class describes two tables, one for the components (types) and the other
 * for the components instances. It is linked to the CM by the OSGi service
 * system. It offers a way to create component instances from a component, and
 * to connect locally component instances each other.
 * 
 * @author Baptiste.Boussemart@inria.fr
 * @version $Revision: 2819 $
 */
public class LocalDSImpl implements LocalDS {

	/**
	 * A tuple for a connectToAll delegation made by the CM.
	 */
	private class ConnectToAllCommand {

		Container source;
		String typeDestination;

		public ConnectToAllCommand(final String typeDestination,
				final Container source) {
			this.typeDestination = typeDestination;
			this.source = source;
		}
	}

	/**
	 * A weak link to the CM, so that the CM can disapear easily.
	 */
	private ComponentsManager cm;

	/**
	 * Components.
	 */
	private final Map<String, ContainerService> componentsLoaded = new HashMap<String, ContainerService>();

	/**
	 * Components instances.
	 */
	private final Map<String, Container> componentsStarted = new HashMap<String, Container>();

	/**
	 * List of the connectToAll commands delegated by the CM.
	 */
	private final List<ConnectToAllCommand> connectToAllCommands = new Vector<ConnectToAllCommand>();

	/**
	 * This method is used for starting or installing a bundle that represent a
	 * component. It depends on the name of the component, but also the version
	 * of the Bundle. If a Bundle is already installed, it is easy, but if the
	 * Bundle is not already installed, it is very hard to find the component
	 * automatically. The component name can be equals to the bundle name, or
	 * can be the end of the bundle name (so that you can specify a longer
	 * package-like name). If you want to be sure not losing time or your
	 * component is taken by the platform, be sure you start your component.
	 * (Add a component represented by a ".jar" and a decription file used to
	 * describe it. It is used for starting a bundle that represents a Bundle.)
	 * 
	 * @param jarName
	 *            a <code>String</code> value : jar name
	 * @param componentName
	 *            a <code>String</code> value : compnent name
	 * @param uriToJar
	 *            a <code>String</code> value : uri to the component
	 */
	public void addComponent(final String jarName, final String componentName,
			final String uriToJar) {
		try {
			Bundle cmp = null;
			final Bundle[] bundles = Activator.context.getBundles();
			for (int i = 0; i < bundles.length; ++i) {
				if (bundles[i].getSymbolicName() != null
						&& bundles[i].getSymbolicName().equals(componentName)) {
					cmp = bundles[i];
					break;
				}
			}
			if (cmp == null) {
				for (int i = 0; i < bundles.length; ++i) {
					if (bundles[i].getSymbolicName() != null
							&& bundles[i].getSymbolicName().endsWith(
									"." + componentName)) {
						cmp = bundles[i];
						break;
					}
				}
			}
			if (cmp == null && uriToJar != null) {
				if (new File(uriToJar).exists()) {
					cmp = Activator.context.installBundle(uriToJar);
				}
			}
			if (cmp != null && cmp.getState() != Bundle.ACTIVE) {
				cmp.start();
			}
		} catch (final Exception e) {
			if (cm != null) {
				cm.logERROR("Cannot add components " + componentName);
			}
			inria.smarttools.ds.local.Activator.LOGGER
					.throwing(
							getClass().getName(),
							"void addComponent(String jarName, String componentName, String uriToJar)",
							e);
		}
	}

	/**
	 * Used by the CM for delegating a connectToAll.
	 */
	public void addConnectToAll(final ConnectToAll connectToAll) {
		inria.smarttools.ds.local.Activator.LOGGER.info("connectToAll "
				+ connectToAll.getTypeSource() + ", "
				+ connectToAll.getTypeDestination() + " "
				+ connectToAll.getIdSource());
		// add for further notifications
		connectToAllCommands.add(new ConnectToAllCommand(connectToAll
				.getTypeDestination(), getContainerProxy(connectToAll
				.getIdSource())));

		// to it for all started components now
		final Iterator<Container> it = componentsStarted.values().iterator();
		while (it.hasNext()) {
			final Container destination = it.next();
			try {
				if (destination.getContainerDescription().getComponentName()
						.equals(connectToAll.getTypeDestination())) {
					connectComponent(getContainerProxy(connectToAll
							.getIdSource()), destination);
				}
			} catch (final NullPointerException e) {
				// ok it's the CM
			}
		}
	}

	/**
	 * Close the localDS, used by the LocalDS's Activator. (as the LocalDS is
	 * registered into the STBundleRegistry, it will be automatically closed
	 * when the CM stops)
	 */
	public void close() {
		cm = null;
		componentsLoaded.clear();
		componentsStarted.clear();
		connectToAllCommands.clear();
	}

	/**
	 * Connect source and dest component by exchanging "knowledge".
	 * 
	 * @param source
	 *            a <code>Container</code> value
	 * @param dest
	 *            a <code>Container</code> value
	 */
	private void connectComponent(final Container source, final Container dest) {

		inria.smarttools.ds.local.Activator.LOGGER.info("newConnexion "
				+ source.getIdName() + " -> " + dest.getIdName());
		cm.sendWhenAvailable(new MessageImpl("newConnexion", new PropertyMap() {

			private static final long serialVersionUID = 1L;

			{
				put("srcNodeName", source.getIdName());
				put("destNodeName", dest.getIdName());
			}
		}, null));

		source.connect(dest);
		dest.connect(source);
		if (source instanceof AbstractContainer && dest instanceof AbstractContainer){
			((AbstractContainer) source).CheckNeighbors((AbstractContainer) dest);
			((AbstractContainer) dest).CheckNeighbors((AbstractContainer) source);
		}
	}

	/**
	 * Called by the CM when it orders a connectToOne connection.
	 */
	public void connectToOne(final String idSource,
			final String typeDestination, final String idDestination,
			PropertyMap actions) {
		Container destination = getContainerProxy(idDestination);
		final Container source = getContainerProxy(idSource);
		boolean creating = false;

		if (destination == null) { // creation of the component destination
			if (actions == null) {
				actions = new PropertyMap();
			}
			destination = startComponent(idDestination, typeDestination,
					actions, true);
			if (!idSource.equals("ComponentsManager")) {
				connectToOne("ComponentsManager", typeDestination, destination
						.getIdName(), null);
			}
			creating = true;
		}

		if (source != null && destination != null) {
			connectComponent(source, destination);
		} else {
			return;
		}

		if (actions != null) {
			final PropertyMap attrs = actions.getPropertyMap("attributes");
			if (attrs != null) {
				final Iterator<String> iter = attrs.stringsKeySet().iterator();
				while (iter.hasNext()) {
					final String key = iter.next();
					final String type = attrs.getString(key).getClass()
							.getName();
					destination.setAttribute(key, attrs.getString(key), type);
				}
			}
		}

		if (creating) {
			destination.switchOn();
		}

		if (actions != null) {
			int j = 0;
			Message afterConnect = null;
			while ((afterConnect = actions.getAfterConnect("afterConnect" + j)) != null) {
				afterConnect.setExpeditor(source);
				destination.receive(afterConnect);
				j++;
			}
		}

		// destination.releaseWaitingMsg();
		// source.releaseWaitingMsg();
	}

	/**
	 * SmartTools Eclipse use. Finds the component that corresponds to the
	 * document.
	 * 
	 * @param info
	 * @return
	 */
	private String foundComponentType(final String info) {
		final String res = null;
		int pos = info.lastIndexOf(":"); // bypass document and file ??
		final String name = info.substring(pos + 1);

		pos = name.lastIndexOf(".");
		final String ext = name.substring(pos + 1);

		ComponentDescription data;

		final Iterator<String> iter = componentsLoaded.keySet().iterator();
		while (iter.hasNext()) {
			final String cmpName = iter.next();

			data = componentsLoaded.get(cmpName).getComponentDescription();

			if (data.getComponentType().equals("document")) {
				final Map<String, ?> parsers = data.getParsers();
				final Iterator<String> iterParsers = parsers.keySet()
						.iterator();
				while (iterParsers.hasNext()) {
					final ParserDef pd = (ParserDef) parsers.get(iterParsers
							.next());
					if (pd.getExtentions().contains(ext)) {
						inria.smarttools.ds.local.Activator.LOGGER
								.info("YEPP, I found component "
										+ data.getComponentName());
						return data.getComponentName();
					}
				}
			}
		}
		return res;
	}

	/**
	 * Used by RemoteDS
	 * 
	 * @return the CM
	 */
	public ComponentsManager getCm() {
		return cm;
	}

	/**
	 * Asked by the CM, the localDS looks for the component instance and reply
	 * if the is has the component or not. But, if the localDS doesn't have the
	 * component instance, and has the component type, it replies that it has
	 * the component type. If both is not filled, it replies null.
	 */
	public void getComponent(final String type, final String id,
			final Long responseId) {
		try {
			if (componentsStarted.containsKey(id)) {
				Activator.LOGGER.finest(id + " is in the componentsStarted");
				cm.notifyComponent(id, this, null, responseId);
			} else if (componentsLoaded.containsKey(type)) {
				Activator.LOGGER
						.finest(id
								+ " is not started yet, but will be as I have its definition");
				cm.notifyComponent("TYPE@" + type, this, null, responseId);
			} else {
				Activator.LOGGER.finest("I don't have the definition of " + id);
				addComponent(type + "_" + cm.version + ".jar", type, "plugins/"
						+ type + "_" + cm.version + ".jar");
				if (componentsLoaded.containsKey(type)) {
					cm.notifyComponent("TYPE@" + type, this, null, responseId);
				} else {
					cm.notifyComponent(null, this, null, responseId);
				}
			}
		} catch (final Throwable e) {
			e.printStackTrace();
			cm.notifyComponent(null, this, null, responseId);
		}
	}

	/**
	 * Return the components types. Be careful you have full access to the Map!
	 */
	public Map<String, ContainerService> getComponentsLoaded() {
		return componentsLoaded;
	}

	/**
	 * Return the components instances. Be careful you have full access to the
	 * Map!
	 */
	public Map<String, Container> getComponentsStarted() {
		return componentsStarted;
	}

	/**
	 * Get a local component instance from its name.
	 */
	public Container getContainerProxy(final String id) {
		return componentsStarted.get(id);
	}

	/**
	 * Asked by the CM, if any instance of a type exist, the localDS answers the
	 * first instance found.
	 */
	public void getFirstOfType(final String type, final Long responseId) {
		final Iterator<Container> it = componentsStarted.values().iterator();
		while (it.hasNext()) {
			final ContainerProxy containerProxy = it.next();
			if (!containerProxy.getIdName().equals("ComponentsManager")
					&& containerProxy.getContainerDescription()
							.getComponentName().equals(type)) {
				cm.notifyComponent(containerProxy.getIdName(), this, null,
						responseId);
				return;
			}
		}
		cm.notifyComponent(null, this, null, responseId);
	}

	/**
	 * Start (instanciate) a component of a given type. This is the way to start
	 * a component instance from a component type.
	 * 
	 * @param instance_id
	 *            a <code>String</code> value : instance name
	 * @param type
	 *            a <code>String</code> value : component type
	 * @return a <code>Container</code> value : the component or null
	 */
	public Container internalStartComponent(final String instance_id,
			final String type) {
		final ContainerService cmp = getComponentsLoaded().get(type);
		if (cmp != null) {
			final Container proxy = cmp.createComponent(instance_id);
			registerNewComponentInstance(proxy, cmp.getComponentDescription());
			proxy.switchOn();
			return proxy;
		} else {
			cm.logERROR("Unable to find component " + type
					+ " in component repository");
			return null;
		}
	}

	/**
	 * Used for checking if a component is local, by the CM. The basic rule is
	 * that the component source in a connectTo is local.
	 */
	public boolean isLocal(final String idSource) {
		return componentsStarted.containsKey(idSource);
	}

	/**
	 * IMPLEMENTATION OF SERVICE <newComponent> A new (delocalized) component
	 * want to be registered. If any component instance had been instantiated
	 * anywhere else, this method is called. This is a SmartTools way to do
	 * that. You can have the OSGi way also.
	 */
	public synchronized void newComponentInstance(final Container c,
			final String c_name, final String file_name) {
		cm.logINFO("A new component is available " + c_name);
		ComponentDescription cmp_desc = null;
		if (c_name == null) {
			cmp_desc = c.getContainerDescription();
		} else {
			try {
				cmp_desc = getComponentsLoaded().get(c_name)
						.getComponentDescription();
			} catch (final NullPointerException e) {
				addComponent(c_name + "_" + cm.version + ".jar", c_name,
						"plugins/" + c_name + "_" + cm.version + ".jar");

				cmp_desc = getComponentsLoaded().get(c_name)
						.getComponentDescription();
			}

			getComponentsLoaded().get(c_name).createComponent(c, c_name,
					file_name);
		}

		registerNewComponentInstance(c, cmp_desc);
		//
		// connect to CM
		//
		connectComponent(cm, c);

		c.switchOn();

		notifyConnectToAll(c);
	}

	/**
	 * It is an internal notification for connectToAll commands.
	 * 
	 * @param destination
	 */
	private void notifyConnectToAll(final Container destination) {
		final Iterator<ConnectToAllCommand> it = connectToAllCommands
				.iterator();
		while (it.hasNext()) {
			final LocalDSImpl.ConnectToAllCommand connectToAllCommand = it
					.next();
			if (connectToAllCommand.typeDestination.equals(destination
					.getContainerDescription().getComponentName())) {
				cm.logINFO("notifyConnectToAll is going to connect "
						+ connectToAllCommand.source.getIdName() + " to "
						+ destination.getIdName());

				connectComponent(connectToAllCommand.source, destination);
			}
		}
	}

	/**
	 * Add a component type, if it is not already done.
	 * 
	 * @param component
	 */
	public void registerComponent(final ContainerService component) {
		synchronized (componentsLoaded) {
			if (!componentsLoaded.containsKey(component.getComponentName())) {
				componentsLoaded.put(component.getComponentName(), component);
				if (cm != null) {
					cm.notifyComponent(component.getComponentName(), this,
							null, null);
				}
			}
		}
	}

	/**
	 * Register a new component i.e : - declare it as started - register it on
	 * the <world> (send a newComponentAvailable msg) - register spies on it -
	 * eventually set debug state
	 * 
	 * @param c
	 *            a <code>Container</code> value
	 * @param cmp
	 *            a <code>ComponentDescription</code> value
	 */
	public void registerNewComponentInstance(final Container c,
			final ComponentDescription cmp) {
		getComponentsStarted().put(c.getIdName(), c);
		cm.sendWhenAvailable(new MessageImpl("newComponentInstanceAvailable",
				new PropertyMap() {

					private static final long serialVersionUID = 1L;

					{
						put("nodeName", c.getIdName());
						put("nodeType", "oval");
						put("nodeColor", "yellow");
					}
				}, null));

		final Iterator<String> it = cm.getSpies().iterator();
		while (it.hasNext()) {
			final String input = it.next();
			final Container spy = getComponentsStarted().get(input);
			if (spy != null) {
				connectComponent(spy, c);
			}
		}
	}

	/**
	 * For SmartTools Eclipse only. The way to request the document's component
	 * instance.
	 */
	public Message requestFile(final String requestedFilename) {
		final String cmpType = foundComponentType(requestedFilename);
		if (cmpType != null) {
			return requestFileImpl(requestedFilename);
		} else {
			final String cmpName = StringUtil.extension(requestedFilename);
			addComponent(null, cmpName, null);
			synchronized (this) {
				try {
					Thread.sleep(5000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			return requestFileImpl(requestedFilename);
		}
	}

	/**
	 * For SmartTools Eclipse only. Internal implementation to request a
	 * document's component instance.
	 */
	private Message requestFileImpl(final String requestedFilename) {
		final String cmpType = foundComponentType(requestedFilename);
		if (cmpType != null) {
			ComponentDescription cmpDefinition = null;
			// if (remoteComponentsLoaded.get(cmpType)!= null)
			// cmpDefinition = remoteComponentsLoaded.get(cmpType);
			// else
			cmpDefinition = componentsLoaded.get(cmpType)
					.getComponentDescription();
			if (cmpDefinition != null) {
				final Map<String, String> lmls = cmpDefinition.getLmls();
				final String lml = "DEFAULT";
				// if (lmls.size() > 1) {
				// Object[] possibleValues = lmls.keySet().toArray();
				// try {
				// lml = (String) JOptionPane.showInputDialog(null, "More
				// than one lml found for "
				// + cmpType + ", choose one", "Choose Lml",
				// JOptionPane.WARNING_MESSAGE,
				// null, possibleValues, "DEFAULT");
				// } catch (IllegalArgumentException e) {
				// lml = "DEFAULT";
				// }
				// }
				final String lmlFile = lmls.get(lml);
				return new MessageImpl("processLmlSkeleton", new PropertyMap() {

					private static final long serialVersionUID = 1L;

					{
						put("skeleton", lmlFile);
						put("arguments", new PropertyMap() {

							private static final long serialVersionUID = 1L;

							{
								if (requestedFilename.startsWith("file:")
										|| requestedFilename
												.startsWith("workspace:")) {
									put("filename", requestedFilename);
								} else {
									put("filename", "file:" + requestedFilename);
								}
							}
						});
					}
				}, null);
			}
		} else {
			// add to a list
		}
		return null;
	}

	/**
	 * CM set by the CM itself.
	 */
	public void setComponentsManager(final ComponentsManager cm) {
		this.cm = cm;
	}

	/**
	 * CM's service implementation.
	 */
	public void sleepComponent(final String instance_id) {
		final Container cmp = getComponentsStarted().get(instance_id);
		if (cmp != null) {
			cmp.hibernate();
		} else {
			cm.logERROR("Unable to find component named " + instance_id
					+ " in component repository");
		}
	}

	/**
	 * CM's service implementation.
	 */
	public Container startComponent(final String componentId,
			final String componentType, final PropertyMap actions,
			final boolean allInit) {
		Container c = null;
		if (getComponentsStarted().get(componentId) != null) {
			return getComponentsStarted().get(componentId);
		}
		PropertyMap attrs = actions.getPropertyMap("attributes");
		if (componentId.startsWith("file:")
				|| componentId.startsWith("resources:")
				|| componentId.startsWith("workspace:")) {
			if (attrs == null) {
				attrs = new PropertyMap();
				actions.put("attributes", attrs);
			}
			attrs.put("urlToSource", componentId);
		}
		if (!componentsLoaded.containsKey(componentType)) {
			addComponent(componentType + ".jar", componentType, "file:"
					+ cm.world.getRepository() + componentType + ".jar");
		}
		c = internalStartComponent(componentId, componentType);
		if (allInit) {
			connectToOne(cm.getIdName(), componentType, c.getIdName(), null);
			if (actions != null) {
				if (attrs != null) {
					final Iterator<String> it = attrs.stringsKeySet()
							.iterator();
					while (it.hasNext()) {
						final String key = it.next();
						final String type = attrs.getString(key).getClass()
								.getName();
						c.setAttribute(key, attrs.getString(key), type);
					}
				}
			}
		}
		c.switchOn();
		notifyConnectToAll(c);
		return c;
	}

	/**
	 * CM's service implementation.
	 */
	public void stopComponent(final String instance_id) {
		final Container cmp = getComponentsStarted().get(instance_id);
		if (cmp != null) {
			cmp.switchOff();
		} else {
			cm.logERROR("Unable to find component named " + instance_id
					+ " in component repository");
		}
	}

	/**
	 * Called by the ContainerService tracker when a component disappear.
	 */
	public void unregisterComponent(final String componentName) {
		synchronized (componentsLoaded) {
			componentsLoaded.remove(componentName);
		}
	}

	/**
	 * CM's service implementation.
	 */
	public void wakeupComponent(final String instance_id) {
		final Container cmp = getComponentsStarted().get(instance_id);
		if (cmp != null) {
			cmp.wakeup();
		} else {
			cm.logERROR("Unable to find component named " + instance_id
					+ " in component repository");
		}
	}
}
