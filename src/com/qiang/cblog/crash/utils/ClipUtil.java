package com.qiang.cblog.crash.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipUtil {

	public static void copy(Context context, String text) {
		ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("data", text);
		//		clip.addItem(new Item(text));
		clipboardManager.setPrimaryClip(clip);
	}

}
