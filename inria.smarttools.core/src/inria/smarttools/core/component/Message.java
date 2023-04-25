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
// $Id: Message.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.component;

import java.io.Serializable;

/**
 * Description of a SmartTools Message. Mandatory Data: - name - attributes
 * (depends of Message Definition) - expeditor Extra data: - data
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2446 $
 */
public interface Message extends Serializable {

	public PropertyMap getAttributesForMessage();

	public PropertyMap getAttributesForRecipient();

	public String getExpeditorId();

	public String getExpeditorType();

	public ContainerProxy getExpeditor();

	public String getName();

	public boolean isSended();

	public void setAttributesForMessage(PropertyMap attrs);

	public void setAttributesForRecipient(PropertyMap attrs);

	public void setExpeditor(ContainerProxy v);

	public void setName(String name);

	public void setSended(boolean v);

	public void setAdresseeId(String adresseeId);

	public void setAdresseeType(String adresseeType);

	public String getAdresseeType();
	
	public boolean isAsynch();
	
	public void setAsynch(boolean b);

	public String getAdresseeId();
}
