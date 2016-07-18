package com.bingghost.simpletetris.config;

import com.bingghost.simpletetris.game.TetrisAI;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
	public static final String CONF_FILE_NAME = "config";
	public static final String AI_LEVEL = "ai_level";
	public static final String AI_SPEED = "ai_speed";
	public static final String GAME_LEVEL = "game_level";
	public static final String SOUND_SWITCH = "sound_switch";
	public static final String MUSIC_SWITCH = "music_switch";
	
	private static int aiLevel = TetrisAI.AI_ADULT;  // ai级别
	private static int aiSpeed = 200;                // ai速度
	private static int gameLevel = 0;                // 游戏难度
	private static int aiControlModel = 0;           // ai操作模式   暂不实现
	private static boolean soundSwitch = true;       // 音效开关
	private static boolean musicSwitch = true;       // 音乐开关
	
	public static void readConfig(Context context){
		init();
		
		if (context == null) {
			return;
		}
		
		SharedPreferences pref = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE);
		aiLevel = pref.getInt(AI_LEVEL, TetrisAI.AI_ADULT);
		aiSpeed = pref.getInt(AI_SPEED, 200);
		gameLevel = pref.getInt(GAME_LEVEL, 6);
		soundSwitch = pref.getBoolean(SOUND_SWITCH, true);
		musicSwitch = pref.getBoolean(MUSIC_SWITCH, true);


	}
	
	private static void init() {
		aiLevel = TetrisAI.AI_ADULT;
		aiSpeed = 200;
		aiControlModel = 1;
		gameLevel = 6;
		soundSwitch = true;
		musicSwitch = true;
	}
	
	public static int getAiLevel() {
		return aiLevel;
	}
	
	public static int getAiSpeed() {
		return aiSpeed;
	}
	
	public static int getGameLevel() {
		return gameLevel;
	}
	
	public static int getAiControlModel() {
		return aiControlModel;
	}
	
	public static void setAiLevel(Context context,int level) {
		aiLevel = level;
		
		if (context == null) {
			return;
		}
		
		SharedPreferences.Editor editor = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(AI_LEVEL, aiLevel);
		editor.commit();
	}
	
	public static void setAiSpeed(Context context,int speed) {
		aiSpeed = speed;
		
		if (context == null) {
			return;
		}
		
		SharedPreferences.Editor editor = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(AI_SPEED, aiSpeed);
		editor.commit();
	}
	
	public static void setGameLevel(Context context,int level) {
		gameLevel = level;
		
		if (context == null) {
			return;
		}
		
		SharedPreferences.Editor editor = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(GAME_LEVEL, gameLevel);
		editor.commit();
	}
	
	public static void setAiControlModel(Context context,int model) {
		aiControlModel = model;
	}

	public static boolean isSoundSwitch() {
		return soundSwitch;
	}

	public static void setSoundSwitch(boolean soundSwitch,Context context) {
		AppConfig.soundSwitch = soundSwitch;
		
		if (context == null) {
			return;
		}
		
		SharedPreferences.Editor editor = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(SOUND_SWITCH, AppConfig.soundSwitch);
		editor.commit();
	}

	public static boolean isMusicSwitch() {
		return musicSwitch;
	}

	public static void setMusicSwitch(boolean musicSwitch,Context context) {
		AppConfig.musicSwitch = musicSwitch;
		
		if (context == null) {
			return;
		}
		
		SharedPreferences.Editor editor = context.getSharedPreferences(CONF_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(MUSIC_SWITCH, AppConfig.musicSwitch);
		editor.commit();
	}
}
