package com.acs.demo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpWebUI extends Activity {
    private String baseURL = null;
    private String helpURL = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	helpURL = this.getString(R.string.helpURL);

	// 创建网页视图对象
	WebView wv = new WebView(this);
	// 设置视图界面为该网页视图对象
	setContentView(wv);

	// 定义网页视图对象所加载的网址
	wv.loadUrl(baseURL + helpURL);
	// 设置显示比例，0——100
	wv.setInitialScale(0);
    }

}
