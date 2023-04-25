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
// $Id: DocumentExtensionsRegistry.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.dynamic;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by SmartTools Eclipse for registering the extensions of
 * the documents. This registry is important to link a document to a component.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public class DocumentExtensionsRegistry {

	private static DocumentExtensionsRegistry instance = null;

	public static void dispose() {
		DocumentExtensionsRegistry.instance.extensions.clear();
		DocumentExtensionsRegistry.instance = null;
	}

	public synchronized static DocumentExtensionsRegistry getInstance() {
		if (DocumentExtensionsRegistry.instance == null) {
			DocumentExtensionsRegistry.instance = new DocumentExtensionsRegistry();
		}
		return DocumentExtensionsRegistry.instance;
	}

	private final Map<String, String> extensions = new HashMap<String, String>();

	private DocumentExtensionsRegistry() {
	// singleton
	}

	private String extractExtension(final String filename) {
		return filename.substring(filename.lastIndexOf('.') + 1, filename
				.length());
	}

	public String getComponentType(final String filename) {
		if (filename != null) {
			return extensions.get(extractExtension(filename));
		}
		return null;
	}

	public void registerExtension(final String extension, final String type) {
		extensions.put(extension, type);
	}

	public void removeExtension(final String extension) {
		extensions.remove(extension);
	}
}
