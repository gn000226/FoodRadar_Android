package com.example.foodradar_android.res;

import java.io.Serializable;
import java.sql.Timestamp;

public class Res implements Serializable, Comparable {
	private int resId;
	private String resName;
	private String resAddress;
	private Double resLat;
	private Double resLon;
	private String resTel;
	private String resHours;
	private int resCategoryId;
	private String resCategoryInfo;
	private boolean resEnable;
	private int userId;
	private String userName;
	private Timestamp modifyDate;
	private Float rating;
	private Float distance;
	private boolean myRes;
	
	public Res(int resId, String resName, String resAddress, Double resLat, Double resLon, String resTel,
			String resHours, int resCategoryId, boolean resEnable, int userId, Timestamp modifyDate) {
		super();
		this.resId = resId;
		this.resName = resName;
		this.resAddress = resAddress;
		this.resLat = resLat;
		this.resLon = resLon;
		this.resTel = resTel;
		this.resHours = resHours;
		this.resCategoryId = resCategoryId;
		this.resEnable = resEnable;
		this.userId = userId;
		this.modifyDate = modifyDate;
	}

	//暘璿
//	public Res(int resId, String resName, String resAddress , boolean resEnable) {
//		super();
//		this.resId = resId;
//		this.resName = resName;
//		this.resAddress = resAddress;
//		this.resEnable = resEnable;
//	}

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

	public Double getResLat() {
		return resLat;
	}

	public void setResLat(Double resLat) {
		this.resLat = resLat;
	}

	public Double getResLon() {
		return resLon;
	}

	public void setResLon(Double resLon) {
		this.resLon = resLon;
	}

	public String getResTel() {
		return resTel;
	}

	public void setResTel(String resTel) {
		this.resTel = resTel;
	}

	public String getResHours() {
		return resHours;
	}

	public void setResHours(String resHours) {
		this.resHours = resHours;
	}

	public int getResCategoryId() {
		return resCategoryId;
	}

	public void setResCategoryId(int resCategoryId) {
		this.resCategoryId = resCategoryId;
	}

	public boolean isResEnable() {
		return resEnable;
	}

	public void setResEnable(boolean resEnable) {
		this.resEnable = resEnable;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Timestamp getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Timestamp modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getResCategoryInfo() {
		return resCategoryInfo;
	}

	public void setResCategoryInfo(String resCategoryInfo) {
		this.resCategoryInfo = resCategoryInfo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Object o) {
		return Float.compare(this.distance, ((Res)o).getDistance());
	}

	public boolean isMyRes() {
		return myRes;
	}

	public void setMyRes(boolean myRes) {
		this.myRes = myRes;
	}
}
