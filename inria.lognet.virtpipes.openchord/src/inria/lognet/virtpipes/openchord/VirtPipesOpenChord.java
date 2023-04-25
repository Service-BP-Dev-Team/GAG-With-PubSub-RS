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
package inria.lognet.virtpipes.openchord;

import inria.lognet.virtpipes.Activator;
import inria.lognet.virtpipes.VirtPipeOutput;
import inria.lognet.virtpipes.VirtPipeOutputDefault;
import inria.lognet.virtpipes.VirtPipesModule;
import inria.lognet.virtpipes.VirtPipesService;
import inria.lognet.virtpipes.VirtPipesServiceUtil;

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

import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * Information module for VirtPipesService with Open-Chord implementation
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public class VirtPipesOpenChord implements VirtPipesModule {

	/**
	 * Own local entries registered in Open_Chord
	 * 
	 * @author baptiste
	 */
	private class ChordEntry {

		public Key key;
		public Serializable serializable;
	}

	// OSGi Tracker of OpenChord
	private final ServiceTracker chordTracker;

	// The master, VirtPipesService
	private VirtPipesService vps = null;

	// OpenChord, the DHT key-values store
	private Chord chord = null;

	// Entries register in the DHT
	private final Map<UUID, ChordEntry> entries = new HashMap<UUID, ChordEntry>();

	// List of VPI to register without ID check, like awaiting registrations
	private final List<UUID> pendingVpisWithoutIdCheck = new ArrayList<UUID>();

	public VirtPipesOpenChord(final BundleContext context) {
		(chordTracker = new ServiceTracker(context, Chord.class.getName(),
				new ServiceTrackerCustomizer() {

					public Object addingService(final ServiceReference reference) {
						final Object o = context.getService(reference);
						if (chord == null && o instanceof Chord) {
							chord = (Chord) o;
							if (vps != null) {
								registerVpsInChord();
								registerPendingVpis();
							}
							return chord;
						}
						return null;
					}

					public void modifiedService(
							final ServiceReference reference,
							final Object service) {}

					public void removedService(
							final ServiceReference reference,
							final Object service) {
						if (chord.equals(service)) {
							chord = null;
						}
					}

				})).open();
	}

	public void close() {
		synchronized (entries) {
			for (final ChordEntry entry : entries.values()) {
				try {
					chord.remove(entry.key, entry.serializable);
				} catch (final ServiceException e) {
					e.printStackTrace();
				}
			}
			entries.clear();
		}
		if (vps != null) {
			vps.removeVirtPipesModule(this);
		}
		vps = null;
		chord = null;
		chordTracker.close();
	}

	public List<InetSocketAddress> getSocketAddresses(final UUID vps) {
		Iterator<Serializable> it2;
		try {
			it2 = chord.retrieve(new Key() {

				public byte[] getBytes() {
					return ("ips:" + vps.toString()).getBytes();
				}

			}).iterator();
			if (it2.hasNext()) {
				return VirtPipesServiceUtil.parse((String) it2.next());
			}
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
		return new ArrayList<InetSocketAddress>();
	}

	public VirtPipeOutput getVirtPipeOutput(final UUID id) {
		Iterator<Serializable> it;
		try {
			it = chord.retrieve(new Key() {

				public byte[] getBytes() {
					return id.toString().getBytes();
				}

			}).iterator();
			if (it.hasNext()) {
				return new VirtPipeOutputDefault(vps, id);
			}
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Set<UUID> getVirtPipesService(final UUID vp) {
		final Set<UUID> vps = new HashSet<UUID>();
		if (chord != null) {
			try {
				final Set<Serializable> set = chord.retrieve(new Key() {

					public byte[] getBytes() {
						return vp.toString().getBytes();
					}

				});
				for (final Serializable s : set) {
					if (s instanceof String) {
						vps.add(UUID.fromString((String) s));
					}
				}
			} catch (final ServiceException e) {
				e.printStackTrace();
			}
		}
		return vps;
	}

	public boolean isUuidIsUnique(final UUID id) {
		final Key key = new Key() {

			public byte[] getBytes() {
				return id.toString().getBytes();
			}

		};
		try {
			return chord.retrieve(key).isEmpty();
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
		return false;
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
		if (chord != null && vps != null) {
			if (!entries.containsKey(id)) {
				try {
					final ChordEntry entry = new ChordEntry();
					entry.key = new Key() {

						public byte[] getBytes() {
							return id.toString().getBytes();
						}

					};
					entry.serializable = vps.getId().toString();
					chord.insert(entry.key, entry.serializable);
					entries.put(id, entry);
				} catch (final ServiceException e) {
					Activator.LOGGER.throwing(getClass().getName(),
							"registerVirtPipeWithoutUuidChech(VirtPipeInput)",
							e);
					throw new IllegalStateException(e);
				}
			}
		} else {
			synchronized (pendingVpisWithoutIdCheck) {
				pendingVpisWithoutIdCheck.add(id);
			}
		}
	}

	private void registerVpsInChord() {
		try {
			chord.insert(new Key() {

				public byte[] getBytes() {
					return ("ips:" + vps.getId().toString()).getBytes();
				}

			}, vps.getIp());
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
	}

	public void setVirtPipesService(final VirtPipesService vps) {
		if (this.vps == null) {
			this.vps = vps;
			if (chord != null) {
				registerVpsInChord();
				registerPendingVpis();
			}
		}
	}

	public void unregisterVirtPipe(final UUID id) {
		final ChordEntry entry = entries.get(id);
		if (chord != null) {
			try {
				chord.remove(entry.key, entry.serializable);
			} catch (final ServiceException e) {
				e.printStackTrace();
			}
		}
	}
}
