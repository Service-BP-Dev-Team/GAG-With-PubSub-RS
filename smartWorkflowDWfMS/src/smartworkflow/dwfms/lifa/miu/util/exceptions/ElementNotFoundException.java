/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.exceptions;

/**
 *
 * @author Ndadji Maxime
 */
public class ElementNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ElementNotFoundException(Throwable cause) {
        super(cause);
    }

    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementNotFoundException(String message) {
        super(message);
    }
}
