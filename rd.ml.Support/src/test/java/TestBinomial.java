import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rd.ml.support.probability.Binomial;

public class TestBinomial {

	@Test
	public void doTest() {
		Binomial b = new Binomial(1234);
		assertTrue(0.24609375 == b.getProbability(10, 5, 0.5f));
		assertTrue(0.5 == b.getProbability(1, 1, 0.5f));
		assertTrue(0.03125 == b.getProbability(5, 5, 0.5f));
		assertTrue(0.375 == b.getProbability(3, 2, 0.5f));
	}

	@Test
	public void doGenerate() {
		Binomial b = new Binomial(1234);

		int N = 50;
		for (float p = 0.0f; p <= 1.0; p += 0.1) {
			for (int i = 0; i <= N; i++) {
				System.out.println(i + "," + p + "," + b.getProbability(N, i, p));
			}
		}
	}
}
