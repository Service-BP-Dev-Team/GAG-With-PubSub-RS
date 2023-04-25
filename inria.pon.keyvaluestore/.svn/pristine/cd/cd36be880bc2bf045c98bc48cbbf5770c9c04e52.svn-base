package inria.pon.keyvaluestore.framework;

import java.util.Set;

/**
 * Main class for the users of this key-value storage abstraction. With OSGi, an
 * instance of this interface is published in the OSGi services.
 * 
 * @author baptisteboussemart@gmail.com
 */
public interface KeyValueStore {

	/**
	 * Retrieve the list of values indexed by the key.
	 * 
	 * @param key
	 *            that indexes values
	 * @return list of values
	 */
	public Set<String> get(String key);

	/**
	 * @param key
	 *            that indexes the value
	 * @param value
	 *            to register/publish
	 * @return KeyValueRegistration (a ticket) you use to unregister the
	 *         key-value couple. Or null if the key-value is not published
	 *         (key-value couple already exist).
	 */
	public KeyValueRegistration put(String key, String value);

}
