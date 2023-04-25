package inria.pon.keyvaluestore.impl;

import inria.pon.keyvaluestore.impl.local.LocalEntries;
import inria.pon.keyvaluestore.store.KeyValueStoreModule;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Manages the KeyValueStoreModules.
 * 
 * @author baptisteboussemart@gmail.com
 */
public class KeyValueStoreModulesManager {

	/**
	 * List of the running KeyValueStoreModules.
	 */
	private final Set<KeyValueStoreModule> keyValueStoreModules = new HashSet<KeyValueStoreModule>();

	/**
	 * References to the localEntries (for adding and removing)
	 */
	private LocalEntries localEntries;

	public KeyValueStoreModulesManager(final BundleContext context) {
		try {
			final ServiceReference[] servicesRefs = context
					.getAllServiceReferences(KeyValueStoreModule.class
							.getName(), null);
			context.addServiceListener(new ServiceListener() {

				public void serviceChanged(final ServiceEvent event) {
					if (event.getType() == ServiceEvent.REGISTERED) {
						final Object o = context.getService(event
								.getServiceReference());
						if (o != null && o instanceof KeyValueStoreModule) {
							registerKvsModule((KeyValueStoreModule) o);
						}
					} else if (event.getType() == ServiceEvent.UNREGISTERING) {
						final Object o = context.getService(event
								.getServiceReference());
						if (o != null && o instanceof KeyValueStoreModule) {
							unregisterKvsModule((KeyValueStoreModule) o);
						}
					}
				}

			}, "(objectclass=" + KeyValueStoreModule.class.getName() + ")");
			if (servicesRefs != null) {
				for (final ServiceReference ref : servicesRefs) {
					final Object o = context.getService(ref);
					if (o instanceof KeyValueStoreModule) {
						registerKvsModule((KeyValueStoreModule) o);
					}
				}
			}
		} catch (final InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		keyValueStoreModules.clear();
	}

	/**
	 * Will do a GET to every KeyValueStoreModules.
	 * 
	 * @param key
	 * @return set of all values found and indexed with the key
	 */
	public Set<String> get(final String key) {
		final Set<String> values = new HashSet<String>();
		for (final KeyValueStoreModule module : keyValueStoreModules) {
			values.addAll(module.get(key));
		}
		return values;
	}

	/**
	 * Will do a ADD/PUT to every KeyValueStoreModules.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(final String key, final String value) {
		for (final KeyValueStoreModule module : keyValueStoreModules) {
			module.put(key, value);
		}
	}

	/**
	 * Called when a new KeyValueStoreModule is detected by OSGi listener.
	 * 
	 * @param o
	 */
	private void registerKvsModule(final KeyValueStoreModule o) {
		keyValueStoreModules.add(o);
		localEntries.notifyNewKeyValueStoreModule(o);
	}

	/**
	 * Will do a REMOVE on every KeyValueStoreModule
	 * 
	 * @param key
	 * @param value
	 */
	public void remove(final String key, final String value) {
		for (final KeyValueStoreModule module : keyValueStoreModules) {
			module.remove(key, value);
		}
	}

	/**
	 * Called by LocalEntries to configure its reference for callbacks.
	 * 
	 * @param localEntries
	 */
	public void setLocalEntries(final LocalEntries localEntries) {
		this.localEntries = localEntries;
	}

	/**
	 * Remote a KeyValueModuleStore, when OSGi detects that a service had been
	 * removed.
	 * 
	 * @param o
	 */
	private void unregisterKvsModule(final KeyValueStoreModule o) {
		keyValueStoreModules.remove(o);
		localEntries.notifyRemovedKeyValueStoreModule(o);
	}
}
