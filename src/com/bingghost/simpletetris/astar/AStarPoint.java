package com.bingghost.simpletetris.astar;

import java.lang.Math;

public class AStarPoint {
	public static final int KEY_SEED = 1000;      
	
	private int mX = 0;               // 当前坐标点
	private int mY = 0;                
	private int mPrevPointX = 0;      // 上一个坐标点
	private int mPrevPointY = 0;      
	private int mNextPointX = 0;
	private int mNextPointY = 0;
	private double mScore = 0.0;      // 评分
	private int mStepFromSrc = 0;     // 距离起始点的步数
	private int mKey = 0;
	
	
	public void calcKey(){
		setKey(KEY_SEED * getX() + getY());
	}
	
	public AStarPoint(int x,int y) {
		setX(x);
		setY(y);
		setKey(KEY_SEED * getX() + getY());
		setScore(0);
		mPrevPointX  = 0;
		mPrevPointY  = 0;
		setStepFromSrc(0);
	}
	
	public void setPrevPoint(int x,int y) {
		mPrevPointX = x;
		mPrevPointY = y;
	}
	
	public int getPrevPointX() {
		return mPrevPointX;
	}
	
	public int getPrevPointY() {
		return mPrevPointY;
	}
	
	// 计算评分
	public void calcScore(AStarPoint destPoint) {
		// 2点间距离
		double pow_sub = Math.pow(destPoint.getY() - getY(), 2.0) + Math.pow(destPoint.getX() - getX(), 2.0);
		
		double distance = Math.sqrt(pow_sub);
		
		double stepFromSrc = getStepFromSrc();
		
		setScore(-(distance + 5*stepFromSrc));
	}
	
	public int getKey() {
		return this.mKey;
	}

	public void setKey(int mKey) {
		this.mKey = mKey;
	}

	public double getScore() {
		return mScore;
	}

	public void setScore(double mScore) {
		this.mScore = mScore;
	}

	public int getX() {
		return mX;
	}

	public void setX(int mX) {
		this.mX = mX;
	}

	public int getY() {
		return mY;
	}

	public void setY(int mY) {
		this.mY = mY;
	}

	public int getStepFromSrc() {
		return mStepFromSrc;
	}

	public void setStepFromSrc(int mStepFromSrc) {
		this.mStepFromSrc = mStepFromSrc;
	}

	public int getNextPointX() {
		return mNextPointX;
	}

	public void setNextPoint(int nextPointX,int nextPointY) {
		this.mNextPointX = nextPointX;
		this.mNextPointY = nextPointY;
	}

	public int getNextPointY() {
		return mNextPointY;
	}
}
