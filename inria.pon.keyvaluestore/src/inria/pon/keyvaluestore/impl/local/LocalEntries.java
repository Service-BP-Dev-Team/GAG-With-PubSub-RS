package inria.pon.keyvaluestore.impl.local;

import inria.pon.keyvaluestore.framework.KeyValueRegistration;
import inria.pon.keyvaluestore.impl.KeyValueStoreModulesManager;
import inria.pon.keyvaluestore.store.KeyValueStoreModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe qui gère les clés-valeurs enregistrées par cette JVM. Principalement,
 * on faire les PUT et les REMOVE dans cette classe.
 * 
 * @author baptiste
 */
public class LocalEntries {

	/**
	 * Entrées enregistrées par cette JVM.
	 */
	private final Map<String, Set<String>> entries = new HashMap<String, Set<String>>();

	/**
	 * Instance du gestionnaire des modules implémentant le KeyValueStore.
	 */
	private final KeyValueStoreModulesManager kvsmManager;

	public LocalEntries(final KeyValueStoreModulesManager kvsmManager) {
		this.kvsmManager = kvsmManager;
		kvsmManager.setLocalEntries(this);
	}

	/**
	 * Supprime toutes les entrées enregitrées et ferme le gestionnaire des
	 * modules implémentant le KeyValueStore.
	 */
	public void close() {
		synchronized (entries) {
			for (final String key : entries.keySet()) {
				final Set<String> values = entries.get(key);
				synchronized (values) {
					for (final String value : values) {
						kvsmManager.remove(key, value);
					}
				}
				values.clear();
			}
			entries.clear();
		}
		kvsmManager.close();
	}

	/**
	 * Utilisé pour compléter les KeyValues venant des modules, au cas où il n'y
	 * a aucun module associé ou que les implémentations ne donnent pas les
	 * clés-valeurs locales.
	 * 
	 * @param key
	 *            index
	 * @return values
	 */
	public Set<String> get(final String key) {
		synchronized (entries) {
			return entries.get(key);
		}
	}

	/**
	 * Méthode appelée par le gestionnaire de modules KeyValueStore pour
	 * enregistrer les KeyValues locales dans le module KeyValueStore
	 * fraichement associé.
	 * 
	 * @param module
	 */
	public void notifyNewKeyValueStoreModule(final KeyValueStoreModule module) {
		synchronized (entries) {
			for (final String key : entries.keySet()) {
				for (final String value : entries.get(key)) {
					module.put(key, value);
				}
			}
		}
	}

	/**
	 * Méthode appelée par le gestionnaire de modules KeyValueStore pour
	 * supprimer les KeyValues locales du module KeyValueStore fraichement
	 * dissocié.
	 * 
	 * @param module
	 */
	public void notifyRemovedKeyValueStoreModule(
			final KeyValueStoreModule module) {
		synchronized (entries) {
			for (final String key : entries.keySet()) {
				for (final String value : entries.get(key)) {
					module.remove(key, value);
				}
			}
		}
	}

	/**
	 * Méthode appelée par le KeyValueStoreImpl, donc par les couches
	 * utilisatrices. Ajoute un KeyValue, s'il n'existe pas encore, auprès des
	 * modules associés et renvoie un ticket d'enregistrement lorsque
	 * l'opération est un succès.
	 * 
	 * @param key
	 * @param value
	 * @return ticket d'enregistrement
	 */
	public KeyValueRegistration put(final String key, final String value) {
		synchronized (entries) {
			Set<String> values = entries.get(key);
			if (values == null) {
				values = new HashSet<String>();
				entries.put(key, values);
			}
			synchronized (values) {
				if (values.add(value)) {
					kvsmManager.put(key, value);
					return new KeyValueRegistration() {

						public String getKey() {
							return key;
						}

						public String getValue() {
							return value;
						}

						public void unregister() {
							LocalEntries.this.unregister(getKey(), getValue());
						}

					};
				}
			}
		}
		return null;
	}

	/**
	 * Appelé par les tickets KeyValueRegistration pour supprimer le KeyValue
	 * associé au ticket appelant.
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void unregister(final String key, final String value) {
		synchronized (entries) {
			final Set<String> values = entries.get(key);
			if (values != null) {
				synchronized (values) {
					if (values.remove(value)) {
						if (values.size() == 0) {
							entries.remove(key);
						}
						kvsmManager.remove(key, value);
					}
				}
			}
		}
	}

}
