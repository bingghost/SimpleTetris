package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.MyWindowMgr;
import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.common.LogUtil;
import com.bingghost.simpletetris.config.AppConfig;
import com.bingghost.simpletetris.control.AIThread;
import com.bingghost.simpletetris.control.GameThread;
import com.bingghost.simpletetris.control.GameUIThread;
import com.bingghost.simpletetris.control.MusicPlayer;
import com.bingghost.simpletetris.game.TetrisAI;
import com.bingghost.simpletetris.game.TetrisGame;
import com.bingghost.simpletetris.ui.GameView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameActivity extends Activity implements OnTouchListener, OnGestureListener {
	public static final int MSG_VIEW_REFRESH = 1; // 游戏界面刷新
	public static final int MSG_GAME_STOP = 2; // 游戏结束

	public GestureDetector mGestureDetector = null;
	public GameView mGameView = null;
	public TetrisGame mTetrisGame = null;
	public GameThread mGameThread = null;
	public GameUIThread mGameUIThread = null;
	private TetrisAI mTetrisAI = null;
	private boolean isAiStart = false;
	private boolean mIsGamePause = false;

	AIThread mAIThread = null;

	@SuppressWarnings("deprecation")
	public GameActivity() {
		mGestureDetector = new GestureDetector((OnGestureListener) this);
	}

	@SuppressLint("HandlerLeak")
	public Handler m_Handler = new Handler() // 定义Handler变量 实现更新View视图
	{
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GameActivity.MSG_VIEW_REFRESH:
				mGameView.invalidate();
				break;

			case GameActivity.MSG_GAME_STOP:
				dialogGameOver();
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void gamePause() {
		mIsGamePause = true;

		LogUtil.d("gamePause");

		if (mGameThread != null) {
			mGameThread.pause();
		}
	}

	public void gameRecover() {
		mIsGamePause = false;

		LogUtil.d("gameRecover");

		if (mGameThread != null) {
			mGameThread.recover();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyWindowMgr.SetFullScreen(this);

		super.onCreate(savedInstanceState);
		mGameView = new GameView(this);
		setContentView(mGameView);
		mTetrisGame = mGameView.mTetrisGame;

		mTetrisAI = new TetrisAI(mTetrisGame, AppConfig.getAiLevel());
		Init();

		mGameUIThread = new GameUIThread(this);
		if (mGameUIThread != null) {
			mGameUIThread.start();
		}

		mGameThread = new GameThread(this);
		if (mGameThread != null) {
			mGameThread.start();
		}
	}

	public void Init() {
		InitEvent();
		initMusic();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void InitEvent() {
		mGameView.setOnTouchListener((OnTouchListener) this); // 设置可以响应触摸事件
		mGameView.setClickable(true);
		mGestureDetector.setIsLongpressEnabled(true); // 可以响应长按
	}
	
	public void initMusic() {
		MusicPlayer.initMusicPlay(this); // 初始化播放器及相关操作
		MusicPlayer.initSound();
		
		if (AppConfig.isSoundSwitch()) {
			MusicPlayer.BgMediaplayer();
		}
		
		if (AppConfig.isMusicSwitch()) {
			MusicPlayer.startMusic();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		int x = (int) e.getX(); // 获取按下的坐标位置
		int y = (int) e.getY();

		// 判断按钮是否按下
		boolean isLeftClick = mGameView.mBtnMgr.mBtnLeft.IsClick(x, y);
		if (isLeftClick) {
			mGameView.mBtnMgr.mBtnLeftStatus = false;

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveLeft();

				MusicPlayer.playSound(R.raw.action, 0);
			}

		}

		boolean isDownClick = mGameView.mBtnMgr.mBtnDown.IsClick(x, y);
		if (isDownClick) {
			mGameView.mBtnMgr.mBtnDownStatus = false;

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveDown();

				MusicPlayer.playSound(R.raw.down, 0);
			}
		}

		boolean isRightClick = mGameView.mBtnMgr.mBtnRight.IsClick(x, y);
		if (isRightClick) {
			mGameView.mBtnMgr.mBtnRightStatus = false;

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveRight();

				MusicPlayer.playSound(R.raw.action, 0);
			}
		}

		boolean isRotateClick = mGameView.mBtnMgr.mBtnRotate.IsClick(x, y);
		if (isRotateClick) {
			mGameView.mBtnMgr.mBtnRotateStatus = false;

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveUp();

				MusicPlayer.playSound(R.raw.rotation, 0);
			}

		}

		boolean isFastClick = mGameView.mBtnMgr.mBtnFast.IsClick(x, y);
		if (isFastClick) {
			mGameView.mBtnMgr.mBtnFastStatus = false;

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveFastDown();

				MusicPlayer.playSound(R.raw.fastdown, 0);
			}
		}

		boolean isAiStartClick = mGameView.mBtnMgr.mBtnAIStart.IsClick(x, y);
		if (isAiStartClick) {
			mGameView.mBtnMgr.mBtnAIStartStatus = false;

			// 启动智能
			startAI();
		}

		boolean isAIStopClick = mGameView.mBtnMgr.mBtnAIStop.IsClick(x, y);
		if (isAIStopClick) {
			mGameView.mBtnMgr.mBtnAIStopStatus = false;

			// 停止智能
			stopAI();
		}

		boolean isAIDownClick = mGameView.mBtnMgr.mBtnAIDown.IsClick(x, y);
		if (isAIDownClick) {
			mGameView.mBtnMgr.mBtnAIDownStatus = false;

			// 智能提示逻辑
			AIControl();
		}

		boolean isPauseClick = mGameView.mBtnMgr.mBtnPause.IsClick(x, y);
		if (isPauseClick) {
			mGameView.mBtnMgr.mBtnPauseStatus = false;

			if (!mIsGamePause) {
				gamePause();
			} else {
				gameRecover();
			}

			mGameView.mBtnMgr.mIsPause = mIsGamePause;
		}

		return true;
	}

	public void AIControl() {
		mTetrisAI.startAI();

		if (mTetrisAI.isFind()) {
			int bestX = mTetrisAI.getBestX();
			int bestRotate = mTetrisAI.getBestRotate();

			autoControl(bestX, bestRotate);
		}
	}

	public void autoControl(int destX, int destRotate) {
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

				if (!mTetrisGame.moveRight()) {
					break;
				}
			}
		} else {
			while (true) {
				if (destX == mTetrisGame.getCurX()) {
					break;
				}

				if (!mTetrisGame.moveLeft()) {
					break;
				}
			}
		}

		mTetrisGame.moveFastDown();
		mTetrisGame.moveDown();
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setGameTheadExit() {
		if (mGameThread != null) {
			mGameThread.recover();
			mGameThread.exitGame();
		}
		
		if (mGameUIThread != null) {
			mGameUIThread.exitGame();
		}
	}

	public void stopAI() {
		if (isAiStart) {
			mAIThread.stopAI();
			isAiStart = false;
		}
	}

	public void startAI() {
		while (true) {
			if (isAiStart) {
				break;
			}

			isAiStart = true;
			mAIThread = new AIThread(mTetrisGame);
			new Thread(mAIThread).start();

			break;
		}
	}

	public void exitGame() {
		MusicPlayer.releaseMusic();
		setGameTheadExit();
		stopAI();
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveLeft();

				MusicPlayer.playSound(R.raw.action, 0);
			}

			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveRight();

				MusicPlayer.playSound(R.raw.action, 0);
			}

			break;

		case KeyEvent.KEYCODE_DPAD_UP:

			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveUp();

				MusicPlayer.playSound(R.raw.rotation, 0);
			}
			break;

		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveDown();

				MusicPlayer.playSound(R.raw.down, 0);
			}

			break;

		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (!mGameThread.isGamePause()) {
				mTetrisGame.moveFastDown();

				MusicPlayer.playSound(R.raw.fastdown, 0);
			}
			break;

		// 按下返回按钮
		case KeyEvent.KEYCODE_BACK:
			if (event.getRepeatCount() == 0) {
				// 询问是否退出

				dialogExit();
			}
			break;
		default:
			break;
		}

		return true;
	}

	protected void dialogExit() // 对话框函数
	{
		AlertDialog.Builder builder = new Builder(GameActivity.this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				exitGame();
			}
		});
		
		builder.setNeutralButton("退出并存档", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitGame();
				
				startSaveActivity();
			}
		});
		
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	protected void dialogGameOver() // 对话框函数
	{
		setGameTheadExit();

		AlertDialog.Builder builder = new Builder(GameActivity.this);
		builder.setMessage("游戏结束！！！");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				exitGame();

				Intent intent = new Intent(GameActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		builder.setNeutralButton("退出并存档", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitGame();
				
				startSaveActivity();
			}
		});

		builder.create().show();
	}
	
	private void startSaveActivity() {
		Intent intent = new Intent(GameActivity.this, SaveScore.class);
		intent.putExtra("cur_score", mTetrisGame.getScore());
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		MusicPlayer.releaseMusic();
	}

}
