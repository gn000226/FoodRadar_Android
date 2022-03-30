package com.example.foodradar_android.article;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Img implements Serializable {

    private int imgId;
    private int articleId;
    private byte[] imgByte;
    private List<byte[]> imgByteList;

    public Img() {
        super();
    }


    public Img(int imgId, int articleId) {
        super();
        this.imgId = imgId;
        this.articleId = articleId;
    }



    public int getImgId() {
        return imgId;
    }



    public void setImgId(int imgId) {
        this.imgId = imgId;
    }



    public int getArticleId() {
        return articleId;
    }



    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public Img(int imgId, int articleId, byte[] imgByte) {
        this.imgId = imgId;
        this.articleId = articleId;
        this.imgByte = imgByte;
    }

    public byte[] getImgByte() {
        return imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }

    public List<byte[]> getImgByteList() {
        return imgByteList;
    }

    public void setImgByteList(List<byte[]> imgByteList) {
        this.imgByteList = imgByteList;
    }

    //
    public Img(List<byte[]> imgByteList) {
        this.imgByteList = imgByteList;
    }
}
