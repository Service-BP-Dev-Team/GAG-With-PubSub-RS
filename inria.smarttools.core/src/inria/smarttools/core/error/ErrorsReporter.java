/*******************************************************************************
 * Copyright (c) 2000, 2007 INRIA.
 * 
 *    This file is part of SmartTools project
 *    <a href="http://www-sop.inria.fr/smartool/">SmartTools</a>
 * 
 *     SmartTools is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     SmartTools is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SmartTools; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: ErrorsReporter.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.error;

import java.io.OutputStream;

/**
 * Interface for Object that can report errors/warning/info messages Retrieving
 * error output stream is also provided.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public interface ErrorsReporter extends ErrorConstants {

	/**
	 * Report an error message
	 * 
	 * @param flag
	 *            an <code>int</code> value
	 * @param error
	 *            an <code>Object[]</code> value
	 */
	public void error(int flag, Object[] error);

	/**
	 * Report a warning message
	 * 
	 * @param flag
	 *            an <code>int</code> value
	 * @param error
	 *            an <code>Object[]</code> value
	 */
	public void warning(int flag, Object[] error);

	/**
	 * Report an error message
	 * 
	 * @param message
	 *            a <code>String</code> value
	 */
	public void error(String message);

	/**
	 * Report an info message
	 * 
	 * @param message
	 *            a <code>String</code> value
	 */
	public void info(String message);

	/**
	 * Return the current error stream
	 * 
	 * @return an <code>OutputStream</code> value
	 */
	public OutputStream getErrorStream();
}
