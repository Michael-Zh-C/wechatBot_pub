package com.robot.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponMain implements RowMapper {
    private int id;
    private String englishName;
    private String chineseName;
    private String pictureName;
    private int buyLevel;
    private int perDamage;
    private int sub;
    private int special;
    private int specialPoint;
    private int position;
    private int weaponType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public int getBuyLevel() {
        return buyLevel;
    }

    public void setBuyLevel(int buyLevel) {
        this.buyLevel = buyLevel;
    }

    public int getPerDamage() {
        return perDamage;
    }

    public void setPerDamage(int perDamage) {
        this.perDamage = perDamage;
    }

    public int getSub() {
        return sub;
    }

    public void setSub(int sub) {
        this.sub = sub;
    }

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public int getSpecialPoint() {
        return specialPoint;
    }

    public void setSpecialPoint(int specialPoint) {
        this.specialPoint = specialPoint;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        WeaponMain weaponMain = new WeaponMain();
        weaponMain.setId(resultSet.getInt(1));
        weaponMain.setEnglishName(resultSet.getString(2));
        weaponMain.setChineseName(resultSet.getString(3));
        weaponMain.setPictureName(resultSet.getString(4));
        weaponMain.setBuyLevel(resultSet.getInt(5));
        weaponMain.setPerDamage(resultSet.getInt(6));
        weaponMain.setSub(resultSet.getInt(7));
        weaponMain.setSpecial(resultSet.getInt(8));
        weaponMain.setSpecialPoint(resultSet.getInt(9));
        weaponMain.setPosition(resultSet.getInt(10));
        weaponMain.setWeaponType(resultSet.getInt(11));

        return weaponMain;
    }
}
