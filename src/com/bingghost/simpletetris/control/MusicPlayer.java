package com.bingghost.simpletetris.control;

import java.util.HashMap;
import java.util.Map;

import com.bingghost.simpletetris.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class MusicPlayer {
	public static MediaPlayer m_MediaPlay;      // 背景播放器
	public static MediaPlayer m_MenuPlay;       // 菜单播放器
	public static SoundPool m_SoundPool;        // 声明音效播放器
	public static MediaPlayer m_FastDown;       // 快速下滑播放器

	private static boolean musicSwitch = true;  // 音乐开关
	private static boolean soundSwitch = true;  // 音效开关

	private static Map<Integer, Integer> soundMap; // 音效资源id与加载过后的音源id的映射关系表
	private static Context context;

	public static void initMusicPlay(Context c) {
		context = c;
	}

	public static void initMenuMusicPlay(Context c) {
		context = c;
	}

	public static void BgMediaplayer() {
		m_MediaPlay = MediaPlayer.create(context, R.raw.gamebg);
		m_MediaPlay.setLooping(true);
	}

	public static void menuMusic() {
		m_MenuPlay = MediaPlayer.create(context, R.raw.menubg);
		m_MenuPlay.setLooping(true);
	}

	public static void pauseMusic() {
		if (m_MediaPlay.isPlaying()) {
			m_MediaPlay.pause();
		}
	}

	public static void pauseMenuMusic() {
		if (m_MenuPlay.isPlaying()) {
			m_MenuPlay.pause();
		}
	}

	public static void startMusic() {
		if (musicSwitch) {
			m_MediaPlay.start();
		}
	}

	public static void startMenuMusic() {
		if (musicSwitch) {
			m_MenuPlay.start();
		}
	}

	public static void releaseMusic() {
		if (m_MediaPlay != null) {
			m_MediaPlay.release();
		}
	}

	public static void releaseMenuMusic() {
		if (m_MenuPlay != null) {
			m_MenuPlay.release();
		}
	}

	// 设置音乐开关
	public static void setMusicSwitch(boolean musicSwitch) {

		MusicPlayer.musicSwitch = musicSwitch;
		if (MusicPlayer.musicSwitch) {
			m_MediaPlay.start();
		} else {
			m_MediaPlay.stop();
		}
	}

	@SuppressLint("UseSparseArrays")
	public static void initSound() {
		m_SoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<Integer, Integer>();
		
		// 将音效资源加入 soundPool，并做成soundMap 映射
		soundMap.put(R.raw.action, m_SoundPool.load(context, R.raw.action, 1));
		soundMap.put(R.raw.fastdown, m_SoundPool.load(context, R.raw.fastdown, 1));
		soundMap.put(R.raw.rotation, m_SoundPool.load(context, R.raw.rotation, 1));
		soundMap.put(R.raw.down, m_SoundPool.load(context, R.raw.down, 1));
		soundMap.put(R.raw.delete1, m_SoundPool.load(context, R.raw.delete1, 1));
		soundMap.put(R.raw.delete2, m_SoundPool.load(context, R.raw.delete2, 1));
		soundMap.put(R.raw.delete3, m_SoundPool.load(context, R.raw.delete3, 1));
		soundMap.put(R.raw.delete4, m_SoundPool.load(context, R.raw.delete4, 1));
	}

	public static int playSound(int resId, int loop) {
		if (!isSoundSwitch()) {
			return 0;
		}

		Integer soundId = soundMap.get(resId);
		if (soundId != null) {
			return m_SoundPool.play(soundId, 1, 1, 1, loop, 1);
		} else {
			return 0;
		}
	}

	public static void releaseSound() {
		if (m_SoundPool != null) {
			m_SoundPool.release();
		}
	}

	public static boolean isSoundSwitch() {
		return soundSwitch;
	}

	public static void setSoundSwitch(boolean soundSwitch) {
		MusicPlayer.soundSwitch = soundSwitch;
	}

}
