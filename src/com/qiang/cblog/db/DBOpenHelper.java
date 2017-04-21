package com.qiang.cblog.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private List<String> mTableSqls;

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	public DBOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	public DBOpenHelper(Context context, int version) {
		this(context, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (mTableSqls != null && mTableSqls.size() > 0) {
			for (String tSql : mTableSqls) {
				db.execSQL(tSql);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//		if (oldVersion < newVersion) {
		//			db.execSQL("drop *");
		//			onCreate(db);
		//		}

	}

	public void createTable(String table) {
		if (mTableSqls == null) {
			mTableSqls = new ArrayList<String>();
		}
		mTableSqls.add(table);
	}

	public void createTables(List<String> tables) {
		mTableSqls = tables;
	}

}
