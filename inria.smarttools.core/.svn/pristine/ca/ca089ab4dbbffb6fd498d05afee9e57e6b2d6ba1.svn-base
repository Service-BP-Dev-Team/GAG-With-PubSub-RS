/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
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
// $Id: StStringBuffer.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.util;

import inria.smarttools.core.Activator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public class StStringBuffer {

	protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

	/**
	 * Creates a new <code>StringBuffer</code> instance.
	 */
	public StStringBuffer() {}

	/**
	 * Describe <code>add</code> method here.
	 * 
	 * @param str
	 *            a <code>String</code> value
	 */
	public void add(final String str) {
		try {
			baos.write(str.getBytes());
		} catch (final IOException e) {
			Activator.LOGGER.throwing(getClass().getName(),
					"void add(String str)", e);
		}
	}

	/**
	 * Describe <code>add</code> method here.
	 * 
	 * @param str
	 *            a <code>String</code> value
	 */
	public void add(final StStringBuffer str) {
		try {
			baos.write(str.getBytes());
		} catch (final IOException e) {
			Activator.LOGGER.throwing(getClass().getName(),
					"void add(StStringBuffer str)", e);
		}
	}

	/**
	 * Describe <code>toString</code> method here.
	 * 
	 * @return a <code>String</code> value
	 */
	@Override
	public String toString() {
		return baos.toString();
	}

	/**
	 * Describe <code>getBytes</code> method here.
	 * 
	 * @return a <code>byte[]</code> value
	 */
	public byte[] getBytes() {
		return baos.toByteArray();
	}

	public ByteArrayOutputStream getStream() {
		return baos;
	}

}
