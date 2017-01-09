package com.company;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.*;

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
        // 기술 이름 파싱 후 저장(스킬 이름 오름차순)
        parsingOfferInfo(totalPage);

        // 많이 쓰는 스킬 내림차순
        //sortByValue();
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
     * @param count 페이지 횟수
     * @return 파싱된 map
     */
    public void parsingOfferInfo(int count) throws SQLException, ClassNotFoundException {
        SkillStatsDAO dao = new Factory().skillStatsDAO();

        // 페이지 갯수 만큼 스킬 파싱
        for(int i=1; i<=count; i++){
            log.info("################ " + i + " 페이지 파싱 중... ################");

            Element pageHtml = openUrl(urlWithPage+i);

            // 한페이지에서 구인공고 하나를 가져옴
            for( Element offerEl : pageHtml.select(".card.job.list")){
                Domain vo = new Domain();

                // 구인 ID
                String offerIdNameByURL = "/jobs/";
                String offerHref = offerEl.select(".summary a").attr("href");
                int targetIndex = offerHref.indexOf(offerIdNameByURL);
                int firstIndex = targetIndex + offerIdNameByURL.length();
                int lastIndex = offerHref.indexOf("/",firstIndex);
                int offerId = Integer.parseInt(offerEl.select(".summary a").attr("href").substring(firstIndex, lastIndex));
                vo.setOfferId(offerId);
                log.info("구인 ID : " + offerId);

                // 등록일[수정일]
                String[] regist = offerEl.select(".date.reg").text().split(" ");
                vo.setOfferRegistDate(regist[0]);
                vo.setOfferState(regist[1]);
                log.info("구인 등록일 : " + offerEl.select(".date.reg").text());

                // 구인공고ID와 등록일[수정일]이 일치하는 경우 패스
                if(!dao.hasOffer(vo)){
                    // 회사 이름
                    String companyName = offerEl.select(".p.nowrap.jobtitle>.worksfor").text();

                    int cpnId = dao.getCompanyId(companyName);
                    if(0 == cpnId) {
                        vo.setCompanyId(dao.getMaxId("company"));
                        vo.setCompanyName(companyName);

                        dao.addCompany(vo);
                    } else {
                        vo.setCompanyId(cpnId);
                    }
                    log.info("회사 명 : " + vo.getCompanyId() + " " + companyName);

                    dao.addOffer(vo); //offer_id, company_id

                    // 제목
                    Elements subject = offerEl.select(".p.nowrap.jobtitle");
                    subject.select(".worksfor").remove();
                    String offerSubject = subject.text();
                    vo.setOfferSubject(offerSubject);
                    log.info("구인 제목 : " + offerSubject);

                    // 마감
                    String expire = offerEl.select(".date.end").text().split(" ")[0];
                    vo.setOfferExpireDate(expire);
                    log.info("구인 마감일 : " + expire);

                    vo.setId(dao.getMaxId("offer_info"));

                    dao.addOfferInfo(vo);


                    // 구인 기술
                    Map<String, Integer> map = new TreeMap<>();
                    for( Element el : offerEl.select(".btn.btn-xs.btn-tag")){ // 구인공고하나당 기술하나를 가져옴
                        vo.setId(dao.getMaxId("offer_skills"));
                        //vo.setCompanyId(cpnId);
                        vo.setSkillName(el.text());
                        log.info(vo.getId() + " " + vo.getCompanyId() + " " + vo.getSkillName());

                        dao.addSkill(vo);
                    }

                    // 구인 포지션
                    for( Element el : offerEl.select(".nowrap.area>.ic-text")){
                        vo.setId(dao.getMaxId("offer_position"));
                        //vo.setCompanyId(cpnId);
                        vo.setPosition(el.text());
                        log.info(vo.getId() + " " + vo.getCompanyId() + " " + el.text());

                        dao.addPosition(vo);
                    }

                    // 구인 경력
                    Elements offerExp = offerEl.select(".dl-info.dl-role.clearfix>.dd>.ic-text");
                    for(Element el : offerExp){
                        vo.setId(dao.getMaxId("offer_exp"));
                        vo.setOfferExp(el.text());
                        dao.addExp(vo);
                        log.info(el.text());
                    }
                } else if(!dao.isEqualContent(vo)) {
                    // 제목
                    Elements subject = offerEl.select(".p.nowrap.jobtitle");
                    subject.select(".worksfor").remove();
                    String offerSubject = subject.text();
                    vo.setOfferSubject(offerSubject);
                    log.info("구인 제목 : " + offerSubject);

                    // 마감
                    String expire = offerEl.select(".date.end").text().split(" ")[0];
                    vo.setOfferExpireDate(expire);
                    log.info("구인 마감일 : " + expire);

                    dao.addOfferInfo(vo);


                    // 구인 기술
                    Map<String, Integer> map = new TreeMap<>();
                    for( Element el : offerEl.select(".btn.btn-xs.btn-tag")){ // 구인공고하나당 기술하나를 가져옴
                        vo.setId(dao.getMaxId("offer_skills"));
                        vo.setSkillName(el.text());
                        log.info(vo.getId() + " " + vo.getCompanyId() + " " + vo.getSkillName());

                        dao.addSkill(vo);
                    }

                    // 구인 포지션
                    for( Element el : offerEl.select(".nowrap.area>.ic-text")){
                        vo.setId(dao.getMaxId("offer_position"));
                        vo.setPosition(el.text());
                        log.info(vo.getId() + " " + vo.getCompanyId() + " " + el.text());

                        dao.addPosition(vo);
                    }

                    // 구인 경력
                    Elements offerExp = offerEl.select(".dl-info.dl-role.clearfix>.dd>.ic-text");
                    for(Element el : offerExp){
                        vo.setId(dao.getMaxId("offer_exp"));
                        vo.setOfferExp(el.text());
                        dao.addExp(vo);
                        log.info(el.text());
                    }
                }

                log.info("");
            }
        }
    }




    public void addStats(Map<String, Integer> map) throws SQLException, ClassNotFoundException {
        SkillStatsDAO dao = new Factory().skillStatsDAO();

        for (String key : map.keySet()){
            int value = map.get(key);
            Domain vo = new Domain();
            vo.setId(dao.getMaxId("offer_skill"));
            vo.setSkillCount(value);
            vo.setSkillName(key);
            //dao.add(vo);
            log.info(key + " : " + value);
        }
    }

    public List sortByValue(final Map<String,Integer> map){
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
}
