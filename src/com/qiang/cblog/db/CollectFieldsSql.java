package com.qiang.cblog.db;

import java.util.List;

public class CollectFieldsSql extends AbsFieldsSql {

	private String fuserid = "userid char(100)";
	private String fid = "id int(11)";

	@Override
	public List<String> getFields() {
		absF.add(fid);
		absF.add(fuserid);
		return absF;
	}

	@Override
	public String getTableName() {
		return "collect";
	}

}
