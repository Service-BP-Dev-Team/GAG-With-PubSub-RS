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
// $Id$
package inria.lognet.virtpipes.keyvaluestore;

import inria.lognet.virtpipes.VirtPipesModule;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public class Activator implements BundleActivator {

	private VirtPipesKeyValueStore vpoc;
	private ServiceRegistration registration;

	/**
	 * When the JVM is forced to quit, try to remove registered entries by this
	 * JVM
	 */
	private Thread shutdownHook;

	public void start(final BundleContext context) throws Exception {
		vpoc = new VirtPipesKeyValueStore(context);
		registration = context.registerService(VirtPipesModule.class.getName(),
				vpoc, new Hashtable<String, Object>());
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

				}, "VirtPipesOpenChord Shutdown Hook"));
	}

	public void stop(final BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}
		registration = null;
		if (vpoc != null) {
			vpoc.close();
		}
		vpoc = null;
		if (context != null) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
			shutdownHook = null;
		}
	}

}
