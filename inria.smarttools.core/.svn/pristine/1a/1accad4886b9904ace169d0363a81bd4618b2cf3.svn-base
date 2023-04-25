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
// $Id: STResources.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.util;

import java.util.ResourceBundle;

/**
 * Property of SmartTools
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public class STResources {

	private static String RESOURCE_BUNDLE = "inria.smarttools.core.util.SmartTools";

	public static String getProperty(final String key) {
		final ResourceBundle bundle = STResources.getResource();
		return (bundle != null) ? bundle.getString(key) : key;
	}

	static ResourceBundle getResource() {
		return ResourceBundle.getBundle(STResources.RESOURCE_BUNDLE);
	}
}
