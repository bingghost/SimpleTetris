package com.bingghost.simpletetris.game;

public class TetrisAI {
	
	public static final int AI_AMENTIA = 0;
	public static final int AI_KID     = 1;
	public static final int AI_ADULT   = 2;
	public static final int AI_GENIUS  = 3;
	
	private TetrisGame mTetrisGame;
	private int mAiLevel = 2;
	
	// ����ȱ�
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
	

	private boolean isFind = false;            // AI �Ƿ��ҵ�
	private int[][] mCopyArray;                // ��������
	private int[][] mFirstBoxs;                // ��һ���������������
	private int[][] mNextBoxs;                 // �ڶ����������������
	
	private int mGameY = 0;                    // ��Ϸ�е�Y����
	private int mNextType = 0;
	private int mCurX = 0;                     // ��ǰ������X Y
	private int mCurY = 0;
	private int mCurRotate = 0;                // ��ǰ����
	private int mCurType = 0;
	
	private int mBestX = 0;                    // ����X Y
	private int mBestY = 0;
	private int mBestRotate = 0;
	private int mBestType   = 0;
	private int mBestScore  = 0;               // ������߷� 
	
	public TetrisAI(TetrisGame tetrisGame,int aiLevel) {
		mTetrisGame = tetrisGame;
		mAiLevel = aiLevel;
		
		mCopyArray = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
		mFirstBoxs = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
		mNextBoxs = new int[mTetrisGame.mGameHeight][mTetrisGame.mGameWidth];
	}

	// ���״̬����˼��
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
		// �����һ��״̬
		cleanStatus();
		
		// ֱ���ж��ܷ��ƶ� �����ƶ����޲���
		if (!isCanMove(mTetrisGame.mGameBoxs, 
				      mTetrisGame.getCurX(), 
				      mTetrisGame.getCurY(), 
				      mTetrisGame.getBoxIndex())) {
			return;
		}
		
		// ������Ϸ�ռ�
		copyGameSpace(mTetrisGame.mGameBoxs,mCopyArray);
		copyGameSpace(mTetrisGame.mGameBoxs,mFirstBoxs);
		copyGameSpace(mTetrisGame.mGameBoxs,mNextBoxs);
		
