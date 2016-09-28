package rd.ml.support.probability;

import java.util.Random;

public class Binomial {
	private final Random rnd;

	public Binomial(long seed) {

		rnd = new Random(seed);
	}

	public boolean get(float probability) {
		return rnd.nextFloat() < probability ? true : false;
	}

	public double getProbability(int n, int k, double optionAProb) {
		// n!/k!(n-k)!
		double nDivKFact = 1;
		double nSubKFact = 1;
		if (k > 0) {

			for (int i = (n-k) + 1; i <= n; i++) {
				nDivKFact *= i;
			}

			for (int i = 1; i <= k; i++) {
			
				nSubKFact *= i;
			}
		}

		return (double)( (nDivKFact / nSubKFact) * (Math.pow(optionAProb, k) * Math.pow((1 - optionAProb), (n - k))));

	}
}
