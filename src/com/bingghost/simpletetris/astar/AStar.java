package com.bingghost.simpletetris.astar;

import android.annotation.SuppressLint;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.math.BigDecimal;

public class AStar {
	public static final int ASTAR_BLANK      = 0;     // �հ� ��Ѱ·
	public static final int ASTAR_ROADBLOCK  = 1;     // ·��
	public static final int ASTAR_STARTPOINT = 2;     // ��ʼ��
	public static final int ASTAR_ENDPOINT   = 3;     // �յ�
	public static final int ASTAR_ROADLINE   = 4;     // ���·��
	
	public static final int ASTAR_ROADBLOCK_NUM  = 300;     // ·������
	
	private int mAStarWidth  = 0;
    private int mAStarHeight = 0;
	
	private int mStartX = 0;
	private int mStartY = 0;
	private int mEndX   = 0;
	private int mEndY   = 0;
	
	private Queue<AStarPoint> mOpenSet;
	private Map<Integer,AStarPoint> mOpenMap;
	private Map<Integer,AStarPoint> mCloseMap;
	
	private boolean mIsFind = false;
	
	private AStarPoint mStartPoint = null;
	private AStarPoint mEndPoint = null;
	
	private int[][] mBgArray = null;
	
	public AStar(int aStarWidth,int aStarHeight) {
		setAStarWidth(aStarWidth);
		setAStarHeight(aStarHeight);
		
		createMap();
	}
	
	public void createMap() {
		setIsFind(false);
		
		// ��ʼ���߽�
		initEdge();
		
		// �����ʼ��·��
		initRoadBlock();
		
		// ������ʼ����յ�
		initStartPoint();
		initEndPoint();
	}
	
	private void initStartPoint() {
		while (true) {
			int x = (int)(1 + Math.random()* (getAStarWidth() - 1));
			int y = (int)(1 + Math.random()* (getAStarHeight() - 1));
			
			if (mBgArray[y][x] == ASTAR_BLANK) {
				mStartX = x;
				mStartY = y;
				mBgArray[y][x] = ASTAR_STARTPOINT;
				break;
			}
		}
	}
	
	public static Comparator<AStarPoint> pointComparator = new Comparator<AStarPoint>() {

		@Override
		public int compare(AStarPoint lhs, AStarPoint rhs) {
			
			BigDecimal src = new BigDecimal(lhs.getScore());
			BigDecimal des = new BigDecimal(rhs.getScore());
			
			int ret = src.compareTo(des);
			if (ret < 0) {
				return 1;
			}
			
			if (ret > 0) {
				return -1;
			}
			
			
			return 0;
		}
		
	};
	
    @SuppressLint("UseSparseArrays")
	private void initDataStruct() {
    	mOpenSet = new PriorityQueue<AStarPoint>(15,pointComparator);
    	mOpenMap = new HashMap<Integer, AStarPoint>();
    	setCloseMap(new HashMap<Integer, AStarPoint>());
    }
    
    private void calcStartToEndPath() {


		
		int x = mEndPoint.getPrevPointX();
		int y = mEndPoint.getPrevPointY();
		int nextPointX = mEndPoint.getX();
		int nextPointY = mEndPoint.getY();
		AStarPoint tmpPoint = new AStarPoint(x, y);
		
		while (true) {
			
			tmpPoint.setX(x);
			tmpPoint.setY(y);
			tmpPoint.calcKey();
			AStarPoint curPoint = tmpPoint;
			curPoint = mCloseMap.get(curPoint.getKey());
			
			curPoint.setNextPoint(nextPointX, nextPointY);
			
			if (mStartPoint.getKey() == curPoint.getKey()) {
				break;
			}
			
			nextPointX = curPoint.getX();
			nextPointY = curPoint.getY();
			x = curPoint.getPrevPointX();
			y = curPoint.getPrevPointY();
		}
    }
	
