package com.robot.dao;

import com.robot.pojo.GroupMemberInfo;

/**
 * @author Michael
 */
public interface GroupMemberInfoDao {
    /**
     * 增
     * @param groupMemberInfo
     * */
    void add(GroupMemberInfo groupMemberInfo);
    /**
     * 删
     * @param groupMemberInfo
     * */
    void delete(GroupMemberInfo groupMemberInfo);
    /**
     * 改
     * @param groupMemberInfo
     * */
    void edit(GroupMemberInfo groupMemberInfo);

    /**
     * 查询玩家群名片
     * @param groupMemberInfo
     * @return 群名片，如果为空则昵称
     * */
    String queryNickName(GroupMemberInfo groupMemberInfo);

    /**
     * 查询玩家是否为管理员
     * @param groupMemberInfo
     * @return 是否管理员，0：否，1：是
     * */
    int queryIsManager(GroupMemberInfo groupMemberInfo);
}
