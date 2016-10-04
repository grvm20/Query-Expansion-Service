import java.util.Comparator;
import java.util.HashMap;

public class ResultCompare implements Comparator<String> {

    
    private HashMap<String, Double> tfidf;
    
    
    public ResultCompare(HashMap<String, Double> tfidf) {
        this.tfidf = tfidf;
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
