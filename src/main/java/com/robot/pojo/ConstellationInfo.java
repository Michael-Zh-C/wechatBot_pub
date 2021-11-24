package com.robot.pojo;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Michael
 */
public class ConstellationInfo implements RowMapper {
    private int id;
    private long currentDate;
    private int allNum;
    private String color;
    private int healthNum;
    private int loveNum;
    private int moneyNum;
    private int luckyNumber;
    private String qFriend;
    private int workNum;
    private String summary;
    private String constellationName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getHealthNum() {
        return healthNum;
    }

    public void setHealthNum(int healthNum) {
        this.healthNum = healthNum;
    }

    public int getLoveNum() {
        return loveNum;
    }

    public void setLoveNum(int loveNum) {
        this.loveNum = loveNum;
    }

    public int getMoneyNum() {
        return moneyNum;
    }

    public void setMoneyNum(int moneyNum) {
        this.moneyNum = moneyNum;
    }

    public int getLuckyNumber() {
        return luckyNumber;
    }

    public void setLuckyNumber(int luckyNumber) {
        this.luckyNumber = luckyNumber;
    }

    public String getqFriend() {
        return qFriend;
    }

    public void setqFriend(String qFriend) {
        this.qFriend = qFriend;
    }

    public int getWorkNum() {
        return workNum;
    }

    public void setWorkNum(int workNum) {
        this.workNum = workNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getConstellationName() {
        return constellationName;
    }

    public void setConstellationName(String constellationName) {
        this.constellationName = constellationName;
    }

    @Override
    public ConstellationInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        ConstellationInfo constellationInfo = new ConstellationInfo();
        constellationInfo.setId(resultSet.getInt(1));
        constellationInfo.setCurrentDate(resultSet.getLong(2));
        constellationInfo.setAllNum(resultSet.getInt(3));
        constellationInfo.setColor(resultSet.getString(4));
        constellationInfo.setHealthNum(resultSet.getInt(5));
        constellationInfo.setLoveNum(resultSet.getInt(6));
        constellationInfo.setMoneyNum(resultSet.getInt(7));
        constellationInfo.setLuckyNumber(resultSet.getInt(8));
        constellationInfo.setqFriend(resultSet.getString(9));
        constellationInfo.setWorkNum(resultSet.getInt(10));
        constellationInfo.setSummary(resultSet.getString(11));
        constellationInfo.setConstellationName(resultSet.getString(12));

        return constellationInfo;
    }
}
