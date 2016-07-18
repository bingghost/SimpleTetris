package com.bingghost.simpletetris.db;

public class ScoreInfo {
	private String name;
	private int score;
	private int resID;
	private int ID;
	
	public ScoreInfo(String name,int score,int dbID,int resID) {
		this.setName(name);
		this.setScore(score);
		this.setResID(resID);
		this.setID(dbID);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setResID(int resID) {
		this.resID = resID;
	}

	public int getResID() {
		return resID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int dbID) {
		this.ID = dbID;
	}
}
