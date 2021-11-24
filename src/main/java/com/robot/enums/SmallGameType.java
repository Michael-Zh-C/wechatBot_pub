package com.robot.enums;

/**
 * @author Michael
 */

public enum SmallGameType {
    /**未开始游戏*/
    NOPE("未开始游戏",0),
    /**成语接龙*/
    PROVERB_DRAGON("成语接龙",1),
    /**猜歌名*/
    GUESS_SONG("猜歌名",2);

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
    private SmallGameType(String chineseMode, int i) {
        this.chineseMode = chineseMode;
        this.i = i;
    }
}
