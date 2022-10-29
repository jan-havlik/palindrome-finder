package cz.mendelu.genetika.bulk.analysis.helpers;

import cz.mendelu.genetika.bulk.analysis.BulkAnalysisCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.stream.Stream;

public class FileList {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAnalysisCSV.class);

    public static Stream<File> listFiles(String filesPath, String ext) {
        File folder = new File(filesPath);
        if(!folder.exists()) {
            LOG.error("Folder {} does not exist.", filesPath);
        }
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(ext);
            }
        });

        return Stream.of(files);
    }

    public static Stream<File> listCSVFiles(String filesPath) {
        return listFiles(filesPath, ".csv");
    }

}
