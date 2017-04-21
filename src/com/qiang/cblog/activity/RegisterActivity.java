package com.qiang.cblog.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSON;
import com.qiang.cblog.R;
import com.qiang.cblog.entity.BlogUser;
import com.qiang.cblog.requestbase.BaseActivity;
import com.qiang.cblog.requestbase.RequestWrapper;

public class RegisterActivity extends BaseActivity implements OnClickListener, TextWatcher {

	private static final int REQ_ADD_USER = 0;

	private View mViewPhone;
	private View mViewPasssword;
	private View mViewVerify;
	private EditText mRegisterPhone;
	private EditText mRegisterVerifyCode;
	private EditText mRegisterPassword;

	private TextView mRegisterSubmit;
//	private TextView mRegisterBack;
//	private TextView mRegisterStatetext;
	private ViewFlipper mRegisterViewFlipper;

	private Animation leftInAnimation;
	private Animation leftOutAnimation;
	private Animation rightInAnimation;
	private Animation rightOutAnimation;

	private int mFlipperCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
		leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
		rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	}

	@Override
	protected int onContentView() {
		return R.layout.activity_register;
	}

	@Override
	protected void onInitView() {
		mViewPhone = LayoutInflater.from(this).inflate(R.layout.register_phone, null);
		mViewVerify = LayoutInflater.from(this).inflate(R.layout.register_verify, null);
		mViewPasssword = LayoutInflater.from(this).inflate(R.layout.register_password, null);

		mRegisterPhone = (EditText) mViewPhone.findViewById(R.id.register_phone);
		mRegisterVerifyCode = (EditText) mViewVerify.findViewById(R.id.register_verifycode);
		mRegisterPassword = (EditText) mViewPasssword.findViewById(R.id.register_password);
		mRegisterSubmit = (TextView) findViewById(R.id.register_submit);
//		mRegisterStatetext = (TextView) findViewById(R.id.register_statetext);
//		mRegisterBack = (TextView) findViewById(R.id.register_back);
		mRegisterViewFlipper = (ViewFlipper) findViewById(R.id.register_view_flipper);

		mRegisterViewFlipper.addView(mViewPhone);
		mRegisterViewFlipper.addView(mViewVerify);
		mRegisterViewFlipper.addView(mViewPasssword);
	}

	@Override
	protected void onInitEvent() {
		mRegisterSubmit.setOnClickListener(this);
//		mRegisterBack.setOnClickListener(this);
		// mRegisterPhone.addTextChangedListener(this);

		EventHandler eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {

				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// 提交验证码成功
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// 获取验证码成功
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
						// ArrayList<>
					}
				} else if (result == SMSSDK.RESULT_ERROR) {
					if (data != null) {
						((Throwable) data).printStackTrace();
					}
				}
			}
		};
		SMSSDK.registerEventHandler(eh); // 注册短信回调
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_submit:
			if (mFlipperCount < 2) {
				if (mFlipperCount == 0) {
					// SMSSDK.getVerificationCode("86", mRegisterPhone.getText()
					// .toString());
				}
				mRegisterViewFlipper.setInAnimation(leftInAnimation);
				mRegisterViewFlipper.setOutAnimation(leftOutAnimation);
				mRegisterViewFlipper.showNext();
				mFlipperCount++;
				if (mFlipperCount == 1) {
					mRegisterSubmit.setText("继续");
					setSomething1("请输入验证码");
				}
				if (mFlipperCount == 2) {
					mRegisterSubmit.setText("完成");
					setSomething1("请输入密码");
				}
			} else if (mFlipperCount == 2) {
				requsetAddUser();
			}
			//			break;
			//		case R.id.cblog_back:
			//			if (mFlipperCount >= 1) {
			//				mRegisterViewFlipper.setInAnimation(rightInAnimation);
			//				mRegisterViewFlipper.setOutAnimation(rightOutAnimation);
			//				mRegisterViewFlipper.showPrevious();
			//				mFlipperCount--;
			//				if (mFlipperCount == 1) {
			//					mRegisterSubmit.setText("继续");
			//					setSomething1("请输入验证码");
			//				}
			//				if (mFlipperCount == 0) {
			//					mRegisterSubmit.setText("发送验证码");
			//					setSomething1("请输入手机号");
			//				}
			//			} else if (mFlipperCount == 0) {
			//				finish();
			//			}
			//			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onResponse(Object tag, String response) {
		super.onResponse(tag, response);
		if (TextUtils.isEmpty(response)) {
			return;
		} else {
			int iTag = (int) tag;
			switch (iTag) {
			case REQ_ADD_USER:
				BlogUser user = JSON.parseObject(response, BlogUser.class);
				if (user.getUserState() == 1) {
					setResult(RESULT_OK);
					back();
				} else {
					Toast.makeText(this, "已注册", Toast.LENGTH_SHORT).show();
					mFlipperCount = 0;
				}
				break;
			}
		}
	}

	private void requsetAddUser() {
		String userId = mRegisterPhone.getText().toString();
		String userPwd = mRegisterPassword.getText().toString();

		RequestWrapper reqWrp = new RequestWrapper();
		reqWrp.setTag(REQ_ADD_USER);
		reqWrp.setHeader("Accept", "application/json");
		reqWrp.setHeader("Content-Type", "text/html; charset=UTF-8");
		///wp/site/api/addUser/11111111111/dodsll;1
		///wp/site/api/addUser/11111111111/dodsll;1
		reqWrp.setUrl("http://169.254.30.38/wp/site/api/addUser/" + userId + "/" + userPwd);
		requestConnection(reqWrp);
	}

	@Override
	protected void back() {
		
		if (mFlipperCount >= 1) {
			mRegisterViewFlipper.setInAnimation(rightInAnimation);
			mRegisterViewFlipper.setOutAnimation(rightOutAnimation);
			mRegisterViewFlipper.showPrevious();
			mFlipperCount--;
			if (mFlipperCount == 1) {
				mRegisterSubmit.setText("继续");
				setSomething1("请输入验证码");
			}
			if (mFlipperCount == 0) {
				mRegisterSubmit.setText("发送验证码");
				setSomething1("请输入手机号");
			}
		} else if (mFlipperCount == 0) {
			SMSSDK.unregisterAllEventHandler();
			finish();
		}
//		super.back();

	}

	@Override
	public void onUpdateSkin() {
		// TODO Auto-generated method stub
		
	}

}
