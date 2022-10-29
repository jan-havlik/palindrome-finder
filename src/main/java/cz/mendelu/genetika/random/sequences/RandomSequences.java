package cz.mendelu.genetika.random.sequences;

import cz.mendelu.genetika.genoms.Sequence;
import cz.mendelu.genetika.palindrome.NumberRange;
import cz.mendelu.genetika.palindrome.PalindromeDetector;
import cz.mendelu.genetika.palindrome.PalindromeDetectorBuilder;
import cz.mendelu.genetika.rest.jetty.JettyContext;

import java.util.Random;

public class RandomSequences {

    private static char[] lettersCG50 = new char[]{'A', 'C', 'G', 'T'};
    private static char[] lettersCG20 = new char[]{'C', 'G', 'A', 'T', 'A', 'T', 'A', 'T', 'A', 'T'};
    private static char[] lettersCG40 = new char[]{'C', 'G', 'C', 'G', 'A', 'T', 'A', 'T', 'A', 'T'};
    private static char[] lettersCG60 = new char[]{'C', 'G', 'C', 'G', 'C', 'G', 'A', 'T', 'A', 'T'};
    private static char[] lettersCG80 = new char[]{'C', 'G', 'C', 'G', 'C', 'G', 'C', 'G', 'A', 'T'};

    private static int MIN_LEN = 10;
    private static int MAX_LEN = 10;
    private static int N = 1;  //pocet mereni

    private static int AMOUNT = 1000;   //velikost sady
    private static int LEN = 1000;  //delka sekvence

    private PalindromeDetectorBuilder palindromeDetectorBuilder = JettyContext.getPalindromeDetectorBuilder();

    private PalindromeDetector makePalindromeDetector(int min, int max) {
        NumberRange size = new NumberRange(min, max);
        NumberRange spacer = new NumberRange("0");
        NumberRange mismatches = new NumberRange("0,1");
        PalindromeDetector pd = palindromeDetectorBuilder.getPalindromeDetector(size.getValues(), spacer.getValues(), mismatches.getValues());
        pd.setCycleMode(false);
        pd.setAtatFilter(true);
        return pd;
    }

    private String[] generate(int n, int len, char[] letters) {
        Random rand = new Random();

        String[] seqList = new String[n];
        for(int i = 0; i < n; i++) {
            seqList[i] = "";
            for(int j = 0; j < len; j++) {
                int p = rand.nextInt(letters.length);
                seqList[i] += letters[p];
            }
        }
        return seqList;
    }

    private int[] findPalindromes(String[] sequences, int min, int max) {
        PalindromeDetector pd = makePalindromeDetector(min, max);
        int[] ret = new int[sequences.length];
        for(int i = 0; i < sequences.length; i++) {
            ret[i] = pd.findPalindrome(new Sequence(sequences[i])).getCount();
        }
        return ret;
    }

    private int countCG(String sequence) {
        int ret = 0;
        for(int i = 0; i < sequence.length(); i++) {
            if(sequence.charAt(i) == 'C' || sequence.charAt(i) == 'G') {
                ret++;
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        RandomSequences rs = new RandomSequences();
        for(int L = MIN_LEN; L <= MAX_LEN; L++) {
            System.out.print(L);
            for(int M = 1; M <= N; M++) {

                String[] sequences = rs.generate(AMOUNT, LEN, lettersCG50);
                int[] counts = rs.findPalindromes(sequences, L, L);

                int cnt = 0;
                int allCnt = 0;
                int CGcountAll = 0;
                for(int i = 0; i < AMOUNT; i++) {
                    allCnt += counts[i];
                    if(counts[i] > 0) {
                        cnt++;
                    }
                    int CGcount = rs.countCG(sequences[i]);
                    CGcountAll += CGcount;
                    //System.out.println(sequences[i] + "," + counts[i] + "," + CGcount);
                }
                //System.out.println("Sequences with IR: " + cnt + "/" + N);
                //System.out.println("IR found: " + allCnt);
                //System.out.println("Seq. with IR ratio: " + ((double)cnt / AMOUNT * 100));
                //System.out.println("IR ratio per 1000bp: " + (((double)allCnt / (AMOUNT * LEN)) * 1000));
                //System.out.println("CG ratio: " + ((double)CGcountAll / (AMOUNT * LEN) * 100));
                System.out.print(", " + allCnt);
            }
            System.out.println();
        }
    }

}
