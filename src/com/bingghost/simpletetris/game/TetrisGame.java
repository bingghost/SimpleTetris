package com.bingghost.simpletetris.game;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.config.AppConfig;
import com.bingghost.simpletetris.control.MusicPlayer;

import android.util.Log;

public class TetrisGame {
	public static final int COLORS_NUM = 6;
	public static final int BOX_NUM = 4;
	public static final int GAME_WALL = 2;
	public static final int GAME_EMPTY = 0;
	public static final int BOX_FIX = 1;
	private int mGameLevel = 1;
	
	private int mLines = 0;
	private int mScore = 0;
	private int mCurX = 0;        // 当前方块的坐标
	private int mCurY = 0;
	
	public int mAssistX = 0;      // 辅助方块
	public int mAssistY = 0;
	
	private int mCurColor  = 0;   // 当前方块颜色
	private int mCurType   = 0;   // 当前方块类型
	private int mCurRotate = 0;
	
	private int mNextColor  = 0;
	private int mNextType   = 0;
	private int mNextRotate = 0;
	
	public int[][] mGameBoxs;
	public int[][] mGameColors;
	
	public int mGameHeight = 0;
	public int mGameWidth = 0;
	
	public TetrisGame(int width,int height) {
		mGameWidth = width + 2;
		mGameHeight = height + 1;
		
		initGame();
		
		NextBox();
		
		createNewBox();
	}
	
	public void NextBox() {
		int box_total = BoxStatus.box_state.length;
		int box_types = box_total / 4;
		
		setNextType((int)(Math.random() * box_types));
		mNextRotate = (int)(Math.random() * BOX_NUM);
		setNextColor((int)(Math.random() * COLORS_NUM));
	}
	
	public void rotateBox(){
		setCurRotate((getCurRotate() + 1) % 4);
	}
	
	public int getBoxIndex() {
		return getCurType() * 4 + getCurRotate();
	}
	
	public int getNextBoxIndex() {
		return getNextType() * 4 + mNextRotate;
	}
	
	public boolean isCanMove(int x,int y) {
		
		int box_index = getBoxIndex();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// 碰撞检测
				if (BoxStatus.box_state[box_index][i][j] == BOX_FIX) {
					if ((mGameBoxs[y + i][x + j] == BOX_FIX) || 
						(mGameBoxs[y + i][x + j] == GAME_WALL) ) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public void calcAssistCoordinate() {
		mAssistX = mCurX;
		mAssistY = mCurY;
		
		while (true) {
			if (!isCanMove(mAssistX, mAssistY + 1)) {
				break;
			}
			
			if (mAssistY >= mGameHeight) {
				break;
			}
			
			if (isCanMove(mAssistX, mAssistY + 1)) {
				mAssistY++;
				continue;
			}
			
			break;
		}
	}
	
	public void moveFastDown() {
		while (true) {
			if (!isCanMove(getCurX(), getCurY() + 1)) {
				break;
			}
			
			if (getCurY() >= mGameHeight) {
				break;
			}
			
			moveDown(); 
		}
	}
	
	public void moveUp() {
		int x = getCurX();
		if (isCanMove(x, getCurY()) && 
			(x > 0) && (x < mGameWidth)) {
			rotateBox();
		}
	}
	
	public boolean moveLeft() {
		if (isCanMove(getCurX() - 1, getCurY())) {
			setCurX(getCurX() - 1);
			return true;
		}
		
		return false;
	}
	
	public boolean moveRight() {
		if (isCanMove(getCurX() + 1, getCurY())) {
			setCurX(getCurX() + 1);
			return true;
		}
		
		return false;
	}
	
	public boolean moveDown() {
		if (isCanMove(getCurX(), getCurY() + 1)) {
			setCurY(getCurY() + 1);
			return true;
		}
		
		fixBox();
		
		return createNewBox();
	}
	
	public boolean isCanRelease(int row) {
		
		for (int i = 1;i < mGameWidth;i++) {
			if (mGameBoxs[row][i] == GAME_EMPTY) {
				return false;
			}
		}
		
		return true;
	}
	
	public void releaseLine(int nRow){
		for (int i = nRow; i >= 0; i--) {
			for(int j = 1; j < mGameWidth - 1; j++) {
				if (i != 0) {
					mGameBoxs[i][j] = mGameBoxs[i - 1][j];
					mGameColors[i][j] = mGameColors[i - 1][j];
					continue;
				}
				
				mGameBoxs[i][j] = GAME_EMPTY;
			}
		}
	}
	
	public void releaseLines() {
		int release_count = 0;
		
		for (int nRow = mGameHeight - 2; nRow >= 0; nRow--) {
			if (isCanRelease(nRow)) {
				releaseLine(nRow);
				
				nRow++;
				setLines(getLines() + 1);
				setScore(getLines() * 10);
				
				release_count++;
			}
		}
		
		playSound(release_count);
	}
	
	public void playSound(int sound_type) {
		switch (sound_type) {
		case 0:
			break;
		case 1:
			MusicPlayer.playSound(R.raw.delete1,0);
			break;
		case 2:
			MusicPlayer.playSound(R.raw.delete2,0);
			break;
		case 3:
			MusicPlayer.playSound(R.raw.delete3,0);
			break;
		case 4:
			MusicPlayer.playSound(R.raw.delete4,0);
			break;
		}
	}
	
	public void fixBox() {
		int box_index = getBoxIndex();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// 碰撞检测
				if (BoxStatus.box_state[box_index][i][j] == BOX_FIX) {
					mGameBoxs[getCurY() + i][getCurX() + j] = BOX_FIX;
					mGameColors[getCurY() + i][getCurX() + j] = getCurColor();
				}
			}
		}
		
		releaseLines();
	}
	
