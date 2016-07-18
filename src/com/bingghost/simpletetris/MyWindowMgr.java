package com.bingghost.simpletetris;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class MyWindowMgr {
	
	
	public static void SetFullScreen(Activity ac){
		// set window no title and full screen
		ac.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// set full screen
		int flag_full_screen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		int mask_full_screen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		ac.getWindow().setFlags(flag_full_screen, mask_full_screen);
	}
	
	
}
