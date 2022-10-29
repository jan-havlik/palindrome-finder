package cz.mendelu.genetika.palindrome

import cz.mendelu.genetika.genoms.Sequence

/**
 * Created by Honza on 6. 10. 2015.
 */
class CompositePalindromeDetectorTest extends GroovyTestCase {

    CompositePalindromeDetector detector = new CompositePalindromeDetector(null, null);

    public void testMerge_NoColision() {
        SimplePalindromeDetector.SimplePalindromeMatcher a = new SimplePalindromeDetector.SimplePalindromeMatcher();
        a.add(new Palindrome(new Sequence("AAAAAAA"), 10, 7, 5, 3));
        SimplePalindromeDetector.SimplePalindromeMatcher b = new SimplePalindromeDetector.SimplePalindromeMatcher();
        b.add(new Palindrome(new Sequence("CCCCCCC"), 20, 7, 5, 3));


        PalindromeMatcher merge = detector.merge(a, b);
        assert 2 == merge.count;
    }

    public void testMerge_ColisionReplace() {
        Palindrome p7_5_3 = new Palindrome(new Sequence("AAAAAAA"), 10, 7, 5, 3);
        Palindrome p8_3_3 = new Palindrome(new Sequence("CCCCCCCC"), 10, 8, 3, 3)

        SimplePalindromeDetector.SimplePalindromeMatcher a = new SimplePalindromeDetector.SimplePalindromeMatcher();
        SimplePalindromeDetector.SimplePalindromeMatcher b = new SimplePalindromeDetector.SimplePalindromeMatcher();

        a.add(p7_5_3);
        b.add(p8_3_3);

        PalindromeMatcher merge = detector.merge(a, b);

        assert 1 == merge.count;
        assert p8_3_3.equals(merge.stream().findFirst().get());
    }

    public void testMerge_ColisionState() {
        Palindrome p7_5_3 = new Palindrome(new Sequence("AAAAAAA"), 10, 7, 5, 3);
        Palindrome p8_3_3 = new Palindrome(new Sequence("CCCCCCCC"), 10, 8, 3, 3)

        SimplePalindromeDetector.SimplePalindromeMatcher a = new SimplePalindromeDetector.SimplePalindromeMatcher();
        SimplePalindromeDetector.SimplePalindromeMatcher b = new SimplePalindromeDetector.SimplePalindromeMatcher();

        a.add(p7_5_3);
        b.add(p8_3_3);

        PalindromeMatcher merge = detector.merge(b, a);

        assert 1 == merge.count;
        assert p8_3_3.equals(merge.stream().findFirst().get());
    }

    public void "test Issue #47"() {
        SortedSet size = (15..20) as SortedSet;
        SortedSet spacer = (0..10) as SortedSet;
        SortedSet mismatches = (0..5) as SortedSet;
        PalindromeDetector detector = CompositePalindromeDetector.palindromeDetectorBuilder().getPalindromeDetector(size, spacer, mismatches);

        Sequence sequence = new Sequence("AAAAAAAAAAAAAAAAAAAAGGGTTTTTTTTTTTTTTTTTTTT");
        PalindromeMatcher matcher = detector.findPalindrome(sequence);

        assert matcher.count == 1;
        assert matcher.iterator().next().sequence == new Sequence("AAAAAAAAAAAAAAAAAAAA");

    }

    public void "test ATAT rich genome problem"() {
        SortedSet size = (5..8) as SortedSet;
        SortedSet spacer = (0..3) as SortedSet;
        SortedSet mismatches = (0..1) as SortedSet;
        PalindromeDetector detector = CompositePalindromeDetector.palindromeDetectorBuilder().getPalindromeDetector(size, spacer, mismatches);

        Sequence sequence = new Sequence("GGATATATATATATATATATGG");
        PalindromeMatcher matcher = detector.findPalindrome(sequence);

        assert matcher.count == 1;
        assert matcher.iterator().next().sequence == new Sequence("ATATATATA");

    }

    public void "test NNNNNN sequence should not be found"() {
        SortedSet size = (6..30) as SortedSet;
        SortedSet spacer = (0..10) as SortedSet;
        SortedSet mismatches = (0..1) as SortedSet;
        PalindromeDetector detector = CompositePalindromeDetector.palindromeDetectorBuilder().getPalindromeDetector(size, spacer, mismatches);

        Sequence sequence = new Sequence("GGATNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNTGG");
        PalindromeMatcher matcher = detector.findPalindrome(sequence);

        assert matcher.count == 0;
    }
}
