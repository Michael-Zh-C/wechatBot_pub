package com.robot.service;

/**
 * @author zhang
 */
public interface LotteryService {
    /**
     * 抽签功能
     * @param fromUser 消息发送人
     * @param fromGroup 来源群组
     * @return 返回抽签签文
     * */
    String getLotteryTicket(String fromUser,String fromGroup);

    /**
     * 解签功能
     * @param fromUser 消息发送人
     * @param fromGroup 来源群组
     * @return 返回解签签文
     * */
    String getLotteryAnswer(String fromUser,String fromGroup);
}
