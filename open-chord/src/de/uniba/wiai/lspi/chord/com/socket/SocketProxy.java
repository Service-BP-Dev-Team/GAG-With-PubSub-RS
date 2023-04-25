/***************************************************************************
 *                                                                         *
 *                             SocketProxy.java                            *
 *                            -------------------                          *
 *   date                 : 12.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                          karsten.loesing@uni-bamberg.de                 *
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
package de.uniba.wiai.lspi.chord.com.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.Proxy;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * This is the implementation of {@link Proxy} for the socket protocol. This
 * connects to the {@link SocketEndpoint endpoint} of the node it represents by
 * means of <code>Sockets</code>.
 * 
 * @author sven
 * @version 1.0.5
 */
public final class SocketProxy extends Proxy implements Runnable {

	/**
	 * Wraps a thread, which is waiting for a response.
	 * 
	 * @author sven
	 */
	private static class WaitingThread {

		private boolean hasBeenWokenUp = false;

		private final Thread thread;

		private WaitingThread(final Thread thread) {
			this.thread = thread;
		}

		/**
		 * Returns <code>true</code> when the thread has been woken up by
		 * invoking {@link #wakeUp()}
		 * 
		 * @return
		 */
		boolean hasBeenWokenUp() {
			return hasBeenWokenUp;
		}

		@Override
		public String toString() {
			return thread.toString() + ": Waiting? " + !hasBeenWokenUp();
		}

		/**
		 * Wake up the thread that is waiting for a response.
		 */
		void wakeUp() {
			hasBeenWokenUp = true;
			thread.interrupt();
		}
	}
	/**
	 * The logger for instances of this class.
	 */
	private final static Logger logger = Logger.getLogger(SocketProxy.class
			.getName());

	/**
	 * Map of existing proxies. Key: {@link String}, Value: {@link SocketProxy}.
	 * changed on 21.03.2006 by sven. See documentation of method
	 * {@link #createProxyKey(URL, URL)}
	 */
	private static Map<String, SocketProxy> proxies = new HashMap<String, SocketProxy>();

	static {
		SocketProxy.logger.setParent(Activator.LOGGER);
	}

	/**
	 * Establishes a connection from <code>urlOfLocalNode</code> to
	 * <code>url</code>. The connection is represented by the returned
	 * <code>SocketProxy</code>.
	 * 
	 * @param url
	 *            The {@link URL} to connect to.
	 * @param urlOfLocalNode
	 *            {@link URL} of local node that establishes the connection.
	 * @return <code>SocketProxy</code> representing the established connection.
	 * @throws CommunicationException
	 *             Thrown if establishment of connection to <code>url</code>
	 *             failed.
	 */
	public static SocketProxy create(final URL urlOfLocalNode, final URL url)
			throws CommunicationException {
		synchronized (SocketProxy.proxies) {
			/*
			 * added on 21.03.2006 by sven. See documentation of method
			 * createProxyKey(URL, URL);
			 */
			final String proxyKey = SocketProxy.createProxyKey(urlOfLocalNode,
					url);
			SocketProxy.logger.info("Known proxies "
					+ SocketProxy.proxies.keySet());
			if (SocketProxy.proxies.containsKey(proxyKey)) {
				SocketProxy.logger.info("Returning existing proxy for " + url);
				return SocketProxy.proxies.get(proxyKey);
			} else {
				SocketProxy.logger.info("Creating new proxy for " + url);
				final SocketProxy newProxy = new SocketProxy(url,
						urlOfLocalNode);
				SocketProxy.proxies.put(proxyKey, newProxy);
				return newProxy;
			}
		}
	}

