package com.robot.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhang
 */
@Getter
@Setter
@ToString
public class LotteryRecord {
    private String userName;
    private int todayLottery;
    private long todayTimestamp;
}
