package org.columbia.adb.queryexpansionservice.queryexpander;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.columbia.adb.queryexpansionservice.cache.StopWordsCache;
import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;
import org.columbia.adb.queryexpansionservice.queryexpander.comparator.TermComparatorBasedOnTFIDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/***
 * Modified form of Rocchio's algorithm for query expansion
 * 
 * @author gauravmishra
 *
 */
@Component
@Scope("prototype")
public class ModifiedRocchioQueryExpander implements QueryExpander {

    // Define constants for Rocchio algorithm
    private final static double ALPHA = 1;
    private final static double BETA = 0.9;
    private final static double GAMMA = 0.4;
    private final static double DELTA = 0.3;
    private final static Pattern VALID_STRING_PATTERN = Pattern.compile(".*[A-Za-z0-9]+.*");

    @Autowired
    private StopWordsCache stopWordsCache;

    private Set<String> initialQueryList;
    private int relevantdocs;

    private Map<Integer, Map<String, Integer>> docTermFrequencies;
    private Map<String, Set<Integer>> invertedIndex;
    private Map<String, Double> inverseDocumentFrequencies;
    private Map<Integer, Set<String>> docWordsInTitle;

    @Override
    public String expandQuery(String query,
            List<QueryResultInfo> queryResultInfoList) {

        this.initialQueryList = new HashSet<String>(Arrays.asList(query
                .split(" ")));

        initializeDocFrequenceRelevantCountAndWordsInTitle(queryResultInfoList);
        Map<String, Double> newQueryVector = computeNewQueryVector(queryResultInfoList);

        return String.join(" ", getNewQuery(newQueryVector));
    }

    private void initializeDocFrequenceRelevantCountAndWordsInTitle(
            List<QueryResultInfo> queryResultInfoList) {

        invertedIndex = new HashMap<String, Set<Integer>>();
        docTermFrequencies = new HashMap<Integer, Map<String, Integer>>();
        docWordsInTitle = new HashMap<Integer, Set<String>>();
        int i = 1;
        for (QueryResultInfo queryResultInfo : queryResultInfoList) {

            if (queryResultInfo.isRelevant()) {
                this.relevantdocs++;
            }

            Map<String, Integer> termFrequency = new HashMap<String, Integer>();

            List<String> documentTerms = Arrays.asList((queryResultInfo
                    .getTitle() + " " + queryResultInfo.getSummary())
                    .split(" "));

            for (String term : documentTerms) {

                term = term.toLowerCase();
                // Ignore stop words
                if (stopWordsCache.isStopWord(term)) {
                    continue;
                }
                Matcher matcher = VALID_STRING_PATTERN.matcher(term);
                if(!matcher.find()){
                    continue;
                }

                if (termFrequency.containsKey(term)) {
                    termFrequency.put(term, termFrequency.get(term) + 1);
                } else {
                    termFrequency.put(term, 1);
                }

                Set<Integer> documentSet;
                if (invertedIndex.containsKey(term)) {
                    documentSet = invertedIndex.get(term);
                } else {
                    documentSet = new HashSet<Integer>();
                }
                documentSet.add(i);
                invertedIndex.put(term, documentSet);
            }
            docTermFrequencies.put(i, termFrequency);

            docWordsInTitle.put(
                    i,
                    new HashSet<String>(Arrays.asList(queryResultInfo
                            .getTitle().split(" "))));
            i++;

        }

        inverseDocumentFrequencies = new HashMap<String, Double>();
        for (Map.Entry<String, Set<Integer>> entry : invertedIndex.entrySet()) {
            String term = entry.getKey();
            Double idf = Math.log(10.0 / (double) entry.getValue().size());
            inverseDocumentFrequencies.put(term, idf);
        }

    }

    // Compute the expanded query vector following Rocchio algorithm
    // new query vector = alpha * initial query vector + beta * sum(relevant
    // document vector) / number of relevant documents -
    // gamma * sum(non-relevant document vector) / number of non-relevant
    // documents
    private Map<String, Double> computeNewQueryVector(
            List<QueryResultInfo> queryResultInfoList) {

        Map<String, Double> newQueryVector = new HashMap<String, Double>();

        for (QueryResultInfo queryResultInfo : queryResultInfoList) {

            Map<String, Integer> tf = docTermFrequencies.get(queryResultInfo
                    .getId());
            if (queryResultInfo.isRelevant()) { // Relevant

                for (String term : tf.keySet()) {

                    double val = BETA / relevantdocs * tf.get(term)
                            * inverseDocumentFrequencies.get(term);

                    if (newQueryVector.containsKey(term)) {
                        newQueryVector
                                .put(term, newQueryVector.get(term) + val);
                    } else {
                        newQueryVector.put(term, val);
                    }

                    if (docWordsInTitle.get(queryResultInfo.getId()).contains(
                            term)) {
                        newQueryVector.put(term, newQueryVector.get(term)
                                + DELTA);
                    }
                }
            } else { // Non-relevant document
                for (String term : tf.keySet()) {
                    double val = GAMMA / (10 - relevantdocs) * tf.get(term)
                            * inverseDocumentFrequencies.get(term);
                    if (newQueryVector.containsKey(term))
                        newQueryVector
                                .put(term, newQueryVector.get(term) - val);
                    else
                        newQueryVector.put(term, -1*val);
                }
            }
        }
        // Adding
        for (String term : initialQueryList) {
            Double termValue = ALPHA;
            Double queryVectorValue = newQueryVector.get(term.toLowerCase());
            if (queryVectorValue != null) {
                newQueryVector.remove(term.toLowerCase());
                termValue += queryVectorValue;
            }
            newQueryVector.put(term, termValue);
        }
        return newQueryVector;

    }

    private List<String> getNewQuery(Map<String, Double> newQueryVector) {
        // Sort the terms in the new query vector by tf-idf
        List<String> sortedTerms = new ArrayList<String>(
                newQueryVector.keySet());
        Collections
                .sort(sortedTerms, new TermComparatorBasedOnTFIDF(newQueryVector));

        int numberOfTermsToBeAdded = 2;
        // Get new query terms starting from the most weighted
        int countOfNewTermsAdded = 0;
        int countOfOriginalQueryTermsAdded = 0;
        List<String> newQuery = new ArrayList<String>();
        // Keeps adding terms until the number of terms in the new query is
        // numberOfTermsToBeAdded
        // more than the initial query
        for (int i = 0; (countOfNewTermsAdded + countOfOriginalQueryTermsAdded) < initialQueryList
                .size() + numberOfTermsToBeAdded; i++) {
            String term = sortedTerms.get(i);

            if (!initialQueryList.contains(term)) {
                if (countOfOriginalQueryTermsAdded < numberOfTermsToBeAdded) {
                    newQuery.add(term);
                    countOfOriginalQueryTermsAdded++;
                }
            } else {
                newQuery.add(term);
                countOfNewTermsAdded++;
            }

        }
        return newQuery;
    }

}
