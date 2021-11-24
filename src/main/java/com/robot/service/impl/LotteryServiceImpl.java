package com.robot.service.impl;

import com.robot.common.CommonConsts;
import com.robot.common.CommonReply;
import com.robot.common.CommonUtils;
import com.robot.dao.GroupMemberInfoDao;
import com.robot.dao.LotteryInfoDao;
import com.robot.dao.LotteryRecordDao;
import com.robot.pojo.GroupMemberInfo;
import com.robot.pojo.LotteryInfo;
import com.robot.pojo.LotteryRecord;
import com.robot.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhang
 */
@Service
public class LotteryServiceImpl implements LotteryService {
    @Autowired
    LotteryRecordDao lotteryRecordDao;
    @Autowired
    LotteryInfoDao lotteryInfoDao;
    @Autowired
    GroupMemberInfoDao groupMemberInfoDao;

    @Override
    public String getLotteryTicket(String fromUser,String fromGroup) {
        if (CommonConsts.getInstance().todayLotteryRecord.containsKey(fromUser)) {
            //重复抽签
            return CommonReply.AUTO_LOTTERY_ALREADY_HAVE_TICKET;
        }

        int lotteryId = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().lotteryIds);
        LotteryInfo lotteryInfo = lotteryInfoDao.queryLotteryInfoById(lotteryId);

        System.out.println("当前用户：" + fromUser + "当前签文：" + lotteryInfo);
        //记录抽签信息，并且入库
        CommonConsts.getInstance().todayLotteryRecord.put(fromUser,lotteryId);

        LotteryRecord lotteryRecord = new LotteryRecord();
        lotteryRecord.setUserName(fromUser);
        lotteryRecord.setTodayLottery(lotteryId);
        lotteryRecord.setTodayTimestamp(System.currentTimeMillis());
        lotteryRecordDao.add(lotteryRecord);

        //拼接返回信息
        GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
        groupMemberInfo.setUserName(fromUser);
        groupMemberInfo.setChatRoomId(fromGroup);
        String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);

        StringBuffer sb = new StringBuffer();
        sb.append(nickName).append("，");
        sb.append("你抽到了").append(lotteryInfo.getLotteryName()).append("\n");
        sb.append("签诗：").append(lotteryInfo.getLotteryContent()).append("\n");
        sb.append("如果需要解签，请发送“！解签”");

        return sb.toString();
    }

    @Override
    public String getLotteryAnswer(String fromUser,String fromGroup) {
        if (!CommonConsts.getInstance().todayLotteryRecord.containsKey(fromUser)) {
            //还没抽过签
            return CommonReply.AUTO_LOTTERY_NO_TICKET;
        }

        int lotteryId = CommonConsts.getInstance().todayLotteryRecord.get(fromUser);
        LotteryInfo lotteryInfo = lotteryInfoDao.queryLotteryInfoById(lotteryId);

        //拼接返回信息
        GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
        groupMemberInfo.setUserName(fromUser);
        groupMemberInfo.setChatRoomId(fromGroup);
        String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);

        StringBuffer sb = new StringBuffer();
        sb.append(nickName).append("，");
        sb.append("你抽到了").append(lotteryInfo.getLotteryName()).append("\n");
        sb.append("签诗：").append(lotteryInfo.getLotteryContent()).append("\n");
        sb.append("解签：").append(lotteryInfo.getLotteryAnswer());

        return sb.toString();
    }
}
