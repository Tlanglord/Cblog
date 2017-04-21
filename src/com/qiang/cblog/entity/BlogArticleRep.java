package com.qiang.cblog.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class BlogArticleRep {
	private List<BlogArticle> articles;

	private int statu;
	
	private int isCollect;

	@JSONField(name="isCollect")
	public int getIsCollect() {
		return isCollect;
	}

	@JSONField(name="isCollect")
	public void setIsCollect(int isCollect) {
		this.isCollect = isCollect;
	}
	
	@JSONField(name="data")
	public void setArticles(List<BlogArticle> data) {
		this.articles = data;
	}

	@JSONField(name="data")
	public List<BlogArticle> getArticle() {
		return this.articles;
	}

	@JSONField(name="statu")
	public void setStatu(int statu) {
		this.statu = statu;
	}
	@JSONField(name="statu")
	public int getStatu() {
		return this.statu;
	}

}
