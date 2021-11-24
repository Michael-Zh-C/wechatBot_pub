package com.robot.service;

/**
 * @author Michael
 */
public interface GroupChatService {
    /**
     * 获取群里最能BB的前三名
     * @param groupId 群组ID
     * @return
     * */
    String getGroupChatChampion(String groupId);

    /**
     * 更新群内成员信息
     * @param groupId
     * */
    void updateGroupMemberInfo(String groupId);

    /**
     * 更新群内活跃度信息
     * */
    void updateGroupActivityInfo();
}
