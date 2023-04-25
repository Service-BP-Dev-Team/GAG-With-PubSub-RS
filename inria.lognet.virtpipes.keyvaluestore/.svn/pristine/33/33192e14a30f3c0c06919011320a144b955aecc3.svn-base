/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
 * 
 *    This file is part of LogNet project
 *    <a href="http://www-sop.inria.fr/teams/lognet">LogNet</a>
 * 
 *     LogNet is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     LogNet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with LogNet; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id$
package inria.lognet.virtpipes.keyvaluestore;

import inria.lognet.virtpipes.VirtPipeOutput;
import inria.lognet.virtpipes.VirtPipeOutputDefault;
import inria.lognet.virtpipes.VirtPipesModule;
import inria.lognet.virtpipes.VirtPipesService;
import inria.lognet.virtpipes.VirtPipesServiceUtil;
import inria.pon.keyvaluestore.framework.KeyValueRegistration;
import inria.pon.keyvaluestore.framework.KeyValueStore;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Information module for VirtPipesService with Open-Chord implementation
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public class VirtPipesKeyValueStore implements VirtPipesModule {

	// OSGi Tracker of OpenChord
	private final ServiceTracker keyValueStoreTracker;

	// The master, VirtPipesService
	private VirtPipesService vps = null;

	// OpenChord, the DHT key-values store
	private KeyValueStore kvs = null;

	// Entries register in the DHT
	private final Map<UUID, KeyValueRegistration> entries = new HashMap<UUID, KeyValueRegistration>();

	// List of VPI to register without ID check, like awaiting registrations
	private final List<UUID> pendingVpisWithoutIdCheck = new ArrayList<UUID>();

	public VirtPipesKeyValueStore(final BundleContext context) {
		(keyValueStoreTracker = new ServiceTracker(context, KeyValueStore.class
				.getName(), new ServiceTrackerCustomizer() {

			public Object addingService(final ServiceReference reference) {
				final Object o = context.getService(reference);
				if (kvs == null && o instanceof KeyValueStore) {
					kvs = (KeyValueStore) o;
					if (vps != null) {
						registerVpsInChord();
						registerPendingVpis();
					}
					return kvs;
				}
				return null;
			}

			public void modifiedService(final ServiceReference reference,
					final Object service) {}

			public void removedService(final ServiceReference reference,
					final Object service) {
				if (kvs.equals(service)) {
					kvs = null;
				}
			}

		})).open();
	}

	public void close() {
		synchronized (entries) {
			for (final KeyValueRegistration entry : entries.values()) {
				entry.unregister();
			}
			entries.clear();
		}
		if (vps != null) {
			vps.removeVirtPipesModule(this);
		}
		vps = null;
		kvs = null;
		keyValueStoreTracker.close();
	}

	public List<InetSocketAddress> getSocketAddresses(final UUID vps) {
		Iterator<String> it2;
		it2 = kvs.get("ips:" + vps.toString()).iterator();
		if (it2.hasNext()) {
			return VirtPipesServiceUtil.parse(it2.next());
		}
		return new ArrayList<InetSocketAddress>();
	}

	public VirtPipeOutput getVirtPipeOutput(final UUID id) {
		System.out.println("getVirtPipeOutput " + id);
		Iterator<String> it;
		it = kvs.get(id.toString()).iterator();
		if (it.hasNext()) {
			return new VirtPipeOutputDefault(vps, id);
		}
		return null;
	}

	public Set<UUID> getVirtPipesService(final UUID vp) {
		System.out.println("getVirtPipesService " + vp);
		final Set<UUID> vps = new HashSet<UUID>();
		if (kvs != null) {
			final Set<String> set = kvs.get(vp.toString());
			for (final Serializable s : set) {
				if (s instanceof String) {
					vps.add(UUID.fromString((String) s));
				}
			}
		}
		return vps;
	}

	public boolean isUuidIsUnique(final UUID id) {
		System.out.println("isUuidIsUnique " + id);
		return kvs.get(id.toString()).isEmpty();
	}

	private void registerPendingVpis() {
		synchronized (pendingVpisWithoutIdCheck) {
			for (final UUID id : pendingVpisWithoutIdCheck) {
				registerVirtPipeInput(id);
			}
			pendingVpisWithoutIdCheck.clear();
		}
	}

	public void registerVirtPipeInput(final UUID id) {
		if (kvs != null && vps != null) {
			if (!entries.containsKey(id)) {
				entries.put(id, kvs.put(id.toString(), vps.getId().toString()));
			}
		} else {
			synchronized (pendingVpisWithoutIdCheck) {
				pendingVpisWithoutIdCheck.add(id);
			}
		}
	}

	private void registerVpsInChord() {
		kvs.put("ips:" + vps.getId().toString(), vps.getIp());
	}

	public void setVirtPipesService(final VirtPipesService vps) {
		if (this.vps == null) {
			this.vps = vps;
			if (kvs != null) {
				registerVpsInChord();
				registerPendingVpis();
			}
		}
	}

	public void unregisterVirtPipe(final UUID id) {
		if (entries.containsKey(id)) {
			entries.get(id).unregister();
		}
	}
}
