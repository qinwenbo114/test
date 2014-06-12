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

	// ��������Ӧ�����������̶߳���
	private Thread mNetWork = null;
	// Ӧ�÷���
	private boolean reApply = false;
	// handler����UI
	private Handler mHandler = null;
	// handler���������쳣
	private Handler mWarmingHandler = null;
	// ��������״���Ի���
	private AlertDialog adwarming;
	// ���Ƽ��
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply);
		baseURL = ((BaseURL)getApplicationContext()).getBaseURL();
		mHandler = new Handler();// ����������UI
		mWarmingHandler = new Handler();

		Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�RoomUI�е�putExtra()�������ݹ����ļ�ֵ��
		roomId = bundle.getString("roomId");// ȡ����ֵ����roomId������Ӧ��ֵ������ֵ��roomId
		
		// �ɽ���48�����ֵ��Ϊ����ģ�����ڡ�
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

		// ���ضԻ���������ʾ��������״��
		final View dialogWarming = getLayoutInflater().inflate(
				R.layout.dialog_warming, null);
		adwarming = new AlertDialog.Builder(ApplyUI.this)
				.setView(dialogWarming).create();

		// ActionBar������ʾ
		ActionBar acb = getActionBar();
		acb.setTitle(R.string.applyTo);
		acb.setDisplayHomeAsUpEnabled(true);

		// ���Ʋ���
		detector = new GestureDetector(this, new GestureListener());
		LinearLayout layout = (LinearLayout) findViewById(R.id.applyLayout);
		layout.setOnTouchListener(new TouhListener());
		layout.setLongClickable(true);

		// RelativeLayout�����ѡ��checkbox
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
			/*Toast.makeText(getApplicationContext(), "��ɲ˵�����",
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
				// û�����ڱ�ѡ��
				Toast.makeText(ApplyUI.this, R.string.pleaseChooseDay, Toast.LENGTH_SHORT)
						.show();
			else
			{
				// ִ������ͨ�ţ����±����Ϣ
				mNetWork = new Thread(mNetRunnable);
				mNetWork.start();
				ApplyUI.this.finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ����Ӧ��Runnable
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
				Log.v("������ø���״��", re_err);

				if (re_err.equals("0"))
				{
					// Ӧ�óɹ�����handler����toast
					reApply = true;
					mHandler.post(mReApplyRunnable);
				}
				else if (re_err.equals("1"))
				{
					// Ӧ��ʧ�ܣ���handler����toast
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
	// �����쳣�Ի��򵯳�Runnable
	private Runnable mWarmingRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub

			// ���ضԻ���������·�߳��쳣ʱ����

			if (adwarming.isShowing())
			{
			}
			else
				adwarming.show();
			// adwarming.show();
		}

	};

	// ����дһ�����һ��������ϲ�Activity�����Ʋ���
	// ������Ļ����
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

	// ���ƻ�������
	class GestureListener implements OnGestureListener
	{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			// e1 ��������ʼλ�ã�e2 �����Ľ���λ�ã�velocityX X��ÿһ���ƶ��������ٶȣ���������˼�� velocityY
			// ���ǣٿ�
			// ������,��Ϊ�� �������ң���Ϊ����
			if ((e2.getX() - e1.getX()) > 50)
			{
				// Ϊʲô��50�� ����������ģ������С����������ģ������ȣ�e2.getX()-e1.getX()<��Ļ��Ⱦͣϣ�
				Toast.makeText(getApplicationContext(),
						"���һ���,�������룺" + (e2.getX() - e1.getX()),
						Toast.LENGTH_LONG).show();
				// Ҫ����ʲô�¼���������д��OK
				// ���Ҫ��ת������һ��activity
				// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
				// intent.putExtra("uid", "1");
				// startActivity(intent);
				ApplyUI.this.finish();

			}
			/*
			 * if ((e2.getX()-e1.getX())<-50) {
			 * Toast.makeText(getApplicationContext(),
			 * "���󻬶�,�������룺"+(e2.getX()-e1.getX()), Toast.LENGTH_LONG).show();
			 * Intent intent=new Intent(ProgramUI.this, ProgramUI.class);
			 * intent.putExtra("ID", roomId); startActivity(intent); } if
			 * ((e2.getY()-e1.getY())<-50) {
			 * Toast.makeText(getApplicationContext(), "���ϻ���",
			 * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
			 * Toast.makeText(getApplicationContext(), "���»���",
			 * Toast.LENGTH_LONG).show(); }
			 */
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e)
		{
			// TODO Auto-generated method stub\
			/*
			 * �����¼� һ�г��¼�����Ļ��Ҫ�������¼���������д
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
			 * ����������������������distanceX ��X��·�����ȣ�distanceY ��Y��·�����ȣ�ע�⣺��·��������λ�ƣ�;
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
