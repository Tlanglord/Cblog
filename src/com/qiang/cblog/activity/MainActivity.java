package com.qiang.cblog.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiang.cblog.R;
import com.qiang.cblog.adapter.ArticleAdapter;
import com.qiang.cblog.adapter.ArticlePagerAdapter;
import com.qiang.cblog.app.AppConfig;
import com.qiang.cblog.crash.utils.NotifyUtil;
import com.qiang.cblog.db.DBHelper;
import com.qiang.cblog.db.DBOpenHelper;
import com.qiang.cblog.entity.BlogArticle;
import com.qiang.cblog.entity.BlogArticleRep;
import com.qiang.cblog.entity.UserManager;
import com.qiang.cblog.requestbase.BaseActivity;
import com.qiang.cblog.requestbase.RequestWrapper;
import com.qiang.cblog.requestbase.RequestWrapper.onPostExecuteListener;
import com.qiang.cblog.requestbase.RequestWrapper.onPreExecuteListener;
import com.qiang.cblog.skin.ISkinUpdate;
import com.qiang.cblog.skin.SkinManager;
import com.qiang.cblog.ui.SettingPopupWindow;
import com.qiang.cblog.ui.SuperListView;
import com.qiang.cblog.ui.SuperListView.OnLoadMoreListener;
import com.qiang.cblog.ui.SuperListView.OnRefreshListener;

public class MainActivity extends BaseActivity implements OnClickListener, OnPageChangeListener,ISkinUpdate {
	private static final int REQ_NOLMAL_TAG = 0;
	private static final int REQ_REFRESH_TAG = 1;
	private static final int REQ_LOADMORE_TAG = 2;

	private static final int REQ_COLLECT_NOLMAL_TAG = 3;
	private static final int REQ_COLLECT_REFRESH_TAG = 4;
	private static final int REQ_COLLECT_LOADMORE_TAG = 5;

	private int index_count = 0;
	private int collect_index_count = 0;

	private SuperListView mListViewAll;
	private SuperListView mListViewCollect;
	private List<BlogArticle> mListArticleAll;
	private List<BlogArticle> mListArticleCollect;
	//	private List<BlogArticle> mListArticleAll;
	private ArticleAdapter mAdapterArticleAll;
	private ArticleAdapter mAdapterArticleCollect;
	private ViewPager mArticlePager;

	private TextView mSearchView;
	private TextView mSettingView;

	private View mArticelAll;
	private View mArticelCollect;
	private View[] mViews = new View[2];
	private TextView mViewAll;
	private TextView mViewCollect;
	private TextView mViewScrollLeft;
	private TextView mViewScrollRight;
	private TextView mPost;

	private DBOpenHelper mDbOpenHelper;
	private SQLiteDatabase mSqLiteDatabase;

