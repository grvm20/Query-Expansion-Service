import java.util.ArrayList;
import java.util.HashMap;
// Class for each result 
public class Resultdoc {
	private int id;										// Entry number from 1 to 10
	private String url;									// Entry URL
	private String title;								// Entry title
	private String description;							// Entry description
	private boolean isRelevant;							// Stores if the entry is relevant
	private ArrayList<String> words;					// Stores a list of terms in the entry with stop words eliminated
	private HashMap<String, Double> termFrequencies;	// Stores term frequencies in a string and double pair
	private String[] wordsInTitle;
	// Constructor
	public Resultdoc(int id, String url, String title, String description) {
		this.id = id;
		this.url = url;
		this.title = title;
		this.isRelevant = false;
		this.description = description;
		this.words = new ArrayList<String>();
		this.wordsInTitle=title.split(" ");
		this.computeTermFrequencies();						
				
			}
	
	
	
	private String strip(String term) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < term.length(); i++) {	
			if (Character.isLetterOrDigit(term.charAt(i)) || term.charAt(i) == '-' || term.charAt(i) == 39 || term.charAt(i) == '.')
				builder.append(Character.toLowerCase(term.charAt(i)));
		}
		return builder.toString();
	}
	
	private void computeTermFrequencies() {
		termFrequencies = new HashMap<String, Double>();
		
		String[] terms = (title + " " + description).split(" ");
		for (String term : terms) {
			term = strip(term);		
			if (term.length() > 0 ) {
				this.words.add(term);							// Count term occurrence first
				if (termFrequencies.containsKey(term))
					termFrequencies.put(term, termFrequencies.get(term) + 1.0);
				else
					termFrequencies.put(term, 1.0);
			}
		}
		// Compute term frequencies
		for (String term : termFrequencies.keySet())
			termFrequencies.put(term, termFrequencies.get(term) / termFrequencies.size());
	}
	
	
	public int getId() {
		return id;
	}
	
	public String[] getTitleWords() {
		return wordsInTitle;
	}
	
	public void setRelevance(boolean relevance) {
		isRelevant = relevance;
	}
	
	public boolean isRelevant() {
		return isRelevant;
	}
	
	public HashMap<String, Double> getTermFrequencies() {
		return termFrequencies;
	}
	
	/*private void getWords() {
		String[] wordsres = (title + " " + description).split(" ");
		for (String word : wordsres) {
			if (word.length() > 0 && !(isNumeric(word))) {
				this.words.add(word);							// Count term occurrence first
				
			}
		}
	}*/
	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\n Search Result %d : \n", this.id));
		builder.append(String.format("URL: %s\nTitle: %s\nDescription: %s\n", this.url, this.title, this.description));
		return builder.toString();
	}
	
}
