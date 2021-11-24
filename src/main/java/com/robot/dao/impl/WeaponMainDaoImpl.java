package com.robot.dao.impl;

import com.robot.dao.WeaponMainDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class WeaponMainDaoImpl implements WeaponMainDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Integer> getWeaponIdList(int position){
        String sql = "select id from t_weapon_main";
        if (position > 0){
            sql = sql + " where position = " + position;
        } else {
            sql = sql + " where position > 0 ";
        }
        List<Integer> resultList = jdbcTemplate.queryForList(sql,Integer.class);

        return resultList;
    }

    @Override
    public Map<String,Object> getWeaponDetail(int weaponId){
        String sql = "SELECT t1.id,t1.english_name,t1.chinese_name AS mainName,t1.picture_name AS mainPicture," +
                "t2.chinese_name AS subName,t2.picture_name AS subPicture," +
                "t3.chinese_name AS specialName,t3.picture_name AS specialPicture " +
                "from t_weapon_main t1 INNER JOIN t_weapon_sub t2 ON t1.sub = t2.id " +
                "INNER JOIN t_weapon_special t3 ON t1.special = t3.id where t1.id = " + weaponId;

        Map<String,Object> resultMap = jdbcTemplate.queryForMap(sql);
        System.out.println("resultMap = " + resultMap);
        return resultMap;
    }
}
