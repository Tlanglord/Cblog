package com.qiang.cblog.activity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiang.cblog.R;
import com.qiang.cblog.app.AppConfig;
import com.qiang.cblog.db.DBHelper;
import com.qiang.cblog.db.DBOpenHelper;
import com.qiang.cblog.entity.BlogArticle;
import com.qiang.cblog.entity.BlogArticleRep;
import com.qiang.cblog.entity.BlogUser;
import com.qiang.cblog.entity.UserManager;
import com.qiang.cblog.requestbase.BaseActivity;
import com.qiang.cblog.requestbase.RequestWrapper;
import com.qiang.cblog.requestbase.RequestWrapper.onPostExecuteListener;
import com.qiang.cblog.requestbase.RequestWrapper.onPreExecuteListener;

@SuppressLint("SetJavaScriptEnabled")
public class WebviewActivity extends BaseActivity implements OnClickListener {

	private WebView mWebView;
	private final static int REQ_ADD_COLLECT = 0;
	private final static int REQ_DEL_COLLECT = 2;
	private final static int REQ_GET_SINGLE = 1;

	private String mTitle;
	private String mUrl;
	private boolean isNeedHead = false;
	private boolean isData = false;
	private String mArticleId;
	private TextView mCollect;
	private TextView mBack;

	private DBOpenHelper mDbOpenHelper;
	private SQLiteDatabase mSqLiteDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		initView();
		initData();
		initEvent();
	}

	private void getData() {
		Intent intent = getIntent();
		mTitle = intent.getStringExtra("title");
		mUrl = intent.getStringExtra("url");
		isData = intent.getBooleanExtra("isData", false);
		isNeedHead = intent.getBooleanExtra("needHead", false);
		mArticleId = intent.getStringExtra("articleId");
		mDbOpenHelper = DBHelper.getDbOpenHelper();
		mSqLiteDatabase = mDbOpenHelper.getReadableDatabase();
	}

	private void initView() {
		mWebView = (WebView) findViewById(R.id.web_webview);
		mBack = (TextView) findViewById(R.id.webview_back);
	}

	private void initData() {
		WebSettings settings = mWebView.getSettings();
		settings.setAppCacheEnabled(false);
		settings.setJavaScriptEnabled(true);

		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);

		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setSupportZoom(true);
		settings.setDisplayZoomControls(false);

		settings.setDefaultFixedFontSize(40);
		settings.setDefaultFontSize(40);
		settings.setDefaultTextEncodingName("utf-8");

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

		});
		//
		//		if (isData) {
		//			//mWebView.loadData(URLDecoder.decode(mUrl, "utf-8"), "text/html; charset=UTF-8", null);
		//			mWebView.loadDataWithBaseURL(null, mUrl, "text/html; charset=UTF-8", null, mTitle);
		//		} else {
		//			mWebView.loadUrl(mUrl);
		//		}
		requsetArticleContent();

	}

	private void initEvent() {
		mBack.setOnClickListener(this);
	}

	ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			URL url;
			try {
				url = new URL(source);
				drawable = Drawable.createFromStream(url.openStream(), "");
			} catch (Exception e) {
				return null;
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			return drawable;
		}
	};

	public void onBackPressed() {

		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			mWebView.destroy();
			finish();
		}
	}

	@Override
	protected int onContentView() {
		return R.layout.activity_webview;
	}

	@Override
	protected void onInitView() {
		mCollect = (TextView) findViewById(R.id.webview_collect);

	}

	@Override
	protected void onInitEvent() {
		mCollect.setOnClickListener(this);
	};

	BlogArticleRep articleRep = null;

	@Override
	public void onResponse(Object tag, String response) {
		super.onResponse(tag, response);
		if (TextUtils.isEmpty(response)) {
			return;
		} else {
			int iTag = (int) tag;
			switch (iTag) {
			case REQ_ADD_COLLECT:
				BlogUser blogUser = JSON.parseObject(response, BlogUser.class);
				if (blogUser.getUserCollect()) {
					Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
					mCollect.setBackgroundResource(R.drawable.cblog_collect);
				}
				break;
			case REQ_DEL_COLLECT:
				BlogUser blogUser1 = JSON.parseObject(response, BlogUser.class);
				if (!blogUser1.getUserCollect()) {
					Toast.makeText(this, "取消成功", Toast.LENGTH_SHORT).show();
					mCollect.setBackgroundResource(R.drawable.cblog_collect_g);
				}
				break;
			case REQ_GET_SINGLE:
				articleRep = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (articleRep != null && articleRep.getArticle() != null && articleRep.getArticle().size() > 0) {
					BlogArticle article = articleRep.getArticle().get(0);
					//				article.getContent();

					if (articleRep.getIsCollect() == 1) {
						mCollect.setBackgroundResource(R.drawable.cblog_collect);
					} else {
						mCollect.setBackgroundResource(R.drawable.cblog_collect_g);
					}
					if (isData) {
						//mWebView.loadData(URLDecoder.decode(mUrl, "utf-8"), "text/html; charset=UTF-8", null);
						mWebView.loadDataWithBaseURL(null, article.getContent(), "text/html; charset=UTF-8", null, mTitle);
					} else {
						mWebView.loadUrl(mUrl);
					}
				}
				break;

			}

		}
	}

	private void requsetArticleContent() {
		RequestWrapper reqWrp = new RequestWrapper();
		reqWrp.setHeader("Accept", "application/json");
		reqWrp.setHeader("Content-Type", "text/html; charset=UTF-8");
		reqWrp.setUrl(AppConfig.getHttpUrl() + "getSingle/" + UserManager.getUserManager().getUserId() + "/" + mArticleId);
		reqWrp.setTag(REQ_GET_SINGLE);

		reqWrp.setOnPreExecuteListener(new onPreExecuteListener() {

			@Override
			public String onPreExecute() {

				BlogArticleRep baRep = new BlogArticleRep();

				if (UserManager.getUserManager().isLogin()) {
					String sqlCollect = "SELECT userid FROM collect WHERE id=" + mArticleId + "HAVING userid=" + UserManager.getUserManager().getUserId();
					Cursor cursor = mSqLiteDatabase.rawQuery(sqlCollect, null);
					List<Map<String, String>> listMap = DBHelper.convertCursorToList(cursor);

					if (listMap != null && listMap.size() > 0) {
						Map<String, String> map = listMap.get(0);
						String userId = map.get("userId");
						if (!TextUtils.isEmpty(userId) && userId.equals(UserManager.getUserManager().getUserId())) {
							baRep.setIsCollect(1);
						}
					}
				}

				String sqlContent = "SELECT content FROM androids where id=" + mArticleId;
				Cursor cursor = mSqLiteDatabase.rawQuery(sqlContent, null);
				List<Map<String, String>> listMap = DBHelper.convertCursorToList(cursor);
				List<BlogArticle> listArticles = new ArrayList<BlogArticle>();
				if (listMap != null && listMap.size() > 0) {
					for (Map<String, String> map : listMap) {
						BlogArticle article = new BlogArticle();
						article.setContent(map.get("content"));
						listArticles.add(article);
					}
					baRep.setArticles(listArticles);
				}

				if (baRep.getArticle() != null && baRep.getArticle().size() > 0) {
					if (TextUtils.isEmpty(baRep.getArticle().get(0).getContent())) {
						baRep = null;
					}
				}
				return JSON.toJSONString(baRep);
			}
		});

		reqWrp.setOnPostExecuteListener(new onPostExecuteListener() {

			@Override
			public void onPostExecute(String result) {
				if (!TextUtils.isEmpty(result)) {
					BlogArticleRep rep = JSON.parseObject(result, BlogArticleRep.class);
					if (rep != null) {
						List<BlogArticle> articles = rep.getArticle();
						if (articles != null && articles.size() > 0) {
							BlogArticle article = articles.get(0);
							ContentValues cValues = new ContentValues();
							cValues.put("content", article.getContent());
							String where = "id=" + mArticleId;
							mSqLiteDatabase.update("androids", cValues, where, null);
						}
					}
				}
			}
		});

		requestConnection(reqWrp);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.webview_collect:
			String userId = UserManager.getUserManager().getUserId();
			if (UserManager.getUserManager().isLogin()) {
				RequestWrapper requestWrapper = new RequestWrapper();
				if (articleRep.getIsCollect() == 0) {
					requestWrapper.setTag(REQ_ADD_COLLECT);
					requestWrapper.setUrl("http://169.254.30.38/wp/site/api/addCollect/" + userId + "/" + mArticleId);
				} else {
					requestWrapper.setTag(REQ_DEL_COLLECT);
					requestWrapper.setUrl("http://169.254.30.38/wp/site/api/delCollect/" + userId + "/" + mArticleId);

				}
				requestWrapper.setHeader("Accept", "application/json");
				requestWrapper.setHeader("Content-Type", "text/html; charset=UTF-8");
				requestConnection(requestWrapper);
			} else {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.webview_back:
			finish();
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