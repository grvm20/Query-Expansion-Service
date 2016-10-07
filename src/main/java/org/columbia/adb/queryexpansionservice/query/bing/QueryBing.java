package org.columbia.adb.queryexpansionservice.query.bing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/***
 * Class responsible for querying bing.
 * 
 * @author gauravmishra
 ***/

@Component(value = "QueryBing")
@Scope("prototype")
public class QueryBing implements QueryWeb {

    private String KEY;

    public QueryBing(final String key) {
        Validate.notEmpty(key, "Key passed is Empty");
        this.KEY = key;
    }

    @Override
    public List<QueryResultInfo> query(final String query,
            final int totalNumberOfResults) throws Exception {

        Validate.notEmpty(query, "Empty query passed while trying to call bing");

        String accountKeyAuth = Base64.encodeBase64String(
                (KEY + ":" + KEY).getBytes());
        String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=JSON";
        final String query1 = URLEncoder.encode(query, "utf8");
        String urlString =String.format(bingUrlPattern, query1);

        System.out.println("URL: " + urlString);

        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(0);
        connection.setRequestProperty("Authorization", "Basic "
                + accountKeyAuth);

        if (connection.getResponseCode() != 200) {
            System.err
                    .printf("Got HTTP error %d", connection.getResponseCode());
            throw new Exception(
                    "Bing threw exception while processing query. Response Code : "
                            + connection.getResponseCode());
        }

        List<QueryResultInfo> queryResponses = null;
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
             JSONObject json = new JSONObject(response.toString());
            queryResponses = constructQueryResponses(json);

        return queryResponses;
        }
    }

    private List<QueryResultInfo> constructQueryResponses(
            final JSONObject result) throws JSONException {

        List<QueryResultInfo> queryResponses = new ArrayList<>();

        // TODO Add null checks. If it fails thrown exception
        JSONObject d = result.getJSONObject("d");
        JSONArray webResults = d.getJSONArray("results");

        // TODO We should instead validate and throw exception
        if (webResults != null) {
            for (int i = 0; i < 10; i++) {
                JSONObject jsonObject = webResults.getJSONObject(i);

                QueryResultInfo queryResponseModel = new QueryResultInfo(i + 1,
                        jsonObject.getString("DisplayUrl"),
                        jsonObject.getString("Title"),
                        jsonObject.getString("Description"));

                queryResponses.add(queryResponseModel);
            }

        }

        return queryResponses;
    }

}
