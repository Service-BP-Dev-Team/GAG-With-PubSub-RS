package inria.scifloware.sciflowarecomponent.REST;

import inria.scifloware.sciflowarecomponent.SciflowareComponentContainer;
import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.Attribute;
import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.InOut;
import inria.smarttools.core.component.Input;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.PropertyMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * Http Servlet to publish REST components on an existing HTTP server from a SON container. Extract the components from CDML file and
 * handle the incoming HTTP requests. 
 * 
 * @author dimitri.dupuis@inria.fr
 */

public class ServletREST extends HttpServlet {

	private static final long serialVersionUID = -8425301515276406336L;
	private SciflowareComponentContainer component;
	
	private ComponentDescription CmpDesc;
	private Map<String, Input> InputsCmp;
	private List<String> methods=new ArrayList<String>();
	private List<String> services=new ArrayList<String>();
	private Map<String, InOut> InOutsCmp;
		
	public ServletREST() {
	}
	
	/**
	 * 
	 * Initialize the servlet and get the component description and the REST inputs from the container CDML 
	 * 
	 * @param o Object corresponding to the container of the SON component 
	 * 
	 * @param AllRest Boolean flag : if set to true all inputs will be considered as REST, if false only specific REST input are treated as REST
	 */
	public void InitServlet(Object o){

		component=(SciflowareComponentContainer) o;
		CmpDesc=component.getContainerDescription();
		
		// Get the REST inputs from CDML
		InputsCmp=CmpDesc.getRESTInputs();
		InOutsCmp=CmpDesc.getRESTInOuts();
		Set<String> keys=InputsCmp.keySet();
		
		for(String key : keys){
			// get Methods (method name) and service (service name)
			methods.add(InputsCmp.get(key).getMethodName());
			services.add(key);
		}
		
		keys=InOutsCmp.keySet();
		
		for(String key : keys){
			// get Methods (method name) and service (service name)
			methods.add(InOutsCmp.get(key).getMethodName());
			services.add(key);
		}
	}

