package cz.mendelu.genetika.palindrome;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xkoloma1 on 28. 7. 2015.
 */
public class NumberRange {

    private static final Pattern RE_NUMBER = Pattern.compile("^\\d+$");
    private static final Pattern RE_RANGE = Pattern.compile("^(\\d+)-(\\d+)$");

    private final String input;
    private final NavigableSet<Integer> values;

    public NumberRange(int min, int max) {
        this.input = min + "-" + max;
        this.values = new TreeSet<>();

        for (int i = min; i <= max; i++) {
            values.add(i);
        }
    }

    public NumberRange(String input) {
        this.input = input.trim();
        this.values = new TreeSet<>();

        String[] parts = input.trim().split("\\s*,\\s*");
        Matcher matcher;
        for (String part : parts) {
            matcher = RE_NUMBER.matcher(part);
            if (matcher.find()) {
                values.add(Integer.parseInt(part));
                continue;
            }

            matcher = RE_RANGE.matcher(part);
            if (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                int max = Integer.parseInt(matcher.group(2));
                for (int i = min; i <= max; i++) {
                    values.add(i);
                }
                continue;
            }
        }
    }

    public NavigableSet<Integer> getValues() {
        return Collections.unmodifiableNavigableSet(values);
    }

    public String toString() {
        return input;
    }
}
