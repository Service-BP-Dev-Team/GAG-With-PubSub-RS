package smartworkflow.dwfms.lifa.miu.controllers.util;

public interface IEditorController {

	public void messageSend(String expeditor, String name);
	
	public void forwardMessageArrived(String expeditor, String message);
	public void returnMessageArrived(String expeditor, String message);
	public void forwardMessage(String expeditor, String message);
	public void returnMessage(String expeditor, String message);
	
}
