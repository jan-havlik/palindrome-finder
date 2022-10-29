package cz.mendelu.genetika.dao.jasdb

import cz.mendelu.genetika.user.User
import nl.renarj.jasdb.LocalDBSession
import nl.renarj.jasdb.api.DBSession

/**
 * Created by xkoloma1 on 05.01.2016.
 */
class JasUserDaoTest extends GroovyTestCase {

    private static final String INSTANCE = JasUserDaoTest.class.simpleName;

    private static final DBSession dbSession = new LocalDBSession();

    private JasUserDao jasUserDao;

    private File tmpDBFile;

    void setUp() {
        tmpDBFile = File.createTempDir();
        dbSession.addAndSwitchInstance(INSTANCE, tmpDBFile.absolutePath);

        jasUserDao = new JasUserDao(dbSession);
        jasUserDao.store(new User("test1@email.com", "password 1"));
        jasUserDao.store(new User("test2@email.com", "password 2"));
        jasUserDao.store(new User("test3@email.com", "password 3"));
    }

    void tearDown() {
        dbSession.deleteInstance(INSTANCE);
        tmpDBFile.deleteDir();
    }

    void testStore() {
        User user = new User("test@email.com", "password");
        jasUserDao.store(user);
        assert user.id != null;
    }

    void testFindAll() {
        List<User> users = jasUserDao.findAll()
        assert users.size() == 3;
        assert users.get(0).email == "test1@email.com";
        assert users.get(0).password == "password 1";
    }

    void testJsonFindByEmail() {
        User user = jasUserDao.findByEmail("test1@email.com")
        assert user != null;
        assert user.email == "test1@email.com";
        assert user.password == "password 1";
    }
}
