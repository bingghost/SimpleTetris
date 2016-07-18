package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.common.LogUtil;
import com.bingghost.simpletetris.config.AppConfig;
import com.bingghost.simpletetris.game.TetrisAI;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ToggleButton;


public class ConfigActivity extends BaseActivity {
	
	int[] ai_levels = new int[] {
			TetrisAI.AI_AMENTIA,
			TetrisAI.AI_KID,
			TetrisAI.AI_ADULT,
			TetrisAI.AI_GENIUS
	};
	
	int[] ai_speeds = new int[] {
			200,400,600,800
	};
	
	int[] game_levels = new int[] {
			1,2,3,4,5,6
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting);
		
		init();

	}
	
	private void init() {
		initToggleButton();
		
		initSpinnerAiLevel();
		
		initSpinnerAiSpeed();
		
		initSpinnerGameLevel();
		
	}
	
	private int calcAiLevelPosition(int aiLevel) {
		int position = 0;
		switch (aiLevel) {
		
		case TetrisAI.AI_AMENTIA:
			position = 0;
			break;
		case TetrisAI.AI_KID:
			position = 1;
			break;
		case TetrisAI.AI_ADULT:
			position = 2;
			break;
		case TetrisAI.AI_GENIUS:
			position = 3;
			break;
		default:
			break;
		}
		
		return position;
	}
	
	private void initSpinnerAiLevel() {
		Spinner spinner = (Spinner)findViewById(R.id.spAiLevel);
		
		int aiLevel = AppConfig.getAiLevel();
		int position = calcAiLevelPosition(aiLevel);
		
		String[] items = new String[] {
			"脑残","简单","天才1","天才2"	
		};
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(position,true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	int choose_ai_level = ai_levels[pos];
		    	AppConfig.setAiLevel(ConfigActivity.this, choose_ai_level);
		    }
		    
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {

		    }
		});
	}
	
	private int calcAiSpeedPosition(int aiSpeed) {
		int position = 0;
		
		switch (aiSpeed) {
		case 200:
			position = 0;
			break;
		case 400:
			position = 1;
			break;
		case 600:
			position = 2;
			break;
		case 800:
			position = 3;
			break;
		default:
			break;
		}
		
		return position;
	}
	
	private void initSpinnerAiSpeed() {
		Spinner spinner = (Spinner)findViewById(R.id.spAiSpeed);
		String[] items = new String[] {
			"飞快","快","慢","很慢"	
		};
		
		int aiSpeed = AppConfig.getAiSpeed();
		int position = calcAiSpeedPosition(aiSpeed);
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(position,true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	int choose_ai_speed = ai_speeds[pos];
		    	AppConfig.setAiSpeed(ConfigActivity.this, choose_ai_speed);
		    }
		    
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {

		    }
		});
	}
	
	private int calcGameLevelPosition(int gameLevel) {
		int position = 0;
		
		switch (gameLevel) {
		case 1:
			position = 0;
			break;
		case 2:
			position = 1;
			break;
		case 3:
			position = 2;
			break;
		case 4:
			position = 3;
			break;
		case 5:
			position = 4;
			break;
		case 6:
			position = 5;
			break;
		default:
			break;
		}
		
		return position;
	}
	
	private void initSpinnerGameLevel() {
		Spinner spinner = (Spinner)findViewById(R.id.spGameLevel);
		String[] items = new String[] {
			"脑残","婴儿","小孩","成人","变态","天才"	
		};
		
		int gameLevel = AppConfig.getGameLevel();
		int position = calcGameLevelPosition(gameLevel);
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(position,true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	LogUtil.d("ItemSelected:" + pos);
		    	int choose_game_level = game_levels[pos];
		    	AppConfig.setGameLevel(ConfigActivity.this, choose_game_level);
		    }
		    
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {

		    }
		});
	}
	
	private void initToggleButton() {
		ToggleButton btnSoundSwitch = (ToggleButton) findViewById(R.id.tbtnSoundSwitch);
		btnSoundSwitch.setChecked(AppConfig.isSoundSwitch());
		btnSoundSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppConfig.setSoundSwitch(!AppConfig.isSoundSwitch(), ConfigActivity.this);
			}
		});
		
		ToggleButton btnMusicSwitch = (ToggleButton) findViewById(R.id.tbtnMusicSwitch);
		btnMusicSwitch.setChecked(AppConfig.isMusicSwitch());
		btnMusicSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppConfig.setMusicSwitch(!AppConfig.isMusicSwitch(), ConfigActivity.this);
			}
		});
	}
}
