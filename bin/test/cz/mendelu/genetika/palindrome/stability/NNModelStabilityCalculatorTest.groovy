package cz.mendelu.genetika.palindrome.stability

import cz.mendelu.genetika.genoms.Sequence
import cz.mendelu.genetika.genoms.SequenceTest
import cz.mendelu.genetika.palindrome.Palindrome

/**
 * Created by Honza on 20.11.2015.
 */
class NNModelStabilityCalculatorTest extends GroovyTestCase {

    static {
        SequenceTest.stringAsSequence();
    }

    NNModelStabilityCalculator calculator = new NNModelStabilityCalculator();

    void testCalculate() {
        Palindrome palindrome = new Palindrome("CGTTGATCAACG" as Sequence, 0, 6, 0, 0);
        double value = calculator.calculateCruciForm(palindrome);
        assert value == -5.35;
    }

    // TODO otestovat na mfoold;
}