	/**
	 * Creates a <code>SocketProxy</code> representing the connection from
	 * <code>urlOfLocalNode</code> to <code>url</code>. The connection is
	 * established when the first (remote) invocation with help of the
	 * <code>SocketProxy</code> occurs.
	 * 
	 * @param url
	 *            The {@link URL} of the remote node.
	 * @param urlOfLocalNode
	 *            The {@link URL} of local node.
	 * @param nodeID
	 *            The {@link ID} of the remote node.
	 * @return SocketProxy
	 */
	protected static SocketProxy create(final URL url,
			final URL urlOfLocalNode, final ID nodeID) {
		synchronized (SocketProxy.proxies) {
			/*
			 * added on 21.03.2006 by sven. See documentation of method
			 * createProxyKey(String, String);
			 */
			final String proxyKey = SocketProxy.createProxyKey(urlOfLocalNode,
					url);
			SocketProxy.logger.info("Known proxies "
					+ SocketProxy.proxies.keySet());
			if (SocketProxy.proxies.containsKey(proxyKey)) {
				SocketProxy.logger.info("Returning existing proxy for " + url);
				return SocketProxy.proxies.get(proxyKey);
			} else {
				SocketProxy.logger.info("Creating new proxy for " + url);
				final SocketProxy proxy = new SocketProxy(url, urlOfLocalNode,
						nodeID);
				SocketProxy.proxies.put(proxyKey, proxy);
				return proxy;
			}
		}
	}

	/**
	 * Method that creates a unique key for a SocketProxy to be stored in
	 * {@link #proxies}. This is important for the methods
	 * {@link #create(URL, URL)}, {@link #create(URL, URL, ID)}, and
	 * {@link #disconnect()}, so that socket communication also works when it is
	 * used within one JVM. Added by sven 21.03.2006, as before SocketProxy were
	 * stored in {@link #proxies} with help of their remote URL as key, so that
	 * they were a kind of singleton for that URL. But the key has to consist of
	 * the URL of the local peer, that uses the proxy, and the remote URL as
	 * SocketProxies must only be (kind of) a singleton per local and remote
	 * URL.
	 * 
	 * @param localURL
	 * @param remoteURL
	 * @return The key to store the SocketProxy
	 */
	private static String createProxyKey(final URL localURL, final URL remoteURL) {
		return localURL.toString() + "->" + remoteURL.toString();
	}

	/**
	 * Closes all outgoing connections to other peers. Allows the local peer to
	 * shutdown cleanly.
	 */
	static void shutDownAll() {
		synchronized (SocketProxy.proxies) {
			final Set<String> keys = SocketProxy.proxies.keySet();
			for (final String key : keys) {
				SocketProxy.proxies.get(key).disconnect();
			}
			SocketProxy.proxies.clear();
		}
	}

	/**
	 * The {@link URL}of the node that uses this proxy to connect to the node,
	 * which is represented by this proxy.
	 */
	private URL urlOfLocalNode = null;

	/**
	 * Counter for requests that have been made by this proxy. Also required to
	 * create unique identifiers for {@link Request requests}.
	 */
	private long requestCounter = -1;

	/**
	 * The socket that provides the connection to the node that this is the
	 * Proxy for. This is transient as a proxy can be transferred over the
	 * network. After transfer this socket has to be restored by reconnecting to
	 * the node.
	 */
	private transient Socket mySocket;

	/**
	 * The {@link ObjectOutputStream}this Proxy writes objects to. This is
	 * transient as a proxy can be transferred over the network. After transfer
	 * this stream has to be restored.
	 */
	private transient ObjectOutputStream out;

	/**
	 * The {@link ObjectInputStream}this Proxy reads objects from. This is
	 * transient as a proxy can be transferred over the network. After transfer
	 * this stream has to be restored.
	 */
	private transient ObjectInputStream in;

	/**
	 * The {@link ObjectInputStream} this Proxy reads objects from. This is
	 * transient as a proxy can be transferred over the network. After transfer
	 * this stream has to be restored.
	 */
	private transient Map<String, Response> responses;

	/**
	 * {@link Map} where threads are put in that are waiting for a repsonse.
	 * Key: identifier of the request (same as for the response). Value: The
	 * Thread itself.
	 */
	private transient Map<String, WaitingThread> waitingThreads;

	/**
	 * This indicates that an exception occured while waiting for responses and
	 * that the connection to the {@link Node node}, that this is the proxy for,
	 * could not be reestablished.
	 */
	private volatile boolean disconnected = false;

