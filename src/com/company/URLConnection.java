package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class URLConnection {

    // private static final String URL = "https://www.rocketpunch.com/jobs?job=%EA%B0%9C%EB%B0%9C%EC%9E%90";
    private String url = "";
    private String charset = "";
    private String baseUrl = "";
    private String pageParamName = "";
    private String urlWithPage = "";
    private Map<String, Integer> parsingMap;

    public URLConnection(String url, String baseUrl, String pageParamName){
        this(url, "utf-8", baseUrl, pageParamName);
    }

    public URLConnection(String url, String charset, String baseUrl, String pageParamName){
        this.url = url;
        this.charset = charset;
        this.baseUrl = baseUrl;
        this.pageParamName = pageParamName;
        this.urlWithPage = url + "&" + pageParamName + "=";
    }

    private Document openUrl(String url){
        URL u;
        Document doc = null;

        try {
            u = new URL(url);
            java.net.URLConnection conn = u.openConnection();
            doc = Jsoup.parse(conn.getInputStream(), charset, baseUrl);
        } catch(Exception e){
            e.printStackTrace();
            System.out.printf("%s\n", url + " 접속에 실패하였습니다.");
        }
        return doc;
    }

    /**
     * 파싱할 총 페이지 갯수를 a태그를 이용하여 찾는다
     * @param el : a태그를 가지고 있는 Elements
     * @param pagePramName : href안에 있는 page 갯수를 가지고 있는 파라미터 name
     * @return int : 총 페이지 갯수
     * @throws Exception
     */
    public int getTotalPageAtHref(Elements el, String pagePramName) throws Exception {
        //String node = doc.select("nav.pagination.wrap ul li").last().select("a").attr("href");
        String node = el.attr("href");

        if("".equals(node.trim())){
            System.out.println("getTotalPage 정보를 가져오지 못했습니다");
            throw new Exception();
        }

        String target = pagePramName+"=";
        node = node.substring(node.lastIndexOf(target)+target.length(), node.length());

        int totalPage = 0;
        try{
            totalPage = Integer.parseInt(node);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        System.out.println("총 페이지 : " + totalPage);
        return totalPage;
    }

    /**
     * 파싱할 정보가 text일 경우 사용한다
     * @param parsingTag : 페이지의 파싱할 text를 가진 태그
     * @param count : 페이지 총 개수
     */
    public void parsingText(String parsingTag, int count){
        this.parsingMap = new TreeMap<>();

        // 페이지 갯수 만큼 스킬 파싱
        for(int i=1; i<=count; i++){
            System.out.println(i + " 페이지 파싱 중...");
            setParsingMap(openUrl(urlWithPage+i).select(parsingTag));
        }
    }

    public void parsingText(String parsingTag){
        parsingText(parsingTag, 1);
    }

    public void setParsingMap(Elements el){
        for( Element elem : el ){
            if(this.parsingMap.get(elem.text()) == null){
                this.parsingMap.put(elem.text(),1);
            } else {
                this.parsingMap.put(elem.text(),this.parsingMap.get(elem.text())+1);
            }
        }
    }

    public List sortByValue(final Map map){
    ArrayList list = new ArrayList();
        list.addAll(map.keySet());

        list.sort(new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v1).compareTo(v2);
            }
        });

        Collections.reverse(list); // 내림차순

        return list;
    }

    public void sortByValue(){
        for (Object o : sortByValue(this.parsingMap)) {
            String temp = (String) o;
            System.out.println(temp + " = " + this.parsingMap.get(temp));
        }
    }

    public void addStats() throws SQLException, ClassNotFoundException {
        SkillStatsDAO dao = new Factory().skillStatsDAO();

        for (String key : this.parsingMap.keySet()){
            int value = this.parsingMap.get(key);
            Domain vo = new Domain();
            vo.setId(dao.getMaxId());
            vo.setSkillCount(value);
            vo.setSkillName(key);
            dao.add(vo);
            System.out.println(key + " : " + value);
        }
    }

    public void execute() throws Exception {
        // 파싱할 페이지 갯수 얻기
        Document doc = openUrl(url);
        Elements el = doc.select("nav.pagination.wrap ul li").last().select("a");
        int totalPage = getTotalPageAtHref(el, pageParamName);

        // 페이지 갯수 만큼 정보 파싱
        String parsingTag = ".list.list-unstyled.tags li a"; // 가져올 정보를 가진 태그
        parsingText(parsingTag, totalPage);

        // 스킬 이름 오름차순
        addStats();

        // 많이 쓰는 스킬 내림차순
        //sortByValue();

        System.out.println("스킬 개수 : " + this.parsingMap.size());
    }
}
