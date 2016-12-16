package com.company;

/**
 * Created by mac on 2016. 12. 12..
 */
public class Test {

    @org.junit.Test
    public void Test() throws Exception {
        long start = System.currentTimeMillis();
        //Main.execute(); // 서버가 요청을 받아주지 못하고 503에러 뱉음
        String targetUrl = "https://www.rocketpunch.com/jobs?job=%EA%B0%9C%EB%B0%9C%EC%9E%90";
        String baseUrl = "https://www.rocketpunch.com";
        String pageParamName = "page";
        new URLConnection(targetUrl, baseUrl, pageParamName).execute();
        /*DBConnection c = new DBConnection();
        Domain d = new Domain();
        d.setId(2);
        d.setSkillName("ㅋㅋㅋㅋ");
        d.setSkillCount(1);
        c.add(d);*/

        long end = System.currentTimeMillis();

        System.out.println( "실행 시간 : " + ( end - start )/1000.0 );
    }
}
