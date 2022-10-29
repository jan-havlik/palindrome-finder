package cz.mendelu.genetika.palindrome;


import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.features.SubSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Honza on 24. 7. 2015.
 */
public class Palindrome implements Iterable<Palindrome.Entry>, SubSequence {

    public static final int SIZE_MARGIN = 5;

    private Sequence genom;

    private int position;
    private int size;
    private int spacer;
    private int mismatches;

    public Palindrome(Sequence genom, int positions, int size, int spacer, int mismatches) {
        this.genom = genom;
        this.position = positions;
        this.size = size;
        this.spacer = spacer;
        this.mismatches = mismatches;
    }

    public Sequence getBefore() {
        int beforePosition = (position < SIZE_MARGIN) ? 0 : position - SIZE_MARGIN;
        int beforeSize = (position < SIZE_MARGIN) ? position : SIZE_MARGIN;
        return genom.getSequence(beforePosition, beforeSize);
    }

    public Sequence getSequence() {
        return genom.getSequence(position, size);
    }

    public Sequence getSpacer() {
        return genom.getSequence(position + size, spacer);
    }

    public Sequence getOpposite() {
        return genom.getSequence(position + size + spacer, size);
    }

    public Sequence getAfter() {
        int afterPosition = position + size + spacer + size;
        int afterSize = (afterPosition > genom.getLength()) // Otestuji, zda pozice konce nalezeneho palindromu nekonci na zacatku genomu, jinymy slovy zda se nejedna o cyklu
                ? SIZE_MARGIN // Jde o cyklicky genom -> plna delka
                : (genom.getLength() > afterPosition + SIZE_MARGIN) // Nejde, pak vyberu zbytek do konce
                        ? SIZE_MARGIN // Tady nezasahu je do konce
                        : genom.getLength() - afterPosition; // Tady zasahuje a beru jen cast
        return genom.getSequence(afterPosition, afterSize);
    }

    public float getMidlePosition() {
        return position + size + (spacer / 2f);
    }

    @Override
    public Iterator<Entry> iterator() {
        List<Entry> entries = new ArrayList<>();
        Iterator<Byte> seq = getSequence().iterator();
        Iterator<Byte> ops =  getOpposite().revert().iterator();
        // Nejprve vytvorim dvojice z sekvence a jejiho protejsku
        while (seq.hasNext() && ops.hasNext()) {
            entries.add(new Entry(seq.next(), ops.next()));
        }
        // Pote pridam spacer, ale bez protejsku
        getSpacer().forEach(nuclid -> entries.add(new Entry(nuclid)));

        // Z vytvorene sekvence vradim iterator
        // + obalim zamcenym listem, abych zabranil odsranovani polozek
        return Collections.unmodifiableList(entries).iterator();
    }

    public Iterator<Entry> linearFormIterator() {
        List<Entry> entries = new ArrayList<>();
        getSequence().forEach(b -> entries.add(new Entry(b, Genome.Nuclid.supplement(b))));
        getSpacer().forEach(b -> entries.add(new Entry(b, Genome.Nuclid.supplement(b))));
        getOpposite().forEach(b -> entries.add(new Entry(b, Genome.Nuclid.supplement(b))));
        // Z vytvorene sekvence vradim iterator
        // + obalim zamcenym listem, abych zabranil odsranovani polozek
        return Collections.unmodifiableList(entries).iterator();
    }

    public long getMismatches() {
        return mismatches;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int getDirection() {
        return 0;
    }

    public int getSize() {
        return size;
    }

    public int getLength() {
        return 2 * size + spacer;
    }

    public static class Entry {

        public final byte nuclid;

        public final Byte oposite;

        public Entry(byte nuclid, byte oposite) {
            this.nuclid = nuclid;
            this.oposite = oposite;
        }

        public Entry(byte nuclid) {
            this.nuclid = nuclid;
            this.oposite = null;
        }

        public String getNuclid() {
            return Character.toString((char)nuclid);
        }

        public String getOposite() {
            return (oposite == null)
                    ? ""
                    : Character.toString((char)(byte)oposite);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry entry = (Entry) o;

            if (nuclid != entry.nuclid) return false;
            return !(oposite != null ? !oposite.equals(entry.oposite) : entry.oposite != null);

        }

        @Override
        public int hashCode() {
            int result = (int) nuclid;
            result = 31 * result + (oposite != null ? oposite.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            char f = (char)nuclid;

            return f + "-" + ((oposite != null) ? (char)(byte)oposite : "");
        }
    }
}
