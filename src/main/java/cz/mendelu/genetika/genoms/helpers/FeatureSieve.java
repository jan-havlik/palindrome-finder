package cz.mendelu.genetika.genoms.helpers;

import cz.mendelu.genetika.genoms.features.SubSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * sito pro hledani mist zajmu v okoli, rozradi ostatni sievedFeatures v okoli daneho do 3 skupin:
 * - dana feature primo obsahuje jinou
 * - jina feature je v blizkem okoli
 * - jina feature je ve vzdalenejsim okoli
 *
 * Created by Jiří Lýsek on 14.12.2016.
 */
public class FeatureSieve<T extends SubSequence> {

    private HashMap<Integer, List<T>> sievedFeatures = new HashMap<>();

    private SubSequence place;

    public FeatureSieve(SubSequence place) {
        this.place = place;
    }

    public void addRange(int size) {
        sievedFeatures.put(size, new ArrayList<T>());
    }

    /**
     * Rozhodne, jestli je palindrom v blizkem/dalekem okoli nebo se prekryva s mistem prediktoru
     *
     * @param feature
     * @return
     */
    public boolean decideMembership(T feature) {
        return decideMembership(feature, 0);
    }

    public boolean decideMembership(T feature, int offset) {
        for(Integer range : sievedFeatures.keySet()) {
            if(SequenceHelper.isInRange(place, feature, range, offset)) {
                List<T> features = sievedFeatures.get(range);
                features.add(feature);
                return true;
            }
        }
        return false;
    }

    public int getCount() {
        List<T> features = sievedFeatures.get(0);
        return features.size();
    }

    public int getCount(int range) {
        List<T> features = sievedFeatures.get(range);
        return features.size();
    }

    public int getCount(int range, int minLen) {
        List<T> features = sievedFeatures.get(range);
        return (int) features.stream().filter(f -> f.getSize() >= minLen).count();
    }

    public int getCountBefore(int range) {
        List<T> features = sievedFeatures.get(range);
        return (int) features.stream().filter(f -> fetatureIsBefore(f)).count();
    }

    public int getCountBefore(int range, int minLen) {
        List<T> features = sievedFeatures.get(range);
        return (int) features.stream().filter(f -> fetatureIsBefore(f) && f.getSize() >= minLen).count();
    }

    public int getCountAfter(int range) {
        List<T> features = sievedFeatures.get(range);
        return (int) features.stream().filter(f -> !fetatureIsBefore(f)).count();
    }

    public int getCountAfter(int range, int minLen) {
        List<T> features = sievedFeatures.get(range);
        return (int) features.stream().filter(f -> !fetatureIsBefore(f) && f.getSize() >= minLen).count();
    }

    private boolean fetatureIsBefore(T f) {
        if(place.getDirection() > 0) {
            return place.getPosition() > f.getPosition();
        } else if(place.getDirection() < 0) {
            return place.getPosition() + place.getLength() < f.getPosition();
        }
        return true;
    }

}
