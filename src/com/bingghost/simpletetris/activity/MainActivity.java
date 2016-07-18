package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.common.LogUtil;
import com.bingghost.simpletetris.config.AppConfig;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {
	
	private static final int MSG_START_MENU = 1;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_START_MENU:
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MenuActivity.class);
				startActivity(intent);
				finish();
				break;

			default:
				LogUtil.e("MainActivity:mHandler:default");
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// º”‘ÿ”Œœ∑≈‰÷√
		AppConfig.readConfig(this);
		
		// delay send message
		mHandler.sendEmptyMessageDelayed(MSG_START_MENU, 2000);
	}
}
