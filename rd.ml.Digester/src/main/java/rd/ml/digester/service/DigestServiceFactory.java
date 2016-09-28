package rd.ml.digester.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;

public final class DigestServiceFactory implements ServiceFactory {

	public DigestServiceFactory()
	{
		
	}

	private static final AtomicInteger i = new AtomicInteger(0);
	@Override
	public Object getService(Bundle arg0, ServiceRegistration arg1) {
		System.out.println("GOT "+i.incrementAndGet());
		return new DigestServiceImpl((EventAdmin)arg0.getBundleContext().getService(arg0.getBundleContext().getServiceReference(EventAdmin.class.getName())));
	}

	@Override
	public void ungetService(Bundle arg0, ServiceRegistration arg1, Object arg2) {
		arg2 = null;
		
	}
}
