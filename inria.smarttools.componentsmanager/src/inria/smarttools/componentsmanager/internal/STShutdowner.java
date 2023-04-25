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
// $Id: STShutdowner.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.componentsmanager.internal;

import inria.smarttools.dynamic.DocumentExtensionsRegistry;
import inria.smarttools.dynamic.STBundleRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.Bundle;

/**
 * Class that stops all Bundles registered in the STBundleRegistry.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2819 $
 */
public class STShutdowner implements Runnable {

	public void run() {

		/*
		 * Have to copy first, because bundles will be removes from the original
		 * Map of STBundleRegistry.
		 */
		final Collection<Bundle> bundlesToShutdown = new ArrayList<Bundle>();
		synchronized (this) {
			final Collection<Bundle> bundles = STBundleRegistry.values();
			final Iterator<Bundle> it = bundles.iterator();
			while (it.hasNext()) {
				bundlesToShutdown.add(it.next());
			}
		}

		/*
		 * Now we stop all the bundles from the list.
		 */
		final Iterator<Bundle> it = bundlesToShutdown.iterator();
		while (it.hasNext()) {
			try {
				it.next().stop();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		DocumentExtensionsRegistry.dispose();
		STBundleRegistry.clear();

	}

}
