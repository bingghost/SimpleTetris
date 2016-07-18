package com.bingghost.simpletetris.ui;

import com.bingghost.simpletetris.activity.GameActivity;
import com.bingghost.simpletetris.game.BoxStatus;
import com.bingghost.simpletetris.game.TetrisGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

public class GameView extends View {
	
	public Context mContext;
	
	public int mScreenWidth = 0;
	public int mScreenHeight = 0;
	
	public GameResources mGameResources = null;    // 游戏需要的资源
	public int   mBoxLen    = 0;                   // 每一个方块的长度
	public Paint mBtnPaint  = null;                // button画笔
	public Paint mBtnInfoPaint  = null;            // button画笔
	public Paint mBoxPaint  = null;                // Box画笔
	public Paint mBoxAssistPaint  = null;          // 辅助Box画笔
	
	public ButtonManager mBtnMgr = null;           // 按钮管理类
	public TetrisGame mTetrisGame = null;
	
	
	public GameView(Context context) {
		super(context);
		
		mContext = context;
		initGame(context);
		
	}
	
	public void initGame(Context context) {
		getScreenInfo(context);
		
		initGameResource(context);
		
		initBtnMgr(context);
		
		initPaint();
		
		initTetrisGame();
	}
	
	public void initTetrisGame(){
		mTetrisGame = new TetrisGame(mGameResources.mGameWidth,mGameResources.mGameHeight);
	}
	
	public void getScreenInfo(Context context){
		// get screen information
		DisplayMetrics  dm = new DisplayMetrics();    
		((GameActivity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;         
		mScreenHeight = dm.heightPixels;
	}
	
	public void initGameResource(Context context){
		mGameResources = new GameResources(context, mScreenWidth, mScreenHeight);
		mBoxLen = mGameResources.mBoxLen;
	}
	
	public void initPaint() { 
		// button paint
		mBtnPaint = new Paint();          // 新建画笔
		mBtnPaint.setAntiAlias(true);     // 设置抗锯齿
		mBtnPaint.setFakeBoldText(true);  // true为粗体，false为非粗体
		mBtnPaint.setColor(Color.RED);
		mBtnPaint.setAlpha(150);          // 设置透明度
		
		mBtnInfoPaint = new Paint();
		mBtnInfoPaint.setAntiAlias(true);     // 设置抗锯齿
		mBtnPaint.setAlpha(200);              // 设置透明度
		
		mBoxPaint = new Paint();
		mBoxPaint.setAntiAlias(true);     // 设置抗锯齿
		
		mBoxAssistPaint = new Paint();
		mBoxAssistPaint.setAntiAlias(true);     // 设置抗锯齿
		mBoxAssistPaint.setFakeBoldText(true);  // true为粗体，false为非粗体
		mBoxAssistPaint.setAlpha(100);          // 设置透明度
	}
	
	public void initBtnMgr(Context context) {
		mBtnMgr = new ButtonManager(context, mScreenWidth, mScreenWidth,mBoxLen,mGameResources);
	}
	
	public void drawAssistBox(Canvas canvas) {
		mTetrisGame.calcAssistCoordinate();
		
		// 画当前方块
		mGameResources.drawBoxs(canvas, BoxStatus.box_state[mTetrisGame.getBoxIndex()], 
				mTetrisGame.mAssistX, 
				mTetrisGame.mAssistY, 
				mTetrisGame.getCurColor(), mBoxAssistPaint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	
		// draw backgroud color
		canvas.drawColor(Color.BLACK);
		// canvas.drawColor(Color.argb(255, 9, 137, 212));
		
		// draw control buttons
		mBtnMgr.drawButtons(canvas, mBtnPaint);
		mBtnMgr.drawInfoButtons(canvas,mBtnInfoPaint);
		
		drawGameInfo(canvas);
		
		// 画下一个方块
		mGameResources.drawBoxs(canvas, BoxStatus.box_state[mTetrisGame.getNextBoxIndex()], 
				mGameResources.mNextX, 
				mGameResources.mNextY, 
				mTetrisGame.getNextColor(), mBoxPaint);
		// 画当前方块
		mGameResources.drawBoxs(canvas, BoxStatus.box_state[mTetrisGame.getBoxIndex()], 
				mTetrisGame.getCurX(), 
				mTetrisGame.getCurY(), 
				mTetrisGame.getCurColor(), mBoxPaint);
		
		drawAssistBox(canvas);
		
		drawGameInfo(canvas);
		
		// 画背景数组
		mGameResources.drawGameBoxs(canvas,mTetrisGame.mGameBoxs,mTetrisGame.mGameColors,mBoxPaint);
		
		// draw backgroud
		canvas.drawBitmap(mGameResources.mBitmapBgCache,0,0,null);
	}
	
	public void drawGameInfo(Canvas canvas){
		mGameResources.setGameLevel(canvas, mTetrisGame.getGameLevel());
		mGameResources.setLineValue(canvas, mTetrisGame.getLines());
		mGameResources.setScoreValue(canvas, mTetrisGame.getScore());
	}
}
