package cz.mendelu.genetika.palindrome;

import cz.mendelu.genetika.genoms.Sequence;

/**
 * Created by Honza on 24. 7. 2015.
 */
public interface PalindromeDetector {

    PalindromeMatcher findPalindrome(Sequence sequence);

    void setCycleMode(boolean cycleMode);
    void setAtatFilter(boolean atatFilter);

}
