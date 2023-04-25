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
// $Id: Activator.java 2884 2009-07-03 06:39:51Z bboussema $
package inria.smarttools.componentsmanager;

import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.util.STResources;
import inria.smarttools.dynamic.DocumentExtensionsRegistry;
import inria.smarttools.dynamic.STBundleRegistry;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Activator of Component Manager <li>read worldfile <li>add service bundle
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2884 $
 */
public class Activator implements BundleActivator {

	public final static Logger LOGGER = Logger
			.getLogger("SmartTools Components Manager");

	public static BundleContext context = null;

	private static ComponentsManager cm = null;

	static {
		Activator.LOGGER.setParent(Logger.getLogger("SmartTools"));
		Logger.getLogger("SmartTools").setLevel(Level.FINEST);
	}

	protected Hashtable<String, Object> props = null;

	private final ServiceListener loggerHandlerServiceListener = new ServiceListener() {

		public void serviceChanged(final ServiceEvent event) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				final Object o = Activator.context.getService(event
						.getServiceReference());
				if (o.getClass().getName().equals(
						"loggerviewer.LoggerViewerHandler")) {
					Logger.getLogger("SmartTools").removeHandler(
							Logger.getLogger("SmartTools").getHandlers()[0]);
					Logger.getLogger("SmartTools").addHandler((Handler) o);
				}
			}
		}
	};

	void connect(final Object other) {}

	void disconnect(final Object other) {}

	void loadWorld() throws Exception {
		if (!World.exists()) {
			final Map<String, String> params = new HashMap<String, String>();
			String worldfile = System.getProperty("smarttools.worldfile");
			String layoutfile = System.getProperty("smarttools.layoutfile");

			if (worldfile == null) {
				worldfile = STResources.getProperty("SmartTools.cm.worldFile");
			}
			if (layoutfile == null) {
				layoutfile = STResources
						.getProperty("SmartTools.cm.layoutFile");
				params.put("filename", layoutfile);
			}
			if (layoutfile.equals("")) {
				layoutfile = STResources
						.getProperty("SmartTools.cm.layoutFile");
				params.put("filename", layoutfile);
			} else {
				params.put("filename", "file:" + layoutfile);
			}
			World.getInstance().setWorldFile(worldfile);
			World.getInstance().setParams(params);
			World.getInstance().loadWorld();
			Activator.LOGGER.info("Param: " + worldfile + "," + layoutfile);
		}
		Activator.cm.setWorld(World.getInstance());
	}

	public void start(final BundleContext context) throws Exception {
		try {
			Activator.context = context;

			// Setting general SmartTools' Loggger's level
			{
				final Logger logger = Logger.getLogger("SmartTools");
				final String level = Activator.context
						.getProperty("inria.smarttools.logger.level");
				if (level != null) {
					logger.setLevel(Level.parse(level));
				} else {
					logger.setLevel(Level.FINEST);
				}
			}

			// See LoggerViewer http://nil.boussemart.com/loggerviewer
			{
				Logger.getLogger("SmartTools").setUseParentHandlers(false);
				Handler handler;
				final ServiceReference svc = Activator.context
						.getServiceReference("loggerviewer.LoggerViewerHandler");
				if (svc != null
						&& (handler = (Handler) Activator.context
								.getService(svc)) != null) {
					Logger.getLogger("SmartTools").addHandler(handler);
				} else {
					Logger.getLogger("SmartTools").addHandler(
							new ConsoleHandler() {

								{
									setFormatter(new LogFormatter());
								}
							});
					Activator.context.addServiceListener(
							loggerHandlerServiceListener,
							"(objectclass=loggerviewer.LoggerViewerHandler)");
				}
			}

			Activator.LOGGER.info("Starting Component Manager....");

			DocumentExtensionsRegistry.getInstance().registerExtension("lml",
					"lml");

			Activator.cm = new ComponentsManager();

			{
				boolean found = false;
				while (!found) {
					final ServiceReference[] services = Activator.context
							.getAllServiceReferences(null, null);
					if (services != null && services.length > 0) {
						for (final ServiceReference service : services) {
							final Object o = Activator.context
									.getService(service);
							if (o instanceof LocalDS) {
								Activator.cm.setLocalDS((LocalDS) o);
								found = true;
								break;
							}
						}
					}
					{
						final Bundle[] bundles = Activator.context.getBundles();
						for (final Bundle bundle : bundles) {
							if (bundle.getSymbolicName().contains(
									"inria.smarttools.ds.local")) {
								try {
									bundle.start();
								} catch (final BundleException e) {
									e.printStackTrace();
								}
								break;
							}
						}
					}
				}
			}

			loadWorld();

			props = new Hashtable<String, Object>();
			props.put("FILTER", ",ComponentsManager");
			Activator.context.registerService(ContainerProxy.class.getName(),
					Activator.cm, props);

			STBundleRegistry.registerBundle(Activator.context.getBundle());

			final ServiceReference[] services = Activator.context
					.getAllServiceReferences(RemoteDS.class.getName(),
							"(objectclass=inria.smarttools.componentsmanager.RemoteDS)");
			if (services != null) {
				for (final ServiceReference service : services) {
					Activator.LOGGER.info("RDS added");
					Activator.cm.addRemoteDS((RemoteDS) Activator.context
							.getService(service));
				}
			}
			Activator.context.addServiceListener(new ServiceListener() {

				public void serviceChanged(final ServiceEvent event) {
					try {
						if (event.getType() == ServiceEvent.REGISTERED) {
							Activator.LOGGER.info("RDS added");
							Activator.cm
									.addRemoteDS((RemoteDS) Activator.context
											.getService(event
													.getServiceReference()));
						} else if (event.getType() == ServiceEvent.UNREGISTERING) {
							Activator.cm
									.removeRemoteDS((RemoteDS) Activator.context
											.getService(event
													.getServiceReference()));
						}
					} catch (final ClassCastException e) {
						// normal
					}
				}

			}, "(objectclass=inria.smarttools.componentsmanager.RemoteDS)");

			Activator.cm.switchOn();
		} catch (final Exception e) {
			stop(context);
			throw e;
		}
	}

	public void stop(final BundleContext context) throws Exception {
		STBundleRegistry.removeBundle(context.getBundle());
		if (Activator.cm != null) {
			Activator.cm.exit();
		}
		Activator.context = null;
		Activator.cm = null;
		if (props != null) {
			props.clear();
		}
		props = null;

		context.removeServiceListener(loggerHandlerServiceListener);

		World.dispose();

		final Handler[] handlers = Logger.getLogger("SmartTools").getHandlers();
		for (final Handler handler : handlers) {
			Logger.getLogger("SmartTools").removeHandler(handler);
		}

	}
}
