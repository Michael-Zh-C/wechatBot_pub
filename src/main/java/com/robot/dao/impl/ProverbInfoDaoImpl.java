package com.robot.dao.impl;

import com.robot.dao.ProverbInfoDao;
import com.robot.pojo.ProverbInfo;
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
public class ProverbInfoDaoImpl implements ProverbInfoDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, ProverbInfo> selectAll() {
        Map<String, ProverbInfo> resultMap = new HashMap<>();
        String sql = "SELECT * FROM t_proverb_info";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);
        for (Map<String,Object> map:result) {
            ProverbInfo proverbInfo = new ProverbInfo();
            proverbInfo.setId((Integer) map.get("id"));
//            proverbInfo.setDerivation((String) map.get("derivation"));
//            proverbInfo.setExample((String) map.get("example"));
//            proverbInfo.setExplanation((String) map.get("explanation"));
            proverbInfo.setPinyin((String) map.get("pinyin"));
            proverbInfo.setWord((String) map.get("word"));
            proverbInfo.setAbbreviation((String) map.get("abbreviation"));

            resultMap.put((String) map.get("word"),proverbInfo);
        }
        return resultMap;
    }
}
