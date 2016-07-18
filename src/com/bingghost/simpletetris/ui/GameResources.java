package com.bingghost.simpletetris.ui;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.game.TetrisGame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;

/*
 * ˵��: ����Ϊ��Դװ����
 * */
public class GameResources {
	public static final String TAG = "_BING_";
	
	public int mGameWidth  = 0;                // ����˹��������
	public int mGameHeight = 0;                // ����˹����߸���
	public static final int WALL_SCALE = 5;    // ��Ϸǽ�������           
	public static final int INFO_SCALE = 3;    // ��Ϣ���ȱ���
	public static final int TOTAL_SCALE = WALL_SCALE + INFO_SCALE; // ��Ļ��Ȼ��ֱ���
	
	public Resources mResources = null;        // ��Դ����
	public Canvas mCanvas = null;              // ������˫���廭��
	
	// background bitmap
	public Bitmap mBitmapBg = null;            // ����ͼbitmap
	public Bitmap mBitmapBgCache = null;       // ����˫����bitmap
	
	// box wall bitmap
	public Bitmap mBitmapWall = null;          // ǽ��bitmap
	
	public int mBoxLen = 0;        // ����ߴ�
	public int mScreenWidth = 0;   // ��Ļ���
	public int mScreenHeight = 0;  // ��Ļ�߶�
	
	public int mNextX = 0;         // ��һ���������ʼ����
	public int mNextY = 0;
	
	public int mPaintWidth = 0;    // ���ʿ��
	
	public int mGameInfoX = 0;     // GameInfo ����
	public int mLineY  = 0;
	public int mScoreY = 0;
	public int mHardY  = 0;
	
	// box λͼ����
	Bitmap[] mBitmapBoxs = new Bitmap[6];
	
	public GameResources(Context context,int screenWidth,int screenheight) {
		mResources = context.getResources();
		mScreenWidth = screenWidth;
		mScreenHeight = screenheight;
		mGameWidth = 14;
		
		// ���㻭�ʿ��
		double tmp = (double)mScreenHeight / (double)mScreenWidth;
		mPaintWidth = (int) Math.rint(tmp - 0.1);
		
		calcBoxLen();
		
		initBoxs();
		
		InitGame();
		
		InitResources();
	}
	
	public void initBoxs() {
		// ��ʼ����Ϸ��������
		// ��������
		for(int i=0; i < 6; i++)
		{
			mBitmapBoxs[i] = createImage(mResources.getDrawable(R.drawable.box_001+i),mBoxLen,mBoxLen);
		}
	}
	
	public void InitGame() {
		// ��̬������Ϸ�߶�
		int nBtnLen = mScreenWidth / 5;
		int tmp_len = mScreenHeight - (mGameWidth + 4) * mBoxLen - nBtnLen;
		mGameHeight = tmp_len / mBoxLen + mGameWidth;
	}
	
	public void InitResources() {
		// initialize wall bitmap
		Drawable wallDrawable = mResources.getDrawable(R.drawable.box_wall);
		mBitmapWall = createImage(wallDrawable, mBoxLen, mBoxLen);
		
		// initialize bitmap cache to draw background
		Drawable bgDrawable = mResources.getDrawable(R.drawable.background);
		mBitmapBg = createImage(bgDrawable,mScreenWidth,mScreenHeight);
		mBitmapBgCache = Bitmap.createBitmap(mScreenWidth,mScreenHeight, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmapBgCache);
		bitmapBackground();
	}
	
	public void calcBoxLen() {
		int game_len = (mScreenWidth / TOTAL_SCALE) * WALL_SCALE;
		mBoxLen = game_len / (mGameWidth + 2);
	}
	
	public void bitmapBackground()
	{
		drawLogo();
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setAlpha(150);
		mCanvas.drawBitmap(mBitmapBg,0,0,paint);
		
		drawWall();
		drawNext();
		drawEdge();
		drawBtnLine();
		drawGameInfo();
	}
	
