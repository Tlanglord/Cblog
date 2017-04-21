package com.qiang.cblog.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiang.cblog.R;

public class SuperListView extends ListView implements OnScrollListener {
	private boolean mIsLoading = false;
	private LinearLayout mHeader;
	private LinearLayout mFooter;
	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadListener;
	private int mHeaderHeight;
	private int mFooterHeight;

	private ProgressBar mHeaderPrgbar;
	private ProgressBar mFooterPrgbar;

	private TextView mHeaderText;
	private TextView mFooterText;

	public SuperListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public SuperListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener loadListener) {
		this.mLoadListener = loadListener;
	}

	private void initView(Context context) {
		mHeader = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_header, null);
		mFooter = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.load_footer, null);
		mHeaderPrgbar = (ProgressBar) mHeader.findViewById(R.id.header_prgbar);
		mHeaderText = (TextView) mHeader.findViewById(R.id.header_refreshtext);
		mFooterPrgbar = (ProgressBar) mFooter.findViewById(R.id.footer_prgbar);
		mFooterText = (TextView) mFooter.findViewById(R.id.footer_loadtext);
		mHeader.measure(0, 0);
		mHeaderHeight = mHeader.getMeasuredHeight();
		mHeaderRF = mHeaderHeight;
		mHeader.setPadding(0, -mHeaderHeight, 0, 0);
		addHeaderView(mHeader, null, false);
		mFooter.measure(0, 0);
		mFooterHeight = mFooter.getMeasuredHeight();
		mFooter.setPadding(0, -mFooterHeight, 0, 0);
		addFooterView(mFooter);
		setOnScrollListener(this);
	}

	//	private void measureView(View child) {
	//		ViewGroup.LayoutParams p = child.getLayoutParams();
	//		if (p == null) {
	//			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	//		}
	//		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
	//		int lpHeight = p.height;
	//		int childHeightSpec;
	//		if (lpHeight > 0) {
	//			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
	//		} else {
	//			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	//		}
	//
	//		child.measure(childWidthSpec, childHeightSpec);
	//	}

	private final int BACK_TO_REFRESH = 1;
	private final int RELEASE_TO_REFRESH = 2;
	private final int REFESHING = 3;
	private final int DONE = 4;

	private int mCurrentState = DONE;
	private int mStartY = 0;
	private int mHeaderRF;
	private boolean isRefresh = true;

	public void setRefresh(boolean isRefresh) {
		this.isRefresh = isRefresh;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int mLastY = 0;
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (mCurrentState == RELEASE_TO_REFRESH && mRefreshListener != null && mCurrentState != REFESHING) {
				mHeader.setPadding(0, mHeaderHeight, 0, 0);
				mRefreshListener.onRefresh();
				mCurrentState = REFESHING;
			} else if (mCurrentState == REFESHING) {
				mHeader.setPadding(0, mHeaderHeight, 0, 0);
			} else if (mCurrentState == BACK_TO_REFRESH) {
				mHeader.setPadding(0, -mHeaderHeight, 0, 0);
			} else {
				mCurrentState = DONE;
			}
			changeHeaderState();
			break;
		case MotionEvent.ACTION_MOVE:
			mLastY = (int) ev.getY();
			int h = (mLastY - mStartY);
			if (getFirstVisiblePosition() == 0 && h > 0 && isRefresh) {
				int rh = (h - mHeaderRF);
				if (rh > 0) {
					if (mCurrentState != REFESHING) {
						mCurrentState = RELEASE_TO_REFRESH;
					}
					mHeader.setPadding(0, rh, 0, 0);
				} else {
					if (mCurrentState != REFESHING) {
						mCurrentState = BACK_TO_REFRESH;
					}
					mHeader.setPadding(0, -rh, 0, 0);
				}
			}
			changeHeaderState();
			break;
		case MotionEvent.ACTION_CANCEL:
			mCurrentState = DONE;
			changeHeaderState();
			break;
		}
		Log.v("Listview", "onTouchEvent");
		return super.onTouchEvent(ev);
	}

	private void changeHeaderState() {
		switch (mCurrentState) {
		case RELEASE_TO_REFRESH:
			mHeader.setVisibility(View.VISIBLE);
			break;
		case REFESHING:
			//			mHeader
			//			mHeader.setPadding(0, mHeaderRF, 0, 0);
			mHeader.setVisibility(View.VISIBLE);
			break;
		case BACK_TO_REFRESH:
			mHeader.setVisibility(View.GONE);
			break;
		case DONE:
			mHeader.setVisibility(View.GONE);
			break;
		}
	}

	public void OnRefreshComplete() {
		mCurrentState = DONE;
		mHeader.setPadding(0, -mHeaderHeight, 0, 0);
		changeHeaderState();
	}

	public void onLoadCompelte() {
		mIsLoading = false;
		mFooter.setPadding(0, -mFooterHeight, 0, 0);
	}

	public interface OnRefreshListener {
		void onRefresh();
	}

	public interface OnLoadMoreListener {
		void onLoadMore();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if ((getLastVisiblePosition() == (getCount() - 1)) && !mIsLoading && mLoadListener != null) {
				mFooter.setPadding(0, 0, 0, 0);
				mIsLoading = true;
				mLoadListener.onLoadMore();
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

}
