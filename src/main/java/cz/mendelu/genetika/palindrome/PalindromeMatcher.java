package cz.mendelu.genetika.palindrome;

import java.util.stream.Stream;

/**
 * Created by Honza on 29. 7. 2015.
 */
public interface PalindromeMatcher extends Iterable<Palindrome> {

    int getCount();

    Stream<Palindrome> stream();

}
