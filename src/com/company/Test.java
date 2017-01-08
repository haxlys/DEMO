package com.company;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getRootLogger();
        try{
            System.setErr(new PrintStream(new FileOutputStream(System.getProperty("user.home")+"/LOG/rocketpunch/error.log")));

            long start = System.currentTimeMillis();

            //ThreadWork.execute(); // 서버가 요청을 받아주지 못하고 503에러 뱉음
            String targetUrl = "https://www.rocketpunch.com/jobs?job=%EA%B0%9C%EB%B0%9C%EC%9E%90";
            String baseUrl = "https://www.rocketpunch.com";
            String pageParamName = "page";
            URLConnection u = new URLConnection(targetUrl, baseUrl, pageParamName);
            u.execute();

            logger.info( "실행 시간 : " + ( System.currentTimeMillis() - start )/1000.0 );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
