package inria.scifloware.sciflowarecomponent;

import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.PropertyMap;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

/**
 * 
 * Abstract Facade component for SciFloware components :
 * Added : - Full list of neighboors
 * - XML parsing
 *  
 * 
 * @author dimitri.dupuis@inria.fr
 *
 */
public abstract class SciflowareComponent {

	private final Map<String, Set<String>> neighbours = Collections
			.synchronizedMap(new HashMap<String, Set<String>>());
	protected AbstractContainer container=null;

	
	public void addNeighbour(final String name, final String service) {
		if (!neighbours.containsKey(service)) {
			neighbours.put(service,
					Collections.synchronizedSet(new TreeSet<String>()));
		}
		final Set<String> s = neighbours.get(service);
		if(!s.contains(name)){
			s.add(name);
			neighbourJustConnected(name, service);
		}

	}

	protected void neighbourJustConnected(final String name,
			final String service) {
	}

	public void disconnectInput(String expeditor) {
		// TODO Auto-generated method stub

	}

	public void shutdown(String expeditor) {
		// TODO Auto-generated method stub

	}

	public Object requestTree(String expeditor) {
		// TODO Auto-generated method stub
		return null;
	}

	public void quit(String expeditor) {
		// TODO Auto-generated method stub

	}

	public List<Map<String, String>> parseXmlByName(String xml, String Name) {

		//System.out.println("Begin parsing ... : " + xml);

		XMLInputFactory XMLfact = XMLInputFactory.newInstance();

		XMLStreamReader XMLsr;

		List<Map<String,String>> res=new ArrayList<Map<String,String>>();
		
		try {
			XMLsr = XMLfact.createXMLStreamReader(new FileReader(xml));
			int event = XMLsr.next();
			while (event != XMLStreamConstants.END_DOCUMENT) {
				switch (event) {
				case XMLEvent.START_ELEMENT:
					if(XMLsr.getLocalName().equals(Name))
					{
						Map<String,String> newNode=new TreeMap<String,String>();
						int nbattr = XMLsr.getAttributeCount();
						//.out.println("count attr");
						for (int i = 0; i < nbattr; i++) {
							newNode.put(XMLsr.getAttributeLocalName(i),
									XMLsr.getAttributeValue(i));
						}
					
						String content=XMLsr.getElementText();						
						if (!content.equals(""))
						{
							newNode.put("content",content);
						}
						//return newNode;
						res.add(newNode);
					}
					break;
				case XMLEvent.CHARACTERS:
					String chaine = XMLsr.getText();
					if (!XMLsr.isWhiteSpace()) {
					}
					break;
				default:
					break;

				}
				event = XMLsr.next();
			}
			XMLsr.close();
			//System.out.println("parsing returning res : " + res);
			return res;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
	
	public void sendWaitingMessages(){
		container.releaseWaitingMsg();
	}
	
	
	
	public void setContainer(SciflowareComponentContainer c){
		container=c;
	}
	
	public void InitOn(SciflowareComponentContainer c) {
		container=c;
	}
	
	public void logINFO(String msg){
		if (container!=null){
			container.logINFO(msg);
		}
	}

	public Map<String, Set<String>> getNeighbours() {
		return neighbours;
	}

	public Set<String> getNeighbours(String service) {
		Set<String> s = neighbours.get(service);
		if (s == null)
			return new TreeSet<String>();
		return s;
		
	}
	
	protected void sendWhenAvailable(MessageImpl msg) {
		container.sendWhenAvailable(msg);
	}
	
	protected void send(MessageImpl msg) {
		container.send(msg);
	}
	
	protected void connectTo(final String src,
			final String type, final String dest) {
		
		if(container == null){
			System.err.println("Error : container is null! ");
		}
		
		container.sendWhenAvailable(new MessageImpl("connectTo", new PropertyMap() {
			private static final long serialVersionUID = 1L;
			{
				put("id_src", src);
				put("type_dest", type);
				put("id_dest", dest);
				put("dc", "");
				put("tc", "");
				put("sc", "");
			}
		}, new PropertyMap()));

	}
	
	public void connectTo(final String src,
			final String type, final String dest, final String dc,
			final String tc, final String sc) {
		
		if(container == null){
			System.err.println("Error : container is null! ");
		}

		container.sendWhenAvailable(new MessageImpl("connectTo", new PropertyMap() {
			private static final long serialVersionUID = 1L;
			{
				put("id_src", src);
				put("type_dest", type);
				put("id_dest", dest);
				put("dc", dc);
				put("tc", tc);
				put("sc", sc);
			}
		}, new PropertyMap()));
	}

	/*	public List<Map<String, String>> parseXmlByName(String path, String Name) {


	Document document = null;
	Element root;

	Map<String, String> res = new TreeMap<String, String>();

	File test = new File(path);


	try {
		SAXBuilder builder = new SAXBuilder();
		// bug ? non r�solu :
		// blocage sur new SAXBuilder(); ---> bloque quand appel par un composant quand code dans scifloware component, ne bloque pas quand code directement dans le composant
		//
		Document test1 = new Document();
		document = builder.parse();
		System.out.println("saxbuilder OK ");
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	System.out.println("get root ");

	root = document.getRootElement();

	System.out.println(root);
	Element content = root.getChild(Name);

	List attributes = content.getAttributes();
	System.out.println(attributes);
	Iterator it = attributes.iterator();

	while (it.hasNext()) {
		Attribute e = (Attribute) it.next();
		System.out.println(e);
		res.put(e.getName(), e.getValue());
	}
}
	 */
	
	
}
