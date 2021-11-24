package com.robot.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorization implements RowMapper {
    private int id;
    private String authorization;
    private String wechatId;
    private String wId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getwId() {
        return wId;
    }

    public void setwId(String wId) {
        this.wId = wId;
    }

    @Override
    public Authorization mapRow(ResultSet resultSet, int i) throws SQLException {
        Authorization authorization = new Authorization();
        authorization.setId(resultSet.getInt(1));
        authorization.setAuthorization(resultSet.getString(2));
        authorization.setWechatId(resultSet.getString(3));
        authorization.setwId(resultSet.getString(4));
        return authorization;
    }
}
