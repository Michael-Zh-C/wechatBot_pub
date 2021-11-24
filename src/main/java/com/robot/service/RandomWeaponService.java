package com.robot.service;

/**
 * @author Michael
 */
public interface RandomWeaponService {
    /**
     * 随机武器
     * */
    String getRandomWeapon();
    /**
     * 根据位置获取随机武器
     * */
    String getPositionRandomWeapon(String content);
}