		// ������ѵ�
		seekBestStrategy();
	}
	
	/*
	 * ��  ��: ˢ���������
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
	 * ��  ��: Ѱ����һ���������Ų��� 
	 * */
	public void seekNextStrategy(int prevReleaseLine) {
		// ��ȡ��ߵ�
		int highestY = calcApex(mFirstBoxs);
		highestY = dimApex(highestY);
		
		// copy box
		restoreGameSpace(mFirstBoxs,mNextBoxs,highestY);
		
		// ����������ڵ����
		for (int rotate = 0;rotate < getRotateCount(mNextType);rotate++) {
			for (int i = 0;i < mTetrisGame.mGameWidth - 2;i++) {
				int boxIndex = calcBoxIndex(mNextType,rotate);
				if (!isCanMove(mNextBoxs, i, 0, boxIndex)) {
					continue;
				}
				
				fallDown(mNextBoxs,i,0,boxIndex);
				
				// ����
				int curReleaseCount = releaseLines(mNextBoxs,highestY); 
				
				// ����
				evaluation(prevReleaseLine,curReleaseCount);
				
				// �ָ���������״̬(��ߵ�����copy)
				restoreGameSpace(mFirstBoxs,mNextBoxs,highestY);
			}
		}
		
	}
	
	/*
	 * ��    ��: Ѱ�����Ž�
	 * ��    ��: �޲���
	 * ����ֵ: �޲���
	 * */
	public void seekBestStrategy() {
		// ��ȡ��ߵ�
		int highestY = calcApex(mCopyArray);
		highestY = dimApex(highestY);
		
		// ����������ڵ����
		for (int rotate = 0;rotate < getRotateCount(mCurType);rotate++) {
			for (int i = 0;i < mTetrisGame.mGameWidth - 2;i++) {
				int boxIndex = calcBoxIndex(mCurType,rotate);
				if (!isCanMove(mFirstBoxs, i, mGameY, boxIndex)) {
					continue;
				}
				
				// ����
				int curY = fallDown(mFirstBoxs,i,mGameY,boxIndex); 
				
				// ���õ�ǰ״̬
				setCurStatus(i,curY,rotate);
				
				// ����
				int releaseCount = releaseLines(mFirstBoxs,highestY);
				
				// ѡ��ָ���Ĳ��Խ���AI
				chooseStrategy(boxIndex,releaseCount);
				
				// �ָ���������״̬(��ߵ�����copy)
				restoreGameSpace(mCopyArray,mFirstBoxs,highestY);
			}
		}
		
		// �ҵ�һ����ѵ�
		setFind(true);
	}
	
	private void chooseStrategy(int boxIndex,int releaseLines){
		while (true) {
			if (mAiLevel == 3) {
				PierreDellacherie(boxIndex,releaseLines);
				break;
			}
			
			// ������2�鷽������
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
	    // ��0�еľ�s
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
	    
	    // ���һ�еľ�
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
	    
	    
	    // �м��еľ�
	    for (int c = 2; c < mTetrisGame.mGameWidth-2; c++) {
	        for (int r = highestY; r < mTetrisGame.mGameHeight; r++) {
	        	if (curArray[r][c] == TetrisGame.GAME_EMPTY) {
	                // ���߾��з���
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
			
			// Ѱ�ҵ�ǰ�ߵ�
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// �ӵ�ǰ��ߵ�����Ѱ�ҿն���
			for (int j = topY+1; j < mTetrisGame.mGameHeight - 1; j++) {
				if (mNextBoxs[j][i] == TetrisGame.GAME_EMPTY) {
					sum++;
				}
			}
		}
	    
	    return sum;
	}
	
	private int landingHeight(int boxIndex) {
	    // ���߶�
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
	    
	    // ��С�߶�
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
		int cellCount = 0;  // ��������ͳ��
	    for (int i = 0;i < 4;i++) {
	    	for (int j = 0; j < 4; j++) {
				// ��ײ���
				if (BoxStatus.box_state[boxIndex][i][j] == TetrisGame.BOX_FIX) {
					if (curArray[y+i][x+j] == TetrisGame.GAME_EMPTY) {
						cellCount--;
					}
				}
			}
	    }
		
		return releaseLines*cellCount;
	}
	
	// �����б任
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
	 *                                        �������ģʽ
	 * *********************************************************************************************************/
	
	/*
	 * ��  ��: ���Ե�˼����(�߼���)
	 *  ��   ��:
	 *     int firstReleaseLines:  ��һ�������������� 
	 *     int nextReleaseLines:   �ڶ���������������
	 *  ��  ע:
	 *  	�ڵͶ��ԵĻ�����,���ӿն�������ά��
	 * */
	private int advanceBrain(int firstReleaseLines,int nextReleaseLines){
		int highestY = mTetrisGame.mGameHeight;   // ��ߵ�
		double avgHight = 0;     // ƽ���߶�
		
		// ����ͳ���ٽ��ĸ߶Ȳ�
		int topA = 0;       // ǰһ�и߶�
		int topB = 0;       // ��һ�и߶�
		
		int diffHight    = 0;  // �߶Ȳ�
		int deepPitCount = 0;  // �����
		int mountainCount = 0; // ��ɽ��
		
		int topY = 0;       // ��ǰ����ߵ�
		int holes = 0;      // �ն���
		
		for (int i = 1;i < mTetrisGame.mGameWidth-1;i++) {
			
			topA = topB;    
			topB = topY;
			
			// Ѱ�ҵ�ǰ�ߵ�
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// �ӵ�ǰ��ߵ�����Ѱ�ҿն���
			for (int j = topY+1; j < mTetrisGame.mGameHeight - 1; j++) {
				if (mNextBoxs[j][i] == TetrisGame.GAME_EMPTY) {
					holes++;
				}
			}
			
			avgHight += (double)topY;
			if (((topB - topA) > 1) && ((topB - topY) > 1)) {
				deepPitCount++;   // �������+1

			}
			
			if (((topB - topA) < -1) && ((topB - topY) < -1)) {
				mountainCount++;  // ��ɽ����+1
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
		
		// ��������
		int score = highestY*19 + (int)avgHight*15 + firstReleaseLines*2 + nextReleaseLines 
				- holes*7 - deepPitCount*4 - mountainCount*2 - diffHight;
		
		return score;
	}
	
	
	/*
	 * ��  ��: ���Ե�˼����(�Ͷ���)
	 *  ��   ��:
	 *     int firstReleaseLines:  ��һ�������������� 
	 *     int nextReleaseLines:   �ڶ���������������
	 *  ��  ע:
	 *  	�ڵͶ��ԵĻ�����,���ӿն�������ά��
	 * */
	private int mediumBrain(int firstReleaseLines,int nextReleaseLines){
		int highestY = mTetrisGame.mGameHeight;   // ��ߵ�
		int topY = 0;       // ��ǰ����ߵ�
		int holes = 0;      // �ն���
		
		for (int i = 1;i < mTetrisGame.mGameWidth-1;i++) {
			// Ѱ�ҵ�ǰ�ߵ�
			for (topY = 0; topY < mTetrisGame.mGameHeight - 1; topY++) {
				if (mNextBoxs[topY][i] == TetrisGame.BOX_FIX) {
					break;
				}
			}
			
			// �ӵ�ǰ��ߵ�����Ѱ�ҿն���
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
	 * ��  ��: ���Ե�˼����(�Ͷ���)
	 *  ��   ��:
	 *     int firstReleaseLines:  ��һ�������������� 
	 *     int nextReleaseLines:   �ڶ���������������
	 *  ��   ע:
	 *     ֻ����������������Ϸ�߶�,���Ի���ɴ����ն�
	 * */
	private int lowBrain(int firstReleaseLines,int nextReleaseLines){
		int curHighestY = calcApex(mNextBoxs);
		
		return curHighestY + firstReleaseLines*4 + nextReleaseLines*2;
	}
	
	/*
	 *  ��   ��: �Ե�ǰ��������
	 *  ��   ��:
	 *     int firstReleaseLines:  ��һ�������������� 
	 *     int nextReleaseLines:   �ڶ���������������
	 * */
	private void evaluation(int firstReleaseLines,int nextReleaseLines) {
		int score = 0;
		
		// ѡ�����۲���
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
		
		// ˢ����߷ֲ���
		refreshScore(score);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * ��    ��:��ȡָ���������͵���ת����
	 * ��    ��:
	 * 	 int boxType: ��������
	 * ����ֵ:
	 *   ������ת��������
	 * */
	public int getRotateCount(int boxType) {
		
		int rotateCount = 0;
		switch (boxType) {
		case 0:
			rotateCount = 2;    // ����
			break;
		case 1:
			rotateCount = 1;    // ��
			break;
		case 2:
			rotateCount = 4;    // ��L
			break;
		case 3:
			rotateCount = 4;    // ��L
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
	 * ��    ��: ���õ�ǰ�����״̬
	 * ��    ��: ��ǰ���������,ѡ������
	 * ����ֵ: �޷���ֵ
	 * */
	public void setCurStatus(int x,int y,int rotate) {
		mCurX = x;
		mCurY = y;
		mCurRotate = rotate;
	}
	
	
	/*
	 * ��    ��: ģ������
	 * ��    ��: 
	 *     int nRow: ��ǰ������
	 *     int[][] curArray:��ǰ��Ϸ����
	 * ����ֵ: �޷���ֵ
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
	 * ��    ��: ģ������
	 * ��    ��: 
	 *     int highestY: ��ǰ��ߵ�Y����
	 *     int[][] curArray:��ǰ��Ϸ����
	 * ����ֵ: �޷���ֵ
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
	 * ��    ��: �ܷ�����
	 * ��    ��:
	 *     int nRow: ��ǰ������
	 *     int[][] curArray:��ǰ��Ϸ����
	 * ����ֵ:
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
	 * ��    ��: �̶�����
	 * ��    ��:
	 *   int[][] curArray: ��Ϸ����
	 *   int boxIndex  ������������
	 *   int x,int y:  ����
	 * ����ֵ: (void)
	 * */
	private void fixBoxs(int[][] curArray,int boxIndex,int x,int y) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// ��ײ���
				if (BoxStatus.box_state[boxIndex][i][j] == TetrisGame.BOX_FIX) {
					curArray[y + i][x + j] = TetrisGame.BOX_FIX;
				}
			}
		}
	}
	
	/*
	 * ��    ��: ������Ϸ�ռ�
	 * ��    ��: 
	 * int[][] srcArray:ԭ�ռ�
	 * int[][] dstArray:Ŀ��ռ�
	 * ����ֵ: (void)
	 * */
	private void copyGameSpace(int[][] srcArray,int[][] dstArray) {
		for (int i = 0;i < mTetrisGame.mGameHeight;i++) {
			for (int j = 0;j < mTetrisGame.mGameWidth;j++) {
				dstArray[i][j] = srcArray[i][j];
			}
		}
	}
	
	/*
	 * ��    ��: �ָ���Ϸ�ռ�
	 * ��    ��:
	 *    int[][] srcArray:ԭ��Ϸ�ռ�
	 *    int[][] dstArray:Ŀǰ��Ϸ�ռ�
	 *    int highestY:��ǰ��Ϸ�ռ���ߵ�
	 * ����ֵ:
	 * */
	private void restoreGameSpace(int[][] srcArray,int[][] dstArray,int highestY) {
		int copyY = highestY;
		if (highestY >= 4) {
			copyY = highestY- 4;
		}
		
		// ����ߵ����¿��� ǽ�岻����
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
		
		// �̶����鵽������
		fixBoxs(curArray,box_index,x,y);
		
		return y;
	}
	
	// ��ǰ�����ܷ��ƶ�
	public boolean isCanMove(int[][] curArray,int x,int y,int box_index) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// ��ײ���
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
	
	// ��ǰ���Ƿ��ǿ���
	private boolean isBlankLine(int[][] curArray,int row) {
		for (int i = 1;i < mTetrisGame.mGameWidth;i++) {
			if (curArray[row][i] == TetrisGame.BOX_FIX) {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * ��    ��: ������Ϸ��ߵ�����������
	 * ��    ��: 
	 *    int[][] curArray:��ǰ��Ϸ����
	 * ����ֵ:
	 *    ���ص�ǰ��ߵ�Y����
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
	 * ��    ��: ģ������ߵ�Y����(���������߶ȵı仯)
	 * ��    ��: 
	 *    int hightestY: ��ߵ�Y����
	 * ����ֵ:
	 *    ���ص�ǰ��ߵ�Y����
	 * */
	private int dimApex(int hightestY) {
		if (hightestY > 4) {
			return hightestY - 4;
		}
		
		return 0;
	}
	
	// ���㵱ǰ��������
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
