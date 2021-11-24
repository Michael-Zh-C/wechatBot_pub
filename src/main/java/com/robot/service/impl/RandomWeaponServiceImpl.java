package com.robot.service.impl;

import com.robot.common.CommonConsts;
import com.robot.common.CommonUtils;
import com.robot.convertimg.RandomWeaponPdfUtils;
import com.robot.dao.BattleStageDao;
import com.robot.dao.WeaponMainDao;
import com.robot.service.RandomWeaponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Michael
 */
@Service
public class RandomWeaponServiceImpl implements RandomWeaponService {
    @Autowired
    private WeaponMainDao weaponMainDao;
    @Autowired
    private BattleStageDao battleStageDao;
    @Override
    public String getRandomWeapon() {
        //1、获取随机武器
        List<Integer> weaponIds = weaponMainDao.getWeaponIdList(0);
        System.out.println("weaponIds = " + weaponIds.size());

        Map<Integer,String> weaponDetailA = new HashMap<>();
        Map<Integer,String> weaponDetailB = new HashMap<>();
        getWeaponsDetail(weaponIds,4,weaponDetailA);
        getWeaponsDetail(weaponIds,4,weaponDetailB);

        System.out.println("weaponDetailA = " + weaponDetailA);
        System.out.println("weaponDetailB = " + weaponDetailB);

        //2、获取随机地图
        int mapId = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().mapIds);
        System.out.println("mapId = " + mapId);
        Map<String,Object> mapDetail = battleStageDao.getStageInfoById(mapId);

        return RandomWeaponPdfUtils.getRandomWeaponImg(weaponDetailA,weaponDetailB,mapDetail);
    }

    @Override
    public String getPositionRandomWeapon(String content) {
        int front = 0;
        int middle = 0;
        int back = 0;

        for (char c:content.toCharArray()) {
            switch (c){
                case '前':
                    front++;
                    break;
                case '中':
                    middle++;
                    break;
                case '后':
                    back++;
                    break;
                default:
            }
        }
        System.out.println("前排：" + front + ",中卫：" + middle + ",后卫：" + back);
        return getWeaponsByPosition(front,middle,back);
    }

    private void getWeaponsDetail(List<Integer> weaponIds,int num,Map<Integer,String> weaponDetails){
        //随机获取武器ID
        Set<Integer> choosedIds = new HashSet<>();

        while (choosedIds.size() < num) {
            choosedIds.add(CommonUtils.getInstance().getIntegerRandomId(weaponIds));
        }

        System.out.println("choosedIds = " + choosedIds);
        for (int i:choosedIds) {
            //获取武器参数
            Map<String,Object> map = weaponMainDao.getWeaponDetail(i);

            String mainPicture = (String) map.get("mainPicture");
            String subPicture = (String) map.get("subPicture");
            String specialPicture = (String) map.get("specialPicture");
            String weaponDetail = mainPicture + "," + subPicture + "," + specialPicture;
            weaponDetails.put(i,weaponDetail);
        }
    }

    private String getWeaponsByPosition(int front,int middle,int back){
        //1、获取随机武器
        Map<Integer,String> weaponDetailA = new HashMap<>();
        Map<Integer,String> weaponDetailB = new HashMap<>();
        if (front > 0) {
            List<Integer> weaponIds = weaponMainDao.getWeaponIdList(1);
            getWeaponsDetail(weaponIds,front,weaponDetailA);
            getWeaponsDetail(weaponIds,front,weaponDetailB);
        }

        if (middle > 0) {
            List<Integer> weaponIds = weaponMainDao.getWeaponIdList(2);
            getWeaponsDetail(weaponIds,middle,weaponDetailA);
            getWeaponsDetail(weaponIds,middle,weaponDetailB);
        }

        if (back > 0) {
            List<Integer> weaponIds = weaponMainDao.getWeaponIdList(3);
            getWeaponsDetail(weaponIds,back,weaponDetailA);
            getWeaponsDetail(weaponIds,back,weaponDetailB);
        }

        //2、获取随机地图
        int mapId = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().mapIds);
        System.out.println("mapId = " + mapId);
        Map<String,Object> mapDetail = battleStageDao.getStageInfoById(mapId);

        return RandomWeaponPdfUtils.getRandomWeaponImg(weaponDetailA,weaponDetailB,mapDetail);
    }
}


