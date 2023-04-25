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
// $Id: VirtPipeOutput.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.virtpipes;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Represents the Output end of a VirtPipe, identified by id. You own thread can
 * call send for sending a message/data as a bytes array form. The other end(s)
 * (VirtPipeInput) will be notified that they received the data you had just
 * sent.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public abstract class VirtPipeOutput {

	private final UUID id;

	private final VirtPipesService virtPipesService;

	public VirtPipeOutput(final VirtPipesService virtPipesService, final UUID id) {
		this.virtPipesService = virtPipesService;
		this.id = id;
	}

	public final void dispose() {}

	/**
	 * Notification that an error occured with your sending.
	 * 
	 * @param e
	 */
	public abstract void error(final Exception e);

	/**
	 * @return id of the VirtPipe (output end)
	 */
	public final UUID getId() {
		return id;
	}

	public final VirtPipesService getVirtPipesService() {
		return virtPipesService;
	}

	/**
	 * Call this when you need to send information through this VirtPipe
	 * 
	 * @param buf
	 *            message/data to send
	 */
	public final void send(final ByteBuffer buf) {
		virtPipesService.send(id, buf, this);
	}

	/**
	 * Notify that no VirtPipeInput exists anywhere.
	 */
	public abstract void virtPipeClosed();
}
