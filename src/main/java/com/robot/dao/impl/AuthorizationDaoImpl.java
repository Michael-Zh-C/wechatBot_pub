package com.robot.dao.impl;

import com.robot.dao.AuthorizationDao;
import com.robot.pojo.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 */
@Repository
public class AuthorizationDaoImpl implements AuthorizationDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int add(Authorization authorization) {
        String sql = "insert into authorization  (authorization) values (:authorization)";
        Map<String ,Object> params = new HashMap<>();
        params.put("authorization",authorization.getAuthorization());

        return (int) jdbcTemplate.update(sql, params);
    }

    @Override
    public int update(Authorization authorization) {
        StringBuffer sb = new StringBuffer();
        sb.append("update authorization set ");
        Map<String ,Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(authorization.getAuthorization())) {
            sb.append("authorization = :authorization");
            sb.append(",");
            params.put("authorization",authorization.getAuthorization());
        }

        if (StringUtils.isNotBlank(authorization.getWechatId())){
            sb.append("wechatId = :wechatId");
            sb.append(",");
            params.put("wechatId",authorization.getWechatId());
        }

        if (StringUtils.isNotBlank(authorization.getwId())){
            sb.append("wid = :wid");
            sb.append(",");
            params.put("wid",authorization.getwId());
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(" where id = :id");

        String sql = sb.toString();
        System.out.println("sql = " + sql);
        params.put("id",authorization.getId());

        return (int) jdbcTemplate.update(sql, params);
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public Authorization findAuthor(int id) {
        String sql = "select id,authorization,wechatId,wId from authorization where id = " + id;
        Map<String ,Object> params = new HashMap<>();

        return  (Authorization) jdbcTemplate.query(sql,new Authorization()).get(0);
    }

    @Override
    public int selectCount(Authorization authorization) {
        String sql = "select count(*) from authorization";
        Map<String ,Object> params = new HashMap<>();


        return  jdbcTemplate.queryForObject(sql,params,Integer.class);    }

}
