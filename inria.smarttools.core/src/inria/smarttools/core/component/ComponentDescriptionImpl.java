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
// $Id: ComponentDescriptionImpl.java 3070 2010-11-17 10:37:55Z gverger $
package inria.smarttools.core.component;

import inria.smarttools.core.Activator;
import inria.smarttools.core.util.AutoKeyHashMap;
import inria.smarttools.core.util.KeyAccessor;
import inria.smarttools.dynamic.STUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import sun.awt.PlatformFont;

/**
 * This class provide a easy way to manipulate SmartTools component description
 * file. It's load the xml file and build an equivalent Java Object. SmartTools
 * component description file :
 * 
 * <pre>
 *   - component : [name, type, extends, ns]
 *     - formalism      : [name, dtd]
 *     - parser*        : [type, extension, classname]
 *  
 *     - containerclass : [name]
 *     - facadeclass    : [name]
 *     - documentation  :
 *  
 *     - attribute* : [doc, javatype, name, ns]
 *  
 *     - input*     : [doc, method, name, ns]
 *         - attribute* : [doc, javatype, name, ns]
 *         - binding    : [toMethod]
 *            - attribute*      : [type, attribute_ref, name]
 *     - output*    : [doc, name, method, ns]
 *         - attribute*       : [name, doc, javatype]
 *     - inout*     : [doc, name, method, output, ns]
 *         - attribute*       : [name, doc, javatype]
 * 
 * </pre>
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 3070 $
 */
public final class ComponentDescriptionImpl implements ComponentDescription {

	private XMLStreamReader parser = null;

	protected String absyntFilename = null;

	protected AutoKeyHashMap<String, Attribute> attributes = new AutoKeyHashMap<String, Attribute>(
			new KeyAccessor<String, Attribute>() {

				public String getKey(final Attribute o) {
					return o.getName();
				}
			});

	protected String behavior = null;

	private ClassLoader cl;

	protected String componentDesc = null;

	protected String componentName = null;
	protected String componentType = null;
	protected String parentComponent = null;
	protected String parentComponentName = null;
	protected String containerClassname = null;
	protected String containerNamespace = null;
	protected Map<String, String> dependances = new HashMap<String, String>();
	protected String doctypeName = null;
	protected List<String> documentation = new Vector<String>();
	protected String dtdName = null;
	protected String facadeClassname = null;
	protected String facadeNamespace = null;
	protected boolean importResources = true;
	protected AutoKeyHashMap<String, InOut> inOuts = new AutoKeyHashMap<String, InOut>(
			new KeyAccessor<String, InOut>() {

				public String getKey(final InOut o) {
					return o.getName();
				}
			});
	protected AutoKeyHashMap<String, Input> inputs = new AutoKeyHashMap<String, Input>(
			new KeyAccessor<String, Input>() {

				public String getKey(final Input o) {
					return o.getName();
				}
			});
	
	protected AutoKeyHashMap<String, Input> RESTinputs = new AutoKeyHashMap<String, Input>(
			new KeyAccessor<String, Input>() {

				public String getKey(final Input o) {
					return o.getName();
				}
			});
	
	protected AutoKeyHashMap<String, InOut> RESTinouts = new AutoKeyHashMap<String, InOut>(
			new KeyAccessor<String, InOut>() {

				public String getKey(final InOut o) {
					return o.getName();
				}
			});
	
	protected String jarName = null;
	protected Map<String, String> lmls = new HashMap<String, String>();
	protected String namespace = null;
	protected AutoKeyHashMap<String, Output> outputs = new AutoKeyHashMap<String, Output>(
			new KeyAccessor<String, Output>() {

				public String getKey(final Output o) {
					return o.getName();
				}
			});
	
	protected AutoKeyHashMap<String, Output> RESToutputs = new AutoKeyHashMap<String, Output>(
			new KeyAccessor<String, Output>() {

				public String getKey(final Output o) {
					return o.getName();
				}
			});
	
	public AutoKeyHashMap<String, Output> getRESToutputs() {
		return RESToutputs;
	}

