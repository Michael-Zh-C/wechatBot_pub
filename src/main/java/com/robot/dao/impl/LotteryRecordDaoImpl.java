package com.robot.dao.impl;

import com.robot.dao.LotteryRecordDao;
import com.robot.pojo.LotteryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhang
 */
@Repository
public class LotteryRecordDaoImpl implements LotteryRecordDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public void add(LotteryRecord lotteryRecord) {
        String sql = "INSERT INTO t_lottery_record (user_name,today_lottery,today_timestamp) VALUES (?,?,?);";
        jdbcTemplate.update(sql,lotteryRecord.getUserName(),lotteryRecord.getTodayLottery(),lotteryRecord.getTodayTimestamp());
    }

    @Override
    public Map<String, Integer> getTodayLotteryRecord() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT user_name AS userName,today_lottery AS todayLottery FROM t_lottery_record";
        List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);

        for (Map<String,Object> map:queryForList) {
            result.put((String) map.get("userName"),(Integer) map.get("todayLottery"));
        }

        return result;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM t_lottery_record";
        jdbcTemplate.update(sql);
    }
}
