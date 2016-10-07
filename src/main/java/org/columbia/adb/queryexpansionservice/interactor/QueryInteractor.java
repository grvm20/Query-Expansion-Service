package org.columbia.adb.queryexpansionservice.interactor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.columbia.adb.queryexpansionservice.cache.StopWordsCache;
import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;
import org.columbia.adb.queryexpansionservice.queryexpander.QueryExpander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/***
 * This class handles all query interaction.
 * 
 * @author gauravmishra
 *
 */
@Component(value = "QueryInteractor")
@Scope("prototype")
public class QueryInteractor {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private StopWordsCache stopWordsCache;

    @Autowired
    private QueryExpander queryExpander;

    private double targetPrecision;
    private String query;
    private String accessKey;

    private QueryWeb queryWeb;

    private BufferedReader br = new BufferedReader(new InputStreamReader(
            System.in));

    public QueryInteractor(String accessKey, double targetPrecision,
            String query) {
        this.accessKey = accessKey;
        this.targetPrecision = targetPrecision;
        this.query = query;
    }

    public void startQueryInteractor() throws Exception {

        queryWeb = (QueryWeb) ctx.getBean("QueryBing", accessKey);

        double prec = 0;
        int totalNumberOfResults = 10;
        int round = 1;
        // TODO add more validations
        Validate.notEmpty(query, "Empty query issued. This is not cool bro.");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File("transcript.txt")))) {

            while (prec < targetPrecision) {

                System.out.println("Parameters:");
                System.out.println("Client Key = " + accessKey);
                System.out.println("Query = " + query);
                System.out.println("Precision = " + targetPrecision);

                writer.write("Parameters:");
                writer.newLine();
                writer.write("Client Key = " + accessKey);
                writer.newLine();
                writer.write("Query = " + query);
                writer.newLine();
                writer.write("Precision = " + targetPrecision);
                writer.newLine();

                int relevantResults = 0;
                List<QueryResultInfo> queryResultInfoList = queryWeb.query(
                        query, totalNumberOfResults);

                System.out.println("Total no of results : "
                        + totalNumberOfResults);
                System.out.println("Bing Search Results:");
                System.out.println("======================");

                int resultCount = 1;

                if (queryResultInfoList.size() < totalNumberOfResults) {
                    System.err.println("Cannot process further");
                    throw new RuntimeException(
                            "Total Number of responses returned are less than "
                                    + totalNumberOfResults);
                }

                writer.write("=====================================");
                writer.newLine();
                writer.write("ROUND " + round);
                writer.newLine();
                for (QueryResultInfo queryResultInfo : queryResultInfoList) {

                    System.out.println();
                    String resCountString = "Result " + resultCount;
                    System.out.println(resCountString);

                    writer.write(resCountString);
                    writer.newLine();

                    System.out.println("[");
                    writer.write("[");
                    writer.newLine();

                    String urlString = "URL: " + queryResultInfo.getUrl();
                    String titleString = "Title: " + queryResultInfo.getTitle();
                    String summaryString = "Summary: "
                            + queryResultInfo.getSummary();

                    System.out.println(urlString);
                    System.out.println(titleString);
                    System.out.println(summaryString);

                    writer.write(urlString);
                    writer.newLine();
                    writer.write(titleString);
                    writer.newLine();
                    writer.write(summaryString);
                    writer.newLine();

                    System.out.println("]");

                    writer.write("]");
                    writer.newLine();

                    System.out.print("Relevant (Y/N)?");
                    String isRevelantString = br.readLine();

                    while (!(StringUtils
                            .equalsIgnoreCase("Y", isRevelantString) || StringUtils
                            .equalsIgnoreCase("N", isRevelantString))) {
                        System.out.println("Please enter either X or Y");
                        isRevelantString = br.readLine();
                    }
                    Boolean isRelevant = StringUtils.equalsIgnoreCase("Y",
                            isRevelantString);
                    if (isRelevant) {
                        relevantResults++;
                        queryResultInfo.setRelevant(true);
                        writer.write("RELEVANT: YES");
                    } else {
                        writer.write("RELEVENT: NO");
                    }
                    writer.newLine();
                    resultCount++;

                }
                System.out.println("======================");
                System.out.println("FEEDBACK SUMMARY");
                System.out.println("Query " + query);

                prec = relevantResults / (double) totalNumberOfResults;
                System.out.println("Precision " + prec);

                writer.write("----------------------");
                writer.newLine();
                writer.write("FEEDBACK SUMMARY");
                writer.newLine();
                writer.write("PRECISION " + prec);
                writer.newLine();

                if (prec < targetPrecision) {
                    System.out.println("Still below the desired precision of "
                            + targetPrecision);
                    query = queryExpander.expandQuery(query,
                            queryResultInfoList);
                } else {
                    System.out.println("Desired precision reached, done");
                }
                round++;

            }

        }

    }
}
