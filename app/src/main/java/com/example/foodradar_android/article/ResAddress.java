package com.example.foodradar_android.article;

import java.io.Serializable;

public class ResAddress implements Serializable {
    private int resId;
    private String resName;
    private String resAddress;
    private String resCategoryInfo;


    public ResAddress(int resId, String resName, String resAddress, String resCategoryInfo) {
        super();
        this.resId = resId;
        this.resName = resName;
        this.resAddress = resAddress;
        this.resCategoryInfo = resCategoryInfo;
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

    public String getResAddress() {
        return resAddress;
    }

    public void setResAddress(String resAddress) {
        this.resAddress = resAddress;
    }


    public String getResCategoryInfo() {
        return resCategoryInfo;
    }

    public void setResCategoryInfo(String resCategoryInfo) {
        this.resCategoryInfo = resCategoryInfo;
    }


}
