package com.company;

import org.apache.log4j.Logger;

/**
 * Created by mac on 2016. 12. 12..
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getRootLogger();

        long start = System.currentTimeMillis();

        //ThreadWork.execute(); // 서버가 요청을 받아주지 못하고 503에러 뱉음
        String targetUrl = "https://www.rocketpunch.com/jobs?job=%EA%B0%9C%EB%B0%9C%EC%9E%90";
        String baseUrl = "https://www.rocketpunch.com";
        String pageParamName = "page";
        URLConnection u = new URLConnection(targetUrl, baseUrl, pageParamName);
        u.execute();

        logger.info( "실행 시간 : " + ( System.currentTimeMillis() - start )/1000.0 );
    }
}
