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
// $Id: ComponentDescriptionChecker.java 2450 2008-08-29 08:44:53Z bboussema $
package inria.smarttools.core.component;

import inria.smarttools.core.Activator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2450 $
 */
public class ComponentDescriptionChecker {

	/**
	 * Read Attribut "Components" in jar file to kwno what components this jar
	 * declare.
	 * 
	 * @param jar
	 *            a <code>String</code> value
	 * @return a <code>List</code> value
	 * @exception IOException
	 *                if an error occurs
	 */
	protected static List<String> extractComponentsDefinition(final String jar)
			throws IOException {
		final JarFile jf = new JarFile(jar);
		final Manifest m = jf.getManifest();
		final Attributes at = m.getMainAttributes();
		final String cmps = at.getValue("Components");
		List<String> v = null;
		if (cmps != null) {
			final StringTokenizer st = new StringTokenizer(cmps, " ");
			if (st != null) {
				v = new ArrayList<String>();
			}
			while (st.hasMoreTokens()) {
				v.add(st.nextToken());
			}
		}
		return v;
	}

	public static void main(final String[] args) {
		boolean checkNotDeclared = false;
		final List<ComponentDescription> cmpSrc = new ArrayList<ComponentDescription>();
		final List<ComponentDescription> cmpDest = new ArrayList<ComponentDescription>();

		if (args.length == 0) {
			System.out.println("usage :");
			System.out.println(" ComponentDescriptionChecker options ");
			System.out.println(" options are :");
			System.out.println("   -checkNotDeclared ");
			System.out.println("   -cdml : file1.cdml file2.cdml");
			System.out.println("   -jar : arch1.jar arch2.jar");
		} else {
			for (int i = 0; i < args.length; i++) {
				if ("-checkNotDeclared".equalsIgnoreCase(args[i])) {
					checkNotDeclared = true;
				} else if ("-cdml".equalsIgnoreCase(args[i])) {
					try {
						final URL url1 = new URL(args[++i]);
						final URL url2 = new URL(args[++i]);

						final ComponentDescription cmp1 = new ComponentDescriptionImpl();
						final ComponentDescription cmp2 = new ComponentDescriptionImpl();

						cmp1.setResourceFilename(url1.getFile());
						cmp2.setResourceFilename(url2.getFile());

						cmp1.process();
						cmp2.process();

						cmpSrc.add(cmp1);
						cmpDest.add(cmp2);
					} catch (final MalformedURLException e) {
						Activator.LOGGER.throwing(
								ComponentDescriptionChecker.class.getName(),
								"public static void main(String[] args)", e);
					}
				} else if ("-jar".equalsIgnoreCase(args[i])) {
					try {
						final URL url1 = new URL(args[++i]);
						final URL url2 = new URL(args[++i]);

						String path = url1.getFile();
						List<String> v = ComponentDescriptionChecker
								.extractComponentsDefinition(path);

						if (v != null) {
							final int size = v.size();
							for (int ii = 0; ii < size; ii++) {
								final String urlName = "jar:file:"
										+ url1.getFile() + "!/" + v.get(ii);
								final URL url_tmp = new URL(urlName);
								if (url_tmp != null) {
									final ComponentDescription cmp = new ComponentDescriptionImpl();
									cmp.setResourceFilename(urlName);
									cmp.process();
									cmpSrc.add(cmp);
								}
							}
						}

						path = url2.getFile();
						v = ComponentDescriptionChecker
								.extractComponentsDefinition(path);

						if (v != null) {
							final int size = v.size();
							for (int ii = 0; ii < size; ii++) {
								final String urlName = "jar:file:"
										+ url2.getFile() + "!/" + v.get(ii);
								final URL url_tmp = new URL(urlName);
								if (url_tmp != null) {
									final ComponentDescription cmp = new ComponentDescriptionImpl();
									cmp.setResourceFilename(urlName);
									cmp.process();
									cmpDest.add(cmp);
								}
							}
						}

					} catch (final Exception e) {
						Activator.LOGGER.throwing(
								ComponentDescriptionChecker.class.getName(),
								"public static void main(String[] args)", e);
					}
				}
			}
			ComponentDescriptionChecker cdc = null;
			ComponentDescription cmp1 = null;
			ComponentDescription cmp2 = null;
			for (int i = 0; i < cmpSrc.size(); i++) {
				cmp1 = cmpSrc.get(i);
				for (int j = 0; j < cmpDest.size(); j++) {
					cmp2 = cmpDest.get(j);
					cdc = new ComponentDescriptionChecker(cmp1, cmp2);
					cdc.setCheckNotDeclared(checkNotDeclared);
					cdc.check();
				}
			}
		}
	}
	protected ComponentDescription component1 = null;

	protected ComponentDescription component2 = null;

	protected boolean checkNotDeclared = false;

	public ComponentDescriptionChecker(final ComponentDescription cmp1,
			final ComponentDescription cmp2) {
		component1 = cmp1;
		component2 = cmp2;
	}

	public void check() {
		//
		// Cmp1 Outputs -> Cmp2 Inputs
		//
		checkOutIn(component1, component2);
		//
		// Cmp2 Outputs -> Cmp1 Inputs
		//
		checkOutIn(component2, component1);
	}

	protected void checkOutIn(final ComponentDescription c1,
			final ComponentDescription c2) {
		final Map<String, Output> cmp1_Outputs = c1.getOutputs();
		final Map<String, Input> cmp2_Inputs = c2.getInputs();
		final Map<String, Object> outputsNotConnected = new HashMap<String, Object>();

		final Iterator<String> iterOut = cmp1_Outputs.keySet().iterator();
		while (iterOut.hasNext()) {
			final String name = iterOut.next();
			if (cmp2_Inputs.get(name) == null) {
				outputsNotConnected.put(name, cmp1_Outputs.get(name));
			} else {
				// Parameters Declaration compatibility
				final Input input = cmp2_Inputs.get(name);
				cmp2_Inputs.remove(input);
				// Output output = cmp1_Outputs.get(name);
				// HashMap<String, Object> parameters = input.getParameters();
				// Arg[] args = output.getArgs();
				// if (args.length == parameters.size()) {
				// for (int i = 0; i < args.length; i++) {
				// Arg arg = args[i];
				// String argName = arg.getName();
				// Attribute attr = (Attribute) parameters.get(argName);
				// if (attr != null) {
				// String typeArg = arg.getType();
				// String typeAttr = attr.getType();
				// }
				// }
				// }
			}
		}
	}

	public boolean isCheckNotDeclared() {
		return checkNotDeclared;
	}

	public void setCheckNotDeclared(final boolean v) {
		checkNotDeclared = v;
	}

	@Override
	public String toString() {
		return "ComponentDescriptionChecker";
	}
}
