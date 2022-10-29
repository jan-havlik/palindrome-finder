package cz.mendelu.genetika.bulk.analysis;

import au.com.bytecode.opencsv.CSVWriter;
import cz.mendelu.genetika.genoms.Feature;
import cz.mendelu.genetika.genoms.FeatureTable;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.helpers.FeatureSieve;
import cz.mendelu.genetika.palindrome.*;
import cz.mendelu.genetika.rest.jetty.JettyContext;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jiří Lýsek on 16.11.2016.
 */
abstract public class BulkAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAnalysis.class);

    private PalindromeDetectorBuilder palindromeDetectorBuilder = JettyContext.getPalindromeDetectorBuilder();

    protected int minLength;
    protected int maxLength;

    protected int minSpacer;
    protected int maxSpacer;
    protected String mismatches;

    protected DecimalFormat df;

    protected boolean isCircular;
    protected boolean filterAT;

    public BulkAnalysis(int minLength, int maxLength, int minSpacer, int maxSpacer, String mismatches, boolean isCircular, boolean filterAT) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        df = new DecimalFormat("#.########", otherSymbols);

        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minSpacer = minSpacer;
        this.maxSpacer = maxSpacer;
        this.mismatches = mismatches;
        this.isCircular = isCircular;
        this.filterAT = filterAT;
    }

    protected Stream<Pair<Feature, FeatureSieve<Palindrome>>> findPalindromesAroundFeatures(String path, PalindromeMatcher pm) throws IOException {
        FeatureTable ft = new FeatureTable(new File(path));
        Stream<Pair<Feature, FeatureSieve<Palindrome>>> pairs = ft.loadFeatureDescriptions().map(f -> {
            FeatureSieve<Palindrome> sieve = new FeatureSieve<Palindrome>(f);
            sieve.addRange(0);
            sieve.addRange(f.COUNT_DISTANCE);
            //najit palindromy v okoli teto feature
            pm.forEach(p -> {
                sieve.decideMembership(p);
            });
            return new Pair<Feature, FeatureSieve<Palindrome>>(f, sieve);
        });
        return pairs;
    }

    protected PalindromeMatcher runPalindromeAnalysis(Sequence s) throws IOException {
        PalindromeDetector detector = makePalindromeDetector();
        PalindromeMatcher pm = detector.findPalindrome(s);
        return pm;
    }

    /**
     * ulozit pocty a delky inverznich repetic
     */
    protected void storeHistogramLength(String path, Map<Integer, Integer> hist) throws IOException {
        FileWriter f = new FileWriter(path);
        BufferedWriter fileWriter = new BufferedWriter(f);
        CSVWriter writer = new CSVWriter(fileWriter);
        writer.writeNext(new String[]{"Size of palindrome", "Amount"});
        for (Integer size : hist.keySet()) {
            writer.writeNext(new String[]{String.valueOf(size), String.valueOf(hist.get(size))});
        }
        writer.close();
    }

    private int calcGC(Sequence s) {
        int ret = 0;
        for (int i = 0; i < s.getLength(); i++) {
            String gene = s.getSequence(i, 1).toString();
            if (gene.equals(Sequence.G) || gene.equals(Sequence.C)) {
                ret++;
            }
        }
        return ret;
    }

    private PalindromeDetector makePalindromeDetector() {
        NumberRange size = new NumberRange(this.minLength, this.maxLength);
        NumberRange spacer = new NumberRange(this.minSpacer, this.maxSpacer);
        NumberRange mismatches = new NumberRange(this.mismatches);
        PalindromeDetector pd = palindromeDetectorBuilder.getPalindromeDetector(size.getValues(), spacer.getValues(), mismatches.getValues());
        pd.setCycleMode(this.isCircular);
        pd.setAtatFilter(this.filterAT);
        return pd;
    }

    /**
     * ulozit nalezene palindromy
     *
     * @param pm
     */
    protected void storePalindromes(String path, PalindromeMatcher pm) throws IOException {
        List<Palindrome> palindromes = pm.stream().sorted((p1, p2) -> {
            return Long.compare(p1.getPosition(), p2.getPosition());
        }).collect(Collectors.toList());

        FileWriter f = new FileWriter(path);
        BufferedWriter fileWriter = new BufferedWriter(f);
        CSVWriter palindromeCSV = new CSVWriter(fileWriter);
        palindromeCSV.writeNext(new String[]{
                "Signature",
                "Position",
                "Length",
                "Spacer length",
                "Mismatch count",
                "Sequence",
                "Spacer",
                "Opposite"
        });
        palindromes.stream().forEach(palindrome -> {
            palindromeCSV.writeNext(new String[]{
                    palindrome.getSequence().getLength() + "-" + palindrome.getSpacer().getLength() + "-" + palindrome.getMismatches(),
                    (palindrome.getPosition() + 1) + "",
                    palindrome.getSequence().getLength() + "",
                    palindrome.getSpacer().getLength() + "",
                    palindrome.getMismatches() + "",
                    palindrome.getSequence().toString(),
                    palindrome.getSpacer().toString(),
                    palindrome.getOpposite().toString()
            });
        });
        fileWriter.close();
    }

}
