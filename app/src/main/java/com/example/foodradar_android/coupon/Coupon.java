package com.example.foodradar_android.coupon;

import android.widget.Button;
import android.widget.Spinner;

import java.io.Serializable;
import java.sql.Timestamp;

public class Coupon implements Serializable {
    private int id;
    private int couPonId;
    private int userId;
    private int resId;
    private String resName;
    private String couPonInfo;
    private String couPonStartDate;
    private String couPonEndDate;
    private boolean couPonType;
    private boolean couPonEnable;
    private Timestamp date;
    private Button btCollect;
    private boolean couponLoveStatus;
    private int loveCount;
    private boolean spTypeBoolean;
    private boolean spEnableBoolean;


    public Coupon(int id, int userId, int couPonId, int resId, String resName, String couPonInfo, String couPonStartDate, String couPonEndDate, boolean couPonType, boolean couPonEnable, Timestamp date, Button btCollect, boolean CouPonLoveStatus, int loveCount, boolean spType, boolean spEnable) {
        this.id = id;
        this.couPonId = couPonId;
        this.resId = resId;
        this.userId = userId;
        this.resName = resName;
        this.couPonInfo = couPonInfo;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.couPonType = couPonType;
        this.couPonEnable = couPonEnable;
        this.date = date;
        this.btCollect = btCollect;
        this.couponLoveStatus = couponLoveStatus;
        this.loveCount = loveCount;
        this.spTypeBoolean = spTypeBoolean;
        this.spEnableBoolean = spEnableBoolean;

    }
    public Coupon(int couPonId, Integer resId, String couPonStartDate, String couPonEndDate, boolean couPonType,
                  String couPonInfo, boolean couPonEnable, Integer userId) {
        this.couPonId = couPonId;
        this.resId = resId;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.couPonType = couPonType;
        this.couPonInfo = couPonInfo;
        this.couPonEnable = couPonEnable;
        this.userId = userId;
    }
    public Coupon(int couPonId, Integer resId, String couPonStartDate, String couPonEndDate, boolean couPonType,
                  String couPonInfo, boolean couPonEnable, Integer userId, boolean couponLoveStatus) {
        this.couPonId = couPonId;
        this.resId = resId;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.couPonType = couPonType;
        this.couPonInfo = couPonInfo;
        this.couPonEnable = couPonEnable;
        this.userId = userId;
        this.couponLoveStatus = couponLoveStatus;

    }
    public Coupon(int couPonId, Integer resId, String couPonStartDate, String couPonEndDate, boolean couPonType,
                  String couPonInfo, boolean couPonEnable, Integer userId, String resName) {
        this.couPonId = couPonId;
        this.resId = resId;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.couPonType = couPonType;
        this.couPonInfo = couPonInfo;
        this.couPonEnable = couPonEnable;
        this.userId = userId;
        this.resName = resName;
    }
    public Coupon(int couPonId, int resId, String couPonStartDate, String couPonEndDate, boolean couPonType, String couPonInfo, boolean couPonEnable, int userId) {
        this.resId = resId;
        this.couPonId = couPonId;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.couPonType = couPonType;
        this.couPonEnable = couPonEnable;
        this.couPonInfo = couPonInfo;
        this.userId = userId;

    }

//    public Coupon(int id, int resId, String couPonStartDate, String couPonEndDate, boolean couPonType, String couPonInfo, boolean couPonEnable, int userId) {
//        this.id = id;
//        this.resId = resId;
//        this.couPonStartDate = couPonStartDate;
//        this.couPonEndDate = couPonEndDate;
//        this.couPonType = couPonType;
//        this.couPonInfo = couPonInfo;
//        this.couPonEnable = couPonEnable;
//        this.userId = userId;
//    }

    public Coupon(int resId, String couPonStartDate, String couPonEndDate, boolean spTypeBoolean, String couPonInfo, boolean spEnableBoolean, int userId) {
        this.resId = resId;
        this.couPonStartDate = couPonStartDate;
        this.couPonEndDate = couPonEndDate;
        this.spTypeBoolean = spTypeBoolean;
        this.couPonInfo = couPonInfo;
        this.spEnableBoolean = spEnableBoolean;
        this.userId = userId;
    }


    public boolean isSpTypeBoolean() {
        return spTypeBoolean;
    }

    public void setSpTypeBoolean(boolean spTypeBoolean) {
        this.spTypeBoolean = spTypeBoolean;
    }

    public boolean isSpEnableBoolean() {
        return spEnableBoolean;
    }

    public void setSpEnableBoolean(boolean spEnableBoolean) {
        this.spEnableBoolean = spEnableBoolean;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    public boolean isCouPonLoveStatus() {
        return couponLoveStatus;
    }

    public void setCouPonLoveStatus(boolean couPonLoveStatus) {
        this.couponLoveStatus = couponLoveStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCouPonId() {
        return couPonId;
    }

    public void setCouPonId(int couPonId) {
        this.couPonId = couPonId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getCouPonInfo() {
        return couPonInfo;
    }

    public void setCouPonInfo(String couPonInfo) {
        this.couPonInfo = couPonInfo;
    }

    public String getCouPonStartDate() {
        return couPonStartDate;
    }

    public void setCouPonStartDate(String couPonStartDate) {
        this.couPonStartDate = couPonStartDate;
    }

    public String getCouPonEndDate() {
        return couPonEndDate;
    }

    public void setCouPonEndDate(String couPonEndDate) {
        this.couPonEndDate = couPonEndDate;
    }

    public boolean isCouPonType() {
        return couPonType;
    }

    public void setCouPonType(boolean couPonType) {
        this.couPonType = couPonType;
    }

    public boolean isCouPonEnable() {
        return couPonEnable;
    }

    public void setCouPonEnable(boolean couPonEnable) {
        this.couPonEnable = couPonEnable;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Button getBtCollect() {
        return btCollect;
    }

    public void setBtCollect(Button btCollect) {
        this.btCollect = btCollect;
    }
}
