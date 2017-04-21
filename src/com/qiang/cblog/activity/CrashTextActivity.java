package com.qiang.cblog.activity;

import com.qiang.cblog.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class CrashTextActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crash_text);
		String crashText =getIntent().getStringExtra("crashText");
		TextView text=(TextView)findViewById(R.id.crash_text_txt);
		text.setText(crashText);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String crashText =getIntent().getStringExtra("crashText");
		if(!TextUtils.isEmpty(crashText)){
			TextView text=(TextView)findViewById(R.id.crash_text_txt);
			text.setText(crashText);
		}
		
	}
}
