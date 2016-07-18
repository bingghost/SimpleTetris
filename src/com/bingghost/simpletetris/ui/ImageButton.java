package com.bingghost.simpletetris.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class ImageButton {
	//  ��ťͼƬ
    private Bitmap mBitButton = null;
    
    //ͼƬ���Ƶ�XY����
    private int mPosX =0;
    private int mPosY =0;
    //ͼƬ���ƵĿ��
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
    
    // ����ͼƬ��ť
    //   * @param canvas
    //   * @param paint
    public void DrawImageButton(Canvas canvas, Paint paint) {
    	canvas.drawBitmap(mBitButton, mPosX, mPosY, paint);
    }
    
   //  �ж��Ƿ����ͼƬ��ť
    public boolean IsClick(int x, int y) {
		boolean isClick = false;
		if (x >= mPosX && x <= mPosX + mWidth && y >= mPosY
			&& y <= mPosY + mHeight) {
		    isClick = true;
		}
		return isClick;
    }
    
    // ��ȡͼƬ��Դ
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
