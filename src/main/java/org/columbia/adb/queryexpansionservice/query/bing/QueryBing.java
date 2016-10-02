package org.columbia.adb.queryexpansionservice.query.bing;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.columbia.adb.queryexpansionservice.query.model.QueryResponseModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "QueryBing")
@Scope("prototype")
public class QueryBing implements QueryWeb {

    private String KEY;

    public QueryBing(String key) {
        this.KEY = key;
    }

    public List<QueryResponseModel> query(final String query) throws Exception {

        
        
        String accountKeyAuth = Base64.getEncoder().encodeToString((KEY + ":" + KEY).getBytes());

        URL url = new URL(
                "https://api.datamarket.azure.com/Bing/Search/v1/Composite?Sources=%27web%2Bspell%2BRelatedSearch%27&Query=%27"
                        + URLEncoder.encode(query, "utf8")
                        + "%27&Options=%27EnableHighlighting%27&$top=10&Market=%27en-US%27&Adult=%27Off%27&$format=Json");

        JSONObject result = null;

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(0);
        connection.setRequestProperty("Authorization", "Basic " + accountKeyAuth);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "multipart/form-data");

        if (connection.getResponseCode() != 200) {
            System.err.printf("Got HTTP error %d", connection.getResponseCode());
            throw new Exception();
        }

        List<QueryResponseModel> queryResponses = null;
        try (@SuppressWarnings("resource")
        Scanner s = new Scanner(connection.getInputStream()).useDelimiter("\\A")) {
            String resultStr = s.hasNext() ? s.next() : "";
            result = new JSONObject(resultStr);

            queryResponses = constructQueryResponses(result);

        }

        return queryResponses;
    }

    private List<QueryResponseModel> constructQueryResponses(final JSONObject result) throws JSONException {

        List<QueryResponseModel> queryResponses = new ArrayList<>();

        // TODO Add null checks. If it fails thrown exception
        JSONObject data = (JSONObject) result.get("d");
        JSONObject results = (JSONObject) ((JSONArray) data.get("results")).get(0);
        JSONArray webResults = (JSONArray) results.get("Web");

        // TODO We should instead validate and throw exception
        if (webResults != null && (StringUtils.isNotEmpty((String) results.get("WebTotal")))) {
            for (int i = 0; i < webResults.length(); i++) {
                JSONObject jsonObject = webResults.getJSONObject(i);

                QueryResponseModel queryResponseModel = QueryResponseModel.builder()
                        .url(jsonObject.getString("DisplayUrl")).title(jsonObject.getString("Title"))
                        .description(jsonObject.getString("Description")).build();

                queryResponses.add(queryResponseModel);
            }

        }

        return queryResponses;
    }

}
