package com.bingghost.simpletetris.game;

public class TetrisAI {
	
	public static final int AI_AMENTIA = 0;
	public static final int AI_KID     = 1;
	public static final int AI_ADULT   = 2;
	public static final int AI_GENIUS  = 3;
	
	private TetrisGame mTetrisGame;
	private int mAiLevel = 2;
	
	// 井深度表
	public static int[] wellDepthTable = new int[]{
        0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 
        120, 136, 153, 171, 190, 210, 231, 253, 276, 300, 325, 
        351, 378, 406, 435, 465, 496, 528, 561, 595, 630, 666, 
        703, 741, 780, 820, 861, 903, 946, 990, 1035, 1081, 1128, 
        1176, 1225, 1275, 1326, 1378, 1431, 1485, 1540, 1596, 1653, 
        1711, 1770, 1830, 1891, 1953, 2016, 2080, 2145, 2211, 2278, 
        2346, 2415, 2485, 2556, 2628, 2701, 2775, 2850, 2926, 3003, 
        3081, 3160, 3240, 3321, 3403, 3486, 3570, 3655, 3741, 3828, 
        3916, 4005, 4095, 4186, 4278, 4371, 4465, 4560, 4656, 4753, 
        4851, 4950, 5050};
	

	private boolean isFind = false;            // AI 是否找到
	private int[][] mCopyArray;                // 背景数组
	private int[][] mFirstBoxs;                // 第一个方块的整体数组
	private int[][] mNextBoxs;                 // 第二个方块的整体数组
	
	private int mGameY = 0;                    // 游戏中的Y坐标
	private int mNextType = 0;
	private int mCurX = 0;                     // 当前遍历的X Y
	private int mCurY = 0;
	private int mCurRotate = 0;                // 当前方块
	private int mCurType = 0;
	
	private int mBestX = 0;                    // 最优X Y
	private int mBestY = 0;
	private int mBestRotate = 0;
	private int mBestType   = 0;
	private int mBestScore  = 0;               // 策略最高分 
	
	public TetrisAI(TetrisGame tetrisGame,int aiLevel) {
		mTetrisGame = tetrisGame;
		mAiLevel = aiLevel;
		
		mCopyArray = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
		mFirstBoxs = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
		mNextBoxs = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
	}

	// 清空状态重新思考
	public void cleanStatus() {
		setFind(false);
		
		setBestX(0);
		setBestY(0);
		mBestScore = -999999999;
		
		mCurType = mTetrisGame.getCurType();
		setBestType(mCurType);
		
		mGameY = mTetrisGame.getCurY();
		mNextType = mTetrisGame.getNextType();
	}
	
	public void startAI() {
		// 清空上一次状态
		cleanStatus();
		
		// 直接判断能否移动 不能移动则无策略
		if (!isCanMove(mTetrisGame.mGameBoxs, 
				      mTetrisGame.getCurX(), 
				      mTetrisGame.getCurY(), 
				      mTetrisGame.getBoxIndex())) {
			return;
		}
		
		// 复制游戏空间
		copyGameSpace(mTetrisGame.mGameBoxs,mCopyArray);
		copyGameSpace(mTetrisGame.mGameBoxs,mFirstBoxs);
		copyGameSpace(mTetrisGame.mGameBoxs,mNextBoxs);
		
		// 搜索最佳点
		seekBestStrategy();
	}
	
	/*
	 * 功  能: 刷新最佳评分
	 * */
	public void refreshScore(int score) {
		if (score > mBestScore) {
			mBestScore = score;
			
			setBestX(mCurX);
			setBestY(mCurY);
			setBestRotate(mCurRotate);
		}
	}
	
	/*
	 * 功  能: 寻找下一个方块最优策略 
	 * */
	public void seekNextStrategy(int prevReleaseLine) {
		// 获取最高点
		int highestY = calcApex(mFirstBoxs);
		highestY = dimApex(highestY);
		
		// copy box
		restoreGameSpace(mFirstBoxs,mNextBoxs,highestY);
		
		// 深度搜索存在的情况
		for (int rotate = 0;rotate < getRotateCount(mNextType);rotate++) {
			for (int i = 0;i < mTetrisGame.mGameWidth - 2;i++) {
				int boxIndex = calcBoxIndex(mNextType,rotate);
				if (!isCanMove(mNextBoxs, i, 0, boxIndex)) {
					continue;
				}
				
				fallDown(mNextBoxs,i,0,boxIndex);
				
				// 消行
				int curReleaseCount = releaseLines(mNextBoxs,highestY); 
				
				// 评分
				evaluation(prevReleaseLine,curReleaseCount);
				
				// 恢复背景数组状态(最高点向下copy)
				restoreGameSpace(mFirstBoxs,mNextBoxs,highestY);
			}
		}
		
	}
	
