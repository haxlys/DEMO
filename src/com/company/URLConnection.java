package com.company;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class URLConnection {

    private Logger log = Logger.getLogger(this.getClass());

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

    public void execute() throws Exception {
        // 파싱할 페이지 갯수 얻기
        Document doc = openUrl(url);
        Elements el = doc.select("nav.pagination.wrap ul li").last().select("a"); // 마지막 페이지로 가는 버튼을 이용해 가져옴
        int totalPage = getTotalPageAtHref(el, pageParamName);

        // 총 페이지 기반으로 정보 파싱
        String parsingTag = ".list.list-unstyled.tags li a"; // 기술 text 구분할 수 있는 셀렉터

        // 기술 이름 파싱 후 저장(스킬 이름 오름차순)
        addStats(parsingText(parsingTag, totalPage));

        // 많이 쓰는 스킬 내림차순
        //sortByValue();

        log.info("스킬 개수 : " + this.parsingMap.size());
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
        String node = el.attr("href");

        if("".equals(node.trim())){
            log.warn("getTotalPage 정보를 가져오지 못했습니다");
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

        log.info("총 페이지 : " + totalPage);
        return totalPage;
    }

    /**
     * text 정보를 수집한다
     * @param parsingTag text정보를 가지고 있는 셀럭터 태그 값
     * @param count 페이지 횟수
     * @return 파싱된 map
     */
    public Map<String, Integer> parsingText(String parsingTag, int count){
        Map<String, Integer> map = new TreeMap<>();

        // 페이지 갯수 만큼 스킬 파싱
        for(int i=1; i<=count; i++){
            log.info(i + " 페이지 파싱 중...");

            for( Element elem : openUrl(urlWithPage+i).select(parsingTag) ){
                if(map.get(elem.text()) == null){
                    map.put(elem.text(),1);
                } else {
                    map.put(elem.text(),map.get(elem.text())+1);
                }
            }
        }

        return map;
    }

    public void addStats(Map<String, Integer> map) throws SQLException, ClassNotFoundException {
        SkillStatsDAO dao = new Factory().skillStatsDAO();

        for (String key : map.keySet()){
            int value = map.get(key);
            Domain vo = new Domain();
            vo.setId(dao.getMaxId());
            vo.setSkillCount(value);
            vo.setSkillName(key);
            //dao.add(vo);
            log.info(key + " : " + value);
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
}
