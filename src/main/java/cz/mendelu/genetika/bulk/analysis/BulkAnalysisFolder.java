package cz.mendelu.genetika.bulk.analysis;

import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.format.ConvertorFactory;
import cz.mendelu.genetika.genoms.format.FormatConverter;
import cz.mendelu.genetika.genoms.helpers.PalindromeMatcherHelper;
import cz.mendelu.genetika.genoms.helpers.SequenceHelper;
import cz.mendelu.genetika.palindrome.PalindromeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.stream.Stream;

/**
 * provede hromadnou analyzu nad slozkou s fasta soubory
 *
 * Created by Jiří Lýsek on 1.6.2017.
 */
public class BulkAnalysisFolder extends BulkAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAnalysisFolder.class);

    private String filesPath;
    private String storagePath;

    private static final String TMP_DIR = "tmp";
    private static final String RESULTS_DIR = "results";

    private String fileExtenstion = "fasta";

    private BulkCSVWriter overallReport;

    public BulkAnalysisFolder(String filesPath, String storagePath, String extension, int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        super(minLength, maxLength, minSpacer, maxSpacer, mismatches, isCircular, filterAT);

        this.fileExtenstion = extension;
        this.filesPath = filesPath;
        this.storagePath = storagePath;
    }

    private Stream<File> listFiles() {
        File folder = new File(this.filesPath);
        if(!folder.exists()) {
            LOG.error("Folder {} does not exist.", this.filesPath);
        }
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("." + fileExtenstion);
            }
        });

        return Stream.of(files);
    }

    private void storePlainFile(File targetFile, FormatConverter converter, File fastaFile) throws IOException {
        FileInputStream genomeInputStream = new FileInputStream(fastaFile);
        try (OutputStream genomeOutputStream = new FileOutputStream(targetFile)) {
            StringBuilder info = new StringBuilder();
            converter.convert(genomeInputStream, genomeOutputStream, info);
            genomeOutputStream.close();
        } catch (Exception e) {
            targetFile.delete();
            throw e;
        }
    }

    private void init() {
        File tmpDir = new File(storagePath + "/" + TMP_DIR);
        tmpDir.mkdirs();
        File resultsDir = new File(storagePath + "/" + RESULTS_DIR);
        resultsDir.mkdirs();
    }

    private PalindromeMatcher processRawFile(Sequence s, String filename) throws IOException {

        PalindromeMatcher pm = runPalindromeAnalysis(s);
        LOG.info("Found {} palindromes for {}", pm.getCount(), filename);

        Map<Integer, Integer> hist = PalindromeMatcherHelper.calcHistogramLength(pm);
        storeHistogramLength(this.storagePath + "/" + RESULTS_DIR + "/" + filename + "_hist_length.csv", hist);

        storePalindromes(this.storagePath + "/" + RESULTS_DIR + "/" + filename + "_palindromes.csv", pm);

        return pm;
    }

    public void runRaw() throws IOException {
        init();

        overallReport = new BulkCSVWriter(df, storagePath, minLength, maxLength);
        overallReport.prepareOverallReport();

        Stream<File> filesStream = listFiles();
        filesStream.forEach(file -> {
            try {
                Sequence s = new Sequence(file);
                PalindromeMatcher pm = processRawFile(s, file.getName());

                Map<Integer, Integer> hist = PalindromeMatcherHelper.calcHistogramLength(pm);

                overallReport.storeOverallInfo(
                        file.getName(),
                        "",
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
    }

    public void runFasta() throws IOException {
        init();

        FormatConverter converter = ConvertorFactory.convertor(FormatConverter.FASTA);

        overallReport = new BulkCSVWriter(df, storagePath, minLength, maxLength);
        overallReport.prepareOverallReport();

        Stream<File> fastaFilesStream = listFiles();
        fastaFilesStream.forEach(fastaFile -> {
            try {
                File file = new File(this.storagePath + "/" + TMP_DIR + "/" + fastaFile.getName() + ".txt");
                storePlainFile(file, converter, fastaFile);

                Sequence s = new Sequence(file);
                PalindromeMatcher pm = processRawFile(s, file.getName());

                Map<Integer, Integer> hist = PalindromeMatcherHelper.calcHistogramLength(pm);

                overallReport.storeOverallInfo(
                        file.getName(),
                        "",
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
    }

}
