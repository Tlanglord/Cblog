package com.qiang.cblog.requestbase;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class RequestWrapper {
	private StringRequest mRequest;
	private Object mTag;
	private String mUrl;
	private IMidResponse mResponse;
	private Map<String, String> mHeader = new HashMap<String, String>();
	private Map<String, String> mParams = new HashMap<String, String>();
	private int mReqMethod;
	private byte[] mBytes;

	private onPreExecuteListener mPreExecuteListener;
	private onPostExecuteListener mPostExecuteListener;
	private boolean isExecuteReq = false;

	private String mBodyContentType;

	public String getReqBodyContentType() {
		return mBodyContentType;
	}

	public void setReqBodyContentType(String bodyContentType) {
		this.mBodyContentType = bodyContentType;
	}

	public onPostExecuteListener getPostExecuteListener() {
		return mPostExecuteListener;
	}

	public void setOnPostExecuteListener(onPostExecuteListener postExecuteListener) {
		this.mPostExecuteListener = postExecuteListener;
	}

	public onPreExecuteListener getPreExecuteListener() {
		return mPreExecuteListener;
	}

	public void setOnPreExecuteListener(onPreExecuteListener preExecuteListener) {
		this.mPreExecuteListener = preExecuteListener;
	}

	public byte[] getReqBody() {
		return mBytes;
	}

	public void setReqBody(byte[] bytes) {
		this.mBytes = bytes;
	}

	public interface ReqMethod {
		int DEPRECATED_GET_OR_POST = -1;
		int GET = 0;
		int POST = 1;
		int PUT = 2;
		int DELETE = 3;
	}

	public int getReqMethod() {
		return mReqMethod;
	}

	public void setReqMethod(int reqMethod) {
		this.mReqMethod = reqMethod;
	}

	public Map<String, String> getReqParams() {
		return mParams;
	}

	public void setReqParams(Map<String, String> mParams) {
		this.mParams = mParams;
	}

	public void setReqParam(String key, String value) {
		if (mParams == null) {
			mParams = new HashMap<String, String>();
		}
		mParams.put(key, value);
	}

	public Map<String, String> getReqHeaders() {
		return mHeader;
	}

	public void setHeader(Map<String, String> mHeader) {
		this.mHeader = mHeader;
	}

	public void setHeader(String key, String value) {
		if (mHeader == null) {
			mHeader = new HashMap<String, String>();
		}
		mHeader.put(key, value);
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object tag) {
		this.mTag = tag;
	}

	public IMidResponse getResponse() {
		return mResponse;
	}

	public void setResponse(IMidResponse response) {
		this.mResponse = response;
	}

	public StringRequest getRequest() {
		mRequest = new StringRequest(getUrl(), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				mResponse.onResponse(mTag, response);
				if (getPostExecuteListener() != null) {
					getPostExecuteListener().onPostExecute(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mResponse.onResponse(mTag, null);
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				return getReqParams();
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return getReqHeaders();
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				return super.getBody() == null ? getReqBody() : super.getBody();
			}

			@Override
			public int getMethod() {
				return getReqMethod();
			}

		};
		mRequest.setTag(mTag);
		return mRequest;
	}

	public void execute() {
		if (getPreExecuteListener() != null) {
			String result = getPreExecuteListener().onPreExecute();
			if (!TextUtils.isEmpty(result) && !result.equals("null")) {
				if (mResponse != null) {
					//					isExecuteReq = false;
					mResponse.onResponse(mTag, result);
				}
			} else {
				VolleyRequestManager.executeRequest(getRequest());
			}
		} else {
			VolleyRequestManager.executeRequest(getRequest());
		}
	}

	public interface onPreExecuteListener {
		public String onPreExecute();
	}

	public interface onPostExecuteListener {
		public void onPostExecute(String result);
	}

}
