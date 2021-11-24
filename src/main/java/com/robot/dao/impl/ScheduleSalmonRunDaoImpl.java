package com.robot.dao.impl;

import com.robot.dao.ScheduleSalmonRunDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ScheduleSalmonRunDaoImpl implements ScheduleSalmonRunDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getSalmonRunSchedule(long currentTime, int nextTime) {
        String sql = "select t1.start_time AS startTime,t1.end_time AS endTime,t2.chinese_name AS mapName,\n" +
                "t2.picture_name AS mapImg,t3.chinese_name AS w1Name,t3.picture_name AS w1Img,\n" +
                "t4.chinese_name AS w2Name,t4.picture_name AS w2Img,t5.chinese_name AS w3Name,\n" +
                "t5.picture_name AS w3Img,t6.chinese_name AS w4Name,t6.picture_name AS w4Img \n" +
                "FROM t_schedule_salmonrun t1\n" +
                "INNER JOIN t_salmon_run_stage_info t2 ON t1.map = t2.id \n" +
                "INNER JOIN t_weapon_main t3 ON t1.weapon1 = t3.id \n" +
                "INNER JOIN t_weapon_main t4 ON t1.weapon2 = t4.id \n" +
                "INNER JOIN t_weapon_main t5 ON t1.weapon3 = t5.id \n" +
                "INNER JOIN t_weapon_main t6 ON t1.weapon4 = t6.id where t1.end_time > " + currentTime +
                " order by t1.id asc limit " + nextTime + ",2";

        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);

        return result;
    }
}
