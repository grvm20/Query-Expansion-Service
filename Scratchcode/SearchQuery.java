import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class SearchQuery {
	 private String key;         // Holds the account key for authentication
	 private String query;       // Holds the query terms as a single string separated by spaces
	 private Resultdoc[] results; 
	 
	 public SearchQuery(String query){
		 this.query=query;
		 this.key="53P3c4mc5Y4VeQJOy4bA+I2+/a84DbSf/lqWXgZVkmk";
		 this.results=new Resultdoc [10];
	 }
	 
	 public Resultdoc[] resultReturn() throws Exception {
	       // final String accountKey = "53P3c4mc5Y4VeQJOy4bA+I2+/a84DbSf/lqWXgZVkmk";
	         String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=JSON";

	        final String query1 = URLEncoder.encode(query, Charset.defaultCharset().name());
	        final String bingUrl = String.format(bingUrlPattern, query1);

	        final String accountKeyEnc = Base64.getEncoder().encodeToString((key + ":" + key).getBytes());

	        final URL url = new URL(bingUrl);
	        final URLConnection connection = url.openConnection();
	        connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
	        Resultdoc[] finalres=new Resultdoc[10];
	        
	        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	            String inputLine;
	            final StringBuilder response = new StringBuilder();
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	             JSONObject json = new JSONObject(response.toString());
	             JSONObject d = json.getJSONObject("d");
	             JSONArray results = d.getJSONArray("results");
	             int resultsLength = results.length();
	            
	           /* for (int i = 0; i < resultsLength; i++) {
	                final JSONObject aResult = results.getJSONObject(i);
	                System.out.println(aResult.get("Url").toString());
	            }*/
	            for (int i = 0; i < 10; i++) {
	            	 final JSONObject res = results.getJSONObject(i);
	            	 finalres[i]=new Resultdoc(i+1, (res.get("Url").toString()), (res.get("Title").toString()), (res.get("Description").toString()));
	            }
	            return finalres;
	        }
}
}
