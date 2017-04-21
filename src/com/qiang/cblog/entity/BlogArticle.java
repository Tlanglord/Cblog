package com.qiang.cblog.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class BlogArticle {
	private String title;

	private String content;

	private String postdate;

	private String id;

	private String csdnid;

	private String cdate;
	
	
	@JSONField(name="title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JSONField(name="title")
	public String getTitle() {
		return this.title;
	}

	@JSONField(name="content")
	public void setContent(String content) {
		this.content = content;
	}

	@JSONField(name="content")
	public String getContent() {
		return this.content;
	}

	@JSONField(name="postdate")
	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}

	@JSONField(name="postdate")
	public String getPostdate() {
		return this.postdate;
	}

	@JSONField(name="id")
	public void setId(String id) {
		this.id = id;
	}

	@JSONField(name="id")
	public String getId() {
		return this.id;
	}

	@JSONField(name="csdnid")
	public void setCsdnid(String csdnid) {
		this.csdnid = csdnid;
	}

	@JSONField(name="csdnid")
	public String getCsdnid() {
		return this.csdnid;
	}

	@JSONField(name="cdate")
	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	@JSONField(name="cdate")
	public String getCdate() {
		return this.cdate;
	}

}
