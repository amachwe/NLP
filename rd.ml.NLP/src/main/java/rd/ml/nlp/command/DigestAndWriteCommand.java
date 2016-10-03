package rd.ml.nlp.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.log4j.Logger;

import rd.ml.nlp.service.DigestAndWriteService;

/**
 * Command to Digest and Write documents to the destination (Mongo or File).
 * Usage: digestandwrite 'directory'
 * 
 * @author azahar
 *
 */
@Command(name = "digestandwrite", scope = "nlp", description = "Digest and Write to Database/File")
@Service
public class DigestAndWriteCommand implements Action {

	private static final Logger logger = Logger.getLogger(DigestAndWriteCommand.class);
	@Argument(index = 0, name = "directory", required = true, description = "Root directory for digesting (recursive).")
	String directory = null;

	@Reference
	DigestAndWriteService service;

	@Override
	public Object execute() throws Exception {

		String message = "Pending...";
		if (service != null) {
			service.digestAndWrite(directory);
			message = "Complete.";
		} else {
			System.err.println("No service available to digest and write");
			System.out.println("No service available to digest and write");
			logger.error("No service available to digest and write");
			message = "Errors.";
		}
		System.out.println(message);
		return message;
	}

}
