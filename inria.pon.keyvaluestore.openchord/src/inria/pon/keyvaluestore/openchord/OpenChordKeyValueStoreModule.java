package inria.pon.keyvaluestore.openchord;

import inria.pon.keyvaluestore.store.KeyValueStoreModule;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * Classe qui fait la glue entre OpenChord et KeyValueStore en implémentant
 * KeyValueStoreModule.
 * 
 * @author baptiste
 */
public class OpenChordKeyValueStoreModule implements KeyValueStoreModule {

	private final Chord chord;

	public OpenChordKeyValueStoreModule(final Chord chord) {
		this.chord = chord;
	}

	/**
	 * Recherche les valeurs dans OpenChord.
	 */
	public Set<String> get(final String key) {
		final Set<String> response = new HashSet<String>();
		try {
			Set<Serializable> values = null;
			for (int i = 0; i < 5; i++) {
				values = chord.retrieve(new Key() {

					public byte[] getBytes() {
						return key.getBytes();
					}

				});
				if (values != null && !values.isEmpty()) {
					break;
				}
//				System.err.println("Chord failed on retrieving values for the key " + key + " for the " + i + " time.");
			}
			for (final Serializable s : values) {
				if (s instanceof String) {
					response.add((String) s);
				}
			}
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Ajoute une valeur indexée par une clé dans OpenChord.
	 */
	public void put(final String key, final String value) {
		try {
			chord.insert(new Key() {

				public byte[] getBytes() {
					return key.getBytes();
				}

			}, value);
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Supprime une valeur indexée par une clé d'OpenChord.
	 */
	public void remove(final String key, final String value) {
		try {
			chord.remove(new Key() {

				public byte[] getBytes() {
					return key.getBytes();
				}

			}, value);
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
	}

}
