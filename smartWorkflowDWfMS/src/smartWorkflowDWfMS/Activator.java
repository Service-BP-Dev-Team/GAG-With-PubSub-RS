package smartWorkflowDWfMS;

import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.STGenericActivator;

public class Activator extends STGenericActivator {

	public Activator() {
		resourceFileName = "/smartWorkflowDWfMS/resources/smartWorkflowDWfMS.cdml";
		bundleName = "smartWorkflowDWfMS";
	}

	@Override
	public Container createComponent(final String componentId) {
		final SmartWorkflowDWfMSContainer container = new SmartWorkflowDWfMSContainer(
				componentId, resourceFileName);
		registerContainer(container);
		return container;
	}

}
