package com.qiang.cblog.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ArticlePagerAdapter extends PagerAdapter {
	private List<View> mViews;

	public void setViews(List<View> views) {
		if (mViews == null) {
			mViews = new ArrayList<View>();
		}
		mViews.clear();
		mViews.addAll(views);
		notifyDataSetChanged();
	}

	public void addView(View view) {
		if (mViews == null) {
			mViews = new ArrayList<View>();
		}
		mViews.clear();
		mViews.add(view);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViews.get(position));
		return mViews.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
