package rd.ml.nlp.service;

import javax.ws.rs.core.Response;

/**
 * Digest Text and Write data to a source
 * @author azahar
 *
 */
public interface DigestAndWriteService {

	/**
	 * Digest and write to the currently configured Sink
	 * @param rootDir
	 * @throws Exception
	 */
	public void digestAndWrite(String rootDir) throws Exception;
	
	/**
	 * Web-Service method for digest and write 
	 * @param param - Query Parameters - namely root directory
	 * @return
	 * @throws Exception
	 */
	public Response digestAndWriteWs(String param) throws Exception;
}
