package cz.mendelu.genetika.bulk.analysis;

import au.com.bytecode.opencsv.CSVWriter;
import cz.mendelu.genetika.bulk.analysis.helpers.FileList;
import cz.mendelu.genetika.genoms.Feature;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.helpers.FeatureSieve;
import cz.mendelu.genetika.genoms.helpers.NCBIMultiDownloader;
import cz.mendelu.genetika.genoms.helpers.PalindromeMatcherHelper;
import cz.mendelu.genetika.genoms.helpers.SequenceHelper;
import cz.mendelu.genetika.palindrome.Palindrome;
import cz.mendelu.genetika.palindrome.PalindromeMatcher;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Jiří Lýsek on 1.6.2017.
 */
public class BulkAnalysisCSV extends BulkAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAnalysisCSV.class);

    private String pathToCsv;
    private String storagePath;

    private BulkCSVWriter overallReport;
    private FeatureCSVWriter overallFeatureReport;

    public BulkAnalysisCSV(String pathToCsv, String storagePath, int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        super(minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);
        this.pathToCsv = pathToCsv;
        this.storagePath = storagePath;
    }

    public void run() throws IOException {
        NCBIDownloader downloader = new NCBIDownloader(this.pathToCsv, this.storagePath);
        Stream<Pair<String, String>> idStream = downloader.download();
        overallReport = new BulkCSVWriter(df, storagePath, minLength, maxLength);
        overallReport.prepareOverallReport();
        overallFeatureReport = new FeatureCSVWriter(df, storagePath);
        idStream.forEach(info -> {
            try {
                Sequence s = new Sequence(new File(this.storagePath + "/" + info.getKey() + NCBIMultiDownloader.DOWNLOADED_FILE_EXT));
                PalindromeMatcher pm = runPalindromeAnalysis(s);
                LOG.info("Found {} palindromes for {}", pm.getCount(), info.getKey());

                //pro kazdou feature v genu mam roztridene palindromy
                Stream<Pair<Feature, FeatureSieve<Palindrome>>> sievedPalindromes = findPalindromesAroundFeatures(this.storagePath + "/" + info.getKey() + "_ft" + NCBIMultiDownloader.DOWNLOADED_FILE_EXT, pm);
                storeFeatureStatistics(info.getKey(), sievedPalindromes);

                Map<Integer, Integer> hist = PalindromeMatcherHelper.calcHistogramLength(pm);
                storeHistogramLength(this.storagePath + "/" + info.getKey() + "_hist_length.csv", hist);

                storePalindromes(this.storagePath + "/" + info.getKey() + "_palindromes.csv", pm);

                overallReport.storeOverallInfo(
                        info.getValue(),
                        info.getKey(),
                        s.getLength(),
                        pm.getCount(),
                        PalindromeMatcherHelper.getCount(pm, 8).intValue(),
                        PalindromeMatcherHelper.getCount(pm, 10).intValue(),
                        PalindromeMatcherHelper.getCount(pm, 12).intValue(),
                        SequenceHelper.count(s, Sequence.G) + SequenceHelper.count(s, Sequence.C),
                        hist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        overallReport.finish();
        overallFeatureReport.store();
    }

    private void storeFeatureStatistics(String id, Stream<Pair<Feature, FeatureSieve<Palindrome>>> sievedPalindromes) throws IOException {
        FileWriter fw = new FileWriter(this.storagePath + "/" + id + "_feature_palindromes.csv");
        CSVWriter writer = new CSVWriter(new BufferedWriter(fw));

        String[] header = new String[] {
                "Feature",
                "Info",
                "Feature start",
                "Feature end",
                "Feature size",
                "fr. all inside",
                "fr. 8+ inside",
                "fr. 10+ inside",
                "fr. 12+ inside",
                "fr. all around",
                "fr. 8+ around",
                "fr. 10+ around",
                "fr. 12+ around",
                "fr. all before",
                "fr. 8+ before",
                "fr. 10+ before",
                "fr. 12+ before",
                "fr. all after",
                "fr. 8+ after",
                "fr. 10+ after",
                "fr. 12+ after",
                "all inside",
                "8+ inside",
                "10+ inside",
                "12+ inside",
                "all around",
                "8+ around",
                "10+ around",
                "12+ around",
                "all before",
                "8+ before",
                "10+ before",
                "12+ before",
                "all after",
                "8+ after",
                "10+ after",
                "12+ after",
        };
        writer.writeNext(header);

        sievedPalindromes.forEach(pair -> {
            Feature f = pair.getKey();
            FeatureSieve<Palindrome> sieve = pair.getValue();

            overallFeatureReport.addFeature(f, sieve);

            try {
                writer.writeNext(new String[] {
                        f.getName(),
                        f.concatQualifiers(),
                        String.valueOf(f.getPosition() + 1),
                        String.valueOf(f.getPosition() + f.getLength() + 1),
                        String.valueOf(f.getLength()),
                        //frekvence v miste
                        df.format(sieve.getCount(0,0) / (double)f.getLength()),
                        df.format(sieve.getCount(0,8) / (double)f.getLength()),
                        df.format(sieve.getCount(0,10) / (double)f.getLength()),
                        df.format(sieve.getCount(0,12) / (double)f.getLength()),
                        //frekvence v okoli 100
                        df.format(sieve.getCount(100) / (2 * (double)Feature.COUNT_DISTANCE)),
                        df.format(sieve.getCount(100,8) / (2 * (double)Feature.COUNT_DISTANCE)),
                        df.format(sieve.getCount(100,10) / (2 * (double)Feature.COUNT_DISTANCE)),
                        df.format(sieve.getCount(100,12) / (2 * (double)Feature.COUNT_DISTANCE)),
                        //frekvence v okoli 100 pred
                        df.format(sieve.getCountBefore(100) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountBefore(100,8) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountBefore(100,10) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountBefore(100,12) / (double)Feature.COUNT_DISTANCE),
                        //frekvence v okoli 100 za
                        df.format(sieve.getCountAfter(100) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountAfter(100,8) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountAfter(100,10) / (double)Feature.COUNT_DISTANCE),
                        df.format(sieve.getCountAfter(100,12) / (double)Feature.COUNT_DISTANCE),
                        //pocty v miste
                        String.valueOf(sieve.getCount(0)),
                        String.valueOf(sieve.getCount(0,8)),
                        String.valueOf(sieve.getCount(0,10)),
                        String.valueOf(sieve.getCount(0,12)),
                        //pocty v okoli 100
                        String.valueOf(sieve.getCount(100)),
                        String.valueOf(sieve.getCount(100,8)),
                        String.valueOf(sieve.getCount(100,10)),
                        String.valueOf(sieve.getCount(100,12)),
                        //pocty v okoli 100 pred
                        String.valueOf(sieve.getCountBefore(100)),
                        String.valueOf(sieve.getCountBefore(100,8)),
                        String.valueOf(sieve.getCountBefore(100,10)),
                        String.valueOf(sieve.getCountBefore(100,12)),
                        //pocty v okoli 100 za
                        String.valueOf(sieve.getCountAfter(100)),
                        String.valueOf(sieve.getCountAfter(100,8)),
                        String.valueOf(sieve.getCountAfter(100,10)),
                        String.valueOf(sieve.getCountAfter(100,12)),
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    public static void main(String[] args) {
        String[] files = new String[]{
                "list"
        };
        for (String batchFile : files) {
            BulkAnalysisCSV ba = new BulkAnalysisCSV("data/20190618/" + batchFile + ".csv", "data/20190618/results/" + batchFile, 6, 30, 0, 10, "0,1", false, false);
            try {
                ba.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void analyseCSVFolder(String filesPath, String target, int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        Stream<File> files = FileList.listCSVFiles(filesPath);
        files.forEach(batchFile -> {
            analyseCSVFile(batchFile, target, minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);
        });
    }

    public static void analyseCSVFile(File batchFile, String target, int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        BulkAnalysisCSV ba = new BulkAnalysisCSV(batchFile.getAbsolutePath(), target + '/' + batchFile.getName(), minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);
        try {
            ba.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
