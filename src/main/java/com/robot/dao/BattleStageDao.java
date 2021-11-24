package com.robot.dao;

import java.util.List;
import java.util.Map;

public interface BattleStageDao {
    /**
     * 获取所有地图ID列表
     * */
    List<Integer> getStageIdList();

    Map<String,Object> getStageInfoById (int id);
}
