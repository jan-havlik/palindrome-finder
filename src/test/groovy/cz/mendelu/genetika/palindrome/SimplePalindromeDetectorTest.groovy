package cz.mendelu.genetika.palindrome

import cz.mendelu.genetika.genoms.Sequence
import cz.mendelu.genetika.genoms.SequenceTest

/**
 * Created by xkoloma1 on 27. 7. 2015.
 *
 *
 * Dobrı den,

 posílám krátké instrukce a data pro první typ analızy DNA sekvencí.

 Jak jsme se domlouvali, tak bychom nejprve vyzkoušeli "predikci køíovıch struktur" - a program by tedy mìl umìt
 zanalizovat pøítomnost obrácenıch repetic v genomu - èi v libovolnıch sekvencích DNA.
 Pro jistotu bych se pokusil co nejpøesnìji definovat obrázenou repetici. Pøímá repetice je jasná - to je nìjakı
 opakující se motiv - napø. v sekvenci : ACGTACG - se opakuje sekvence ACT:  ACGTACG . U DNA se køíové struktury mohou
 tvoøit z "obrácenıch repetic" - protoe je DNA dvouøetìzcová - a vytvoøení "køíové struktury" je nutná - identická
 sekvence-ale v opaèném poøadí - na druhém øetìzci DNA: inverzní repetice by tedy vypadala napøíklad takto: ACGTCGT

 protoe kdy bychom zapsali oba øetìzce, tak by to vypadalo takto:
 ACGTCGT
 TGCAGCA
 tedy sekvece tøí nukleotidù prvního øetìzce zleva - je identická se sekvencí druhého (komplemantárního
 antiparalelního-oficiální pojmy...) zprava! Pøesnì to je inverzní repetice!

 Názornì je to ukázáno na tomto obrázku:
 http://www.biomedcentral.com/1471-2199/12/33/figure/F1

 Teoreticky a matematicky mùe mít obrácená repetice minimální délku 2 nukleotidy AC-GT, nicménì prakticky se
 pøedpokládá, e minimální délka pro vznik pøíové struktury z DNA je 6 nukleotidová obrácená repetice. (byl bych pro
 udìlat program obecnì a klidnì matematicky na základì analızy charakterizovat a srovnat rùzné genomy ...) . Optimální
 délka je 7-30 nukleotidù, jsou známé inverzní repetice i o délkách nìkolik desítek a stovek nukleotidù - ty zpravidla
 pùsobí nestabilitu genomu, èasto jsou souèástí transpozomù, aj ...

 Z hlediska programování jsou esenciální ještì další 2 aspekty
 A) délka mezery mezi sekvencemi s inverzní repeticí - v našem pøíkladu ACGTCGT oddìluje inverzí repetici 1
    nukleotid (T), principiálnì mohou bıt inverzní repetice pøímé - tedy bez jakéhokoli oddìlení (ACGCGT) - nebo s rùznì
    dlouhım oddìlením jinou sekvencí (v pøíkladu na obrázku 4 nukleotidy) - pokud bychom neuvaovali monost vzniku jiné
    struktury, tak èím delší "spacer"- oddìlení inverzní repetice - tím meší pravdìpodobnost, e se tyto sekvence k sobì
    dostanou a vytvoøí køíovou strukturu
 B) èím je sekvence inverzní repetice delší, tím vzniká stabilnìjší køíová struktura - tato struktura mùe tedy
    vzniknout i v pøípadì, e inverzní repetice není dokonalá (obsahuje tzv. mismatch) - tedy 1 èi více bazí není
    komplementární a je tam jednoduše øeèeno "chyba"- na pøíkladu pøímé repetice to bude zøejmé : ACGTACG - je pøíma
    repetice 3 nukleotidù ACGTAGG - pøi zmìnì jednoho nukleotidu - je to stále pøímá repetice 3 nukleotidù - s jednou
    "chybou" tzv. mismatchem - stejnì tak to funguje u inverzních repetic. Sekvence 7 nukleotidù v inverzní repetici
    mùe vytvoøit køíovou strukturu, ale køíovou strukturu mùe také vytvoøit sekvence 12 nukleotidù v nedokonalé
    inverzní repetici se 2 chybami- mismachi.

 Proto by vlastnì vıstupem analızy mìlo bıt:
 místo inverzní repetice a
 délka inverzní repetice - poèet mismachù - délka mezery mezi sekvencemi
 tedy u pøíkladu z obrázku
 http://www.biomedcentral.com/1471-2199/12/33/figure/F1
 to je:
 7-0-4

 (dle jednotlivıch parametrù potom budeme moci IR sortovat do skupin a dle modelu m-fold spoèítat pro jejich sekvenci
 energii pro jejich vznik ...
 V databázích je vdy uveden pouze 1 øetìzec DNA - vis soubory v pøíloze - druhı øetìzec je mono doplnit dle pravidla
 párování bazí A-T C-G - take se neuvádí.

 Doufám, e se v tom dá aspoò trošku vyznat. Prosím dejte mi vìdìt, jestli to došlo - a jakékoli nejasnosti - a
 nejasnosti vèas odladíme. Pøíští tıden mám dovolenou, ale obèas na e-mailu budu, tento tıden a prvních 14 dní v srpnu
 budu dostupnı i pøes telefon èi mùeme nìkdy osobnì probrat.

 S díky

 Václav Brázda
 */

class SimplePalindromeDetectorTest extends groovy.util.GroovyTestCase {

    static {
        SequenceTest.stringAsSequence();
    }

    void testFindPalindrome() {
        PalindromeDetector detector = new SimplePalindromeDetector(7,4,0);
        PalindromeMatcher matcher = detector.findPalindrome("GAACATGTCCCAACATGTTG" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "AACATGT" as Sequence;
        assert palindrome.opposite ==  "ACATGTT" as Sequence;
        assert palindrome.position == 1;
    }

    void testFindPalindrome2() {
        PalindromeDetector detector = new SimplePalindromeDetector(10,0,0);
        PalindromeMatcher matcher = detector.findPalindrome("GAGACATGCCTAGGCATGTCTG" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "AGACATGCCT" as Sequence;
        assert palindrome.opposite ==  "AGGCATGTCT" as Sequence;
        assert palindrome.position == 1;
    }

    void testFindPalindrome3() {
        PalindromeDetector detector = new SimplePalindromeDetector(8,6,0);
        PalindromeMatcher matcher = detector.findPalindrome("AACGCGGAGACATGCCTGGGGGGAGGCATGTCTGGGCGGG" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "ACATGCCT" as Sequence;
        assert palindrome.opposite ==  "AGGCATGT" as Sequence;
        assert palindrome.position == 9;
    }

    void testFindPalindrome_Whole() {
        PalindromeDetector detector = new SimplePalindromeDetector(4,0,0);
        PalindromeMatcher matcher = detector.findPalindrome("AAAATTTT" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "AAAA" as Sequence;
        assert palindrome.position == 0;
    }

    void testFindPalindrome_Mismatche() {
        PalindromeDetector detector = new SimplePalindromeDetector(4,0,1);
        PalindromeMatcher matcher = detector.findPalindrome("AAAATTCT" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "AAAA" as Sequence;
        assert palindrome.opposite ==  "TTCT" as Sequence;
        assert palindrome.position == 0;

    }



}
