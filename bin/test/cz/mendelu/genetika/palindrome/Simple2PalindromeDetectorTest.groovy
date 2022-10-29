package cz.mendelu.genetika.palindrome

import cz.mendelu.genetika.genoms.Sequence
import cz.mendelu.genetika.genoms.SequenceTest

/**
 * Created by xkoloma1 on 27. 7. 2015.
 *
 *
 * Dobr� den,

 pos�l�m kr�tk� instrukce a data pro prvn� typ anal�zy DNA sekvenc�.

 Jak jsme se domlouvali, tak bychom nejprve vyzkou�eli "predikci k��ov�ch struktur" - a program by tedy m�l um�t
 zanalizovat p��tomnost obr�cen�ch repetic v genomu - �i v libovoln�ch sekvenc�ch DNA.
 Pro jistotu bych se pokusil co nejp�esn�ji definovat obr�zenou repetici. P��m� repetice je jasn� - to je n�jak�
 opakuj�c� se motiv - nap�. v sekvenci : ACGTACG - se opakuje sekvence ACT:  ACGTACG . U DNA se k��ov� struktury mohou
 tvo�it z "obr�cen�ch repetic" - proto�e je DNA dvou�et�zcov� - a vytvo�en� "k��ov� struktury" je nutn� - identick�
 sekvence-ale v opa�n�m po�ad� - na druh�m �et�zci DNA: inverzn� repetice by tedy vypadala nap��klad takto: ACGTCGT

 proto�e kdy� bychom zapsali oba �et�zce, tak by to vypadalo takto:
 ACGTCGT
 TGCAGCA
 tedy sekvece t�� nukleotid� prvn�ho �et�zce zleva - je identick� se sekvenc� druh�ho (komplemant�rn�ho
 antiparaleln�ho-ofici�ln� pojmy...) zprava! P�esn� to je inverzn� repetice!

 N�zorn� je to uk�z�no na tomto obr�zku:
 http://www.biomedcentral.com/1471-2199/12/33/figure/F1

 Teoreticky a matematicky m��e m�t obr�cen� repetice minim�ln� d�lku 2 nukleotidy AC-GT, nicm�n� prakticky se
 p�edpokl�d�, �e minim�ln� d�lka pro vznik p��ov� struktury z DNA je 6 nukleotidov� obr�cen� repetice. (byl bych pro
 ud�lat program obecn� a klidn� matematicky na z�klad� anal�zy charakterizovat a srovnat r�zn� genomy ...) . Optim�ln�
 d�lka je 7-30 nukleotid�, jsou zn�m� inverzn� repetice i o d�lk�ch n�kolik des�tek a� stovek nukleotid� - ty zpravidla
 p�sob� nestabilitu genomu, �asto jsou sou��st� transpozom�, aj ...

 Z hlediska programov�n� jsou esenci�ln� je�t� dal�� 2 aspekty
 A) d�lka mezery mezi sekvencemi s inverzn� repetic� - v na�em p��kladu ACGTCGT odd�luje inverz� repetici 1
    nukleotid (T), principi�ln� mohou b�t inverzn� repetice p��m� - tedy bez jak�hokoli odd�len� (ACGCGT) - nebo s r�zn�
    dlouh�m odd�len�m jinou sekvenc� (v p��kladu na obr�zku 4 nukleotidy) - pokud bychom neuva�ovali mo�nost vzniku jin�
    struktury, tak ��m del�� "spacer"- odd�len� inverzn� repetice - t�m me�� pravd�podobnost, �e se tyto sekvence k sob�
    dostanou a vytvo�� k��ovou strukturu
 B) ��m je sekvence inverzn� repetice del��, t�m vznik� stabiln�j�� k��ov� struktura - tato struktura m��e tedy
    vzniknout i v p��pad�, �e inverzn� repetice nen� dokonal� (obsahuje tzv. mismatch) - tedy 1 �i v�ce baz� nen�
    komplement�rn� a je tam jednodu�e �e�eno "chyba"- na p��kladu p��m� repetice to bude z�ejm� : ACGTACG - je p��ma
    repetice 3 nukleotid� ACGTAGG - p�i zm�n� jednoho nukleotidu - je to st�le p��m� repetice 3 nukleotid� - s jednou
    "chybou" tzv. mismatchem - stejn� tak to funguje u inverzn�ch repetic. Sekvence 7 nukleotid� v inverzn� repetici
    m��e vytvo�it k��ovou strukturu, ale k��ovou strukturu m��e tak� vytvo�it sekvence 12 nukleotid� v nedokonal�
    inverzn� repetici se 2 chybami- mismachi.

 Proto by vlastn� v�stupem anal�zy m�lo b�t:
 m�sto inverzn� repetice a
 d�lka inverzn� repetice - po�et mismach� - d�lka mezery mezi sekvencemi
 tedy u p��kladu z obr�zku
 http://www.biomedcentral.com/1471-2199/12/33/figure/F1
 to je:
 7-0-4

 (dle jednotliv�ch parametr� potom budeme moci IR sortovat do skupin a dle modelu m-fold spo��tat pro jejich sekvenci
 energii pro jejich vznik ...
 V datab�z�ch je v�dy uveden pouze 1 �et�zec DNA - vis soubory v p��loze - druh� �et�zec je mo�no doplnit dle pravidla
 p�rov�n� baz� A-T C-G - tak�e se neuv�d�.

 Douf�m, �e se v tom d� aspo� tro�ku vyznat. Pros�m dejte mi v�d�t, jestli to do�lo - a jak�koli nejasnosti - a�
 nejasnosti v�as odlad�me. P��t� t�den m�m dovolenou, ale ob�as na e-mailu budu, tento t�den a prvn�ch 14 dn� v srpnu
 budu dostupn� i p�es telefon �i m��eme n�kdy osobn� probrat.

 S d�ky

 V�clav Br�zda
 */

