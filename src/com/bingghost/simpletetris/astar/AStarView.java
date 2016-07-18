package com.bingghost.simpletetris.astar;

import java.util.Map;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.activity.AStarActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class AStarView extends View {
	public static final int ASTAR_WIDTH = 30;
	
	public int mScreenWidth  = 0;
	public int mScreenHeight = 0;
	
	private int mBoxLen = 0;
	private Resources mResources = null;
	public Context mContext = null;
	
	private Bitmap mBitmapRoadblock = null;      // bitmap 用于绘制
	private Bitmap mBitmapBlank     = null;
	private Bitmap mBitmapStart     = null;
	private Bitmap mBitmapEnd       = null;
	private Bitmap mBitmapRoadLine  = null;
	
	private Paint mPaint     = null;    
	private Paint mPaintInfo = null;
	
	
	public AStarView(Context context) {
		super(context);
		
		initAstar(context);
	}
	
	public void initAstar(Context context) {
		mContext = context;
		
		setAStarResources(context.getResources());
		
		initView();	
	}
	
	public AStarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AStarView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	
	private void initPaint(){
		mPaint     = new Paint();
		mPaint.setAntiAlias(true);
		
		mPaintInfo = new Paint();
		mPaintInfo.setAntiAlias(true);
		mPaintInfo.setColor(Color.YELLOW);
		mPaintInfo.setTextSize(2*mBoxLen);
		
	}
	
	private void drawBox(Canvas canvas,int aStarStatus,int x,int y,Paint paint) {
		switch (aStarStatus) {
		case AStar.ASTAR_BLANK:
			canvas.drawBitmap(mBitmapBlank, x*getBoxLen(), y*getBoxLen(), paint);
			break;
		case AStar.ASTAR_ROADBLOCK:
			canvas.drawBitmap(mBitmapRoadblock, x*getBoxLen(), y*getBoxLen(), paint);
			break;
		case AStar.ASTAR_STARTPOINT:
			canvas.drawBitmap(mBitmapStart, x*getBoxLen(), y*getBoxLen(), paint);
			break;
		case AStar.ASTAR_ENDPOINT:
			canvas.drawBitmap(mBitmapEnd, x*getBoxLen(), y*getBoxLen(), paint);
			break;
		case AStar.ASTAR_ROADLINE:
			canvas.drawBitmap(mBitmapRoadLine, x*getBoxLen(), y*getBoxLen(), paint);
		default:
			break;
		}
	}
	
	public void drawBackground(Canvas canvas) {
		AStarActivity theAStarActivity = (AStarActivity)mContext;
		if (theAStarActivity == null) {
			return;
		}
		
		AStar aStar = theAStarActivity.mAStar;
		int[][] mBgArray = aStar.getBgArray();
		
		for (int i = 0;i < aStar.getAStarHeight();i++) {
			for (int j = 0;j < aStar.getAStarWidth();j++) {
				drawBox(canvas,mBgArray[i][j],j,i,mPaint);
			}
		}
	}
	
	public void drawPath(Canvas canvas) {
		AStarActivity theAStarActivity = (AStarActivity)mContext;
		if (!theAStarActivity.mAStar.IsFind()) {
			return;
		}
		
		Map<Integer,AStarPoint> closeMap = theAStarActivity.mAStar.getCloseMap();
		AStarPoint endPoint = theAStarActivity.mAStar.getEndPoint();
		AStarPoint startPoint = theAStarActivity.mAStar.getStartPoint();
		
		int x = endPoint.getPrevPointX();
		int y = endPoint.getPrevPointY();
		while (true) {
			AStarPoint curPoint = new AStarPoint(x, y);
			
			curPoint = closeMap.get(curPoint.getKey());
			
			if (startPoint.getKey() == curPoint.getKey()) {
				break;
			}
			
			drawBox(canvas,AStar.ASTAR_ROADLINE,x,y,mPaint);
			
			x = curPoint.getPrevPointX();
			y = curPoint.getPrevPointY();
			
		}
	}
	
	public void drawPathEx(Canvas canvas) {
		AStarActivity theAStarActivity = (AStarActivity)mContext;
		if (theAStarActivity == null) {
			return;
		}
		
		if (!theAStarActivity.mAStar.IsFind()) {
			return;
		}
		
		Map<Integer,AStarPoint> closeMap = theAStarActivity.mAStar.getCloseMap();
		AStarPoint endPoint = theAStarActivity.mAStar.getEndPoint();
		AStarPoint startPoint = theAStarActivity.mAStar.getStartPoint();
		
		int x = startPoint.getNextPointX();
		int y = startPoint.getNextPointY();
		AStarPoint tmpPoint = new AStarPoint(x, y);
		while (true) {
			tmpPoint.setX(x);
			tmpPoint.setY(y);
			tmpPoint.calcKey();
			AStarPoint curPoint = tmpPoint;
			if (endPoint.getKey() == curPoint.getKey()) {
				break;
			}
			
			drawBox(canvas,AStar.ASTAR_ROADLINE,x,y,mPaint);
			
			curPoint = closeMap.get(curPoint.getKey());
			x = curPoint.getNextPointX();
			y = curPoint.getNextPointY();
			
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.BLACK);
		drawInfomation(canvas);
		
		// 画背景
		drawBackground(canvas);
		
		// drawPath(canvas);
		
		drawPathEx(canvas);
	}
	
	private void drawInfomation(Canvas canvas){
		AStarActivity theAStarActivity = (AStarActivity)mContext;
		if (theAStarActivity == null) {
			return;
		}
		
		AStar aStar = theAStarActivity.mAStar;
		
		int x = mBoxLen * 2;
		int y = (aStar.getAStarHeight() + 2) * mBoxLen;
		
		canvas.drawText("↓方向键生成地图,↑方向键寻路", x, y, mPaintInfo);
	}
	
	
	private void initResources() {
		
		mBitmapRoadblock = createImage(mResources.getDrawable(R.drawable.box_1),getBoxLen(),getBoxLen());
		mBitmapBlank = createImage(mResources.getDrawable(R.drawable.box_5),getBoxLen(),getBoxLen());
		mBitmapStart = createImage(mResources.getDrawable(R.drawable.box_3),getBoxLen(),getBoxLen());
		mBitmapEnd = createImage(mResources.getDrawable(R.drawable.box_7),getBoxLen(),getBoxLen());
		mBitmapRoadLine = createImage(mResources.getDrawable(R.drawable.box_8),getBoxLen(),getBoxLen());
	}
	
	private void initView() {
		getScreenInfo(mContext);
		
		calcBoxLen();
		
		initResources();
		
		initPaint();
	}
	
	private void calcBoxLen() {
		setBoxLen(mScreenWidth / ASTAR_WIDTH);
	}
	
	public void getScreenInfo(Context context){
		// get screen information
		DisplayMetrics  dm = new DisplayMetrics();    
		((AStarActivity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;         
		mScreenHeight = dm.heightPixels;
	}

	public int getBoxLen() {
		return mBoxLen;
	}

	public void setBoxLen(int mBoxLen) {
		this.mBoxLen = mBoxLen;
	}

	public Resources getAStarResources() {
		return mResources;
	}

	public void setAStarResources(Resources mResources) {
		this.mResources = mResources;
	}
	
	public static Bitmap createImage(Drawable dw, int width, int height) { 
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		dw.setBounds(0, 0, width, height);
		dw.draw(canvas);
		return bitmap;
	}

}
