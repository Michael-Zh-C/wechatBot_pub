package com.robot.enums;

/**
 * @author Michael
 */

public enum Constellation {
    /**魔羯座*/
    CAPRICORN("摩羯座"),
    /**水瓶座*/
    AQUARIUS("水瓶座"),
    /**双鱼座*/
    PISCES("双鱼座"),
    /**白羊座*/
    ARIES("白羊座"),
    /**金牛座*/
    TAURUS("金牛座"),
    /**双子座*/
    GEMINI("双子座"),
    /**巨蟹座*/
    CANCER("巨蟹座"),
    /**狮子座*/
    LEO("狮子座"),
    /**处女座*/
    VIRGO("处女座"),
    /**天秤座*/
    LIBRA("天秤座"),
    /**天蝎座*/
    SCORPIO("天蝎座"),
    /**射手座*/
    SAGITTARIUS("射手座")
    ;

    private String chineseName;

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    private Constellation(String chineseMode) {
        this.chineseName = chineseMode;
    }
}