class Simple2PalindromeDetectorTest extends groovy.util.GroovyTestCase {

    static {
        SequenceTest.stringAsSequence();
    }

    void testFindPalindrome() {
        PalindromeDetector detector = new Simple2PalindromeDetector(5, (0..5) as SortedSet, (0..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCCCCAAAAACTTTTTCCCCCCCCCCCCCCTTTTTCCCAAGAACCCCC" as Sequence);
        Iterator<Palindrome> iterator = matcher.iterator();
        Palindrome palindrome;

        assert matcher.count == 2;

        palindrome = iterator.next();
        assert palindrome.sequence == "AAAAA" as Sequence;
        assert palindrome.opposite ==  "TTTTT" as Sequence;
        assert palindrome.position == 5;
        assert palindrome.spacer.length == 1;
        assert palindrome.mismatches == 0;

        palindrome = iterator.next();
        assert palindrome.sequence == "TTTTT" as Sequence;
        assert palindrome.opposite ==  "AAGAA" as Sequence;
        assert palindrome.position == 30;
        assert palindrome.spacer.length == 3;
        assert palindrome.mismatches == 1;
    }

    void testFindPalindrome_RNA() {
        PalindromeDetector detector = new Simple2PalindromeDetector(5, (0..5) as SortedSet, (0..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("CCCCCAAAAACUTUUUCCCCCCCCCCCCCCUUUUUCCCAAGAACCCCC" as Sequence);
        Iterator<Palindrome> iterator = matcher.iterator();
        Palindrome palindrome;

        assert matcher.count == 2;

        palindrome = iterator.next();
        assert palindrome.sequence == "AAAAA" as Sequence;
        assert palindrome.opposite ==  "UTUUU" as Sequence;
        assert palindrome.position == 5;
        assert palindrome.spacer.length == 1;
        assert palindrome.mismatches == 0;

        palindrome = iterator.next();
        assert palindrome.sequence == "UUUUU" as Sequence;
        assert palindrome.opposite ==  "AAGAA" as Sequence;
        assert palindrome.position == 30;
        assert palindrome.spacer.length == 3;
        assert palindrome.mismatches == 1;
    }

    void testFindPalindrome_Whole() {
        PalindromeDetector detector = new Simple2PalindromeDetector(4, (0..5) as SortedSet, (0..3) as SortedSet);
        PalindromeMatcher matcher = detector.findPalindrome("AAAATTTT" as Sequence);
        assert matcher.count == 1;

        Palindrome palindrome = matcher.iterator().next();
        assert palindrome.sequence == "AAAA" as Sequence;
        assert palindrome.position == 0;
        assert palindrome.spacer.length == 0;
        assert palindrome.mismatches == 0;
    }

    void testCyclePalindrome() {
        PalindromeDetector detector = new Simple2PalindromeDetector(10, (0..0) as SortedSet, (0..0) as SortedSet);
        detector.setCycleMode(true);
        PalindromeMatcher matcher = detector.findPalindrome("TGCCTNAGGCATGTCTAGACA" as Sequence);
        Iterator<Palindrome> iterator = matcher.iterator();
        Palindrome palindrome;

        assert matcher.count == 1;

        palindrome = iterator.next();
        assert palindrome.sequence == "AGGCATGTCT" as Sequence;
        assert palindrome.opposite ==  "AGACATGCCT" as Sequence;
        assert palindrome.position == 6;
        assert palindrome.spacer.length == 0;
        assert palindrome.mismatches == 0;
    }
    
}
