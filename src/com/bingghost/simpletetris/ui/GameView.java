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
	
	public GameResources mGameResources = null;    // ��Ϸ��Ҫ����Դ
	public int   mBoxLen    = 0;                   // ÿһ������ĳ���
	public Paint mBtnPaint  = null;                // button����
	public Paint mBtnInfoPaint  = null;            // button����
	public Paint mBoxPaint  = null;                // Box����
	public Paint mBoxAssistPaint  = null;          // ����Box����
	
	public ButtonManager mBtnMgr = null;           // ��ť������
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
		mBtnPaint = new Paint();          // �½�����
		mBtnPaint.setAntiAlias(true);     // ���ÿ����
		mBtnPaint.setFakeBoldText(true);  // trueΪ���壬falseΪ�Ǵ���
		mBtnPaint.setColor(Color.RED);
		mBtnPaint.setAlpha(150);          // ����͸����
		
		mBtnInfoPaint = new Paint();
		mBtnInfoPaint.setAntiAlias(true);     // ���ÿ����
		mBtnPaint.setAlpha(200);              // ����͸����
		
		mBoxPaint = new Paint();
		mBoxPaint.setAntiAlias(true);     // ���ÿ����
		
		mBoxAssistPaint = new Paint();
		mBoxAssistPaint.setAntiAlias(true);     // ���ÿ����
		mBoxAssistPaint.setFakeBoldText(true);  // trueΪ���壬falseΪ�Ǵ���
		mBoxAssistPaint.setAlpha(100);          // ����͸����
	}
	
	public void initBtnMgr(Context context) {
		mBtnMgr = new ButtonManager(context, mScreenWidth, mScreenWidth,mBoxLen,mGameResources);
	}
	
	public void drawAssistBox(Canvas canvas) {
		mTetrisGame.calcAssistCoordinate();
		
		// ����ǰ����
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
		
		// ����һ������
		mGameResources.drawBoxs(canvas, BoxStatus.box_state[mTetrisGame.getNextBoxIndex()], 
				mGameResources.mNextX, 
				mGameResources.mNextY, 
				mTetrisGame.getNextColor(), mBoxPaint);
		// ����ǰ����
		mGameResources.drawBoxs(canvas, BoxStatus.box_state[mTetrisGame.getBoxIndex()], 
				mTetrisGame.getCurX(), 
				mTetrisGame.getCurY(), 
				mTetrisGame.getCurColor(), mBoxPaint);
		
		drawAssistBox(canvas);
		
		drawGameInfo(canvas);
		
		// ����������
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
