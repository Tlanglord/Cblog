package com.qiang.cblog.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;

public class DBHelper {

	private final static String mDBName = "cblog.db";
	private static int mVersion = 0;
	private static Context mContext;
	private static DBOpenHelper mDbOpenHelper;

	public static String createTableSql(String tname, List<String> tfields) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("create table " + tname);
		sbSql.append("(");
		if (tfields != null && tfields.size() > 0) {
			for (int i = 0; i < tfields.size(); i++) {
				sbSql.append(tfields.get(i));
				if (i != (tfields.size() - 1)) {
					sbSql.append(",");
				}
			}
		}
		sbSql.append(")");
		sbSql.append(";");
		return sbSql.toString();
	}

	public static void init(Context context, int version) {
		mContext = context;
		mVersion = version;
		mDbOpenHelper = getDbOpenHelper();
		mDbOpenHelper.createTables(getTableList());
	}

	private static List<String> getTableList() {
		List<String> sqlList = new ArrayList<String>();

		AbsFieldsSql afSql = new AndroidsFieldsSql();
		String aSql = DBHelper.createTableSql(afSql.getTableName(), afSql.getFields());
		sqlList.add(aSql);

		AbsFieldsSql cfSql = new CollectFieldsSql();
		String cSql = DBHelper.createTableSql(cfSql.getTableName(), cfSql.getFields());
		sqlList.add(cSql);

		return sqlList;
	}

	public static synchronized DBOpenHelper getDbOpenHelper() {
		if (mDbOpenHelper == null) {
			mDbOpenHelper = new DBOpenHelper(mContext, mDBName, mVersion);
		}
		return mDbOpenHelper;
	}

	public static List<Map<String, String>> convertCursorToList(Cursor cursor) {
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		String[] keys = cursor.getColumnNames();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < keys.length; i++) {
					map.put(keys[i], cursor.getString(i));
				}
				listMap.add(map);
			}
		}

		return listMap;
	}
}
