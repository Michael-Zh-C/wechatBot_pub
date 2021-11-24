package com.robot.dao.impl;

import com.robot.dao.GroupAnalysisSwitchDao;
import com.robot.pojo.GroupAnalysisSwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class GroupAnalysisSwitchDaoImpl implements GroupAnalysisSwitchDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> selectAll() {
        String sql = "SELECT chat_room_id AS chatRoomId,state FROM t_group_analysis_switch";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public void edit(GroupAnalysisSwitch groupAnalysisSwitch) {
        String sql = "insert into t_group_analysis_switch (chat_room_id,state) values (?,?) " +
                "ON DUPLICATE KEY UPDATE state = ?";
        jdbcTemplate.update(sql,groupAnalysisSwitch.getGroupId(),groupAnalysisSwitch.getState(),groupAnalysisSwitch.getState());
    }


}