	/**
	 * The string representation of this proxy. Created when {@link #toString()}
	 * is invoked for the first time.
	 */
	private String stringRepresentation = null;

	/**
	 * Corresponding constructor to factory method {@link #create(URL, URL)}.
	 * 
	 * @see #create(URL, URL)
	 * @param url
	 * @param urlOfLocalNode1
	 * @throws CommunicationException
	 */
	private SocketProxy(final URL url, final URL urlOfLocalNode1)
			throws CommunicationException {
		super(url);
		if (url == null || urlOfLocalNode1 == null) {
			throw new IllegalArgumentException("URLs must not be null!");
		}
		urlOfLocalNode = urlOfLocalNode1;
		initializeNodeID();
		SocketProxy.logger
				.info("SocketProxy for " + url + " has been created.");
	}

	/**
	 * Corresponding constructor to factory method {@link #create(URL, URL, ID)}
	 * .
	 * 
	 * @see #create(URL, URL, ID)
	 * @param url
	 * @param urlOfLocalNode1
	 * @param nodeID1
	 */
	protected SocketProxy(final URL url, final URL urlOfLocalNode1,
			final ID nodeID1) {
		super(url);
		if (url == null || urlOfLocalNode1 == null || nodeID1 == null) {
			throw new IllegalArgumentException("null");
		}
		urlOfLocalNode = urlOfLocalNode1;
		nodeID = nodeID1;
	}

	/**
	 * Method to indicate that connection to remote {@link Node node} is broken
	 * down.
	 */
	private void connectionBrokenDown() {
		if (responses == null) {
			/*
			 * Nothing to do!
			 */
			return;
		}
		/* synchronize on responses, as all threads accessing this proxy do so */
		synchronized (responses) {
			SocketProxy.logger.info("Connection broken down!");
			disconnected = true;
			/* wake up all threads */
			for (final WaitingThread thread : waitingThreads.values()) {
				SocketProxy.logger
						.info("Interrupting waiting thread " + thread);
				thread.wakeUp();
			}
		}
	}

	/**
	 * Private method to create an identifier that enables this to associate a
	 * {@link Response response}with a {@link Request request}made before. This
	 * method is synchronized to protect {@link #requestCounter}from race
	 * conditions.
	 * 
	 * @param methodIdentifier
	 *            Integer identifying the method this method is called from.
	 * @return Unique Identifier for the request.
	 */
	private synchronized String createIdentifier(final int methodIdentifier) {
		/* Create unique identifier from */
		final StringBuilder uid = new StringBuilder();
		/* Time stamp */
		uid.append(System.currentTimeMillis());
		uid.append("-");
		/* counter and */
		uid.append(requestCounter++);
		/* methodIdentifier */
		uid.append("-");
		uid.append(methodIdentifier);
		return uid.toString();
	}

	/**
	 * Creates a request for the method identified by
	 * <code>methodIdentifier</code> with the parameters <code>parameters</code>
	 * . Sets also field {@link Request#getReplyWith()}of created
	 * {@link Request request}.
	 * 
	 * @param methodIdentifier
	 *            The identifier of the method to request.
	 * @param parameters
	 *            The parameters for the request.
	 * @return The {@link Request request}created.
	 */
	private Request createRequest(final int methodIdentifier,
			final Serializable[] parameters) {
		// if (SocketProxy.logger.isEnabledFor(DEBUG)) {
		SocketProxy.logger.info("Creating request for method "
				+ MethodConstants.getMethodName(methodIdentifier)
				+ " with parameters "
				+ java.util.Arrays.deepToString(parameters));
		// }
		final String responseIdentifier = createIdentifier(methodIdentifier);
		final Request request = new Request(methodIdentifier,
				responseIdentifier);
		request.setParameters(parameters);
		SocketProxy.logger.info("Request " + request + " created.");
		return request;
	}

