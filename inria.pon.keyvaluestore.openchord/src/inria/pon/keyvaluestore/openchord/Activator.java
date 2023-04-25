package inria.pon.keyvaluestore.openchord;

import inria.pon.keyvaluestore.store.KeyValueStoreModule;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.uniba.wiai.lspi.chord.service.Chord;

/**
 * Recherche OpenChord. Ensuite, crée OpenChordKeyValueStoreModule et le publie
 * en tant que service OSGi, pour que ce module soit découvert par le
 * KeyValueStoreModuleManager.
 * 
 * @author baptiste
 */
public class Activator implements BundleActivator {

	private BundleContext context;
	private ServiceRegistration registration;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception {
		this.context = context;
		final ServiceReference[] refs = context.getAllServiceReferences(
				Chord.class.getName(), null);
		context.addServiceListener(new ServiceListener() {

			public void serviceChanged(final ServiceEvent event) {
				if (event.getType() == ServiceEvent.REGISTERED) {
					final Object o = context.getService(event
							.getServiceReference());
					if (o instanceof Chord) {
						start((Chord) o);
					}
				} else if (event.getType() == ServiceEvent.UNREGISTERING) {
					stop();
				}
			}

		}, "(objectclass=" + Chord.class.getName() + ")");
		if (refs != null && refs.length > 0 && refs[0] instanceof Chord) {
			start((Chord) refs[0]);
		}
	}

	private void start(final Chord chord) {
		registration = context.registerService(KeyValueStoreModule.class
				.getName(), new OpenChordKeyValueStoreModule(chord),
				new Hashtable<String, Object>());
	}

	private void stop() {
		if (registration != null) {
			registration.unregister();
		}
		registration = null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		this.context = null;
		stop();
	}

}
