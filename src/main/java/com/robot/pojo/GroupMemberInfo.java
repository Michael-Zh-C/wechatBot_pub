package com.robot.pojo;

import java.util.Date;

/**
 * @author Michael
 */
public class GroupMemberInfo {
    private String chatRoomId;
    private String userName;
    private String nickName;
    private String displayName;
    private String bigHeadImgUrl;
    private String smallHeadImgUrl;
    private int isManager;
    private Date lastActivity;

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBigHeadImgUrl() {
        return bigHeadImgUrl;
    }

    public void setBigHeadImgUrl(String bigHeadImgUrl) {
        this.bigHeadImgUrl = bigHeadImgUrl;
    }

    public String getSmallHeadImgUrl() {
        return smallHeadImgUrl;
    }

    public void setSmallHeadImgUrl(String smallHeadImgUrl) {
        this.smallHeadImgUrl = smallHeadImgUrl;
    }

    public int getIsManager() {
        return isManager;
    }

    public void setIsManager(int isManager) {
        this.isManager = isManager;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }
}
