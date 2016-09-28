package rd.ml.nlp.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public interface DigestAndWriteService {

	public void digestAndWrite(String rootDir) throws Exception;
	
	
	public Response digestAndWriteWs(String param) throws Exception;
}
