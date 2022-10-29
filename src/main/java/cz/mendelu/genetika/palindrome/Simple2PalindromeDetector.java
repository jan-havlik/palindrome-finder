package cz.mendelu.genetika.palindrome;

import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by xkoloma1 on 27. 7. 2015.
 */
public class Simple2PalindromeDetector implements PalindromeDetector {

    private static final Logger LOG = LoggerFactory.getLogger(Simple2PalindromeDetector.class);

    private final int size;

    private final SortedSet<Integer> spacers;

    private final SortedSet<Integer> mismatches;

    private boolean cycleMode = false;

    private boolean atatFilter = true;

    public Simple2PalindromeDetector(int size, SortedSet<Integer> spacers, SortedSet<Integer> mismatches) {
        LOG.debug("Create: size = {}, spacer = {}, mismatches = {}", size, spacers, mismatches);
        this.size = size;
        this.spacers = spacers;
        this.mismatches = mismatches;
    }

    public void setCycleMode(boolean cycleMode) {
        this.cycleMode = cycleMode;
    }

    public void setAtatFilter(boolean atatFilter) {
        this.atatFilter = atatFilter;
    }

    @Override
    public PalindromeMatcher findPalindrome(Sequence sequence) {
        SimplePalindromeDetector.SimplePalindromeMatcher palindromeMatcher = new SimplePalindromeDetector.SimplePalindromeMatcher();
        Integer nextSequencePosition, mismatche;
        Sequence testedSequence, oppositSequence, nextSequence;
        int searchToPosition = sequence.getLength() - ((cycleMode) ? 0 : (2 * size) - 1); /* Pokud je povoleno cyklicke
            hledání, je sekvence prohledana az nakonec, jinak se prohledani zastavi pred tak, aby nebyli nalezeny
            palindromy mimo delku sekvence. */

        for (int i = 0; i < searchToPosition; i++) { // Cyklu prochazim cely genom
            testedSequence = getSubSeqvence(sequence, i);

            // pokud se maji filtrovat atat sekvence, jsou atomaticky vyrazeny z vysledku.
            if (atatFilter && AtatPalindromeDetector.isAtat(testedSequence)) continue;

            oppositSequence = testedSequence.revert().supplement();

            for (int spacer : spacers) { // Postupne natahuji mezeru a porovnavam shodu
                nextSequencePosition = i + size + spacer;
                if (isSubSeqvenceToComapere(sequence, nextSequencePosition)) {
                    nextSequence = getSubSeqvence(sequence, nextSequencePosition);

                    // pokud je další sekvence, jsou atomaticky vyrazeny z vysledku.
                    if (atatFilter && AtatPalindromeDetector.isAtat(nextSequence)) continue;
                    mismatche = oppositSequence.mismatches(nextSequence);
                    if (mismatche != null && mismatches.contains(mismatche)) { // Pokud jsem nalezl odpovidaji sekvenci pridam ji do vysledku
                        palindromeMatcher.add(new Palindrome(sequence, i, size, spacer, mismatche));
                        break; // Ukonceni cyklu zvytsujiho mezeru
                    }
                }
            }
        }
        LOG.debug("AnalysePalindrome for size {} done, found {}", size, palindromeMatcher.getCount());
        return palindromeMatcher;
    }

    private boolean isSubSeqvenceToComapere(Sequence sequence, int nextSequencePosition) {
        return (cycleMode)
                ? true
                : sequence.existsSubSequence(nextSequencePosition, size);
    }

    private Sequence getSubSeqvence(Sequence sequence, int nextSequencePosition) {
        return sequence.getSequence(nextSequencePosition, size);
    }

}
