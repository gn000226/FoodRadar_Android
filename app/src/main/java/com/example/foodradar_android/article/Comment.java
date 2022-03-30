package com.example.foodradar_android.article;

public class Comment {

    private int commentId;
    private String commentTime;
    private int articleId;
    private int userId;
    private boolean commentGoosStatus;
    private String commentModifyTime;
    private boolean commentStatus;
    private String commentText;
    private String userName;
    private int commentGoodId;
    private boolean commentGoodStatus;
    private int commentGoodCount;

    Comment() {
        super();
    }

    public Comment(int commentId, String commentTime, int articleId, int userId, String commentModifyTime,
                   boolean commentStatus, String commentText, String userName,boolean commentGoodStatus, int commentGoodCount) {
        super();
        this.commentId = commentId;
        this.commentTime = commentTime;
        this.articleId = articleId;
        this.userId = userId;
        this.commentModifyTime = commentModifyTime;
        this.commentStatus = commentStatus;
        this.commentText = commentText;
        this.userName = userName;
//        this.commentGoodId = commentGoodId;
        this.commentGoodStatus = commentGoodStatus;
        this.commentGoodCount = commentGoodCount;
    }

    //刪除文章用
    public void setStatus(boolean commentStatus){
        this.commentStatus = commentStatus;
    }

    public Comment(int userId, int commentId) {
        super();
        this.commentId = commentId;
        this.userId = userId;
    }
    //新增留言用
    public Comment(int commentId, int articleId, int userId, boolean commentStatus, String commentText) {
        super();
        this.commentId = commentId;
        this.articleId = articleId;
        this.userId = userId;
        this.commentStatus = commentStatus;
        this.commentText = commentText;
    }

    public Comment(int commentId, String commentTime, int articleId, int userId, String commentModifyTime,
                   boolean commentStatus, String commentText, String userName, int commentGoodCount) {
        super();
        this.commentId = commentId;
        this.commentTime = commentTime;
        this.articleId = articleId;
        this.userId = userId;
        this.commentModifyTime = commentModifyTime;
        this.commentStatus = commentStatus;
        this.commentText = commentText;
        this.userName = userName;
//		this.commentGoodId = commentGoodId;
//		this.commentGoodStatus = commentGoodStatus;
        this.commentGoodCount = commentGoodCount;
    }

    //update
    public void setComment(int commentId, String commentText, String commentModifyTime) {
        this.commentText = commentText;
        this.commentModifyTime = commentModifyTime;
        this.commentId = commentId;
    }

//    public Comment(int commentId, int articleId, int userId, boolean commentStatus, String commentText, String userName, String commentTime) {
//        super();
//        this.commentId = commentId;
//        this.articleId = articleId;
//        this.userId = userId;
//        this.commentStatus = commentStatus;
//        this.commentText = commentText;
//        this.userName = userName;
//    }

//    public Comment(int commmentId) {
//        super();
//        this.commmentId = commmentId;
////        this.commentTime = commentTime;
////        this.articleId = articleId;
////        this.userId = userId;
////        this.commentModifyTime = commentModifyTime;
////        this.commentStatus = commentStatus;
////        this.commentText = commentText;
////        this.userName = userName;
//    }

//    public Comment(int articleId, int userId,
//                    boolean commentStatus, String commentText, String userName) {
//        super();
////        this.commmentId = commmentId;
////        this.commentTime = commentTime;
//        this.articleId = articleId;
//        this.userId = userId;
////        this.commentModifyTime = commentModifyTime;
//        this.commentStatus = commentStatus;
//        this.commentText = commentText;
//        this.userName = userName;
//    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommmentId(int commmentId) {
        this.commentId = commentId;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommentModifyTime() {
        return commentModifyTime;
    }

    public void setCommentModifyTime(String commentModifyTime) {
        this.commentModifyTime = commentModifyTime;
    }

    public boolean isCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(boolean commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isCommentGoosStatus() {
        return commentGoosStatus;
    }

    public int getCommentGoodId() {
        return commentGoodId;
    }

    public void setCommentGoodId(int commentGoodId) {
        this.commentGoodId = commentGoodId;
    }

    public boolean isCommentGoodStatus() {
        return commentGoodStatus;
    }

    public void setCommentGoodStatus(boolean commentGoodStatus) {
        this.commentGoodStatus = commentGoodStatus;
    }

    public void setCommentGoosStatus(boolean commentGoosStatus) {
        this.commentGoosStatus = commentGoosStatus;
    }

    public int getCommentGoodCount() {
        return commentGoodCount;
    }

    public void setCommentGoodCount(int commentGoodCount) {
        this.commentGoodCount = commentGoodCount;
    }
}
