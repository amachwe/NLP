package rd.ml.digester.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

import rd.ml.nlp.data.WordMap.Type;
import rd.ml.nlp.data.WordMapToDocument.Keys;

public class MongoDataSink implements DataSink {

	private final InsertManyOptions options = new InsertManyOptions();
	{
		options.ordered(false);
	}

	private static final Logger logger = Logger.getLogger(MongoDataSink.class);
	private final String host, dbName, topicCollName, docCollName;
	private final int port;

	public MongoDataSink(String host, int port, String dbName, String topicCollName, String docCollName) {

		this.port = port;
		this.host = host;
		this.dbName = dbName;
		this.topicCollName = topicCollName;
		this.docCollName = docCollName;

		logger.info("Service details: " + this.host + ":" + this.port + "/" + this.dbName + " / " + this.topicCollName
				+ " " + this.docCollName);
	}

	@Override
	public void write(List<Document> docsList) throws Exception {
		MongoClient mc = new MongoClient(host, port);
		mc.dropDatabase(dbName);
		List<Document> docs = new ArrayList<>();
		List<Document> topics = new ArrayList<>();
		docsList.forEach(doc -> {
			if (doc.getString(Keys.Type.toString()).equalsIgnoreCase(Type.Document.toString())) {
				docs.add(doc);
			} else {
				topics.add(doc);
			}

		});
		MongoDatabase db = mc.getDatabase(dbName);
		if (db == null) {
			logger.warn("Database null, name: " + dbName);
		}
		db.getCollection(docCollName).insertMany(docs, options);
		db.getCollection(topicCollName).insertMany(topics, options);
		logger.info("Processed and added: " + (docs.size() + topics.size()));
		mc.close();

	}

}