	public void drawGameInfo () {
		Paint textPaint = new Paint();
		int textSize = mBoxLen;
		textPaint.setColor(Color.YELLOW);
		textPaint.setTextSize(textSize);               //���������С
		textPaint.setTypeface(Typeface.SANS_SERIF);    //������������
		
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(mPaintWidth);
		linePaint.setColor(Color.WHITE);
		
		int lineX = mBoxLen * (mGameWidth + 2);
		int lineY = mBoxLen * 8;
		mCanvas.drawLine(lineX, lineY, mScreenWidth, lineY, linePaint);
		
		int textX = mBoxLen * 2 + lineX;
		int textY = lineY + mBoxLen*2;
		
		mGameInfoX = textX + mBoxLen * 3;
		mLineY     = textY;
		mScoreY    = textY + mBoxLen*2;
		mHardY     = textY + mBoxLen*4;
		
		mCanvas.drawText("lines:",textX, mLineY,textPaint);
		mCanvas.drawText("score:",textX, mScoreY,textPaint);
		mCanvas.drawText("level:",textX,  mHardY,textPaint);
		
		lineY = textY + mBoxLen * 6;
		mCanvas.drawLine(lineX, lineY, mScreenWidth, lineY, linePaint);
	}
	
	public void setLineValue(Canvas canvas,int lines) {
		Paint textPaint = new Paint();
		int textSize = mBoxLen;
		textPaint.setColor(Color.GREEN);
		textPaint.setTextSize(textSize);               //���������С
		textPaint.setTypeface(Typeface.SANS_SERIF);    //������������
		
		canvas.drawText("" + lines,mGameInfoX,mLineY,textPaint);
	}
	
	public void setScoreValue(Canvas canvas,int score) {
		Paint textPaint = new Paint();
		int textSize = mBoxLen;
		textPaint.setColor(Color.GREEN);
		textPaint.setTextSize(textSize);               //���������С
		textPaint.setTypeface(Typeface.SANS_SERIF);    //������������
		
		canvas.drawText("" + score,mGameInfoX,mScoreY,textPaint);
	}
	
	public void setGameLevel(Canvas canvas,int level) {
		Paint textPaint = new Paint();
		int textSize = mBoxLen;
		textPaint.setColor(Color.GREEN);
		textPaint.setTextSize(textSize);               //���������С
		textPaint.setTypeface(Typeface.SANS_SERIF);    //������������
		
		canvas.drawText("" + level,mGameInfoX,mHardY,textPaint);
	}
	
	public void drawEdge() { 
		Paint paint = new Paint();
		paint.setStrokeWidth(mPaintWidth);   //�ʿ�5����
		paint.setColor(Color.WHITE);
		
		// Y�����ȥ���ʿ��
		int Y  = (mGameHeight + 1) * mBoxLen;
		// int X1 = (mGameWidth + 2) * mBoxLen;
		int X2 = mScreenWidth;
		
		mCanvas.drawLine(0,Y,X2,Y,paint);
	}
	
	public void drawBtnLine() { 
		Paint paint = new Paint();
		
		paint.setStrokeWidth(mPaintWidth);   //�ʿ�5����
		paint.setAntiAlias(true);  //��ݲ���ʾ
		paint.setColor(Color.WHITE);
		
		int btnLen = mScreenWidth / 5;
		int btnY1 = mBoxLen * (mGameHeight + 3);
		int btnY2 = btnY1 + btnLen; 
		
		// ������
		mCanvas.drawLine(0,btnY1,mScreenWidth,btnY1,paint);
		mCanvas.drawLine(0,btnY2,mScreenWidth,btnY2,paint);
		
		// ������
		mCanvas.drawLine(btnLen * 0,btnY1,btnLen * 0,btnY2,paint);
		mCanvas.drawLine(btnLen * 1,btnY1,btnLen * 1,btnY2,paint);
		mCanvas.drawLine(btnLen * 2,btnY1,btnLen * 2,btnY2,paint);
		mCanvas.drawLine(btnLen * 3,btnY1,btnLen * 3,btnY2,paint);
		mCanvas.drawLine(btnLen * 4,btnY1,btnLen * 4,btnY2,paint);
		mCanvas.drawLine(btnLen * 5,btnY1,btnLen * 5,btnY2,paint);
	}
	
	public void drawLogo() { 
		// text paint
		Paint textPaint = new Paint();
		int textSize = mBoxLen;
		textPaint.setColor(Color.YELLOW);
		textPaint.setTextSize(textSize);               //���������С
		textPaint.setTypeface(Typeface.SANS_SERIF);    //������������
		
		int textX = (mBoxLen * (mGameWidth + 2)) / 2;
		textX = textX - 3 * textSize;
		int textY = mBoxLen * (mGameHeight / 2);
		mCanvas.drawText("bingghost", textX, textY, textPaint);
	}
	
