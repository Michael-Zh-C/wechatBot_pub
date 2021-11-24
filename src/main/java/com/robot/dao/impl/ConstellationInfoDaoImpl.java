package com.robot.dao.impl;

import com.robot.dao.ConstellationInfoDao;
import com.robot.pojo.ConstellationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class ConstellationInfoDaoImpl implements ConstellationInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public void add(ConstellationInfo constellationInfo) {
        String sql = "INSERT INTO t_constellation_info (currentDate,allNum,colorNum,healthNum,loveNum,moneyNum,luckyNumber,qFriend,workNum,summary,constellationName)" +
                " VALUES (:currentDate,:allNum,:colorNum,:healthNum,:loveNum,:moneyNum,:luckyNumber,:qFriend,:workNum,:summary,:constellationName)";
        Map<String ,Object> params = new HashMap<>();
        params.put("currentDate",constellationInfo.getCurrentDate());
        params.put("allNum",constellationInfo.getAllNum());
        params.put("colorNum",constellationInfo.getColor());
        params.put("healthNum",constellationInfo.getHealthNum());
        params.put("loveNum",constellationInfo.getLoveNum());
        params.put("moneyNum",constellationInfo.getMoneyNum());
        params.put("luckyNumber",constellationInfo.getLuckyNumber());
        params.put("qFriend",constellationInfo.getqFriend());
        params.put("workNum",constellationInfo.getWorkNum());
        params.put("summary",constellationInfo.getSummary());
        params.put("constellationName",constellationInfo.getConstellationName());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public ConstellationInfo queryToday(String dateTime, String constellationName) {
        String sql = "SELECT id,currentDate,allNum,colorNum,healthNum,loveNum,moneyNum,luckyNumber,qFriend,workNum,summary,constellationName " +
                "FROM t_constellation_info WHERE constellationName = :constellationName AND currentDate >= :datetimeStart AND currentDate <= :datetimeEnd";
        String datetimeStart = dateTime + " 00:00:00";
        String datetimeEnd = dateTime + " 23:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String ,Object> params = new HashMap<>();
        params.put("constellationName",constellationName);
        try {
            params.put("datetimeStart",simpleDateFormat.parse(datetimeStart).getTime());
            params.put("datetimeEnd",simpleDateFormat.parse(datetimeEnd).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (ConstellationInfo) jdbcTemplate.query(sql,params,new ConstellationInfo()).get(0);
    }

    @Override
    public int checkCountToday(String dateTime, String constellationName) {
        String sql = "SELECT count(*) FROM t_constellation_info WHERE constellationName = :constellationName AND currentDate >= :datetimeStart AND currentDate <= :datetimeEnd";
        String datetimeStart = dateTime + " 00:00:00";
        String datetimeEnd = dateTime + " 23:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String ,Object> params = new HashMap<>();
        params.put("constellationName",constellationName);
        try {
            params.put("datetimeStart",simpleDateFormat.parse(datetimeStart).getTime());
            params.put("datetimeEnd",simpleDateFormat.parse(datetimeEnd).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  jdbcTemplate.queryForObject(sql,params,Integer.class);
    }


}
