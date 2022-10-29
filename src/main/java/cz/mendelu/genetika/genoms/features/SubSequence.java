package cz.mendelu.genetika.genoms.features;

/**
 * interface pro podsekvence
 *
 * ma pozici a delku
 *
 * Created by Jiří Lýsek on 14.12.2016.
 */
public interface SubSequence {

    /**
     * velikost dane feature, u palindromu napr. jen size (bez druhe casti a spaceru)
     * @return
     */
    public int getSize();

    /**
     * vraci celou delku podsekvence, kterou zabira v sekvenci
     *
     * u palindromu napr. 2*size + spacer
     *
     * @return
     */
    public int getLength();

    /**
     * vraci pozici, na ktere zacina
     *
     * @return
     */
    public int getPosition();

    /**
     * vraci > 0 pro smer na hlavnim DNA retezci
     * vraci < 0 pro smer na doplnkovem DNA retezci
     * vraci 0, pokud je to jedno (palindrom)
     *
     * @return
     */
    public int getDirection();

}
