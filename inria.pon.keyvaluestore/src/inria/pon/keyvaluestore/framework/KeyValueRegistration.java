package inria.pon.keyvaluestore.framework;

/**
 * Ticket gave to you when you registered this couple key-value. Just call
 * unregister to unpublish this couple.
 * 
 * @author baptisteboussemart@gmail.com
 */
public interface KeyValueRegistration {

	public String getKey();

	public String getValue();

	public void unregister();

}
