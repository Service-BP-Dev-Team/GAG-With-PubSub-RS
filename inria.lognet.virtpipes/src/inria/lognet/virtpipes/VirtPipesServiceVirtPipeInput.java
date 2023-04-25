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

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public class VirtPipesServiceVirtPipeInput implements VirtPipeInput {

	/**
	 * UUID of the VirtPipe of this service.
	 */
	private final UUID id;

	public VirtPipesServiceVirtPipeInput(final VirtPipesService virtPipesService) {
		id = virtPipesService.generateUniqueId();
	}

	public UUID getId() {
		return id;
	}

	public void read(final ByteBuffer buffer) {}

}
