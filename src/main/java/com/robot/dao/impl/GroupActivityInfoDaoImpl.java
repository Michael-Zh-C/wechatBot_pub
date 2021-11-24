package com.robot.dao.impl;

import com.robot.dao.GroupActivityInfoDao;
import com.robot.pojo.GroupActivityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class GroupActivityInfoDaoImpl implements GroupActivityInfoDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void add(GroupActivityInfo groupActivityInfo) {
        String sql = "INSERT INTO t_group_activity_info (chat_room_id,user_name,time,msg_count) " +
                "VALUES (:chatRoomId,:userName,:time,:msgCount) ON DUPLICATE KEY UPDATE " +
                "msg_count = :msgCount";

        Map<String,Object> params = new HashMap<>();
        params.put("chatRoomId",groupActivityInfo.getChatRoomId());
        params.put("userName",groupActivityInfo.getUserName());
        params.put("time",groupActivityInfo.getTime());
        params.put("msgCount",groupActivityInfo.getMsgCount());

        namedParameterJdbcTemplate.update(sql,params);
    }

    @Override
    public List<Map<String, Object>> queryTodayAll(String dateTime) {
        String sql = "SELECT chat_room_id AS chatRoomId,user_name AS userName,time AS time,msg_count AS msgCount" +
                " FROM t_group_activity_info WHERE time >=? AND time <?";

        String datetimeStart = dateTime + " 00:00:00";
        String datetimeEnd = dateTime + " 23:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            System.out.println("queryTodayAll --- " + datetimeStart + "---" + simpleDateFormat.parse(datetimeStart).getTime() +
                    "---" + datetimeEnd + "---" + simpleDateFormat.parse(datetimeEnd).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = jdbcTemplate.queryForList(sql,simpleDateFormat.parse(datetimeStart).getTime(),simpleDateFormat.parse(datetimeEnd).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }
}
