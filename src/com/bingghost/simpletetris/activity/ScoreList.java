package com.bingghost.simpletetris.activity;

import java.util.ArrayList;
import java.util.List;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.db.DBScore;
import com.bingghost.simpletetris.db.ScoreInfo;
import com.bingghost.simpletetris.db.ScoreInfoAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

public class ScoreList extends BaseActivity {
	
	private List<ScoreInfo> listScoreInfo = new ArrayList<ScoreInfo>();
	private ScoreInfoAdapter scoreInfoAdapter = null;
	private ListView listViewScore = null;
	private DBScore db = null; 
	private int cur_db_id = 0;
	Context mContext;
	int chooseIndex = ListView.INVALID_POSITION;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_list);
		
		mContext = this;
		
		init();
	}
	
	private void init() {
		initScoreDB();
		getScoreList();
		initScoreList();
		initCustomMenu();
	}
	
	private void initCustomMenu() {
		ListView listView = (ListView) findViewById(R.id.listScore);
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				ScoreInfo scoreInfo = listScoreInfo.get(arg2);
				cur_db_id = scoreInfo.getID();
				chooseIndex = arg2;
				popWindow(arg1);
				return false;
			}
		});
	}
	
	private void initScoreDB() {
		db = new DBScore(this);
	}
	
	private void getScoreList() {
		if (listScoreInfo == null) {
			return;
		}
		
		if (db == null) {
			return;
		}
		
		db.queryInfo(listScoreInfo);
	}
	
	private void initScoreList() {
		scoreInfoAdapter = new ScoreInfoAdapter(ScoreList.this, R.layout.listview_item, listScoreInfo);
		listViewScore = (ListView) findViewById(R.id.listScore);
		listViewScore.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewScore.setAdapter(scoreInfoAdapter);
	}
	
	private void delScore(int id) {
		if (db == null) {
			return;
		}
		
		db.delScore(id);
	}
	
	@SuppressLint("InflateParams")
	private void popWindow(View view) {
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.simple_menu, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		// 删除按钮点击事件
		Button btnDelItem = (Button) contentView.findViewById(R.id.btnDelItem);
		btnDelItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (popupWindow.isShowing()) {
					if (chooseIndex == ListView.INVALID_POSITION) {
						return;
					}
					
					delScore(cur_db_id);
					listScoreInfo.remove(chooseIndex);
					scoreInfoAdapter.notifyDataSetChanged();
					popupWindow.dismiss();
				}
				
			}
		});

		contentView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		
		// 计算出现在屏幕的位置
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		
		int view_width = view.getWidth();
		btnDelItem.setWidth(view_width / 6);
		
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
	    int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
	    btnDelItem.measure(w, h);  
	    int item_width =btnDelItem.getMeasuredWidth();  

		int x = view_width / 2 - item_width / 2;
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, location[1] - popupWindow.getHeight());
		popupWindow.update();
	}
}
