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
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * VirtPipesModule is about information about pipes and resolving
 * VirtPipesService IP addresses and port. You can do it many ways, like a
 * repertoire, a key-value storage, etc.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision$
 */
public interface VirtPipesModule {

	public List<InetSocketAddress> getSocketAddresses(UUID vps);

	public VirtPipeOutput getVirtPipeOutput(UUID id);

	public Set<UUID> getVirtPipesService(UUID vp);

	public boolean isUuidIsUnique(final UUID id);

	public void registerVirtPipeInput(UUID id);

	public void setVirtPipesService(VirtPipesService vps);

	public void unregisterVirtPipe(UUID id);
}
