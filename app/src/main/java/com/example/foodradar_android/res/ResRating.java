package com.example.foodradar_android.res;

public class ResRating {
	private Integer resRatingId;
	private Integer resId;
	private Integer userId;
	private Float rating;
	
	public ResRating(Integer resRatingId, Integer resId, Integer userId, Float rating) {
		super();
		this.resRatingId = resRatingId;
		this.resId = resId;
		this.userId = userId;
		this.rating = rating;
	}

	public Integer getResRatingId() {
		return resRatingId;
	}

	public void setResRatingId(Integer resRatingId) {
		this.resRatingId = resRatingId;
	}

	public Integer getResId() {
		return resId;
	}

	public void setResId(Integer resId) {
		this.resId = resId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}
	
	
}
