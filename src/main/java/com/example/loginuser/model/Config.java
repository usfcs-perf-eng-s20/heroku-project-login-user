package com.example.loginuser.model;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Config {
	
	private boolean faves;
	private boolean search;
	private boolean login;
	private boolean analytics;
	public boolean isFaves() {
		return faves;
	}
	public void setFaves(boolean faves) {
		this.faves = faves;
	}
	public boolean isSearch() {
		return search;
	}
	public void setSearch(boolean search) {
		this.search = search;
	}
	public boolean isLogin() {
		return login;
	}
	public void setLogin(boolean login) {
		this.login = login;
	}
	public boolean isAnalytics() {
		return analytics;
	}
	public void setAnalytics(boolean analytics) {
		this.analytics = analytics;
	}
	
}
