package com.robot.dao.impl;

import com.robot.dao.MessageAutoReplyDao;
import com.robot.pojo.MessageAutoReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class MessageAutoReplyDaoImpl implements MessageAutoReplyDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public void add(MessageAutoReply messageAutoReply) {
        String sql = "insert into t_message_auto_reply (from_group,message_key,message_value) values (?,?,?)";
        jdbcTemplate.execute("set names utf8mb4");
        jdbcTemplate.update(sql,messageAutoReply.getFromGroup(),messageAutoReply.getMessageKey(),messageAutoReply.getMessageValue());
    }

    @Override
    public void edit(MessageAutoReply messageAutoReply) {
        String sql = "update t_message_auto_reply set from_group = ?,message_key = ?,message_value = ? where id = ?";
        jdbcTemplate.execute("set names utf8mb4");
        jdbcTemplate.update(sql,messageAutoReply.getFromGroup(),messageAutoReply.getMessageKey(),messageAutoReply.getMessageValue(),messageAutoReply.getId());
    }

    @Override
    public Map<String, Map<String, String>> selectAll() {
        String sql = "SELECT id,from_group AS fromGroup,message_key AS messageKey,message_value AS messageValue from t_message_auto_reply";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);

        Map<String, Map<String, String>> resultMap = new HashMap<>();

        for (Map<String,Object> map : result) {
            if (resultMap.containsKey(map.get("fromGroup"))){
                resultMap.get(map.get("fromGroup")).put((String)map.get("messageKey"),(String) map.get("messageValue"));
            } else {
                Map<String,String> messageMap = new HashMap<>();
                messageMap.put((String)map.get("messageKey"),(String) map.get("messageValue"));

                resultMap.put((String)map.get("fromGroup"),messageMap);
            }
        }
        return resultMap;
    }

    @Override
    public int getMessageId(MessageAutoReply messageAutoReply) {
        String sql = "select id from t_message_auto_reply where from_group = '" + messageAutoReply.getFromGroup() +
                "' and message_key = '" + messageAutoReply.getMessageKey() + "'";

        return jdbcTemplate.queryForObject(sql,Integer.class);
    }
}
