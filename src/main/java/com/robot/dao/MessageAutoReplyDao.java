package com.robot.dao;

import com.robot.pojo.MessageAutoReply;

import java.util.Map;

/**
 * @author Michael
 */
public interface MessageAutoReplyDao {
    /**
     * @param messageAutoReply
     * */
    void add(MessageAutoReply messageAutoReply);
    /**
     * @param messageAutoReply
     * */
    void edit(MessageAutoReply messageAutoReply);
    /**
     * 获取全部自定义回复消息
     * @return 自定义消息合集
     * */
    Map<String,Map<String,String>> selectAll();

    /**
     * 根据其他值获取消息ID
     * @param messageAutoReply
     * @return 消息ID
     * */
    int getMessageId(MessageAutoReply messageAutoReply);
}
