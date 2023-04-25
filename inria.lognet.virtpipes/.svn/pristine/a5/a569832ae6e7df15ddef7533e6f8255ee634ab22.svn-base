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
// $Id: VirtPipeOutputDefault.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.virtpipes;

import java.util.UUID;

/**
 * Implementation of VirtPipeOutput by default, so you don't have to inherit
 * from VirtPipeOutput and create another class. Useful for tests.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public class VirtPipeOutputDefault extends VirtPipeOutput {

	public VirtPipeOutputDefault(final VirtPipesService virtPipesService,
			final UUID id) {
		super(virtPipesService, id);
	}

	@Override
	public final void error(final Exception e) {
		Activator.LOGGER.severe("VirtPipeOutput > " + getId() + " > "
				+ e.getMessage());
		Activator.LOGGER.throwing(VirtPipeOutputDefault.class.getName(),
				"error", e);
	}

	@Override
	public void virtPipeClosed() {
		Activator.LOGGER.info("VirtPipe " + getId() + " closed");
	}

}
