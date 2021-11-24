package com.robot.dao.impl;

import com.robot.dao.LotteryInfoDao;
import com.robot.pojo.LotteryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhang
 */
@Repository
public class LotteryInfoDaoImpl implements LotteryInfoDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public LotteryInfo queryLotteryInfoById(int id) {
        String sql = "SELECT id,lottery_name AS lotteryName,lottery_content AS lotteryContent,lottery_answer AS lotteryAnswer " +
                "FROM t_lottery_info WHERE id = :id";

        Map<String ,Object> params = new HashMap<>();
        params.put("id",id);

        return (LotteryInfo) jdbcTemplate.query(sql,params,new LotteryInfo()).get(0);
    }

    @Override
    public List<Integer> getIds() {
        String sql = "select id from t_lottery_info";

        List<Integer> resultList = jdbcTemplate.queryForList(sql,new HashMap<String ,Object>(),Integer.class);

        return resultList;
    }


}
