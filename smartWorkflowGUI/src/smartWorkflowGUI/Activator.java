package smartWorkflowGUI;

import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.STGenericActivator;

public class Activator extends STGenericActivator {

	public Activator() {
		resourceFileName = "/smartWorkflowGUI/resources/smartWorkflowGUI.cdml";
		bundleName = "smartWorkflowGUI";
	}

	@Override
	public Container createComponent(final String componentId) {
		final SmartWorkflowGUIContainer container = new SmartWorkflowGUIContainer(
				componentId, resourceFileName);
		registerContainer(container);
		return container;
	}

}
