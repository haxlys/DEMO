package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class ThreadWork implements Runnable {

    private static final String URL = "https://www.rocketpunch.com/jobs?job=%EA%B0%9C%EB%B0%9C%EC%9E%90&page=";
    private static Map<String, Integer> skillMap = new TreeMap<>();
    private int seq = 0;

    public ThreadWork(int seq){
        this.seq = seq;
    }

    public ThreadWork(){}

    public static void execute() throws Exception {
        int totalPage = getTotalPage(openUrl(URL+1));

        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0; i<totalPage; i++) {
            Thread t = new Thread(new ThreadWork(i));
            t.start();
            threads.add(t);
        }

        for(int i=0; i<threads.size(); i++) {
            Thread t = threads.get(i);
            try {
                t.join();
            }catch(Exception e) {
            }
        }

        // 스킬 이름 오름차순
        //showStats();

        // 많이 쓰는 스킬 내림차순
        for (Object o : sortByValue(skillMap)) {
            String temp = (String) o;
            System.out.println(temp + " = " + skillMap.get(temp));
        }

        System.out.println("스킬 종류 : " + skillMap.size());
    }

    private static Document openUrl(String urlStr){
        URL url;
        Document doc = null;
        try {
            url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            doc = Jsoup.parse(conn.getInputStream(), "UTF-8", "https://www.rocketpunch.com");
        } catch(Exception e){
            e.printStackTrace();
            System.out.printf("%s\n", urlStr + " 접속에 실패하였습니다.");
        }

        return doc;
    }

    public static int getTotalPage(Document doc) throws Exception {
        String node = doc.select("nav.pagination.wrap ul li").last().select("a").attr("href");

        if("".equals(node.trim())){
            throw new Exception();
        }

        String target = "page=";
        node = node.substring(node.lastIndexOf(target)+target.length(), node.length());

        int totalPage = 0;
        try{
            totalPage = Integer.parseInt(node);
        } catch (NumberFormatException e){
            e.printStackTrace();
            throw new NumberFormatException();
        }

        System.out.println("총 페이지 : " + totalPage);
        return totalPage;
    }

    public static void skillParsing(Document doc){
        //Map<String, Integer> skillMap = new HashMap<>();
        Elements skillTag = doc.select(".list.list-unstyled.tags li a");
        for( Element elem : skillTag ){
            //System.out.println(elem.text());
            if(skillMap.get(elem.text()) == null){
                skillMap.put(elem.text(),1);
            } else {
                skillMap.put(elem.text(),skillMap.get(elem.text())+1);
            }
        }


    }

    public static void showStats(){
        for (String key : skillMap.keySet()){
            int value = skillMap.get(key);
            System.out.println(key + " : " + value);
        }
    }

    public static List sortByValue(final Map map){
        ArrayList list = new ArrayList();
        list.addAll(map.keySet());

        list.sort(new Comparator() {

            public int compare(Object o1, Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }

        });
        Collections.reverse(list); // 주석시 오름차순
        return list;
    }

    @Override
    public void run() {
        System.out.println(this.seq+" 페이지 파싱 중...");
        try {
            skillParsing(openUrl(URL+seq));
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("쓰레드 도중 에러");
        }
        System.out.println(this.seq+" 페이지 파싱 끝...");
    }
}
