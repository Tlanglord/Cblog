package com.qiang.cblog.crash.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.qiang.cblog.crash.model.CrashModel;
import com.qiang.cblog.crash.utils.DeviceUtil;
import com.qiang.cblog.crash.utils.NotifyUtil;
import com.qiang.cblog.requestbase.RequestWrapper;
import com.qiang.cblog.requestbase.RequestWrapper.ReqMethod;
import com.qiang.cblog.requestbase.VolleyRequestManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	private static CrashHandler instance;

	private static Thread.UncaughtExceptionHandler mDefaultHandler;

	private String mCrashDir;

	private Context mContext;
	private OnPostCrash mPostCrash;
	private CrashNotifyConfig mNotifyConfig = CrashNotifyConfig.CLOSE;

	public enum CrashNotifyConfig {
		OPEN, CLOSE
	}

	public CrashHandler setNofityConfig(CrashNotifyConfig config) {
		this.mNotifyConfig = config;
		return this;
	}

	public CrashNotifyConfig getNofityConfig() {
		return mNotifyConfig;
	}

	public CrashHandler setOnPostCrash(OnPostCrash onPostCrash) {
		this.mPostCrash = onPostCrash;
		return this;
	}

	@SuppressLint("SimpleDateFormat")
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public CrashHandler init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mCrashDir = context.getExternalFilesDir("crash").getAbsolutePath() + "/";
		//mCrashDir=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+context.getPackageName()+"/crash/";
		return this;
	}

	public synchronized static CrashHandler getInstance() {
		if (instance == null)
			instance = new CrashHandler();
		return instance;
	}

	public String getCrashDir() {
		return mCrashDir;
	}

	public CrashHandler setCrashDir(String crashDir) {
		mCrashDir = crashDir;
		return this;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理      
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			//退出程序      
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//收集设备参数信息       
		final String exMs = getExceptionMessage(ex);

		//使用Toast来显示异常信息      
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				//				Toast.makeText(mContext, "程序出现异常,即将退出", Toast.LENGTH_SHORT).show();
				if (mNotifyConfig == CrashNotifyConfig.OPEN) {
					NotifyUtil.notifyMessage(mContext, exMs);
				}
				Looper.loop();
			}
		}.start();
		//保存日志文件       
		handleExceptionWork(ex);
		return true;
	}

	private CrashModel buildCrashModel(Throwable ex, String date) {
		HashMap<String, String> devInfos = DeviceUtil.getDeviceInfos(mContext);
		StringBuffer sbInfo = new StringBuffer();
		for (Map.Entry<String, String> entry : devInfos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sbInfo.append(key + "=" + value + "\n");
		}

		StringBuffer sbEx = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sbEx.append(result);

		CrashModel crash = new CrashModel();
		crash.setContent(sbEx.toString());
		crash.setTime(date);
		crash.setDevice(sbInfo.toString());

		return crash;
	}
	
	private String getExceptionMessage(Throwable ex){
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer.toString();
	}

	private void postLocal(CrashModel model, String path, String fileName) {
		FileOutputStream fos = null;

		if (model == null) {
			return;
		}

		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				fos = new FileOutputStream(path + fileName);
				if (model.getDevice() != null) {
					fos.write(model.getDevice().getBytes());
				}

				if (model.getContent() != null) {
					fos.write(model.getContent().getBytes());
				}
				fos.flush();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void postRemote(CrashModel model) {
		if (mPostCrash != null && model != null) {

			ByteArrayOutputStream bos = null;
			byte[] bytes = null;
			ObjectOutputStream oos = null;
			try {
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(bos);
				oos.writeObject(model);
				oos.flush();
				bytes = bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (oos != null) {
						oos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (bytes != null) {
				mPostCrash.postCrash(JSON.toJSONString(model).getBytes());
				mPostCrash.postCrash(model);
			}
			//postReport(JSON.toJSONString(model).getBytes());
		}
	}

	private void handleExceptionWork(Throwable ex) {
		long timestamp = System.currentTimeMillis();
		String date = formatter.format(new Date());
		String path = mCrashDir;//mContext.getCacheDir().getAbsolutePath() + "/crash/";
		String fileName = "crash-" + date + "-" + timestamp + ".log";
		CrashModel model = buildCrashModel(ex, date);
		postLocal(model, path, fileName);
		postRemote(model);
		//		try {
		//			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		//
		//				//				String esdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//				String path = mContext.getCacheDir().getAbsolutePath() + "/crash/";
		//
		//				File dir = new File(path);
		//				if (!dir.exists()) {
		//					dir.mkdirs();
		//				}
		//				FileOutputStream fos = new FileOutputStream(path + fileName);
		//				fos.write(sbEx.toString().getBytes());
		//				//发送给开发人员  
		//				CrashModel crash = new CrashModel();
		//				crash.setContent(sbEx.toString());
		//				crash.setTime(time);
		//				crash.setDevice(sbInfo.toString());
		//				postReport(JSON.toJSONString(crash).getBytes());
		//				//Log.e(TAG, "crash : " + JSON.toJSONString(crash).getBytes());
		//				//sendCrashLog2PM(path + fileName);
		//				fos.close();
		//			}
		//			return fileName;
		//		} catch (Exception e) {
		//			Log.e(TAG, "an error occured while writing file...", e);
		//		}
		//		return null;
	}
	//	private void sendCrashLog2PM(String fileName) {
	//		if (!new File(fileName).exists()) {
	//			Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
	//			return;
	//		}
	//		FileInputStream fis = null;
	//		BufferedReader reader = null;
	//		String s = null;
	//		try {
	//			fis = new FileInputStream(fileName);
	//			reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
	//			while (true) {
	//				s = reader.readLine();
	//				if (s == null)
	//					break;
	//				//由于目前尚未确定以何种方式发送，所以先打出log日志。  
	//				Log.i("info", s.toString());
	//			}
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} finally { // 关闭流  
	//			try {
	//				reader.close();
	//				fis.close();
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	private void postReport(byte[] bytes) {
		RequestWrapper reqWrp = new RequestWrapper();
		reqWrp.setReqMethod(ReqMethod.POST);
		reqWrp.setUrl("http://www.wpdqq.com/site/api/postReport/");
		reqWrp.setReqBody(bytes);
		VolleyRequestManager.executeRequest(reqWrp.getRequest());
	}

	public interface OnPostCrash {
		public void postCrash(byte[] bytes);
		
		public void postCrash(CrashModel model);
	}
}
