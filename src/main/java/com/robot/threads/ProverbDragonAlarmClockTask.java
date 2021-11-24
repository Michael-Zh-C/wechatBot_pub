package com.robot.threads;

import com.robot.chatroom.ChatRoom;
import com.robot.common.CommonConsts;

/**
 * @author Michael
 */
public class ProverbDragonAlarmClockTask implements Runnable {
    private String chat_room_id;
    public ProverbDragonAlarmClockTask(String chat_room_id){
        this.chat_room_id = chat_room_id;
    }

    @Override
    public void run() {
        try {
            ChatRoom chatRoom = CommonConsts.getInstance().chatRooms.get(chat_room_id);
            int expireTime = 60*1000;
            System.out.println("chatRoom.getLastProverbDragonTime() = " + chatRoom.getLastMiniGameTime());
            while (System.currentTimeMillis() - chatRoom.getLastMiniGameTime() <= expireTime){
                Thread.sleep(1000);
            }

            chatRoom.finishProverbDragon();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
