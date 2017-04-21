package com.qiang.cblog.app;

import com.qiang.cblog.crash.handler.CrashHandler;
import com.qiang.cblog.crash.handler.CrashHandler.CrashNotifyConfig;
import com.qiang.cblog.crash.handler.CrashHandler.OnPostCrash;
import com.qiang.cblog.crash.model.CrashModel;
import com.qiang.cblog.db.DBHelper;
import com.qiang.cblog.requestbase.RequestWrapper;
import com.qiang.cblog.requestbase.RequestWrapper.ReqMethod;
import com.qiang.cblog.requestbase.VolleyRequestManager;
import com.testin.agent.TestinAgent;

import android.app.Application;
import cn.smssdk.SMSSDK;

public class CBlogApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		VolleyRequestManager.init(this);
		SMSSDK.initSDK(this, "14b049d8bcdb3", "f7e6aae5500b715633fea4f5b0105ca2", false);
		CrashHandler.getInstance().init(this);
		CrashHandler.getInstance().setOnPostCrash(new OnPostCrash() {

			@Override
			public void postCrash(byte[] bytes) {
				postReport(bytes);
			}
			
			@Override
			public void postCrash(CrashModel model) {
				
			}
		}).setNofityConfig(CrashNotifyConfig.OPEN);
		AppConfig.init(true);
		TestinAgent.init(this, "704ec407b576a8127f6af9c016866587", "");
		new Thread() {
			public void run() {
				DBHelper.init(getApplicationContext(), 2);
			};
		}.start();
	}

	private void postReport(byte[] bytes) {
		RequestWrapper reqWrp = new RequestWrapper();
		reqWrp.setReqMethod(ReqMethod.POST);
		reqWrp.setUrl("http://www.wpdqq.com/site/api/postReport/");
		reqWrp.setReqBody(bytes);
		VolleyRequestManager.executeRequest(reqWrp.getRequest());
	}
	
}
