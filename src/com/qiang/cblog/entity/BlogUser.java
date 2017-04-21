package com.qiang.cblog.entity;

public class BlogUser {
	private String userId;

	private String userPwd;

	private int userState;

	private String userToken;
	private boolean userCollect;

	public boolean getUserCollect() {
		return userCollect;
	}

	public void setUserCollect(boolean userCollect) {
		this.userCollect = userCollect;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserPwd() {
		return this.userPwd;
	}

	public void setUserState(int userState) {
		this.userState = userState;
	}

	public int getUserState() {
		return this.userState;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getUserToken() {
		return this.userToken;
	}

}