	public boolean createNewBox() {
		// 设置新方块坐标
		setCurX(mGameWidth / 2 - 1);
		setCurY(0);
		
		setCurType(getNextType());
		setCurRotate(mNextRotate);
		setCurColor(getNextColor());
		
		NextBox();
		
		
		
		// 是否能移动
		if (isCanMove(getCurX(), getCurY())) {
			return true;
		}
		
		Log.v("__BING__", "createNewBox::can not move return flase");
		
		return false;
	}
	
	
	public void initGame() {
		mGameLevel = AppConfig.getGameLevel();
		
		mGameBoxs = new int[mGameHeight][mGameWidth];
		mGameColors = new int[mGameHeight][mGameWidth];
		
		initGameArray();
	}
	
	public void initGameArray() {
		for (int i = 0;i < mGameHeight;i++) {
			for (int j = 0;j < mGameWidth;j++) {
				
				// 初始化墙
				while (true) {
					if (j == 0) {
						mGameBoxs[i][j] = GAME_WALL;
						break;
					}
					
					if (j == (mGameWidth - 1)) {
						mGameBoxs[i][j] = GAME_WALL;
						break;
					}
					
					if (i == (mGameHeight - 1)) {
						mGameBoxs[i][j] = GAME_WALL;
						break;
					}
					
					mGameBoxs[i][j] = 0;
					mGameColors[i][j] = 0;
					break;
				}
			}
		}
	}

	public int getNextColor() {
		return mNextColor;
	}

	public void setNextColor(int mNextColor) {
		this.mNextColor = mNextColor;
	}

	public int getCurColor() {
		return mCurColor;
	}

	public void setCurColor(int mCurColor) {
		this.mCurColor = mCurColor;
	}

	public int getCurX() {
		return mCurX;
	}

	public void setCurX(int mCurX) {
		this.mCurX = mCurX;
	}

	public int getCurY() {
		return mCurY;
	}

	public void setCurY(int mCurY) {
		this.mCurY = mCurY;
	}

	public int getGameLevel() {
		return mGameLevel;
	}

	public void setGameLevel(int mGameLevel) {
		this.mGameLevel = mGameLevel;
	}

	public int getLines() {
		return mLines;
	}

	public void setLines(int mLines) {
		this.mLines = mLines;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int mScore) {
		this.mScore = mScore;
		
		
		while (true) {
			if (this.mScore >= 1000) {
				setGameLevel(5);
				break;
			}  
				
			if (this.mScore >= 2000) {
				setGameLevel(4);
				break;
			}
			
			if (this.mScore >= 3000) {
				setGameLevel(3);
				break;
			}
			
			if (this.mScore >= 4000) {
				setGameLevel(2);
				break;
			}
			
			break;
		}

	}

	public int getCurType() {
		return mCurType;
	}

	public void setCurType(int mCurType) {
		this.mCurType = mCurType;
	}

	public int getCurRotate() {
		return mCurRotate;
	}

	public void setCurRotate(int mCurRotate) {
		this.mCurRotate = mCurRotate;
	}

	public int getNextType() {
		return mNextType;
	}

	public void setNextType(int mNextType) {
		this.mNextType = mNextType;
	}
	
}
