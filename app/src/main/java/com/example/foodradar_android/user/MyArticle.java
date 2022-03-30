package com.example.foodradar_android.user;

import java.sql.Timestamp;

// Date Time: 2020-10-14 13:31:20
// table name: MyArticle
public class MyArticle {
    private int myArticleId;
    private int userId;
    private int articleId;
    private Timestamp modifyDate;

    private String articleTitle;
    private Timestamp articleTime;
    private String articleText;
    private String userName;
    private Boolean articleStatus;

    private int commentId;
    private Timestamp commentTime;
    private String commentText;
    private Boolean commentStatus;

    public static boolean goToMyArticleDetail = false;

    public MyArticle(int myArticleId, int userId, int articleId, Timestamp modifyDate) {
        super();
        this.myArticleId = myArticleId;
        this.userId = userId;
        this.articleId = articleId;
        this.modifyDate = modifyDate;
    }

    // getAllById
    public MyArticle(int articleId, String articleTitle, Timestamp articleTime, String articleText, String userName, Boolean articleStatus) {
        super();
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleTime = articleTime;
        this.articleText = articleText;
        this.userName = userName;
        this.articleStatus = articleStatus;
    }

    public MyArticle (int articleId, String articleTitle, int commentId, Timestamp commentTime, String commentText, String userName, Boolean commentStatus) {
        super();
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.commentId = commentId;
        this.commentTime = commentTime;
        this.commentText = commentText;
        this.userName = userName;
        this.commentStatus = commentStatus;
    }

    public int getMyArticleId() {
        return myArticleId;
    }
    public void setMyArticleId(int myArticleId) {
        this.myArticleId = myArticleId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getArticleId() {
        return articleId;
    }
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
    public Timestamp getModifyDate() {
        return modifyDate;
    }
    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public Timestamp getArticleTime() {
        return articleTime;
    }

    public void setArticleTime(Timestamp articleTime) {
        this.articleTime = articleTime;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public Timestamp getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Timestamp commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Boolean getArticleStatus() { return articleStatus; }

    public void setArticleStatus(Boolean articleStatus) { this.articleStatus = articleStatus; }

    public Boolean getCommentStatus() { return commentStatus; }

    public void setCommentStatus(Boolean commentStatus) { this.commentStatus = commentStatus; }
}
