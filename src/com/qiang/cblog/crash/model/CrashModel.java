package com.qiang.cblog.crash.model;

import java.io.Serializable;

public class CrashModel  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String time;
	private String content;
	private String device;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "{"+"time:"+time +"," +"device:" +device+"," + "content:"+content+"}";
	}

}