	/**
	 * 
	 * service is called when you have an incoming HTTP request from a client (GET, POST, PUT ...). For our use we are going to take only
	 * GET and POST request and parse the request to get the service called and the arguments.
	 * 
	 * 
	 * @param req HttpServletRequest object from servlet : contains all informations on the request 
	 * 
	 * @param resp HttpServletResponse object from servlet : Can be used to send a response to the client
	 */
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		try{
			
			//System.out.println("Thread : Servlet Service"+Thread.currentThread().getName());
			
			// TO write the response 
			PrintWriter out=resp.getWriter();
			
			// URI of the request
			String uri=req.getPathInfo();
		 
			CheckURI(uri);
			
			//final AsyncContext ac = req.startAsync(req, resp);
			
			resp.setStatus(200);
			
			// HTTP Request process
			handleMethod(out,req);
			
			resp.getWriter().println("Ok");
			
		}
		catch(Exception e){
			// Code 404 : Not Found
			resp.setStatus(404);
			resp.getWriter().println(e.getMessage());
			((SciflowareComponentContainer) component).logERROR(e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * Get the parameters of the request if it is GET or POST, Content-Type should be "application/x-www-form-urlencoded"
	 * and arguments should be in the form (param=value&param2=value2)
	 * 
	 * @param req Request
	 * @return Map of the arguments (name:value)
	 * @throws Exception
	 */
	private Map<String,String> GetRequestParam(HttpServletRequest req) throws Exception{
		Map<String,String> res= new HashMap<String,String>();
		
		// Check for json
		if(req.getMethod().equals("POST") && req.getContentType()!=null && req.getContentType().contains("application/json")){
			System.out.println("Json ...");
			
			BufferedReader reader=req.getReader();
			String json="";
			String line= "";
		

			while((line=reader.readLine())!=null)
			{
				json=json+line;
			}
		
			System.out.println("json : "+json);
			
			// TODO parse json
			
			/*JsonParserFactory factory = Json.createParserFactory(null);
			JsonParser parser = factory.createParser(new StringReader(json));

			String key="";
			String value="";
			  
			while (parser.hasNext()) {
			  Event event = parser.next();
		  
			  switch (event) {
			    case KEY_NAME: {
			    	key=parser.getString(); break;
			    }
			    case VALUE_STRING: {
			    	value=parser.getString();
			    	res.put(key, value);
					key="";
					value=""; break;
			    }
			  }		  
			}*/
			
			System.out.println("Test res = "+res.toString());
			return res;
			
		} else if (req.getMethod().equals("POST") || req.getMethod().equals("GET")){
			Map<String,String[]> PostParams=req.getParameterMap();
			if(!PostParams.isEmpty()){
				Integer i=0;
				Set<String> keys=PostParams.keySet();
				for(String key : keys)
				{
					if(PostParams.get(key)[0].equals("")){
						res.put(i.toString(), key);
						i++;
					}
					else{
						res.put(key, PostParams.get(key)[0]);
					}
				}
			}
			else{
				return res;
			}
		}
		else{
			throw new Exception("Method is HTTP Request is incorrect !"
					+ "Accepted methods : (GET,POST)");
		}
		
		return res;
	}
/**
 * 
 * Handle the request if the component is present	
 * 
 * @param out Response writer
 * @param req Incoming Request
 */
private void handleMethod(PrintWriter out, HttpServletRequest req) {
		
			String uri=req.getPathInfo();
			
			//String clientadr=req.getServerName()
			
			//System.out.println("remote adr = "+req.getServerName()+" remote host "+req.getRemoteHost()+" remote port "+req.getServerPort()+"local port : "+req.getLocalPort()+"url :"+req.getRequestURL());
			Map<String, String> paramsReq = new HashMap<String,String>();
			try {
				paramsReq = GetRequestParam(req);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("req params :"+paramsReq.toString());
						
			// Get rid of the "/"
			String cleanUri=uri.replaceAll("/","");
			String res="";
			Map<String, String> args=new HashMap<String, String>();
			
			try {
				
				if(InputsCmp.get(cleanUri)==null && InOutsCmp.get(cleanUri)==null){
					throw new Exception("Method "+cleanUri+" not found in cdml");
				}else{
					if(InputsCmp.get(cleanUri)!=null){
						args=CheckParameters(InputsCmp.get(cleanUri),paramsReq);
						Message toSend=createMessage(InputsCmp.get(cleanUri),args,((SciflowareComponentContainer) component).getIdName());
						if(cleanUri.equals("Query")){
							toSend.setAsynch(false);
						}
						((SciflowareComponentContainer) component).sendFinal(toSend);
					}else{
						args=CheckParameters(InOutsCmp.get(cleanUri),paramsReq);
						Message toSend=createMessage(InOutsCmp.get(cleanUri),args,((SciflowareComponentContainer) component).getIdName());
						if(cleanUri.equals("QueryDataResp")){
							toSend.setAsynch(false);
						}
						((SciflowareComponentContainer) component).sendFinal(toSend);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			

	
	}
/**
 * 
 * Create a SON-Type Message to send to the component itself. Transforms the request in a SON message
 * 
 * @param input CDML extracted input
 * @param args Parameters from request
 * @return
 */
private Message createMessage(Input input, Map<String, String> args,String addresseExp) {
		
		PropertyMap props=new PropertyMap() {
			private static final long serialVersionUID = 1L;
			{
			}
		};
		
		Set<String> keys=args.keySet();
		for(String key : keys){
			props.put(key, args.get(key));
		}
		
		Message msg=new MessageImpl(input.getName(), props, new PropertyMap(), addresseExp);
		return msg;
}

/**
 * 
 * Check if we have the same number of parameters in request and in the CDML 
 * 
 * @param input CDML extracted input
 * @param paramsReq Parameters from request
 * @return
 * @throws Exception
 */
private Map<String, String> CheckParameters(Input input, Map<String, String> paramsReq) throws Exception {
		

		Map<String, String> ret=new HashMap<String, String>();
		
		Map<String,Attribute> Attributes=input.getAttributes();
		
		if(Attributes.size() == paramsReq.size()){
			// TODO Check attribute names ?
			// yep
			
			Set<String> CdmlParamsKeys=Attributes.keySet();
			for(String key : CdmlParamsKeys){
				if(!paramsReq.containsKey(key)){
					//System.err.println("argument manquant "+key+" (parameters are "+Attributes.toString()+")");
					throw new Exception("argument manquant "+key+" (parameters are "+Attributes.toString()+")");
				}
			}
				
			return paramsReq;
		}
		else{
			
		}
		return ret;
		
	}

/**
 * Check if the URI is correct and if it reffers to an existing component
 * 
 * @param uri2
 * @throws Exception Throw "Not found" if the component is not in the CDML
 */
private void CheckURI(String uri2){
		
		if(!uri2.endsWith("/")){
			uri2=uri2+"/";
		}

		String uri=uri2.replaceAll("/", "");
		if(!services.contains(uri)){
			//throw new Exception("");
			System.err.println("Error : Component not found or URI is incorrect !");
			component.logERROR("Error : Component not found or URI is incorrect !");
		}
	}

public boolean SendREST(String uri, Message msg) throws InterruptedException, TimeoutException, ExecutionException {
	
	//
	PropertyMap attrs=msg.getAttributesForMessage();
	Set<String> keys=attrs.keySet( );
	
	
	String params="";
	
	int i=0;
	for(String key : keys){
		if(i==0){
			i++;
		}else{params+="&";}
		
		params+=key+"="+attrs.get(key);
	}
	
	System.out.println("uri finale : "+uri);
	
	HttpClient cli= new HttpClient();
	try {
		cli.start();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}

	HttpExchange exchange = new HttpExchange();
	exchange.setMethod("POST");
	
	exchange.setURL(uri);
	
	Buffer Content = new ByteArrayBuffer(params);
	exchange.setRequestContent(Content);
	exchange.setRequestContentType("application/x-www-form-urlencoded");
	
	System.out.println("Rest Envoy�");
	
	try {
		cli.send(exchange);
	} catch (IOException e) {
		System.err.println(e.getLocalizedMessage());
		component.logERROR(e.getLocalizedMessage());
		return false;
	}

	return true;
	
}

public boolean SendRESTURL(String url) throws InterruptedException, TimeoutException, ExecutionException {
			
	HttpClient cli= new HttpClient();
	try {
		cli.start();
	} catch (Exception e) {
		System.err.println(e.getLocalizedMessage());
		component.logERROR(e.getLocalizedMessage());
		return false;
	}

	HttpExchange exchange = new HttpExchange();
	exchange.setMethod("POST");
	
	exchange.setURL(url);
	
	exchange.setRequestContentType("application/x-www-form-urlencoded");
	
	System.out.println("Rest Envoy�");
	
	try {
		cli.send(exchange);
	} catch (IOException e) {
		System.err.println(e.getLocalizedMessage());
		component.logERROR(e.getLocalizedMessage());
		return false;
	}

	return true;
	
}



}
