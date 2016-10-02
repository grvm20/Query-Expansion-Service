package org.columbia.adb.queryexpansionservice.interactor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.columbia.adb.queryexpansionservice.cache.StopWordsCache;
import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.columbia.adb.queryexpansionservice.query.model.QueryResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QueryInteractor {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private StopWordsCache stopWordsCache;

    private QueryWeb queryWeb;

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    @PostConstruct
    public void init() throws Exception {

        queryWeb = (QueryWeb) ctx.getBean("QueryBing", "a4YMppsSw10MCeXqtQY41lxTAHv0LFoU7sn4WbpjH/k");

        System.out.print("Enter Query: ");
        String query = br.readLine();
        Validate.notEmpty(query, "Empty query issued. This is not cool bro.");
        String updatedQuery = removeStopWordsFromQueryIfPossible(query);
        System.out.println("Updated Query: " + updatedQuery);
        List<QueryResponseModel> queryResponses = queryWeb.query(updatedQuery);
        for (QueryResponseModel queryResponse : queryResponses) {
            System.out.println("#####################################");
            System.out.println("URL: " + queryResponse.getUrl());
            System.out.println("Description: " + queryResponse.getDescription());
            System.out.println("Title: " + queryResponse.getTitle());
            System.out.println("#####################################");
        }
        init();
    }

    /**
     * Only remove stop words if sentence has atleast word which is not a stop
     * word
     * 
     * @param query
     * @return
     */
    private String removeStopWordsFromQueryIfPossible(String query) {

        StringBuilder updatedQueryBuilder = new StringBuilder();

        for (String word : query.split(" ")) {
            if (!stopWordsCache.isStopWord(word)) {
                updatedQueryBuilder.append(word + " ");
            }
        }

        String updatedQuery = updatedQueryBuilder.toString();
        return updatedQuery.length() == 0 ? query : updatedQuery.trim();
    }

}
