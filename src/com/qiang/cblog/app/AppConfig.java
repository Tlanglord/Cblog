package com.qiang.cblog.app;

public class AppConfig {

	public final static String httpL = "http://169.254.30.38/wp/site/api/";
	public final static String httpR = "http://www.wpdqq.com/site/api/";

	public static boolean isUseHttpR = false;

	public static void init(boolean isUseR) {
		isUseHttpR = isUseR;
	}

	public static String getHttpUrl() {
		if (isUseHttpR) {
			return httpR;
		}
		return httpL;
	}

}
