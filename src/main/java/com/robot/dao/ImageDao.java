package com.robot.dao;

import java.util.List;

public interface ImageDao {
    List<Integer> getBeautyImgIdList();

    String getBeautyImgInfoById(int id);
}
