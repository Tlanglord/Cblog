package com.qiang.cblog.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.qiang.cblog.R;
import com.qiang.cblog.activity.LoginActivity;
import com.qiang.cblog.activity.SettingActivity;

public class SettingPopupWindow extends PopupWindow {

	private Context mContext;
	private View view;

	public SettingPopupWindow(Context context) {
		super(context);
		mContext = context;
		initView();
		initEvent();
	}

	private void initView() {
		view = LayoutInflater.from(mContext).inflate(R.layout.setting_pop, null);
		setOutsideTouchable(true);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setAnimationStyle(android.R.style.Animation);
	}

	private void initEvent() {
		view.findViewById(R.id.pop_user).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(mContext, LoginActivity.class);
				mContext.startActivity(intent);
				dismiss();
			}
		});
		view.findViewById(R.id.pop_setting).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, SettingActivity.class);
				mContext.startActivity(intent);
				dismiss();
			}
		});
	}

}
