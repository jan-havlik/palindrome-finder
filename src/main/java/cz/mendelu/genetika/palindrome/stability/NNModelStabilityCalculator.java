package cz.mendelu.genetika.palindrome.stability;

import cz.mendelu.genetika.palindrome.Palindrome;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;

/**
 * Created by xkoloma1 on 19.11.2015.
 */
public class NNModelStabilityCalculator implements StabilityCalculator {

    private static final Map<String, Double> table = new HashMap<>();
    static {
        table.put("AA/TT", -1.00);
        table.put("AT/TA", -0.88);
        table.put("AC/TG", -1.45);
        table.put("AG/TC", -1.00);

        table.put("TA/AT", -0.58);
        table.put("TT/AA", -1.00);
        table.put("TC/AG", -1.00);
        table.put("TG/AC", -1.45);

        table.put("CA/GT", -1.45);
        table.put("CT/GA", -1.28);
        table.put("CC/GG", -1.84);
        table.put("CG/GC", -2.17);

        table.put("GA/CT", -1.30);
        table.put("GT/CA", -1.44);
        table.put("GC/CG", -2.24);
        table.put("GG/CC", -1.84);

        table.put("G-C", 0.98);
        table.put("C-G", 0.98);
        table.put("A-T", 1.03);
        table.put("T-A", 1.03);
    }

    @Override
    public double calculateCruciForm(Palindrome palindrome) {
        return calculate(palindrome.iterator());
    }

    @Override
    public double calculateLinearForm(Palindrome palindrome) {
        return calculate(palindrome.linearFormIterator());
    }

    private double calculate(Iterator<Palindrome.Entry> i) {
        Palindrome.Entry first = i.next();
        Palindrome.Entry second;

        double sum = table.getOrDefault(first.toString(), 0.0);
        while (i.hasNext()) {
            second = i.next();
            String key = makeKey(first, second);
            double value = table.getOrDefault(key, 0.0);
            sum += value;
            first = second;
        }
        sum += table.getOrDefault(first.toString(), 0.0);
        return sum;
    }

    private String makeKey(Palindrome.Entry first, Palindrome.Entry second) {
        return first.getNuclid()
                + second.getNuclid()
                + "/"
                + first.getOposite()
                + second.getOposite();
    }
}
