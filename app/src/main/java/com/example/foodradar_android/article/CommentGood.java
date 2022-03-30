package com.example.foodradar_android.article;

public class CommentGood {

    private int commentGoodId;
    private int commentId;
    private int userId;
    private int commentGoodCount;
    private boolean commentGoodStatus;


    public CommentGood() {
        super();
    }

    public CommentGood(int commentGoodId, int commentId, int userId, int commentGoodCount, boolean commentGoodStatus) {
        this.commentGoodId = commentGoodId;
        this.commentId = commentId;
        this.userId = userId;
        this.commentGoodCount = commentGoodCount;
        this.commentGoodStatus = commentGoodStatus;
    }

    public int getCommentGoodId() {
        return commentGoodId;
    }

    public void setCommentGoodId(int commentGoodId) {
        this.commentGoodId = commentGoodId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentGoodCount() {
        return commentGoodCount;
    }

    public void setCommentGoodCount(int commentGoodCount) {
        this.commentGoodCount = commentGoodCount;
    }

    public boolean isCommentGoodStatus() {
        return commentGoodStatus;
    }

    public void setCommentGoodStatus(boolean commentGoodStatus) {
        this.commentGoodStatus = commentGoodStatus;
    }
}
