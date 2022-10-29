package cz.mendelu.genetika.bulk.analysis;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Jiří Lýsek on 28.11.2016.
 */
public class BulkCSVWriter {

    private static final int RAW = 0;   //primo hodnoty
    private static final int AVERAGE = 1;   //podelit hodnotu poctem
    private static final int VARIANCE = 2;  //spocitat rozptyl
    private static final int STANDARD_DEV = 3;  //sm. odchylka

    private static final String OVERALL_FILE_NAME = "!overall_palindromes.csv";
    private static final String STATS_FILE_NAME = "!stats_palindromes.csv";
    private static final int STATIC_COL_COUNT = 12;  //pevne sloupecky v CSV

    private final int minLength;
    private final int maxLength;

    private CSVWriter overallReport;
    private CSVWriter statsReport;
    private String storagePath;

    private double[] statsCnt;
    private double[] statsSum;
    private double[] statsMin;
    private double[] statsMax;
    private double[] statsVarM2;
    private double[] statsMean ;

    private int cnt = 0;

    private DecimalFormat df;

    public BulkCSVWriter(DecimalFormat df, String storagePath, int minLength, int maxLength) {
        this.storagePath = storagePath;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.df = df;

        int tmp = (maxLength - minLength) + 1 + STATIC_COL_COUNT - 2; //pro vsechny delky a pro velikost, GC pocet, celkovy pocet, 8, 10, 12
        statsCnt = new double[tmp];
        statsSum = new double[tmp];
        statsMin = new double[tmp];
        statsMax = new double[tmp];
        statsVarM2 = new double[tmp];
        statsMean = new double[tmp];
    }

    private void insertHeader(CSVWriter w) throws IOException {
        int j = 0;
        String[] header = new String[(maxLength - minLength) + 1 + STATIC_COL_COUNT];
        header[j++] = "Title";
        header[j++] = "NCBI";
        header[j++] = "Size";
        header[j++] = "GC count";
        header[j++] = "fr. all";
        header[j++] = "fr. 8+";
        header[j++] = "fr. 10+";
        header[j++] = "fr. 12+";
        header[j++] = "all";
        header[j++] = "8+";
        header[j++] = "10+";
        header[j++] = "12+";
        for(int i = minLength; i <= maxLength; i++) {
            header[j++] = "" + i;
        }
        w.writeNext(header);
        w.flush();
    }

    public void prepareOverallReport() throws IOException {
        FileWriter f = new FileWriter(this.storagePath + "/" + OVERALL_FILE_NAME);
        BufferedWriter fileWriter = new BufferedWriter(f);
        overallReport = new CSVWriter(fileWriter);
        insertHeader(overallReport);
    }

    private void writeStats(int column, double value) {
        writeStats(column, value, true);
    }

    private void writeStats(int column, double value, boolean allowSum) {
        //pocet nenulovych hodnot
        statsCnt[column] += value != 0 ? 1 : 0;
        //soucet hodnoty value pro dany sloupec
        if(allowSum) {
            statsSum[column] += value;
        } else {
            statsSum[column] += 0;
        }
        //minimum a maximum pro dany sloupec
        if(cnt == 1) {
            statsMin[column] = value;
            statsMax[column] = value;
        } else {
            statsMin[column] = value < statsMin[column] ? value : statsMin[column];
            statsMax[column] = value > statsMax[column] ? value : statsMax[column];
        }
        //rozptyl
        double delta = value - statsMean[column];
        statsMean[column] = statsMean[column] + (delta / cnt);
        double delta2 = value - statsMean[column];
        statsVarM2[column] = statsVarM2[column] + (delta * delta2);
    }

