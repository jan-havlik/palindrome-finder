package cz.mendelu.genetika.binding.predictor;

import au.com.bytecode.opencsv.CSVReader;
import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.genoms.format.ConvertorFactory;
import cz.mendelu.genetika.genoms.format.FormatConverter;
import cz.mendelu.genetika.genoms.helpers.FeatureSieve;
import cz.mendelu.genetika.palindrome.NumberRange;
import cz.mendelu.genetika.palindrome.Palindrome;
import cz.mendelu.genetika.palindrome.PalindromeDetector;
import cz.mendelu.genetika.palindrome.PalindromeDetectorBuilder;
import cz.mendelu.genetika.rest.jetty.JettyContext;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * poznamky:
 * v p53 excel souboru je asi tento Genom r. 2006
 * https://genome-euro.ucsc.edu/cgi-bin/hgTracks?db=hg18&lastVirtModeType=default&lastVirtModeExtraState=&virtModeType=default&virtMode=0&nonVirtPosition=&position=chr1%3A1%2D10&hgsid=216206389_kfXR4ag8iQhyEDx3BtwD4LPhn193
 * ke stazeni zde:
 * http://hgdownload.soe.ucsc.edu/goldenPath/hg18/chromosomes/
 *
 * Created by Jiří Lýsek on 22.6.2016.
 */
public class P53PalindromeAnalysis {

    private double threshold;
    private String path = "";

    private PalindromeDetectorBuilder palindromeDetectorBuilder = JettyContext.getPalindromeDetectorBuilder();

    public P53PalindromeAnalysis(String path, double threshold) {
        this.path = path;
        this.threshold = threshold;
    }

    /**
     * konverze cele sekvence do interniho formatu
     *
     * @param file
     * @throws IOException
     */
    private void convertToInternalFormat(String file) throws IOException {
        File inFile = new File(path + "/" + file + ".fa");
        File outFile = new File(path + "/" + file);
        FormatConverter converter = ConvertorFactory.convertor(FormatConverter.FASTA);
        StringBuilder info = new StringBuilder();
        try (InputStream data = new BufferedInputStream(new FileInputStream(inFile))) {
            try (OutputStream genomeOutputStream = new BufferedOutputStream(new FileOutputStream(outFile))) {
                converter.convert(data, genomeOutputStream, info);
                genomeOutputStream.close();
            }
            data.close();
        }
    }

    private void convertGenome(String name) throws IOException {
        File fileInInternalFormat = new File(path + "/" + name);
        if(!fileInInternalFormat.exists()) {
            System.out.println("Converting " + name + " to internal format.");
            convertToInternalFormat(name);
        }
    }

