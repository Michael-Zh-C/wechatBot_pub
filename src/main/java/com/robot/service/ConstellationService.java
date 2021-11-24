package com.robot.service;

/**
 * @author Michael
 */
public interface ConstellationService {
    /**
     * 拼装星座运势自动回复
     * @param constellationName
     * @return 回复具体内容
     * */
    String getConstellationReply(String constellationName);

    /**
     * 更新星座运势信息
     * */
    void updateConstellationInfo();

}
