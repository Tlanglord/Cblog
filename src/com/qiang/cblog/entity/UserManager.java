package com.qiang.cblog.entity;

public class UserManager {
	private BlogUser mUser;
	private static UserManager mUserManager;
	private boolean isLogin = false;

	public static synchronized UserManager getUserManager() {
		if (mUserManager == null) {
			mUserManager = new UserManager();
		}

		return mUserManager;
	}

	public void setUser(BlogUser user) {
		mUser = user;
		isLogin = mUser.getUserState() == 1 ? true : false;
	}

	public boolean isLogin() {
		if (mUser == null || mUser.getUserState() == 0) {
			return false;
		}

		if (mUser.getUserState() == 1) {
			return true;
		}

		return false;
	}

	public String getUserId() {
		if (mUser == null) {
			return "";
		}
		return mUser.getUserId();
	}

	public String getUserToken() {
		if (mUser == null) {
			return "";
		}
		return mUser.getUserToken();
	}

}
