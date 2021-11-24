package com.robot.dao.impl;

import com.robot.dao.ScheduleBattleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ScheduleBattleDaoImpl implements ScheduleBattleDao {
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public List<Map<String, Object>> getBattleSchedule(long currentTime, int nextTime3) {
        String sql = "select t1.type AS type,t1.game_mode AS gameMode,t1.start_time AS startTime,\n" +
                "t1.end_time AS endTime,t2.chinese_name AS map1Name,t2.picture_name AS map1Pic,\n" +
                "t3.chinese_name AS map2Name,t3.picture_name AS map2Pic \n" +
                "FROM t_schedule_battle t1 \n" +
                "INNER JOIN t_battle_stage_info t2 on t1.map1 = t2.id\n" +
                "INNER JOIN t_battle_stage_info t3 ON t1.map2 = t3.id\n" +
                "where t1.end_time > "+ currentTime +" ORDER BY t1.end_time,t1.type ASC limit " + nextTime3 + ",3";

        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    @Override
    public long getTypeModeBattleTime(long currentTime, int gameType, int mode, int nextTime) {
        String sql = "SELECT start_time FROM t_schedule_battle " +
                "WHERE type = " + gameType + " and game_mode = " + mode + " and end_time > "+ currentTime +
                " ORDER BY end_time ASC LIMIT " + nextTime + ",1";
        long result = 0L;

        try {
            result = jdbcTemplate.queryForObject(sql,Long.class);
        } catch (Exception e) {
            System.out.println("数据库没有这条数据");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getBattleScheduleByCurrentTime(long currentTime) {
        String sql = "select t1.type AS type,t1.game_mode AS gameMode,t1.start_time AS startTime,\n" +
                "t1.end_time AS endTime,t2.chinese_name AS map1Name,t2.picture_name AS map1Pic,\n" +
                "t3.chinese_name AS map2Name,t3.picture_name AS map2Pic \n" +
                "FROM t_schedule_battle t1 \n" +
                "INNER JOIN t_battle_stage_info t2 on t1.map1 = t2.id\n" +
                "INNER JOIN t_battle_stage_info t3 ON t1.map2 = t3.id\n" +
                "where t1.start_time = "+ currentTime +" ORDER BY t1.end_time,t1.type ASC limit 0,3";

        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }


}
