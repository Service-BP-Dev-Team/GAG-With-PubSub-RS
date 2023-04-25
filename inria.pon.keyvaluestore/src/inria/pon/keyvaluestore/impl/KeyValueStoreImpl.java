package inria.pon.keyvaluestore.impl;

import inria.pon.keyvaluestore.framework.KeyValueRegistration;
import inria.pon.keyvaluestore.framework.KeyValueStore;
import inria.pon.keyvaluestore.impl.local.LocalEntries;
import inria.pon.keyvaluestore.impl.remote.RemoteEntries;

import java.util.Set;

/**
 * Implementation of the KeyValueStore published as an OSGi service, that users
 * will add, get and remove key-value couples. Joue le rôle de façade pour les
 * couches utilisatrices.
 * 
 * @author baptisteboussemart@gmail.com
 */
public class KeyValueStoreImpl implements KeyValueStore {

	/**
	 * Key-values published.
	 */
	private final LocalEntries localEntries;

	/**
	 * Cache of key-values retrieved.
	 */
	private final RemoteEntries remoteEntries;

	public KeyValueStoreImpl(final KeyValueStoreModulesManager kvsmManager) {
		localEntries = new LocalEntries(kvsmManager);
		remoteEntries = new RemoteEntries(kvsmManager);
	}

	public void close() {
		localEntries.close();
		remoteEntries.close();
	}

	/**
	 * Retrieve of key-value couples.
	 */
	public synchronized Set<String> get(final String key) {
		final Set<String> values = remoteEntries.get(key);
		final Set<String> localValues = localEntries.get(key);
		if (localValues != null) {
			synchronized (localValues) {
				values.addAll(localValues);
			}
		}
		return values;
	}

	/**
	 * Add a key-value couple.
	 */
	public synchronized KeyValueRegistration put(final String key,
			final String value) {
		return localEntries.put(key, value);
	}

}
