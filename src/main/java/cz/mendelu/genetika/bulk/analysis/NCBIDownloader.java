package cz.mendelu.genetika.bulk.analysis;

import au.com.bytecode.opencsv.CSVReader;
import cz.mendelu.genetika.Config;
import cz.mendelu.genetika.genoms.helpers.NCBIMultiDownloader;
import cz.mendelu.genetika.genoms.resources.NCBI;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * stahne z NCBI genomy a feature table podle ID
 *
 * vstup:
 * CSV soubor s nazvem a ID
 *
 * Created by Jiří Lýsek on 20.6.2017.
 */
public class NCBIDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(NCBIDownloader.class);

    private final String pathToCsv;
    private final String storagePath;

    public NCBIDownloader(String pathToCsv, String storagePath) {
        this.pathToCsv = pathToCsv;
        this.storagePath = storagePath;
    }

    private Map<String, String> getIDsFromCSV() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(this.pathToCsv));
        CSVReader reader = new CSVReader(fileReader, '\t');
        List<String[]> rows = reader.readAll();
        reader.close();

        Map<String, String> ret = new HashMap<String, String>();
        for (String[] row : rows) {
            Pattern p = Pattern.compile("[A-Z]{1,2}_?[A-Z0-9]+(\\.[0-9]+){0,1}");
            if (p.matcher(row[1]).matches()) {
                ret.put(row[1], row[0]);
            } else if (p.matcher(row[0]).matches()) {
                ret.put(row[0], row[0]); // in case NCBI ID is the first column
            } else {
                LOG.info("Not matched ID '{}' for {}", row[1], row[0]);
            }
        }
        return ret;
    }

    public Stream<Pair<String, String>> download() throws IOException {
        NCBI ncbiGenomes = new NCBI(
                Config.ncbi.restriction_timing(),
                Config.ncbi.url(),
                Config.ncbi.db(),
                Config.ncbi.retmode(),
                Config.ncbi.rettype()
        );

        NCBI ncbiFeatureTable = new NCBI(
                Config.ncbi.restriction_timing(),
                Config.ncbiFeatureTable.url(),
                Config.ncbiFeatureTable.db(),
                Config.ncbiFeatureTable.retmode(),
                Config.ncbiFeatureTable.rettype()
        );

        Map<String, String> genomeIDs = this.getIDsFromCSV();
        NCBIMultiDownloader downloader = new NCBIMultiDownloader(this.storagePath, ncbiGenomes, ncbiFeatureTable);
        downloader.init();
        return downloader.downloadGenomes(genomeIDs.keySet()).map(id -> new Pair(id, genomeIDs.get(id)));
    }

}
