package com.bingghost.simpletetris.common;

import android.util.Log;

/*
 * 日志工具类 
 */
public class LogUtil {
	public static final int VERBOSE = 1;   // 开发使用标记
	public static final int DEBUG   = 2;
	public static final int INFO    = 3;
	public static final int WARN    = 4;
	public static final int ERROR   = 5;
	public static final int NOTHING = 6;   // 发行版本使用
	
	public static final int LEVEL = VERBOSE;
	
	public static final String TAG = "__BING__";
	
	@SuppressWarnings("unused")
	public static void v(String tag,String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}
	
	@SuppressWarnings("unused")
	public static void d(String tag,String msg) {
		if (LEVEL <= VERBOSE) {
			Log.d(tag, msg);
		}
	}
	
	@SuppressWarnings("unused")
	public static void i(String tag,String msg) {
		if (LEVEL <= VERBOSE) {
			Log.i(tag, msg);
		}
	}
	
	@SuppressWarnings("unused")
	public static void w(String tag,String msg) {
		if (LEVEL <= VERBOSE) {
			Log.w(tag, msg);
		}
	}
	
	@SuppressWarnings("unused")
	public static void e(String tag,String msg) {
		if (LEVEL <= VERBOSE) {
			Log.e(tag, msg);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//                            更精简版
	////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public static void v(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(TAG, msg);
		}
	}
	
	@SuppressWarnings("unused")
	public static void d(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.d(TAG, msg);
		}
	}
	@SuppressWarnings("unused")
	public static void i(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.i(TAG, msg);
		}
	}
	@SuppressWarnings("unused")
	public static void w(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.w(TAG, msg);
		}
	}
	@SuppressWarnings("unused")
	public static void e(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.e(TAG, msg);
		}
	}
	
}
