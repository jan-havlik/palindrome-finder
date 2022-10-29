package cz.mendelu.genetika.genoms.folder;

import cz.mendelu.genetika.genoms.Genom;
import cz.mendelu.genetika.genoms.GenomSource;
import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Honza on 23. 7. 2015.
 */
public class FolderGenomSource implements GenomSource {

    private static final Logger LOG = LoggerFactory.getLogger(FolderGenomSource.class);

    private File folder;


    public FolderGenomSource(File folder) {
        this.folder = folder;
        LOG.info("New genoms source folder created at {}", folder);
    }

    @Override
    public Genom getGenom(String name) {
        LOG.info("Load genom name '{}'", name);
        return new Genom(new File(folder, name));
    }

    @Override
    public Collection<Genom> getGenoms() {
        LOG.info("Scan folder {} for exists genoms", folder);
        return Arrays.asList(folder.listFiles()).stream()
                .map(f -> new Genom(f))
                .collect(Collectors.toList());
    }

    @Override
    public Genom createGenom(String name) throws IOException {
        LOG.info("Create new ganom name {} in folder {}.", name, folder);
        return new Genom(new File(folder, name));
    }
    
    @Override
    public Iterator<Genom> iterator() {
        return getGenoms().iterator();
    }

}
