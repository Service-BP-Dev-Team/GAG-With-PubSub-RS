package inria.scifloware.sciflowarecomponent;

import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import inria.smarttools.core.component.Container;
import inria.smarttools.core.component.STGenericActivator;

public class Activator extends STGenericActivator {
	
	public static final Logger LOGGER = Logger.getLogger("SciFloware Component");

	static {
		Activator.LOGGER.setParent(Logger.getLogger("SmartTools"));
	}
	
	public Activator() {
		resourceFileName = "/inria/scifloware/sciflowarecomponent/resources/inria.scifloware.sciflowarecomponent.cdml";
		bundleName = "sciflowarecomponent";
	}
	
	@Override
	public Container createComponent(final String componentId) {
		final SciflowareComponentContainer container = new SciflowareComponentContainer(
				componentId, resourceFileName);
		registerContainer(container);
		container.setContext(context);
		return container;
		//
	}
	

}
