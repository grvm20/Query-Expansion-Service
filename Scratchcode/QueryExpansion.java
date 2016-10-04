import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QueryExpansion {
	private Resultdoc[] results;                                        
    private String[] initialQuery;                                  
    private int relevantdocs;                             
    private HashMap<String, Double> newQueryVector;                 
    private HashMap<String, Double> inverseDocumentFrequencies;     
    
    // Define constants for Rocchio algorithm
    private final static double alpha = 1;
    private final static double beta = 0.9;
    private final static double gamma = 0.4;
    private final static double delta = 0.3;
    
    
    public QueryExpansion(String[] initialQuery, Resultdoc[] results) {
        this.results = results;
        this.initialQuery = initialQuery;
        this.relevantdocs = 0;
        this.computeInverseDocumentFrequencies();
        this.computeNewQueryVector();
    }
    
    
    private void computeInverseDocumentFrequencies() {
        inverseDocumentFrequencies = new HashMap<String, Double>();
        for (Resultdoc res : results) {
            
            if (res.isRelevant())
                relevantdocs++;
           
            for (String term : res.getTermFrequencies().keySet())
                inverseDocumentFrequencies.put(term, Math.log(10.0 / docsWithWord(term)));
        }
    }
    
    // Return the number of documents with the input word
    private int docsWithWord(String term) {
        int count = 0;
        for (Resultdoc res : results) {
            if (res.getTermFrequencies().containsKey(term))
                count = count + 1;
        }
        return count;
    }
    
    // Compute the expanded query vector following Rocchio algorithm
    // new query vector = alpha * initial query vector + beta * sum(relevant document vector) / number of relevant documents -
    //          gamma * sum(non-relevant document vector) / number of non-relevant documents
    private void computeNewQueryVector() {
        newQueryVector = new HashMap<String, Double>();
        for (Resultdoc res : results) {
            HashMap<String, Double> tf = res.getTermFrequencies();
            if (res.isRelevant()) {       //Relevant
                for (String term : tf.keySet()) {
                	double val=beta / relevantdocs * tf.get(term) * inverseDocumentFrequencies.get(term);
                    if (newQueryVector.containsKey(term))
                        newQueryVector.put(term, newQueryVector.get(term) + val);
                    else
                        newQueryVector.put(term,val);
                    if(Arrays.asList(res.getTitleWords()).contains(term)){
                    	newQueryVector.put(term, newQueryVector.get(term) + delta);
                    }
                }
            }
            else {                        // Non-relevant document
                for (String term : tf.keySet()) {
                	double val=gamma / (10 - relevantdocs) * tf.get(term) * inverseDocumentFrequencies.get(term);
                    if (newQueryVector.containsKey(term))
                        newQueryVector.put(term, newQueryVector.get(term) - val);
                    else
                        newQueryVector.put(term, val);
                }
            }
        }
        // Adding 
        for (String term : initialQuery)
            newQueryVector.put(term, newQueryVector.get(term) + alpha);
    }
    
    // Public interface to get the new query terms in a list
    public List<String> getNewQuery() {
        // Sort the terms in the new query vector by tf-idf
        List<String> sortedTerms = new ArrayList<String>(newQueryVector.keySet());
        Collections.sort(sortedTerms, new ResultCompare(newQueryVector));
        
        // Get new query terms starting from the most weighted
        int ct = 0;    
        int cn = 0;
        List <String> newQuery = new ArrayList<String>();
        // Keeps adding terms until the number of terms in the new query is two more than the initial query
        for (int i = 0; (ct+cn) < initialQuery.length + 1; i++) {
            String term = sortedTerms.get(i);
            
            if (!Arrays.asList(initialQuery).contains(term)) {
            	if(cn<1)
            	{
            	newQuery.add(term);
            	cn++;  }          	
            }
            else{
            	newQuery.add(term);
            	ct++;            	
            }
            
        }
        return newQuery;
    }
}