	/*
	 * 功    能: 寻找最优解
	 * 参    数: 无参数
	 * 返回值: 无参数
	 * */
	public void seekBestStrategy() {
		// 获取最高点
		int highestY = calcApex(mCopyArray);
		highestY = dimApex(highestY);
		
		// 深度搜索存在的情况
		for (int rotate = 0;rotate < getRotateCount(mCurType);rotate++) {
			for (int i = 0;i < mTetrisGame.mGameWidth - 2;i++) {
				int boxIndex = calcBoxIndex(mCurType,rotate);
				if (!isCanMove(mFirstBoxs, i, mGameY, boxIndex)) {
					continue;
				}
				
				// 下落
				int curY = fallDown(mFirstBoxs,i,mGameY,boxIndex); 
				
				// 设置当前状态
				setCurStatus(i,curY,rotate);
				
				// 消行
				int releaseCount = releaseLines(mFirstBoxs,highestY);
				
				// 选择指定的策略进行AI
				chooseStrategy(boxIndex,releaseCount);
				
				// 恢复背景数组状态(最高点向下copy)
				restoreGameSpace(mCopyArray,mFirstBoxs,highestY);
			}
		}
		
		// 找到一个最佳点
		setFind(true);
	}
	
	private void chooseStrategy(int boxIndex,int releaseLines){
		while (true) {
			if (mAiLevel == 3) {
				PierreDellacherie(boxIndex,releaseLines);
				break;
			}
			
			// 遍历第2块方块的情况
			seekNextStrategy(releaseLines);
			break;
		}
	}
	
	
	/* ********************************************************************************************************
	 *                                  PierreDellacherie
	 * *********************************************************************************************************/
	private void PierreDellacherie(int boxIndex,int releaseLines) {
		int highestY = calcApex(mFirstBoxs);
		
		int landingHeight = landingHeight(boxIndex);
		int erodedPieceCellsMetric = erodedPieceCellsMetric(mFirstBoxs,mCurX,mCurY,boxIndex,releaseLines);
		int boardRowTransitions = boardRowTransitions(mFirstBoxs,highestY);
		int boardColTransitions = boardColTransitions(mFirstBoxs,highestY);
		int boardBuriedHoles = boardBuriedHoles(mFirstBoxs,highestY);
		int boardWells = boardWells(mFirstBoxs,highestY);
		
		int score = -45*landingHeight + 34*erodedPieceCellsMetric -
	    32*boardRowTransitions -
	    93*boardColTransitions -
	    79*boardBuriedHoles -
	    34*boardWells;
	
		
		refreshScore(score);
	}
	
	private int boardWells(int[][] curArray,int highestY) {
	    int sum = 0;
	    int wellDeep = 0;
	    // 第0列的井s
	    for (int r = highestY; r < mTetrisGame.mGameHeight; r++) {
	        if (curArray[r][1] == TetrisGame.GAME_EMPTY) {
	            if (curArray[r][2] == TetrisGame.BOX_FIX) {
	                wellDeep++;
	            }
	        }
	        else {
	            sum += wellDepthTable[wellDeep];
	            wellDeep = 0;
	        }
	    }
	    
        sum += wellDepthTable[wellDeep];
        wellDeep = 0;
	    
	    // 最后一列的井
	    for (int r = highestY; r < mTetrisGame.mGameHeight; r++) {
	        if (curArray[r][mTetrisGame.mGameWidth-2] == TetrisGame.GAME_EMPTY) {
	            if (curArray[r][mTetrisGame.mGameWidth-3] == TetrisGame.BOX_FIX) {
	                wellDeep++;
	            }
	        }
	        else {
	            sum += wellDepthTable[wellDeep];
	            wellDeep = 0;
	        }
	    }
	    
        sum += wellDepthTable[wellDeep];
        wellDeep = 0;
	    
	    
	    // 中间列的井
	    for (int c = 2; c < mTetrisGame.mGameWidth-2; c++) {
	        for (int r = highestY; r < mTetrisGame.mGameHeight; r++) {
	        	if (curArray[r][c] == TetrisGame.GAME_EMPTY) {
	                // 两边均有方块
	                if ((curArray[r][c - 1] == TetrisGame.BOX_FIX) &&
	                    (curArray[r][c + 1] == TetrisGame.BOX_FIX)) {
	                    wellDeep++;
	                }
	            }
	            else {
		            sum += wellDepthTable[wellDeep];
		            wellDeep = 0;
	            }
	        }
            sum += wellDepthTable[wellDeep];
            wellDeep = 0;
	    }
	    return sum;
	}
	
