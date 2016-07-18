package com.bingghost.simpletetris.control;

import com.bingghost.simpletetris.activity.GameActivity;
import com.bingghost.simpletetris.game.TetrisGame;

import android.os.Message;

public class GameThread extends Thread {
	public static final int GAME_START = 1;
	public static final int GAME_PASUE = 3;
	public static final int GAME_EXIT = 4;

	private GameActivity mGameActivity = null;
	private int mTimeCount = 1;
	private int mGameState = 0;
	public int mSleepTime = 200;

	private Object mControl = new Object();

	public GameThread(GameActivity gameActivity) {
		mGameActivity = gameActivity;
		mGameState = GAME_START;
	}

	@Override
	public void run() {
		while (true) {
			
			checkPause();
			sleep(mSleepTime);
			
			if (isGameExit()) {
				break;
			}

			if (isGamePause()) {
				continue;
			}

			if (!gameDown()) {
				mGameState = GAME_EXIT;
				
				// 发送重新绘制消息
				Message message = new Message();
				message.what = GameActivity.MSG_GAME_STOP;
				mGameActivity.m_Handler.sendMessage(message);
			}
		}
	}

	public void recover() {
		synchronized (mControl) {
			mGameState = GAME_START;
			mControl.notifyAll();
		}
	}

	public void pause() {
		synchronized (mControl) {
			mGameState = GAME_PASUE;
		}
		
	}

	public void exitGame() {
		if (!isGameExit()) {
			mGameState = GAME_EXIT;
		}
	}

	private void checkPause() {
		synchronized (mControl) {
			if (isGamePause()) {
				try {
					mControl.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isGamePause() {

		if (mGameState == GAME_PASUE) {
			return true;
		}

		return false;
	}

	public boolean isGameExit() {
		if (mGameState == GameThread.GAME_EXIT) {
			return true;
		}

		return false;
	}

	public boolean gameDown() {
		TetrisGame theGame = mGameActivity.mTetrisGame;

		int timer = mTimeCount * mSleepTime;
		int time_to_down = theGame.getGameLevel() * mSleepTime;
		if (timer >= time_to_down) {
			mTimeCount = 1;

			return theGame.moveDown();
		}

		mTimeCount++;
		return true;

	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
