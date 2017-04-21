package com.qiang.cblog.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.qiang.cblog.R;
import com.qiang.cblog.requestbase.BaseActivity;
import com.qiang.cblog.ui.SlideSwitch;
import com.qiang.cblog.ui.SlideSwitch.OnStateChangedListener;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private SlideSwitch mLightDrakSwitch;
	private boolean isLightSwitch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSomething1("设置");
	}

	@Override
	protected int onContentView() {
		return R.layout.activity_setting;
	}

	@Override
	protected void onInitView() {
		mLightDrakSwitch = (SlideSwitch) findViewById(R.id.light_dark_switch);
		mLightDrakSwitch.setOnStateChangedListener(new OnStateChangedListener() {
			
			@Override
			public void onStateChanged(boolean state) {
				if (isLightSwitch) {
					setTheme(R.style.lightTheme);
				} else {
					setTheme(R.style.AppBaseTheme);
				}
				isLightSwitch = !isLightSwitch;
			}
		});
	}

	@Override
	protected void onInitEvent() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.light_dark_switch:
			
			break;
		default:
			break;
		}
	}

	@Override
	public void onUpdateSkin() {
		// TODO Auto-generated method stub
		
	}

}
