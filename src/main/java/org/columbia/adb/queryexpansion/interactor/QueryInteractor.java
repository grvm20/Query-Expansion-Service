package org.columbia.adb.queryexpansion.interactor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.columbia.adb.queryexpansion.query.model.QueryResponseModel;
import org.columbia.adb.queryexpansionservice.query.QueryWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QueryInteractor {

    @Autowired
    private ApplicationContext ctx;

    private QueryWeb queryWeb;

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    @PostConstruct
    public void init() throws Exception {
        queryWeb = (QueryWeb) ctx.getBean("QueryBing", "a4YMppsSw10MCeXqtQY41lxTAHv0LFoU7sn4WbpjH/k");

        System.out.print("Enter Query: ");
        String query = br.readLine();
        List<QueryResponseModel> queryResponses = queryWeb.query(query);
        for (QueryResponseModel queryResponse : queryResponses) {
            System.out.println("#####################################");
            System.out.println("URL: " + queryResponse.getUrl());
            System.out.println("Description: " + queryResponse.getDescription());
            System.out.println("Title: " + queryResponse.getTitle());
            System.out.println("#####################################");
        }
        init();
    }

}
