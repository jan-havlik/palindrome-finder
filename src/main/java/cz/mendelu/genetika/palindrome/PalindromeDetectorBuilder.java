package cz.mendelu.genetika.palindrome;

import java.util.SortedSet;

/**
 * Created by xkoloma1 on 06.10.2015.
 */
public interface PalindromeDetectorBuilder {

    PalindromeDetector getPalindromeDetector(SortedSet<Integer> size, SortedSet<Integer> spacers, SortedSet<Integer> mismatches);


}
