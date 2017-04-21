package com.qiang.cblog.skin;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

public class SkinManager {
	private boolean isLight = true;
	private static SkinManager mInstance = new SkinManager();
	private Map<String,ISkinUpdate> updateMap = new HashMap<String,ISkinUpdate>();
    private Resources mResources;
	
	public static SkinManager getInstance() {
		return mInstance;
	}

	public void setSkinState(boolean lightOrDark) {
		isLight = lightOrDark;
	}

	public boolean getSkinState() {
		return isLight;
	}

	public void addUpdateObject(ISkinUpdate themeUpdate) {
		if (updateMap == null) {
			updateMap = new HashMap<String,ISkinUpdate>();
		}
		
	}
	
	public void delUpdateObject(ISkinUpdate themeUpdate){
		if (updateMap != null) {
			updateMap.remove("");
		}
	}
	
	public Resources getResources(){
		return mResources;
	}
	
	public void loaderSkin(){
		SkinLoader.getInstance().setSkinPkgPath("");
		mResources = SkinLoader.getInstance().loader();
	}

}
