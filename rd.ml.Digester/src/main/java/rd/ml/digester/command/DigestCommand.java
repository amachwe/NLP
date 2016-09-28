package rd.ml.digester.command;

import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.osgi.service.event.EventAdmin;

import rd.ml.digester.service.DataSink;
import rd.ml.digester.service.DigestServiceImpl;
import rd.ml.digester.service.FileWriterDataSink;

@Command(scope = "nlp", name = "digest_test", description = "digester command - digest plain text documents in a directory (recursively) through a NLP pipeline outputs to a default file 'default_output.txt'")
@Service
public class DigestCommand implements Action {

	private static final Logger logger = Logger.getLogger(DigestCommand.class);

	@Argument(index = 0, name = "directory", required = true, description = "the root directory for the text files.")
	String directory = null;

	@Argument(index = 1, name = "output", required = false, description = "the directory to put the output")
	String output = null;

	private static final String defaultOutput = "default_output.txt";

	@Reference
	EventAdmin admin;
	public DigestCommand() {

	}

	@Override
	public Object execute() throws Exception {

		if(output==null)
		{
			output = defaultOutput;
			System.out.println("Using default output file 'default_output.txt' ");
		}
		if (directory == null) {
			return "Error - root directory cannot be blank.";
		} else {

			DataSink dataSink = new FileWriterDataSink(output);

			toConsole("Executing with root directory:  " + directory);
			try {
				long start = System.currentTimeMillis();
				List<Document> docs = (new DigestServiceImpl(admin)).run(directory);
				dataSink.write(docs);
				System.out.println("Time taken (ms): " + (System.currentTimeMillis() - start));
				if (docs.isEmpty()) {
					return "Bad status: There might have been an error.. please check logs";
				}
			} catch (Exception e) {
				toConsole("Error: check log for details. " + e.toString());

				logger.error(e);
			}
			return "Done";
		}
	}

	private void toConsole(String message) {
		System.out.println(message);
	}

}
