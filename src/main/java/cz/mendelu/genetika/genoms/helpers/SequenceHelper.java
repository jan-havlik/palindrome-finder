package cz.mendelu.genetika.genoms.helpers;

import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.features.SubSequence;

/**
 * Created by Jiří Lýsek on 28.11.2016.
 */
public class SequenceHelper {

    /**
     * spocita, kolik je v sekvenci urcite hodnoty A,C,G,T nebo U
     *
     * @param c
     * @return
     */
    public static int count(Sequence s, char c) {
        int ret = 0;
        byte[] byteBuffer = s.toByteArray();
        int length = byteBuffer.length;
        for (int i = 1; i < length - 1; i++) {
            if(byteBuffer[i] == c) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * zjisti, jestli je sekvence b v urcitem okoli sekvence a
     *
     * @param a
     * @param b
     * @param range rozsah na obe strany od sekvence a, ve kterem se musi sekvence b nachazet
     * @return
     */
    public static boolean isInRange(SubSequence a, SubSequence b, int range) {
        int positionA = a.getPosition();
        int positionB = b.getPosition();
        return positionA - range <= positionB && positionB + b.getLength() <= positionA + a.getLength() + range;
    }

    /**
     *
     * @param a
     * @param b
     * @param range rozsah na obe strany od sekvence a, ve kterem se musi sekvence b nachazet
     * @param offset odsazeni zacatku sekvence b od zacatku sekvence a
     * @return
     */
    public static boolean isInRange(SubSequence a, SubSequence b, int range, int offset) {
        int positionA = a.getPosition();
        int positionB = b.getPosition();
        return positionA - range <= positionB + offset && positionB + b.getLength() + offset <= positionA + a.getLength() + range;
    }

}
