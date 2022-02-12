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
 *  应急管理部应急新闻服务
 */
@Service
public class EmergencyManagementNewsService {
    @Autowired
    public void setYjywDao(YJYWDao yjywDao) {
        this.yjywDao = yjywDao;
    }

    private YJYWDao yjywDao;

    /**
     * 真正暴露出来给controllor调用的方法，待完善
     */
    public void doService(){

    }

    /**
     *目前被controller调用的方法，用于实现每日要闻和其他新闻的获取，以及存入数据库
     * 实现方法待优化，仅实现功能
     * @return
     */
    public String getNews(){
        try{
            /**
             * 获取每日应急要闻和其他新闻
             */
            ArrayList<YJYWContentPO> dailynews = (ArrayList<YJYWContentPO>) getYjywContent(0);
            ArrayList<YJYWContentPO>othernews = (ArrayList<YJYWContentPO>) getNewsContent(0);
            /**
             * 比较获取的新闻时间和数据库时间最近的一条数据，看是否有新数据，如果是新数据，加入数据库
             */
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

    /**
     * 依据url获取html源码
     * @param url
     * @return html源码
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
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
     * 获取其他新闻
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
        //解析html，拿到我们想要的数据
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
        //解析html，拿到我们想要的数据
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
        }
        return yjywList;
    }

    /**
     * 拿到所有页面的数据，完完整整的爬虫，并存入数据库
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private void GetAllPage() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException, IOException {
        for(int i=0;i<100;i++){
                    ArrayList<YJYWContentPO> dailynews = (ArrayList<YJYWContentPO>) getYjywContent(i);
                    ArrayList<YJYWContentPO>othernews = (ArrayList<YJYWContentPO>) getNewsContent(i);
                    yjywDao.addfromList(dailynews);
                    yjywDao.addfromList(othernews);
                }
        }

    /**
     *
     * @param NewGet 新获取的新闻数据
     * @param type 类型 0是每日新闻，1是其他新闻
     * @return
     */
    private boolean CompareTime(YJYWContentPO NewGet,int type){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        YJYWContentPO LastDailyFromDB = yjywDao.queryLastone(type);
        LocalDateTime LastTimeFromDB = LocalDateTime.parse(LastDailyFromDB.getTime(), inputFormatter);
        LocalDateTime DailyNewsTime = LocalDateTime.parse(NewGet.getTime(),inputFormatter);
        return DailyNewsTime.isAfter(LastTimeFromDB);
        }
    }


