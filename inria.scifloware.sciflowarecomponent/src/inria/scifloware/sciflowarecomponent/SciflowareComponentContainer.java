package inria.scifloware.sciflowarecomponent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import inria.scifloware.sciflowarecomponent.REST.HttpTracker;
import inria.scifloware.sciflowarecomponent.REST.ServletREST;
import inria.smarttools.core.Activator;
import inria.smarttools.core.component.AbstractContainer;
import inria.smarttools.core.component.ComponentDescription;
import inria.smarttools.core.component.ContainerProxy;
import inria.smarttools.core.component.Input;
import inria.smarttools.core.component.Message;
import inria.smarttools.core.component.MessageImpl;
import inria.smarttools.core.component.Output;

/**
 * 
 * AbstractContainer for SciFloware components : 
 * Added : - REST services 
 * - Complete list of neighboors for the component
 * - SwitchOn() actions 
 * 
 * @author dimitri.dupuis@inria.fr
 *
 */
public class SciflowareComponentContainer extends AbstractContainer implements
		inria.smarttools.core.component.Container,
		inria.scifloware.sciflowarecomponent.SendListener, inria.scifloware.sciflowarecomponent.UndoListener,
		inria.scifloware.sciflowarecomponent.DisconnectListener,
		inria.scifloware.sciflowarecomponent.InitDataListener,
		inria.scifloware.sciflowarecomponent.LogUndoListener,
		inria.scifloware.sciflowarecomponent.LogListener, inria.scifloware.sciflowarecomponent.ExitListener,
		inria.scifloware.sciflowarecomponent.ConnectToListener  {

	public SciflowareComponentFacade facade;
	protected ServletREST servlet=null;
	
	public ServletREST getServlet() {
		return servlet;
	}

	protected HttpService service;
	
	private boolean started = false;
	private BundleContext context;

	public SciflowareComponentContainer(final String name,
			final String componentDescriptionResource) {
		super(name, componentDescriptionResource);		
	}

	/**
	 * 
	 * Look for the HTTP service and create the corresponding servlet of the SciFloware component, throws 
	 * error if the HTTP service is not found
	 * 
	 * @param BundleContext c Context of the component  
	 * 
	 */
	
	public void CreateServlet(BundleContext c) throws Exception {
		
		// Searching for existing HTTP Service ( Ex : Jetty )
		HttpTracker tracker = new HttpTracker(c);
		tracker.open();
		
		// Get the HTTP Service
		service=tracker.getService();
		
		if(service!=null){
			//Create the servlet
			servlet=new ServletREST();
			InitiateRestServlet();
		}
		else{
			throw new Exception("Error : No HTTP Service found to publish the REST Servlet ...");
		}
		
	}

	
	public BundleContext GetContext(){
		return context;
	}
	
	protected void initFacadeListeners() {
		facade.addSendListener(this);
		facade.addUndoListener(this);
		facade.addDisconnectListener(this);
		facade.addInitDataListener(this);
		facade.addLogUndoListener(this);
		facade.addLogListener(this);
		facade.addExitListener(this);
		facade.addConnectToListener(this);
	}
		
	@Override
	public void connect(final ContainerProxy other) {
		super.connect(other);
		//CheckNeighbors(other);
	}
	
	@Override
	public void CheckNeighbors(AbstractContainer other) {
		
		final Map<String, Input> otherInputs = other.getContainerDescription().getInputs();
		if (otherInputs != null) {
			final Iterator<String> iterOtherInputs = otherInputs.keySet().iterator();
			final Collection<String> iterOuputs = getContainerDescription().getOutputs().keySet();
			while (iterOtherInputs.hasNext()) {
				final String inputName = iterOtherInputs.next();
				if (iterOuputs.contains(inputName)) {
					(facade).addNeighbour(other.getIdName(), inputName);
				}
			}
		}
		
		final Map<String, Output> otherOutputs = other.getContainerDescription().getOutputs();
		if (otherOutputs != null) {
			final Iterator<String> iterOtherOutputs = otherOutputs.keySet().iterator();
			final Collection<String> iterInputs = getContainerDescription().getInputs().keySet();
			while (iterOtherOutputs.hasNext()) {
				final String OutputName = iterOtherOutputs.next();
				if (iterInputs.contains(OutputName)) {
						(facade).addNeighbour(other.getIdName(), OutputName);
					}
				}
			}
		
		}
	
	@Override
	public void CheckNeighbors(ContainerProxy other) {
		
		final Map<String, Input> otherInputs = other.getContainerDescription().getInputs();
		if (otherInputs != null) {
			final Iterator<String> iterOtherInputs = otherInputs.keySet().iterator();
			final Collection<String> iterOuputs = getContainerDescription().getOutputs().keySet();
			while (iterOtherInputs.hasNext()) {
				final String inputName = iterOtherInputs.next();
				if (iterOuputs.contains(inputName)) {
					(facade).addNeighbour(other.getIdName(), inputName);
				}
			}
		}
		
		final Map<String, Output> otherOutputs = other.getContainerDescription().getOutputs();
		if (otherOutputs != null) {
			final Iterator<String> iterOtherOutputs = otherOutputs.keySet().iterator();
			final Collection<String> iterInputs = getContainerDescription().getInputs().keySet();
			while (iterOtherOutputs.hasNext()) {
				final String OutputName = iterOtherOutputs.next();
				if (iterInputs.contains(OutputName)) {
						(facade).addNeighbour(other.getIdName(), OutputName);
					}
				}
			}
		
		}

	@Override
	public void switchOn(){
		super.switchOn();
		if (!started) {
			(facade).setContainer(this);
			(facade).InitOn(this);
			started=true;
			if(context==null){
				logINFO("WARNING : No BundleContext available : REST components could not be available!");
			}
		}

	}
	
	/**
	 * 
	 * Overrides the send Method from abstract component : 
	 * Check the cdml and if we have a Rest Service to send we use the SendREST Method from servlet
	 * else we use the standard send()
	 * 
	 * @param msg Message of type SON Message (inria.smartools.core)
	 * 
	 */
	@Override
	public boolean send(final Message msg) {
		// REST Outputs from Cdml
		Map<String, Output> outputs=getContainerDescription().getRESToutputs();
		
		if(outputs.containsKey(msg.getName())){
			// Construct the URI
			String uri;
			String AdrDistante=msg.getAdresseeId();
			if(AdrDistante!=null){
				uri=AdrDistante+msg.getName()+"/";
			}			
			else{
				uri=context.getProperty("remote.jetty.adress")+msg.getName()+"/";
			}
			try{
				// Rest Send from servlet
				//System.out.println("uri (send): "+uri);
				boolean IsSent=servlet.SendREST(uri,msg);
				if(IsSent){
					return true;
				}
				else{
					return super.send(msg);
				}
			} catch(Exception e){
				e.printStackTrace();
				return super.send(msg);
			}
			
		}
		else{
			// Standard send()
			return super.send(msg);
		}
	}
	
	// Standard Send with any message
	public boolean sendFinal(final Message msg) {
		return super.send(msg);
	}
	
	/**
	 * 
	 * Initiate the previously created servlet, register the servlet and methods on the server
	 * @throws ServletException and Exception if the servlet is null
	 * 
	 * @param AllRest Boolean flag : if set to true all inputs will be considered as REST, if false only specific REST input are treated as REST
	 * @throws Exception
	 * 
	 */
	
	public void InitiateRestServlet() throws Exception {
		
		// Initialize the servlet with the current container 
		if(servlet==null){
			throw new Exception("the current servlet is null (check if servlet created)");
		}
		servlet.InitServlet(this);
		try {
			// Registering the servlet
			service.registerServlet("/"+this.getIdName(), servlet, null, null);
		} catch (ServletException | NamespaceException e) {
			this.logERROR(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * ConnectToListener
	 * 
	 * @throws IllegalStateException
	 *             to absolutely care in business methods
	 **/
	public void connectTo(final ConnectToEvent e) {
		sendWhenAvailable(new MessageImpl("connectTo", e.getAttributes(), null,
				e.getAdressee()));
	}

	@Override
	protected void createFacade() {
		facade = new SciflowareComponentFacade(getIdName());
		initFacadeListeners();
		if (context!=null && servlet==null){
			if( (!getContainerDescription().getRESTInputs().isEmpty()) || (!getContainerDescription().getRESToutputs().isEmpty()) ){
				try {
					CreateServlet(context);
				} catch (Exception e) {
					this.logERROR(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * DisconnectListener
	 * 
	 * @throws IllegalStateException
	 *             to absolutely care in business methods
	 **/
	public void disconnectOut(final DisconnectEvent e) {
		send(new MessageImpl("disconnect", e.getAttributes(), null,
				e.getAdressee()));
	}

	//
	// Constructors
	//

	/**
	 * ExitListener
	 * 
	 * @throws IllegalStateException
	 *             to absolutely care in business methods
	 **/
	
	public void logERROR(final String message) {
		logERROR(message, "[" + toString() + "]");
	}

	public void logERROR(final String message, final String pre) {
		if (Activator.LOGGER != null) {
			Activator.LOGGER.severe(pre + " " + message);
		} else {
			System.err.println(pre + " " + message);
		}
	}

	public void logINFO(final String message) {
		logINFO(message, "[" + toString() + "]");
	}

	public void logINFO(final String message, final String pre) {
		if (Activator.LOGGER != null) {
			Activator.LOGGER.info(pre + " " + message);
		} else {
			System.out.println(pre + " " + message);
		}
	}
	
	public void exit(final ExitEvent e) {
		send(new MessageImpl("exit", e.getAttributes(), null, e.getAdressee()));
	}

	
	public void log(final LogEvent e) {
		send(new MessageImpl("log", e.getAttributes(), null, e.getAdressee()));
		
	}

	public void logUndo(final LogUndoEvent e) {
		send(new MessageImpl("logUndo", e.getAttributes(), null,
				e.getAdressee()));
	}

	public void initData(final InitDataEvent e) {
		send(new MessageImpl("initData", e.getAttributes(), null,
				e.getAdressee()));
	}

	public void undo(final UndoEvent e) {
		send(new MessageImpl("undo", e.getAttributes(), null, e.getAdressee()));
	}

	public void send(final SendEvent e) {
		send(new MessageImpl("send", e.getAttributes(), null, e.getAdressee()));

	}
	
	@Override
	public void receive(final Message msg) {
		System.out.println("Thread : ContainerSciflowre receive"+Thread.currentThread().getName());
		
		if(!msg.isAsynch()){
			execute(msg);
		} else {
			super.receive(msg);
		}
	}
	
	public void sendWhenAvailable(MessageImpl msg) {
		super.sendWhenAvailable(msg);
	}

	public void setContext(BundleContext c) {
		context=c;
		// Create a servlet if we have REST services declared in CDML
		if( (!getContainerDescription().getRESTInputs().isEmpty()) || (!getContainerDescription().getRESToutputs().isEmpty()) ){
			try {
				if(servlet==null){
					CreateServlet(c);
				}
			} catch (Exception e) {
				this.logERROR(e.getMessage());
				e.printStackTrace();
			}
		}

	}

}
