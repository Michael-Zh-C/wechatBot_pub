package com.robot.enums;


/**
 * @author Michael
 */

public enum Mode{
    /**
     * 涂地模式
     * */
    TURF_WAR("涂地模式",0),
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
    private Mode(String chineseMode, int i) {
        this.chineseMode = chineseMode;
        this.i = i;
    }

    public static Mode randomType(Mode[] values){
        return values[(int)(Math.random()*values.length)];
    }
}
