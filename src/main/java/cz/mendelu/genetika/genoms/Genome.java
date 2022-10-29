package cz.mendelu.genetika.genoms;

import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import cz.mendelu.genetika.dao.jasdb.JasGenomeDao;

import java.io.*;
import java.util.Date;

/**
 * Created by Honza on 23. 7. 2015.
 */
@JasDBEntity(bagName = JasGenomeDao.BAG_NAME)
public class Genome {

    public static final class Nuclid {

        public static final byte A = 'A';
        public static final byte T = 'T';
        public static final byte C = 'C';
        public static final byte G = 'G';
        public static final byte U = 'U';


        public static final byte supplement(byte b) {
            switch (b) {
                case A: return T;
                case T: return A;
                case C: return G;
                case G: return C;
                case U: return A;
                default: return 0;
            }
        }

        public static boolean equals(byte a, byte b) {
            if (a == b) {
                return true;
            }
            if (a == T && b == U) {
                return true;
            }
            if (a == U && b == T) {
                return true;
            }
            return false;
        }

        public static boolean notEquals(byte a, byte b) {
            return !equals(a, b);
        }

    }



    private String id;
    private String name;
    private String info = "";
    private long date;
    private long length;

    private File file;
    private Sequence sequence;

    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JasDBProperty
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JasDBProperty
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }

    @JasDBProperty
    public long getLength(){
        return length;
    }
    public void setLength(long length) {
        this.length = length;
    }

    @JasDBProperty()
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }

    public Sequence sequence() {
        if (sequence == null) {
            sequence = new Sequence(file);
        }
        return  sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }
}
