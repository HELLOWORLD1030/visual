package top.zzgpro.programdesighgame.PO;

import top.zzgpro.programdesighgame.VO.YJYWContentVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author HELLO_WORLD
 */
public class YJYWContentPO  {
    private String title;
    private String link;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    private Integer type;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTime() {
        return time.toString();
    }

    public void setTime(String time) {
        this.time=time;
       //this.time = LocalDateTime.parse(time, formatter);
    }

    private String time;
    @Override
    public String toString(){
        return title+" "+link+" "+time+" "+type;
    }
}
