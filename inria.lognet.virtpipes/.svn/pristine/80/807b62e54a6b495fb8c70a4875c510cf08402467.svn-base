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
// $Id: VirtPipeInput.java 2875 2009-06-23 10:56:16Z bboussema $
package inria.lognet.virtpipes;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Represents a part of a VirtPipe where you read data. It is message passing
 * model. You are notified when you have received a message. It is your job to
 * know how to deal with this message in the bytes array form.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2875 $
 */
public interface VirtPipeInput {

	/**
	 * @return ID of the VirtPipe (input end)
	 */
	public UUID getId();

	/**
	 * User code notified when a data/message arrived. Please don't be long to
	 * give the hand back, or create a job/thread to deal the next operations.
	 * 
	 * @param buffer
	 */
	public void read(ByteBuffer buffer);

}
