package cz.mendelu.genetika.palindrome;

import cz.mendelu.genetika.genoms.Genome.Nuclid;
import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by xkoloma1 on 03.03.2016.
 */
public class AtatPalindromeDetector implements PalindromeDetector {

    private static final Logger LOG = LoggerFactory.getLogger(AtatPalindromeDetector.class);

    public static boolean isAtat(Sequence sequence) {
        if (sequence.getLength() == 0) {
            return false;
        }

        Iterator<Byte> i = sequence.iterator();
        byte current, expects = Nuclid.supplement(i.next());
        while (i.hasNext()) {
            current = i.next();
            if (expects != current) {
                return false;
            }
            expects = Nuclid.supplement(current);
        }
        return true;
    }


    private final int minSize, maxSize, maxSpacer;

    public AtatPalindromeDetector(SortedSet<Integer> size, SortedSet<Integer> spacers) {
        this.minSize = size.first();
        this.maxSize = size.last();
        this.maxSpacer = spacers.last() - (spacers.last() % 2);
    }

    @Override
    public PalindromeMatcher findPalindrome(Sequence sequence) {
        SimplePalindromeDetector.SimplePalindromeMatcher result = new SimplePalindromeDetector.SimplePalindromeMatcher();
        if(sequence.getLength() < minSize * 2) {
            return result;  //TODO nebo radeji vyvolat vyjimku?
        }

        Iterator<Byte> i = sequence.iterator();
        byte current, expects = Nuclid.supplement(i.next());
        int start = 0, length = 0;

        while (i.hasNext()) {
            current = i.next();
            length++;
            if (expects != current) {
                // Testuji zda sekvence atat je dostatecna na vytvoreni palindromu
                if (length >= (minSize * 2)) {
                    result.add(newPalindrome(sequence, start, length));
                }
                start += length;
                length = 0;
            }
            expects = Nuclid.supplement(current);
        }

        // Pridani palindromu na konci genomu.
        if (length >= minSize * 2) {
            result.add(newPalindrome(sequence, start, length));
        }
        return result;
    }

    private Palindrome newPalindrome(Sequence sequence, int start, int length) {
        length -= (length % 2); // Délka musí být sudé číslo!
        int size =      Math.min(length / 2, maxSize);
        int remainder = Math.max(0, length - (size * 2));
        int spacer =    Math.min(remainder, maxSpacer);
        spacer -= (spacer % 2); // Délka spaceru musí být také sudé číslo.
        return new Palindrome(sequence, start, size, spacer, 0);
    }

    @Override
    public void setCycleMode(boolean cycleMode) {
        // Ignore
    }

    @Override
    public void setAtatFilter(boolean atatFilter) {
        // Ignore
    }
}