	protected AutoKeyHashMap<String, ParserDef> parsers = new AutoKeyHashMap<String, ParserDef>(
			new KeyAccessor<String, ParserDef>() {

				public String getKey(final ParserDef o) {
					return o.getType();
				}
			});
	protected String resourceFilename = null;

	protected String userFacadeClassname = null;

	public ComponentDescriptionImpl() {}

	public ComponentDescriptionImpl(final String componentDescriptionResource) {
		setResourceFilename(componentDescriptionResource);
		process();
	}

	public void dispose() {
		attributes.clear();
		dependances.clear();
		inOuts.clear();
		inputs.clear();
		lmls.clear();
		outputs.clear();
		parsers.clear();
		parser = null;
	}

	private void extractData() throws Exception {
		for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser
				.next()) {
			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if (parser.getLocalName().equals("component")) {
						for (int i=0,j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("ns")) {
								namespace = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"name")) {
								componentName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"type")) {
								componentType = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"doc")) {
								componentDesc = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"extends")) {

								parentComponentName = parser.getAttributeValue(i);
								if (!parentComponentName.equals("")) {									

									URL url = null;
									String resource=null;
									
									String whereToLookFor[] = {
											(new StringBuilder(
													String.valueOf(parentComponentName
															.replace(".", "/"))))
													.append("/resources/")
													.append(parentComponentName)
													.append(".cdml").toString(),
											(new StringBuilder(
													"inria/smarttools/core/component/resources/"))
													.append(parentComponentName)
													.append(".cdml").toString(),
											(new StringBuilder(
													"inria/smarttools/document/resources/"))
													.append(parentComponentName)
													.append(".cdml").toString(),
											(new StringBuilder(
													"inria/smarttools/view/resources/"))
													.append(parentComponentName)
													.append(".cdml").toString() 
									};
									
									String as[];
									int l = (as = whereToLookFor).length;
									for (int k = 0; k < l; k++) {
										String file = as[k];
										try {
											url = STUtilities
													.getURLForFile((new StringBuilder(
															"resources:")).append(
															file).toString());
											if (url == null)
												continue;
											resource = (new StringBuilder("/"))
													.append(file).toString();
											break;
										} catch (IOException e) {
											e.printStackTrace();
										}
									}							
									
									if (url != null) {
										final ComponentDescription parentComponent = new ComponentDescriptionImpl(resource);
										if (importResources)
											importResources(parentComponent);
									} else {
										Activator.LOGGER
												.severe("Unable to found "
														+ parentComponentName);
									}

								}
							}
						}
					} else if (parser.getLocalName().equals("containerclass")) {
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								containerClassname = parser
										.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"ns")) {
								containerNamespace = parser
										.getAttributeValue(i);
							}
						}
					} else if (parser.getLocalName().equals("facadeclass")) {
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								facadeClassname = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"ns")) {
								facadeNamespace = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"userclassname")) {
								userFacadeClassname = parser
										.getAttributeValue(i);
							}
						}
					} else if (parser.getLocalName().equals("formalism")) {
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								doctypeName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"dtd")) {
								dtdName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"file")) {
								absyntFilename = parser.getAttributeValue(i);
							}
						}
					} else if (parser.getLocalName().equals("behavior")) {
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("file")) {
								behavior = parser.getAttributeValue(i);
							}
						}
					} else if (parser.getLocalName().equals("attribute")) {
						String attrName = null;
						String attrNs = null;
						String attrType = null;
						String attrDoc = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								attrName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"ns")) {
								attrNs = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"javatype")) {
								attrType = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"doc")) {
								attrDoc = parser.getAttributeValue(i);
							}
						}
						attributes.put(new Attribute(attrName, attrNs,
								attrType, attrDoc));
					} else if (parser.getLocalName().equals("parser")) {
						String type = null;
						String classname = null;
						String generator = null;
						String file = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("type")) {
								type = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"classname")) {
								classname = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"generator")) {
								generator = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"file")) {
								file = parser.getAttributeValue(i);
							}
						}
						final List<String> exts = new LinkedList<String>();
						boolean continueTo = true;
						while (continueTo) {
							event = parser.next();
							switch (event) {
								case XMLStreamConstants.START_ELEMENT:
									if (parser.getLocalName().equals(
											"extension")) {
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("name")) {
												exts.add(parser
														.getAttributeValue(i));
											}
										}
									}
									break;
								case XMLStreamConstants.END_ELEMENT:
									if (parser.getLocalName().equals("parser")) {
										continueTo = false;
									}
									break;
							}
						}
						parsers.put(new ParserDef(type, exts, classname,
								generator, file));
					} else if (parser.getLocalName().equals("lml")) {
						String lmlName = null;
						String lmlFile = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								lmlName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"file")) {
								lmlFile = parser.getAttributeValue(i);
							}
						}
						lmls.put(lmlName, lmlFile);
					} else if (parser.getLocalName().equals("input")) {
						String inputName = null;
						String inputDoc = null;
						String inputMethod = null;
						String inputRest = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								inputName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"doc")) {
								inputDoc = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"method")) {
								inputMethod = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"rest")) {
								inputRest = parser.getAttributeValue(i);
							}
						}
						final Map<String, Attribute> parameters = new LinkedHashMap<String, Attribute>();
						JavaBinding binding = null;
						boolean continueTo = true;
						while (continueTo) {
							event = parser.next();
							switch (event) {
								case XMLStreamConstants.START_ELEMENT:
									if (parser.getLocalName().equals(
											"attribute")) {
										String attrName = null;
										String attrNs = null;
										String attrType = null;
										String attrDoc = null;
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("name")) {
												attrName = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("ns")) {
												attrNs = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("javatype")) {
												attrType = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("doc")) {
												attrDoc = parser
														.getAttributeValue(i);
											}
										}
										parameters.put(attrName, new Attribute(
												attrName, attrNs, attrType,
												attrDoc));
									} else if (parser.getLocalName().equals(
											"binding")) {
										String toMethod = null;
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("toMethod")) {
												toMethod = parser
														.getAttributeValue(i);
											}
										}
										final Map<String, Attribute> args = new LinkedHashMap<String, Attribute>();
										boolean continueTo2 = true;
										while (continueTo2) {
											event = parser.next();
											switch (event) {
												case XMLStreamConstants.START_ELEMENT:
													if (parser
															.getLocalName()
															.equals("attribute")) {
														String argName = null;
														String argType = null;
														String argRef = null;
														String argValue = null;
														for (int i = 0, j = parser
																.getAttributeCount(); i < j; i++) {
															if (parser
																	.getAttributeLocalName(
																			i)
																	.equals("name")) {
																argName = parser
																		.getAttributeValue(i);
															} else if (parser
																	.getAttributeLocalName(
																			i)
																	.equals("type")) {
																argType = parser
																		.getAttributeValue(i);
															} else if (parser
																	.getAttributeLocalName(
																			i)
																	.equals("ref")) {
																argRef = parser
																		.getAttributeValue(i);
															} else if (parser
																	.getAttributeLocalName(
																			i)
																	.equals("value")) {
																argValue = parser
																		.getAttributeValue(i);
															}
														}
														if (argName != null
																&& !argName
																		.equals("")) {
															args.put(
																	argName,
																	new Attribute(
																			argName,
																			null,
																			argType,
																			"",
																			argValue));
														} else {
															args.put(
																	argRef,
																	new Attribute(
																			argRef,
																			null,
																			argType,
																			"",
																			argValue));
														}
													}
												case XMLStreamConstants.END_ELEMENT:
													if (parser.getLocalName()
															.equals("binding")) {
														continueTo2 = false;

														binding = new JavaBinding(
																toMethod, args);
													}
													break;
											}
										}
									}
									break;
								case XMLStreamConstants.END_ELEMENT:
									if (parser.getLocalName().equals("input")) {
										continueTo = false;
									}
									break;
							}
						}
						final Input input = new Input(inputName, parameters,
								inputMethod, inputDoc);
						if (binding != null) {
							input.setBinding(binding);
						}
						if (inputRest!=null && inputRest.toLowerCase().equals("true")){
							RESTinputs.put(input);
						}
						inputs.put(input);
					} else if (parser.getLocalName().equals("inout")) {
						String inoutName = null;
						String inoutDoc = null;
						String inoutMethod = null;
						String outputName = null;
						String outputArg = null;
						String RestInOut = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								inoutName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"doc")) {
								inoutDoc = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"method")) {
								inoutMethod = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"output")) {
								outputName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"outputArg")) {
								outputArg = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"rest")) {
								RestInOut = parser.getAttributeValue(i);
							}
						}
						final Map<String, Attribute> parameters = new LinkedHashMap<String, Attribute>();
						boolean continueTo = true;
						final Map<String, Attribute> args = new LinkedHashMap<String, Attribute>();
						while (continueTo) {
							event = parser.next();
							switch (event) {
								case XMLStreamConstants.START_ELEMENT:
									if (parser.getLocalName().equals(
											"attribute")) {
										String attrName = null;
										String attrNs = null;
										String attrType = null;
										String attrDoc = null;
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("name")) {
												attrName = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("ns")) {
												attrNs = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("javatype")) {
												attrType = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("doc")) {
												attrDoc = parser
														.getAttributeValue(i);
											}
										}
										parameters.put(attrName, new Attribute(
												attrName, attrNs, attrType,
												attrDoc));
									} else if (parser.getLocalName().equals(
											"attribute")) {
										String argName = null;
										String argType = null;
										String argRef = null;
										String argValue = null;
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("name")) {
												argName = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("type")) {
												argType = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("ref")) {
												argRef = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("value")) {
												argValue = parser
														.getAttributeValue(i);
											}
										}
										if (argName != null
												&& !argName.equals("")) {
											args.put(argName, new Attribute(
													argName, null, argType, "",
													argValue));
										} else {
											args.put(argName, new Attribute(
													argRef, null, argType, "",
													argValue));
										}
									}
									break;
								case XMLStreamConstants.END_ELEMENT:
									if (parser.getLocalName().equals("inout")) {
										continueTo = false;
									}
									break;
							}
						}
						
						if (RestInOut!=null && RestInOut.toLowerCase().equals("true")){
							RESTinouts.put(new InOut(inoutName, parameters,
									outputArg, inoutMethod, outputName, inoutDoc));
						}
						
						final InOut inout = new InOut(inoutName, parameters,
								outputArg, inoutMethod, outputName, inoutDoc);
						inOuts.put(inout);
					} else if (parser.getLocalName().equals("output")) {
						String outputName = null;
						String outputDoc = null;
						String outputMethod = null;
						String outputREST = null;
						for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
							if (parser.getAttributeLocalName(i).equals("name")) {
								outputName = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"doc")) {
								outputDoc = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"method")) {
								outputMethod = parser.getAttributeValue(i);
							} else if (parser.getAttributeLocalName(i).equals(
									"rest")) {
								outputREST = parser.getAttributeValue(i);
							}
						}
						boolean continueTo = true;
						final Map<String, Attribute> args = new LinkedHashMap<String, Attribute>();
						// final PropertyMap at = new PropertyMap();
						while (continueTo) {
							event = parser.next();
							switch (event) {
								case XMLStreamConstants.START_ELEMENT:
									if (parser.getLocalName().equals(
											"attribute")) {
										String argName = null;
										String argType = null;
										String argDoc = null;
										for (int i = 0, j = parser
												.getAttributeCount(); i < j; i++) {
											if (parser.getAttributeLocalName(i)
													.equals("name")) {
												argName = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("javatype")) {
												argType = parser
														.getAttributeValue(i);
											} else if (parser
													.getAttributeLocalName(i)
													.equals("doc")) {
												argDoc = parser
														.getAttributeValue(i);
											}
										}
										args.put(argName, new Attribute(
												argName, null, argType, argDoc));
									}
									// else if (parser.getLocalName().equals(
									// "message")) {
									// String messageName = null;
									// for (int i = 0, j = parser
									// .getAttributeCount(); i < j; i++) {
									// if (parser.getAttributeLocalName(i)
									// .equals("name")) {
									// messageName = parser
									// .getAttributeValue(i);
									// }
									// }
									// boolean continueTo2 = true;
									// final List<String> atts = new
									// ArrayList<String>();
									// while (continueTo2) {
									// parser.next();
									// switch (event) {
									// case XMLStreamConstants.START_ELEMENT:
									// if (parser.getLocalName()
									// .equals("attr")) {
									// for (int i = 0, j = parser
									// .getAttributeCount(); i < j; i++) {
									// if (parser
									// .getAttributeLocalName(
									// i)
									// .equals(
									// "ref")) {
									// atts
									// .add(parser
									// .getAttributeValue(i));
									// }
									// }
									// } else if (parser
									// .getLocalName()
									// .equals("data")) {
									// boolean continueTo3 = true;
									// final List<String> innerAtts = new
									// ArrayList<String>();
									// while (continueTo3) {
									// parser.next();
									// switch (event) {
									// case XMLStreamConstants.START_ELEMENT:
									// if (parser
									// .getLocalName()
									// .equals(
									// "attr")) {
									// for (int i = 0, j = parser
									// .getAttributeCount(); i < j; i++) {
									// if (parser
									// .getAttributeLocalName(
									// i)
									// .equals(
									// "ref")) {
									// innerAtts
									// .add(parser
									// .getAttributeValue(i));
									// }
									// }
									// }
									// break;
									// case XMLStreamConstants.END_ELEMENT:
									// if (parser
									// .getLocalName()
									// .equals(
									// "data")) {
									// continueTo3 = false;
									// }
									// break;
									// }
									// }
									//
									// }
									// break;
									// case XMLStreamConstants.END_ELEMENT:
									// if (parser.getLocalName()
									// .equals("message")) {
									// continueTo2 = false;
									// }
									// break;
									// }
									// for (final Iterator<String> it = atts
									// .iterator(); it.hasNext();) {
									// final String att = it.next();
									// at.put(att, att);
									// }
									// }
									//
									// }
									break;
								case XMLStreamConstants.END_ELEMENT:
									if (parser.getLocalName().equals("output")) {
										continueTo = false;
									}
									break;
							}
						}
						
						if (outputREST!=null && outputREST.toLowerCase().equals("true")){
							RESToutputs.put(new Output(outputName, args, outputMethod,
									outputDoc));
						}
						
						outputs.put(new Output(outputName, args, outputMethod,
								outputDoc));
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					break;
				case XMLStreamConstants.CHARACTERS:
					break;
				case XMLStreamConstants.CDATA:
					break;
			} // end switch
		} // end while
	}

	public String getAbsyntFilename() {
		return absyntFilename;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public String getBehavior() {
		return behavior;
	}

	public String getComponentDesc() {
		return componentDesc;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentType() {
		return componentType;
	}
	
	public String getParentComponentName()
    {
        return parentComponentName;
    }

	public String getParentComponent() {
		return parentComponent;
	}

	public String getContainerClassname() {
		return containerClassname;
	}

	public String getContainerNamespace() {
		return containerNamespace;
	}

	public Map<String, String> getDependances() {
		return dependances;
	}

	public String getDoctypeName() {
		return doctypeName;
	}

	public List<String> getDocumentation() {
		return documentation;
	}

	public String getDtdName() {
		return dtdName;
	}

	public String getFacadeClassname() {
		return facadeClassname;
	}

	public String getFacadeNamespace() {
		return facadeNamespace;
	}

	public boolean getImportResources() {
		return importResources;
	}

	public Map<String, InOut> getInOuts() {
		return inOuts;
	}

	public Map<String, Input> getInputs() {
		return inputs;
	}
	
	public Map<String, Input> getRESTInputs() {
		return RESTinputs;
	}
	
	public Map<String, InOut> getRESTInOuts() {
		return RESTinouts;
	}

	public String getJar() {
		return jarName;
	}

	public Map<String, String> getLmls() {
		return lmls;
	}

	public String getNamespace() {
		return namespace;
	}

	public Map<String, Output> getOutputs() {
		return outputs;
	}

	public Map<String, ParserDef> getParsers() {
		return parsers;
	}

	public String getResourceFilename() {
		return resourceFilename;
	}

	public String getUserFacadeClassname() {
		return userFacadeClassname;
	}

	/**
	 * Import resources data from the specified field.
	 * 
	 * @param imported
	 *            a <code>ResourcesData</code> value
	 */
	private void importResources(final ComponentDescription imported) {
		// attributes
		attributes.putAll(imported.getAttributes());
		// inputs
		inputs.putAll(imported.getInputs());
		// outputs
		outputs.putAll(imported.getOutputs());
		// inouts
		inOuts.putAll(imported.getInOuts());
	}

	/**
	 * Load the file that describe the component and fill data on object in
	 * order to produce a file
	 */
	private void load() throws MalformedURLException, IOException, Exception {
		final XMLInputFactory factory = XMLInputFactory.newInstance();
		if (!resourceFilename.startsWith("file:")) {
			InputStream docFile = null;
			if (cl != null) {
				docFile = cl.getResourceAsStream(resourceFilename);
			}
			if (docFile == null) {
				docFile = STUtilities.class
						.getResourceAsStream(resourceFilename);
			}
			parser = factory.createXMLStreamReader(docFile);
		} else {
			parser = factory.createXMLStreamReader(STUtilities
					.getISForFile(resourceFilename));
		}
	}

	/**
	 * Start parsing file and extract data.
	 */
	public void process() {
		if (resourceFilename != null) {
			try {
				load();
				extractData();
			} catch (final Exception e) {
				Activator.LOGGER.throwing(getClass().getName(),
						"void process()", e);
			} finally {
				try {
					parser.close();
				} catch (final XMLStreamException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void process(final InputStream message) throws IOException,
			Exception {
		try {
			final XMLInputFactory factory = XMLInputFactory.newInstance();
			parser = factory.createXMLStreamReader(message);
			extractData();
		} catch (final Exception e) {
			parser.close();
			message.close();
			throw e;
		}
		parser.close();
		message.close();
	}

	public void save(final String filename) {
		try {
			final Writer ps = new FileWriter(new File(filename));
			ps.write(serialize());
			ps.write('\n');
			ps.close();
		} catch (final Exception e) {
			Activator.LOGGER.throwing(getClass().getName(),
					"void save(String filename)", e);
		}
	}

	public String serialize() throws IOException, Exception {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		final XMLStreamWriter writer = factory.createXMLStreamWriter(bos);

		writer.writeStartDocument();
		writer.writeStartElement("component");
		if (getComponentType() != null) {
			writer.writeAttribute("type", getComponentType());
		}
		if (getComponentType() != null) {
			writer.writeAttribute("ns", getNamespace());
		}
		if (getComponentName() != null) {
			writer.writeAttribute("name", getComponentName());
		}
		if (getComponentDesc() != null) {
			writer.writeAttribute("doc", getComponentDesc());
		}

		writer.writeStartElement("containerclass");
		if (getContainerClassname() != null) {
			writer.writeAttribute("name", getContainerClassname());
		}
		if (getContainerNamespace() != null) {
			writer.writeAttribute("ns", getContainerNamespace());
		}
		writer.writeEndElement();

		writer.writeStartElement("facadeclass");
		if (getFacadeClassname() != null) {
			writer.writeAttribute("name", getFacadeClassname());
		}
		if (getFacadeNamespace() != null) {
			writer.writeAttribute("ns", getFacadeNamespace());
		}
		if (getUserFacadeClassname() != null) {
			writer.writeAttribute("userclassname", getUserFacadeClassname());
		}
		writer.writeEndElement();

		writer.writeStartElement("formalism");
		if (getDoctypeName() != null) {
			writer.writeAttribute("name", getDoctypeName());
		}
		if (getDtdName() != null) {
			writer.writeAttribute("dtd", getDtdName());
		}
		if (getAbsyntFilename() != null) {
			writer.writeAttribute("file", getAbsyntFilename());
		}
		writer.writeEndElement();

		if (getBehavior() != null) {
			writer.writeStartElement("behavior");
			writer.writeAttribute("file", getBehavior());
			writer.writeEndElement();
		}

		for (final Iterator<Attribute> it = getAttributes().values().iterator(); it
				.hasNext();) {
			final Attribute at = it.next();
			writer.writeStartElement("attribute");
			if (at.getName() != null) {
				writer.writeAttribute("name", at.getName());
			}
			if (at.getNs() != null) {
				writer.writeAttribute("ns", at.getNs());
			}
			if (at.getType() != null) {
				writer.writeAttribute("javatype", at.getType());
			}
			if (at.getDescription() != null) {
				writer.writeAttribute("doc", at.getDescription());
			}
			writer.writeEndElement();
		}
		for (final Iterator<ParserDef> it = parsers.values().iterator(); it
				.hasNext();) {
			final ParserDef parserDef = it.next();
			writer.writeStartElement("parser");
			if (parserDef.getType() != null) {
				writer.writeAttribute("type", parserDef.getType());
			}
			if (parserDef.getClassname() != null) {
				writer.writeAttribute("classname", parserDef.getClassname());
			}
			if (parserDef.getGenerator() != null) {
				writer.writeAttribute("generator", parserDef.getGenerator());
			}
			if (parserDef.getFile() != null) {
				writer.writeAttribute("file", parserDef.getFile());
			}
			for (final Iterator<String> itExts = parserDef.getExtentions()
					.iterator(); itExts.hasNext();) {
				writer.writeStartElement("extension");
				writer.writeAttribute("name", itExts.next());
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		for (final Iterator<Input> it = inputs.values().iterator(); it
				.hasNext();) {
			final Input input = it.next();
			writer.writeStartElement("input");
			if (input.getName() != null) {
				writer.writeAttribute("name", input.getName());
			}
			if (input.getDocumentation() != null) {
				writer.writeAttribute("doc", input.getDocumentation());
			}
			if (input.getMethodName() != null) {
				writer.writeAttribute("method", input.getMethodName());
			}
			for (final Iterator<Attribute> itAttrs = input.getAttributes()
					.values().iterator(); itAttrs.hasNext();) {
				final Attribute attr = (Attribute) itAttrs.next();
				writer.writeStartElement("attribute");
				if (attr.getName() != null) {
					writer.writeAttribute("name", attr.getName());
				}
				if (attr.getNs() != null) {
					writer.writeAttribute("ns", attr.getNs());
				}
				if (attr.getType() != null) {
					writer.writeAttribute("javatype", attr.getType());
				}
				if (attr.getDescription() != null) {
					writer.writeAttribute("doc", attr.getDescription());
				}
				writer.writeEndElement();
			}
			if (input.getBinding() != null
					&& input.getBinding().getMethod() != null) {
				writer.writeStartElement("binding");
				writer.writeAttribute("toMethod", input.getBinding()
						.getMethod());
				for (final Iterator<Attribute> it2 = input.getBinding()
						.getAttributes().values().iterator(); it2.hasNext();) {
					final Attribute attr = it2.next();
					writer.writeStartElement("attribute");
					if (attr.getName() != null) {
						writer.writeAttribute("name", attr.getName());
					}
					if (attr.getType() != null) {
						writer.writeAttribute("type", attr.getType());
					}
					if (attr.getDescription() != null) {
						writer.writeAttribute("ref", attr.getDescription());
					}
					if (attr.getValue() != null) {
						writer.writeAttribute("value", attr.getValue());
					}
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		for (final Iterator<InOut> it = inOuts.values().iterator(); it
				.hasNext();) {
			final InOut inOut = it.next();
			writer.writeStartElement("inout");
			if (inOut.getName() != null) {
				writer.writeAttribute("name", inOut.getName());
			}
			if (inOut.getDocumentation() != null) {
				writer.writeAttribute("doc", inOut.getDocumentation());
			}
			if (inOut.getMethodName() != null) {
				writer.writeAttribute("method", inOut.getMethodName());
			}
			if (inOut.getOutputName() != null) {
				writer.writeAttribute("output", inOut.getOutputName());
			}
			if (inOut.getArg() != null) {
				writer.writeAttribute("outputArg", inOut.getArg());
			}
			for (final Iterator<Attribute> itAttrs = inOut.getAttributes()
					.values().iterator(); itAttrs.hasNext();) {
				final Attribute attr = itAttrs.next();
				writer.writeStartElement("attribute");
				if (attr.getName() != null) {
					writer.writeAttribute("name", attr.getName());
				}
				if (attr.getNs() != null) {
					writer.writeAttribute("ns", attr.getNs());
				}
				if (attr.getType() != null) {
					writer.writeAttribute("javatype", attr.getType());
				}
				if (attr.getDescription() != null) {
					writer.writeAttribute("doc", attr.getDescription());
				}
				writer.writeEndElement();
			}
			if (inOut.getBinding() != null
					&& inOut.getBinding().getMethod() != null) {
				writer.writeStartElement("binding");
				writer.writeAttribute("toMethod", inOut.getBinding()
						.getMethod());
				for (final Iterator<Attribute> it2 = inOut.getBinding()
						.getAttributes().values().iterator(); it2.hasNext();) {
					final Attribute attr = it2.next();
					writer.writeStartElement("attribute");
					if (attr.getName() != null) {
						writer.writeAttribute("name", attr.getName());
					}
					if (attr.getType() != null) {
						writer.writeAttribute("type", attr.getType());
					}
					if (attr.getDescription() != null) {
						writer.writeAttribute("ref", attr.getDescription());
					}
					if (attr.getValue() != null) {
						writer.writeAttribute("value", attr.getValue());
					}
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}

		for (final Iterator<Output> it = outputs.values().iterator(); it
				.hasNext();) {
			final Output output = it.next();
			writer.writeStartElement("output");
			if (output.getName() != null) {
				writer.writeAttribute("name", output.getName());
			}
			if (output.getDocumentation() != null) {
				writer.writeAttribute("doc", output.getDocumentation());
			}
			if (output.getMethodName() != null) {
				writer.writeAttribute("method", output.getMethodName());
			}
			for (final Iterator<Attribute> it2 = output.getAttributes()
					.values().iterator(); it2.hasNext();) {
				final Attribute attr = it2.next();
				writer.writeStartElement("attribute");
				if (attr.getName() != null) {
					writer.writeAttribute("name", attr.getName());
				}
				if (attr.getType() != null) {
					writer.writeAttribute("javatype", attr.getType());
				}
				if (attr.getDescription() != null) {
					writer.writeAttribute("doc", attr.getDescription());
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}

		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
		return bos.toString();
	}

	public void setAbsyntFilename(final String v) {
		absyntFilename = v;
	}

	public void setAttributes(final AutoKeyHashMap<String, Attribute> v) {
		attributes = v;
	}

	public void setBehavior(final String v) {
		behavior = v;
	}

	public void setClassLoader(final ClassLoader classLoader) {
		cl = classLoader;
	}

	public void setComponentDesc(final String v) {
		componentDesc = v;
	}

	public void setComponentName(final String v) {
		componentName = v;
	}

	public void setComponentType(final String v) {
		componentType = v;
	}

	public void setContainerClassname(final String v) {
		containerClassname = v;
	}

	public void setContainerNamespace(final String v) {
		containerNamespace = v;
	}

	public void setDependances(final Map<String, String> v) {
		dependances = v;
	}

	public void setDoctypeName(final String v) {
		doctypeName = v;
	}

	public void setDocumentation(final List<String> v) {
		documentation = v;
	}

	public void setDtdName(final String v) {
		dtdName = v;
	}

	public void setFacadeClassname(final String v) {
		facadeClassname = v;
	}

	public void setFacadeNamespace(final String v) {
		facadeNamespace = v;
	}

	public void setImportResources(final boolean v) {
		importResources = v;
	}

	public void setInOuts(final AutoKeyHashMap<String, InOut> v) {
		inOuts = v;
	}

	public void setInputs(final AutoKeyHashMap<String, Input> v) {
		inputs = v;
	}

	public void setJar(final String name) {
		jarName = name;
	}

	public void setLmls(final Map<String, String> v) {
		lmls = v;
	}

	public void setNamespace(final String v) {
		namespace = v;
	}

	public void setOutputs(final AutoKeyHashMap<String, Output> v) {
		outputs = v;
	}

	public void setParsers(final AutoKeyHashMap<String, ParserDef> v) {
		parsers = v;
	}

	public void setResourceFilename(final String v) {
		resourceFilename = v;
	}

	public void setUserFacadeClassname(final String v) {
		userFacadeClassname = v;
	}

	@Override
	public String toString() {
		return "ComponentDescriptionImpl>" + componentName;
	}

	public void toWsdl(final String filename) {
		// TODO toWsdl
	}

}
