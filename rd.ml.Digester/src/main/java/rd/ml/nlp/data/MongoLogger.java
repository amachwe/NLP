package rd.ml.nlp.data;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class MongoLogger implements EventHandler {

	private static final Logger logger = Logger.getLogger(MongoLogger.class);
	private MongoCollection<Document> mColl;
	private MongoClient mc;
	private String host = "localhost", db = "Events", coll = "EventLog";
	private int port = 27017;

	public MongoLogger(String host, int port, String db, String coll) {
		this.db = db;
		this.coll = coll;
		this.host = host;
		this.port = port;
		init(host,port);

	}

	public void init(String host,int port) {
		if (mc != null) {

			mc.close();
		}
		mc = new MongoClient(host, port);
		mColl = mc.getDatabase(db).getCollection(coll);
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
