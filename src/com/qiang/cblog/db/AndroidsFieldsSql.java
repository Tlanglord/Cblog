package com.qiang.cblog.db;

import java.util.List;

public class AndroidsFieldsSql extends AbsFieldsSql {
	
	private String fid = "id integer primary key";
	private String fpostdate = "postdate varchar(100)";
	private String ftitle = "title varchar(100)";
	private String fcontent = "content longtext";

	@Override
	public List<String> getFields() {
		absF.add(fid);
		absF.add(fpostdate);
		absF.add(ftitle);
		absF.add(fcontent);
		return absF;
	}

	@Override
	public String getTableName() {
		return "androids";
	}
}
