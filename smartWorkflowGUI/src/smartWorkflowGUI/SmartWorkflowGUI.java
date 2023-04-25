package smartWorkflowGUI;

import smartworkflow.gui.lifa.miu.controllers.StartupController;
import smartworkflow.gui.lifa.miu.controllers.util.IEditorController;
import smartworkflow.gui.lifa.miu.models.StartupModel;
import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.view.StartupView;
import smartworkflow.gui.lifa.miu.view.util.PanelModel;

public abstract class SmartWorkflowGUI extends inria.communicationprotocol.CommunicationProtocolFacade {
	protected IEditorController model = null;
	public SmartWorkflowGUI () {
		//view = getView ();
		//SwingUtilities.invokeLater(new Runnable() {
       //     public void run() {
       //         view = getView ();
        //    }
        //});
	}

	public void inForwardTo(String expeditor, String artefact) {
		System.out.println("The input inForwardTo service has been invoked with " + artefact);
		model.forwardMessageArrived(expeditor, artefact);
	}
	
	public void inReturnTo(String expeditor, String artefact) {
		System.out.println("The input inReturnTo service has been invoked with " + artefact);
		model.returnMessageArrived(expeditor, artefact);
		if (expeditor.equals("B"))
		outReturnTo(expeditor, artefact + expeditor); //on retourne le msg à l'expéditeur après l'avoir estampillé
	}

	public abstract void outForwardTo(String adressee, String artefact);
	public abstract void outReturnTo(String adressee, String artefact);
	
	public IEditorController getView() {
		if (model == null) {
			ModelModel.thisSONCommunicationInterface = this;
			PanelModel.setController(ModelModel.getIHMController());
			final StartupModel startupModel = new StartupModel();
	        StartupController controller = new StartupController(startupModel);
	        StartupView startupView = new StartupView(controller);
	        startupView.setEnabled(true);
			model = ModelModel.getIEditorController();
		}
		return model;
	}
	
	@Override
	protected void neighbourJustConnected(String name, String service) {
		if (name.equals("ComponentsManager")) {
			model = getView ();
		}
	}
	
	public void disconnectInput(String expeditor) {
		
	}
		
	public void shutdown(String expeditor) {
		
	}
	
	public Object requestTree(String expeditor) {
		return null;
	}
	
	public void quit(String expeditor) {
		
	}
}
