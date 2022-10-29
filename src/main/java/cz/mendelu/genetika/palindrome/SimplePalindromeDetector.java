package cz.mendelu.genetika.palindrome;

import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by xkoloma1 on 27. 7. 2015.
 */
public class SimplePalindromeDetector implements PalindromeDetector {

    private static final Logger LOG = LoggerFactory.getLogger(SimplePalindromeDetector.class);

    private final int size;

    private final int spacer;

    private final int mismatches;

    public SimplePalindromeDetector(int size, int spacer, int mismatches) {
        LOG.debug("Create: size = {}, spacer = {}, mismatches = {}", size, spacer, mismatches);
        this.size = size;
        this.spacer = spacer;
        this.mismatches = mismatches;
    }

    @Override
    public PalindromeMatcher findPalindrome(Sequence sequence) {
        SimplePalindromeMatcher palindromeMatcher = new SimplePalindromeMatcher();
        Integer mismatche;
        Sequence testedSequence, oppositSequence, nextSequence;
        if(sequence.getLength() >= (size + spacer + size)) {
            for (int i = 0; i < sequence.getLength() - (size + spacer + size - 1); i++) {
                testedSequence = sequence.getSequence(i, size);
                oppositSequence = testedSequence.revert().supplement();
                nextSequence = sequence.getSequence(i + size + spacer, size);
                mismatche = oppositSequence.mismatches(nextSequence);
                if (mismatche != null && mismatche == mismatches) {
                    palindromeMatcher.add(new Palindrome(sequence, i, size, spacer, mismatches));
                }
            }
        }
        LOG.info("AnalysePalindrome done, found: {}", palindromeMatcher.getCount());
        return palindromeMatcher;
    }

    @Override
    public void setCycleMode(boolean cycleMode) {
        if (cycleMode) LOG.warn("Not supported operation \"Cycle detection\" was set");
    }

    @Override
    public void setAtatFilter(boolean atatFilter) {
        if (atatFilter) LOG.warn("Not supported operation \"Cycle detection\" was set");
    }

    public static class SimplePalindromeMatcher implements PalindromeMatcher {

        private List<Palindrome> palindromes = new LinkedList<>();

        void add(Palindrome palindrome) {
            palindromes.add(palindrome);
        }

        @Override
        public int getCount() {
            return palindromes.size();
        }

        @Override
        public Stream<Palindrome> stream() {
            return palindromes.stream();
        }

        @Override
        public Iterator<Palindrome> iterator() {
            return palindromes.iterator();
        }
    }
}
