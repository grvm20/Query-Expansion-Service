package org.columbia.adb.queryexpansionservice.runner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class QueryExpansionRunner {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("org.columbia.adb");
    }

}