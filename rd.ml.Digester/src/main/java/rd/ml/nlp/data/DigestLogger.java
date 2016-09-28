package rd.ml.nlp.data;

import org.apache.log4j.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DigestLogger implements EventHandler {


	private static final Logger logger = Logger.getLogger(DigestLogger.class);

	@Override
	public void handleEvent(Event event) {
		StringBuilder sb = new StringBuilder("Digest Event\n");
		for (String propName : event.getPropertyNames()) {
			sb.append(propName);
			sb.append(": ");
			sb.append(event.getProperty(propName));
			sb.append("\n");
		}

		logger.info(sb);
	}

}
