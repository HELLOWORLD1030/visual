package top.zzgpro.programdesighgame.Dao;

import top.zzgpro.programdesighgame.PO.YJYWContentPO;

import java.util.ArrayList;

/**
 * 应急要闻数据持久层接口
 */
public interface YJYWDao {
    void add(YJYWContentPO yjywContentPO);
    void addfromList(ArrayList<YJYWContentPO> yjywContentPOArrayList);
    void update(YJYWContentPO yjywContentPO);
    void delete(YJYWContentPO yjywContentPO);
    YJYWContentPO query(YJYWContentPO yjywContentPO);
    YJYWContentPO queryLastone(int type);
}
