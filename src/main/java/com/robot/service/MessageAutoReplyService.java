package com.robot.service;

/**
 * @author Michael
 */
public interface MessageAutoReplyService {
    /**
     * 自动回复
     * @param fromGroup 消息来源群
     * @param content 消息内容
     * @return 机器人返回消息
     * */
    String handleAutoReply(String fromGroup,String content);

    /**
     * 学习功能
     * @param fromGroup 消息来源群
     * @param content 消息内容
     * @return 机器人返回消息
     * */
    String study(String fromGroup,String content);
}
