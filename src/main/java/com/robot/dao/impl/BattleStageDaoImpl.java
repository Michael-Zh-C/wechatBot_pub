package com.robot.dao.impl;

import com.robot.dao.BattleStageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class BattleStageDaoImpl implements BattleStageDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> getStageIdList() {
        String sql = "select id from t_battle_stage_info";

        List<Integer> resultList = jdbcTemplate.queryForList(sql,Integer.class);

        return resultList;
    }

    @Override
    public Map<String, Object> getStageInfoById(int id) {
        String sql = "SELECT id,english_name AS englishName,chinese_name AS chineseName,picture_name AS pictureName" +
                " from t_battle_stage_info where id = " + id;
        Map<String,Object> resultMap = jdbcTemplate.queryForMap(sql);
        System.out.println("resultMap = " + resultMap);
        return resultMap;
    }
}
