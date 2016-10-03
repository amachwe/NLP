package rd.ml.digester.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import rd.ml.nlp.data.DirectoryParser;
import rd.ml.nlp.data.WordMap;
import rd.ml.nlp.data.WordMap.Type;
import rd.ml.nlp.data.WordMapImpl;
import rd.ml.nlp.data.WordMapToDocument;
/**
 * Implementation of the Digest Service - it takes documents and using a multi-threaded processor - processes them into a word matrix
 * @author azahar
 *
 */
public class DigestServiceImpl implements DigestService{
	private static final String TOPIC_NAME = "nlp/digest/uselog";
	private static final TokenizerFactory stopWordTokenizer = new EnglishStopTokenizerFactory(
			IndoEuropeanTokenizerFactory.INSTANCE);
	private static final Logger logger = Logger.getLogger(DigestService.class);

	private static final List<Document> empty = Collections.emptyList();

	private final EventAdmin admin;



	
	public DigestServiceImpl(EventAdmin admin) {
		this.admin = admin;
	
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rd.ml.digester.service.DigestService#run(java.lang.String)
	 */
	@Override
	public List<Document> run(String rootDirectory, int instance) {
		ForkJoinPool pool = new ForkJoinPool(instance);
		WordMap CORPUS = new rd.ml.nlp.data.WordMapImpl("CORPUS", "CORPUS", Type.Corpus);
		ConcurrentLinkedQueue<Document> docsQ = new ConcurrentLinkedQueue<>();

		logger.info("Root Directory: "+rootDirectory);
		Set<File> files = DirectoryParser.getFiles(new File(rootDirectory), DirectoryParser.TEXT_FILE_FILTER);
		
		try {
			return pool.submit(() -> {
				try {

					logger.info("--- Streaming ---");
					Stream<File> fileStream = StreamSupport.stream(files.spliterator(), true);
					fileStream.parallel().map(file -> {
						//Start file processing.
						String topicName = file.getParentFile().getName();
						logger.debug(topicName);
						String name = file.getName();
						char[] text = new char[0];
						try {
							String allText = "";
							for (String line : Files.readAllLines(file.toPath())) {
								allText += line;
							}

							text = allText.toCharArray();
						} catch (IOException e) {
							System.err.println("Warning: Not able to process " + name + " in " + topicName);
							System.err.println(e.getMessage());
							logger.error(e);
						}
						Tokenizer tokenizer = stopWordTokenizer.tokenizer(text, 0, text.length);
						final rd.ml.nlp.data.WordMap doc = new WordMapImpl(topicName, name, WordMap.Type.Document);
						final WordMap topic = new WordMapImpl(topicName, topicName, WordMap.Type.Topic);

						tokenizer.forEach(str -> {
							if (check(str)) {
								String docID = topicName + "_" + name;

								doc.add(str, docID);
								topic.add(str, name);
								CORPUS.addOncePerDoc(str, docID);

							}
						});
						if (doc != null) {
							docsQ.add(WordMapToDocument.getDocument(doc));
						}

						return topic;

						//Collect and Group by Topic Name
					}).collect(Collectors.groupingBy(WordMap::getTopic)).forEach((name, topics) -> {
						WordMap topicName = new WordMapImpl(name, name, WordMap.Type.Topic);
						topics.stream().forEach(topic -> {
							topicName.accumulate(topic);

						});

						docsQ.add(WordMapToDocument.getDocument(topicName));

					});

				} catch (Exception e) {

					logger.error(e);
					sendErrorEvent(e, admin);
					return empty;
				}

				logger.info("--- Writing ---");
				docsQ.add(WordMapToDocument.getDocument(CORPUS));
				sendEvent(docsQ.size(), admin);
				return docsQ.stream().parallel().collect(Collectors.toList());
			}).get();
		} catch (Exception e) {
			logger.error(e);
			sendErrorEvent(e, admin);
			return empty;

		}

	}

	/**
	 * Send Event
	 * @param size
	 * @param admin Event Admin Service
	 */
	private static final void sendEvent(int size, EventAdmin admin) {
		Map<String, Object> value = new HashMap<>();
		value.put("NumberOfDocs", size);
		value.put(EventConstants.TIMESTAMP, System.currentTimeMillis());
		Event e = new Event(TOPIC_NAME, value);
		admin.postEvent(e);

	}

	/**
	 * Send Error Event
	 * @param e - Exception
	 * @param admin Event Admin Service
	 */
	private static final void sendErrorEvent(Exception e, EventAdmin admin) {
		Map<String, Object> value = new HashMap<>();
		value.put(EventConstants.EXCEPTION_MESSAGE, e.getMessage());
		value.put(EventConstants.EXCEPTION, e);
		value.put(EventConstants.TIMESTAMP, System.currentTimeMillis());
		Event ev = new Event(TOPIC_NAME, value);
		admin.postEvent(ev);

	}

	private static final boolean check(String str) {
		String _str = str.trim();
		if (_str.length() > 1) {
			if (!_str.contains(".")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<Document> run(String rootDirectory) {
		return run(rootDirectory, 2);

	}




}
