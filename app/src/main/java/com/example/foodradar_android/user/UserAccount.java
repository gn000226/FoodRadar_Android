package com.example.foodradar_android.user;

import android.graphics.Bitmap;

import java.sql.Timestamp;

public class UserAccount {
    // Date Time: 2020-09-10 15:22:30
    // table name: UserAccount
    private int userId;
    private String userPhone;
    private String userPwd;
    private Timestamp userBirth;
    private String userName;
    private Boolean allowNotifi;
    private Boolean isEnable;
    private Boolean isAdmin;
    private byte[] userAvatar;
    private Bitmap userAvatarBitmap;
    private Timestamp createDate;
    private Timestamp modifyDate;



    public UserAccount(int userId, String userPhone, String userPwd, Timestamp userBirth, String userName,
                       Boolean allowNotifi, Boolean isEnable, Boolean isAdmin, byte[] userAvatar, Timestamp createDate,
                       Timestamp modifyDate) {
        super();
        this.userId = userId;
        this.userPhone = userPhone;
        this.userPwd = userPwd;
        this.userBirth = userBirth;
        this.userName = userName;
        this.allowNotifi = allowNotifi;
        this.isEnable = isEnable;
        this.isAdmin = isAdmin;
        this.userAvatar = userAvatar;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

    // For Register
    public UserAccount(String userPhone, String userPwd, Timestamp userBirth, String userName) {
        super();
        this.userPhone = userPhone;
        this.userPwd = userPwd;
        this.userBirth = userBirth;
        this.userName = userName;
    }

    // For Update
    public UserAccount(int userId, String userPhone, String userPwd, Timestamp userBirth, String userName,
                       Boolean allowNotifi) {
        super();
        this.userId = userId;
        this.userPhone = userPhone;
        this.userPwd = userPwd;
        this.userBirth = userBirth;
        this.userName = userName;
        this.allowNotifi = allowNotifi;
        this.userAvatar = userAvatar;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    // For get UserAccount
    public UserAccount(int userId, String userPhone, Timestamp userBirth, String userName,
                       Boolean allowNotifi, Bitmap userAvatarBitmap, Timestamp createDate,
                       Timestamp modifyDate) {
        super();
        this.userId = userId;
        this.userPhone = userPhone;
        this.userBirth = userBirth;
        this.userName = userName;
        this.allowNotifi = allowNotifi;
        this.userAvatarBitmap = userAvatarBitmap;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

    // For Signup and Update
    public UserAccount(String userPhone, String userPwd, Timestamp userBirth, String userName,
                       Boolean allowNotifi) {
        super();
        this.userPhone = userPhone;
        this.userPwd = userPwd;
        this.userBirth = userBirth;
        this.userName = userName;
        this.allowNotifi = allowNotifi;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUserPhone() {
        return userPhone;
    }
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    public String getUserPwd() {
        return userPwd;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
    public Timestamp getUserBirth() {
        return userBirth;
    }
    public void setUserBirth(Timestamp userBirth) {
        this.userBirth = userBirth;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Boolean getAllowNotifi() {
        return allowNotifi;
    }
    public void setAllowNotifi(Boolean allowNotifi) {
        this.allowNotifi = allowNotifi;
    }
    public Boolean getIsEnable() {
        return isEnable;
    }
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public byte[] getUserAvatar() {
        return userAvatar;
    }
    public void setUserAvatar(byte[] userAvatar) {
        this.userAvatar = userAvatar;
    }
    public Timestamp getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    public Timestamp getModifyDate() {
        return modifyDate;
    }
    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

}
