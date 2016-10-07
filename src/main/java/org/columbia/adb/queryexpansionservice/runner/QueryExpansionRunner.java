package org.columbia.adb.queryexpansionservice.runner;

import org.columbia.adb.queryexpansionservice.interactor.QueryInteractor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class QueryExpansionRunner {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(
                "org.columbia.adb.queryexpansionservice");
        String bingAccessKey = args[0];
        Double targetPrecision = Double.parseDouble(args[1]);
        String query = args[2];
        QueryInteractor queryInteractor = (QueryInteractor) ctx.getBean(
                "QueryInteractor", bingAccessKey, targetPrecision, query);
        queryInteractor.startQueryInteractor();
    }
}