	private int mTabDefColor;
	private int mTabSelectColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbOpenHelper = DBHelper.getDbOpenHelper();
		mSqLiteDatabase = mDbOpenHelper.getReadableDatabase();
		reqeustArticle(REQ_NOLMAL_TAG);
		mTabDefColor = getResources().getColor(R.color.tab_def);
		mTabSelectColor = getResources().getColor(R.color.tab_select);
	}

	@Override
	protected int onContentView() {
		return R.layout.activity_article;
	}

	@Override
	protected void onInitView() {
		mArticelAll = LayoutInflater.from(this).inflate(R.layout.article_all, null);
		mArticelCollect = LayoutInflater.from(this).inflate(R.layout.article_collect, null);
		mListViewAll = (SuperListView) mArticelAll.findViewById(R.id.article_all_listview);
		mListViewCollect = (SuperListView) mArticelCollect.findViewById(R.id.article_collect_listview);

		mArticlePager = (ViewPager) findViewById(R.id.article_viewpager);
		ArticlePagerAdapter adapter = new ArticlePagerAdapter();
		List<View> list = new ArrayList<View>();
		list.add(mArticelAll);
		list.add(mArticelCollect);
		adapter.setViews(list);
		mArticlePager.setAdapter(adapter);

		mSearchView = (TextView) findViewById(R.id.article_search);
		mSettingView = (TextView) findViewById(R.id.article_setting);

		mViewAll = (TextView) findViewById(R.id.article_all);
		mViewCollect = (TextView) findViewById(R.id.article_collect);
		mViewScrollLeft = (TextView) findViewById(R.id.aritcle_scroll_left);
		mViewScrollRight = (TextView) findViewById(R.id.aritcle_scroll_right);
		mPost = (TextView) findViewById(R.id.post);
		mPost.setVisibility(View.VISIBLE);

		mViews[0] = mViewAll;
		mViews[1] = mViewCollect;
	}

	@Override
	protected void onInitEvent() {
		mListViewAll.setRefresh(false);
		mListViewAll.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				reqeustArticle(REQ_REFRESH_TAG);
			}
		});

		mListViewAll.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				reqeustArticle(REQ_LOADMORE_TAG);
			}
		});

		mListViewCollect.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				reqeustCollect(REQ_COLLECT_REFRESH_TAG);
			}
		});

		mListViewCollect.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				reqeustCollect(REQ_COLLECT_LOADMORE_TAG);
			}
		});

		mSearchView.setOnClickListener(this);
		mSettingView.setOnClickListener(this);
		mArticlePager.addOnPageChangeListener(this);
		mPost.setOnClickListener(this);

		mViewAll.setOnClickListener(this);
		mViewCollect.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_search:
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
			break;
		case R.id.article_setting:
			SettingPopupWindow setPop = new SettingPopupWindow(this);
			setPop.showAsDropDown(v, -v.getMeasuredWidth(), -v.getMeasuredHeight());
			break;
		case R.id.post:
			NotifyUtil.notifyMessage(getApplicationContext(), Math.random()+"");
			String temp = null;
			temp.toString();
			break;
		case R.id.article_all:
			mArticlePager.setCurrentItem(0, true);
			break;
		case R.id.article_collect:
			mArticlePager.setCurrentItem(1, true);
			break;
		}
	}

	private List<Map<String, String>> convertCursorToList(Cursor cursor) {
		List<Map<String, String>> mlist = new ArrayList<Map<String, String>>();
		String[] keys = cursor.getColumnNames();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < keys.length; i++) {
					map.put(keys[i], cursor.getString(i));
				}
				mlist.add(map);
			}
		}

		return mlist;
	}

	private void reqeustArticle(int tag) {
		RequestWrapper reqWrp = new RequestWrapper();
		if (tag != REQ_LOADMORE_TAG) {
			index_count = 0;
		} else {
			index_count++;
		}

		reqWrp.setTag(tag);
		reqWrp.setHeader("Accept", "application/json");
		reqWrp.setHeader("Content-Type", "text/html; charset=UTF-8");
		reqWrp.setUrl(AppConfig.getHttpUrl() + "getArticles/" + index_count);

		reqWrp.setOnPreExecuteListener(new onPreExecuteListener() {

			@Override
			public String onPreExecute() {
				int index_s = 10;
				int index_e = index_count * 10;
				String sql = "SELECT title,postdate,id FROM androids Limit " + index_s + " Offset " + index_e;
				Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
				List<Map<String, String>> list = convertCursorToList(cursor);
				List<BlogArticle> articles = new ArrayList<BlogArticle>();
				BlogArticleRep rep = null;
				if (list != null && list.size() > 0) {
					for (Map<String, String> blogArticle : list) {
						BlogArticle article = new BlogArticle();
						article.setId(blogArticle.get("id"));
						article.setTitle(blogArticle.get("title"));
						article.setPostdate(blogArticle.get("postdate"));
						articles.add(article);
					}
				}
				if (articles != null && articles.size() > 0) {
					rep = new BlogArticleRep();
					rep.setArticles(articles);
				}
				return JSON.toJSONString(rep);
			}
		});

		reqWrp.setOnPostExecuteListener(new onPostExecuteListener() {

			@Override
			public void onPostExecute(String result) {
				BlogArticleRep rep = JSON.parseObject(result, BlogArticleRep.class);
				List<BlogArticle> articles = rep.getArticle();
				if (articles != null && articles.size() > 0) {

					for (BlogArticle article : articles) {
						ContentValues cValues = new ContentValues();
						cValues.put("id", article.getId());
						cValues.put("title", article.getTitle());
						cValues.put("postdate", article.getPostdate());
						mSqLiteDatabase.insert("androids", null, cValues);
					}
				}

			}
		});

		requestConnection(reqWrp);
	}

	@Override
	public void onResponse(Object tag, String response) {
		super.onResponse(tag, response);
		if (!TextUtils.isEmpty(response)) {
			//			JSONObject object = JSON.parseObject(response);
			int iTag = (int) tag;
			switch (iTag) {
			case REQ_NOLMAL_TAG:
				BlogArticleRep articleRep0 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (articleRep0 != null) {
					mListArticleAll = articleRep0.getArticle();
					if (mAdapterArticleAll == null) {
						mAdapterArticleAll = new ArticleAdapter(MainActivity.this, mListArticleAll);
						mListViewAll.setAdapter(mAdapterArticleAll);
					}
				}
				break;
			case REQ_REFRESH_TAG:
				BlogArticleRep articleRep1 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (mListArticleAll == null) {
					mListArticleAll = articleRep1.getArticle();
				} else {
					mListArticleAll.clear();
					mListArticleAll.addAll(articleRep1.getArticle());
				}

				if (mAdapterArticleAll == null) {
					mAdapterArticleAll = new ArticleAdapter(MainActivity.this, mListArticleAll);
					mListViewAll.setAdapter(mAdapterArticleAll);
				} else {
					mAdapterArticleAll.notifyDataSetChanged();
				}
				mListViewAll.OnRefreshComplete();
				break;
			case REQ_LOADMORE_TAG:
				BlogArticleRep articleRep2 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				mListArticleAll.addAll(articleRep2.getArticle());
				mAdapterArticleAll.notifyDataSetChanged();
				mListViewAll.onLoadCompelte();
				break;
			case REQ_COLLECT_NOLMAL_TAG:
				BlogArticleRep articleRep3 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (articleRep3 != null) {
					mListArticleCollect = articleRep3.getArticle();
					if (mAdapterArticleCollect == null && mListArticleCollect != null) {
						mAdapterArticleCollect = new ArticleAdapter(MainActivity.this, mListArticleCollect);
						mListViewCollect.setAdapter(mAdapterArticleCollect);
					}
				}
				break;
			case REQ_COLLECT_REFRESH_TAG:
				BlogArticleRep articleRep4 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (mListArticleCollect == null) {
					mListArticleCollect = articleRep4.getArticle();
				} else {
					mListArticleCollect.clear();
					if (articleRep4.getArticle() != null) {
						mListArticleCollect.addAll(articleRep4.getArticle());
					}
				}

				if (mAdapterArticleCollect == null) {
					mAdapterArticleCollect = new ArticleAdapter(MainActivity.this, mListArticleCollect);
					mListViewCollect.setAdapter(mAdapterArticleCollect);
				} else {
					mAdapterArticleCollect.notifyDataSetChanged();
				}
				mListViewCollect.OnRefreshComplete();
				break;
			case REQ_COLLECT_LOADMORE_TAG:
				BlogArticleRep articleRep5 = JSON.parseObject(response.trim(), BlogArticleRep.class);
				if (articleRep5.getArticle() != null) {
					mListArticleCollect.addAll(articleRep5.getArticle());
				}
				mAdapterArticleCollect.notifyDataSetChanged();
				mListViewCollect.onLoadCompelte();
				break;
			case 6:
				System.out.println(response);
				Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		//mViewScrollLeft.setTranslationX(arg1 * mViewAll.getMeasuredWidth());
	}

	@Override
	public void onPageSelected(int position) {
		if (position == 1) {
			if (mAdapterArticleCollect == null) {
				reqeustCollect(REQ_COLLECT_NOLMAL_TAG);
			} else {
				mAdapterArticleCollect.notifyDataSetChanged();
			}
			mViewScrollLeft.setVisibility(View.INVISIBLE);
			mViewScrollRight.setVisibility(View.VISIBLE);
			mViewAll.setTextColor(mTabDefColor);
			mViewCollect.setTextColor(mTabSelectColor);
		} else {
			mViewScrollLeft.setVisibility(View.VISIBLE);
			mViewScrollRight.setVisibility(View.INVISIBLE);
			mViewAll.setTextColor(mTabSelectColor);
			mViewCollect.setTextColor(mTabDefColor);
		}
	}

	private void reqeustCollect(int tag) {

		if (!UserManager.getUserManager().isLogin()) {
			return;
		}

		RequestWrapper reqWrp = new RequestWrapper();
		if (tag != REQ_COLLECT_LOADMORE_TAG) {
			collect_index_count = 0;
		} else {
			collect_index_count++;
		}

		reqWrp.setTag(tag);
		reqWrp.setHeader("Accept", "application/json");
		reqWrp.setHeader("Content-Type", "text/html; charset=UTF-8");
		reqWrp.setUrl(AppConfig.getHttpUrl() + "getCollect/" + UserManager.getUserManager().getUserId() + "/" + collect_index_count);

		requestConnection(reqWrp);
	}

	@Override
	public void onUpdateSkin() {
		Resources resources = SkinManager.getInstance().getResources();
		
	}

}
