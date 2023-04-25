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
// $Id: STBundleRegistry.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.dynamic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;

/**
 * Registers every Bundle started by SmartTools. Bundle needs to register or
 * inherit STAbstractUIPlugin or STGenericActivator.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public class STBundleRegistry {

	private static Map<Long, Bundle> bundles;

	public static void clear() {
		STBundleRegistry.getBundles().clear();
		STBundleRegistry.bundles = null;
	}

	private static Map<Long, Bundle> getBundles() {
		if (STBundleRegistry.bundles == null) {
			STBundleRegistry.bundles = new HashMap<Long, Bundle>();
		}
		return STBundleRegistry.bundles;
	}

	public static void registerBundle(final Bundle bundle) {
		STBundleRegistry.getBundles().put(bundle.getBundleId(), bundle);
	}

	public static void removeBundle(final Bundle bundle) {
		STBundleRegistry.getBundles().remove(bundle.getBundleId());
	}

	public static Collection<Bundle> values() {
		return STBundleRegistry.getBundles().values();
	}

}
