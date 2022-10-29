package cz.mendelu.genetika.binding.predictor;

import au.com.bytecode.opencsv.CSVWriter;
import cz.mendelu.genetika.genoms.Sequence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * vypocitat maxima p53 prediktoru po stovkach
 *
 * Created by Jiří Lýsek on 8.7.2016.
 */
public class P53Energy {

    //TODO: histogram?
    public static void main(String[] args) throws IOException {
        Sequence s = new Sequence(new File("./data/homo-sapiens/hg18/chr21"));
        P53BindingPredictor predictor = new P53BindingPredictor();
        double[] p53Sites = predictor.scan(s);

        CSVWriter writer = new CSVWriter(new FileWriter("./chr21-p53-energie.csv"));
        double max = 0;
        int cnt = 100;
        for(int i = 0; i < p53Sites.length; i++) {
            if(p53Sites[i] < max) {
                max = p53Sites[i];
            }
            if(i % cnt == 0) {
                if(max < 0) {
                    String[] row = new String[]{i + "", max + ""};
                    writer.writeNext(row);
                    max = 0;
                }
            }
        }
    }

}
