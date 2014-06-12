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

	// ������ҳ��ͼ����
	WebView wv = new WebView(this);
	// ������ͼ����Ϊ����ҳ��ͼ����
	setContentView(wv);

	// ������ҳ��ͼ���������ص���ַ
	wv.loadUrl(baseURL + helpURL);
	// ������ʾ������0����100
	wv.setInitialScale(0);
    }

}
