package com.acs.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IfAddBoiler extends Activity
{
	private Button btNotAddBoiler;
	private Button btSureAddBoiler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_if_add_boiler);
		
		btNotAddBoiler = (Button) findViewById(R.id.btNotAddBoiler);
		btSureAddBoiler = (Button) findViewById(R.id.btSureAddBoiler);
		
		btNotAddBoiler.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				IfAddBoiler.this.finish();
				
			}
		});
	}

}
