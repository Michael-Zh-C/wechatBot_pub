package com.robot.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Michael
 */
@Getter
@Setter
public class GroupActivityInfo {
    private int id;
    private String chatRoomId;
    private String userName;
    private long time;
    private int msgCount;
}
