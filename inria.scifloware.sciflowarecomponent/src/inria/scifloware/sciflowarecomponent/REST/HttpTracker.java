package inria.scifloware.sciflowarecomponent.REST;


import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * Tracker to track existing HTTP service in osgi launched bundles. BundleContext needed. 
 * 
 * @author dimitri.dupuis@inria.fr
 *
 */
public class HttpTracker extends ServiceTracker {
	
		protected HttpService httpService=null;
		
		public HttpTracker(BundleContext context) {
			super(context, HttpService.class.getName(), null);
		}
		
		@Override
		public Object addingService(ServiceReference reference) {
			httpService = (HttpService) context.getService(reference);			
			return null;
		}
		
		public HttpService getService(){
			return httpService;
		}
		
		@Override
		public void removedService(ServiceReference reference, Object service) {
			
			context.ungetService(reference);
		}
}
