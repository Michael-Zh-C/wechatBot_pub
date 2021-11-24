package com.robot.service;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
public interface UserSummonService {
    /**
     * 获取用户所有召唤的神兽
     * @return
     * */
    Map<String, List<String>> getAllPets();

    /**
     * 本命召唤
     * @param fromUser 召唤人
     * @param pet 宠物
     * @return
     * */
    String trueSummon(String fromUser,String pet);

    /**
     * 随机召唤
     * @param fromUser 召唤人
     * @param pet 宠物
     * @return
     * */
    String randomSummon(String fromUser,String pet);

    /**
     * 召唤！
     * @param fromUser 召唤人
     * @return 宠物
     * */
    String summon(String fromUser);

    /**
     * 放生
     * @param fromUser 召唤人
     * @param pet 宠物
     * @return
     * */
    String dropPet(String fromUser,String pet);

}
