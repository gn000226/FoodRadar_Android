package com.example.foodradar_android.article;

import java.io.Serializable;

public class ArticleGood implements Serializable {
    private int articleGoodId;
    private int articleId;
    private int userId;
    private int articleGoodCount;
    private int articleGoodStatus;

    public ArticleGood(int userId) {
        this.userId = userId;
    }

    public ArticleGood(int userId , int articleGoodId, int articleId) {
        super();
        this.articleGoodId = articleGoodId;
        this.articleId = articleId;
        this.userId = userId;
    }

    public ArticleGood(int articleGoodId, int articleId, int userId, int articleGoodCount, int articleGoodStatus) {
        this.articleGoodId = articleGoodId;
        this.articleId = articleId;
        this.userId = userId;
        this.articleGoodCount = articleGoodCount;
        this.articleGoodStatus = articleGoodStatus;
    }

    public int getArticleGoodId() {
        return articleGoodId;
    }

    public void setArticleGoodId(int articleGoodId) {
        this.articleGoodId = articleGoodId;
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

    public int getArticleGoodCount() {
        return articleGoodCount;
    }

    public void setArticleGoodCount(int articleGoodCount) {
        this.articleGoodCount = articleGoodCount;
    }

    public int getArticleGoodStatus() {
        return articleGoodStatus;
    }

    public void setArticleGoodStatus(int articleGoodStatus) {
        this.articleGoodStatus = articleGoodStatus;
    }
}
