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
// $Id: InOut.java 2738 2009-02-24 13:36:10Z bboussema $
package inria.smarttools.core.component;

import java.util.Map;

/**
 * InOut.java
 * 
 * <pre>
 * Decription d'un service IN/OUT : request/response.
 * - name       : nom
 * - Arg        : argument a vehiculer dans la reponse 
 *                (un message de type &lt;outputName&gt;)
 * - methodName : nom de la methode a invoke sur la Facade 
 *                a la reception de la requete.
 * - outputName : nom de l'OUTPUT a produire en reponse [peux deja exister 
 *                sous forme d'un output simple]
 * - doc        : documentation
 * </pre>
 * 
 * @author Didier.Parigot@sophia.inria.fr
 * @version $Revision: 2738 $
 */
public class InOut extends Input implements Service {

	protected String outputName;
	protected String arg;

	public InOut(final String name, final Map<String, Attribute> attributes,
			final String arg, final String methodName, final String outputName,
			final String doc) {
		super(name, attributes, methodName, doc);
		this.arg = arg;
		this.outputName = outputName;
	}

	public String getArg() {
		return arg;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setArg(final String v) {
		arg = v;
	}

	public void setOutputName(final String v) {
		outputName = v;
	}
}
