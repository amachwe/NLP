package rd.ml.nlp.service;

import java.util.Set;

import rd.ml.nlp.data.WordMatrix;

public interface TermFrequencyService {

	public WordMatrix process(Set<String> documents);
}
