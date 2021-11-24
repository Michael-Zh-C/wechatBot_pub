package com.robot.dao.impl;

/**
 * @author Michael
 */

import com.robot.dao.GroupMemberInfoDao;
import com.robot.pojo.GroupMemberInfo;
import com.robot.service.GroupChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GroupMemberInfoDaoImpl implements GroupMemberInfoDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    GroupChatService groupChatService;
    @Override
    public void add(GroupMemberInfo groupMemberInfo) {
//        String sql = "INSERT INTO t_group_member_info " +
//                "(chat_room_id,user_name,nick_name,display_name,big_head_img_url,small_head_img_url) " +
//                "VALUES (:chatRoomId,:userName,:nickName,:displayName,:bigHeadImgUrl,:smallHeadImgUrl) " +
//                "ON DUPLICATE KEY UPDATE" +
//                " nick_name = :nickName,display_name = :displayName,big_head_img_url = :bigHeadImgUrl,small_head_img_url = :smallHeadImgUrl";

        String sql = "INSERT INTO t_group_member_info " +
                "(chat_room_id,user_name,nick_name,display_name,big_head_img_url,small_head_img_url) " +
                "VALUES (?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE" +
                " nick_name = ?,display_name = ?,big_head_img_url = ?,small_head_img_url = ?,is_manager = ?";

        jdbcTemplate.execute("set names utf8mb4");

        jdbcTemplate.update(sql,groupMemberInfo.getChatRoomId(),groupMemberInfo.getUserName(),groupMemberInfo.getNickName()
                ,groupMemberInfo.getDisplayName(),groupMemberInfo.getBigHeadImgUrl(),groupMemberInfo.getSmallHeadImgUrl()
                ,groupMemberInfo.getNickName(),groupMemberInfo.getDisplayName(),groupMemberInfo.getBigHeadImgUrl(),groupMemberInfo.getSmallHeadImgUrl(),0);
    }

    @Override
    public void delete(GroupMemberInfo groupMemberInfo) {
        String sql = "DELETE FROM t_group_member_info WHERE chat_room_id = :chatRoomId AND user_name = :userName";

        Map<String ,Object> params = new HashMap<>();
        params.put("chatRoomId",groupMemberInfo.getChatRoomId());
        params.put("userName",groupMemberInfo.getUserName());

        namedParameterJdbcTemplate.update(sql,params);
    }

    @Override
    public void edit(GroupMemberInfo groupMemberInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append("update t_group_member_info set ");
        Map<String ,Object> params = new HashMap<>();
        params.put("chatRoomId",groupMemberInfo.getChatRoomId());
        params.put("userName",groupMemberInfo.getUserName());

        if (StringUtils.isNotBlank(groupMemberInfo.getNickName())) {
            sb.append("nick_name = :nickName");
            sb.append(",");
            params.put("nickName",groupMemberInfo.getNickName());
        }

        if (StringUtils.isNotBlank(groupMemberInfo.getDisplayName())) {
            sb.append("display_name = :displayName");
            sb.append(",");
            params.put("displayName",groupMemberInfo.getDisplayName());
        }

        if (StringUtils.isNotBlank(groupMemberInfo.getBigHeadImgUrl())) {
            sb.append("big_head_img_url = :bigHeadImgUrl");
            sb.append(",");
            params.put("bigHeadImgUrl",groupMemberInfo.getBigHeadImgUrl());
        }

        if (StringUtils.isNotBlank(groupMemberInfo.getSmallHeadImgUrl())) {
            sb.append("small_head_img_url = :smallHeadImgUrl");
            sb.append(",");
            params.put("smallHeadImgUrl",groupMemberInfo.getSmallHeadImgUrl());
        }

        if (groupMemberInfo.getIsManager() > 0) {
            sb.append("is_manager = :isManager");
            sb.append(",");
            params.put("isManager",groupMemberInfo.getIsManager());
        }

        if (groupMemberInfo.getLastActivity() != null) {
            sb.append("last_activity = :lastActivity");
            sb.append(",");
            params.put("lastActivity",groupMemberInfo.getLastActivity());
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(" where chat_room_id = :chatRoomId and user_name = :userName");
        String sql = sb.toString();
        System.out.println("GroupMemberInfoDaoImpl - edit sql : " + sql);
        System.out.println("params = " + params);

        namedParameterJdbcTemplate.update(sql,params);
    }

    @Override
    public String queryNickName(GroupMemberInfo groupMemberInfo) {
        String sql = "SELECT nick_name AS nickName,display_name AS displayName FROM t_group_member_info WHERE chat_room_id = :chatRoomId AND user_name = :userName";
        Map<String ,Object> params = new HashMap<>();
        params.put("chatRoomId",groupMemberInfo.getChatRoomId());
        params.put("userName",groupMemberInfo.getUserName());

        Map<String,Object> map;
        //如果有新进群成员导致查询报错，先更新群成员信息再查询
        try {
            map = namedParameterJdbcTemplate.queryForMap(sql,params);
        } catch (Exception e) {
            System.out.println("群成员信息不存在，更新信息后重新查询");
            groupChatService.updateGroupMemberInfo(groupMemberInfo.getChatRoomId());
            map = namedParameterJdbcTemplate.queryForMap(sql,params);
        }

        if (StringUtils.isNotBlank((String) map.get("displayName"))) {
            return (String) map.get("displayName");
        } else {
            return (String) map.get("nickName");
        }
    }

    @Override
    public int queryIsManager(GroupMemberInfo groupMemberInfo) {
        String sql = "SELECT is_manager AS isManager FROM t_group_member_info WHERE chat_room_id = ? AND user_name = ?";
        return jdbcTemplate.queryForObject(sql,Integer.class,groupMemberInfo.getChatRoomId(),groupMemberInfo.getUserName());
    }


}
