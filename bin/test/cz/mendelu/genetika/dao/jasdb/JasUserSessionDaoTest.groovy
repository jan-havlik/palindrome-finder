package cz.mendelu.genetika.dao.jasdb

import cz.mendelu.genetika.user.User
import nl.renarj.jasdb.LocalDBSession
import nl.renarj.jasdb.api.DBSession

/**
 * Created by xkoloma1 on 11.01.2016.
 */
class JasUserSessionDaoTest extends GroovyTestCase {

    private static final String INSTANCE = JasUserSessionDaoTest.class.simpleName;

    private static final DBSession dbSession = new LocalDBSession();

    private JasUserSessionDao jasUserSessionDao;

    private User user;

    private File tmpDBFile;

    void setUp() {
        tmpDBFile = File.createTempDir();
        dbSession.addAndSwitchInstance(INSTANCE, tmpDBFile.absolutePath);

        user = new User("test1@email.com", "password 1")
        JasUserDao jasUserDao = new JasUserDao(dbSession);
        jasUserDao.store(user);

        jasUserSessionDao = new JasUserSessionDao(dbSession);
    }

    void tearDown() {
        dbSession.deleteInstance(INSTANCE);
        tmpDBFile.deleteDir();
    }

    void testGetNull() {
        assert jasUserSessionDao.getUserBySessionId("testGetNull") == null;
    }

    void testStoreAndGet() {
        jasUserSessionDao.store(user, "testStoreAndGet");
        User storedUser = jasUserSessionDao.getUserBySessionId("testStoreAndGet");

        assert storedUser == user;
    }

    void testDelete() {
        jasUserSessionDao.store(user, "testDelete");
        jasUserSessionDao.delete("testDelete");

        assert jasUserSessionDao.getUserBySessionId("testDelete") == null;
    }

}
