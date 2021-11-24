package com.robot.service;


/**
 * @author zhang
 */
public interface StealSongListService {
    /**
     * 获取语音消息
     * @param msgId 消息id
     * @param length 语音的长度（xml数据中的length字段）
     * @param bufId xml中返回的bufId字段值
     * @param fromUser 发送者
     * */
    void getSongVoiceMsg(long msgId,String length,String bufId,String fromUser);
}
