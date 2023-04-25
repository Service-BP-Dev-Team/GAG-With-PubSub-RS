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
// $Id: Activator.java 2465 2008-10-08 08:56:22Z bboussema $
package inria.smarttools.ds.pon;

import inria.lognet.virtpipes.VirtPipesService;
import inria.smarttools.componentsmanager.RemoteDS;
import inria.smarttools.core.component.ContainerProxy;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * OSGi Activator (start and stop) Look for OpenChord and VirtPipesService, then
 * start effectively the Bundle, and registers the PON DS.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @revision $Revision: 2465 $
 */
public class Activator implements BundleActivator, ServiceListener {

	/**
	 * Present when the Bundle is started
	 */
	private static Activator instance = null;

	public static Activator getInstance() {
		return Activator.instance;
	}

	/**
	 * OSGi logic for PON DS : add and remove of components (osgi services)
	 */
	private final PonDsLogic logic = new PonDsLogic();

	public static final Logger LOGGER = Logger.getLogger("SmartTools PON DS");

	static {
		Activator.LOGGER.setParent(Logger.getLogger("SmartTools"));
	}

	private BundleContext context;

	/**
	 * Store of the components, create proxies, connect local components to
	 * these proxies
	 */
	private PonDs piNetDS;

	/**
	 * Key-Values storage
	 */
	private Chord chord = null;

	/**
	 * Communication channel manager
	 */
	private VirtPipesService vps = null;

	/**
	 * Ticket for the fact we registered PON DS as an OSGi service
	 */
	private ServiceRegistration registration;

	public BundleContext getContext() {
		return context;
	}

	public Chord getDHT() {
		return chord;
	}

	public PonDs getDS() {
		return piNetDS;
	}

	public PonDsLogic getLogic() {
		return logic;
	}

	public VirtPipesService getVirtPipesService() {
		return vps;
	}

	/**
	 * Listen to VirtPipesService and OpenChord
	 */
	public void serviceChanged(final ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			final Object o = context.getService(event.getServiceReference());
			if (o != null && o instanceof Chord) {
				chord = (Chord) o;
				if (vps != null && chord != null) {
					try {
						start();
						context.removeServiceListener(this);
					} catch (final IOException e) {
						e.printStackTrace();
					} catch (final InvalidSyntaxException e) {
						e.printStackTrace();
					}
				}
			} else if (o != null && o instanceof VirtPipesService) {
				vps = (VirtPipesService) o;
				if (vps != null && chord != null) {
					try {
						start();
						context.removeServiceListener(this);
					} catch (final IOException e) {
						e.printStackTrace();
					} catch (final InvalidSyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
			if (Chord.class.getName().equals(
					event.getServiceReference().getProperty("object_class"))) {
				Logger.global
						.info("PON is notified that Chord is now shutdown");
				chord = null;
				try {
					unregister();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			} else if (VirtPipesService.class.getName().equals(
					event.getServiceReference().getProperty("object_class"))) {
				Logger.global
						.info("PON is notified that VirtPipesService is now shutdown");
				vps = null;
				try {
					unregister();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Invoked when Open-Chord and VirtPipesService are present
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	private void start() throws IOException, InvalidSyntaxException {
		piNetDS = new PonDs(chord, vps); // create PON DS
		registration = context.registerService(RemoteDS.class.getName(),
				piNetDS, new Hashtable<String, Object>()); // register PON DS as RemoteDS
		// add PonDsLogic as component listener
		context.addServiceListener(getLogic(), "(FILTER=*)");
		// search for components and notify PonDsLogic
		final ServiceReference[] references = context.getServiceReferences(
				ContainerProxy.class.getName(), "(FILTER=*)");
		Activator.LOGGER.info("starting registering proxies");
		if (references != null) {
			for (final ServiceReference reference : references) {
				logic.registerContainerProxy((ContainerProxy) context
						.getService(reference));
			}
		}
		Activator.LOGGER.info("registering proxies OK");
	}

	public void start(final BundleContext context) throws Exception {
		Activator.instance = this;
		this.context = context;
		{
			final ServiceReference ref = context
					.getServiceReference(Chord.class.getName());
			try {
				chord = (Chord) context.getService(ref);
			} catch (final Exception e) {
				context.addServiceListener(this);
			}
		}
		{
			final ServiceReference ref = context
					.getServiceReference(VirtPipesService.class.getName());
			try {
				vps = (VirtPipesService) context.getService(ref);
			} catch (final Exception e) {
				context.addServiceListener(this);
			}
		}
		if (chord != null && vps != null) {
			start();
		}
	}

	public void stop(final BundleContext context) throws Exception {
		unregister();
		Activator.instance = null;
	}

	/**
	 * Invoked when either Open-Chord or VirtPipesService disappear
	 * 
	 * @throws ServiceException
	 * @throws IOException
	 */
	private void unregister() throws ServiceException, IOException {
		context.removeServiceListener(getLogic());
		if (registration != null) {
			registration.unregister();
		}
		registration = null;
		if (piNetDS != null) {
			piNetDS.close();
		}
		piNetDS = null;
	}
}
