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
// $Id: ChannelsPoolsController.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.channels;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor of threads, mainly use for 1 thread with ChannelsPool. Not used so
 * much, the idea here was to execute many ChannelsPools, so many threads. So it
 * is not complete.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public final class ChannelsPoolsController {

	private final ExecutorService executorService = Executors
			.newCachedThreadPool();

	public ChannelsPoolsController() {

	}

	public final void addRunnable(final Runnable runnable) {
		executorService.execute(runnable);
	}

	public final void addRunnables(final Set<Runnable> runnables) {
		for (final Runnable runnable : runnables) {
			addRunnable(runnable);
		}
	}

	public final void removeChannelsPools(final Set<ChannelsPool> channelsPools) {

	}
}
