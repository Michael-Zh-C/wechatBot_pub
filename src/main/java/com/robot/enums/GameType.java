package com.robot.enums;


import java.util.Arrays;

/**
 * @author Michael
 */

public enum GameType {
    /**涂地模式*/
    REGULAR_BATTLE("涂地",1),
    /**真格*/
    RANKED_BATTLE("真格",2),
    /**组排*/
    LEAGUE_BATTLE("组排",3)
    ;

    private String chineseMode;
    private Integer i;

    public String getChineseMode() {
        return chineseMode;
    }

    public void setChineseMode(String chineseMode) {
        this.chineseMode = chineseMode;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }
    private GameType(String chineseMode, int i) {
        this.chineseMode = chineseMode;
        this.i = i;
    }
}
