package cz.mendelu.genetika.palindrome;

import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import cz.mendelu.genetika.dao.jasdb.JasStoredPalindromeDao;
import cz.mendelu.genetika.genoms.Genome;

/**
 * trida pro ulozeni palindromu po analyze do databaze
 *
 * Created by Jiří Lýsek on 8.2.2016.
 */
@JasDBEntity(bagName = JasStoredPalindromeDao.BAG_NAME)
public class StoredPalindrome {

    private String id;

    private long position;
    private long size;
    private long spacer;
    private long mismatches;

    private String analysisParentId;    //id analyzy

    public StoredPalindrome() {

    }

    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JasDBProperty
    public long getSpacerSize() { return spacer; };
    public void setSpacerSize(long spacer) { this.spacer = spacer; }

    @JasDBProperty
    public long getPosition() {
        return position;
    }
    public void setPosition(long position) { this.position = position; }

    @JasDBProperty
    public long getMismatches() {
        return mismatches;
    }
    public void setMismatches(long mismatches) { this.mismatches = mismatches; }

    @JasDBProperty
    public long getSize() {
        return size;
    }
    public void setSize(long size) { this.size = size; }

    @JasDBProperty
    public String getAnalysisParentId() { return analysisParentId; }
    public void setAnalysisParentId(String analysisParentId) { this.analysisParentId = analysisParentId; }

    public Palindrome makePalindrome(Genome genome) {
        return new Palindrome(genome.sequence(), (int)position, (int)size, (int)spacer, (int) mismatches);
    }
}
