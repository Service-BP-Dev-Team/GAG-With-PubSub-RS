package inria.pon.keyvaluestore.impl.remote;

import inria.pon.keyvaluestore.impl.KeyValueStoreModulesManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe qui cherche les valeurs indexées par une clés auprès de modules de
 * KeyValueStore, passant par le gestionnaire KeyValueStoreModulesManager. Elle
 * joue aussi le rôle de cache, dont le temps de mise en cache est paramétrable.
 * Cela évite de lancer beaucoup de GET sur le réseau dans un laps de temps
 * court.
 * 
 * @author baptiste
 */
public class RemoteEntries {

	/**
	 * Classe représentant une entrée mis en cache.
	 * 
	 * @author baptiste
	 */
	private class RemoteEntry {

		Long time;
		String key;
		Set<String> values;

		public RemoteEntry(final Long time, final String key,
				final Set<String> values) {
			this.key = key;
			this.time = time;
			this.values = values;
		}
	}

	private final static long TIME_OUT = 5000;

	/**
	 * Les entrées misent en cache.
	 */
	private final Map<String, RemoteEntry> caches = new HashMap<String, RemoteEntry>();

	/**
	 * Le gestionnaire de modules implémentant le KeyValueStorage, à qui on fait
	 * les GET.
	 */
	private final KeyValueStoreModulesManager kvsmManager;

	public RemoteEntries(final KeyValueStoreModulesManager kvsmManager) {
		this.kvsmManager = kvsmManager;
	}

	public void close() {
		caches.clear();
	}

	/**
	 * Méthode appelée par le KeyValueStoreImpl, donc les couches utilisatrices,
	 * pour cherche les valeurs indexées par une clé spécifiée. Si la requête
	 * intervient dans un lapse de temps plus court que le temps de mise en
	 * cache, alors aucune recherche n'est effectuée, et on rend comme réponse
	 * ce que le cache contient.
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> get(final String key) {
		RemoteEntry cache = caches.get(key);
		if (cache != null) {
			if (cache.time + RemoteEntries.TIME_OUT > System
					.currentTimeMillis()) {
				return cache.values;
			} else {
				caches.remove(key); // XXX suppression pourrait être dans un
									// thread
			}

		}
		cache = new RemoteEntry(System.currentTimeMillis(), key, kvsmManager
				.get(key));
		caches.put(key, cache);
		return cache.values;
	}
}
