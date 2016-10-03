package rd.ml.nlp.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public final class WordMatrix {
	private static final Logger logger = Logger.getLogger(WordMatrix.class);

	private final ConcurrentHashMap<String, Integer> docI = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> termI = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, String> revDocI = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, String> revTermI = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> termDocCounts = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Set<String>> topicTerms = new ConcurrentHashMap<>();

	private AtomicInteger termC = new AtomicInteger(0),docC = new AtomicInteger(0);
	private double[][] termCounts = null;
	private final int incrementTerms, incrementDocs;

	/**
	 * 
	 * @param maxDocs - maximum number of documents to be stored
	 * @param maxTerms - maximum number of terms to be stored
	 * @param incrementDocs - 
	 * @param incrementTerms
	 */
	public WordMatrix(int maxDocs, int maxTerms, int incrementDocs, int incrementTerms) {
		termCounts = new double[maxTerms][maxDocs];
		this.incrementDocs = incrementDocs;
		this.incrementTerms = incrementTerms;
	}

	/**
	 * 
	 * @param topic
	 * @param term
	 */
	public void addTopic(String topic, String term) {
		if (topicTerms.get(topic) == null) {
			synchronized (this) {
				Set<String> set = new ConcurrentSkipListSet<>();
				set.add(term);
				topicTerms.put(topic, set);
			}
		} else {
			topicTerms.get(topic).add(term);
		}

	}

	/**
	 * 
	 * @param docId
	 * @param term
	 */

	public void addWord(String docId, String term) throws InterruptedException {
		Integer docIdx = null;
		Integer termIdx = null;
		if(term.trim().isEmpty())
		{
			return;
		}
		//System.out.println(docId+"  + "+term);
		if ((termIdx = termI.get(term)) == null) {
			synchronized (this) {

				termIdx = termC.getAndIncrement();
			
				termI.put(term, termIdx);
				revTermI.put(termIdx, term);
			}

		}
		if ((docIdx = docI.get(docId)) == null) {
			synchronized (this) {
				
				docIdx = docC.getAndIncrement();
				docI.put(docId, docIdx);
				revDocI.put(docIdx, docId);

			}

		}

		synchronized (this) {
		
//			if (termIdx >= termCounts.length || docIdx >= termCounts[0].length) {
//				logger.info(termIdx + " " + docIdx);
//				expand(termIdx, docIdx);
//			}
			double tempCount = termCounts[termIdx][docIdx];

			if (tempCount == 0) {
				Integer docCount = null;
				if ((docCount = termDocCounts.get(term)) == null) {
					termDocCounts.put(term, 0);
				} else {

					termDocCounts.put(term, 1 + docCount);
				}

			}
			termCounts[termIdx][docIdx] += 1;
		}

	}

	private synchronized void expand(int termIdx, int docIdx) {
		if (termIdx >= termCounts.length || docIdx >= termCounts[0].length) {
			logger.info(termIdx + " " + docIdx + " Expanding array.");
			long start = System.currentTimeMillis();
			double[][] tempTermCounts = new double[termCounts.length + incrementTerms][termCounts[0].length
					+ incrementDocs];
			for (int i = 0; i < termCounts.length; i++) {
				for (int j = 0; j < termCounts[0].length; j++) {
					tempTermCounts[i][j] = termCounts[i][j];
				}
			}

			termCounts = tempTermCounts;
			logger.info("Taken (ms):" + (System.currentTimeMillis() - start));

		} else {
			logger.info("Skipping expansion.");
		}

	}

	public double getCounts(String docId, String term) {
		Integer termIdx = getTermId(term);
		Integer docIdx = getDocId(docId);
		if (termIdx == null || docIdx == null) {
			return -1;
		}

		return termCounts[termIdx][docIdx];

	}

	public Map<String, Double> getTermCountsbyId(String id) {
		Map<String, Double> data = new HashMap<>();
		Integer docIdx = getDocId(id);
		if (docIdx == null) {
			return data;
		}

		for (int i = 0; i < termCounts.length; i++) {
			data.put(getRevTermId(i), termCounts[i][docIdx]);
		}

		return data;
	}

	public int getDocCounts(String term) {
		return termDocCounts.getOrDefault(term, 0);
	}

	public Set<String> getTermsByTopic(String topic) {
		return Collections.unmodifiableSet(topicTerms.get(topic));
	}

	public Set<String> getTopics() {
		return topicTerms.keySet();
	}

	private String getRevDocId(Integer docId) {
		return revDocI.get(docId);
	}

	private String getRevTermId(Integer termId) {
		return revTermI.get(termId);
	}

	private Integer getDocId(String docKey) {
		return docI.get(docKey);
	}

	private Integer getTermId(String term) {
		return termI.get(term);
	}

	public Set<String> getTerms() {
		return Collections.unmodifiableSet(termI.keySet());
	}

	public Set<String> getDocIds() {
		return Collections.unmodifiableSet(docI.keySet());
	}

	@Override
	public String toString() {
		return Arrays.deepToString(termCounts);
	}
}
