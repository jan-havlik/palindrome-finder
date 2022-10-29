package cz.mendelu.genetika.palindrome

import cz.mendelu.genetika.genoms.Sequence
import cz.mendelu.genetika.genoms.SequenceTest
import cz.mendelu.genetika.palindrome.AtatPalindromeDetector

/**
 * Created by xkoloma1 on 03.03.2016.
 */
class AtatPalindromeDetectorTest extends groovy.util.GroovyTestCase {

    static {
        SequenceTest.stringAsSequence();
    }

    void "test IsAtat(): Valid"() {
        assert AtatPalindromeDetector.isAtat("ATATAT" as Sequence) == true;
        assert AtatPalindromeDetector.isAtat("TATATA" as Sequence) == true;
        assert AtatPalindromeDetector.isAtat("CGCGCG" as Sequence) == true;
        assert AtatPalindromeDetector.isAtat("GCGCGC" as Sequence) == true;
    }

    void "test IsAtat(): Invalid"() {
        assert AtatPalindromeDetector.isAtat("ATATTAT" as Sequence) == false;
        assert AtatPalindromeDetector.isAtat("ATATCAT" as Sequence) == false;
    }

    void "test findPalindrome in longer sequence"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((4..6) as SortedSet, (2..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATCC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATAT" as Sequence;
        assert palindrome.opposite == "ATATAT" as Sequence;
        assert palindrome.spacer == "AT" as Sequence;
        assert palindrome.position == 2;
    }

    void "test findPalindrome in longer sequence 2"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((4..6) as SortedSet, (2..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATACC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATAT" as Sequence;
        assert palindrome.opposite == "ATATAT" as Sequence;
        assert palindrome.spacer == "AT" as Sequence;
        assert palindrome.position == 2;
    }

    void "test findPalindrome in short sequence"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((5..15) as SortedSet, (2..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATATCC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATATATAT" as Sequence;
        assert palindrome.opposite == "ATATATATAT" as Sequence;
        assert palindrome.spacer.length == 0
        assert palindrome.position == 2;
    }

    void "test findPalindrome in short sequence 2"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((5..15) as SortedSet, (2..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATATACC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATATATAT" as Sequence;
        assert palindrome.opposite == "ATATATATAT" as Sequence;
        assert palindrome.spacer.length == 0
        assert palindrome.position == 2;
    }

    void "test findPalindrome in middle sequence"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((5..7) as SortedSet, (3..7) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATCC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATATA" as Sequence;
        assert palindrome.opposite == "TATATAT" as Sequence;
        assert palindrome.spacer == "TATA" as Sequence;
        assert palindrome.position == 2;
    }

    void "test findPalindrome in middle sequence 2"() {
        AtatPalindromeDetector detector = new AtatPalindromeDetector((5..7) as SortedSet, (3..7) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCATATATATATATATATATACC" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ATATATA" as Sequence;
        assert palindrome.opposite == "TATATAT" as Sequence;
        assert palindrome.spacer == "TATA" as Sequence;
        assert palindrome.position == 2;
    }
}
