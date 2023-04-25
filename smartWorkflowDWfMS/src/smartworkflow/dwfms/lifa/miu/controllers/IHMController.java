package smartworkflow.dwfms.lifa.miu.controllers;

import smartworkflow.dwfms.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.dwfms.lifa.miu.controllers.util.IEditorController;
import smartworkflow.dwfms.lifa.miu.models.IHMModel;


public class IHMController extends ControllerModel implements IEditorController {
    
    public IHMController(IHMModel model) {
        super(model);
    }
    
    public void editDesign() {
        ((IHMModel)model).editDesign();
    }
    
    public void startTask(String task){
        ((IHMModel)model).startTask(task);
    }
    
    public void stopTask(String task){
        ((IHMModel)model).stopTask(task);
    }
    
    @Override
	public void forwardMessageArrived(String expeditor, String message) {
		
	}

	@Override
	public void returnMessageArrived(String expeditor, String message) {
		
	}

	@Override
	public void messageSend(String expeditor, String name) {
		
	}

	@Override
	public void forwardMessage(String expeditor, String message) {
		
	}

	@Override
	public void returnMessage(String expeditor, String message) {
		
	}
}