	private int boardBuriedHoles(int[][] curArray,int highestY) {
	    int sum = 0;
	    int topY = 0;
	    
		for (int i = 1;i < mTetrisGame.mGameWidth-1;i++) {
			
			// 寻找当前高点
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// 从当前最高点向下寻找空洞数
			for (int j = topY+1; j < mTetrisGame.mGameHeight - 1; j++) {
				if (mNextBoxs[j][i] == TetrisGame.GAME_EMPTY) {
					sum++;
				}
			}
		}
	    
	    return sum;
	}
	
	private int landingHeight(int boxIndex) {
	    // 最大高度
	    boolean find = false;
	    int maxHeight = mTetrisGame.mGameHeight-mCurY;
	    for (int r = 0; r < 4 && !find; r++) {
	        for (int c = 0; c < 4; c++) {
	            if (TetrisGame.BOX_FIX == BoxStatus.box_state[boxIndex][r][c]) {
	                find = true;
	                maxHeight = maxHeight-r;
	                break;
	            }
	        }
	    }
	    
	    // 最小高度
	    find = false;
	    int minHeight = mTetrisGame.mGameHeight-mCurY+3;
	    for (int r = 3; r >= 0 && !find; r--) {
	        for (int c = 0; c < 4; c++) {
	            if (TetrisGame.BOX_FIX == BoxStatus.box_state[boxIndex][r][c]) {
	                find = true;
	                minHeight = minHeight-r;
	                break;
	            }
	        }
	    }
	    
	    return (maxHeight+minHeight)/2;
	}
	
	private int erodedPieceCellsMetric(int[][] curArray,int x,int y,int boxIndex,int releaseLines) {
		int cellCount = 0;  // 被消格子统计
	    for (int i = 0;i < 4;i++) {
	    	for (int j = 0; j < 4; j++) {
				// 碰撞检测
				if (BoxStatus.box_state[boxIndex][i][j] == TetrisGame.BOX_FIX) {
					if (curArray[y+i][x+j] == TetrisGame.GAME_EMPTY) {
						cellCount--;
					}
				}
			}
	    }
		
		return releaseLines*cellCount;
	}
	
	// 计算行变换
	private int boardRowTransitions(int[][] curArray,int highestY)
	{
	    int sum = 0;
	    
	    for (int i=highestY;i < mTetrisGame.mGameHeight - 1;i++) {
	    	boolean isTrans = true;
	    	for (int j = 1; j < mTetrisGame.mGameWidth - 1; j++) {
				
	    		if (isTrans) {
					if (curArray[i][j] == TetrisGame.GAME_EMPTY) {
						sum++;
						isTrans = false;
					}
				} else {
					if (curArray[i][j] == TetrisGame.BOX_FIX) {
						sum++;
						isTrans = true;
					}
				}
			}
	    	
	    	if (curArray[i][mTetrisGame.mGameWidth - 2] == TetrisGame.GAME_EMPTY) {
				sum++;
			}
	    }
	    
	    return sum;
	}
	
	
	private int boardColTransitions(int[][] curArray,int highestY) {
	    int sum = 0;
	    
	    for (int i= 1;i < mTetrisGame.mGameWidth - 1;i++) {
	    	boolean isTrans = true;
	    	for (int j = highestY; j < mTetrisGame.mGameHeight - 1; j++) {
				
	    		if (isTrans) {
					if (curArray[j][i] == TetrisGame.GAME_EMPTY) {
						sum++;
						isTrans = false;
					}
				} else {
					if (curArray[j][i] == TetrisGame.BOX_FIX) {
						sum++;
						isTrans = true;
					}
				}
			}
	    	
	    	if (curArray[mTetrisGame.mGameHeight - 1][i] == TetrisGame.GAME_EMPTY) {
				sum++;
			}
	    }
	    
	    return sum;
	}
	