	public void FindPath(){
		if (IsFind()) {
			return;
		}
		
		initDataStruct();
		
		setStartPoint(new AStarPoint(mStartX,mStartY));
		setEndPoint(new AStarPoint(mEndX,mEndY));
		
		mOpenSet.add(getStartPoint());
		mOpenMap.put(getStartPoint().getKey(), getStartPoint());
		
		while (true) {
			// ���ȶ��п���ֹͣ
			if (mOpenSet.isEmpty()) {
				break;
			}
			
			// ���ȼ��ߵĳ���
			AStarPoint curPoint = mOpenSet.poll();
			AStarPoint childPoint = null;
			mOpenMap.remove(curPoint.getKey());
			
			// Ѱ·���յ� �õ����·��
			if (curPoint.getKey() == getEndPoint().getKey()) {
				getEndPoint().setPrevPoint(curPoint.getPrevPointX(),curPoint.getPrevPointY());
				
				// ����������·��
				calcStartToEndPath();
				setIsFind(true);
				break;
			}
			
			int x = curPoint.getX();
			int y = curPoint.getY();
			
			// ��������4������������·��
			for (int i = -1;i <= 1;i++) {
				for (int j = -1;j <= 1;j++) {
					// �����Ǻ�ԭ������(ֻ����������Ѱ·)
					if (((Math.abs(i) == 1) && (Math.abs(j) == 1)) ||
						((i == 0) && (j == 0))){
						continue;
					}
					
					// �߽���
					if (((x + i) < 0) || ((y + j) < 0) ||
						((x + i) >= getAStarWidth()) || 
						((y + j) >= getAStarHeight())) {
						continue;
					}
					
					// ǽ����
					if (mBgArray[y + j][x + i] == ASTAR_ROADBLOCK) {
						continue;
					}
					
					//  ����
					childPoint = new AStarPoint(x + i,y + j);
					childPoint.calcScore(getEndPoint());
					
					// �򿪱�͹رձ���û�и�Ԫ��
					AStarPoint tmpOpenPoint = mOpenMap.get(childPoint.getKey());
					AStarPoint tmpClosePoint = getCloseMap().get(childPoint.getKey());
					if ((tmpOpenPoint == null) && (tmpClosePoint) == null){
						childPoint.setStepFromSrc(curPoint.getStepFromSrc() + 1);
						
						childPoint.setPrevPoint(curPoint.getX(),curPoint.getY());
						
						mOpenSet.add(childPoint);
						mOpenMap.put(childPoint.getKey(), childPoint);
					} else if (tmpOpenPoint != null) {
						if ((curPoint.getStepFromSrc() + 1) < tmpOpenPoint.getStepFromSrc()) {
							tmpOpenPoint.setStepFromSrc(curPoint.getStepFromSrc() + 1);
						}
					} else if (tmpClosePoint != null) {
						if ((curPoint.getStepFromSrc() + 1) < tmpClosePoint.getStepFromSrc()) {
							tmpClosePoint.setStepFromSrc(curPoint.getStepFromSrc() + 1);
							
							getCloseMap().remove(childPoint.getKey());
							
							mOpenMap.put(tmpClosePoint.getKey(), tmpClosePoint);
							mOpenSet.add(tmpClosePoint);
						}
					}
				}
			}
			
			// �ҵ����ڵ�ǰ�����ϵ���ѵ�
			getCloseMap().put(curPoint.getKey(), curPoint);
		}
		
		
	}
	
	private void initEndPoint() {
		while (true) {
			int x = (int)(1 + Math.random()* (getAStarWidth() - 1));
			int y = (int)(1 + Math.random()* (getAStarHeight() - 1));
			
			if (mBgArray[y][x] == ASTAR_BLANK) {
				mEndX = x;
				mEndY = y;
				mBgArray[y][x] = ASTAR_ENDPOINT;
				break;
			}
		}
	}
	
	private void initRoadBlock() {
		for (int i= 0;i < ASTAR_ROADBLOCK_NUM;i++) {
			int x = (int)(1 + Math.random()* (getAStarWidth() - 1));
			int y = (int)(1 + Math.random()* (getAStarHeight() - 1));
			
			mBgArray[y][x] = ASTAR_ROADBLOCK;
		}
	}
	
	private void initEdge() {
		setBgArray(new int[getAStarHeight()][getAStarWidth()]);
		
		for (int i = 0;i < getAStarHeight();i++) {
			for (int j = 0;j < getAStarWidth();j++) {
				if ((j == 0) || (j == (getAStarWidth() - 1)) || 
				    (i == 0) || (i == (getAStarHeight() - 1))) {
					mBgArray[i][j] = 1;
					continue;
				}
				
				mBgArray[i][j] = 0;
			}
		}
	}

	public int[][] getBgArray() {
		return mBgArray;
	}

	public void setBgArray(int[][] mBgArray) {		
		this.mBgArray = mBgArray;
	}

	public int getStartX() {
		return mStartX;
	}

	public void setStartX(int mStartX) {
		this.mStartX = mStartX;
	}

	public int getStartY() {
		return mStartY;
	}

	public void setStartY(int mStartY) {
		this.mStartY = mStartY;
	}

	public int getEndX() {
		return mEndX;
	}

	public void setEndX(int mEndX) {
		this.mEndX = mEndX;
	}

	public int getEndY() {
		return mEndY;
	}

	public void setEndY(int mEndY) {
		this.mEndY = mEndY;
	}

	public boolean IsFind() {
		return mIsFind;
	}

	public void setIsFind(boolean mIsFind) {
		this.mIsFind = mIsFind;
	}

	public Map<Integer,AStarPoint> getCloseMap() {
		return mCloseMap;
	}

	public void setCloseMap(Map<Integer,AStarPoint> mCloseMap) {
		this.mCloseMap = mCloseMap;
	}

	public AStarPoint getEndPoint() {
		return mEndPoint;
	}

	public void setEndPoint(AStarPoint mEndPoint) {
		this.mEndPoint = mEndPoint;
	}

	public AStarPoint getStartPoint() {
		return mStartPoint;
	}

	public void setStartPoint(AStarPoint mStartPoint) {
		this.mStartPoint = mStartPoint;
	}

	public int getAStarHeight() {
		return mAStarHeight;
	}

	public void setAStarHeight(int mAStarHeight) {
		this.mAStarHeight = mAStarHeight;
	}

	public int getAStarWidth() {
		return mAStarWidth;
	}

	public void setAStarWidth(int mAStarWidth) {
		this.mAStarWidth = mAStarWidth;
	}
	
	
	
}
