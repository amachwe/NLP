package rd.ml.digester.service;

import java.util.List;

import org.bson.Document;

public interface DataSink {

	final String TARGET = "target";
	public void write(List<Document> docs) throws Exception;
}
