/***************************************************************************
 *                                                                         *
 *                            RequestHandler.java                          *
 *                            -------------------                          *
 *   date                 : 02.09.2004, 14:02                              *
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

/*
 * RequestHandler.java
 *
 * Created on 2. September 2004, 14:02
 */

package de.uniba.wiai.lspi.chord.com.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.EndpointStateListener;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * This class handles {@link Request requests} for a single incoming connection
 * from another node sent through a {@link SocketProxy proxy} that represents
 * the local node at the remote node.
 * 
 * @author sven
 * @version 1.0.5
 */
final class RequestHandler extends Thread implements EndpointStateListener {

	/**
	 * Logger for this class.
	 */
	private static Logger logger = Logger.getLogger(RequestHandler.class
			.getName());
	static {
		RequestHandler.logger.setParent(Activator.LOGGER);
	}

	/**
	 * The node this RequestHandler invokes methods on.
	 */
	private Node node;

	/**
	 * The socket over that this RequestHandler receives requests.
	 */
	private Socket connection;

	/**
	 * {@link ObjectOutputStream}to write answers to.
	 */
	private ObjectOutputStream out;

	/**
	 * {@link ObjectInputStream}to read {@link Request requests}from.
	 */
	private ObjectInputStream in;

	/**
	 * Indicates if this RequestHandler is connected. Used in {@link #run()} to
	 * determine if this is still listening for requests.
	 */
	boolean connected = true;

	/**
	 * The state that the {@link SocketEndpoint endpoint}, that started this
	 * request handler, is currently in. See constants of class
	 * {@link de.uniba.wiai.lspi.chord.com.Endpoint}.
	 */
	private int state;

	/**
	 * The {@link SocketEndpoint endpoint}that started this handler.
	 */
	private final SocketEndpoint endpoint;

	/**
	 * This {@link Vector}contains {@link Thread threads}waiting for a state of
	 * the {@link SocketEndpoint endpoint}that permits the execution of the
	 * methods the threads are about to execute. This is also used as
	 * synchronization variable for these threads.
	 */
	private final Set<Thread> waitingThreads = new HashSet<Thread>();

	/**
	 * Creates a new instance of RequestHandler
	 * 
	 * @param node_
	 *            The {@link Node node}to delegate requested methods to.
	 * @param connection_
	 *            The {@link Socket}over which this receives requests.
	 * @param ep
	 * @throws IOException
	 * @throws IOException
	 *             Thrown if the establishment of a connection over the provided
	 *             socket fails.
	 */
	RequestHandler(final Node node_, final Socket connection_,
			final SocketEndpoint ep) throws IOException {
		super("RequestHandler_" + ep.getURL());

		// if (RequestHandler.logger.isEnabledFor(INFO)) {
		RequestHandler.logger.info("Initialising RequestHandler. Socket "
				+ connection_ + ", " + ", Endpoint " + ep);
		// }
		// logger = Logger.getLogger(this.getClass().toString() +
		// connection.toString());
		node = node_;
		connection = connection_;
		out = new ObjectOutputStream(connection.getOutputStream());
		try {
			in = new ObjectInputStream(connection.getInputStream());
		} catch (final IOException e1) {
			out.close();
			throw e1;
		}
		try {
			final Request r = (Request) in.readObject();
			if (r.getRequestType() != MethodConstants.CONNECT) {
				final Response resp = new Response(Response.REQUEST_FAILED, r
						.getRequestType(), r.getReplyWith());
				try {
					out.writeObject(resp);
				} catch (final IOException e) {}
				try {
					out.close();
				} catch (final IOException e) {}
				try {
					in.close();
				} catch (final IOException e) {}
				throw new IOException("Unexpected Message received! " + r);
			} else {
				final Response resp = new Response(Response.REQUEST_SUCCESSFUL,
						r.getRequestType(), r.getReplyWith());
				out.writeObject(resp);
			}
		} catch (final ClassNotFoundException e) {
			throw new IOException("Unexpected class type received! "
					+ e.getMessage());
		}
		endpoint = ep;
		state = endpoint.getState();
		endpoint.register(this);
		RequestHandler.logger.info("RequestHandler initialised.");
	}

