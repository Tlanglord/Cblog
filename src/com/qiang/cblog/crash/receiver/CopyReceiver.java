package com.qiang.cblog.crash.receiver;

import com.qiang.cblog.crash.utils.ClipUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CopyReceiver extends BroadcastReceiver{
	
	private String TAG = "CopyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null){
			String text = intent.getStringExtra("crashText");
			Log.v(TAG, text);
			ClipUtil.copy(context, text);
		}
	}
	
}
