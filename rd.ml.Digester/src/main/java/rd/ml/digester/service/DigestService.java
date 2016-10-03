package rd.ml.digester.service;

import java.util.List;

import org.bson.Document;

/**
 * Service interface to digest documents from a directory
 * @author azahar
 *
 */
public interface DigestService {

	/**
	 * Tuneable version
	 * @param rootDirectory - root directory
	 * @param instance - max number of parallel instances
	 * @return
	 */
	List<Document> run(String rootDirectory, int instance);

	/**
	 * Fixed - 2 instances used.
	 * @param rootDirectory - root directory
	 * @return
	 */
	List<Document> run(String rootDirectory);

}