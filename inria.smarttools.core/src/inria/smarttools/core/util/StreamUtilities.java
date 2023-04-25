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
// $Id: StreamUtilities.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * A handful of utilities for dealing with IO streams.
 * 
 * @author <a href="mailto:kdowney@amberarcher.com">Kyle F. Downey</a>
 * @version $Revision: 2446 $
 */
public class StreamUtilities {

	public static final int copy(final InputStream from, final OutputStream to)
			throws IOException {

		final byte[] buffer = StreamUtilities.loadBytes(from);
		to.write(buffer);
		to.flush();

		return buffer.length;
	}

	/**
	 * reads all bytes from the given stream
	 * 
	 * @param is
	 *            the stream to read from
	 */
	public static final byte[] loadBytes(final InputStream is)
			throws IOException {
		// read in the entry data
		int count = 0;
		byte[] buffer = new byte[0];
		final byte[] chunk = new byte[4096];
		while ((count = is.read(chunk)) >= 0) {
			final byte[] t = new byte[buffer.length + count];
			System.arraycopy(buffer, 0, t, 0, buffer.length);
			System.arraycopy(chunk, 0, t, buffer.length, count);
			buffer = t;
		}
		return buffer;
	}

	/**
	 * Reads all the characters from the given Reader and generates a String
	 */
	public static final String loadString(final Reader reader)
			throws IOException {
		// read in the entry data
		int count = 0;
		char[] buffer = new char[0];
		final char[] chunk = new char[4096];
		while ((count = reader.read(chunk)) >= 0) {
			final char[] t = new char[buffer.length + count];
			System.arraycopy(buffer, 0, t, 0, buffer.length);
			System.arraycopy(chunk, 0, t, buffer.length, count);
			buffer = t;
		}

		return new String(buffer);
	}
}
