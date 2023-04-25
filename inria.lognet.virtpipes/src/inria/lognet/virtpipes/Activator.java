/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
 * 
 *    This file is part of LogNet project
 *    <a href="http://www-sop.inria.fr/teams/lognet">LogNet</a>
 * 
 *     LogNet is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     LogNet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with LogNet; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: Activator.java 2891 2009-07-20 14:01:58Z bboussema $
package inria.lognet.virtpipes;

import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Activator of the VirtPipesService module.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2891 $
 */
public class Activator implements BundleActivator {

	public static final Logger LOGGER = Logger.getLogger("LogNet VirtPipes");
	static {
		Activator.LOGGER.setLevel(Level.FINEST);
	}

	private BundleContext context;
	private VirtPipesService vps;
	private Thread thread;

	private ServiceRegistration registration;

	private Thread shutdownHook;

	private void start() throws Exception {
		final String port = context.getProperty("inria.lognet.virtpipes.port");
		vps = new VirtPipesService(port == null ? 0 : Integer.valueOf(port),
				context);
		(thread = new Thread(vps.getServerSocket().getChannelsPool(),
				"VirtPipes ChannelsPool")).start();
		registration = context.registerService(
				VirtPipesService.class.getName(), vps, new Hashtable<String, Object>());
	}

	public void start(final BundleContext context) throws Exception {
		{ // Add LoggerViewer Handler
			final ServiceReference ref = context
					.getServiceReference("loggerviewer.LoggerViewerHandler");
			if (ref != null) {
				final Object o = context.getService(ref);
				if (o != null) {
					Activator.LOGGER.setUseParentHandlers(false);
					Activator.LOGGER.addHandler((Handler) o);
				}
			} else {
				context.addServiceListener(new ServiceListener() {

					public void serviceChanged(final ServiceEvent event) {
						if (event.getType() == ServiceEvent.REGISTERED) {
							final Object o = context.getService(event
									.getServiceReference());
							if (o.getClass().getName().equals(
									"loggerviewer.LoggerViewerHandler")) {
								Activator.LOGGER.setUseParentHandlers(false);
								Activator.LOGGER.addHandler((Handler) o);
							}
						}
					}

				}, "(objectclass=loggerviewer.LoggerViewerHandler)");
			}
		} // End add LoggerViewer Handler
		this.context = context;
		Runtime.getRuntime().addShutdownHook(
				shutdownHook = new Thread(new Runnable() {

					public void run() {
						try {
							context.getBundle().stop();
						} catch (final BundleException e) {
							try {
								stop(null);
							} catch (final Exception e1) {}
						}
					}

				}, "VirtPipes Shutdown Hook"));
		start();
	}

	public void stop(final BundleContext context) throws Exception {
		if (context != null) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
			shutdownHook = null;
		}
		if (vps != null) {
			vps.close();
			vps = null;
		}
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
		if (Activator.LOGGER.getHandlers()[0] != null) {
			Activator.LOGGER.removeHandler(Activator.LOGGER.getHandlers()[0]);
		}
	}
}
