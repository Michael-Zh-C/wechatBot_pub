package com.robot.service.impl;

import com.robot.common.CommonConsts;
import com.robot.common.CommonReply;
import com.robot.common.CommonUtils;
import com.robot.dao.UserSummonDao;
import com.robot.pojo.UserSummon;
import com.robot.service.UserSummonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Michael
 */
@Service
public class UserSummonServiceImpl implements UserSummonService {
    @Autowired
    private UserSummonDao userSummonDao;
//    @Value("#{new Integer('${true.summon.count}')}")
//    private int trueSummonCount;
//    @Value("#{new Integer('${random.summon.count}')}")
//    private int randomSummonCount;

    @Override
    public Map<String, List<String>> getAllPets() {
        return userSummonDao.selectAll();
    }

    @Override
    public String trueSummon(String fromUser, String pet) {
        UserSummon userSummon = new UserSummon();
        userSummon.setFromUser(fromUser);
        userSummon.setIsRandom(0);
        userSummon.setPet(pet);

        List<String> pets = CommonConsts.getInstance().pets.get(fromUser);
        if (pets != null && pets.contains(pet)){
            //已经召唤过了
            return CommonReply.AUTO_SUMMON_ALREADY_HAVE;
        }

        int petCounts = userSummonDao.selectUserCount(userSummon);
        if (petCounts >= 5) {
            //超过5只
            return CommonReply.AUTO_SUMMON_TRUE_OVER_FIVE;
        }

        userSummonDao.add(userSummon);
        CommonConsts.getInstance().pets = this.getAllPets();

        return CommonReply.AUTO_SUMMON_TRUE_SUCCESS;
    }

    @Override
    public String randomSummon(String fromUser, String pet) {
        UserSummon userSummon = new UserSummon();
        userSummon.setFromUser(fromUser);
        userSummon.setIsRandom(1);
        userSummon.setPet(pet);

        List<String> pets = CommonConsts.getInstance().pets.get(fromUser);
        if (pets != null && pets.contains(pet)){
            //已经召唤过了
            return CommonReply.AUTO_SUMMON_ALREADY_HAVE;
        }

        int petCounts = userSummonDao.selectUserCount(userSummon);
        if (petCounts >= 10) {
            //超过10只
            return CommonReply.AUTO_SUMMON_RANDOM_OVER_TEN;
        }

        //随机函数
        if (!isRandomSuccess(petCounts)) {
            return CommonReply.AUTO_SUMMON_RANDOM_FAIL;
        }

        userSummonDao.add(userSummon);
        CommonConsts.getInstance().pets = this.getAllPets();

        return CommonReply.AUTO_SUMMON_RANDOM_SUCCESS;
    }

    @Override
    public String summon(String fromUser) {
        List<String> list = CommonConsts.getInstance().pets.get(fromUser);

        if (list == null || list.size() == 0) {
            return CommonReply.AUTO_SUMMON_NO_PET;
        }

        return CommonUtils.getInstance().getStringRandomId(list);
    }

    @Override
    public String dropPet(String fromUser, String pet) {
        UserSummon userSummon = new UserSummon();
        userSummon.setFromUser(fromUser);
        userSummon.setPet(pet);
        List<String> list = CommonConsts.getInstance().pets.get(fromUser);

        if (list == null || list.size() == 0) {
            return CommonReply.AUTO_SUMMON_NO_PET;
        }

        if (!list.contains(pet)) {
            return CommonReply.AUTO_SUMMON_NOT_HIS_PET;
        }

        userSummonDao.delete(userSummon);
        CommonConsts.getInstance().pets = this.getAllPets();

        return CommonReply.AUTO_SUMMON_DROP_PET_SUCCESS;
    }

    private boolean isRandomSuccess(int petCount){
        Random random = new Random();
        return random.nextInt(petCount+1) == 0;
    }
}
