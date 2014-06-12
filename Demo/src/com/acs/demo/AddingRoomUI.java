package com.acs.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddingRoomUI extends Activity{

    private Button btRefresh = null;
    private String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_adding_room);
	
	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�CRMActivity�е�putExtra()�������ݹ����ļ�ֵ��
	uid = bundle.getString("uid");
	
	btRefresh = (Button) findViewById(R.id.btRefresh);
	
	btRefresh.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(AddingRoomUI.this, RoomListUI.class);
		intent.putExtra("uid", uid);
		startActivity(intent);
		AddingRoomUI.this.finish();
	    }
	});
    }
    
}
