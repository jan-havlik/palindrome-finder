package cz.mendelu.genetika.genoms.helpers;

import cz.mendelu.genetika.palindrome.PalindromeMatcher;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * pomocne funkce nad PalindromeMatcher-em
 *
 * Created by Jiří Lýsek on 28.11.2016.
 */
public class PalindromeMatcherHelper {

    public static Long getCount(PalindromeMatcher palindromes, int minLen) {
        return palindromes.stream().filter(palindrome -> {
            return palindrome.getSize() >= minLen;
        }).collect(Collectors.counting());
    }

    public static Long getCount(PalindromeMatcher palindromes, int minLen, int maxLen) {
        return palindromes.stream().filter(palindrome -> {
            return palindrome.getSize() >= minLen && palindrome.getSize() <= maxLen;
        }).collect(Collectors.counting());
    }

    /**
     * vypocte pocty palindromu pro ruzne delky
     *
     * @param pm
     * @return
     */
    public static Map<Integer, Integer> calcHistogramLength(PalindromeMatcher pm) {
        SortedMap<Integer, Integer> hist = new TreeMap<Integer, Integer>();
        pm.stream().forEach(p -> {
            Integer size = p.getSize();
            Integer old = hist.get(size);
            hist.put(size, old != null ? old + 1 : 1);
        });
        return hist;
    }

}
