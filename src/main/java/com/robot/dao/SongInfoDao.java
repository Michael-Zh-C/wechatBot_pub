package com.robot.dao;

import com.robot.pojo.SongInfo;

import java.util.List;

/**
 * @author Michael
 */
public interface SongInfoDao {
    /**
     * 获取ID列表
     * @return
     * */
    List<Integer> getIds();

    /**
     * 获取歌曲详情
     * @param id
     * @return
     * */
    SongInfo getSongDetail(int id);

    /**
     * 查询这首歌是否存在
     * @param songName
     * @return 是 1 否 0
     * */
    int isSongExist(String songName);
}
