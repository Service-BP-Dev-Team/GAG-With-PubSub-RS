/***************************************************************************
 *                                                                         *
 *                             ChordFuture.java                            *
 *                            -------------------                          *
 *   date                 : 16.08.2005                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *   			    		karsten.loesing@uni-bamberg.de                 *
 *                                                                         *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   A copy of the license can be found in the license.txt file supplied   *
 *   with this software or at: http://www.gnu.org/copyleft/gpl.html        *
 *                                                                         *
 ***************************************************************************/
package de.uniba.wiai.lspi.chord.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.Proxy;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.AsynChord;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ChordCallback;
import de.uniba.wiai.lspi.chord.service.ChordFuture;
import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.Report;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * Implements all operations which can be invoked on the local node.
 * 
 * @author Karsten Loesing
 * @version 1.0.5
 */
public final class ChordImpl implements Chord, Report, AsynChord {

	/**
	 * ThreadFactory used with Executor services.
	 * 
	 * @author sven
	 */
	private static class ChordThreadFactory implements
			java.util.concurrent.ThreadFactory {

		private final String executorName;

		ChordThreadFactory(final String executorName) {
			this.executorName = executorName;
		}

		public Thread newThread(final Runnable r) {
			final Thread newThread = new Thread(r);
			newThread.setName(executorName + "-" + newThread.getName());
			return newThread;
		}

	}

	/**
	 * Number of threads to allow concurrent invocations of asynchronous
	 * methods. e.g. {@link ChordImpl#insertAsync(Key, Serializable)}.
	 */
	private static final int ASYNC_CALL_THREADS = Integer.parseInt(System
			.getProperty(ChordImpl.class.getName() + ".AsyncThread.no"));

	/**
	 * Time in seconds until the stabilize task is started for the first time.
	 */
	private static final int STABILIZE_TASK_START = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.StabilizeTask.start"));

	/**
	 * Time in seconds between two invocations of the stabilize task.
	 */
	private static final int STABILIZE_TASK_INTERVAL = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.StabilizeTask.interval"));

	/**
	 * Time in seconds until the fix finger task is started for the first time.
	 */
	private static final int FIX_FINGER_TASK_START = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.FixFingerTask.start"));

	/**
	 * Time in seconds between two invocations of the fix finger task.
	 */
	private static final int FIX_FINGER_TASK_INTERVAL = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.FixFingerTask.interval"));

	/**
	 * Time in seconds until the check predecessor task is started for the first
	 * time.
	 */
	private static final int CHECK_PREDECESSOR_TASK_START = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.CheckPredecessorTask.start"));

	/**
	 * Time in seconds between two invocations of the check predecessor task.
	 */
	private static final int CHECK_PREDECESSOR_TASK_INTERVAL = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.CheckPredecessorTask.interval"));

	/**
	 * Number of references in the successor list.
	 */
	private static final int NUMBER_OF_SUCCESSORS = Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.successors")) < 1 ? 1
			: Integer
					.parseInt(System
							.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.successors"));

	/**
	 * Object logger.
	 */
	protected Logger logger;

	/**
	 * Reference on that part of the node implementation which is accessible by
	 * other nodes; if <code>null</code>, this node is not connected
	 */
	private NodeImpl localNode;

	/**
	 * Entries stored at this node, including replicas.
	 */
	private Entries entries;

	/**
	 * Executor service for local maintenance tasks.
	 */
	private final ScheduledExecutorService maintenanceTasks;

	/**
	 * Executor service for asynch requests.
	 */
	private final ExecutorService asyncExecutor;

	/**
	 * References to remote nodes.
	 */
	protected References references;

	/**
	 * Reference on hash function (singleton instance).
	 */
	private final HashFunction hashFunction;

	/**
	 * This node's URL.
	 */
	private URL localURL;

	/**
	 * This node's ID.
	 */
	private ID localID;

	/* constructor */

	/**
	 * Creates a new instance of ChordImpl which initially is disconnected.
	 * Constructor is hidden. Only constructor.
	 */
	public ChordImpl() {
		logger = Logger.getLogger(ChordImpl.class.getName() + ".unidentified");
		logger.setParent(Activator.LOGGER);
		logger.info("Logger initialized.");

		maintenanceTasks = new ScheduledThreadPoolExecutor(3,
				new ChordThreadFactory("MaintenanceTaskExecution"));
		asyncExecutor = Executors.newFixedThreadPool(
				ChordImpl.ASYNC_CALL_THREADS, new ChordThreadFactory(
						"AsynchronousExecution"));
		hashFunction = HashFunction.getHashFunction();
		logger.info("ChordImpl initialized!");
	}

	public final void create() throws ServiceException {

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot create network; node is already connected!");
		}

		// has nodeURL been set?
		if (localURL == null) {
			throw new ServiceException("Node URL is not set yet!");
		}

		// if necessary, generate nodeID out of nodeURL
		if (getID() == null) {
			setID(hashFunction.createUniqueNodeID(localURL));
		}

		// establish connection
		createHelp();

	}

