package com.robot.dao;

import java.util.List;
import java.util.Map;

public interface WeaponMainDao {

    /**
     * 根據位置獲取武器ID
     * 如果位置傳0，則默認獲取所有武器
     * @param position 1:前排武器，2：中衛武器，3：後衛武器
     * */
    List<Integer> getWeaponIdList(int position);
    /**
     * 根據武器ID獲取武器具體信息
     * */
    Map<String,Object> getWeaponDetail(int weaponId);
}
