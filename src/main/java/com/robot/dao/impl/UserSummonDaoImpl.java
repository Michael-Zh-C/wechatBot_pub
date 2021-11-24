package com.robot.dao.impl;

import com.robot.dao.UserSummonDao;
import com.robot.pojo.UserSummon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class UserSummonDaoImpl implements UserSummonDao {
    @Autowired JdbcTemplate jdbcTemplate;


    @Override
    public void add(UserSummon userSummon) {
        String sql = "insert into t_user_summon (from_user,is_random,pet) values (?,?,?)";
        jdbcTemplate.execute("set names utf8mb4");
        jdbcTemplate.update(sql,userSummon.getFromUser(),userSummon.getIsRandom(),userSummon.getPet());
    }

    @Override
    public void edit(UserSummon userSummon) {

    }

    @Override
    public int selectUserCount(UserSummon userSummon) {
        String sql = "SELECT COUNT(*) FROM t_user_summon WHERE from_user = ? AND is_random = ?";
        return jdbcTemplate.queryForObject(sql,Integer.class,userSummon.getFromUser(),userSummon.getIsRandom());
    }

    @Override
    public void delete(UserSummon userSummon) {
        String sql = "DELETE FROM t_user_summon WHERE from_user = ? AND pet = ?";
        jdbcTemplate.update(sql,userSummon.getFromUser(),userSummon.getPet());
    }

    @Override
    public Map<String, List<String>> selectAll() {
        String sql = "SELECT from_user AS fromUser,is_random AS isRamdom,pet AS pet FROM t_user_summon";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);

        Map<String, List<String>> resultMap = new HashMap<>();
        for (Map<String,Object> map : result) {
            if (resultMap.containsKey(map.get("fromUser"))) {
                List<String> list = resultMap.get(map.get("fromUser"));
                list.add((String) map.get("pet"));
            } else {
                List<String> list = new ArrayList<>();
                list.add((String) map.get("pet"));
                resultMap.put((String) map.get("fromUser"),list);
            }
        }
        return resultMap;
    }
}
