package cz.mendelu.genetika.palindrome;

import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import cz.mendelu.genetika.dao.jasdb.JasAnalysePalindromeDao;

/**
 * Created by xkoloma1 on 13.01.2016.
 */
@JasDBEntity(bagName = JasAnalysePalindromeDao.BAG_NAME)
public class AnalysePalindrome {

    public enum Status {
        WAITS, PROCESSING, DONE;
    }

    private String id;

    private Status status = Status.WAITS;   //stav analyzy

    private String name;    //nazev chromozomu
    private String genomeId;    //id chromozomu
    private long length;    //delka chromozomu
    private long count;  //pocet nalezenych
    private long date;  //datum analyzy

    //parametry analyzy
    private String spacer;
    private String mismatches;
    private String size;
    private String cycle;

    private String statusString;

    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JasDBProperty(name = "statusString")
    public String getStatusString() { String s = status.toString(); return s; }
    public void setStatusString(String status) { this.status = Status.valueOf(status); }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @JasDBProperty
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JasDBProperty
    public long getLength() { return length; }
    public void setLength(long length) { this.length = length; }

    @JasDBProperty
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }

    @JasDBProperty
    public String getGenomeId() { return genomeId; }
    public void setGenomeId(String chromosomeId) { this.genomeId = chromosomeId; }

    @JasDBProperty
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    @JasDBProperty
    public String getCycle() { return cycle == null ? "false" : cycle; }
    public void setCycle(String cycle) { this.cycle = cycle;    }

    @JasDBProperty
    public String getSpacer() { return spacer; }
    public void setSpacer(String spacer) { this.spacer = spacer; }

    @JasDBProperty
    public String getMismatches() { return mismatches; }
    public void setMismatches(String mismatches) { this.mismatches = mismatches; }

    @JasDBProperty
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
