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
package inria.lognet.virtpipes;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public final class VirtPipesServiceUtil {

	/**
	 * JSON parser for the IP addresses of a remote VirtPipesService.
	 * 
	 * @param ips
	 * @return list of Socket Addresses
	 */
	public final static List<InetSocketAddress> parse(final String ips) {
		final List<InetSocketAddress> isas = new ArrayList<InetSocketAddress>();
		final int port = Integer.valueOf(ips
				.substring(ips.lastIndexOf(',') + 1));
		final String[] listIp = ips.substring(ips.indexOf('{') + 1,
				ips.indexOf('}')).split(",");
		for (final String element : listIp) {
			if (element.length() > 1) {
				final String[] name = element
						.substring(1, element.length() - 1).split("/");
				if (name[0].equals("")) {
					isas.add(new InetSocketAddress(name[1], port));
				} else {
					isas.add(new InetSocketAddress(name[0], port));
				}
			}
		}
		return isas;
	}

	private VirtPipesServiceUtil() {}
}
