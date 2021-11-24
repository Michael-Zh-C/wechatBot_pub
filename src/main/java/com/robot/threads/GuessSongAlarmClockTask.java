package com.robot.threads;

import com.alibaba.fastjson.JSONObject;
import com.robot.chatroom.ChatRoom;
import com.robot.common.CommonConsts;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

/**
 * @author Michael
 */
public class GuessSongAlarmClockTask implements Runnable{
    private String chat_room_id;
    private String gameSessionId;

    public GuessSongAlarmClockTask(String chat_room_id,String gameSessionId){
        this.chat_room_id = chat_room_id;
        this.gameSessionId = gameSessionId;
    }


    @Override
    public void run() {
        try {
            ChatRoom chatRoom = CommonConsts.getInstance().chatRooms.get(chat_room_id);

            int notifyTime = 30*1000;
            int expireTime = 60*1000;
            System.out.println("chatRoom.getLastMiniGameTime() = " + chatRoom.getLastMiniGameTime());
            while (System.currentTimeMillis() - chatRoom.getLastMiniGameTime() <= expireTime){
                if (System.currentTimeMillis() - chatRoom.getLastMiniGameTime() > notifyTime) {
                    //时间还剩一半，提醒
                    chatRoom.notifyGuessSong(gameSessionId);
                }
                Thread.sleep(1000);
            }

            chatRoom.finishGuessSong(gameSessionId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
