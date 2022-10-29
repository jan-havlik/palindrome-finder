package cz.mendelu.genetika.genoms;

import cz.mendelu.genetika.genoms.features.SubSequence;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * https://www.ncbi.nlm.nih.gov/WebSub/html/help/feature-table.html
 *
 * Created by Jiří Lýsek on 7.12.2016.
 */
public class Feature implements SubSequence {

    public static final int COUNT_DISTANCE = 100;   //vzdalenost, ve ktere pocitat palindromy
    public static final int FORWARD = 1;
    public static final int UNKNOWN = 0;
    public static final int BACKWARD = -1;

    private List<Pair<String, String>> qualifiers = new ArrayList<Pair<String, String>>();
    private int start;
    private int end;
    private int direction;
    private String name;

    public Feature(String name, int start, int end, int direction) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.direction = direction;
    }

    public void addQualifier(String name, String value) {
        qualifiers.add(new Pair<String, String>(name, value));
    }

    @Override
    public String toString() {
        return String.format("%s <%d, %d>", name, start, end);
    }

    public int getSize() {
        return getLength();
    }

    public int getLength() {
        return end - start;
    }

    public int getPosition() {
        return start;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String concatQualifiers() {
        return qualifiers.stream().map(q -> {
            return q.getKey() + " (" + q.getValue() + ")";
        }).reduce("", (s1, s2) -> {
            return s1 + (s1.isEmpty() ? "" : ", ") + s2;
        });
    }

}
