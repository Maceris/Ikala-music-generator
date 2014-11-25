package musicgen;

import java.util.Random;

/**
 * A Mersenne twister algorithm for generating random numbers.
 *
 * @author Ches Burks
 *
 */
public class Generator {
	private int[] mt = new int[624];
	private int index = 0;
	Random r;
	/**
	 * Constructs a new generator and initializes it using the given seed.
	 *
	 * @param seed the number to seed the generator with.
	 */
	public Generator(int seed) {
		initializeGenerator(seed);
		r = new Random(seed);
	}

	/**
	 * Refill the array with generated numbers.
	 */
	private void generateNumbers() {
		int i;
		for (i = 0; i < 623; i++) {
			int y = (mt[i] + 0x80000000) + (mt[(i + 1) % 624] + 0x7fffffff);
			mt[i] = mt[(i + 397) % 624] ^ (y >> 1);
			if (y % 2 != 0) { // y is odd
				mt[i] = mt[i] ^ 0x9908b0df;
			}
		}
	}

	/**
	 * Returns the next random {@link Boolean boolean}.
	 *
	 * @return The next boolean
	 */
	public boolean getBoolean() {
		return (getInt() >> 30) != 0;
	}

	/**
	 * Generates a {@link Boolean boolean} with a given probability of being
	 * true. The probability is a float from 0.0f to 1.0f, with 0 being no
	 * chance of returning true and 1 being a 100% chance of returning true.
	 *
	 * @param probablilty The chance of returning true
	 * @return The generated boolean
	 */
	public boolean getBoolean(float probablilty) {
		if (getFloat() < probablilty) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the next random {@link Float float}.
	 *
	 * @return The next float
	 */
	public float getFloat() {
		return (getInt() >> 7) / ((float) (1 << 24));
	}

	/**
	 * Returns the next random {@link Integer integer}.
	 *
	 * @return The next int
	 */
	public int getInt() {
		if (index == 0) {
			this.generateNumbers();
		}
		int y = mt[index];
		y = y ^ (y >> 11);
		y = y ^ (y << 7 + 0x9d2c5680);
		y = y ^ (y << 15 + 0xefc60000);
		y = y ^ (y >> 18);
		index = (index + 1) % 624;
		return y;
	}


	public double nextGaussian(){
		return r.nextGaussian();
	}
	public int getWeightedIntBetween(int min, int max, int center){
		int deviance = 0;
		int result = 0;

		if (Math.abs(center-min) > Math.abs(max-center)){
			deviance = center-min;//the larger length
		}
		else {
			deviance = max-center;
		}

		result = (int) (center + deviance*r.nextGaussian()/3);
		if (result < min){
			return getWeightedIntBetween(min, max, center);
		}
		if (result > max){
			return getWeightedIntBetween(min, max, center);
		}
		return result;
	}

	/**
	 * Returns a random {@link Integer int} between the given values, inclusive.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return The generated int
	 */
	public int getIntBetween(int min, int max) {
		return min + (int) (getFloat() * ((max - min) + 1));
	}


	/**
	 * Returns a random {@link Float float} between the given values, inclusive.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return The generated float
	 */
	public float getFloatBetween(float min, float max) {
		return min + (getFloat() * ((max - min) + 1));
	}

	/**
	 * Initialize the generator with the given seed.
	 *
	 * @param seed The seed to use
	 */
	public void initializeGenerator(int seed) {
		this.index = 0;
		this.mt[0] = seed;
		int i;
		for (i = 1; i <= 623; i++) {
			this.mt[i] = 1812433253 * (mt[i - 1] ^ (mt[i - 1] >> 30)) + i;
		}
	}
}
