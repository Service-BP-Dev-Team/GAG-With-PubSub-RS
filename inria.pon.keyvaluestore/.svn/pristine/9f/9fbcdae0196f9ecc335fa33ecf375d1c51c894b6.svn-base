package inria.pon.keyvaluestore;

import inria.pon.keyvaluestore.framework.KeyValueStore;
import inria.pon.keyvaluestore.impl.KeyValueStoreImpl;
import inria.pon.keyvaluestore.impl.KeyValueStoreModulesManager;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	/**
	 * Instance unique du KeyValueStore, lorsque le Bundle est lancé.
	 */
	private KeyValueStoreImpl instance = null;

	/**
	 * OSGi Registration du KeyValueStore.
	 */
	private ServiceRegistration registration = null;

	/**
	 * Thread qui est lancé lorsque la JVM est tuée.
	 */
	private Thread shutdownHook = null;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception {
		instance = new KeyValueStoreImpl(new KeyValueStoreModulesManager(
				context));
		context.registerService(KeyValueStore.class.getName(), instance,
				new Hashtable<String, Object>());
		Runtime.getRuntime().addShutdownHook(
				shutdownHook = new Thread(new Runnable() {

					public void run() {
						try {
							shutdownHook = null;
							stop(null);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}

				}, "KeyValueStore Shutdown Hook"));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}
		registration = null;
		if (instance != null) {
			instance.close();
		}
		instance = null;
		if (shutdownHook != null) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
		}
		shutdownHook = null;
	}

}
