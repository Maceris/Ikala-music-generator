
package musicgen;

import java.util.HashMap;

import org.jfugue.Pattern;

/**
 * Used to generate music.
 *
 * @author Ches Burks
 *
 */
public class MusicGen {

	/**
	 * Constructs a new music generator and initializes some variables.
	 */
	public MusicGen() {
		durationValues.put(1.0f, 0);
		durationValues.put(1.0f / 2, 1);
		durationValues.put(1.0f / 4, 2);
		durationValues.put(1.0f / 8, 3);
		durationValues.put(1.0f / 16, 4);
		durationValues.put(1.0f / 32, 5);
	}

	private final String[] notes = {"C", "D", "E", "F", "G", "A", "B"};
	private final String rest = "R";
	private final String flat = "b";
	private final String sharp = "#";
	private final int minOctave = 0;
	private final int maxOctave = 9;
	private final String[] durations = {"w", "h", "q", "i", "s", "t"};
	private final HashMap<Float, Integer> durationValues =
			new HashMap<Float, Integer>();

	// settings
	/**
	 * the chance it will be a rest instead of a note
	 */
	private float restChance = .142f;
	/**
	 * how much the octave leans towards the center when deciding shifts
	 */
	private float octaveShiftVariance = 0.605f;
	/**
	 * the chance a note will not change
	 */
	private float noteChangeChanceSame = 0.558f;
	/**
	 * how many beats per measure
	 */
	private float timeTop = 4;
	/**
	 * how long a beat is
	 */
	private float timeBottom = 1.0f / 4;
	/**
	 * The chance that a beat will be split into sub-beats
	 */
	private float smallerBeatChance = 0.38f;
	/**
	 * Each step down creating smaller beats the chance to split again is
	 * reduced by this amount.
	 */
	private int smallBeatReductionFactor = 15;

	// for calculating
	private int currentOctave = 5;
	private int currentNote = 1;

	/**
	 * Increases the current note if possible by a random amount. This will not
	 * increase the current note above the maximum note for the octave.
	 */
	private void increaseNote() {
		int delta = RNG.getIntBetween(1, 3);
		if (currentNote + delta < notes.length - 1) {
			currentNote += delta;
		}
		else if (currentNote < notes.length - 1) {
			currentNote += 1;
		}

	}

	/**
	 * Decreases teh current note if possible by a random amount. This will not
	 * decrease the current note below the minimum note for the octave.
	 */
	private void decreaseNote() {
		int delta = RNG.getIntBetween(1, 3);
		if (currentNote - delta > 0) {
			currentNote -= delta;
		}
		else if (currentNote > 0) {
			currentNote -= 1;
		}

	}

	/**
	 * Returns a string representing notes that, summed up, have a duration
	 * equal to the supplied duration.
	 *
	 * @param duration the duration to generate beats for
	 * @param splitChance the chance of splitting into sub-beats
	 * @return the generated string
	 */
	private String getBeat(float duration, float splitChance) {
		if (RNG.getBoolean(splitChance)) {
			if (duration / 2 >= 1.0f / 32) {
				String toReturn = "";
				toReturn +=
						getBeat(duration / 2, splitChance
								/ smallBeatReductionFactor);
				toReturn += " ";
				toReturn +=
						getBeat(duration / 2, splitChance
								/ smallBeatReductionFactor);
				return toReturn;
			}
		}
		String note = "";
		if (RNG.getBoolean(restChance)) {
			// Rest
			note += rest;
		}
		else {
			// Note
			if (!RNG.getBoolean(noteChangeChanceSame)) {
				if (RNG.getBoolean()) {
					increaseNote();
				}
				else {
					decreaseNote();
				}
			}// TODO clean up the note generation code
				// TODO perhaps add different keys
			if (currentNote == 1) {
				currentNote = 0;
			}
			else if (currentNote == 3) {
				currentNote = 2;// TODO replace with the musical key values not
								// hard coded note values
			}
			else if (currentNote >= 6) {
				currentNote = 5;
			}
			note += notes[currentNote];
			// make it sharp or flat
			/*
			 * if (RNG.getBoolean(sharpModChance)) { if
			 * (RNG.getBoolean(sharpChance)) { note += sharp; } else { note +=
			 * flat; } }
			 */
			// add the octive number
			note += currentOctave;
		}
		// Length
		note += durations[durationValues.get(duration)];
		return note;
	}

