package top.zzgpro.programdesighgame.Service;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import top.zzgpro.programdesighgame.Dao.YJYWDao;
import top.zzgpro.programdesighgame.PO.YJYWContentPO;
import top.zzgpro.programdesighgame.Utils.Httputil;
import top.zzgpro.programdesighgame.VO.YJYWContentVO;

/**
 * @author HELLO_WORLD
 */
@Service
public class EmergencyManagementNewsService {
    @Autowired
    public void setYjywDao(YJYWDao yjywDao) {
        this.yjywDao = yjywDao;
    }

    private YJYWDao yjywDao;
    private String baseURL="https://www.me//m.gov.cn/xw/yjyw/";

    public void doService(){

    }
    public String getNews(){
        try{
//
//
            //获取时间
            ArrayList<YJYWContentPO> dailynews = (ArrayList<YJYWContentPO>) getYjywContent(0);
            ArrayList<YJYWContentPO>othernews = (ArrayList<YJYWContentPO>) getNewsContent(0);
            if(CompareTime(othernews.get(0),1)){
                yjywDao.addfromList(othernews);
            }
            if(CompareTime(dailynews.get(0),0)){
                yjywDao.addfromList(dailynews);
            }

            return "";
        }
        catch (IOException ioException){
            ioException.printStackTrace();
            return null;
        }
        catch (KeyStoreException| KeyManagementException| NoSuchAlgorithmException ex){
            return null;
        }
    }

        private String fetchNewsHTML(String url) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
       CloseableHttpClient httpClient = Httputil.getIgnoeSSLClient();
        HttpGet httpGet=new HttpGet(url);
        httpGet.addHeader("Content-Type","text/html; charset=utf-8");
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36 Edg/97.0.1072.69");
        CloseableHttpResponse httpResponse=httpClient.execute(httpGet);
        HttpEntity entity=httpResponse.getEntity();

        return EntityUtils.toString(entity,"utf8");
    }

    /**
     * 获取应急要问其他新闻
     * @param index
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    private ArrayList<? extends YJYWContentPO> getNewsContent(int index) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String url="https://www.mem.gov.cn/xw/yjyw/index_"+index+".shtml";
        if(index==0){
            url="https://www.mem.gov.cn/xw/yjyw/";
        }
        String html=fetchNewsHTML(url);
        Document doc = Jsoup.parse(html);
        ArrayList<YJYWContentPO> yjywList=new ArrayList<>(10);
        Elements root = doc.getElementsByClass("cont");
        for (Element rootElement : root) {
            Elements elements=rootElement.getElementsByTag("li");
            for (Element element : elements) {
                Element elementRoot=element.children().first();
                YJYWContentPO yjywContentVO=new YJYWContentVO();
                yjywContentVO.setLink(elementRoot.attr("href"));
                yjywContentVO.setTime(elementRoot.select("span").first().text());
                yjywContentVO.setTitle(elementRoot.ownText());
                yjywContentVO.setType(1);
                yjywList.add(yjywContentVO);
//            yjywList.add()
            }
        }
        return yjywList;
    }

    /**
     * 获取应急要问每日新闻
     * @param index
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    private ArrayList<?extends YJYWContentPO> getYjywContent(int index)throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException{
        String url="https://www.mem.gov.cn/xw/yjyw/index_1863_"+index+".shtml";
        if(index==0){
            url="https://www.mem.gov.cn/xw/yjyw/index_1863.shtml";
        }
        String html=fetchNewsHTML(url);
        Document doc = Jsoup.parse(html);
        Element root = doc.getElementsByClass("cont").first();
        Elements elements=root.getElementsByTag("li");
        ArrayList<YJYWContentPO> yjywList=new ArrayList<>(6);
        for (Element element : elements) {
            Element elementRoot=element.children().first();
            YJYWContentPO yjywContentVO=new YJYWContentVO();
            yjywContentVO.setLink(elementRoot.attr("href"));
            yjywContentVO.setTime(elementRoot.select("span").first().text());
            yjywContentVO.setTitle(elementRoot.ownText());
            yjywContentVO.setType(0);
            yjywList.add(yjywContentVO);
//            yjywList.add()
        }
        return yjywList;
        //System.out.println(yjywList);
    }
    private void GetAllPage() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException, IOException {
        for(int i=0;i<100;i++){
                    ArrayList<YJYWContentPO> dailynews = (ArrayList<YJYWContentPO>) getYjywContent(i);
                    ArrayList<YJYWContentPO>othernews = (ArrayList<YJYWContentPO>) getNewsContent(i);
                    yjywDao.addfromList(dailynews);
                    yjywDao.addfromList(othernews);
//                }
    }
        }
    private boolean CompareTime(YJYWContentPO NewGet,int type){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        YJYWContentPO LastDailyFromDB = yjywDao.queryLastone(type);
        LocalDateTime LastTimeFromDB = LocalDateTime.parse(LastDailyFromDB.getTime(), inputFormatter);
        LocalDateTime DailyNewsTime = LocalDateTime.parse(NewGet.getTime(),inputFormatter);
        return DailyNewsTime.isAfter(LastTimeFromDB);

        }
    }


