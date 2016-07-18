package com.bingghost.simpletetris.control;

import com.bingghost.simpletetris.activity.GameActivity;
import android.os.Message;

public class GameUIThread extends Thread {
	public static final int GAME_START = 1;
	public static final int GAME_EXIT = 2;

	private GameActivity mGameActivity = null;
	private int mGameState = 0;
	public int mSleepTime = 200;


	public GameUIThread(GameActivity gameActivity) {
		mGameActivity = gameActivity;
		mGameState = GAME_START;
	}

	@Override
	public void run() {
		while (true) {

			handleMsg();

			if (isGameExit()) {
				break;
			}
		}
	}

	public void exitGame() {
		mGameState = GAME_EXIT;
	}

	private void handleMsg() {
		sleep(mSleepTime);

		// 发送重新绘制消息
		Message message = new Message();
		setMessage(message);
		mGameActivity.m_Handler.sendMessage(message);
	}

	public boolean isGameExit() {
		if (mGameState == GameThread.GAME_EXIT) {
			return true;
		}

		return false;
	}

	public void setMessage(Message message) {
		message.what = GameActivity.MSG_VIEW_REFRESH;
	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
