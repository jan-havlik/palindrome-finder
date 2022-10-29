package cz.mendelu.genetika.palindrome.stability;

import cz.mendelu.genetika.palindrome.Palindrome;

/**
 * Created by xkoloma1 on 19.11.2015.
 */
public interface StabilityCalculator {

    double calculateCruciForm(Palindrome palindrome);

    double calculateLinearForm(Palindrome palindrome);
}
