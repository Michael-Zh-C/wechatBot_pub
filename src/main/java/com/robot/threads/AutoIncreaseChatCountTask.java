package com.robot.threads;

import com.robot.common.CommonGroupConst;
import com.robot.common.GetBeanUtil;
import com.robot.dao.SongInfoDao;
import com.robot.service.GroupChatService;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael
 */
public class AutoIncreaseChatCountTask implements Runnable {
    private GroupChatService groupChatService = GetBeanUtil.getBean(GroupChatService.class);


    private String chatRoomId;
    private String userName;

    public AutoIncreaseChatCountTask(String chatRoomId,String userName) {
        this.chatRoomId = chatRoomId;
        this.userName = userName;
    }

    @Override
    public void run() {
        if (CommonGroupConst.chatCountMap.containsKey(chatRoomId)) {
            ConcurrentHashMap<String, AtomicInteger> map = CommonGroupConst.chatCountMap.get(chatRoomId);
            if (map.containsKey(userName)) {
                map.get(userName).incrementAndGet();
            } else {
                AtomicInteger integer = new AtomicInteger(1);
                map.put(userName,integer);
            }
        } else {
            //群里第一次有人说话的时候 更新群信息
            groupChatService.updateGroupMemberInfo(chatRoomId);
            ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
            AtomicInteger integer = new AtomicInteger(1);
            map.put(userName,integer);

            CommonGroupConst.chatCountMap.put(chatRoomId,map);
        }

        if (CommonGroupConst.chatLastActiveMap.containsKey(chatRoomId)) {
            ConcurrentHashMap<String, Date> map = CommonGroupConst.chatLastActiveMap.get(chatRoomId);
            map.put(userName,new Date());
        } else {
            ConcurrentHashMap<String, Date> map = new ConcurrentHashMap<>();
            map.put(userName,new Date());
            CommonGroupConst.chatLastActiveMap.put(chatRoomId,map);
        }
    }
}
