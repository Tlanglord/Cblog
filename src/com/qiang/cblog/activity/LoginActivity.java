package com.qiang.cblog.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiang.cblog.R;
import com.qiang.cblog.entity.BlogUser;
import com.qiang.cblog.entity.UserManager;
import com.qiang.cblog.requestbase.BaseActivity;
import com.qiang.cblog.requestbase.RequestWrapper;
import com.qiang.cblog.requestbase.RequestWrapper.ReqMethod;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "LoginActivity";

	private static final int REQCODE_TO_ADD_USER = 0;

	private static final int REQ_LOGFIN_USER = 0;

//	private TextView mLoginRegister;
	private TextView mLoginSubmit;
	private EditText mLoginId;
	private EditText mLoginPwd;

	private ScrollView mLoginBefore;
	private LinearLayout mLoginAfter;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cblog_something2:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(intent, REQCODE_TO_ADD_USER);
			break;
		case R.id.login_submit:
			requsetLoginUser();
			break;
		default:
			break;
		}
	}

	@Override
	protected int onContentView() {
		return R.layout.activity_login;
	}

	@Override
	protected void onInitView() {
//		mLoginRegister = (TextView) findViewById(R.id.login_register);
		mLoginSubmit = (TextView) findViewById(R.id.login_submit);

		mLoginId = (EditText) findViewById(R.id.login_id);
		mLoginPwd = (EditText) findViewById(R.id.login_pwd);

		mLoginAfter = (LinearLayout) findViewById(R.id.login_after);
		mLoginBefore = (ScrollView) findViewById(R.id.login_before);
	}

	@Override
	protected void onInitEvent() {
		getSomething2().setOnClickListener(this);
		mLoginSubmit.setOnClickListener(this);
		setSomething2("注册");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (UserManager.getUserManager().isLogin()) {
			mLoginAfter.setVisibility(View.VISIBLE);
//			mLoginRegister.setVisibility(View.GONE);
			getSomething2().setVisibility(View.GONE);
			mLoginBefore.setVisibility(View.GONE);
		} else {
			mLoginBefore.setVisibility(View.VISIBLE);
			mLoginAfter.setVisibility(View.GONE);
//			mLoginRegister.setVisibility(View.VISIBLE);
			getSomething2().setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQCODE_TO_ADD_USER:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "注册成功请登录", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "请注册", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private void requsetLoginUser() {
		String userId = mLoginId.getText().toString();
		String userPwd = mLoginPwd.getText().toString();

		RequestWrapper reqWrp = new RequestWrapper();
		reqWrp.setTag(REQ_LOGFIN_USER);
		reqWrp.setReqMethod(ReqMethod.POST);
		//reqWrp.setReqParam("method", "loginUser");//
		reqWrp.setHeader("Accept", "application/json");
		//		reqWrp.setHeader("Content-Type","form-data");
		//		reqWrp.setHeader("Content-Type", "application/x-www-form-urlencoded");
		Map<String, String> hMap = new HashMap<>();
		hMap.put("method", "loginUser");
		hMap.put("userId", userId);
		hMap.put("userPwd", userPwd);
		reqWrp.setReqParam("method", "loginUser");
		reqWrp.setReqParam("userId", userId);
		reqWrp.setReqParam("userPwd", userPwd);
		//		reqWrp.setReqParams(hMap);
		//		reqWrp.setReqBody(JSON.toJSONString(hMap).getBytes());
		//		reqWrp.setReqBodyContentType(String.format("application/x-www-form-urlencoded; charset=%s", "utf-8"));
		//http://169.254.30.38/wp/site/api/loginUser/
		reqWrp.setUrl("http://169.254.30.38/wp/site/api/loginUser/");
		//		reqWrp.setResponse(new IMidResponse() {
		//
		//			@Override
		//			public void onResponse(Object tag, String response) {
		//				Log.d(TAG, "response -> " + response);o
		//			}
		//		});
		requestConnection(reqWrp);

		//				StringRequest stringRequest = new StringRequest("http://169.254.30.38/wp/site/api/loginUser/", new Response.Listener<String>() {
		//					@Override
		//					public void onResponse(String response) {
		//						Log.d(TAG, "response -> " + response);
		//					}
		//				}, new Response.ErrorListener() {
		//					@Override
		//					public void onErrorResponse(VolleyError error) {
		//						Log.e(TAG, error.getMessage(), error);
		//					}
		//				}) {
		//					@Override
		//					protected Map<String, String> getParams() {
		//						//在这里设置需要post的参数
		//						Map<String, String> hMap = new HashMap<>();
		//						hMap.put("method", "loginUser");
		//						hMap.put("userId", "11111111111");
		//						hMap.put("userPwd", "1111111111");
		//						return hMap;
		//					}
		//					
		//					@Override
		//					public int getMethod() {
		//						return 1;
		//					}
		//				};

		//		VolleyRequestManager.executeRequest(reqWrp.getRequest());
	}

	@Override
	public void onResponse(Object tag, String response) {
		super.onResponse(tag, response);
		Log.d(TAG, "response -> " + response);
		if (TextUtils.isEmpty(response)) {
			return;
		} else {
			int iTag = (int) tag;
			switch (iTag) {
			case REQ_LOGFIN_USER:
				BlogUser user = JSON.parseObject(response, BlogUser.class);
				if (user.getUserState() == 1) {
					Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
					mLoginBefore.setVisibility(View.GONE);
					mLoginAfter.setVisibility(View.VISIBLE);
					getSomething2().setVisibility(View.GONE);
					UserManager.getUserManager().setUser(user);
				} else if (!TextUtils.isEmpty(user.getUserId())) {
					Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "用户存在", Toast.LENGTH_SHORT).show();
				}
				break;
			}

		}
	}

	@Override
	public void onUpdateSkin() {
		
	}
}
