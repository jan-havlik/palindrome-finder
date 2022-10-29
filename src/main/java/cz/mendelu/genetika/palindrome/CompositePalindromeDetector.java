package cz.mendelu.genetika.palindrome;

import cz.mendelu.genetika.genoms.Sequence;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xkoloma1 on 06.10.2015.
 */
public class CompositePalindromeDetector implements PalindromeDetector {

    public static PalindromeDetectorBuilder palindromeDetectorBuilder() {
            return (size, spacers, mismatches) -> {
                List<PalindromeDetector> palindromeDetectors = size.stream()
                        .map(s -> new Simple2PalindromeDetector(s, spacers, mismatches))
                        .collect(Collectors.toList());

                AtatPalindromeDetector atatPalindromeDetector = new AtatPalindromeDetector(size, spacers);

                return new CompositePalindromeDetector(palindromeDetectors, atatPalindromeDetector);
            };
        }

    private List<PalindromeDetector> palindromeDetectors;
    private AtatPalindromeDetector atatPalindromeDetector;
    private boolean usadAtatPalindromeDetector = true;

    private PalindromeMergeEvalutor evalutor = (f, s) -> f.getSize() - f.getMismatches() <= s.getSize() - s.getMismatches();

    public CompositePalindromeDetector(List<PalindromeDetector> palindromeDetectors,  AtatPalindromeDetector atatPalindromeDetector) {
        this.palindromeDetectors = palindromeDetectors;
        this.atatPalindromeDetector = atatPalindromeDetector;
    }

    @Override
    public PalindromeMatcher findPalindrome(Sequence sequence) {
        PalindromeMatcher result = palindromeDetectors.parallelStream()
                .map(pd -> pd.findPalindrome(sequence)) /* VYBER JEDEN Z NASLEDUJICH DVOU RADKU */
                .reduce(null, (a, b) -> merge(a, b));   // Redukce podle stredu palindromu
                //.reduce(null, (a, b) -> sum(a, b));   // Jednoduche spojeni vsech vysledku do jednoho za ucelem demonstrace na clanek
        if (usadAtatPalindromeDetector)
            result = merge(result, atatPalindromeDetector.findPalindrome(sequence));

        Map<Object, Palindrome> filter = spaceRiseFilter(result);
        return new MergePalindromeMatcher(filter);
    }

    /**
     * Tento filter odstraní palindromy, které jsou na stejném místě a vnikly nárustem spaceru. Tedy ebě jejich části
     * (sekvence i oposite) začínají na stejnmém místě.
     * @param result
     * @return
     */
    private Map<Object, Palindrome> spaceRiseFilter(PalindromeMatcher result) {
        Map<Object, Palindrome> filter = new HashMap<>();
        result.forEach(palindrome ->  {
            int palindromeStart = palindrome.getPosition();
            int oppositeStart = palindromeStart + palindrome.getSequence().getLength() + palindrome.getSpacer().getLength();
            FilterSignature signature = new FilterSignature(palindromeStart, oppositeStart);
            // Otestuji, jestli mam jiz ulozeny podezdeli palindrom,
            if (filter.containsKey(signature)) { // Pokud mam, tak vyhodnotim ten lepsi
                Palindrome oldPalindrome = filter.get(signature);
                if (palindrome.getSequence().getLength() > oldPalindrome.getSequence().getLength()) {
                    filter.put(signature, palindrome);
                }
            } else { // Jinak pouze pridam do mapy
                filter.put(signature, palindrome);
            }
        });
        return filter;
    }


    @Override
    public void setCycleMode(boolean cycleMode) {
        palindromeDetectors.forEach(p -> p.setCycleMode(cycleMode));
    }

    @Override
    public void setAtatFilter(boolean atatFilter) {
        palindromeDetectors.forEach(p -> p.setAtatFilter(atatFilter));
        usadAtatPalindromeDetector = atatFilter;
    }

    private MergePalindromeMatcher merge(PalindromeMatcher a, PalindromeMatcher b) {
        MergePalindromeMatcher result = new MergePalindromeMatcher();
        if (a != null) {
            result.merge(a);
        }
        if (b != null) {
            result.merge(b);
        }
        return result;
    }

    private PalindromeMatcher sum(PalindromeMatcher a, PalindromeMatcher b) {
        return new PalindromeMatcher() {
            private List<Palindrome> sum = new LinkedList<>();
            {
                if (a != null) a.forEach(p -> sum.add(p));
                if (b != null) b.forEach(p -> sum.add(p));
            }

            @Override
            public int getCount() {
                return sum.size();
            }

            @Override
            public Stream<Palindrome> stream() {
                return sum.stream();
            }

            @Override
            public Iterator<Palindrome> iterator() {
                return sum.iterator();
            }
        };
    }

    class MergePalindromeMatcher implements PalindromeMatcher {

        private Map<Object, Palindrome> map = new HashMap<>();

        public MergePalindromeMatcher() {}

        public MergePalindromeMatcher(Map<Object, Palindrome> map) {
            this.map = map;
        }

        private void merge(PalindromeMatcher matcher) {
            matcher.forEach(p -> {
                if (map.containsKey(p.getMidlePosition())) {
                    Palindrome o = map.get(p.getMidlePosition());
                    if (evalutor.evaluate(o, p)) {
                        map.put(p.getMidlePosition(), p);
                    }
                } else {
                    map.put(p.getMidlePosition(), p);
                }
            });
        }


        @Override
        public int getCount() {
            return map.size();
        }

        @Override
        public Stream<Palindrome> stream() {
            return map.values().stream();
        }

        @Override
        public Iterator<Palindrome> iterator() {
            return map.values().iterator();
        }
    }

    public interface PalindromeMergeEvalutor {

        boolean evaluate(Palindrome first, Palindrome second);

    }

    private static final class FilterSignature {

        public final int palindromeStart;
        public final int oppositeStart;

        public FilterSignature(int palindromeStart, int oppositeStart) {
            this.palindromeStart = palindromeStart;
            this.oppositeStart = oppositeStart;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FilterSignature that = (FilterSignature) o;
            return palindromeStart == that.palindromeStart &&
                    oppositeStart == that.oppositeStart;
        }

        @Override
        public int hashCode() {
            return Objects.hash(palindromeStart, oppositeStart);
        }
    }
}