    /**
     * ulozit souhrnne informace
     */
    public void storeOverallInfo(String title, String id, int size, int count, int count8, int count10, int count12, int GCCount, Map<Integer, Integer> histSize) throws IOException {
        int j = 0, k = 0;
        String[] data = new String[(maxLength - minLength) + 1 + STATIC_COL_COUNT];
        data[j++] = title;
        data[j++] = id;
        data[j++] = size + "";
        data[j++] = GCCount + "";
        //frekvence
        data[j++] = df.format(count / (double)size) + "";
        data[j++] = df.format(count8 / (double)size) + "";
        data[j++] = df.format(count10 / (double)size) + "";
        data[j++] = df.format(count12 / (double)size) + "";
        //pocty
        data[j++] = count + "";
        data[j++] = count8 + "";
        data[j++] = count10 + "";
        data[j++] = count12 + "";

        cnt++;

        //statistika
        writeStats(k++, size);
        writeStats(k++, GCCount);
        //frekvence
        writeStats(k++, (count / (double)size), false);
        writeStats(k++, (count8 / (double)size), false);
        writeStats(k++, (count10 / (double)size), false);
        writeStats(k++, (count12 / (double)size), false);
        //pocty
        writeStats(k++, count);
        writeStats(k++, count8);
        writeStats(k++, count10);
        writeStats(k++, count12);
        for (int i = minLength; i <= maxLength; i++) {
            Integer palindromeCount = histSize.get(i);
            data[j++] = palindromeCount != null ? palindromeCount + "" : "0";

            //statistika
            writeStats(k++, palindromeCount != null ? palindromeCount : 0);
        }

        overallReport.writeNext(data);
        overallReport.flush();
    }

    /**
     * zapise statistiku a ukonci
     */
    public void finish() throws IOException {
        storeStats();
        overallReport.flush();
        overallReport.close();
    }

    private void writeStatsRow(String header, double[] statsData, int type) {
        int j = 0, k = 2;
        String[] data = new String[(maxLength - minLength) + 1 + STATIC_COL_COUNT];
        data[0] = header;
        for(int i = 0; i < statsData.length; i++) {
            switch(type) {
                case AVERAGE: {
                    data[k++] = df.format(statsData[j++] / (double)cnt);
                } break;
                case VARIANCE: {
                    data[k++] = df.format(statsVarM2[j++] / (double)(cnt - 1));
                } break;
                case STANDARD_DEV: {
                    data[k++] = df.format(Math.sqrt(statsVarM2[j++] / (double)(cnt - 1)));
                } break;
                default: {
                    data[k++] = df.format(statsData[j++]);
                }
            }
        }
        statsReport.writeNext(data);
    }

    private void storeStats() throws IOException {
        if(cnt > 0) {
            FileWriter f = new FileWriter(this.storagePath + "/" + STATS_FILE_NAME);
            BufferedWriter fileWriter = new BufferedWriter(f);
            statsReport = new CSVWriter(fileWriter);

            insertHeader(statsReport);

            writeStatsRow("Cnt", statsCnt, RAW);
            writeStatsRow("Sum", statsSum, RAW);
            writeStatsRow("Avg", statsSum, AVERAGE);
            writeStatsRow("Min", statsMin, RAW);
            writeStatsRow("Max", statsMax, RAW);
            writeStatsRow("Var", statsVarM2, VARIANCE);
            writeStatsRow("Std", statsVarM2, STANDARD_DEV);

            //statistiky pro frekvence
            String[] data = new String[(maxLength - minLength) + 1 + STATIC_COL_COUNT];
            data[0] = "Average frequency";
            data[4] = df.format(statsSum[6] / (double)statsSum[0]);    //pocet palindromu / pocet bp celkem
            data[5] = df.format(statsSum[7] / (double)statsSum[0]);    //pocet palindromu nad 8 / pocet bp celkem
            data[6] = df.format(statsSum[8] / (double)statsSum[0]);    //pocet palindromu nad 10 / pocet bp celkem
            data[7] = df.format(statsSum[9] / (double)statsSum[0]);    //pocet palindromu nad 12 / pocet bp celkem
            statsReport.writeNext(data);

            statsReport.close();
        }
    }

}
