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
// $Id: ParserDef.java 2450 2008-08-29 08:44:53Z bboussema $
package inria.smarttools.core.component;

import java.util.List;

/**
 * Describe class <code>ParserDef</code> here.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2450 $
 */
public class ParserDef {

	protected String generator;
	protected String file;
	protected String type;
	protected List<String> extentions = null;
	protected String classname;

	public ParserDef(final String type, final List<String> extentions,
			final String classname, final String generator, final String file) {
		this.type = type;
		this.extentions = extentions;
		this.classname = classname;
		this.generator = generator;
		this.file = file;
	}

	public String getClassname() {
		return classname;
	}

	public List<String> getExtentions() {
		return extentions;
	}

	public String getFile() {
		return file;
	}

	public String getGenerator() {
		return generator;
	}

	public String getType() {
		return type;
	}

	public void setClassname(final String v) {
		classname = v;
	}

	public void setExtentions(final List<String> v) {
		extentions = v;
	}

	public void setFile(final String v) {
		file = v;
	}

	public void setGenerator(final String v) {
		generator = v;
	}

	public void setType(final String v) {
		type = v;
	}
}
