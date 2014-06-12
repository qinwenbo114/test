package com.acs.demo;

import android.app.Application;

public class BaseURL extends Application {
    private String baseURL = "http://www.thermostat.cc/Android/";
    //private String baseURL = "http://46.165.225.6/Android/";
    //private String baseURL = "http://192.168.1.250/Android/";

    public String getBaseURL() {
	return baseURL;
    }

    public void setBaseURL(String baseURL) {
	this.baseURL = baseURL;
    }

}
