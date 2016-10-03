package rd.ml.nlp.data;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

/**
 * Mongo Logger, listens for events and logs into Mongo db
 * @author azahar
 *
 */
public class MongoLogger implements EventHandler {

	private static final Logger logger = Logger.getLogger(MongoLogger.class);
	private MongoCollection<Document> mColl;
	private MongoClient mc;
	private String host = "localhost", db = "Events", coll = "EventLog";
	private int port = 27017;

	/**
	 * Log data events to Mongo db
	 * 
	 * @param host
	 * @param port
	 * @param db
	 *            - Mongo database name
	 * @param coll
	 *            - Mongo collection name
	 */
	public MongoLogger(String host, int port, String db, String coll) {
		this.db = db;
		this.coll = coll;

		init(host, port);
		this.host = host;
		this.port = port;

	}

	/**
	 * Re initialise connection when configuration changes.
	 * 
	 * @param host
	 * @param port
	 */
	public void init(String host, int port) {
		if (logger.isInfoEnabled()) {
			logger.info("Attempting to reinit Mongo connection, old host+port: " + this.host + ":" + this.port);
		}
		if (mc != null) {

			mc.close();
		}
		mc = new MongoClient(host, port);
		mColl = mc.getDatabase(db).getCollection(coll);
		if (logger.isInfoEnabled()) {
			logger.info("Done, new host+port: " + host + ":" + port);
		}
	}

	@Override
	public void handleEvent(Event event) {
		Document doc = new Document();
		for (String propName : event.getPropertyNames()) {
			doc.put(propName.replace(".", "_"), event.getProperty(propName));
		}

		mColl.insertOne(doc);
	}

}
