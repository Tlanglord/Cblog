package com.qiang.cblog.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiang.cblog.R;
import com.qiang.cblog.activity.WebviewActivity;
import com.qiang.cblog.entity.BlogArticle;

public class ArticleAdapter extends BaseAdapter {

	private Context mContext;
	private List<BlogArticle> mlist = new ArrayList<BlogArticle>();

	public ArticleAdapter() {
	}

	public ArticleAdapter(Context context, List<BlogArticle> list) {
		mContext = context;
		mlist = list;
	}

	@Override
	public int getCount() {
		if (mlist == null) {
			return 0;
		}
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.article_all_item, null);
			holder = new ViewHolder();
			holder.article_title = (TextView) convertView.findViewById(R.id.article_title);
			holder.article_layout = (LinearLayout) convertView.findViewById(R.id.article_item_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final BlogArticle article = mlist.get(position);

		holder.article_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, WebviewActivity.class);
				intent.putExtra("url", article.getContent());
				intent.putExtra("isData", true);
				intent.putExtra("title", article.getTitle());
				intent.putExtra("articleId", article.getId());
				mContext.startActivity(intent);

			}
		});

		if (mlist.get(position) != null) {
			holder.article_title.setText(article.getTitle());
		}
		return convertView;
	}

	class ViewHolder {
		public TextView article_title;
		public LinearLayout article_layout;
	}

}
