package top.zzgpro.programdesighgame.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import top.zzgpro.programdesighgame.PO.YJYWContentPO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
/**
 * 应急要闻数据持久层实现
 */
public class YJYWDaoImpl implements YJYWDao{
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;
    @Override
    public void add(YJYWContentPO yjywContentPO) {

    }

    /**
     * 从ArrayList中获取元素，批量存入数据库
     * @param yjywContentPOArrayList
     */
    @Override
    public void addfromList(ArrayList<YJYWContentPO> yjywContentPOArrayList) {
        String sql="INSERT INTO `yjywinfo`(`title`, `link`, `type`, `publishtime`) VALUES (?,?,?,str_to_date(?,'%Y-%m-%d %H:%i'))";
        //重载部分方法，自定义插入的内容
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                YJYWContentPO yjywContentPO=(YJYWContentPO) yjywContentPOArrayList.get(i);
                ps.setString(1,yjywContentPO.getTitle());
                ps.setString(2,yjywContentPO.getLink());
                ps.setInt(3,yjywContentPO.getType());
                ps.setString(4,yjywContentPO.getTime());
            }

            @Override
            public int getBatchSize() {
                return yjywContentPOArrayList.size();
            }
        });
    }

    @Override
    public void update(YJYWContentPO yjywContentPO) {

    }

    @Override
    public void delete(YJYWContentPO yjywContentPO) {

    }

    @Override
    public YJYWContentPO query(YJYWContentPO yjywContentPO) {

        return null;
    }

    /**
     * 从数据库取出最晚的一条数据
     * @param type 新闻类型
     * @return
     */
    @Override
    public YJYWContentPO queryLastone(int type) {
        String sql="SELECT `title`,`link`,DATE_FORMAT(publishtime,'%Y-%m-%d %H:%i') as time  FROM `yjywinfo` WHERE type=? ORDER BY publishtime desc limit 1";
        YJYWContentPO LastInfo = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<YJYWContentPO>(YJYWContentPO.class),type);
        return LastInfo;
    }
}
