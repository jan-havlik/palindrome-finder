package cz.mendelu.genetika.dao.jasdb;

import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.Start.Service;
import cz.mendelu.genetika.user.User;
import nl.renarj.jasdb.LocalDBSession;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.core.SimpleKernel;
import nl.renarj.jasdb.core.exceptions.JasDBException;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Jan Kolomazník on 05.01.2016.
 */
public class EmbeddedJasDB extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJasDB.class);

    private static EmbeddedJasDB embeddedJasDB;

    private String genomeDir;

    private String databaseDir;

    private EmbeddedJasDB() {
        super("JasDB Thread");
        LOG.info("Create Embedded JasDB.");
    }

    public static EmbeddedJasDB getEmbeddedJasDB() {
        if (embeddedJasDB == null) {
            synchronized (EmbeddedJasDB.class) {
                if (embeddedJasDB == null) {
                    embeddedJasDB = new EmbeddedJasDB();
                    embeddedJasDB.genomeDir = Config.db.genomeDir();
                    embeddedJasDB.databaseDir = Config.db.directory();
                }
            }
        }
        return embeddedJasDB;
    }

    @Override
    public void run() {
        try {
            LOG.info("Starting Embedded JasDB.");

            synchronized (startUpMonitor) {
                //Forcible initialize JasDB, can also be lazy loaded on first session created
                SimpleKernel.initializeKernel();

                // See up state indicator
                ready = true;
                startUpMonitor.notifyAll();
            }

             //There is a wait for shutdown method
             SimpleKernel.waitForShutdown();
        } catch (Exception e) {
            throw new RuntimeException("Start JasDB failed.", e);
        } finally {
            try {
                //shutting down JasDB on program end / web app shutdown
                SimpleKernel.shutdown();
            } catch (JasDBException e) {
                LOG.warn("Emergency shutdown failed.", e);
            }
        }
    }

    public JasSession getSession(User user) {
        try {
            DBSession dbSession = new LocalDBSession();
            if (user != null && user != User.DEFAULT) {
                switchAndCreateInNotExists(dbSession, user);
            } else {
                switchAndCreateInNotExists(dbSession, User.DEFAULT);
            }
            return new JasSession(dbSession, genomeDir);
        } catch (JasDBStorageException e) {
            throw new JasException("Create local session failed.", e);
        }
    }

    private void switchAndCreateInNotExists(DBSession dbSession, User user) throws JasDBStorageException {
        try {
            dbSession.switchInstance(user.getEmail());
        } catch (JasDBStorageException e) {
            File dir = new File(databaseDir, user.getPath());
            if (!dir.exists()) dir.mkdirs();
            dbSession.addAndSwitchInstance(user.getEmail(), dir.getPath());
        }
    }

    @Override
    public void terminate() {
        try {
            LOG.info("Stop Embedded JasDB.");
            //shutting down JasDB on program end / web app shutdown
            SimpleKernel.shutdown();
        } catch (JasDBException e) {
            LOG.warn("Emergency shutdown failed.", e);
        }
    }
}
