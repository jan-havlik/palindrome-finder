package cz.mendelu.genetika.genoms;

import java.io.*;
import java.util.Date;

/**
 * Created by Honza on 23. 7. 2015.
 */
public class Genom {

    public static final class Nuclid {

        public static final byte A = 'A';
        public static final byte T = 'T';
        public static final byte C = 'C';
        public static final byte G = 'G';

    }

    private File file;

    public Genom(File file) {
        this.file = file;
    }

    public String getName(){
        return file.getName();
    }

    public Date getDate() {
        return new Date(file.lastModified());
    }

    public long getLength(){
        return file.length();
    }

    public boolean exists() {
        return file.exists();
    }

    private Sequence sequence;

    public Sequence sequence() {
        if (sequence == null) {
            sequence = new Sequence(file);
        }
        return  sequence;
    }

    public OutputStream getOutputStram() throws IOException {
        return new FileOutputStream(file);
    }
}
