package soaApp.view;

public interface IEditorView {

	public void messageSend(String expeditor, String name);
	
	public void frowardMessageArrived(String expeditor, String message);
	public void returnMessageArrived(String expeditor, String message);
	public void frowardMessage(String expeditor, String message);
	public void returnMessage(String expeditor, String message);
	
	
}
