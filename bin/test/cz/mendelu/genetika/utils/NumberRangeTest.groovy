package cz.mendelu.genetika.utils

/**
 * Created by xkoloma1 on 28. 7. 2015.
 */
class NumberRangeTest extends GroovyTestCase {

    void testGetValues_SingleValue() {
        NumberRange range = new NumberRange("1");
        assert range.values == new TreeSet<>([1]);
    }

    void testGetValues_MultipleValues() {
        NumberRange range = new NumberRange("1,2");
        assert range.values == new TreeSet<>([1, 2]);
    }

    void testGetValues_SingleRange() {
        NumberRange range = new NumberRange("1-3");
        assert range.values == new TreeSet<>([1, 2, 3]);
    }

    void testGetValues_MultipleRanges() {
        NumberRange range = new NumberRange("1-3, 5-7");
        assert range.values == new TreeSet<>([1, 2, 3, 5, 6, 7]);
    }

    void testGetValues_FullMix() {
        NumberRange range = new NumberRange("1-3, 5");
        assert range.values == new TreeSet<>([1, 2, 3, 5]);
    }
}
