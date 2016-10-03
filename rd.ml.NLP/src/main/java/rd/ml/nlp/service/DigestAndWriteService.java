package rd.ml.nlp.service;

import javax.ws.rs.core.Response;

/**
 * Digest Text and Write data to a source
 * @author azahar
 *
 */
public interface DigestAndWriteService {

	public void digestAndWrite(String rootDir) throws Exception;
	
	
	public Response digestAndWriteWs(String param) throws Exception;
}