	public void drawNext() { 
		// text paint
		Paint paint = new Paint();
		int textSize = mBoxLen;
		paint.setColor(Color.YELLOW);
		paint.setTextSize(textSize);               // ���������С
		paint.setTypeface(Typeface.SANS_SERIF);    // ������������
		
		int textX = mBoxLen * (mGameWidth + 3);
		int textY = mBoxLen * 2;
		mCanvas.drawText("Next:", textX, textY, paint);
		
		drawNineBox();
	}
	
	public void drawNineBox() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(mPaintWidth);   // ���ʿ�� ��λ����
		
		// ����5����
		int x1 = mBoxLen * (mGameWidth + 4);
		int x2 = mBoxLen * (mGameWidth + 8);
		
		mCanvas.drawLine(x1, mBoxLen * 3, x2, mBoxLen * 3, paint);
		mCanvas.drawLine(x1, mBoxLen * 4, x2, mBoxLen * 4, paint);
		mCanvas.drawLine(x1, mBoxLen * 5, x2, mBoxLen * 5, paint);
		mCanvas.drawLine(x1, mBoxLen * 6, x2, mBoxLen * 6, paint);
		mCanvas.drawLine(x1, mBoxLen * 7, x2, mBoxLen * 7, paint);
		
		// ����5����
		int y1 = mBoxLen * 3;
		int y2 = mBoxLen * 7;
		mCanvas.drawLine(mBoxLen * (mGameWidth + 4), y1, mBoxLen * (mGameWidth + 4), y2, paint);
		mCanvas.drawLine(mBoxLen * (mGameWidth + 5), y1, mBoxLen * (mGameWidth + 5), y2, paint);
		mCanvas.drawLine(mBoxLen * (mGameWidth + 6), y1, mBoxLen * (mGameWidth + 6), y2, paint);
		mCanvas.drawLine(mBoxLen * (mGameWidth + 7), y1, mBoxLen * (mGameWidth + 7), y2, paint);
		mCanvas.drawLine(mBoxLen * (mGameWidth + 8), y1, mBoxLen * (mGameWidth + 8), y2, paint);
		
		// ����Next�Ź����׵�ַ
		mNextX = mGameWidth + 4;
		mNextY = 3;
	}
	
	public void drawWall() {
		// ����ǽ����Ϸ����Χ��СΪ mGameWidth * mGameHeight
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		for (int i = 0;i < mGameHeight;i++) { 
			mCanvas.drawBitmap(mBitmapWall, 0, i*mBoxLen, paint);
			mCanvas.drawBitmap(mBitmapWall, (mGameWidth + 1) * mBoxLen, i*mBoxLen, paint);
		}
		
		for (int i = 0;i <= mGameWidth + 1;i++) {
			mCanvas.drawBitmap(mBitmapWall, i*mBoxLen, mGameHeight*mBoxLen, paint);
		}
	}
	
	// double-buffing to loading bitmap
	public static Bitmap createImage(Drawable dw, int width, int height) { 
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		dw.setBounds(0, 0, width, height);
		dw.draw(canvas);
		return bitmap;
	}
	
	public void drawBox(Canvas canvas,int startX,int startY,int box_color,Paint paint) {
		canvas.drawBitmap(mBitmapBoxs[box_color], startX*mBoxLen, startY*mBoxLen, paint);
	}
	
	public void drawBoxs(Canvas canvas,int[][] arrayBox,int startX,int startY,int box_color,Paint paint) {
		
		for (int i = 0;i < 4;i++) {
			for (int j = 0;j < 4;j++) {
				if (arrayBox[i][j] == 1) {
					drawBox(canvas,startX + j,startY + i,box_color,paint);
					continue;
				}
			}
		}
	}
	
	public void drawGameBoxs(Canvas canvas,int[][] gameBoxs,int[][] corlors,Paint paint) {
		for (int i = 0;i < mGameHeight;i++) {
			for (int j = 0;j < mGameWidth + 1;j++) {
				if (gameBoxs[i][j] == TetrisGame.BOX_FIX) {
					drawBox(canvas,j,i,corlors[i][j],paint);
				}
			}
		}
	}
	
	/** 
	* �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
	*/  
	public static int dip2px(Context context, float dpValue) {  
	  final float scale = context.getResources().getDisplayMetrics().density;  
	  return (int) (dpValue * scale + 0.5f);  
	}
}
