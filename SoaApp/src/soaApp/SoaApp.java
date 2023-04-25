package soaApp;

import gag.ServiceNode;
import gag.Term;
import gag.behaviour.SubscriptionTable;
import gag.son.SONAdaptator;
import soaApp.view.EditorFrame;
import soaApp.view.IEditorView;
import test.WorkflowFrame;

public abstract class SoaApp extends inria.communicationprotocol.CommunicationProtocolFacade {
	protected WorkflowFrame view = null;
	protected SONAdaptator orchestrator;
	public SoaApp () {
		//view = getView ();
		//SwingUtilities.invokeLater(new Runnable() {
       //     public void run() {
       //         view = getView ();
        //    }
        //});
		orchestrator= new SONAdaptator();
		orchestrator.setSoaApp(this);
	}

	public void inInvokeTo(String expeditor, ServiceNode service, SubscriptionTable subscription) {
		System.out.println("The input inForwardTo service has been invoked with " + service.text());
		orchestrator.execute(expeditor, service, subscription);
	}
	
	public void inReturnTo(String expeditor, Term t) {
		System.out.println("The input inReturnTo service has been invoked with " + t.text());
		orchestrator.receiveTerm(expeditor, t);
		//view.returnMessageArrived(expeditor, t.text());
		/*if (expeditor.equals("B"))
		outReturnTo(expeditor, repliquePartielle + expeditor); //on retourne le msg � l'exp�diteur apr�s l'avoir estampill�*/
	}

	public abstract void outBanane(String expeditor,String repliquePartielle);
	public abstract void outInvokeTo(String expeditor, ServiceNode service, SubscriptionTable subscription);
	public abstract void outReturnTo(String expeditor, Term t);
/*public abstract void outForwardTo(String adressee, String repliquePartielle);
public abstract void outReturnTo(String adressee, String repliquePartielle);*/

public WorkflowFrame getView() {
	if (view == null) {
		view = new WorkflowFrame(this);
	}
	return view;
}

@Override
protected void neighbourJustConnected(String name, String service) {
		if (name.equals("ComponentsManager")) {
			view = getView ();
		}
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

	public SONAdaptator getOrchestrator() {
		return orchestrator;
	}

	public void setOrchestrator(SONAdaptator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	

}
