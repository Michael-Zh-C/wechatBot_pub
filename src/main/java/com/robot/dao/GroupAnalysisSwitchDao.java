package com.robot.dao;

import com.robot.pojo.GroupAnalysisSwitch;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
public interface GroupAnalysisSwitchDao {
    /**
     * 查询全部
     * @return
     * */
    List<Map<String,Object>> selectAll();

    /**
     * 改
     * @param groupAnalysisSwitch
     * */
    void edit(GroupAnalysisSwitch groupAnalysisSwitch);
}
