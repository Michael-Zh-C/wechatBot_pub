package com.robot.dao.impl;

import com.robot.dao.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImageDaoImpl implements ImageDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> getBeautyImgIdList() {
        String sql = "select id from t_image_beauty";

        List<Integer> resultList = jdbcTemplate.queryForList(sql,Integer.class);

        return resultList;
    }

    @Override
    public String getBeautyImgInfoById(int id) {
        String sql = "SELECT pic_name AS pictureName" +
                " from t_image_beauty where id = " + id;
        return jdbcTemplate.queryForObject(sql,String.class);
    }
}
