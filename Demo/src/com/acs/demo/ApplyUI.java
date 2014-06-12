package com.acs.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ApplyUI extends Activity
{
	private String roomId = null;
	//private int[] t = new int[48];
	private int day = 0;
	private String ct = null;
	private String st = null;
	//private int mode; 
	private int sunday = 0;
	private int monday = 0;
	private int tuesday = 0;
	private int wednesday = 0;
	private int thursday = 0;
	private int friday = 0;
	private int saturday = 0;
	private String baseURL = "http://192.168.0.97/Android/";

	private CheckBox cbSunday = null;
	private CheckBox cbMonday = null;
	private CheckBox cbTuesday = null;
	private CheckBox cbWednesday = null;
	private CheckBox cbThursday = null;
	private CheckBox cbFriday = null;
	private CheckBox cbSaturday = null;
	private RelativeLayout rlSun = null;
	private RelativeLayout rlMon = null;
	private RelativeLayout rlTues = null;
	private RelativeLayout rlWed  = null;
	private RelativeLayout rlThur = null;
	private RelativeLayout rlFri = null;
	private RelativeLayout rlSat = null;

	// 创建数据应用网络连接线程对象
	private Thread mNetWork = null;
	// 应用反馈
	private boolean reApply = false;
	// handler更新UI
	private Handler mHandler = null;
	// handler弹出网络异常
	private Handler mWarmingHandler = null;
	// 网络连接状况对话框
	private AlertDialog adwarming;
	// 手势监测
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply);
		baseURL = ((BaseURL)getApplicationContext()).getBaseURL();
		mHandler = new Handler();// 控制器更新UI
		mWarmingHandler = new Handler();

		Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从RoomUI中的putExtra()方法传递过来的键值对
		roomId = bundle.getString("roomId");// 取出键值对中roomId键所对应的值，并赋值给roomId
		
		// 由接收48个点的值改为接收模板日期。
		day = bundle.getInt("dayMode");
		/*
		for (int i = 0; i < 48; i++)
		{
			t[i] = bundle.getInt("t" + i);
		}
		*/
		ct = bundle.getString("ct");
		st = bundle.getString("st");
		//mode = bundle.getInt("mode");
		/*
		for (int i = 0; i < 48; i++)
		{
			Log.v("T" + i, t[i] + "");
		}
		*/
		Log.v("roomId", roomId);
		Log.v("applyComft", ct);
		Log.v("applySaveT", st);

		cbSunday = (CheckBox) findViewById(R.id.cbSunday);
		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
		rlSun = (RelativeLayout) findViewById(R.id.rlSun);
		rlMon = (RelativeLayout) findViewById(R.id.rlMon);
		rlTues = (RelativeLayout) findViewById(R.id.rlTues);
		rlWed = (RelativeLayout) findViewById(R.id.rlWed);
		rlThur = (RelativeLayout) findViewById(R.id.rlThur);
		rlFri = (RelativeLayout) findViewById(R.id.rlFri);
		rlSat = (RelativeLayout) findViewById(R.id.rlSat);

		// 加载对话框，用于显示网络连接状况
		final View dialogWarming = getLayoutInflater().inflate(
				R.layout.dialog_warming, null);
		adwarming = new AlertDialog.Builder(ApplyUI.this)
				.setView(dialogWarming).create();

		// ActionBar标题显示
		ActionBar acb = getActionBar();
		acb.setTitle(R.string.applyTo);
		acb.setDisplayHomeAsUpEnabled(true);

		// 手势操作
		detector = new GestureDetector(this, new GestureListener());
		LinearLayout layout = (LinearLayout) findViewById(R.id.applyLayout);
		layout.setOnTouchListener(new TouhListener());
		layout.setLongClickable(true);

		// RelativeLayout点击即选中checkbox
		rlSun.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (cbSunday.isChecked())
					cbSunday.setChecked(false);
				else
					cbSunday.setChecked(true);
			}
		});
		
		rlMon.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbMonday.isChecked())
					cbMonday.setChecked(false);
				else
					cbMonday.setChecked(true);
			}
		});
		
		rlTues.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbTuesday.isChecked())
					cbTuesday.setChecked(false);
				else
					cbTuesday.setChecked(true);
			}
		});
		
		rlWed.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbWednesday.isChecked())
					cbWednesday.setChecked(false);
				else
					cbWednesday.setChecked(true);
			}
		});
		
		rlThur.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbThursday.isChecked())
					cbThursday.setChecked(false);
				else
					cbThursday.setChecked(true);
			}
		});
		
		rlFri.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbFriday.isChecked())
					cbFriday.setChecked(false);
				else
					cbFriday.setChecked(true);
			}
		});
		
		rlSat.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(cbSaturday.isChecked())
					cbSaturday.setChecked(false);
				else
					cbSaturday.setChecked(true);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.apply_actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case (android.R.id.home):
			ApplyUI.this.finish();
			return true;
		case (R.id.muComplete):
			/*Toast.makeText(getApplicationContext(), "完成菜单项被点击",
					Toast.LENGTH_LONG).show();*/
			if (cbSunday.isChecked())
				sunday = 1;
			if (cbMonday.isChecked())
				monday = 1;
			if (cbTuesday.isChecked())
				tuesday = 1;
			if (cbWednesday.isChecked())
				wednesday = 1;
			if (cbThursday.isChecked())
				thursday = 1;
			if (cbFriday.isChecked())
				friday = 1;
			if (cbSaturday.isChecked())
				saturday = 1;
			if (sunday == 0 && monday == 0 && tuesday == 0 && wednesday == 0
					&& thursday == 0 && friday == 0 && saturday == 0)
				// 没有日期被选中
				Toast.makeText(ApplyUI.this, R.string.pleaseChooseDay, Toast.LENGTH_SHORT)
						.show();
			else
			{
				// 执行网络通信，更新编程信息
				mNetWork = new Thread(mNetRunnable);
				mNetWork.start();
				ApplyUI.this.finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 数据应用Runnable
	private Runnable mNetRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
		    /*
			String url = baseURL + "refreshProgramLand.php?roomId=" + roomId
					+ "&MON=" + monday + "&TUES=" + tuesday + "&WED="
					+ wednesday + "&THUR=" + thursday + "&FRI=" + friday
					+ "&SAT=" + saturday + "&SUN=" + sunday + "&t0=" + t[0]
					+ "&t1=" + t[1] + "&t2=" + t[2] + "&t3=" + t[3] + "&t4="
					+ t[4] + "&t5=" + t[5] + "&t6=" + t[6] + "&t7=" + t[7]
					+ "&t8=" + t[8] + "&t9=" + t[9] + "&t10=" + t[10] + "&t11="
					+ t[11] + "&t12=" + t[12] + "&t13=" + t[13] + "&t14="
					+ t[14] + "&t15=" + t[15] + "&t16=" + t[16] + "&t17="
					+ t[17] + "&t18=" + t[18] + "&t19=" + t[19] + "&t20="
					+ t[20] + "&t21=" + t[21] + "&t22=" + t[22] + "&t23="
					+ t[23] + "&t24=" + t[24] + "&t25=" + t[25] + "&t26="
					+ t[26] + "&t27=" + t[27] + "&t28=" + t[28] + "&t29="
					+ t[29] + "&t30=" + t[30] + "&t31=" + t[31] + "&t32="
					+ t[32] + "&t33=" + t[33] + "&t34=" + t[34] + "&t35="
					+ t[35] + "&t36=" + t[36] + "&t37=" + t[37] + "&t38="
					+ t[38] + "&t39=" + t[39] + "&t40=" + t[40] + "&t41="
					+ t[41] + "&t42=" + t[42] + "&t43=" + t[43] + "&t44="
					+ t[44] + "&t45=" + t[45] + "&t46=" + t[46] + "&t47="
					+ t[47] + "&ct=" + ct + "&st=" + st;
					*/
		    	String url = baseURL + "refreshProgramLand.php?roomId=" + roomId
		    			+ "&MON=" + monday + "&TUES=" + tuesday + "&WED="
		    			+ wednesday + "&THUR=" + thursday + "&FRI=" + friday
		    			+ "&SAT=" + saturday + "&SUN=" + sunday + "&dayMode=" 
		    			+ day + "&ct=" + ct + "&st=" + st;
			MyHttp myHttp = new MyHttp();
			String retStr = myHttp.httpGet(url);
			try
			{
				JSONObject jsonObject = new JSONObject(retStr);
				String re_err = jsonObject.getString("error");
				Log.v("编程设置更新状况", re_err);

				if (re_err.equals("0"))
				{
					// 应用成功，用handler弹出toast
					reApply = true;
					mHandler.post(mReApplyRunnable);
				}
				else if (re_err.equals("1"))
				{
					// 应用失败，用handler弹出toast
					reApply = false;
					mHandler.post(mReApplyRunnable);
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				mWarmingHandler.post(mWarmingRunnable);
			}
			catch (Exception e)
			{
				mWarmingHandler.post(mWarmingRunnable);
			}
		}

	};

	private Runnable mReApplyRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			if (reApply == true)
			{
				Toast.makeText(ApplyUI.this, R.string.applySuccessed, Toast.LENGTH_SHORT)
						.show();
				ApplyUI.this.finish();
			}
			else
				Toast.makeText(ApplyUI.this, R.string.applyFailed, Toast.LENGTH_SHORT)
						.show();
		}

	};
	// 网络异常对话框弹出Runnable
	private Runnable mWarmingRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub

			// 加载对话框，用于网路线程异常时弹出

			if (adwarming.isShowing())
			{
			}
			else
				adwarming.show();
			// adwarming.show();
		}

	};

	// 下面写一个向右滑动返回上层Activity的手势操作
	// 触摸屏幕监听
	class TouhListener implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(), "----?",
			// event.getAction()).show();
			return detector.onTouchEvent(event);
		}

	}

	// 手势滑动监听
	class GestureListener implements OnGestureListener
	{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			// e1 触摸的起始位置，e2 触摸的结束位置，velocityX X轴每一秒移动的像素速度（大概这个意思） velocityY
			// 就是Ｙ咯
			// 手势左,上为正 ——，右，下为负正
			if ((e2.getX() - e1.getX()) > 50)
			{
				// 为什么是50？ 这个根据你的模拟器大小来定，看看模拟器宽度，e2.getX()-e1.getX()<屏幕宽度就ＯＫ
				Toast.makeText(getApplicationContext(),
						"向右滑动,滑动距离：" + (e2.getX() - e1.getX()),
						Toast.LENGTH_LONG).show();
				// 要触发什么事件都在这里写就OK
				// 如果要跳转到另外一个activity
				// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
				// intent.putExtra("uid", "1");
				// startActivity(intent);
				ApplyUI.this.finish();

			}
			/*
			 * if ((e2.getX()-e1.getX())<-50) {
			 * Toast.makeText(getApplicationContext(),
			 * "向左滑动,滑动距离："+(e2.getX()-e1.getX()), Toast.LENGTH_LONG).show();
			 * Intent intent=new Intent(ProgramUI.this, ProgramUI.class);
			 * intent.putExtra("ID", roomId); startActivity(intent); } if
			 * ((e2.getY()-e1.getY())<-50) {
			 * Toast.makeText(getApplicationContext(), "向上滑动",
			 * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
			 * Toast.makeText(getApplicationContext(), "向下滑动",
			 * Toast.LENGTH_LONG).show(); }
			 */
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e)
		{
			// TODO Auto-generated method stub\
			/*
			 * 长按事件 一切长事件按屏幕想要触发的事件都在这里写
			 */
			// Toast.makeText(getApplicationContext(),
			// "------------> onLongPress", Toast.LENGTH_LONG).show();
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY)
		{
			// TODO Auto-generated method stub
			/*
			 * 这个函数大概是这样，有误差。distanceX 是X轴路径长度，distanceY 是Y轴路径长度（注意：是路径，不是位移）;
			 */
			// Toast.makeText(getApplicationContext(), "------------> onScroll",
			// Toast.LENGTH_LONG).show();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e)
		{
			// TODO Auto-generated method stub
			/*  
		             *   
		             */
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e)
		{
			// TODO Auto-generated method stub
			return false;
		}
	}

}