	/**
	 * Tells this proxy that it is not needed anymore.
	 */
	@Override
	public void disconnect() {

		SocketProxy.logger.info("Destroying connection from " + urlOfLocalNode
				+ " to " + nodeURL);

		synchronized (SocketProxy.proxies) {
			/*
			 * added on 21.03.2006 by sven. See documentation of method
			 * createProxyKey(String, String);
			 */
			final String proxyKey = SocketProxy.createProxyKey(urlOfLocalNode,
					nodeURL);
			final Object o = SocketProxy.proxies.remove(proxyKey);
		}
		disconnected = true;
		try {
			if (out != null) {
				try {
					/*
					 * notify endpoint this is connected to, about shut down of
					 * this proxy
					 */
					SocketProxy.logger
							.info("Sending shutdown notification to endpoint.");
					final Request request = createRequest(
							MethodConstants.SHUTDOWN, new Serializable[0]);
					SocketProxy.logger.info("Notification send.");
					out.writeObject(request);
					out.close();
					out = null;
					SocketProxy.logger.info("OutputStream " + out + " closed.");
				} catch (final IOException e) {
					/* should not occur */
					SocketProxy.logger.severe(this
							+ ": Exception during closing of output stream "
							+ out);
				}
			}
			if (in != null) {
				try {
					in.close();
					SocketProxy.logger.info("InputStream " + in + " closed.");
					in = null;
				} catch (final IOException e) {
					/* should not occur */
					SocketProxy.logger
							.info("Exception during closing of input stream"
									+ in);
				}
			}
			if (mySocket != null) {
				try {
					SocketProxy.logger.info("Closing socket " + mySocket + ".");
					mySocket.close();
				} catch (final IOException e) {
					/* should not occur */
					SocketProxy.logger
							.info("Exception during closing of socket "
									+ mySocket);
				}
				mySocket = null;
			}
		} catch (final Throwable t) {
			SocketProxy.logger
					.warning("Unexpected exception during disconnection of SocketProxy");
		}
		connectionBrokenDown();
	}

	/**
	 * Finalization ensures that the socket is closed if this proxy is not
	 * needed anymore.
	 * 
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		SocketProxy.logger.info("Finalization running.");
	}

	/**
	 * @param key
	 * @return The successor of <code>key</code>.
	 * @throws CommunicationException
	 */
	@Override
	public Node findSuccessor(final ID key) throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to find successor for ID " + key);

