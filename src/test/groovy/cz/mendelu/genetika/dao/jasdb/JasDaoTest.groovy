package cz.mendelu.genetika.dao.jasdb

import cz.mendelu.genetika.user.User
import nl.renarj.jasdb.LocalDBSession
import nl.renarj.jasdb.api.DBSession

/**
 * Created by xkoloma1 on 15.01.2016.
 */
class JasDaoTest extends GroovyTestCase {

    private static final String INSTANCE = JasDaoTest.class.simpleName;

    private static final DBSession dbSession = new LocalDBSession();

    private File tmpDBFile;

    void setUp() {
        tmpDBFile = File.createTempDir();
        dbSession.addAndSwitchInstance(INSTANCE, tmpDBFile.absolutePath);
    }

    void tearDown() {
        dbSession.deleteInstance(INSTANCE);
        tmpDBFile.deleteDir();
    }
    void "test correct determine type Class"() {
        JasDao<User> jas = new JasDao<User>(dbSession, INSTANCE) {};
        assert jas.persistentClass == User.class;
    }
}
