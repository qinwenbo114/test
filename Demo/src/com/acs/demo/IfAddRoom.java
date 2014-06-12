package com.acs.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IfAddRoom extends Activity
{
	private Button btNotAddRoom = null;
	private Button btSureAddRoom = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_if_add_room);
		
		btNotAddRoom = (Button) findViewById(R.id.btNotAddRoom);
		btSureAddRoom = (Button) findViewById(R.id.btSureAddRoom);
		
		btNotAddRoom.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				IfAddRoom.this.finish();
			}
		});
	}

}
