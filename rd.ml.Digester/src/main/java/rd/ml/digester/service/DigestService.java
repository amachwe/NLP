package rd.ml.digester.service;

import java.util.List;

import org.bson.Document;

public interface DigestService {

	List<Document> run(String rootDirectory, int instance);

	List<Document> run(String rootDirectory);

}