	/* ********************************************************************************************************
	 *                                        深度搜索模式
	 * *********************************************************************************************************/
	
	/*
	 * 功  能: 策略的思考者(高级脑)
	 *  参   数:
	 *     int firstReleaseLines:  第一个方块消的行数 
	 *     int nextReleaseLines:   第二个方块消的行数
	 *  备  注:
	 *  	在低端脑的基础上,增加空洞数量的维度
	 * */
	private int advanceBrain(int firstReleaseLines,int nextReleaseLines){
		int highestY = mTetrisGame.mGameHeight;   // 最高点
		double avgHight = 0;     // 平均高度
		
		// 用于统计临近的高度差
		int topA = 0;       // 前一列高度
		int topB = 0;       // 后一列高度
		
		int diffHight    = 0;  // 高度差
		int deepPitCount = 0;  // 深坑数
		int mountainCount = 0; // 高山数
		
		int topY = 0;       // 当前列最高点
		int holes = 0;      // 空洞数
		
		for (int i = 1;i < mTetrisGame.mGameWidth-1;i++) {
			
			topA = topB;    
			topB = topY;
			
			// 寻找当前高点
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// 从当前最高点向下寻找空洞数
			for (int j = topY+1; j < mTetrisGame.mGameHeight - 1; j++) {
				if (mNextBoxs[j][i] == TetrisGame.GAME_EMPTY) {
					holes++;
				}
			}
			
			avgHight += (double)topY;
			if (((topB - topA) > 1) && ((topB - topY) > 1)) {
				deepPitCount++;   // 深坑数量+1

			}
			
			if (((topB - topA) < -1) && ((topB - topY) < -1)) {
				mountainCount++;  // 高山数量+1
			}
			
			diffHight += Math.abs(topY -topB);
			
			if (topY < highestY) {
				highestY = topY;
			}
		}
		
		avgHight = avgHight / (mTetrisGame.mGameHeight - 2);
		if (topY - topB > 1) {
			deepPitCount++;
		}
		
		// 计算评分
		int score = highestY*19 + (int)avgHight*15 + firstReleaseLines*2 + nextReleaseLines 
				- holes*7 - deepPitCount*4 - mountainCount*2 - diffHight;
		
		return score;
	}
	
	
	/*
	 * 功  能: 策略的思考者(低端脑)
	 *  参   数:
	 *     int firstReleaseLines:  第一个方块消的行数 
	 *     int nextReleaseLines:   第二个方块消的行数
	 *  备  注:
	 *  	在低端脑的基础上,增加空洞数量的维度
	 * */
	private int mediumBrain(int firstReleaseLines,int nextReleaseLines){
		int highestY = mTetrisGame.mGameHeight;   // 最高点
		int topY = 0;       // 当前列最高点
		int holes = 0;      // 空洞数
		
		for (int i = 1;i < mTetrisGame.mGameWidth-1;i++) {
			// 寻找当前高点
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// 从当前最高点向下寻找空洞数
			for (int j = topY+1; j < mTetrisGame.mGameHeight - 1; j++) {
				if (mNextBoxs[j][i] == TetrisGame.GAME_EMPTY) {
					holes++;
				}
			}
			
			if (topY < highestY) {
				highestY = topY;
			}
		}
		
		return highestY*25 - holes*5 + firstReleaseLines*2 + nextReleaseLines;
	}
	
	/*
	 * 功  能: 策略的思考者(低端脑)
	 *  参   数:
	 *     int firstReleaseLines:  第一个方块消的行数 
	 *     int nextReleaseLines:   第二个方块消的行数
	 *  备   注:
	 *     只考虑了消行数和游戏高度,所以会造成大量空洞
	 * */
	private int lowBrain(int firstReleaseLines,int nextReleaseLines){
		int curHighestY = calcApex(mNextBoxs);
		
		return curHighestY + firstReleaseLines*4 + nextReleaseLines*2;
	}
	
