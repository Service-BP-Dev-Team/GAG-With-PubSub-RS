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
// $Id: UpdateViewEvent.java 2446 2008-08-27 14:09:07Z bboussema $
package inria.smarttools.core.component;

import inria.smarttools.core.util.StEventImpl;

import javax.swing.JPanel;

/**
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2446 $
 */
public class UpdateViewEvent extends StEventImpl {

	//
	// Fields
	//

	private JPanel panel;
	private AbstractContainer container;
	private String info;
	private StyleViewInterface view;

	//
	// Constructors
	//

	/**
	 * Constructor
	 */
	public UpdateViewEvent(JPanel p, AbstractContainer container, String info,
			StyleViewInterface view) {
		panel = p;
		this.container = container;
		this.info = info;
		this.view = view;
	}

	//
	// Methods
	//

	/**
	 * Return a short description of the UpdateLabelEvent object.
	 * 
	 * @return a value of the type 'String' : a string representation of this
	 *         UpdateLabelEvent
	 */
	@Override
	public String toString() {
		return "UpdateViewEvent";
	}

	public JPanel getPanel() {
		return panel;
	}

	public AbstractContainer getContainer() {
		return container;
	}

	public String getInfo() {
		return info;
	}

	public StyleViewInterface getView() {
		return view;
	}

}
