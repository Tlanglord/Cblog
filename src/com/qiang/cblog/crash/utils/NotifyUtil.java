package com.qiang.cblog.crash.utils;

import com.qiang.cblog.R;
import com.qiang.cblog.activity.CrashTextActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

public class NotifyUtil  {
	
	public final static String COPY_ACTION= "com.qiang.copy_action";
	
//	、、extends Notification
	public static int id=0;
	
	@SuppressLint("NewApi")
	public static void notifyMessage(Context context, String text){
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		//.setContentText(text).
		Notification notification = new Notification.Builder(context).setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher).setContentTitle("crash").setTicker("crash").build();
		RemoteViews rv	=new RemoteViews(context.getPackageName(), R.layout.notify_msg);
		rv.setTextViewText(R.id.notify_text, text);
//		Intent intent = new Intent(COPY_ACTION);
		Intent intent = new Intent(context, CrashTextActivity.class);
		intent.putExtra("crashText", text);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,id++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		rv.setOnClickFillInIntent(R.id.noify_copy, intent);
		rv.setOnClickPendingIntent(R.id.notify_text, pendingIntent);
//		rv.setPendingIntentTemplate(R.id.notify_text, pendingIntent);
		notification.contentView = rv;
		notification.when = System.currentTimeMillis();
//		notification.setLatestEventInfo(context, "","", pendingIntent);
		notificationManager.notify(id, notification);
	}
	

}
