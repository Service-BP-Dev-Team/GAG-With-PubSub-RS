package inria.pon.keyvaluestore.store;

import java.util.Set;

/**
 * Interface you need to implements for plugin a KeyValueStoreModule. Publish an
 * instance of your KeyValueStoreModule as this Class for plugin your
 * KeyValueStoreModule to this abstraction automatically.
 * 
 * @author baptisteboussemart@gmail.com
 */
public interface KeyValueStoreModule {

	/**
	 * Your KeyValueStoreModule should search and give back the values indexed
	 * by key.
	 * 
	 * @param key
	 *            of the indexed values
	 * @return list of values indexed by key
	 */
	public Set<String> get(String key);

	/**
	 * Your KeyValueStoreModule should register this couple into your Key Value
	 * Storage implementation.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, String value);

	/**
	 * Your KeyValueStoreModule shoud remove this couple from your Key Value
	 * Storage implementation.
	 * 
	 * @param key
	 * @param value
	 */
	public void remove(String key, String value);

}
