package com.qiang.cblog.requestbase;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyRequestManager {

	private static Context mContext;

	public static void init(Context context) {
		mContext = context;
		getInstance();
	}

	public static StringRequest executeRequest(StringRequest request){
		return (StringRequest) getInstance().add(request);
	}
	
	public static RequestQueue getInstance() {
		return RequestState.mRequestQueue;
	}

	static class RequestState {
		public static final RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
	}
}
