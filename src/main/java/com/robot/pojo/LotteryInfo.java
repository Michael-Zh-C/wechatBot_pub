package com.robot.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhang
 */
@ToString
@Getter
@Setter
public class LotteryInfo implements RowMapper {
    private int id;
    private String lotteryName;
    private String lotteryContent;
    private String lotteryAnswer;


    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        LotteryInfo lotteryInfo = new LotteryInfo();
        lotteryInfo.setId(resultSet.getInt(1));
        lotteryInfo.setLotteryName(resultSet.getString(2));
        lotteryInfo.setLotteryContent(resultSet.getString(3));
        lotteryInfo.setLotteryAnswer(resultSet.getString(4));

        return lotteryInfo;
    }
}
