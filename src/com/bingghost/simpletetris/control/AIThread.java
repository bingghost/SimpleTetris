package com.bingghost.simpletetris.control;

import com.bingghost.simpletetris.common.LogUtil;
import com.bingghost.simpletetris.config.AppConfig;
import com.bingghost.simpletetris.game.TetrisAI;
import com.bingghost.simpletetris.game.TetrisGame;

import android.util.Log;

public class AIThread implements Runnable {
	
	public static final int AI_STATUS_PAUSE = 0;
	public static final int AI_STATUS_START = 1;
	public static final int AI_STATUS_STOP = 2;
	
	private TetrisAI mTetrisAI     = null;
	private TetrisGame mTetrisGame = null;
	
	public int mSleepTime = 100;                // AI Speed
	private int mAIStatus = AI_STATUS_START;    // AI Switch
	

	public AIThread(TetrisGame tetrisGame) {
		mTetrisGame = tetrisGame;
		mTetrisAI = new TetrisAI(tetrisGame,AppConfig.getAiLevel());
		
		mSleepTime = AppConfig.getAiSpeed();
		
		LogUtil.d("ai level:" + AppConfig.getAiLevel());
	}
	
	@Override
	public void run() {
		while (true) {
			sleep(mSleepTime);
			
			if (mAIStatus == AI_STATUS_STOP) {
				break;
			}
			
			AIControl();
		}
	}
	
	public void stopAI(){
		mAIStatus = AI_STATUS_STOP;
	}
	
	public void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void AIControl() {
		mTetrisAI.startAI();
		
		if (mTetrisAI.isFind()) {
			int bestX = mTetrisAI.getBestX();
			int bestRotate = mTetrisAI.getBestRotate();
			
			autoControl(bestX,bestRotate);
		}
	}
	
	// 问题函数 如果坐标算错了 会导致死循环
	public void autoControl(int destX,int destRotate) {
		while (true) {
			if (destRotate == mTetrisGame.getCurRotate()) {
				break;
			}
			
			mTetrisGame.moveUp();
		}
		
		if (destX >= mTetrisGame.getCurX()) {
			
			while (true) {
				if (destX == mTetrisGame.getCurX()) {
					break;
				}
				
				mTetrisGame.moveRight();
			}
		} else {
			while (true) {
				if (destX == mTetrisGame.getCurX()) {
					break;
				}
				
				mTetrisGame.moveLeft();
			}
		}
		
		mTetrisGame.moveFastDown();
		mTetrisGame.moveDown();
	}
}