	/* implementation of Chord interface */

	public final void create(final URL localURL1) throws ServiceException {

		// check if parameters are valid
		if (localURL1 == null) {
			throw new NullPointerException(
					"At least one parameter is null which is not permitted!");
		}

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot create network; node is already connected!");
		}

		// set nodeURL
		localURL = localURL1;

		// if necessary, generate nodeID out of nodeURL
		if (getID() == null) {
			setID(hashFunction.createUniqueNodeID(localURL));
		}

		// establish connection
		createHelp();

	}

	public final void create(final URL localURL1, final ID localID1)
			throws ServiceException {

		// check if parameters are valid
		if (localURL1 == null || localID1 == null) {
			throw new IllegalArgumentException(
					"At least one parameter is null which is not permitted!");
		}

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot create network; node is already connected!");
		}

		// set nodeURL
		localURL = localURL1;

		// set nodeID
		setID(localID1);

		// establish connection
		createHelp();

	}

	/**
	 * Performs all necessary tasks for creating a new Chord ring. Assumes that
	 * localID and localURL are correctly set. Is invoked by the methods
	 * {@link #create()}, {@link #create(URL)}, and {@link #create(URL, ID)}
	 * only.
	 * 
	 * @throws RuntimeException
	 */
	private final void createHelp() {

		logger.info("Help method for creating a new Chord ring invoked.");

		// create local repository for entries
		entries = new Entries();

		// create local repository for node references
		if (ChordImpl.NUMBER_OF_SUCCESSORS >= 1) {
			references = new References(getID(), getURL(),
					ChordImpl.NUMBER_OF_SUCCESSORS, entries);
		} else {
			throw new RuntimeException(
					"NUMBER_OF_SUCCESSORS intialized with wrong value! "
							+ ChordImpl.NUMBER_OF_SUCCESSORS);
		}

		// create NodeImpl instance for communication
		localNode = new NodeImpl(this, getID(), localURL, references, entries);

		// create tasks for fixing finger table, checking predecessor and
		// stabilizing
		createTasks();

		// accept content requests from outside
		localNode.acceptEntries();

	}

	/**
	 * Creates the tasks that must be executed periodically to maintain the
	 * Chord overlay network and schedules them with help of a
	 * {@link ScheduledExecutorService}.
	 */
	private final void createTasks() {

		// start thread which periodically stabilizes with successor
		maintenanceTasks.scheduleWithFixedDelay(new StabilizeTask(localNode,
				references, entries), ChordImpl.STABILIZE_TASK_START,
				ChordImpl.STABILIZE_TASK_INTERVAL, TimeUnit.SECONDS);

		// start thread which periodically attempts to fix finger table
		maintenanceTasks.scheduleWithFixedDelay(new FixFingerTask(localNode,
				getID(), references), ChordImpl.FIX_FINGER_TASK_START,
				ChordImpl.FIX_FINGER_TASK_INTERVAL, TimeUnit.SECONDS);

		// start thread which periodically checks whether predecessor has
		// failed
		maintenanceTasks.scheduleWithFixedDelay(new CheckPredecessorTask(
				references), ChordImpl.CHECK_PREDECESSOR_TASK_START,
				ChordImpl.CHECK_PREDECESSOR_TASK_INTERVAL, TimeUnit.SECONDS);
	}

	/**
	 * Returns the Chord node which is responsible for the given key.
	 * 
	 * @param key
	 *            Key for which the successor is searched for.
	 * @throws NullPointerException
	 *             If given ID is <code>null</code>.
	 * @return Responsible node.
	 */
	final Node findSuccessor(final ID key) {

		if (key == null) {
			final NullPointerException e = new NullPointerException(
					"ID to find successor for may not be null!");
			logger.severe("Null pointer.");
			throw e;
		}

		// final boolean debug = logger.isEnabledFor(DEBUG);

		// check if the local node is the only node in the network
		final Node successor = references.getSuccessor();
		if (successor == null) {

			// if (logger.isEnabledFor(INFO)) {
			logger.info("I appear to be the only node in the network, so I am "
					+ "my own " + "successor; return reference on me: "
					+ getID());
			// }
			return localNode;
		}
		// check if the key to look up lies between this node and its successor
		else if (key.isInInterval(getID(), successor.getNodeID())
				|| key.equals(successor.getNodeID())) {
			// if (debug) {
			logger.info("The requested key lies between my own and my "
					+ "successor's node id; therefore return my successor.");
			// }

			// try to reach successor
			try {
				// successor.ping(); // if methods returns, successor is alive.
				// ping removed on 17.09.2007. sven
				// if (debug) {
				logger.info("Returning my successor " + successor.getNodeID()
						+ " of type " + successor.getClass());
				// }
				return successor;
			} catch (final Exception e) {
				// not successful, delete node from successor list and finger
				// table, and set new successor, if available
				logger
						.warning("Successor did not respond! Removing it from all "
								+ "lists and retrying...");
				references.removeReference(successor);
				return findSuccessor(key);
			}
		}

		// ask closest preceding node found in local references for closest
		// preceding node concerning the key to look up
		else {

			final Node closestPrecedingNode = references
					.getClosestPrecedingNode(key);

			try {
				// if (debug) {
				logger
						.info("Asking closest preceding node known to this node for closest preceding node "
								+ closestPrecedingNode.getNodeID()
								+ " concerning key " + key + " to look up");
				// }
				return closestPrecedingNode.findSuccessor(key);
			} catch (final CommunicationException e) {
				logger
						.severe("Communication failure while requesting successor "
								+ "for key "
								+ key
								+ " from node "
								+ closestPrecedingNode.toString()
								+ " - looking up successor for failed node "
								+ closestPrecedingNode.toString());
				references.removeReference(closestPrecedingNode);
				return findSuccessor(key);
			}
		}
	}

	/**
	 * @return The Executor executing asynchronous request.
	 */
	final Executor getAsyncExecutor() {
		if (asyncExecutor == null) {
			throw new NullPointerException("ChordImpl.asyncExecutor is null!");
		}
		return asyncExecutor;
	}

	public final ID getID() {
		return localID;
	}

	public final URL getURL() {
		return localURL;
	}

	public final void insert(final Key key, final Serializable s) {

		// check parameters
		if (key == null || s == null) {
			throw new NullPointerException(
					"Neither parameter may have value null!");
		}

		// determine ID for key
		final ID id = hashFunction.getHashKey(key);
		final Entry entryToInsert = new Entry(id, s);

		// final boolean debug = logger.isEnabledFor(DEBUG);
		// if (debug) {
		logger.info("Inserting new entry with id " + id);
		// }
		boolean inserted = false;
		while (!inserted) {
			// find successor of id
			Node responsibleNode;
			// try {
			responsibleNode = findSuccessor(id);

			// if (debug) {
			logger.info("Invoking insertEntry method on node "
					+ responsibleNode.getNodeID());
			// }

			// invoke insertEntry method
			try {
				responsibleNode.insertEntry(entryToInsert);
				inserted = true;
			} catch (final CommunicationException e1) {
				// if (debug) {
				logger
						.severe("An error occured while invoking the insertEntry method "
								+ " on the appropriate node! Insert operation "
								+ "failed!");
				// }
				continue;
			}
		}
		logger.info("New entry was inserted!");
	}

	public void insert(final Key key, final Serializable entry,
			final ChordCallback callback) {
		final Chord chord = this;
		asyncExecutor.execute(new Runnable() {

			public void run() {
				Throwable t = null;
				try {
					chord.insert(key, entry);
				} catch (final ServiceException e) {
					t = e;
				} catch (final Throwable th) {
					t = th;
				}
				callback.inserted(key, entry, t);
			}
		});
	}

	public ChordFuture insertAsync(final Key key, final Serializable entry) {
		return ChordInsertFuture.create(asyncExecutor, this, key, entry);
	}

	public final void join(final URL bootstrapURL) throws ServiceException {

		// check if parameters are valid
		if (bootstrapURL == null) {
			throw new NullPointerException(
					"At least one parameter is null which is not permitted!");
		}

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot join network; node is already connected!");
		}

		// has nodeURL been set?
		if (localURL == null) {
			throw new ServiceException("Node URL is not set yet! Please "
					+ "set URL with help of setURL(URL) "
					+ "before invoking join(URL)!");
		}

		// if necessary, generate nodeID out of nodeURL
		if (getID() == null) {
			setID(hashFunction.createUniqueNodeID(localURL));
		}

		// establish connection
		joinHelp(bootstrapURL);

	}

	public final void join(final URL localURL1, final ID localID1,
			final URL bootstrapURL) throws ServiceException {

		// check if parameters are valid
		if (localURL1 == null || localID1 == null || bootstrapURL == null) {
			throw new NullPointerException(
					"At least one parameter is null which is not permitted!");
		}

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot join network; node is already connected!");
		}

		// set nodeURL
		localURL = localURL1;

		// set nodeID
		setID(localID1);

		// establish connection
		joinHelp(bootstrapURL);

	}

	public final void join(final URL localURL1, final URL bootstrapURL)
			throws ServiceException {

		// check if parameters are valid
		if (localURL1 == null || bootstrapURL == null) {
			throw new NullPointerException(
					"At least one parameter is null which is not permitted!");
		}

		// is node already connected?
		if (localNode != null) {
			throw new ServiceException(
					"Cannot join network; node is already connected!");
		}

		// set nodeURL
		localURL = localURL1;

		// if necessary, generate nodeID out of nodeURL
		if (getID() == null) {
			setID(hashFunction.createUniqueNodeID(localURL));
		}

		// establish connection
		joinHelp(bootstrapURL);

	}

	/**
	 * Performs all necessary tasks for joining an existing Chord ring. Assumes
	 * that localID and localURL are correctly set. Is invoked by the methods
	 * {@link #join(URL)}, {@link #join(URL, URL)}, and
	 * {@link #join(URL, ID, URL)} only.
	 * 
	 * @param bootstrapURL
	 *            URL of bootstrap node. Must not be null!.
	 * @throws ServiceException
	 *             If anything goes wrong during the join process.
	 * @throws RuntimeException
	 *             Length of successor list has not been initialized correctly.
	 * @throws IllegalArgumentException
	 *             <code>boostrapURL</code> is null!
	 */
	private final void joinHelp(final URL bootstrapURL) throws ServiceException {

		// create local repository for entries
		entries = new Entries();

		// create local repository for node references
		if (ChordImpl.NUMBER_OF_SUCCESSORS >= 1) {
			references = new References(getID(), getURL(),
					ChordImpl.NUMBER_OF_SUCCESSORS, entries);
		} else {
			throw new RuntimeException(
					"NUMBER_OF_SUCCESSORS intialized with wrong value! "
							+ ChordImpl.NUMBER_OF_SUCCESSORS);
		}

		// create NodeImpl instance for communication
		localNode = new NodeImpl(this, getID(), localURL, references, entries);

		// create proxy for outgoing connection to bootstrap node
		Node bootstrapNode;
		try {
			bootstrapNode = Proxy.createConnection(localURL, bootstrapURL);
		} catch (final CommunicationException e) {
			throw new ServiceException(
					"An error occured when creating a proxy for outgoing "
							+ "connection to bootstrap node! Join operation"
							+ "failed!", e);

		}

		// only an optimization: store reference on bootstrap node
		references.addReference(bootstrapNode);

		// Asking for my successor at node bootstrapNode.nodeID

		// find my successor
		Node mySuccessor;
		try {
			mySuccessor = bootstrapNode.findSuccessor(getID());
		} catch (final CommunicationException e1) {
			throw new ServiceException("An error occured when trying to find "
					+ "the successor of this node using bootstrap node "
					+ "with url " + bootstrapURL.toString() + "! Join "
					+ "operation failed!", e1);
		}

		// store reference on my successor
		logger.info(localURL + " has successor " + mySuccessor.getNodeURL());
		references.addReference(mySuccessor);

		// notify successor for the first time and copy keys from successor
		RefsAndEntries copyOfRefsAndEntries;
		try {
			copyOfRefsAndEntries = mySuccessor.notifyAndCopyEntries(localNode);
		} catch (final CommunicationException e2) {
			throw new ServiceException("An error occured when contacting "
					+ "the successor of this node in order to "
					+ "obtain its references and entries! Join "
					+ "operation failed!", e2);
		}

		List<Node> refs = copyOfRefsAndEntries.getRefs();
		/*
		 * The first list item is the current predecessor of our successor. Now
		 * we are the predecessor, so we can assume, that it must be our
		 * predecessor. 10.06.2007 sven.
		 */
		boolean predecessorSet = false;
		// final int count = 0;
		while (!predecessorSet) {
			logger.info("Size of refs: " + refs.size());
			// there is only one other peer in the network
			if (refs.size() == 1) {
				logger
						.info("Adding successor as predecessor as there are only two peers! "
								+ mySuccessor);
				references.addReferenceAsPredecessor(mySuccessor);
				predecessorSet = true;
				logger.info("Actual predecessor: "
						+ references.getPredecessor());
			} else {
				// we got the right predecessor and successor
				if (getID().isInInterval(refs.get(0).getNodeID(),
						mySuccessor.getNodeID())) {
					references.addReferenceAsPredecessor(refs.get(0));
					predecessorSet = true;
				} else {
					/*
					 * if ID of potential predecessor is greater than ours it
					 * can be our successor...
					 */
					logger.info("Wrong successor found. Going backwards!!!");
					references.addReference(refs.get(0));
					try {
						copyOfRefsAndEntries = refs.get(0)
								.notifyAndCopyEntries(localNode);
						refs = copyOfRefsAndEntries.getRefs();
					} catch (final CommunicationException e) {
						throw new ServiceException(
								"An error occured when contacting "
										+ "the successor of this node in order to "
										+ "obtain its references and entries! Join "
										+ "operation failed!", e);
					}
				}
			}
		}

		// add new references, if pings are successful //removed ping to new
		// references. 17.09.2007 sven
		for (final Node newReference : copyOfRefsAndEntries.getRefs()) {
			if (newReference != null && !newReference.equals(localNode)
					&& !references.containsReference(newReference)) {

				references.addReference(newReference);
				// if (logger.isEnabledFor(DEBUG)) {
				logger.info("Added reference on " + newReference.getNodeID()
						+ " which responded to " + "ping request");
				// }

			}
		}

		// add copied entries of successor
		entries.addAll(copyOfRefsAndEntries.getEntries());

		// accept content requests from outside
		localNode.acceptEntries();

		// create tasks for fixing finger table, checking predecessor and
		// stabilizing
		createTasks();
	}

	public final void leave() {

		if (localNode == null) {
			// ring has not been created or joined, st. leave has no effect
			return;
		}

		maintenanceTasks.shutdownNow();

		try {
			final Node successor = references.getSuccessor();
			if (successor != null && references.getPredecessor() != null) {
				successor.leavesNetwork(references.getPredecessor());
			}
		} catch (final CommunicationException e) {
			/*
			 * throw new ServiceException( "An unknown error occured when trying
			 * to contact the " + "successor of this node to inform it about " +
			 * "leaving! Leave operation failed!");
			 */

		}

		localNode.disconnect();
		asyncExecutor.shutdownNow();
		localNode = null;

		HashFunction.close();
	}

	/* Implementation of Report interface */
	public final String printEntries() {
		return entries.toString();
	}

	public final String printFingerTable() {
		return references.printFingerTable();
	}

	public final String printPredecessor() {
		final Node pre = references.getPredecessor();
		if (pre == null) {
			return "Predecessor: null";
		} else {
			return "Predecessor: " + pre.toString();
		}
	}

	public final String printReferences() {
		return references.toString();
	}

	public final String printSuccessorList() {
		return references.printSuccessorList();
	}

	public final void remove(final Key key, final Serializable s) {

		// check parameters
		if (key == null || s == null) {
			throw new NullPointerException(
					"Neither parameter may have value null!");
		}

		// determine ID for key
		final ID id = hashFunction.getHashKey(key);
		final Entry entryToRemove = new Entry(id, s);

		boolean removed = false;
		while (!removed) {

			// final boolean debug = logger.isEnabledFor(DEBUG);
			// if (debug) {
			logger.info("Removing entry with id " + id + " and value " + s);
			// }

			// find successor of id
			Node responsibleNode;
			responsibleNode = findSuccessor(id);

			// if (debug) {
			logger.info("Invoking removeEntry method on node "
					+ responsibleNode.getNodeID());
			// }
			// invoke removeEntry method
			try {
				responsibleNode.removeEntry(entryToRemove);
				removed = true;
			} catch (final CommunicationException e1) {
				// if (debug) {
				logger
						.severe("An error occured while invoking the removeEntry method "
								+ " on the appropriate node! Remove operation "
								+ "failed!");
				// }
				continue;
			}
		}
		logger.info("Entry was removed!");
	}

	public void remove(final Key key, final Serializable entry,
			final ChordCallback callback) {
		final Chord chord = this;
		asyncExecutor.execute(new Runnable() {

			public void run() {
				Throwable t = null;
				try {
					chord.remove(key, entry);
				} catch (final ServiceException e) {
					t = e;
				} catch (final Throwable th) {
					t = th;
				}
				callback.removed(key, entry, t);
			}
		});
	}

	public ChordFuture removeAsync(final Key key, final Serializable entry) {
		return ChordRemoveFuture.create(asyncExecutor, this, key, entry);
	}

	public final Set<Serializable> retrieve(final Key key) {

		// check parameters
		if (key == null) {
			final NullPointerException e = new NullPointerException(
					"Key must not have value null!");
			logger.severe("Null pointer");
			throw e;
		}

		// determine ID for key
		final ID id = hashFunction.getHashKey(key);

		// final boolean debug = logger.isEnabledFor(DEBUG);
		// if (debug) {
		logger.info("Retrieving entries with id " + id);
		// }
		Set<Entry> result = null;

		boolean retrieved = false;
		while (!retrieved) {
			// find successor of id
			Node responsibleNode = null;

			responsibleNode = findSuccessor(id);

			// invoke retrieveEntry method
			try {
				result = responsibleNode.retrieveEntries(id);
				// cause while loop to end.

				retrieved = true;
			} catch (final CommunicationException e1) {
				// if (debug) {
				logger
						.severe("An error occured while invoking the retrieveEntry method "
								+ " on the appropriate node! Retrieve operation "
								+ "failed!");
				// }
				continue;
			}
		}
		final Set<Serializable> values = new HashSet<Serializable>();

		if (result != null) {
			for (final Entry entry : result) {
				values.add(entry.getValue());
			}
		}

		logger.info("Entries were retrieved!");

		return values;

	}

	public void retrieve(final Key key, final ChordCallback callback) {
		final Chord chord = this;
		asyncExecutor.execute(new Runnable() {

			public void run() {
				Throwable t = null;
				Set<Serializable> result = null;
				try {
					result = chord.retrieve(key);
				} catch (final ServiceException e) {
					t = e;
				} catch (final Throwable th) {
					t = th;
				}
				callback.retrieved(key, result, t);
			}
		});
	}

	public ChordRetrievalFuture retrieveAsync(final Key key) {
		return ChordRetrievalFutureImpl.create(asyncExecutor, this, key);
	}

	public final void setID(final ID nodeID) {

		if (nodeID == null) {
			final NullPointerException e = new NullPointerException(
					"Cannot set ID to null!");
			logger.severe("Null pointer");
			throw e;
		}

		if (localNode != null) {
			final IllegalStateException e = new IllegalStateException(
					"ID cannot be set after creating or joining a network!");
			logger.severe("Illegal state.");
			throw e;
		}

		localID = nodeID;
		logger = Logger.getLogger(ChordImpl.class.getName() + "." + localID);
		logger.setParent(Activator.LOGGER);
	}

	public final void setURL(final URL nodeURL) {

		if (nodeURL == null) {
			final NullPointerException e = new NullPointerException(
					"Cannot set URL to null!");
			logger.severe("Null pointer");
			throw e;
		}

		if (localNode != null) {
			final IllegalStateException e = new IllegalStateException(
					"URL cannot be set after creating or joining a network!");
			logger.severe("Illegal state.");
			throw e;
		}

		localURL = nodeURL;

		logger.info("URL was set to " + nodeURL);
	}

	/**
	 * Returns a human-readable string representation containing this node's
	 * node ID and URL.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "Chord node: id = "
				+ (localID == null ? "null" : localID.toString()) + ", url = "
				+ (localURL == null ? "null" : localURL.toString() + "\n");
	}

}
