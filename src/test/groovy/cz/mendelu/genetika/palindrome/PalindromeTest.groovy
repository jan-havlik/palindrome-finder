package cz.mendelu.genetika.palindrome

import static cz.mendelu.genetika.genoms.Genome.Nuclid.*

import cz.mendelu.genetika.genoms.Sequence
import cz.mendelu.genetika.genoms.SequenceTest

/**
 * Created by xkoloma1 on 21.08.2015.
 */
class PalindromeTest extends GroovyTestCase {

    static {
        SequenceTest.stringAsSequence();
    }

    void testConstructor() {
        Palindrome palindrome = new Palindrome("ABCDEFGHIJKLMNOPQRSTUVWXZ" as Sequence, 8, 4, 2, 0);
        assert palindrome.before == "DEFGH" as Sequence;
        assert palindrome.sequence == "IJKL" as Sequence;
        assert palindrome.spacer == "MN" as Sequence;
        assert palindrome.opposite == "OPQR" as Sequence;
        assert palindrome.after == "STUVW" as Sequence;

    }

    void testIterator() {
        Palindrome palindrome = new Palindrome("ACGCGCCCTGTAGCGGCGCAT" as Sequence, 0, 7, 7, 1);
        Iterator<Palindrome.Entry> i = palindrome.iterator();
        assert i.next() == new Palindrome.Entry(A, T);
        assert i.next() == new Palindrome.Entry(C, A);
        assert i.next() == new Palindrome.Entry(G, C);
        assert i.next() == new Palindrome.Entry(C, G);
        assert i.next() == new Palindrome.Entry(G, C);
        assert i.next() == new Palindrome.Entry(C, G);
        assert i.next() == new Palindrome.Entry(C, G);
        assert i.next() == new Palindrome.Entry(C);
        assert i.next() == new Palindrome.Entry(T);
        assert i.next() == new Palindrome.Entry(G);
        assert i.next() == new Palindrome.Entry(T);
        assert i.next() == new Palindrome.Entry(A);
        assert i.next() == new Palindrome.Entry(G);
        assert i.next() == new Palindrome.Entry(C);
        assert i.hasNext() == false;
    }
}
