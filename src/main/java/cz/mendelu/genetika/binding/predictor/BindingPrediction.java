package cz.mendelu.genetika.binding.predictor;

import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.features.SubSequence;

/**
 * misto, na ktere se muze navazat protein (napr. p53)
 */
public class BindingPrediction implements SubSequence {

	public static final int DISTANCE_CLOSE = 20;
	public static final int DISTANCE_FAR = 100;

	private Sequence sequence;
	
	private int position;
    private int size;
	private double delta;

	/**
	 *
	 * @param sequence cela sekvence, kde se nachazi misto
	 * @param position pozice v sekvenci
	 * @param size velikost mista
	 * @param delta energie
	 */
    public BindingPrediction(Sequence sequence, int position, int size, double delta) {
    	this.sequence = sequence;
    	this.position = position;
		this.delta = delta;
    	this.size = size;
    }

	public int getPosition() {
		return position;
	}

	@Override
	public int getDirection() {
		return 1;
	}

	public int getSize() {
		return size;
	}

	/**
	 * vraci jen sekvenci zajmoveho mista
	 *
	 * @return
	 */
	public Sequence getSequence() {
		return sequence.getSequence(position, size);
	}

	public double getDelta() {
		return delta;
	}

	/**
	 * vraci celou sekvenci, ve ktere se zajmove misto nachazi
	 *
	 * @return
	 */
	public Sequence getOriginalSequence() {
		return sequence;
	}

	public int getLength() {
		return getSize();
	}
/*
	private String printPalindrome(Palindrome p) {
		int offset = p.getPosition();
		String s = StringUtils.repeat(" ", offset);
		s += p.getSequence().toString() + p.getSpacer().toString().toLowerCase() + p.getOpposite().toString();
		s += "\r\n";
		return s;
	}

	public String toString() {
		String s = sequence.getSequence(position - DISTANCE_FAR, size + 2 * DISTANCE_FAR).toString() + "\r\n";
		s += StringUtils.repeat(" ", DISTANCE_FAR) + sequence.getSequence(position, size) + "\r\n";
		List<Palindrome> all = new ArrayList<>();
		all.addAll(inPlacePalindromes);
		all.addAll(closePalindromes);
		all.addAll(farPalindromes);
		for(Palindrome p : all) {
			s += printPalindrome(p);
		}
		return s;
	}
*/

	/**
	 * vraci sekvenci, ve ktere se budou hledat napr. palindromy
	 *
	 * @return
	 */
	public Sequence getSearchSequence() {
		int from = position - DISTANCE_FAR;
		int len = DISTANCE_FAR * 2 + size;
		if(from < 0) {
			len = len + from;
			from = 0;
		}
		if(from + len > sequence.getLength() - 1) {
			len = sequence.getLength() - from;
		}
		return sequence.getSequence(from, len);
	}

	/**
	 * vraci rozdil mezi pocatkem cele sekvence a sekvence pro hledani v okoli
	 * @return
	 */
	public int getSearchSequenceOffset() {
		int from = position - DISTANCE_FAR;
		if(from < 0) {
			return 0;
		}
		return from;
	}

}
