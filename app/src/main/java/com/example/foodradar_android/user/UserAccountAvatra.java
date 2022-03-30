package com.example.foodradar_android.user;

import java.io.Serializable;

// 儲存資料(檔)到Internal Storage時，序列化頭像物件用的類別
public class UserAccountAvatra implements Serializable {
    private final String TAG = "TAG_SerialUserAccount";
    private byte[] byteObject;
//    public String stringObject;

    public UserAccountAvatra(){}

    public UserAccountAvatra(byte[] byteObject){this.byteObject = byteObject;}
    public void setByteObject(byte[] byteObject) {this.byteObject = byteObject;}
    public byte[] getByteObject() {return this.byteObject;}

//    public UserAccountAvatra(String stringObject){this.stringObject = stringObject;}
//    public String getStringObject() {return stringObject;}
//    public void setStringObject(String stringObject) {this.stringObject = stringObject;}



}
