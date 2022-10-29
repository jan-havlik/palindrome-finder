package cz.mendelu.genetika.genoms
/**
 * Created by Honza on 29. 7. 2015.
 */
class SequenceTest extends GroovyTestCase {

    static stringAsSequence() {
        String.metaClass.define {
            oldAsType = String.metaClass.getMetaMethod("asType", [Class] as Class[])
            asType = { Class c ->
                if (c == Sequence)
                    new Sequence(delegate)
                else
                    oldAsType.invoke(delegate, c)
            }
        }
    }

    static  {
        SequenceTest.stringAsSequence();
    }

    void testSequenceFromString() {
        assert ("ACTG" as Sequence).length == 4;
    }

    void testGetSequence() {
        Sequence subSeqvence = ("ACTG" as Sequence).getSequence(1,2);
        assert subSeqvence.length == 2;
        assert subSeqvence.toString() == "CT";
    }

    void testRevert() {
        Sequence revert = ("ACTG" as Sequence).revert();
        assert revert.toString() == "GTCA";
    }

    void testSupplement() {
        Sequence supplement = ("ACTG" as Sequence).supplement();
        assert supplement.toString() == "TGAC";
    }

    void testSequenceMismatches_same() {
        int mismatches = ("ACTG" as Sequence).mismatches("ACTG" as Sequence);
        assert mismatches == 0;
    }

    void testSequenceMismatches_WithDiferentMargins() {
        Integer mismatches = ("ACTG" as Sequence).mismatches("TCTG" as Sequence);
        assert mismatches == null;
    }

    void testSequenceMismatches_1() {
        int mismatches = ("ACTG" as Sequence).mismatches("ACCG" as Sequence);
        assert mismatches == 1;
    }

    void testExistsSequence() {
        assert ("ACTG" as Sequence).existsSubSequence(1,3) == true;
        assert ("ACTG" as Sequence).existsSubSequence(2,3) == false;
    }

    void testCycleSubSequence() {
        Sequence subSeqvence = ("ABCDEFGH" as Sequence).getSequence(6,4);
        assert subSeqvence.length == 4;
        assert subSeqvence.toString() == "GHAB";
    }
}
