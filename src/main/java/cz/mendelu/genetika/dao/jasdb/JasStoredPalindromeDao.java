package cz.mendelu.genetika.dao.jasdb;

import cz.mendelu.genetika.palindrome.StoredPalindrome;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.index.keys.types.StringKeyType;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DAO pro ulozeni palindromu
 *
 * Created by Jiří Lýsek on 5.2.2016.
 */
public class JasStoredPalindromeDao extends JasDao<StoredPalindrome> {

    public static final String BAG_NAME = "palindromes";

    private static final String ANALYSIS_ID_FILED = "analysisParentId";

    public JasStoredPalindromeDao(DBSession dbSession) {
        super(dbSession, BAG_NAME);

        registerIndexString(ANALYSIS_ID_FILED, new StringKeyType(128), false);
    }

    public List<StoredPalindrome> findByAnalysis(@NotNull String id) {
        return findByField(ANALYSIS_ID_FILED, id);
    }

}
