package org.columbia.adb.queryexpansionservice.queryexpander.comparator;

import java.util.Comparator;
import java.util.Map;

public class TermComparatorBasedOnTFIDF implements Comparator<String> {

    private Map<String, Double> tfidf;

    public TermComparatorBasedOnTFIDF(Map<String, Double> queryVectorTfIdf) {
        this.tfidf = queryVectorTfIdf;
    }

    @Override
    public int compare(String t1, String t2) {
        if (tfidf.get(t2) > tfidf.get(t1))
            return 1;
        else if (tfidf.get(t2) < tfidf.get(t1))
            return -1;
        else
            return 0;
    }

}