		/* prepare request for method findSuccessor */
		final Request request = createRequest(MethodConstants.FIND_SUCCESSOR,
				new Serializable[] { key });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.info("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			try {
				final RemoteNodeInfo nodeInfo = (RemoteNodeInfo) response
						.getResult();
				if (nodeInfo.getNodeURL().equals(urlOfLocalNode)) {
					return Endpoint.getEndpoint(urlOfLocalNode).getNode();
				} else {
					return SocketProxy.create(nodeInfo.getNodeURL(),
							urlOfLocalNode, nodeInfo.getNodeID());
				}
			} catch (final ClassCastException e) {
				/*
				 * This should not occur as all nodes should have the same
				 * classes!
				 */
				final String message = "Could not understand result! "
						+ response.getResult();
				SocketProxy.logger.severe(message);
				throw new CommunicationException(message, e);
			}
		}
	}

	/**
	 * @return The id of the node represented by this proxy.
	 * @throws CommunicationException
	 */
	private void initializeNodeID() throws CommunicationException {
		if (nodeID == null) {
			makeSocketAvailable();

			SocketProxy.logger.info("Trying to get node ID ");

			/* prepare request for method findSuccessor */
			final Request request = createRequest(MethodConstants.GET_NODE_ID,
					new Serializable[0]);
			/* send request */
			try {
				SocketProxy.logger.info("Trying to send request " + request);
				send(request);
			} catch (final CommunicationException ce) {
				SocketProxy.logger.severe("Connection failed!");
				throw ce;
			}
			/* wait for response */
			SocketProxy.logger.info("Waiting for response for request "
					+ request);
			final Response response = waitForResponse(request);
			SocketProxy.logger.info("Response " + response + " arrived.");
			if (response.isFailureResponse()) {
				throw new CommunicationException(response.getFailureReason());
			} else {
				try {
					nodeID = (ID) response.getResult();
				} catch (final ClassCastException e) {
					/*
					 * This should not occur as all nodes should have the same
					 * classes!
					 */
					final String message = "Could not understand result! "
							+ response.getResult();
					SocketProxy.logger.severe(message);
					throw new CommunicationException(message);
				}
			}
		}
	}

	/**
	 * @param entry
	 * @throws CommunicationException
	 */
	@Override
	public void insertEntry(final Entry entry) throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to insert entry " + entry + ".");

		/* prepare request for method insertEntry */
		final Request request = createRequest(MethodConstants.INSERT_ENTRY,
				new Serializable[] { entry });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			/* No result here */
			return;
		}
	}

	/**
	 * @param replicas
	 * @throws CommunicationException
	 */
	@Override
	public void insertReplicas(final Set<Entry> replicas)
			throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to insert replicas " + replicas + ".");

		/* prepare request for method insertEntry */
		final Request request = createRequest(MethodConstants.INSERT_REPLICAS,
				new Serializable[] { (Serializable) replicas });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			/* No result here */
			return;
		}
	}

	/**
	 * @param predecessor
	 * @throws CommunicationException
	 */
	@Override
	public void leavesNetwork(final Node predecessor)
			throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to insert notify node that "
				+ predecessor + " leaves network.");

		final RemoteNodeInfo nodeInfo = new RemoteNodeInfo(predecessor
				.getNodeURL(), predecessor.getNodeID());

		/* prepare request for method insertEntry */
		final Request request = createRequest(MethodConstants.LEAVES_NETWORK,
				new Serializable[] { nodeInfo });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			/* No result here */
			return;
		}
	}

	/**
	 * This method has to be called at first in every method that uses the
	 * socket to connect to the node this is the proxy for. This method
	 * establishes the connection if not already done. This method has to be
	 * called as this proxy can be serialized and the reference to the socket is
	 * transient. So by calling this method after a transfer the connection to
	 * the node is reestablished. The same applies for {@link #logger}and
	 * {@link #responses}.
	 * 
	 * @throws CommunicationException
	 */
	private void makeSocketAvailable() throws CommunicationException {
		if (disconnected) {
			throw new CommunicationException("Connection from "
					+ urlOfLocalNode + " to remote host " + nodeURL
					+ " is broken down. ");
		}

		SocketProxy.logger.info("makeSocketAvailable() called. "
				+ "Testing for socket availability");

		if (responses == null) {
			responses = new HashMap<String, Response>();
		}
		if (waitingThreads == null) {
			waitingThreads = new HashMap<String, WaitingThread>();
		}
		if (mySocket == null) {
			try {
				SocketProxy.logger.info("Opening new socket to " + nodeURL);
				mySocket = new Socket(nodeURL.getHost(), nodeURL.getPort());
				SocketProxy.logger.info("Socket created: " + mySocket);
				mySocket.setSoTimeout(5000);
				out = new ObjectOutputStream(mySocket.getOutputStream());
				in = new ObjectInputStream(mySocket.getInputStream());
				SocketProxy.logger.info("Sending connection request!");
				out.writeObject(new Request(MethodConstants.CONNECT,
						"Initial Connection"));
				try {
					// set time out, in case the other side does not answer!
					Response resp = null;
					boolean timedOut = false;
					try {
						SocketProxy.logger
								.info("Waiting for connection response!");
						resp = (Response) in.readObject();
					} catch (final SocketTimeoutException e) {
						SocketProxy.logger.info("Connection timed out!");
						timedOut = true;
					}
					mySocket.setSoTimeout(0);
					if (timedOut) {
						throw new CommunicationException(
								"Connection to remote host timed out!");
					}
					if (resp != null
							&& resp.getStatus() == Response.REQUEST_SUCCESSFUL) {
						final Thread t = new Thread(this, "SocketProxy_Thread_"
								+ nodeURL);
						t.start();
					} else {
						throw new CommunicationException(
								"Establishing connection failed!");
					}
				} catch (final ClassNotFoundException e) {
					throw new CommunicationException(
							"Unexpected result received! " + e.getMessage(), e);
				} catch (final ClassCastException e) {
					throw new CommunicationException(
							"Unexpected result received! " + e.getMessage(), e);
				}
			} catch (final UnknownHostException e) {
				throw new CommunicationException("Unknown host: "
						+ nodeURL.getHost());
			} catch (final IOException ioe) {
				throw new CommunicationException("Could not set up IO channel "
						+ "to host " + nodeURL.getHost(), ioe);
			}
		}
		SocketProxy.logger.info("makeSocketAvailable() finished. Socket "
				+ mySocket);
	}

	/**
	 * @param potentialPredecessor
	 * @return List of references for the node invoking this method. See
	 *         {@link Node#notify(Node)}.
	 */
	@Override
	public List<Node> notify(final Node potentialPredecessor)
			throws CommunicationException {
		makeSocketAvailable();

		final RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(
				potentialPredecessor.getNodeURL(), potentialPredecessor
						.getNodeID());

		final Request request = createRequest(MethodConstants.NOTIFY,
				new Serializable[] { nodeInfoToSend });

		/* send request to remote node. */
		try {
			send(request);
		} catch (final CommunicationException e) {
			throw e;
		}

		/* wait for response to arrive */
		final Response response = waitForResponse(request);
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			try {
				final List<RemoteNodeInfo> references = (List<RemoteNodeInfo>) response
						.getResult();
				final List<Node> nodes = new LinkedList<Node>();
				for (final RemoteNodeInfo nodeInfo : references) {
					if (nodeInfo.getNodeURL().equals(urlOfLocalNode)) {
						nodes.add(Endpoint.getEndpoint(urlOfLocalNode)
								.getNode());
					} else {
						nodes.add(SocketProxy.create(nodeInfo.getNodeURL(),
								urlOfLocalNode, nodeInfo.getNodeID()));
					}
				}
				return nodes;
			} catch (final ClassCastException cce) {
				throw new CommunicationException(
						"Could not understand result! " + response.getResult(),
						cce);
			}
		}
	}

	/**
	 * @param potentialPredecessor
	 * @return See {@link Node#notifyAndCopyEntries(Node)}.
	 * @throws CommunicationException
	 */
	@Override
	public RefsAndEntries notifyAndCopyEntries(final Node potentialPredecessor)
			throws CommunicationException {
		makeSocketAvailable();

		final RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(
				potentialPredecessor.getNodeURL(), potentialPredecessor
						.getNodeID());

		/* prepare request for method notifyAndCopyEntries */
		final Request request = createRequest(MethodConstants.NOTIFY_AND_COPY,
				new Serializable[] { nodeInfoToSend });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.info("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason(),
					response.getThrowable());
		} else {
			try {
				final RemoteRefsAndEntries result = (RemoteRefsAndEntries) response
						.getResult();
				final List<Node> newReferences = new LinkedList<Node>();
				final List<RemoteNodeInfo> references = result.getNodeInfos();
				for (final RemoteNodeInfo nodeInfo : references) {
					if (nodeInfo.getNodeURL().equals(urlOfLocalNode)) {
						newReferences.add(Endpoint.getEndpoint(urlOfLocalNode)
								.getNode());
					} else {
						newReferences.add(SocketProxy.create(nodeInfo
								.getNodeURL(), urlOfLocalNode, nodeInfo
								.getNodeID()));
					}
				}
				return new RefsAndEntries(newReferences, result.getEntries());
			} catch (final ClassCastException cce) {
				throw new CommunicationException(
						"Could not understand result! " + response.getResult());
			}
		}
	}

	/**
	 * @throws CommunicationException
	 */
	@Override
	public void ping() throws CommunicationException {
		makeSocketAvailable();

		// final boolean debugEnabled = SocketProxy.logger.isEnabledFor(DEBUG);
		//
		// if (debugEnabled) {
		SocketProxy.logger.info("Trying to ping remote node " + nodeURL);
		// }

		/* prepare request for method findSuccessor */
		final Request request = createRequest(MethodConstants.PING,
				new Serializable[0]);
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		// if (debugEnabled) {
		SocketProxy.logger.info("Waiting for response for request " + request);
		// }
		final Response response = waitForResponse(request);
		// if (debugEnabled) {
		SocketProxy.logger.info("Response " + response + " arrived.");
		// }
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			return;
		}

	}

	/**
	 * @param entry
	 * @throws CommunicationException
	 */
	@Override
	public void removeEntry(final Entry entry) throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to remove entry " + entry + ".");

		/* prepare request for method findSuccessor */
		final Request request = createRequest(MethodConstants.REMOVE_ENTRY,
				new Serializable[] { entry });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			/* No result here */
			return;
		}

	}

	/**
	 * @param sendingNodeID
	 * @param replicas
	 * @throws CommunicationException
	 */
	@Override
	public void removeReplicas(final ID sendingNodeID, final Set<Entry> replicas)
			throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to remove replicas " + replicas + ".");

		/* prepare request for method insertEntry */
		final Request request = createRequest(MethodConstants.REMOVE_REPLICAS,
				new Serializable[] { sendingNodeID, (Serializable) replicas });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason());
		} else {
			/* No result here */
			return;
		}
	}

	/**
	 * This method is called by {@link #run()}when it receives a
	 * {@link Response}. The {@link Thread thread}waiting for response is woken
	 * up and the response is put into {@link Map responses}.
	 * 
	 * @param response
	 */
	private void responseReceived(final Response response) {
		synchronized (responses) {
			/* Try to fetch thread waiting for this response */
			SocketProxy.logger.info("No of waiting threads " + waitingThreads);
			final WaitingThread waitingThread = waitingThreads.get(response
					.getInReplyTo());
			SocketProxy.logger.info("Response with id "
					+ response.getInReplyTo() + "received.");
			/* save response */
			responses.put(response.getInReplyTo(), response);
			/* if there is a thread waiting for this response */
			if (waitingThread != null) {
				/* wake up the thread */
				SocketProxy.logger.info("Waking up thread!");
				waitingThread.wakeUp();
			} else {
				// TODO what else? why 'else' anyway?
			}
		}
	}

	@Override
	public Set<Entry> retrieveEntries(final ID id)
			throws CommunicationException {
		makeSocketAvailable();

		SocketProxy.logger.info("Trying to retrieve entries for ID " + id);

		/* prepare request for method findSuccessor */
		final Request request = createRequest(MethodConstants.RETRIEVE_ENTRIES,
				new Serializable[] { id });
		/* send request */
		try {
			SocketProxy.logger.info("Trying to send request " + request);
			send(request);
		} catch (final CommunicationException ce) {
			SocketProxy.logger.severe("Connection failed!");
			throw ce;
		}
		/* wait for response */
		SocketProxy.logger.info("Waiting for response for request " + request);
		final Response response = waitForResponse(request);
		SocketProxy.logger.info("Response " + response + " arrived.");
		if (response.isFailureResponse()) {
			throw new CommunicationException(response.getFailureReason(),
					response.getThrowable());
		} else {
			try {
				final Set<Entry> result = (Set<Entry>) response.getResult();
				return result;
			} catch (final ClassCastException cce) {
				throw new CommunicationException(
						"Could not understand result! " + response.getResult());
			}
		}
	}

	/**
	 * The run methods waits for incoming
	 * {@link de.uniba.wiai.lspi.chord.com.socket.Response} made by this proxy
	 * and puts them into a datastructure from where the can be collected by the
	 * associated method call that made a
	 * {@link de.uniba.wiai.lspi.chord.com.socket.Request} to the {@link Node},
	 * that this is the proxy for.
	 */
	public void run() {
		while (!disconnected) {
			try {
				final Response response = (Response) in.readObject();
				SocketProxy.logger.info("Response " + response + "received!");
				responseReceived(response);
			} catch (final ClassNotFoundException cnfe) {
				/* should not occur, as all classes must be locally available */
				SocketProxy.logger
						.severe("ClassNotFoundException occured during deserialization "
								+ "of response. There is something seriously wrong "
								+ " here! ");
			} catch (final IOException e) {
				if (!disconnected) {
					SocketProxy.logger
							.warning("Could not read response from stream!");
				} else {
					SocketProxy.logger.info(this
							+ ": Connection has been closed!");
				}
				connectionBrokenDown();
			}
		}
	}

	/**
	 * Private method to send requests over the socket. This method is
	 * synchronized to ensure that no other thread concurrently accesses the
	 * {@link ObjectOutputStream output stream}<code>out</code> while sending
	 * {@link Request request}.
	 * 
	 * @param request
	 *            The {@link Request}to be sent.
	 * @throws CommunicationException
	 *             while writing to {@link ObjectOutputStream output stream}.
	 */
	private synchronized void send(final Request request)
			throws CommunicationException {
		try {
			SocketProxy.logger
					.info("Sending request " + request.getReplyWith());
			out.writeObject(request);
			out.flush();
			out.reset();
		} catch (final IOException e) {
			throw new CommunicationException("Could not connect to node "
					+ nodeURL, e);
		}
	}

	/**
	 * @return String representation of this.
	 */
	public String toString() {
		if (nodeID == null || mySocket == null) {
			return "Unconnected SocketProxy from " + urlOfLocalNode + " to "
					+ nodeURL;
		}
		if (stringRepresentation == null) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Connection from Node[url=");
			builder.append(urlOfLocalNode);
			builder.append(", socket=");
			builder.append(mySocket);
			builder.append("] to Node[id=");
			builder.append(nodeID);
			builder.append(", url=");
			builder.append(nodeURL);
			builder.append("]");
			stringRepresentation = builder.toString();
		}
		return stringRepresentation;
	}

	/**
	 * Called in a method that is delegated to the {@link Node node}, that this
	 * is the proxy for. This method blocks the thread that calls the particular
	 * method until a {@link Response response} is received.
	 * 
	 * @param request
	 * @return The {@link Response} for <code>request</code>.
	 * @throws CommunicationException
	 */
	private Response waitForResponse(final Request request)
			throws CommunicationException {

		final String responseIdentifier = request.getReplyWith();
		Response response = null;
		SocketProxy.logger.info("Trying to wait for response with identifier "
				+ responseIdentifier + " for method "
				+ MethodConstants.getMethodName(request.getRequestType()));

		synchronized (responses) {
			SocketProxy.logger.info("No of responses " + responses.size());
			/* Test if we got disconnected while waiting for lock on object */
			if (disconnected) {
				throw new CommunicationException("Connection to remote host "
						+ " is broken down. ");
			}
			/*
			 * Test if response is already available (Maybe response arrived
			 * before we reached this point).
			 */
			response = responses.remove(responseIdentifier);
			if (response != null) {
				return response;
			}

			/* WAIT FOR RESPONSE */
			/* add current thread to map of threads waiting for a response */
			final WaitingThread wt = new WaitingThread(Thread.currentThread());
			waitingThreads.put(responseIdentifier, wt);
			while (!wt.hasBeenWokenUp()) {
				try {
					/*
					 * Wait until notified or time out is reached.
					 */
					SocketProxy.logger.info("Waiting for response to arrive.");
					responses.wait();
				} catch (final InterruptedException e) {
					/*
					 * does not matter as this is intended Thread is interrupted
					 * if response arrives
					 */
				}
			}
			SocketProxy.logger
					.info("Have been woken up from waiting for response.");

			/* remove thread from map of threads waiting for a response */
			waitingThreads.remove(responseIdentifier);
			/* try to get the response if available */
			response = responses.remove(responseIdentifier);
			SocketProxy.logger.info("Response for request with identifier "
					+ responseIdentifier + " for method "
					+ MethodConstants.getMethodName(request.getRequestType())
					+ " received.");
			/* if no response availabe */
			if (response == null) {
				SocketProxy.logger.info("No response received.");
				/* we have been disconnected */
				if (disconnected) {
					SocketProxy.logger.info("Connection to remote host lost.");
					throw new CommunicationException(
							"Connection to remote host " + " is broken down. ");
				}
				/* or time out has elapsed */
				else {
					SocketProxy.logger
							.severe("There is no result, but we have not been "
									+ "disconnected. Something went seriously wrong!");
					throw new CommunicationException(
							"Did not receive a response!");
				}
			}
		}
		return response;
	}

}
