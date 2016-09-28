package rd.ml.digester.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

public class FileWriterDataSink implements DataSink {

	
	private final String outputFileName;

	private static final Logger logger = Logger.getLogger(FileWriterDataSink.class);

	public FileWriterDataSink(String outputFileName) throws FileNotFoundException {
		this.outputFileName = outputFileName;
	}

	@Override
	public void write(List<Document> docs)  throws Exception {
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(outputFileName), true));) {
			docs.forEach(doc -> {
				pw.println(doc.toJson());
			});
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

}
