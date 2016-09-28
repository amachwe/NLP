package rd.ml.nlp.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import rd.ml.digester.service.DataSink;
import rd.ml.digester.service.DigestService;

@Path("/digest")
public class DigestAndWriteServiceImpl implements DigestAndWriteService {

	private static final Logger logger = Logger.getLogger(DigestAndWriteServiceImpl.class);
	private final DigestService digestService;
	private final DataSink dataSink;

	public DigestAndWriteServiceImpl(DigestService digestService, DataSink dataSink) {
		this.dataSink = dataSink;
		this.digestService = digestService;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Response digestAndWriteWs(@QueryParam("path") String rootDir) {
		try {
			dataSink.write(digestService.run(rootDir));
			return Response.status(Status.OK).entity("Complete for path: ["+rootDir+"]").header("Content-Type", "text/html").build();
			
		} catch (Exception e) {
			logger.error(e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@Override
	public void digestAndWrite(String rootDir) throws Exception {
		try {
			dataSink.write(digestService.run(rootDir));
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

	}

}
