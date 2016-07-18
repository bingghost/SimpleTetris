package com.bingghost.simpletetris.ui;

import com.bingghost.simpletetris.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ButtonManager {
	
	public static final int BTN_NUMS = 5;      // 按钮个数
	
	public Resources mResources = null;        // 资源对象
	
	public int mBtnLen = 0;        // 按钮尺寸
	public int mScreenWidth = 0;   // 屏幕宽度
	public int mScreenHeight = 0;  // 屏幕高度
	
	public int mBoxLen = 0;
	public int mBtnY = 0;          // Y 坐标
	
	public int mBtnLeftX   = 0;
	public int mBtnRightX  = 0;
	public int mBtnDownX   = 0;
	public int mBtnRotateX = 0;
	public int mBtnFastX   = 0;
	
	public boolean mBtnLeftStatus = true;
	public boolean mBtnDownStatus = true;
	public boolean mBtnRightStatus = true;
	public boolean mBtnRotateStatus = true;
	public boolean mBtnFastStatus = true;
	
	public boolean mBtnAIStartStatus = true;
	public boolean mBtnAIStopStatus = true;
	public boolean mBtnAIDownStatus = true;
	public boolean mBtnPauseStatus = true;
	public boolean mIsPause = false;
	
	public ImageButton mBtnLeft   = null;  // 左按钮1
	public ImageButton mBtnLeft_p = null;  // 左按钮2
	
	public ImageButton mBtnDown   = null;  // 向下按钮1
	public ImageButton mBtnDown_p = null;  // 向下按钮2
	
	public ImageButton mBtnRight   = null;   // 向右按钮1
	public ImageButton mBtnRight_p = null;   // 向右按钮2
	
	public ImageButton mBtnRotate   = null;  // 旋转按钮1
	public ImageButton mBtnRotate_p = null;  // 旋转按钮2
	
	public ImageButton mBtnFast   = null;    // 快速下落按钮1
	public ImageButton mBtnFast_p = null;    // 快速下落按钮2
	
	public ImageButton mBtnAIStart      = null;     // 启动AI
	public ImageButton mBtnAIStart_p    = null;     // 启动AI按钮按下
	
	public ImageButton mBtnAIStop     = null;     // 暂停按钮
	public ImageButton mBtnAIStop_p   = null;     // 暂停按钮按下
	
	public ImageButton mBtnAIDown   = null;     // 恢复按钮
	public ImageButton mBtnAIDown_p = null;     // 恢复按钮按下
	
	public ImageButton mBtnPause      = null;     // 暂停按钮
	public ImageButton mBtnPause_p    = null;     // 暂停按钮按下
	
	public ImageButton mBtnRecover      = null;     // 暂停按钮
	public ImageButton mBtnRecover_p    = null;     // 暂停按钮按下
	
	
	
	Context mContext = null;
	GameResources mGameResources = null;
	
	int mInfoWidth = 0;
	int mInfoHeight = 0;
	int mBtnInfoWidth = 0;
	int mBtnInfoHeight = 0;
	int mInfoStartY = 0;
	
	
	public ButtonManager(Context context,int screenWidth,int screenheight,int boxLen,GameResources rs) { 
		mContext = context;
		mResources = context.getResources();
		mGameResources = rs;
		
		mBoxLen = boxLen;
		mScreenWidth = screenWidth;
		mScreenHeight = screenheight;
		
		mBtnLen = mScreenWidth / 5;
		mInfoWidth = mScreenWidth - (mGameResources.mGameWidth + 2) * mBoxLen;
		mInfoHeight = (mGameResources.mGameHeight - 15)*mBoxLen;
		mInfoStartY = 16* mBoxLen;
		
		mBtnInfoWidth = mInfoWidth - 2*mBoxLen;
		mBtnInfoHeight = mInfoHeight / 4 - 2*mBoxLen;
		
		// calcBtnLen();
		calcBtnCoordinate();
	}
	
	public void calcBtnCoordinate() {
		// button的Y坐标
		mBtnY = mBoxLen * (mGameResources.mGameHeight + 3);
		
		mBtnLeftX  = 0;
		mBtnLeft   = new ImageButton(mContext,R.drawable.btn_blue_left1,mBtnLeftX,mBtnY,mBtnLen,mBtnLen);
		mBtnLeft_p = new ImageButton(mContext,R.drawable.btn_blue_left2,mBtnLeftX,mBtnY,mBtnLen,mBtnLen);
		
		mBtnDownX  = mBtnLeftX + mBtnLeft.mWidth;
		mBtnDown   = new ImageButton(mContext,R.drawable.btn_blue_down1,mBtnDownX,mBtnY,mBtnLen,mBtnLen);
		mBtnDown_p = new ImageButton(mContext,R.drawable.btn_blue_down2,mBtnDownX,mBtnY,mBtnLen,mBtnLen);
		
		mBtnRightX  = mBtnDownX + mBtnLeft.mWidth;
		mBtnRight   = new ImageButton(mContext,R.drawable.btn_blue_right1,mBtnRightX,mBtnY,mBtnLen,mBtnLen);
		mBtnRight_p = new ImageButton(mContext,R.drawable.btn_blue_right2,mBtnRightX,mBtnY,mBtnLen,mBtnLen);
		

		mBtnRotateX  = mBtnRightX + mBtnLeft.mWidth;
		mBtnRotate   = new ImageButton(mContext,R.drawable.btn_blue_rotate1,mBtnRotateX,mBtnY,mBtnLen,mBtnLen);
		mBtnRotate_p = new ImageButton(mContext,R.drawable.btn_blue_rotate2,mBtnRotateX,mBtnY,mBtnLen,mBtnLen);
		
		mBtnFastX  = mBtnRotateX + mBtnLeft.mWidth;
		mBtnFast   = new ImageButton(mContext,R.drawable.btn_blue_fastdown1,mBtnFastX,mBtnY,mBtnLen,mBtnLen);
		mBtnFast_p = new ImageButton(mContext,R.drawable.btn_blue_fastdown2,mBtnFastX,mBtnY,mBtnLen,mBtnLen);
		
		calcBtnInfoCoordinate();
	}
	
	public void calcBtnInfoCoordinate(){
		int btnX = (mGameResources.mGameWidth + 3) * mBoxLen;
		
		int btnAIY = mInfoStartY + mBoxLen;
		mBtnAIStart   = new ImageButton(mContext, R.drawable.btn_start_ai, btnX, btnAIY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnAIStart_p = new ImageButton(mContext, R.drawable.btn_start_ai_p, btnX, btnAIY, mBtnInfoWidth, mBtnInfoHeight);
		
		int btnAIStopY = btnAIY + mBtnInfoHeight + mBoxLen;
		mBtnAIStop   = new ImageButton(mContext, R.drawable.btn_stop_ai, btnX, btnAIStopY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnAIStop_p = new ImageButton(mContext, R.drawable.btn_stop_ai_p, btnX, btnAIStopY, mBtnInfoWidth, mBtnInfoHeight);
		
		int btnAIDownY = btnAIStopY + mBtnInfoHeight + mBoxLen;
		mBtnAIDown   = new ImageButton(mContext, R.drawable.btn_ai_down, btnX, btnAIDownY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnAIDown_p = new ImageButton(mContext, R.drawable.btn_ai_down_p, btnX, btnAIDownY, mBtnInfoWidth, mBtnInfoHeight);
		
		int btnPauseY = btnAIDownY + mBtnInfoHeight + mBoxLen;
		mBtnPause   = new ImageButton(mContext, R.drawable.btn_pause, btnX, btnPauseY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnPause_p = new ImageButton(mContext, R.drawable.btn_pause_p, btnX, btnPauseY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnRecover   = new ImageButton(mContext, R.drawable.btn_recover, btnX, btnPauseY, mBtnInfoWidth, mBtnInfoHeight);
		mBtnRecover_p = new ImageButton(mContext, R.drawable.btn_recover_p, btnX, btnPauseY, mBtnInfoWidth, mBtnInfoHeight);

	}
	
	public void drawButtons(Canvas canvas,Paint paint) { 
		
		if (mBtnLeftStatus) {
			mBtnLeft.DrawImageButton(canvas, paint);
		} else {
			mBtnLeft_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnDownStatus) {
			mBtnDown.DrawImageButton(canvas, paint);
		} else {
			mBtnDown_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnRightStatus) {
			mBtnRight.DrawImageButton(canvas, paint);
		} else {
			mBtnRight_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnRotateStatus) {
			mBtnRotate.DrawImageButton(canvas, paint);
		} else {
			mBtnRotate_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnFastStatus) {
			mBtnFast.DrawImageButton(canvas, paint);
		} else {
			mBtnFast_p.DrawImageButton(canvas, paint);
		}
		
		mBtnLeftStatus = true;
		mBtnDownStatus = true;
		mBtnRightStatus = true;
		mBtnRotateStatus = true;
		mBtnFastStatus = true;
	}
	
	public void drawInfoButtons(Canvas canvas,Paint paint) {
		if (mBtnAIStartStatus) {
			mBtnAIStart.DrawImageButton(canvas, paint);
		} else {
			mBtnAIStart_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnAIStopStatus) {
			mBtnAIStop.DrawImageButton(canvas, paint);
		} else {
			mBtnAIStop_p.DrawImageButton(canvas, paint);
		}
		
		if (mBtnAIDownStatus) {
			mBtnAIDown.DrawImageButton(canvas, paint);
		} else {
			mBtnAIDown_p.DrawImageButton(canvas, paint);
		}
		
		
		if (!mIsPause) {
			if (mBtnPauseStatus) {
				mBtnPause.DrawImageButton(canvas, paint);
			} else {
				mBtnPause_p.DrawImageButton(canvas, paint);
			}
		} else {
			if (mBtnPauseStatus) {
				mBtnRecover.DrawImageButton(canvas, paint);
			} else {
				mBtnRecover_p.DrawImageButton(canvas, paint);
			}
		}
		

		
		
		mBtnAIStartStatus = true;
		mBtnAIStopStatus = true;
		mBtnAIDownStatus = true;
		mBtnPauseStatus = true;
	}
	
	public void calcBtnLen() {
		mBtnLen = mScreenWidth / BTN_NUMS;
	}
}
