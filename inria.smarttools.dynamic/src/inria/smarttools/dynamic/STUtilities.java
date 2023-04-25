/*******************************************************************************
 * Copyright (c) 2000, 2009 INRIA.
 * 
 *    This file is part of SmartTools project
 *    <a href="http://www-sop.inria.fr/teams/lognet/pon/">SmartTools</a>
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
// $Id: STUtilities.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.dynamic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This is an utility class for loading resources from any bundle.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2819 $
 */

public class STUtilities {

	protected static String libdir = null;

	protected static String stlibdir = null;

	public static String workspace = null;

	public static Icon getImage(final String resource) {
		final InputStream imgFile = STUtilities.class
				.getResourceAsStream(resource);
		try {
			return new ImageIcon(ImageIO.read(imgFile));
		} catch (final Exception e) {
			Logger.getLogger("SmartTools").throwing(
					STUtilities.class.getName(),
					"Icon getImage(String resource)", e);
			return null;
		}
	}

	/**
	 * This method return an InputStream for a given file. <filename> is
	 * something like this "resources:css/toto.css" We try to obtain stream from
	 * : - URL("file:resources/css/too.css"); -
	 * getResources("/resources/css/too.css");
	 * 
	 * @param filename
	 *            a <code>String</code> value
	 */
	public static InputStream getISForFile(final String filename)
			throws IOException {
		if (filename.startsWith("resources:")) {
			return STUtilities.class.getResourceAsStream(filename.replaceFirst(
					"resources:", "/"));
		} else {
			return STUtilities.getURLForFile(filename).openStream();
		}
	}

	public static URL getURLForFile(final String filename) throws IOException {
		if (filename == null) {
			throw new IOException("filename cannot be null");
		} else if (filename.startsWith("resources:")) {
			return STUtilities.class.getResource(filename.replaceFirst(
					"resources:", "/"));
		} else if (filename.startsWith("workspace:")) {
			if (STUtilities.workspace == null) {
				throw new IOException("there is no workspace");
			}
			return new URL(filename.replaceFirst("workspace:", "file:///"
					+ STUtilities.workspace + "/"));
		} else if (filename.startsWith("file:")) {
			return new URL(filename);
		} else if (filename.startsWith("jar:file:")) {
			return new URL(filename);
		} else {
			throw new IOException("filename not found : " + filename);
		}
	}
}
