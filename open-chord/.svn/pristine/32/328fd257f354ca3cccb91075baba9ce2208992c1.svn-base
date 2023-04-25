/***************************************************************************
 *                                                                         *
 *                            SocketEndpoint.java                          *
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.Activator;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * This class represents an {@link Endpoint} for communication over socket
 * protocol. It provides a <code>ServerSocket</code> to that clients can connect
 * and starts for each incoming connection a
 * {@link de.uniba.wiai.lspi.chord.com.socket.RequestHandler} that handles
 * {@link de.uniba.wiai.lspi.chord.com.socket.Request}s for method invocations
 * from remote nodes. These {@link de.uniba.wiai.lspi.chord.com.socket.Request}s
 * are sent by one {@link SocketProxy} representing the node, that this is the
 * endpoint for, at another node.
 * 
 * @author sven
 * @version 1.0.5
 */
public final class SocketEndpoint extends Endpoint implements Runnable {

	/**
	 * Logger for this endpoint.
	 */
	private final static Logger logger = Logger.getLogger(SocketEndpoint.class
			.getName());
	static {
		SocketEndpoint.logger.setParent(Activator.LOGGER);
	}

	// private final static boolean debug = SocketEndpoint.logger
	// .isEnabledFor(DEBUG);

	/**
	 * {@link Set} containing all {@link RequestHandler}s created by this
	 * endpoint.
	 */
	private final Set<RequestHandler> handlers = new HashSet<RequestHandler>();

	/**
	 * The Socket this endpoint listens to for connections.
	 */
	private ServerSocket mySocket = null;

	/**
	 * The {@link java.util.concurrent.Executor} responsible for carrying out
	 * executions of methods with help of an instance of
	 * {@link InvocationThread}.
	 */
	private final ThreadPoolExecutor invocationExecutor = InvocationThread
			.createInvocationThreadPool();

	/**
	 * Creates a new <code>SocketEndpoint</code> for the given {@link Node} with
	 * {@link URL url}. <code>url</code> must have the protocol indexed by
	 * <code>{@link URL#SOCKET_PROTOCOL}</code> in the
	 * <code>{@link URL#KNOWN_PROTOCOLS}</code> array.
	 * 
	 * @param node1
	 *            The {@link Node} node this endpoint provides connections to.
	 * @param url1
	 *            The {@link URL} of this endpoint.
	 */
	public SocketEndpoint(final Node node1, final URL url1) {
		super(node1, url1);
		SocketEndpoint.logger.info("Initialisation finished.");
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#closeConnections()
	 */
	@Override
	protected void closeConnections() {
		setState(Endpoint.STARTED);
		/* try to close socket */
		try {
			mySocket.close();
		} catch (final IOException e) {
			/* should not occur */
			// if (SocketEndpoint.debug) {
			SocketEndpoint.logger.severe("Could not close socket " + mySocket);
		}
		// }
		invocationExecutor.shutdownNow();
		/*
		 * Close outgoing connections.
		 */
		SocketProxy.shutDownAll();
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#entriesAcceptable()
	 */
	@Override
	protected void entriesAcceptable() {
		// if (SocketEndpoint.debug) {
		SocketEndpoint.logger.info("entriesAcceptable() called");
		// }
		setState(Endpoint.ACCEPT_ENTRIES);
	}

	/*
	 * (non-Javadoc)
	 * @see de.uniba.wiai.lspi.chord.com.Endpoint#openConnections()
	 */
	@Override
	protected void openConnections() {
		/* Open server socket on port specified by url */
		try {
			// if (SocketEndpoint.debug) {
			SocketEndpoint.logger.info("Trying to open server socket on port "
					+ url.getPort());
			// }
			mySocket = new ServerSocket(url.getPort());
			if (url.getPort() == 0) {
				url.setPort(mySocket.getLocalPort());
			}
			SocketEndpoint.logger.info("Changing state");
			setState(Endpoint.LISTENING);
			// if (SocketEndpoint.debug) {
			SocketEndpoint.logger.info("Server socket opened on port "
					+ url.getPort() + ". Starting listener thread.");
			// }
			/* and start thread to listen for incoming connections. */
			final Thread listenerThread = new Thread(this, "SocketEndpoint_"
					+ url + "_Thread");
			listenerThread.start();
			// if (SocketEndpoint.debug) {
			SocketEndpoint.logger.info("Listener Thread " + listenerThread
					+ "started. ");
			// }
		} catch (final IOException e) {
			/* TODO: change type of exception */
			throw new RuntimeException(
					"SocketEndpoint could not listen on port " + url.getPort()
							+ " " + e.getMessage());
		}
	}

	/**
	 * Run method from {@link Runnable} to accept connections from clients. This
	 * method runs until {@link #closeConnections()} is called. It creates
	 * threads responsible for the handling of requests from other nodes.
	 */
	public void run() {

		try {
			// Cleaner cleaner = new Cleaner();
			while (!Thread.currentThread().isInterrupted()) {
				while (getState() == Endpoint.STARTED) {
					// if (SocketEndpoint.debug) {
					synchronized (this) {
						try {
							wait(500);
						} catch (final InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
					SocketEndpoint.logger
							.info("Waiting for incoming connection.");
				}
				Socket incomingConnection = null;
				incomingConnection = mySocket.accept();
				// if (SocketEndpoint.debug) {
				SocketEndpoint.logger.info("Incoming connection "
						+ incomingConnection);
				// }
				/*
				 * Create a handler for requests that come in over the newly
				 * created socket.
				 */
				// if (SocketEndpoint.debug) {
				SocketEndpoint.logger
						.info("Creating request handler for incoming connection.");
				// }
				final RequestHandler handler = new RequestHandler(node,
						incomingConnection, this);
				/*
				 * Remember handler to be able to close its connection (shut it
				 * down.
				 */
				handlers.add(handler);
				/* Start handler thread */
				// if (SocketEndpoint.info) {
				SocketEndpoint.logger
						.info("Request handler created. Starting thread.");
				// }
				handler.start();
				// if (SocketEndpoint.debug) {
				SocketEndpoint.logger.info("Request handler thread started.");
				// }

			}
		} catch (final IOException e) {

		}
		SocketEndpoint.logger.info("Listener thread stopped.");
		/* Disconnect all */
		for (final RequestHandler handler : handlers) {
			handler.disconnect();
		}
		handlers.clear();
	}

	/**
	 * Schedule an invocation of a local method to be executed.
	 * 
	 * @param invocationThread
	 */
	void scheduleInvocation(final InvocationThread invocationThread) {
		// if (SocketEndpoint.debug) {
		SocketEndpoint.logger
				.info("Scheduling invocation: " + invocationThread);
		// }
		invocationExecutor.execute(invocationThread);
		// if (SocketEndpoint.debug) {
		SocketEndpoint.logger.info("Current jobs: "
				+ invocationExecutor.getQueue().size());
		SocketEndpoint.logger.info("Active jobs: "
				+ invocationExecutor.getActiveCount());
		SocketEndpoint.logger.info("Completed jobs: "
				+ invocationExecutor.getCompletedTaskCount());
		// }

	}

}