	/**
	 * Changes the octave randomly, but always leans towards the middle, so the
	 * octave will tend to stay around the center octave depending on how
	 * strongly it is pulled to the middle.
	 */
	private void shiftOctave() {
		int halfWayOctive = (minOctave + maxOctave) / 2;
		if (currentOctave < halfWayOctive) {
			// want to shift up more
			if (RNG.getBoolean(0.5f + octaveShiftVariance)) {
				++currentOctave;
			}
			else {
				--currentOctave;
			}
		}
		else {
			// want to shift down more
			if (RNG.getBoolean(0.5f + octaveShiftVariance)) {
				--currentOctave;
			}
			else {
				++currentOctave;
			}
		}

		// bounds check
		if (currentOctave < minOctave) {
			currentOctave = minOctave;
		}
		if (currentOctave > maxOctave) {
			currentOctave = maxOctave;
		}
	}

	/**
	 * Builds a measure of piano music and returns the string that represents
	 * the notes.
	 *
	 * @return a string of notes to play
	 */
	private String buildPianoMeasure() {
		String measure = "";
		int i;

		float baseValue = 1.0f;
		if (timeTop < baseValue) {
			baseValue = timeTop;
		}
		// invalid value
		int numElements = -1;

		while ((numElements & (numElements - 1)) != 0
				|| !durationValues.containsKey(timeBottom / numElements)) {
			// not a power of 2
			numElements =
					RNG.getWeightedIntBetween(1,
							(int) (baseValue / (1.0f / 32)), 1);
		}
		for (i = 0; i < numElements; ++i) {
			if (timeTop / numElements > 1) {
				measure += getBeat(1.0f, smallerBeatChance);
			}
			else {
				measure += getBeat(timeTop / numElements, smallerBeatChance);
			}

			measure += " ";
		}

		if (RNG.getBoolean(0.4f)) {
			shiftOctave();
		}
		return measure;
	}

	// TODO add a drum beat gene
	/**
	 * Builds a song with the given number of measures.
	 *
	 * @param measures how many measures to generate
	 * @return the newly generated song
	 */
	private Pattern buildSong(int measures) {
		String tmp = "";
		Pattern song = new Pattern();
		song.add(new Pattern("V0 I0"));// piano on voice 1
		for (int i = 0; i < measures; ++i) {
			tmp =
					"V0 "
							+ buildPianoMeasure()
							+ "V9 L0 [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]s [TAMBOURINE]s [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i ";
			tmp +=
					"V9 L1 [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i ";
			tmp += "V9 L2 Rq Rq [ELECTRIC_SNARE]q Rq ";
			tmp += "V9 L3 [BASS_DRUM]q Ri [BASS_DRUM]i Rq [BASS_DRUM]q ";
			System.out.print(tmp);
			song.add(new Pattern(tmp));
		}
		System.out.println();
		return song;
	}

	// play notes 1 3 5 6 of a major scale

	/**
	 * Creates a new song using information from a genome.
	 *
	 * @param genome the genes to use in generation of the song
	 * @return the newly created song
	 */
	public Pattern getSong(Genome genome) {
		int key = genome.getGene(0).getValue();// TODO change the key
		int length = genome.getGene(1).getValue();
		for (float f : durationValues.keySet()) {
			if (durationValues.get(f) == length) {
				timeBottom = f;
				timeTop = timeBottom * 4;
			}
		}
		int split = genome.getGene(2).getValue();
		switch (split) {
		case 0:
			smallerBeatChance = 0.0f;
			smallBeatReductionFactor = 999999;
			break;
		case 1:
			smallerBeatChance = 0.05f;
			smallBeatReductionFactor = 6;
			break;
		case 2:
			smallerBeatChance = 0.25f;
			smallBeatReductionFactor = 64;
			break;
		case 3:
			smallerBeatChance = 0.25f;
			smallBeatReductionFactor = 64;
			break;
		case 4:
			smallerBeatChance = 0.5f;
			smallBeatReductionFactor = 2;
			break;
		case 5:
			smallerBeatChance = 0.75f;
			smallBeatReductionFactor = 64;
			break;

		case 6:
			smallerBeatChance = 0.75f;
			smallBeatReductionFactor = 64;
			break;
		case 7:
			smallerBeatChance = 0.9f;
			smallBeatReductionFactor = 6;
			break;
		case 8:
			smallerBeatChance = 1.0f;
			smallBeatReductionFactor = 1;
			break;
		}

		Pattern p = buildSong(10);
		return p;
	}
}
