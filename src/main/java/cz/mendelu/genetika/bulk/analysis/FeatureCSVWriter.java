package cz.mendelu.genetika.bulk.analysis;

import au.com.bytecode.opencsv.CSVWriter;
import cz.mendelu.genetika.genoms.Feature;
import cz.mendelu.genetika.genoms.helpers.FeatureSieve;
import cz.mendelu.genetika.palindrome.Palindrome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jiří Lýsek on 16.12.2016.
 */
public class FeatureCSVWriter {

    private static final String FILE_NAME = "!overall_features.csv";

    private final String storagePath;
    private CSVWriter writer;

    private HashMap<String, FeatureStats> features = new HashMap<>();

    private DecimalFormat df;

    public FeatureCSVWriter(DecimalFormat df, String storagePath) {
        this.storagePath = storagePath;
        this.df = df;
    }

    public void store() throws IOException {
        FileWriter f = new FileWriter(this.storagePath + "/" + FILE_NAME);
        BufferedWriter fileWriter = new BufferedWriter(f);
        CSVWriter report = new CSVWriter(fileWriter);

        String[] header = new String[] {
                "Feature",
                "Feature count",
                "Feature total length",
                "Avg feature size",
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
        report.writeNext(header);
        for(FeatureStats fsts : features.values()) {
            int surroundingBases = Feature.COUNT_DISTANCE * fsts.getCount();
            String[] row = new String[] {
                    fsts.getName(),
                    String.valueOf(fsts.getCount()),
                    String.valueOf(fsts.getTotalLength()),
                    df.format(fsts.getTotalLength() / (double)fsts.getCount()),
                    //frekvence uvnitr
                    df.format(fsts.getPalindromesCount(0) / (double)fsts.getTotalLength()),
                    df.format(fsts.getPalindromesCount(0, 8) / (double)fsts.getTotalLength()),
                    df.format(fsts.getPalindromesCount(0, 10) / (double)fsts.getTotalLength()),
                    df.format(fsts.getPalindromesCount(0, 12) / (double)fsts.getTotalLength()),
                    //frekvence v okoli 100
                    df.format(fsts.getPalindromesCount(Feature.COUNT_DISTANCE) / (2 * (double)surroundingBases)),
                    df.format(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 8) / (2 * (double)surroundingBases)),
                    df.format(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 10) / (2 * (double)surroundingBases)),
                    df.format(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 12) / (2 * (double)surroundingBases)),
                    //frekvence v okoli 100 pred
                    df.format(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 8) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 10) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 12) / ((double)surroundingBases)),
                    //frekvence v okoli 100 za
                    df.format(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 8) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 10) / ((double)surroundingBases)),
                    df.format(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 12) / ((double)surroundingBases)),
                    //pocty uvnitr
                    String.valueOf(fsts.getPalindromesCount(0)),
                    String.valueOf(fsts.getPalindromesCount(0, 8)),
                    String.valueOf(fsts.getPalindromesCount(0, 10)),
                    String.valueOf(fsts.getPalindromesCount(0, 12)),
                    //pocty v okoli 100
                    String.valueOf(fsts.getPalindromesCount(Feature.COUNT_DISTANCE)),
                    String.valueOf(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 8)),
                    String.valueOf(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 10)),
                    String.valueOf(fsts.getPalindromesCount(Feature.COUNT_DISTANCE, 12)),
                    //pocty v okoli 100 pred
                    String.valueOf(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE)),
                    String.valueOf(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 8)),
                    String.valueOf(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 10)),
                    String.valueOf(fsts.getPalindromesCountBefore(Feature.COUNT_DISTANCE, 12)),
                    //pocty v okoli 100 za
                    String.valueOf(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE)),
                    String.valueOf(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 8)),
                    String.valueOf(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 10)),
                    String.valueOf(fsts.getPalindromesCountAfter(Feature.COUNT_DISTANCE, 12))
            };
            report.writeNext(row);
        }
        report.close();
    }

    public void addFeature(Feature f, FeatureSieve<Palindrome> fs) {
        FeatureStats fsts;
        if(features.containsKey(f.getName())) {
            fsts = features.get(f.getName());
        } else {
            fsts = new FeatureStats(f.getName());
            features.put(f.getName(), fsts);
        }
        fsts.addStats(f.getLength(), fs);
    }

    private class FeatureStats {

        private String name;
        private int totalLength;

        List<FeatureSieve<Palindrome>> sieves = new ArrayList<FeatureSieve<Palindrome>>();

        public FeatureStats(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getPalindromesCount(int range) {
            return sieves.stream().map(fs -> {
                return fs.getCount(range);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getPalindromesCount(int range, int minLen) {
            return sieves.stream().map(fs -> {
                return fs.getCount(range, minLen);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getPalindromesCountBefore(int range) {
            return sieves.stream().map(fs -> {
                return fs.getCountBefore(range);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getPalindromesCountAfter(int range) {
            return sieves.stream().map(fs -> {
                return fs.getCountAfter(range);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getPalindromesCountBefore(int range, int minLen) {
            return sieves.stream().map(fs -> {
                return fs.getCountBefore(range, minLen);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getPalindromesCountAfter(int range, int minLen) {
            return sieves.stream().map(fs -> {
                return fs.getCountAfter(range, minLen);
            }).reduce(0, (a, b) -> {
                return a + b;
            });
        }

        public int getCount() {
            return sieves.size();
        }

        public int getTotalLength() {
            return totalLength;
        }

        public void addStats(int length, FeatureSieve<Palindrome> fs) {
            totalLength += length;
            sieves.add(fs);
        }
    }

}
