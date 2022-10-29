package cz.mendelu.genetika.dao.jasdb;

import cz.mendelu.genetika.genoms.Genome;
import cz.mendelu.genetika.user.User;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import nl.renarj.jasdb.index.keys.types.StringKeyType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by xkoloma1 on 15.01.2016.
 */
public class JasGenomeDao extends JasDao<Genome> {

    public static final String BAG_NAME = "genomes";
    private static final Logger LOG = LoggerFactory.getLogger(JasGenomeDao.class);
    private static final String NAME_FILED = "name";

    private File genomeDir;

    public JasGenomeDao(@NotNull DBSession dbSession,  @NotNull String genomeDir) {
        super(dbSession, BAG_NAME);

        registerIndexString(NAME_FILED, new StringKeyType(128), false);

        try { // Určení místa pro uložení genomu
            this.genomeDir = (dbSession.getInstanceId().equals(User.DEFAULT.getEmail()))
                    ? new File(genomeDir)
                    : new File(genomeDir, dbSession.getInstanceId());

            if (!this.genomeDir.exists()) {
                if (!this.genomeDir.mkdirs()) {
                    throw new UnsupportedOperationException(String.format("Directory %s for stores genomes not exists or can't be created", genomeDir));
                }
            }
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Create genome folder failed."), e);
        }
    }

    @Override
    public Genome store(@NotNull Genome genome) {
        return store(genome, false);
    }
    public Genome store(@NotNull Genome genome, boolean fileMove) {
        genome = super.store(genome);

        // Pokud genome není uložen v repozitáři, uložím jej tam a metoda končí.
        if (!isGenomeStoreInFile(genome)) {
            try {
                LOG.info("Create for genome {} file {} in storage", genome.getName(), genome.getId());
                File genomeFileInStorage = getGenomeFileInStorage(genome);
                FileUtils.writeByteArrayToFile(genomeFileInStorage, genome.sequence().toByteArray());
                genome.setFile(genomeFileInStorage);
                return genome;
            } catch (IOException e) {
                super.delete(genome);
                throw new JasException(String.format("Store genome %s to file %s failed.", genome.getName(), genome.getId()), e);
            }
        }

        // Pokud genom je ulozen v souboru, ale ve spatnem adresari, je prekopirován do zpravneho
        if (!isGenomeStoreCorrectlyInFolder(genome)) {
            try {
                LOG.info("{} genome {} to file {} in storage", ((fileMove) ? "Move" : "Copy"), genome.getName(), genome.getId());
                File genomeFileInStorage = getGenomeFileInStorage(genome);
                if (fileMove) {
                    FileUtils.moveFile(genome.getFile(), genomeFileInStorage);
                } else {
                    FileUtils.copyFile(genome.getFile(), genomeFileInStorage);
                }
                genome.setFile(genomeFileInStorage);
                return genome;
            } catch (IOException e) {
                super.delete(genome);
                throw new JasException(String.format("Store genome %s to file %s failed.", genome.getName(), genome.getId()), e);
            }
        }

        return genome;
    }

    private File getGenomeFileInStorage(Genome genome) {
        return new File(genomeDir, genome.getId());
    }

    private boolean isGenomeStoreInFile(Genome genome) {
        return genome.getFile() != null && genome.getFile().exists();
    }

    private boolean isGenomeStoreCorrectlyInFolder(Genome genome) {
        File correctFile = getGenomeFileInStorage(genome);
        return correctFile.equals(genome.getFile());
    }

    public Genome findByName(@NotNull String name) {
        Genome genome = findByFiled(NAME_FILED, name);
        genome.setFile(getGenomeFileInStorage(genome));
        return genome;
    }

    @Override
    public Genome get(@NotNull String id) {
        Genome genome = super.get(id);
        genome.setFile(getGenomeFileInStorage(genome));
        return genome;
    }

    @Override
    public List<Genome> findAll() {
        List<Genome> genomes = super.findAll();
        genomes.forEach(g -> g.setFile(getGenomeFileInStorage(g)));
        return genomes;
    }


}
