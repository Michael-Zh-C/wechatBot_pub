package com.robot.dao.impl;

import com.robot.dao.SongInfoDao;
import com.robot.pojo.SongInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class SongInfoDaoImpl implements SongInfoDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public List<Integer> getIds() {
        String sql = "select id from t_song_info";
        List<Integer> resultList = jdbcTemplate.queryForList(sql,Integer.class);
        return resultList;
    }

    @Override
    public SongInfo getSongDetail(int id) {
        String sql = "SELECT id,song_name AS songName,singer_name AS singerName,file FROM t_song_info WHERE id = ?";
        Map<String,Object> resultMap = jdbcTemplate.queryForMap(sql,id);

        SongInfo songInfo = new SongInfo();
        songInfo.setId((Integer) resultMap.get("id"));
        songInfo.setSongName((String) resultMap.get("songName"));
        songInfo.setSingerName((String) resultMap.get("singerName"));
        songInfo.setFile((String) resultMap.get("file"));

        return songInfo;
    }

    @Override
    public int isSongExist(String songName) {
        String sql = "SELECT count(*) FROM t_song_info WHERE song_name = ?";
        return jdbcTemplate.queryForObject(sql,Integer.class,songName);
    }
}
