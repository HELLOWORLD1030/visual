package top.zzgpro.programdesighgame.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zzgpro.programdesighgame.Service.EmergencyManagementNewsService;

@Controller
public class MainController {
    private EmergencyManagementNewsService newsService;
    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }
    @ResponseBody
    @RequestMapping("/news")
    public String getNews(){
        newsService.getNews();
       return "";
    }
    @Autowired
    public void setNewsService(EmergencyManagementNewsService newsService) {
        this.newsService = newsService;
    }
}
