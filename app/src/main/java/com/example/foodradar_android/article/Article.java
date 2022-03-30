package com.example.foodradar_android.article;

import java.io.Serializable;

public class Article implements Serializable {

    private int articleId;
    private String articleTitle;
    private String articleTime ;
    private String articleText ;
    private String modifyTime ;
    private String resCategoryInfo;
    private int resId;
    private int userId;
    private String resName;
    private String userName;
    private int conAmount;
    private int conNum;
    private boolean articleStatus;
    private int articleGoodCount;
    private int commentCount;
    private int favoriteCount;
    private int articleGoodId;
    private boolean articleGoodStatus;
    private boolean articleFavoriteStatus;
    private byte[] articleImg;
    private byte[] userIcon;
    private int articleFavoriteId;

    //包裝資料跳頁用
    public static Integer ARTICLE_ID = 0;
    public static Integer USER_ID = 0;


    public Article() {
        super();
    }


    //ArticleList頁面(新進榜，排行榜，收藏榜，內文)
    public Article(String userName, String resCategoryInfo, String articleTime, String articleTitle, String articleText,
                   String resName,int articleGoodCount, int commentCount, int favoriteCount,boolean articleGoodStatus ,boolean articleFavoriteStatus ,
                   int articleId, int resId, int userId, int conAmount, int conNum, boolean articleStatus, String modifyTime) {
        super();
        this.articleTitle = articleTitle;
        this.articleTime = articleTime;
        this.articleText = articleText;
        this.resCategoryInfo = resCategoryInfo;
        this.resName = resName;
        this.userName = userName;
        this.articleGoodCount = articleGoodCount;
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.articleId = articleId;
        this.resId = resId;
        this.userId = userId;
        this.conAmount = conAmount;
        this.conNum = conNum;
        this.articleStatus = articleStatus;
        this.articleGoodStatus = articleGoodStatus;
        this.articleFavoriteStatus = articleFavoriteStatus;
        this.modifyTime = modifyTime;
    }

    public Article(int articleGoodId, int userId , int articleId) {
        super();
        this.articleGoodId = articleGoodId;
        this.articleId = articleId;
        this.userId = userId;
    }

    //刪除文章用
    public void setStatus(boolean articleStatus){
        this.articleStatus = articleStatus;
    }

    //建構子，發文用
    public  Article(int articleId, String articleTitle, String articleText, int conNum,
                    int conAmount,int resId, int userId, boolean articleStatus){
        super();
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleText = articleText;
        this.conNum = conNum;
        this.conAmount = conAmount;
        this.resId = resId;
        this.userId = userId;
        this.articleStatus = articleStatus;
    }

    //更新文章用
    public  Article( String articleTitle, String articleText, String modifyTime,
                    int conAmount, int conNum,  int articleId){
        super();
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleText = articleText;
        this.conNum = conNum;
        this.conAmount = conAmount;
        this.resId = resId;
        this.userId = userId;
        this.articleStatus = articleStatus;
        this.modifyTime = modifyTime;
    }

    public Article(int userId , int articleId) {
        super();
        this.articleId = articleId;
        this.userId = userId;
    }


    public Article(int articleId) {
        super();
        this.articleId = articleId;
    }

    public int getArticleId() {
        return articleId;
    }


    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }


    public String getArticleTitle() {
        return articleTitle;
    }


    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }


    public String getArticleTime() {
        return articleTime;
    }


    public void setArticleTime(String articleTime) {
        this.articleTime = articleTime;
    }


    public String getArticleText() {
        return articleText;
    }


    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }


    public String getModifyTime() {
        return modifyTime;
    }


    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }


    public int getResId() {
        return resId;
    }


    public void setResId(int resId) {
        this.resId = resId;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getConAmount() {
        return conAmount;
    }


    public void setConAmount(int conAmount) {
        this.conAmount = conAmount;
    }


    public int getConNum() {
        return conNum;
    }


    public void setConNum(int conNum) {
        this.conNum = conNum;
    }


    public boolean isArticleStatus() {
        return articleStatus;
    }


    public void setArticleStatus(boolean articleStatus) {
        this.articleStatus = articleStatus;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public int getArticleGoodId() {
        return articleGoodId;
    }

    public void setArticleGoodId(int articleGoodId) {
        this.articleGoodId = articleGoodId;
    }

    public int getCommentCount() {
        return commentCount;
    }


    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }


    public int getFavoriteCount() {
        return favoriteCount;
    }


    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }


    public byte[] getArticleImg() {
        return articleImg;
    }


    public void setArticleImg(byte[] articleImg) {
        this.articleImg = articleImg;
    }


    public byte[] getUserIcon() {
        return userIcon;
    }


    public void setUserIcon(byte[] userIcon) {
        this.userIcon = userIcon;
    }

    public String getResCategoryInfo() {
        return resCategoryInfo;
    }

    public void setResCategoryInfo(String resCategoryInfo) {
        this.resCategoryInfo = resCategoryInfo;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public boolean isArticleGoodStatus() {
        return articleGoodStatus;
    }

    public void setArticleGoodStatus(boolean articleGoodStatus) {
        this.articleGoodStatus = articleGoodStatus;
    }

    public int getArticleGoodCount() {
        return articleGoodCount;
    }

    public void setArticleGoodCount(int articleGoodCount) {
        this.articleGoodCount = articleGoodCount;
    }

    public boolean isArticleFavoriteStatus() {
        return articleFavoriteStatus;
    }

    public void setArticleFavoriteStatus(boolean articleFavoriteStatus) {
        this.articleFavoriteStatus = articleFavoriteStatus;
    }

    public int getArticleFavoriteId() {
        return articleFavoriteId;
    }

    public void setArticleFavoriteId(int articleFavoriteId) {
        this.articleFavoriteId = articleFavoriteId;
    }

    @Override   //覆寫方法，取得articleId > 透過id 取得article
    public boolean equals(Object obj) {
        return this.articleId == ((Article) obj).articleId;
    }

    //計算平均消費
    public String avgCon () {
        return "平均消費："  + (conAmount / conNum)  + "/人";
    }

}
