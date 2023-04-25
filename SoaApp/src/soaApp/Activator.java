package soaApp;

import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.STGenericActivator;

public class Activator extends STGenericActivator {

	public Activator() {
		resourceFileName = "/soaApp/resources/soaApp.cdml";
		bundleName = "soaApp";
	}

	@Override
	public Container createComponent(final String componentId) {
		final SoaAppContainer container = new SoaAppContainer(
				componentId, resourceFileName);
		registerContainer(container);
		return container;
	}

}