    /**
     * spusteni konverze za podminky, ze jeste neexistuje kopie v internim formatu
     *
     * @param csvFile nazev souboru s informacemi ve formatu fastaFile,od,do
     * @throws IOException
     */
    private void convertGenomes(String csvFile) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path + "/" + csvFile));
        List<String[]> rows = reader.readAll();
        for(String[] row : rows) {
            convertGenome(row[0]);
        }
    }

    private HashMap<String, List<BindingPrediction>> loadSequencesAndPlaces(String csvFile) throws Exception {
        HashMap<String, List<BindingPrediction>> p53Sites = new HashMap<>();
        CSVReader reader = new CSVReader(new FileReader(path + "/" + csvFile));
        List<String[]> rows = reader.readAll();
        HashMap<String, Sequence> allSequences = new HashMap<>();

        int mismatch = 0;
        int match = 0;
        for(String[] row : rows) {
            Sequence s;
            if(allSequences.containsKey(row[0])) {
                s = allSequences.get(row[0]);
            } else {
                s = new Sequence(new File(path + "/" + row[0]));
                allSequences.put(row[0], s);
            }
            int start = Integer.parseInt(row[1]) - 1;
            int size = Integer.parseInt(row[2]) - start;
            Sequence p53Site = s.getSequence(start, size);

            if(!p53Site.toString().equals((row[3] + row[4]).toUpperCase())) {
                mismatch++;
            } else {
                match++;
            }

            double prediction = 0;
            if(size == 20) {
                prediction = P53BindingPredictor.calc(p53Site.toString());
            }

            BindingPrediction bp = new BindingPrediction(s, start, size, prediction);
            if(p53Sites.containsKey(row[0])) {
                p53Sites.get(row[0]).add(bp);
            } else {
                List<BindingPrediction> tmp = new ArrayList<>();
                tmp.add(bp);
                p53Sites.put(row[0], tmp);
            }

            //System.out.println(sq);
        }
        System.out.println("Mis: " + mismatch + "/" + (match + mismatch));
        return p53Sites;
    }

    private PalindromeDetector makePalindromeDetector() {
        NumberRange size = new NumberRange("6-30");
        NumberRange spacer = new NumberRange("0-10");
        NumberRange mismatches = new NumberRange("0,1");
        return palindromeDetectorBuilder.getPalindromeDetector(size.getValues(), spacer.getValues(), mismatches.getValues());
    }

    /**
     * z CSV souboru nacte pozice p53 zajimavych mist a nazvu sekvenci ve fasta formatu
     * nacte sekvence s okolim a potom v techto kratkych sekvencich najde palindromy a spocita je
     *
     * @param csvFile
     */
    private void analyseByPositions(String csvFile) {
        try {
            //konverze sekvenci do interniho formatu
            convertGenomes(csvFile);

            //nacist informace z CSV (chromozom, od, do)
            HashMap<String, List<BindingPrediction>> allP53Sites = loadSequencesAndPlaces(csvFile);

            //najit palindromy v okoli
            PalindromeDetector detector = makePalindromeDetector();
            allP53Sites.forEach((chromosomeName, p53Sites) -> {
                Stream<FeatureSieve<Palindrome>> sieves = p53Sites.stream().map(bp -> {
                    FeatureSieve<Palindrome> sieve = new FeatureSieve<Palindrome>(bp);
                    sieve.addRange(0);
                    sieve.addRange(BindingPrediction.DISTANCE_CLOSE);
                    sieve.addRange(BindingPrediction.DISTANCE_FAR);
                    System.out.println("Detecting palindromes for sequence " + chromosomeName + " at " + bp.getPosition());
                    detector.findPalindrome(bp.getSearchSequence()).forEach(p -> {
                        sieve.decideMembership(p);
                    });
                    return sieve;
                });
                //printResults(p53Sites, sieves.collect(Collectors.toList()), chromosomeName);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printResults(List<BindingPrediction> p53Sites, List<FeatureSieve<Palindrome>> sieves, String sequenceName) throws Exception {
        if(p53Sites.size() != sieves.size()) {
            throw new Exception("p53 sites and sieves must have same size");
        }
        for(int i = 0; i < p53Sites.size(); i++) {
            BindingPrediction bp = p53Sites.get(i);
            FeatureSieve<Palindrome> sieve = sieves.get(i);

            System.out.print(sequenceName + "\t");
            System.out.print((bp.getPosition() + 1) + "\t");
            System.out.print(bp.getDelta() + "\t");
            System.out.print(bp.getSequence() + "\t");
            System.out.print(sieve.getCount(0) + "\t");
            System.out.print(sieve.getCount(BindingPrediction.DISTANCE_CLOSE) + "\t");
            System.out.print(sieve.getCount(BindingPrediction.DISTANCE_FAR) + "\t");
            System.out.println(bp.getSearchSequence());
            //System.out.println(bp.toString());
        }
    }

    /**
     * projde celou sekvenci a najde v ni p53 zajimava mista pomoci prediktoru
     * potom v techto kratkych sekvencich najde palindromy a spocita je
     *
     * @param name nazev souboru
     */
    private void analyseByChromosome(String name) {
        try {
            convertGenome(name);

            Sequence s = new Sequence(new File(path + "/" + name));

            //System.out.println("Detecting p53 sites for " + name);
            List<BindingPrediction> p53Sites = P53BindingPredictor.findSites(s, threshold).collect(Collectors.toList());

            //najit palindromy v okoli
            PalindromeDetector detector = makePalindromeDetector();
            Stream<FeatureSieve<Palindrome>> sieves = p53Sites.stream().map(bp -> {
                //System.out.println("Detecting palindromes for sequence at " + bp.getPosition());
                FeatureSieve<Palindrome> sieve = new FeatureSieve<Palindrome>(bp);
                sieve.addRange(0);
                sieve.addRange(BindingPrediction.DISTANCE_CLOSE);
                sieve.addRange(BindingPrediction.DISTANCE_FAR);
                detector.findPalindrome(bp.getSearchSequence()).forEach(p -> {
                    sieve.decideMembership(p, bp.getSearchSequenceOffset());
                });
                return sieve;
            });

            printResults(p53Sites, sieves.collect(Collectors.toList()), name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //cely genom prediktorem
        /*
        P53PalindromeAnalysis analysis = new P53PalindromeAnalysis("./data/homo-sapiens/hg18", 0.6);
        for(int i = 1; i <= 22; i++) {
            analysis.analyseByChromosome("chr" + i);
        }
        analysis.analyseByChromosome("chrX");
        analysis.analyseByChromosome("chrY");
        */

        //pozice z CSV
        /*
        P53PalindromeAnalysis analysis = new P53PalindromeAnalysis("./data/homo-sapiens/hg18", 0.6);
        analysis.analyseByPositions("positions.csv");
        */

        //P53PalindromeAnalysis analysis = new P53PalindromeAnalysis("./data/homo-sapiens/hg18", 0.6);
        //analysis.analyseByPositions("positions-test.csv");

        //testovaci

        //jen podle prediktoru
        /*
        P53PalindromeAnalysis analysis = new P53PalindromeAnalysis("./data/homo-sapiens/hg18", 0.6);
        analysis.analyseByChromosome("chr21");
        */

        P53PalindromeAnalysis analysis = new P53PalindromeAnalysis("./data/homo-sapiens", 1);
        analysis.analyseByChromosome("test");

        //TODO ulozit do CSV nebo do databaze?


    }

}
