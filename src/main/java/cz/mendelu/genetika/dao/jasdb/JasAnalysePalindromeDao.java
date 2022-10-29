package cz.mendelu.genetika.dao.jasdb;

import cz.mendelu.genetika.palindrome.AnalysePalindrome;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.index.keys.types.StringKeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
public class JasAnalysePalindromeDao extends JasDao<AnalysePalindrome> {

    public static final String BAG_NAME = "analyses";

    private static final String GENOM_ID_FILED = "genomeId";

    private static final Logger LOG = LoggerFactory.getLogger(JasAnalysePalindromeDao.class);

    public JasAnalysePalindromeDao(DBSession dbSession) {
        super(dbSession, BAG_NAME);

        registerIndexString(GENOM_ID_FILED, new StringKeyType(128), false);
    }

    public List<AnalysePalindrome> findByGenomeId(@NotNull String id) {
        return findByField(GENOM_ID_FILED, id);
    }
}
