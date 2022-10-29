package cz.mendelu.genetika.dao.jasdb;

import cz.mendelu.genetika.user.User;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.index.keys.types.StringKeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
public class JasUserDao extends JasDao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(JasUserDao.class);

    public static final String BAG_NAME = "users";
    private static final String EMAIL_FILED = "email";

    public JasUserDao(DBSession dbSession) {
        super(dbSession, BAG_NAME);
        registerIndexString(EMAIL_FILED, new StringKeyType(128), true);
    }

    public User findByEmail(String email) {
        return findByFiled(EMAIL_FILED, email);
    }
}
