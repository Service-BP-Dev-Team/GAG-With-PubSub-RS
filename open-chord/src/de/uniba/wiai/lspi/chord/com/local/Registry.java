/***************************************************************************
 *                                                                         *
 *                              Registry.java                              *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                          karsten.loesing@uni-bamberg.de                 *
 *                                                                         *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   A copy of the license can be found in the license.txt file supplied   *
 *   with this software or at: http://www.gnu.org/copyleft/gpl.html        *
 *                                                                         *
 ***************************************************************************/

package de.uniba.wiai.lspi.chord.com.local;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * This class represents a registry for {@link ThreadEndpoint endpoints}that can
 * be used to build up a chord network within the same JVM with help of Java
 * Threads. This Registry is a singleton so that there is only one instance of
 * this in the JVM. A reference to the singleton can be obtained by invocation
 * of {@link #getRegistryInstance()}.
 * 
 * @author sven
 * @version 1.0.5
 */
public final class Registry {

	/**
	 * Logger for instances of this.
	 */
	private final static Logger logger = Logger.getLogger(Registry.class
			.getName());
	/**
	 * Reference to the registry singleton.
	 */
	private static Registry registry;

	static {
		Registry.logger.setParent(Activator.LOGGER);
	}

	/**
	 * Get a reference to the registry singleton. This method is not
	 * thread-safe!
	 * 
	 * @return Reference to the registry singleton.
	 */
	public static Registry getRegistryInstance() {
		if (Registry.registry == null) {
			Registry.logger.info("Creating registry singleton. ");
			Registry.registry = new Registry();
			Registry.logger.info("Registry singleton created. ");
		}
		return Registry.registry;
	}

	// TODO Hashtable for synchronization?

	/**
	 * Stores the references to the registered {@ThreadEndpoint
	 *  endpoints} .
	 */
	private final Map<URL, ThreadEndpoint> registeredEndpoints = new HashMap<URL, ThreadEndpoint>();

	// TODO Hashtable for synchronization?

	/**
	 * Stores references to the proxies in use by nodes. Key: {@link String}
	 * (name of node), Value: {@link List} of {@link ThreadProxy proxies}.
	 */
	private final Map<URL, List<ThreadProxy>> proxiesInUse = new HashMap<URL, List<ThreadProxy>>();

	/**
	 * Create an instance of Reqistry.
	 */
	private Registry() {
	/* nothing to do here */
	}

	/**
	 * Add the given proxy used by the node with <code>nodeName</code>.
	 * 
	 * @param url
	 *            The url of the node that uses the proxy.
	 * @param proxy
	 *            The {@link ThreadProxy proxy}to add.
	 */
	public void addProxyUsedBy(final URL url, final ThreadProxy proxy) {
		if (!registeredEndpoints.containsKey(url)) {
			return;
		}
		List<ThreadProxy> pList = proxiesInUse.get(url);
		if (pList == null) {
			pList = new LinkedList<ThreadProxy>();
		}
		pList.add(proxy);
		proxiesInUse.put(url, pList);
	}

	/**
	 * Register the given {@link ThreadEndpoint endpoint}so that it can be
	 * looked up via {@link #lookup(URL)}.
	 * 
	 * @param endpoint
	 *            The {@link ThreadEndpoint}to register.
	 */
	public void bind(final ThreadEndpoint endpoint) {
		final URL name = endpoint.getURL();
		Registry.logger.info("Binding endpoint: " + endpoint + "with name "
				+ name);
		/* if there is not already an endpoint for that name */
		final Object temp = registeredEndpoints.get(name);
		if (temp == null) {
			/* bind given endpoint to name */
			registeredEndpoints.put(name, endpoint);
			Registry.logger.info("Endpoint " + endpoint + " bound.");
		} else {
			Registry.logger.warning("Endpoint " + endpoint + " NOT BOUND!!! "
					+ temp + " already registered under " + name);
		}
	}

	/**
	 * Get a reference to the {@link List}of proxies used by the node with
	 * <code>nodeName</code>.
	 * 
	 * @param nodeName
	 *            The url of the node.
	 * @return {@link List}of {@link ThreadProxy proxies}that are used by the
	 *         node with <code>nodeName</code>. May return <code>null</code> if
	 *         there are no proxies in use by the node.
	 */
	public List<ThreadProxy> getProxiesInUseBy(final URL nodeName) {
		Registry.logger.info("getProxiesInUseBy(" + nodeName + ")");
		return proxiesInUse.get(nodeName);
	}

	/**
	 * This method looks up the {@link ThreadEndpoint endpoint}for the node with
	 * the given name. If no endpoint is found <code>null</code> is returned.
	 * 
	 * @param url
	 *            The url of the node, for which the {@link ThreadEndpoint
	 *            endpoint}is looked up.
	 * @return Reference to the {@link ThreadEndpoint endpoint}of node with name
	 *         "<code>name</code>". May be <code>null</code> if no such
	 *         reference exists.
	 */
	public ThreadEndpoint lookup(final URL url) {
		Registry.logger.info("Looking up endpoint for " + url);
		final ThreadEndpoint ep = registeredEndpoints.get(url);
		Registry.logger.info("Endpoint for " + url + ": " + ep);
		return ep;
	}

	/**
	 * Returns an array of all registered endpoints.
	 * 
	 * @return Array of all registered {@link ThreadEndpoint endpoints}. If no
	 *         endpoint is registered an array of length 0 is returned.
	 */
	public Map<URL, ThreadEndpoint> lookupAll() {
		return registeredEndpoints;
		/*
		 * synchronized (this.registeredEndpoints){ ThreadEndpoint[] endpoints =
		 * new ThreadEndpoint[registeredEndpoints.size()]; Iterator iterator =
		 * this.registeredEndpoints.values().iterator(); int index = 0; while
		 * (iterator.hasNext()) { endpoints[index] =
		 * (ThreadEndpoint)iterator.next(); index++; } return endpoints; }
		 */
	}

	/**
	 * Removes the {@link ThreadProxy proxies} used by the node with
	 * <code>nodeName</code>.
	 * 
	 * @param nodeName
	 *            The url of the node, for that the proxies should be removed.
	 */
	public void removeProxiesInUseBy(final URL nodeName) {
		Registry.logger.info("removeProxiesInUseBy(" + nodeName + ")");
		proxiesInUse.remove(nodeName);
	}

	/**
	 * Shutdown this registry.
	 */
	public void shutdown() {
		for (final ThreadEndpoint endpoint : registeredEndpoints.values()) {
			endpoint.crash();
		}
	}

	/**
	 * Overwritten from {@link java.lang.Object}.
	 * 
	 * @return String representation of this.
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[Singleton registry of ThreadEndpoints ");
		buffer.append("with ");
		buffer.append(registeredEndpoints.size());
		buffer.append(" endpoints registered]");
		return buffer.toString();
	}

	/**
	 * Remove the given {@link ThreadEndpoint endpoint}from the registry.
	 * 
	 * @param endpoint
	 *            {@link ThreadEndpoint}to remove.
	 */
	public void unbind(final ThreadEndpoint endpoint) {
		Registry.logger.info("Unbinding endpoint: " + endpoint);
		registeredEndpoints.remove(endpoint.getURL());
		Registry.logger
				.info("Endpoint " + endpoint + " removed from registry.");
	}
}
