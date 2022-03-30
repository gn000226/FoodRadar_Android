package com.example.foodradar_android.user;

import java.io.Serializable;
import java.sql.Timestamp;

public class MyCoupon implements Serializable {
    private int myCouponId;
    private int userId;
    private int couPonId;
    private String couPonInfo;
    private boolean couPonIsUsed;
    private Timestamp modifyDate;
    private byte[] couPonPhoto;

    public MyCoupon(int myCouponId, int userId, int couPonId, boolean couPonIsUsed, String couPonInfo, Timestamp modifyDate, byte[] couPonPhoto) {
        this.myCouponId = myCouponId;
        this.userId = userId;
        this.couPonId = couPonId;
        this.couPonIsUsed = couPonIsUsed;
        this.couPonInfo = couPonInfo;
        this.modifyDate = modifyDate;
        this.couPonPhoto = couPonPhoto;
    }

    public String getCouPonInfo() {
        return couPonInfo;
    }

    public void setCouPonInfo(String couPonInfo) {
        this.couPonInfo = couPonInfo;
    }

    public int getMyCouponId() {
        return myCouponId;
    }

    public void setMyCouponId(int myCouponId) {
        this.myCouponId = myCouponId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCouPonId() {
        return couPonId;
    }

    public void setCouPonId(int couPonId) {
        this.couPonId = couPonId;
    }

    public boolean isCouPonIsUsed() {
        return couPonIsUsed;
    }

    public void setCouPonIsUsed(boolean couPonIsUsed) {
        this.couPonIsUsed = couPonIsUsed;
    }

    public Timestamp getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

    public byte[] getCouPonPhoto() {
        return couPonPhoto;
    }

    public void setCouPonPhoto(byte[] couPonPhoto) {
        this.couPonPhoto = couPonPhoto;
    }
}
