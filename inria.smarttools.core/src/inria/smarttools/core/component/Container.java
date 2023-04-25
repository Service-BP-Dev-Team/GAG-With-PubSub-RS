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
// $Id: Container.java 2928 2009-11-28 09:01:21Z bboussema $
package inria.smarttools.core.component;

import inria.smarttools.core.util.DisconnectEvent;
import inria.smarttools.core.util.MulticastProvider;

/**
 * Common behavior of SmartTools Container : - modify attribute value - excute a
 * service - manage message - retrieve and change state (ON, OFF, SLEEP) -
 * communication
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2928 $
 */
public interface Container extends ContainerProxy, Runnable {

	public static final short ON = 0;
	public static final short OFF = 1;
	public static final short SLEEP = 2;

	public void addMulticastProvider(MulticastProvider mProvider);

	/**
	 * Connect this component with other one. i.e. : - discover services -
	 * connect compatible one - ...
	 * 
	 * @param other
	 *            a <code>Container</code> value : the other component
	 */
	public void connect(ContainerProxy other);

	public void disconnect(DisconnectEvent event);

	/**
	 * Disconnect communication with other component
	 * 
	 * @param cmpName
	 *            the <code>IdName</code> of the other component
	 */
	public void disconnect(String cmpName);

	public ComponentDescription getContainerDescription();

	public String getIdName();

	/**
	 * Set this component state SLEEP : hibernate, we can usr it but action are
	 * logged and wait that it can be use.
	 */
	public void hibernate();

	public boolean isDisconnectable();

	public void quit();

	/**
	 * Behavior on a message reception - set embedded attributes - call
	 * corresponding service
	 * 
	 * @param msg
	 *            a <code>Message</code> value
	 */
	public void receive(Message msg);

	public void releaseWaitingMsg();

	public void removeMulticastProvider(MulticastProvider mProvider);

	public void setAttribute(String name, Object value, String type);

	public void setState(short v);

	public void shutdown();

	/**
	 * Set this component state OFF : not usable
	 */
	public void switchOff();

	/**
	 * Set this component state ON : ready to use
	 */
	public void switchOn();

	/**
	 * Rescane declarer Input/Output to update connections.
	 * 
	 * @param other
	 *            a <code>Container</code> value
	 */
	public void updateConnect(ContainerProxy other, boolean dual);

	/**
	 * Wakeup a component : Set is state to ON and eventually execute waiting
	 * call.
	 */
	public void wakeup();
}
