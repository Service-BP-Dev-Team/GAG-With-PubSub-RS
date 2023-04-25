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
// $Id: World.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.componentsmanager;

import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.ContainerService;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.PropertyMap;
import inria.smarttools.dynamic.STUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * World is a class that load a boot file for SmartTools component manager. This
 * boot file is use to start SmartTools
 * <ul>
 * <li>loadComponent
 * <li>loadJar
 * <li>connectTo
 * <li>addSpy
 * <li>start/stop/sleep/wakeup Component
 * <li>message (send to the CM)
 * </ul>
 * All this call are turned into Message Object and store. After we can play it
 * on a ComponentsManager We can use <pattern> value (data between []). Values
 * are extract from the params table.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2819 $
 */
public class World {

	private static World instance = null;

	public static void createWorld(final String filename,
			final String lml_file,
			final Map<String, ContainerService> componentsLoaded) {
		final Iterator<String> iter = componentsLoaded.keySet().iterator();
		try {
			final FileOutputStream fos = new FileOutputStream(filename);
			final PrintWriter pw = new PrintWriter(fos);
			//
			String res = "";
			res += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
			res += "<world repository=\"file:stlib/\"\n";
			res += "       library=\"file:lib/\"\n";
			res += "       debug=\"OFF\">\n";
			res += "\n";
			res += "   <!--                        -->\n";
			res += "   <!-- Components declaration -->\n";
			res += "   <!--                        -->\n";
			res += "   <load_component jar=\"view.jar\"\n";
			res += "	   	       name=\"glayout\"/>\n";
			res += "   <load_component jar=\"lml.jar\" \n";
			res += "		       name=\"lml\"/>\n";

			while (iter.hasNext()) {
				final String name = iter.next();
				final ComponentDescription cd = (ComponentDescription) componentsLoaded
						.get(name);
				final String jar = name + ".jar";
				final String url = cd.getJar();
				if (url != null && !url.equals("")) {
					res += "   <load_component jar=\"" + jar + "\"\n";
					res += "                   url=\"" + url + "\"\n";
					res += "		       name=\"" + name + "\"/>\n";
				}
			}
			res += " \n";
			res += "   <!--         -->\n";
			res += "   <!-- connect -->\n";
			res += "   <!--         -->\n";
			res += "   <connectTo id_src=\"ComponentsManager\" \n";
			res += "              type_dest=\"glayout\">\n";
			res += "         <attribute name=\"docRef\" \n";
			res += "      	        value=\"" + lml_file + "\"/>\n";
			res += "         <attribute name=\"xslTransform\" \n";
			res += "                    value=\"resources:xsl/lml2bml.xsl\"/>\n";
			res += "         <attribute name=\"behaviors\" \n";
			res += "		        value=\"resources:behaviors/bootbehav.xml\"/>\n";
			res += "         <message name=\"initData\">\n";
			res += "           <attribute name=\"inits\">\n";
			res += "              <collection>\n";
			res += "                 <item name=\"behavior\" value=\"resources:cmbehaviors.xml\"/>\n";
			res += "              </collection>\n";
			res += "           </attribute>\n";
			res += "        </message>\n";
			res += "   </connectTo>\n";
			res += "</world>\n";
			pw.println(res);
			pw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void dispose() {
		if (World.instance != null) {
			if (World.instance.messages != null) {
				World.instance.messages.clear();
			}
			if (World.instance.params != null) {
				World.instance.params.clear();
			}
			World.instance = null;
		}
	}

	public static boolean exists() {
		return World.instance != null;
	}

	public static synchronized World getInstance() {
		if (World.instance == null) {
			World.instance = new World();
		}
		return World.instance;
	}

	protected String worldFile = null;

	protected List<Message> messages = new ArrayList<Message>();

	protected String repository = null;

	protected String library = null;

	protected boolean debug = false;

	protected Map<String, String> params = null;

	public World() {
	//
	}

	public void add(final Message m) {
		messages.add(m);
	}

	public String getLibrary() {
		return library;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getRepository() {
		return repository;
	}

	public String getWorldFile() {
		return worldFile;
	}

	public boolean isDebug() {
		return debug;
	}

	public void loadWorld() {
		if (worldFile.startsWith("resources:")) {
			try {
				loadWorld(STUtilities.getISForFile(worldFile));
			} catch (final Exception e) {
				Activator.LOGGER.severe(worldFile + " not found");
				Activator.LOGGER.throwing(getClass().getName(), "loadWorld()",
						e);
			}
		} else {
			try {
				loadWorld(new FileInputStream(new File(worldFile)));
			} catch (final FileNotFoundException e) {
				logERROR("Unable to open URL " + worldFile + " reason "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load "world" file that describe start state of SmartTools.
	 */
	public void loadWorld(final InputStream is) {
		final XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = null;
		try {
			parser = factory.createXMLStreamReader(is);
			for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser
					.next()) {
				switch (event) {
					case XMLStreamConstants.START_ELEMENT:
						if (parser.getLocalName().equals("connectTo")) {
							String id_src = null;
							String id_dest = null;
							String type_dest = null;
							final String dc = null;
							final String tc = null;
							for (int i = 0, j = parser.getAttributeCount(); i < j; i++) {
								if (parser.getAttributeLocalName(i).equals(
										"id_src")) {
									id_src = parser.getAttributeValue(i);
								} else if (parser.getAttributeLocalName(i)
										.equals("id_dest")) {
									id_dest = parser.getAttributeValue(i);
								} else if (parser.getAttributeLocalName(i)
										.equals("type_dest")) {
									type_dest = parser.getAttributeValue(i);
								}
							}
							final PropertyMap attr = new PropertyMap();
							final PropertyMap at = new PropertyMap();
							int index = 0;
							boolean continueTo = true;
							while (continueTo) {
								event = parser.next();
								switch (event) {
									case XMLStreamConstants.START_ELEMENT:
										if (parser.getLocalName().equals(
												"attribute")) {
											String name = null;
											String value = null;
											for (int i = 0, j = parser
													.getAttributeCount(); i < j; i++) {
												if (parser
														.getAttributeLocalName(
																i).equals(
																"name")) {
													name = parser
															.getAttributeValue(i);
												} else if (parser
														.getAttributeLocalName(
																i).equals(
																"value")) {
													value = parser
															.getAttributeValue(i);
												}
											}
											at.put(name, value);
										} else if (parser.getLocalName()
												.equals("message")) {
											String name = null;
											final PropertyMap msgAttr = new PropertyMap();
											for (int i = 0, j = parser
													.getAttributeCount(); i < j; i++) {
												if (parser
														.getAttributeLocalName(
																i).equals(
																"name")) {
													name = parser
															.getAttributeValue(i);
												}
											}
											boolean continueTo2 = true;
											while (continueTo2) {
												event = parser.next();
												switch (event) {
													case XMLStreamConstants.START_ELEMENT:
														if (parser
																.getLocalName()
																.equals(
																		"attribute")) {
															String msgName = null;
															String msgValue = null;
															for (int i = 0, j = parser
																	.getAttributeCount(); i < j; i++) {
																if (parser
																		.getAttributeLocalName(
																				i)
																		.equals(
																				"name")) {
																	msgName = parser
																			.getAttributeValue(i);
																} else if (parser
																		.getAttributeLocalName(
																				i)
																		.equals(
																				"value")) {
																	msgValue = parser
																			.getAttributeValue(i);
																}
															}
															msgAttr.put(
																	msgName,
																	msgValue);
														}
													case XMLStreamConstants.END_ELEMENT:
														if (parser
																.getLocalName()
																.equals(
																		"message")) {
															continueTo2 = false;
														}
														break;
												}
											}
											attr.put("afterConnect" + index++,
													new MessageImpl(name,
															msgAttr, null));
										}
										break;
									case XMLStreamConstants.END_ELEMENT:
										if (parser.getLocalName().equals(
												"connectTo")) {
											continueTo = false;
											attr.put("attributes", at);
										}
										break;
								}
							}
							final PropertyMap msg = new PropertyMap();
							msg.put("id_src", id_src);
							msg.put("id_dest", id_dest);
							msg.put("type_dest", type_dest);
							msg.put("tc", tc);
							msg.put("dc", dc);
							msg.put("actions", attr);
							messages
									.add(new MessageImpl("connectTo", msg, null));
						}
				}
			}
		} catch (final XMLStreamException e) {
			e.printStackTrace();
		} finally {
			if (parser != null) {
				try {
					parser.close();
					is.close();
				} catch (final XMLStreamException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		// } else if (name.equals("connectTo")) {
		// final String id_src = getAndResolveAttribute(elm,
		// "id_src");
		// final String type_dest = getAndResolveAttribute(elm,
		// "type_dest");
		// final String id_dest = getAndResolveAttribute(elm,
		// "id_dest");
		// final String dc = getAndResolveAttribute(elm, "dc");
		// final String tc = getAndResolveAttribute(elm, "tc");
		// final PropertyMap attrs = new PropertyMap();
		// NodeList attributes = XPathAPI.selectNodeList(elm,
		// "attribute");
		// PropertyMap at = new PropertyMap();
		// for (int ii = 0; ii < attributes.getLength(); ii++) {
		// final Element attribute = (Element) attributes
		// .item(ii);
		// final String attrName = getAndResolveAttribute(
		// attribute, "name");
		// // String attrType =
		// // getAndResolveAttribute(attribute,
		// // "type");
		// final String value = getAndResolveAttribute(
		// attribute, "value");
		// at.put(attrName, value);
		// }
		// attrs.put("attributes", at);
		// //
		// final NodeList msgs = XPathAPI.selectNodeList(elm,
		// "message");
		// for (int j = 0; j < msgs.getLength(); j++) {
		// final Element message = (Element) msgs.item(j);
		// final String msg_name = getAndResolveAttribute(
		// message, "name");
		// attributes = XPathAPI.selectNodeList(message,
		// "attribute");
		// at = new PropertyMap();
		// for (int ii = 0; ii < attributes.getLength(); ii++) {
		// final Element attribute = (Element) attributes
		// .item(ii);
		// final String attrName = getAndResolveAttribute(
		// attribute, "name");
		// //
		// final Element collection = (Element) XPathAPI
		// .selectSingleNode(attribute,
		// "collection");
		// if (collection != null) {
		// final NodeList items = XPathAPI
		// .selectNodeList(collection, "item");
		// final PropertyMap it = new PropertyMap();
		// for (int l = 0; l < items.getLength(); l++) {
		// final Element item = (Element) items
		// .item(ii);
		// final String item_name = getAndResolveAttribute(
		// item, "name");
		// final String item_value = getAndResolveAttribute(
		// item, "value");
		// it.put(item_name, item_value);
		// }
		// at.put(attrName, it);
		// } else {
		// // String attrType = getAndResolveAttribute(
		// // attribute, "type");
		// final String value = getAndResolveAttribute(
		// attribute, "value");
		// at.put(attrName, value);
		// }
		// }
		// if (msg_name != null) {
		// final Message msg = new MessageImpl(msg_name,
		// at, null);
		// attrs.put("afterConnect" + j, msg);
		// }
		// }
		// //
		// messages.add(new MessageImpl("connectTo",
		// new PropertyMap() {
		//
		// private static final long serialVersionUID = 1L;
		//
		// {
		// put("id_src", id_src);
		// put("type_dest", type_dest);
		// put("id_dest", id_dest);
		// put("dc", dc);
		// put("tc", tc);
		// put("actions", attrs);
		// }
		// }, null));
		// }
		// // ========================
	}

	public void logERROR(final String message) {
		logERROR(message, "<" + this + "> ");
	}

	public void logERROR(final String message, final String pre) {
		Activator.LOGGER.severe(pre + " " + message);
	}

	public void logINFO(final String message) {
		logINFO(message, "<" + this + "> ");
	}

	public void logINFO(final String message, final String pre) {
		Activator.LOGGER.info(pre + " " + message);
	}

	public void play(final ComponentsManager player) {
		final List<Message> tmp = new ArrayList<Message>();
		synchronized (this) {
			for (final Message message : messages) {
				tmp.add(message);
			}
		}
		//
		for (final Message message : tmp) {
			player.receive(message);
		}
	}

	public void setDebug(final boolean v) {
		debug = v;
	}

	public void setLibrary(final String v) {
		library = v;
	}

	public void setMessages(final List<Message> v) {
		messages = v;
	}

	public void setParams(final Map<String, String> v) {
		params = v;
	}

	public void setRepository(final String v) {
		repository = v;
	}

	public void setWorldFile(final String v) {
		worldFile = v;
	}

	@Override
	public String toString() {
		return "World";
	}

}
