package org.columbia.adb.queryexpansionservice.interactor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;
import org.columbia.adb.queryexpansionservice.queryexpander.QueryExpander;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.columbia.adb.queryexpansionservice.cache.StopWordsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/***
 * This class handles all query interaction.
 * 
 * @author gauravmishra
 *
 */
@Component
public class QueryInteractor {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private StopWordsCache stopWordsCache;

    @Autowired
    private QueryExpander queryExpander;

    private QueryWeb queryWeb;

    private BufferedReader br = new BufferedReader(new InputStreamReader(
            System.in));

    @PostConstruct
    public void init() throws Exception {

        double targetPrecision = 0.9;
        String query = br.readLine();
        String bingKey = "a4YMppsSw10MCeXqtQY41lxTAHv0LFoU7sn4WbpjH/k";

        queryWeb = (QueryWeb) ctx.getBean("QueryBing", bingKey);

        double prec = 0;
        int totalNumberOfResults = 10;

        // TODO add more validations
        Validate.notEmpty(query, "Empty query issued. This is not cool bro.");

        while (prec < targetPrecision) {

            System.out.println("Parameters:");
            System.out.println("Client Key = " + bingKey);
            System.out.println("Query = " + query);
            System.out.println("Precision = " + targetPrecision);

            int relevantResults = 0;
            List<QueryResultInfo> queryResultInfoList = queryWeb.query(query,
                    totalNumberOfResults);

            System.out.println("Total no of results : " + totalNumberOfResults);
            System.out.println("Bing Search Results:");
            System.out.println("======================");

            int resultCount = 1;

            if (queryResultInfoList.size() < totalNumberOfResults) {
                System.err.println("Cannot process further");
                throw new RuntimeException(
                        "Total Number of responses returned are less than "
                                + totalNumberOfResults);
            }

            for (QueryResultInfo queryResultInfo : queryResultInfoList) {

                System.out.println();
                System.out.println("Result " + resultCount);
                System.out.println("[");

                System.out.println("URL: " + queryResultInfo.getUrl());
                System.out.println("Title: " + queryResultInfo.getTitle());
                System.out.println("Summary: " + queryResultInfo.getSummary());

                System.out.println("]");

                System.out.print("Relevant (Y/N)?");
                String isRevelant = br.readLine();

                while (!(StringUtils.equalsIgnoreCase("Y", isRevelant) || StringUtils
                        .equalsIgnoreCase("N", isRevelant))) {
                    System.out.println("Please enter either X or Y");
                    isRevelant = br.readLine();
                }

                if (StringUtils.equalsIgnoreCase("Y", isRevelant)) {
                    relevantResults++;
                    queryResultInfo.setRelevant(true);
                }
                resultCount++;

            }
            System.out.println("======================");
            System.out.println("FEEDBACK SUMMARY");
            System.out.println("Query " + query);
            prec = relevantResults / (double) totalNumberOfResults;
            System.out.println("Precision " + prec);

            if (prec < targetPrecision) {
                System.out.println("Still below the desired precision of "
                        + targetPrecision);
                query = queryExpander.expandQuery(query, queryResultInfoList);
            } else {
                System.out.println("Desired precision reached, done");
            }

        }
    }
}
