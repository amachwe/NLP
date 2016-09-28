package rd.ml.digester.activation;

import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import rd.ml.nlp.data.DigestLogger;
import rd.ml.nlp.data.MongoLogger;

public class Activator implements BundleActivator, ManagedService {

	private static final Logger logger = Logger.getLogger(Activator.class);

	private ServiceRegistration handlerRegistration, msRegistration;

	private ServiceTracker servTracker;
	private BundleTracker bunTracker;

	private MongoClient mc;
	private MongoCollection<Document> mColl;
	private String host = "localhost";
	private int port = 27017;

	private MongoLogger ml;

	private static final String CONFIG_PID = "rd.ml.nlp.digester.mongo";

	@SuppressWarnings("unchecked")
	@Override
	public void start(BundleContext ctx) throws Exception {

		Dictionary mserv = new Properties();
		mserv.put(Constants.SERVICE_PID, CONFIG_PID);
		msRegistration = ctx.registerService(ManagedService.class.getName(), this, mserv);
		createFileEventLogger(ctx);
		createMongoEventLogger(ctx, "Events", "EventLog");

		servTracker = new ServiceTracker(ctx, handlerRegistration.getReference(), new ServiceTrackerCustomizer() {

			@Override
			public Object addingService(ServiceReference arg0) {
				logger.info("Added: " + arg0);
				return arg0;
			}

			@Override
			public void modifiedService(ServiceReference arg0, Object arg1) {
				logger.info("Mod: " + arg0 + " Obj: " + arg1);

			}

			@Override
			public void removedService(ServiceReference arg0, Object arg1) {
				logger.info("Remove: " + arg0 + "  Obj: " + arg1);

			}

		});

		servTracker.open();

		bunTracker = new BundleTracker(ctx, 0, new BundleTrackerCustomizer() {

			@Override
			public Object addingBundle(Bundle arg0, BundleEvent arg1) {
				logger.info("Added: " + arg0);
				return arg0;
			}

			@Override
			public void modifiedBundle(Bundle arg0, BundleEvent arg1, Object arg2) {
				logger.info("Mod: " + arg0 + " Obj: " + arg1);

			}

			@Override
			public void removedBundle(Bundle arg0, BundleEvent arg1, Object arg2) {
				logger.info("Remove: " + arg0 + " Obj: " + arg1);

			}

		});

		bunTracker.open();

		logger.info("Digester ready");
	}

	@Override
	public void stop(BundleContext ctx) throws Exception {

		if (handlerRegistration != null) {
			ctx.ungetService(handlerRegistration.getReference());
		}
		if (mc != null) {
			mc.close();
		}
		logger.info("Stopping Digester");
	}

	/**
	 * 
	 * @param ctx: Context
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createFileEventLogger(BundleContext ctx) {
		Dictionary props = new Properties();

		props.put(EventConstants.EVENT_TOPIC, "nlp/digest/uselog");
		props.put("target", "file");
		handlerRegistration = (ServiceRegistration) ctx.registerService(EventHandler.class.getName(),
				new DigestLogger(), props);
	}
	/**
	 * 
	 * @param ctx: Context
	 * @param db: DB Name
	 * @param coll: Collection Name
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createMongoEventLogger(BundleContext ctx, String db, String coll) {

		ml = new MongoLogger(host, port, db, coll);
		Dictionary props = new Properties();

		props.put(EventConstants.EVENT_TOPIC, "nlp/digest/uselog");
		props.put("target", "mongo");

		handlerRegistration = (ServiceRegistration) ctx.registerService(EventHandler.class.getName(), ml, props);
	}

	@Override
	public void updated(Dictionary arg0) throws ConfigurationException {
		logger.info("Updated: " + arg0);
		if (arg0.get("host") != null) {
			host = (String) arg0.get("host");
		}
		if (ml != null) {
			ml.init(host, port);
		}

	}

}
