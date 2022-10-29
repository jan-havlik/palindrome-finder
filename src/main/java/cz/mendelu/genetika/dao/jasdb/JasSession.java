package cz.mendelu.genetika.dao.jasdb;

import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
public class JasSession implements AutoCloseable {

    private final DBSession dbSession;
    private final String genomeDir;

    public JasSession(DBSession dbSession, String genomeDir) {
        this.dbSession = dbSession;
        this.genomeDir = genomeDir;
    }

    public JasUserDao getUserDao() {
        return new JasUserDao(dbSession);
    }

    public JasUserSessionDao getUserSessionDao() {
        return new JasUserSessionDao(dbSession);
    }

    public JasAnalysePalindromeDao getAnalysePalindromeDao() {
        return new JasAnalysePalindromeDao(dbSession);
    }

    public JasGenomeDao getJasGenomeDao() {
        return new JasGenomeDao(dbSession, genomeDir);
    }

    public JasStoredPalindromeDao getStoredPalindromeDao() { return new JasStoredPalindromeDao(dbSession); }

    @Override
    public void close() {
        try {
            dbSession.closeSession();
        } catch (JasDBStorageException e) {
            throw new JasException("Session close failed", e);
        }
    }


}
