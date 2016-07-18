package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.astar.AStar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity implements android.view.View.OnClickListener {
	
	public AStar mAStart = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.menu_main);
		
		Button btnStartGame = (Button)findViewById(R.id.btn_start_game);
		btnStartGame.setOnClickListener(this);
		
		Button btnAStar = (Button)findViewById(R.id.btn_astar);
		btnAStar.setOnClickListener(this);
		
		Button btnConfig = (Button) findViewById(R.id.btn_game_set);
		btnConfig.setOnClickListener(this);
		
		Button btnScoreList = (Button) findViewById(R.id.btn_score_list);
		btnScoreList.setOnClickListener(this);
	
	}
	
	private void startGame() {
		Intent intent = new Intent();
		intent.setClass(MenuActivity.this, GameActivity.class);
		startActivity(intent);
	}
	
	private void startAStar() {
		Intent intent = new Intent();
		intent.setClass(MenuActivity.this, AStarActivity.class);
		startActivity(intent);
	}
	
	private void startGameSet() {
		Intent intent = new Intent(MenuActivity.this,ConfigActivity.class);
		startActivity(intent);
	}
	
	private void startScoreList() {
		Intent intent = new Intent(MenuActivity.this,ScoreList.class);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start_game:
			startGame();
			break;
		case R.id.btn_astar:
			startAStar();
			break;
		case R.id.btn_game_set:
			startGameSet();
			break;
		case R.id.btn_score_list:
			startScoreList();
			break;
		}
	}

}
