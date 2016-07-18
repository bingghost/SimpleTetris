package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.common.LogUtil;
import com.bingghost.simpletetris.db.DBScore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaveScore extends Activity {
	
	EditText mEditScore = null;
	int score = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_score_info);
		
		init();
	}
	
	private void AddScore(String name,int score) {
		DBScore db = new DBScore(this);
		
		db.addScore(name, score);
	}
	
	private void buttonEvent() {
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = mEditScore.getText().toString();
				if (name == null || "".equals(name)) {
					LogUtil.v(" null input");
					return;
				}
				
				LogUtil.v(name + ":" + score);
				AddScore(name,score);
				finish();
			}
		});
	}
	
	private void init() {
		mEditScore = (EditText) findViewById(R.id.editTextName);
		
		setScoreInfo();
		buttonEvent();
	}
	
	private void setScoreInfo() {
		Intent intent = getIntent();
		score = intent.getIntExtra("cur_score", 0);
		
		TextView txtScore = (TextView) findViewById(R.id.txtScore);
		String str_score = "ÄúµÄÕ½¼¨ÊÇ:" + score;
		txtScore.setText(str_score);
	}

}