	/**
	 * Disconnect this RequestHandler. Forces the socket, which this
	 * RequestHandler is bound to, to be closed and {@link #run()}to be stopped.
	 */
	public void disconnect() {

		RequestHandler.logger.info("Disconnecting.");
		if (connected) {
			/* cause the while loop in run() method to be finished */
			/* and notify all threads waiting for execution of a method */
			synchronized (waitingThreads) {
				connected = false;
				waitingThreads.notifyAll();
			}
			/* release reference to node. */
			node = null;
			/* try to close the socket */
			try {
				synchronized (out) {
					out.close();
					out = null;
				}
			} catch (final IOException e) {
				/* should not occur */
				/* if closing of socket fails, that does not matter!??? */
				RequestHandler.logger
						.severe("Exception while closing output stream " + out);
			}
			try {
				in.close();
				in = null;
			} catch (final IOException e) {
				/* should not occur */
				/* if closing of socket fails, that does not matter!??? */
				RequestHandler.logger
						.severe("Exception while closing input stream" + in);
			}
			try {
				RequestHandler.logger.info("Closing socket " + connection);
				connection.close();
				connection = null;
				RequestHandler.logger.info("Socket closed.");
			} catch (final IOException e) {
				/* should not occur */
				/* if closing of socket fails, that does not matter!??? */
				RequestHandler.logger.severe("Exception while closing socket "
						+ connection);
			}
			endpoint.deregister(this);
		}
		RequestHandler.logger.info("Disconnected.");
	}

	/**
	 * Returns a reference to the endpoint this {@link RequestHandler} belongs
	 * to.
	 * 
	 * @return Reference to the endpoint this {@link RequestHandler} belongs to.
	 */
	SocketEndpoint getEndpoint() {
		return endpoint;
	}

	/**
	 * Invokes methods on {@link #node}.
	 * 
	 * @param methodType
	 *            The type of the method to invoke. See {@link MethodConstants}.
	 * @param parameters
	 *            The parameters to pass to the method.
	 * @return The result of the invoked method. May be <code>null</code> if
	 *         method is void.
	 * @throws Exception
	 */
	Serializable invokeMethod(final int methodType,
			final Serializable[] parameters) throws Exception {

		final String method = MethodConstants.getMethodName(methodType);
		waitForMethod(method);
		/* If we got disconnected while waiting */
		if (!connected) {
			/* throw an Exception */
			throw new CommunicationException("Connection closed.");
		}
		Serializable result = null;
		RequestHandler.logger.info("Trying to invoke method " + methodType
				+ " with parameters: ");
		for (final Serializable parameter : parameters) {
			RequestHandler.logger.info(parameter.toString());
		}
		switch (methodType) {
			case MethodConstants.FIND_SUCCESSOR: {
				final Node chordNode = node.findSuccessor((ID) parameters[0]);
				result = new RemoteNodeInfo(chordNode.getNodeURL(), chordNode
						.getNodeID());
				break;
			}
			case MethodConstants.GET_NODE_ID: {
				result = node.getNodeID();
				break;
			}
			case MethodConstants.INSERT_ENTRY: {
				node.insertEntry((Entry) parameters[0]);
				break;
			}
			case MethodConstants.INSERT_REPLICAS: {
				node.insertReplicas((Set<Entry>) parameters[0]);
				break;
			}
			case MethodConstants.LEAVES_NETWORK: {
				final RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
				node.leavesNetwork(SocketProxy.create(nodeInfo.getNodeURL(),
						node.getNodeURL(), nodeInfo.getNodeID()));
				break;
			}
			case MethodConstants.NOTIFY: {
				final RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
				final List<Node> l = node
						.notify(SocketProxy.create(nodeInfo.getNodeURL(), node
								.getNodeURL(), nodeInfo.getNodeID()));
				final List<RemoteNodeInfo> nodeInfos = new LinkedList<RemoteNodeInfo>();
				for (final Node current : l) {
					nodeInfos.add(new RemoteNodeInfo(current.getNodeURL(),
							current.getNodeID()));
				}
				result = (Serializable) nodeInfos;
				break;
			}
			case MethodConstants.NOTIFY_AND_COPY: {
				final RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
				final RefsAndEntries refs = node
						.notifyAndCopyEntries(SocketProxy.create(nodeInfo
								.getNodeURL(), node.getNodeURL(), nodeInfo
								.getNodeID()));
				final List<Node> l = refs.getRefs();
				final List<RemoteNodeInfo> nodeInfos = new LinkedList<RemoteNodeInfo>();
				for (final Node current : l) {
					nodeInfos.add(new RemoteNodeInfo(current.getNodeURL(),
							current.getNodeID()));
				}
				final RemoteRefsAndEntries rRefs = new RemoteRefsAndEntries(
						refs.getEntries(), nodeInfos);
				result = rRefs;
				break;
			}
			case MethodConstants.PING: {
				RequestHandler.logger.info("Invoking ping()");
				node.ping();
				RequestHandler.logger.info("ping() invoked.");
				break;
			}
			case MethodConstants.REMOVE_ENTRY: {
				node.removeEntry((Entry) parameters[0]);
				break;
			}
			case MethodConstants.REMOVE_REPLICAS: {
				node.removeReplicas((ID) parameters[0],
						(Set<Entry>) parameters[1]);
				break;
			}
			case MethodConstants.RETRIEVE_ENTRIES: {
				result = (Serializable) node
						.retrieveEntries((ID) parameters[0]);
				break;
			}
			default: {
				RequestHandler.logger.severe("Unknown method requested "
						+ method);
				throw new Exception("Unknown method requested " + method);
			}
		}
		RequestHandler.logger.info("Returning result.");
		return result;
	}

