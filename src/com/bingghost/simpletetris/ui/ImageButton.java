package com.bingghost.simpletetris.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class ImageButton {
	//  按钮图片
    private Bitmap mBitButton = null;
    
    //图片绘制的XY坐标
    private int mPosX =0;
    private int mPosY =0;
    //图片绘制的宽高
    public int mWidth =0;
    public int mHeight =0;
    
    public int mBtnLen = 0;
    
    public ImageButton(Context context, int resId, int x, int y,int width,int height) {
		mWidth  = width;
		mHeight = height;
    	
		mPosX = x;
		mPosY = y;
		mBitButton = ReadBitMap(context,resId);
    }
    
    // 绘制图片按钮
    //   * @param canvas
    //   * @param paint
    public void DrawImageButton(Canvas canvas, Paint paint) {
    	canvas.drawBitmap(mBitButton, mPosX, mPosY, paint);
    }
    
   //  判断是否点中图片按钮
    public boolean IsClick(int x, int y) {
		boolean isClick = false;
		if (x >= mPosX && x <= mPosX + mWidth && y >= mPosY
			&& y <= mPosY + mHeight) {
		    isClick = true;
		}
		return isClick;
    }
    
    // 读取图片资源
    public Bitmap ReadBitMap(Context context, int resId) {
    	Resources resource = context.getResources();
		Drawable drawable = resource.getDrawable(resId);
		return createImage(drawable, mWidth, mHeight);
    }
    
	public Bitmap createImage(Drawable dw, int width, int height) { 
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		dw.setBounds(0, 0, width, height);
		dw.draw(canvas);
		return bitmap;
	}
}
