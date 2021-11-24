package com.robot.enums;

public enum RankedMode {
    /**
     * 区域模式
     * */
    SPLAT_ZONES("区域模式",1),

    /**
     * 占塔模式
     * */
    TOWER_CONTROL("推塔模式",2),

    /**
     * 抢鱼模式
     * */
    RAINMAKER("抢鱼模式",3),

    /**
     * 蛤蜊模式
     * */
    CLAM_BLITZ("蛤蜊模式",4)
    ;

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

    private String chineseMode;
    private Integer i;
    private RankedMode(String chineseMode, int i) {
        this.chineseMode = chineseMode;
        this.i = i;
    }

    public static RankedMode randomType(RankedMode[] values){
        return values[(int)(Math.random()*values.length)];
    }
}
