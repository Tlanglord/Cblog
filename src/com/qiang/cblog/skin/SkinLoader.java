package com.qiang.cblog.skin;

import java.io.File;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class SkinLoader {
	private Context mContext;
	private String mPkgName;
	private String mPath;
	private String mSkinPkgPath;

	private static SkinLoader mLoader = new SkinLoader();

	public static SkinLoader getInstance() {
		return mLoader;
	}

	public void init(Context context) {
		mContext = context;
	}

	public void setSkinPkgPath(String pkgPath) {
		mSkinPkgPath = pkgPath;
	}

	public String getSkinPkgName() {
		return mPkgName;
	}

	public Resources loader() {
		File file = new File(mSkinPkgPath);
		if (file == null || !file.exists()) {
			return null;
		}

		PackageManager mPm = mContext.getPackageManager();
		PackageInfo mInfo = mPm.getPackageArchiveInfo(mSkinPkgPath, PackageManager.GET_ACTIVITIES);
		mPkgName = mInfo.packageName;

		AssetManager assetManager = null;
		try {
			assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(assetManager, mSkinPkgPath);

			Resources superRes = mContext.getResources();
			Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
			return skinResource;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
