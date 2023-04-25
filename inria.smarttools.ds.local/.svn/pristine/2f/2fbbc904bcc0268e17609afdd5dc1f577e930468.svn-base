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
// $Id: Activator.java 2884 2009-07-03 06:39:51Z bboussema $
package inria.smarttools.ds.local;

import inria.smarttools.componentsmanager.LocalDS;
import inria.smarttools.core.component.ContainerService;
import inria.smarttools.dynamic.STBundleRegistry;

import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * This class will activate the bundle, instantiate the LocalDS, register the
 * LocalDS as an OSGi service.
 * 
 * @author Baptiste.Boussemart@inria.fr
 * @version $Revision: 2884 $
 */
public class Activator implements ServiceListener, BundleActivator {

	public static BundleContext context;

	private LocalDSImpl localDS;

	public final static Logger LOGGER = Logger.getLogger("SmartTools Local DS");

	static {
		Activator.LOGGER.setParent(Logger.getLogger("SmartTools"));
	}

	public Activator() {
		localDS = new LocalDSImpl();
	}

	public void serviceChanged(final ServiceEvent event) {
		if (localDS != null) {
			final ContainerService service = (ContainerService) Activator.context
					.getService(event.getServiceReference());
			if (event.getType() == ServiceEvent.REGISTERED) {
				// register the component into the local DS
				localDS.registerComponent(service);
			} else if (event.getType() == ServiceEvent.UNREGISTERING) {
				// unregister the component into the local DS
				localDS.unregisterComponent(service.getComponentName());
			}
		}
	}

	public void start(final BundleContext context) throws Exception {
		Activator.context = context;
		// register the local DS in OSGi, so that the CM will link to it. Only
		// one local DS permitted.
		context.registerService(LocalDS.class.getName(), localDS,
				new Hashtable<String, Object>());
		// add a service listener, see serviceChanged(ServiceEvent).
		context
				.addServiceListener(this,
						"(objectclass=inria.smarttools.core.component.ContainerService)");
		// look into components that are already registered.
		final ServiceReference[] services = context
				.getAllServiceReferences(
						"inria.smarttools.core.component.ContainerService",
						"(objectclass=inria.smarttools.core.component.ContainerService)");
		if (services != null) {
			for (final ServiceReference service : services) {
				localDS.registerComponent((ContainerService) context
						.getService(service));
			}
		}
		// register this bundle into the SmartTools Bundles registry, so when
		// SmartTools will close, this Bundle will follow the lead.
		STBundleRegistry.registerBundle(context.getBundle());
	}

	public void stop(final BundleContext context) throws Exception {
		context.removeServiceListener(this);
		if (localDS != null) {
			localDS.close();
		}
		localDS = null;
		Activator.context = null;
	}
}