	/**
	 * Test if this RequestHandler is disconnected
	 * 
	 * @return <code>true</code> if this is still connected to its remote end.
	 */
	public boolean isConnected() {
		return connected;
	}

	public void notify(final int newState) {
		RequestHandler.logger.info("notify(" + newState + ") called.");
		state = newState;
		/* notify all threads waiting for a state change */
		synchronized (waitingThreads) {
			RequestHandler.logger.info("HERE!!! Notifying waiting threads. "
					+ waitingThreads);
			waitingThreads.notifyAll();
		}
	}

	/**
	 * The task of this Thread. Listens for incoming requests send over the
	 * {@link #connection}of this thread. The thread can be stopped by invoking
	 * {@link #disconnect()}.
	 */
	@Override
	public void run() {
		/*
		 * As long as this is connected
		 */
		while (connected) {
			Request request = null;
			try {
				/* wait for incoming requests */
				RequestHandler.logger.info("Waiting for request...");
				request = (Request) in.readObject();
				if (request.getRequestType() == MethodConstants.SHUTDOWN) {
					RequestHandler.logger.info("Received shutdown request");
					disconnect();
				} else {
					RequestHandler.logger.info("Received request " + request);
					new InvocationThread(this, request, /*
														 * "Request_" +
														 * MethodConstants
														 * .getMethodName
														 * (request
														 * .getRequestType()) +
														 * "_" +
														 * request.getReplyWith
														 * (),
														 */out);
				}
			} catch (final IOException e) {
				/*
				 * This can also occur if disconnect() is called, as the socket
				 * is closed then
				 */
				RequestHandler.logger
						.info("Exception occured while receiving a request. "
								+ "Maybe socket has been closed.");
				/* cannot do anything here but disconnect */
				disconnect();
			} catch (final ClassNotFoundException cnf) {
				/* Should not occur as all nodes should have the same classes */
				RequestHandler.logger
						.severe("Exception occured while receiving a request ");
				/* cannot do anything here */
				disconnect();
			} catch (final Throwable t) {
				RequestHandler.logger
						.severe("Unexpected throwable while receiving message!");
				disconnect();
			}
		}
	}

	/**
	 * Method to create failure responses and send them to the requestor.
	 * 
	 * @param t
	 * @param failure
	 * @param request
	 */
	void sendFailureResponse(final Throwable t, final String failure,
			final Request request) {
		if (!connected) {
			return;
		}
		RequestHandler.logger
				.info("Trying to send failure response. Failure reason "
						+ failure);
		final Response failureResponse = new Response(Response.REQUEST_FAILED,
				request.getRequestType(), request.getReplyWith());
		failureResponse.setFailureReason(failure);
		failureResponse.setThrowable(t);
		try {
			synchronized (out) {
				out.writeObject(failureResponse);
				out.flush();
				out.reset();
			}
			RequestHandler.logger.info("Response send.");
		} catch (final IOException e) {
			if (connected) {
				RequestHandler.logger
						.severe("Connection seems to be broken down. Could not "
								+ "send failure response. Connection is closed. ");
				disconnect();
			}
		}
	}

	/**
	 * This method is used to block threads that want to make a method call
	 * until the method invocation is permitted by the endpoint. Invocation of a
	 * method depends on the state of the endpoint.
	 * 
	 * @param method
	 *            The name of the method to invoke. TODO: change this to another
	 *            type.
	 */
	private void waitForMethod(final String method) {

		RequestHandler.logger
				.info(method
						+ " allowed? "
						+ !(Collections.binarySearch(
								Endpoint.METHODS_ALLOWED_IN_ACCEPT_ENTRIES,
								method) >= 0));
		synchronized (waitingThreads) {
			while (!(state == Endpoint.ACCEPT_ENTRIES)
					&& connected
					&& Collections.binarySearch(
							Endpoint.METHODS_ALLOWED_IN_ACCEPT_ENTRIES, method) >= 0) {

				final Thread currentThread = Thread.currentThread();
				// final boolean debug =
				// RequestHandler.logger.isEnabledFor(DEBUG);
				// if (debug) {
				RequestHandler.logger.info("HERE!!!" + currentThread
						+ " waiting for permission to " + "execute " + method);
				// }
				waitingThreads.add(currentThread);
				try {
					waitingThreads.wait();
				} catch (final InterruptedException e) {
					// do nothing
				}
				// if (debug) {
				RequestHandler.logger.info("HERE!!!" + currentThread
						+ " has been notified.");
				// }
				waitingThreads.remove(currentThread);
			}
		}
		RequestHandler.logger.info("waitForMethod(" + method + ") returns!");
	}

}
