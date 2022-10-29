package cz.mendelu.genetika.genoms;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;


/**
 * Created by Honza on 24. 7. 2015.
 */
public class Sequence implements Iterable<Byte> {

    public static final char A = 'A';
    public static final char C = 'C';
    public static final char G = 'G';
    public static final char T = 'T';
    public static final char U = 'U';

    private byte[] byteBuffer;

    public Sequence(File file) {
        try {
            byteBuffer = IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Sequence(InputStream dataStream) {
        try {
            byteBuffer = IOUtils.toByteArray(dataStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Sequence(String data) {
        this(data.getBytes());
    }

    public Sequence(byte[] data) {
        this.byteBuffer = data;
    }

    @Override
    public String toString() {
        return new String(byteBuffer);
    }

    public int getLength() {
        return byteBuffer.length;
    }

    public boolean existsSubSequence(int position, int size) {
        return byteBuffer.length >= position + size;
    }

    /*
    public Sequence getSequence(int position, int size) {
        byte[] dst = ArrayUtils.subarray(byteBuffer, position, position + size);
        return new Sequence(dst);
    }
    */

    public Sequence getSequence(int position, int size) {
        byte[] dst = new byte[size];
        for (int i = 0; i < size; i++) {
            dst[i] = byteBuffer[(position + i) % byteBuffer.length];
        }
        return new Sequence(dst);
    }

    public Sequence revert() {
        byte[] dst = ArrayUtils.clone(byteBuffer);
        ArrayUtils.reverse(dst);
        return new Sequence(dst);
    }

    public Sequence supplement() {
        byte[] dst = new byte[byteBuffer.length];
        for (int i = 0; i < byteBuffer.length; i++) {
            dst[i] = Genome.Nuclid.supplement(byteBuffer[i]);
        }
        return new Sequence(dst);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sequence sequence = (Sequence) o;

        return Arrays.equals(byteBuffer, sequence.byteBuffer);

    }

    @Override
    public int hashCode() {
        return byteBuffer != null ? Arrays.hashCode(byteBuffer) : 0;
    }

    /**
     * Vypocet poctu chyb mezi aktualni a zadanou sekvekci
     * @param other porovnavana sekvence
     * @return kladna cislo udavajici pocet rozdilu nebo <code>null</code> pokud sekvence maji ruzne okraje.
     */
    public Integer mismatches(Sequence other) {
        if (this.getLength() != other.getLength()) {
            throw new IllegalArgumentException("The sequences don't have same length");
        }
        int length = byteBuffer.length;

        if (Genome.Nuclid.notEquals(this.byteBuffer[0], other.byteBuffer[0]) ||
            Genome.Nuclid.notEquals(this.byteBuffer[length - 1], other.byteBuffer[length - 1])) {
            // Nerovanjí se okraje, proto nelze spočítat počet mistakes.
            return null;
        }

        int result = 0;
        for (int i = 1; i < length - 1; i++) {
            // Pokud jsou na pozicich neurcite znaky, vyhodnoti se to jako chyba
            if (!isSequenceChar(this.byteBuffer[i]) || !isSequenceChar(other.byteBuffer[i])) {
                result++;
                continue;
            }

            if (Genome.Nuclid.notEquals(this.byteBuffer[i], other.byteBuffer[i])) {
                result++;
            }
        }

        return result;
    }

    private boolean isSequenceChar(int b) {
        return b == A
            || b == T
            || b == C
            || b == G
            || b == U;
    }

    public byte[] toByteArray() {
        return byteBuffer;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new ArrayIterator<>(byteBuffer);
    }

}