	/*
	 *  功   能: 对当前策略评分
	 *  参   数:
	 *     int firstReleaseLines:  第一个方块消的行数 
	 *     int nextReleaseLines:   第二个方块消的行数
	 * */
	private void evaluation(int firstReleaseLines,int nextReleaseLines) {
		int score = 0;
		
		// 选择评价策略
		switch(mAiLevel) {
		case AI_AMENTIA:
			score = lowBrain(firstReleaseLines,nextReleaseLines);
			break;
		case AI_KID:
			score = mediumBrain(firstReleaseLines,nextReleaseLines);
			break;
		case AI_ADULT:
			score = advanceBrain(firstReleaseLines,nextReleaseLines);
			break;
		}
		
		// 刷新最高分策略
		refreshScore(score);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * 功    能:获取指定方块类型的旋转种类
	 * 参    数:
	 * 	 int boxType: 方块类型
	 * 返回值:
	 *   返回旋转类型数量
	 * */
	public int getRotateCount(int boxType) {
		
		int rotateCount = 0;
		switch (boxType) {
		case 0:
			rotateCount = 2;    // 棍子
			break;
		case 1:
			rotateCount = 1;    // 田
			break;
		case 2:
			rotateCount = 4;    // 右L
			break;
		case 3:
			rotateCount = 4;    // 左L
			break;
		case 4:
			rotateCount = 4;    // T
			break;
		case 5:
			rotateCount = 2;    // s
			break;
		case 6:
			rotateCount = 2;    // z
			break;
		default:
			rotateCount = 0;
			break;
		}
		
		return rotateCount;
	}
	
	/*
	 * 功    能: 设置当前方块的状态
	 * 参    数: 当前方块的坐标,选择类型
	 * 返回值: 无返回值
	 * */
	public void setCurStatus(int x,int y,int rotate) {
		mCurX = x;
		mCurY = y;
		mCurRotate = rotate;
	}
	
	
	/*
	 * 功    能: 模拟消行
	 * 参    数: 
	 *     int nRow: 当前行坐标
	 *     int[][] curArray:当前游戏数组
	 * 返回值: 无返回值
	 * */
	public void releaseLine(int nRow,int[][] curArray){
		for (int i = nRow; i >= 0; i--) {
			for(int j = 1; j < mTetrisGame.mGameWidth - 1; j++) {
				if (i != 0) {
					curArray[i][j] = curArray[i - 1][j];
					continue;
				}
				
				curArray[i][j] = TetrisGame.GAME_EMPTY;
			}
		}
	}
	
	/*
	 * 功    能: 模拟消行
	 * 参    数: 
	 *     int highestY: 当前最高点Y坐标
	 *     int[][] curArray:当前游戏数组
	 * 返回值: 无返回值
	 * */
	public int releaseLines(int[][] curArray,int highestY) {
		int release_count = 0;
		
		for (int nRow = mTetrisGame.mGameHeight - 2; nRow >= highestY; nRow--) {
			if (isCanRelease(nRow,curArray)) {
				releaseLine(nRow,curArray);
				
				nRow++;
				release_count++;
			}
		}
		
		return release_count;
	}
	
	/*
	 * 功    能: 能否消行
	 * 参    数:
	 *     int nRow: 当前行坐标
	 *     int[][] curArray:当前游戏数组
	 * 返回值:
	 * */
	public boolean isCanRelease(int row,int[][] curArray) {
		for (int i = 1;i < mTetrisGame.mGameWidth - 1;i++) {
			if (curArray[row][i] == TetrisGame.GAME_EMPTY) {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * 功    能: 固定方块
	 * 参    数:
	 *   int[][] curArray: 游戏数组
	 *   int boxIndex  方块类型索引
	 *   int x,int y:  坐标
	 * 返回值: (void)
	 * */
	private void fixBoxs(int[][] curArray,int boxIndex,int x,int y) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// 碰撞检测
				if (BoxStatus.box_state[boxIndex][i][j] == TetrisGame.BOX_FIX) {
					curArray[y + i][x + j] = TetrisGame.BOX_FIX;
				}
			}
		}
	}
	
	/*
	 * 功    能: 复制游戏空间
	 * 参    数: 
	 * int[][] srcArray:原空间
	 * int[][] dstArray:目标空间
	 * 返回值: (void)
	 * */
	private void copyGameSpace(int[][] srcArray,int[][] dstArray) {
		for (int i = 0;i < mTetrisGame.mGameHeight;i++) {
			for (int j = 0;j < mTetrisGame.mGameWidth;j++) {
				dstArray[i][j] = srcArray[i][j];
			}
		}
	}
	
	/*
	 * 功    能: 恢复游戏空间
	 * 参    数:
	 *    int[][] srcArray:原游戏空间
	 *    int[][] dstArray:目前游戏空间
	 *    int highestY:当前游戏空间最高点
	 * 返回值:
	 * */
	private void restoreGameSpace(int[][] srcArray,int[][] dstArray,int highestY) {
		int copyY = highestY;
		if (highestY >= 4) {
			copyY = highestY- 4;
		}
		
		// 从最高点向下拷贝 墙体不拷贝
		for (int i = copyY;i < mTetrisGame.mGameHeight - 1;i++) {
			for (int j = 1;j < mTetrisGame.mGameWidth - 1;j++) {
				dstArray[i][j] = srcArray[i][j];
			}
		}
	}
	
	private int fallDown(int[][] curArray,int x,int y,int box_index) {
		while (true) {		
			if (y >= mTetrisGame.mGameHeight) {
				break;
			}
			
			if (!isCanMove(curArray,x, y + 1,box_index)) {
				break;
			}
			
			y++;
		}
		
		// 固定方块到背景上
		fixBoxs(curArray,box_index,x,y);
		
		return y;
	}
	
	// 当前方块能否移动
	public boolean isCanMove(int[][] curArray,int x,int y,int box_index) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// 碰撞检测
				if (BoxStatus.box_state[box_index][i][j] == TetrisGame.BOX_FIX) {
					if ((curArray[y + i][x + j] == TetrisGame.BOX_FIX) || 
						(curArray[y + i][x + j] == TetrisGame.GAME_WALL) ) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	// 当前行是否是空行
	private boolean isBlankLine(int[][] curArray,int row) {
		for (int i = 1;i < mTetrisGame.mGameWidth;i++) {
			if (curArray[row][i] == TetrisGame.BOX_FIX) {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * 功    能: 计算游戏最高点所在纵坐标
	 * 参    数: 
	 *    int[][] curArray:当前游戏数组
	 * 返回值:
	 *    返回当前最高点Y坐标
	 * */
	private int calcApex(int[][] curArray) {
		int apex = 0;
		
		for (int nRow = mTetrisGame.mGameHeight - 2; nRow >= 0; nRow--) {
			if (isBlankLine(curArray,nRow)) {
				apex = nRow;
				break;
			}
		}
		
		return apex;
	}
	
	/*
	 * 功    能: 模糊化最高点Y坐标(考虑下落后高度的变化)
	 * 参    数: 
	 *    int hightestY: 最高点Y坐标
	 * 返回值:
	 *    返回当前最高点Y坐标
	 * */
	private int dimApex(int hightestY) {
		if (hightestY > 4) {
			return hightestY - 4;
		}
		
		return 0;
	}
	
	// 计算当前方块索引
	public int calcBoxIndex(int boxType,int boxRotate) {
		return boxType * 4 + boxRotate;
	}

	public boolean isFind() {
		return isFind;
	}

	public void setFind(boolean isFind) {
		this.isFind = isFind;
	}

	public int getBestX() {
		return mBestX;
	}

	public void setBestX(int mBestX) {
		this.mBestX = mBestX;
	}

	public int getBestY() {
		return mBestY;
	}

	public void setBestY(int mBestY) {
		this.mBestY = mBestY;
	}

	public int getBestType() {
		return mBestType;
	}

	public void setBestType(int mBestType) {
		this.mBestType = mBestType;
	}

	public int getBestRotate() {
		return mBestRotate;
	}

	public void setBestRotate(int mBestRotate) {
		this.mBestRotate = mBestRotate;
	}
	
}
