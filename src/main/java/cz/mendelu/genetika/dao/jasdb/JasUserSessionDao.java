package cz.mendelu.genetika.dao.jasdb;

import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import cz.mendelu.genetika.user.User;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.api.SimpleEntity;
import nl.renarj.jasdb.api.model.EntityBag;
import nl.renarj.jasdb.api.query.QueryBuilder;
import nl.renarj.jasdb.api.query.QueryResult;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import nl.renarj.jasdb.index.keys.types.StringKeyType;
import nl.renarj.jasdb.index.search.IndexField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xkoloma1 on 11.01.2016.
 */
public class JasUserSessionDao extends JasDao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(JasUserSessionDao.class);
    private static final String BAG_USER_SESSION = "user-session";
    private static final String PROP_SESSION_ID = "sessionId";
    private static final String PROP_USER_ID = "user";
    private static final String PROP_TIMESTAMP = "timestamp";

    public JasUserSessionDao(DBSession dbSession) {
        super(dbSession, BAG_USER_SESSION);
        registerIndexString(PROP_USER_ID, new StringKeyType(128), true);
    }

    public void store(User user, String sessionId) {
        if (user.getId() == null)
            throw new IllegalArgumentException(String.format("User %s is not store yet.", user.getEmail()));

        try {
            SimpleEntity entity = new SimpleEntity();
            entity.addProperty(PROP_USER_ID, user.getId());
            entity.addProperty(PROP_SESSION_ID, sessionId);
            entity.addProperty(PROP_TIMESTAMP, System.currentTimeMillis());
            entityBag.addEntity(entity);
        } catch (JasDBStorageException e) {
            throw new JasException("Strore user session failed.", e);
        } finally {
            try {
                entityBag.flush();
            } catch (JasDBStorageException e) {
                LOG.warn("Flush failed.", e);
            }
        }
    }

    public User getUserBySessionId(String sessionId) {
        try {
            QueryResult result = entityBag.find(QueryBuilder.createBuilder().field(PROP_SESSION_ID).value(sessionId)).execute();
            if (result.size() == 0)
                return null;

            String userId = (String) result.next().getProperty(PROP_USER_ID).getFirstValue().getValue();

            return entityManager.findEntity(User.class, userId);
        } catch (JasDBStorageException e) {
            throw new JasException("Find user by sessionId failed.", e);
        }
    }

    public void delete(String sessionId) {
        try {
            QueryResult result = entityBag.find(QueryBuilder.createBuilder().field(PROP_SESSION_ID).value(sessionId)).execute();
            for (SimpleEntity entity : result) {
                entityBag.removeEntity(entity);
            }
        } catch (JasDBStorageException e) {
            throw new JasException("Find user by sessionId failed.", e);
        } finally {
            try {
                entityBag.flush();
            } catch (JasDBStorageException e) {
                LOG.warn("Flush failed.", e);
            }
        }
    }
}
