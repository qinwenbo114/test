package com.acs.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramUI extends Activity
{

	private String baseURL = "http://192.168.0.97/Android/";
	// private String baseURL = "http://10.0.2.2/Android/";

	// get RoomId form Intent
	private String roomId = null;

	// ��������Ӧ�����������̶߳���
	private Thread mNetWork = null;

	// ��ʼ���߳�
	private Thread mCreateWork = null;

	// handler����UI
	private Handler mHandler = null;

	// handler���������쳣
	private Handler mWarmingHandler = null;

	// Temp״̬,��Сʱһ������48���㣬�ڳ����ʼ��ʱ����ֵΪ2(����)��1(����)
	private int time[] = new int[48];
	// �������������
	private String ct = null;
	private String st = null;
	// �ؼ�����
	private ImageView ivTime[] = new ImageView[48];
	private RadioGroup rgModel = null;
	private RadioButton rbModel1 = null;
	private RadioButton rbModel2 = null;
	private RadioButton rbModel3 = null;
	private RadioButton rbModelSelf = null;
	private CheckBox cbWorkDay = null;
	private CheckBox cbSatDay = null;
	private CheckBox cbSunDay = null;
	private Button btApply = null;
	private TextView tvComfTemp = null;
	private TextView tvSaveTemp = null;
	private SeekBar sbComfTemp = null;
	private SeekBar sbSaveTemp = null;

	// ��¼��ǰ��ѡRadioButton
	private int checkedNow = 0;
	// ��¼Ӧ������
	private int workDay = 0;
	private int satDay = 0;
	private int sunDay = 0;
	// Ӧ�÷���
	private boolean reApply = false;
	// ���Ƽ��
	private GestureDetector detector;
	// �����жԻ���
	private AlertDialog ad;
	// ��������״���Ի���
	private AlertDialog adwarming;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);
		baseURL = ((BaseURL)getApplicationContext()).getBaseURL();

		Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�RoomUI�е�putExtra()�������ݹ����ļ�ֵ��
		roomId = bundle.getString("ID");// ȡ����ֵ����ID������Ӧ��ֵ������ֵ��roomId
		String roomName = bundle.getString("NAME");

		mHandler = new Handler();// ����������UI
		mWarmingHandler = new Handler();

		// �󶨿ؼ�
		rgModel = (RadioGroup) findViewById(R.id.radioGroupModel);
		rbModel1 = (RadioButton) findViewById(R.id.rbModel1);
		rbModel2 = (RadioButton) findViewById(R.id.rbModel2);
		rbModel3 = (RadioButton) findViewById(R.id.rbModel3);
		rbModelSelf = (RadioButton) findViewById(R.id.rbModelSelf);
		cbWorkDay = (CheckBox) findViewById(R.id.cbWorkDay);
		cbSatDay = (CheckBox) findViewById(R.id.cbSatDay);
		cbSunDay = (CheckBox) findViewById(R.id.cbSunDay);
		btApply = (Button) findViewById(R.id.btApply);
		tvComfTemp = (TextView) findViewById(R.id.tvComfTemp);
		tvSaveTemp = (TextView) findViewById(R.id.tvSaveTemp);
		sbComfTemp = (SeekBar) findViewById(R.id.sbComfTemp);
		sbSaveTemp = (SeekBar) findViewById(R.id.sbSaveTemp);

		ivTime[0] = (ImageView) findViewById(R.id.ivTime0);
		ivTime[1] = (ImageView) findViewById(R.id.ivTime1);
		ivTime[2] = (ImageView) findViewById(R.id.ivTime2);
		ivTime[3] = (ImageView) findViewById(R.id.ivTime3);
		ivTime[4] = (ImageView) findViewById(R.id.ivTime4);
		ivTime[5] = (ImageView) findViewById(R.id.ivTime5);
		ivTime[6] = (ImageView) findViewById(R.id.ivTime6);
		ivTime[7] = (ImageView) findViewById(R.id.ivTime7);
		ivTime[8] = (ImageView) findViewById(R.id.ivTime8);
		ivTime[9] = (ImageView) findViewById(R.id.ivTime9);
		ivTime[10] = (ImageView) findViewById(R.id.ivTime10);
		ivTime[11] = (ImageView) findViewById(R.id.ivTime11);
		ivTime[12] = (ImageView) findViewById(R.id.ivTime12);
		ivTime[13] = (ImageView) findViewById(R.id.ivTime13);
		ivTime[14] = (ImageView) findViewById(R.id.ivTime14);
		ivTime[15] = (ImageView) findViewById(R.id.ivTime15);
		ivTime[16] = (ImageView) findViewById(R.id.ivTime16);
		ivTime[17] = (ImageView) findViewById(R.id.ivTime17);
		ivTime[18] = (ImageView) findViewById(R.id.ivTime18);
		ivTime[19] = (ImageView) findViewById(R.id.ivTime19);
		ivTime[20] = (ImageView) findViewById(R.id.ivTime20);
		ivTime[21] = (ImageView) findViewById(R.id.ivTime21);
		ivTime[22] = (ImageView) findViewById(R.id.ivTime22);
		ivTime[23] = (ImageView) findViewById(R.id.ivTime23);
		ivTime[24] = (ImageView) findViewById(R.id.ivTime24);
		ivTime[25] = (ImageView) findViewById(R.id.ivTime25);
		ivTime[26] = (ImageView) findViewById(R.id.ivTime26);
		ivTime[27] = (ImageView) findViewById(R.id.ivTime27);
		ivTime[28] = (ImageView) findViewById(R.id.ivTime28);
		ivTime[29] = (ImageView) findViewById(R.id.ivTime29);
		ivTime[30] = (ImageView) findViewById(R.id.ivTime30);
		ivTime[31] = (ImageView) findViewById(R.id.ivTime31);
		ivTime[32] = (ImageView) findViewById(R.id.ivTime32);
		ivTime[33] = (ImageView) findViewById(R.id.ivTime33);
		ivTime[34] = (ImageView) findViewById(R.id.ivTime34);
		ivTime[35] = (ImageView) findViewById(R.id.ivTime35);
		ivTime[36] = (ImageView) findViewById(R.id.ivTime36);
		ivTime[37] = (ImageView) findViewById(R.id.ivTime37);
		ivTime[38] = (ImageView) findViewById(R.id.ivTime38);
		ivTime[39] = (ImageView) findViewById(R.id.ivTime39);
		ivTime[40] = (ImageView) findViewById(R.id.ivTime40);
		ivTime[41] = (ImageView) findViewById(R.id.ivTime41);
		ivTime[42] = (ImageView) findViewById(R.id.ivTime42);
		ivTime[43] = (ImageView) findViewById(R.id.ivTime43);
		ivTime[44] = (ImageView) findViewById(R.id.ivTime44);
		ivTime[45] = (ImageView) findViewById(R.id.ivTime45);
		ivTime[46] = (ImageView) findViewById(R.id.ivTime46);
		ivTime[47] = (ImageView) findViewById(R.id.ivTime47);

		// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
		LayoutInflater factory = LayoutInflater.from(ProgramUI.this);
		final View dialog = factory.inflate(R.layout.dialog_loading, null);
		ad = new AlertDialog.Builder(ProgramUI.this).setView(dialog).show();

		// ���ضԻ���������ʾ��������״��
		final View dialogWarming = getLayoutInflater().inflate(
				R.layout.dialog_warming, null);
		adwarming = new AlertDialog.Builder(ProgramUI.this).setView(
				dialogWarming).create();

		// ������ʼ���߳�
		mCreateWork = new Thread(mCreateRunnable);
		mCreateWork.start();

		// ActionBar������ʾ
		ActionBar acb = getActionBar();
		acb.setTitle(roomName + " Programming");
		acb.setDisplayHomeAsUpEnabled(true);

		// ��ѡ��ť�����¼�
		rgModel.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				// TODO Auto-generated method stub
				int rbID = group.getCheckedRadioButtonId();
				if (rbID == R.id.rbModel1)
				{
					// UI����Ϊmodel1
					checkedNow = 1;
					mHandler.post(mModelRunnable);
				}
				else if (rbID == R.id.rbModel2)
				{
					// UI����Ϊmodel2
					checkedNow = 2;
					mHandler.post(mModelRunnable);
				}
				else if (rbID == R.id.rbModel3)
				{
					// UI����Ϊmodle3
					checkedNow = 3;
					mHandler.post(mModelRunnable);
				}
				else if (rbID == R.id.rbModelSelf)
				{
					// UI����Ϊ�Զ���model
					checkedNow = 4;
				}
			}
		});

		// Ӧ�ð�ť�����¼�
		btApply.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (cbWorkDay.isChecked())
					workDay = 1;
				else
					workDay = 0;
				if (cbSatDay.isChecked())
					satDay = 1;
				else
					satDay = 0;
				if (cbSunDay.isChecked())
					sunDay = 1;
				else
					sunDay = 0;
				if (workDay == 0 && satDay == 0 && sunDay == 0)
				{// Ӧ������δѡ�񣬲�ִ������ͨ��
					Toast.makeText(ProgramUI.this, "��ѡ��Ӧ������",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					// ִ������ͨ���߳�
					mNetWork = new Thread(mNetRunnable);
					mNetWork.start();
				}
			}
		});

		sbComfTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				// TODO Auto-generated method stub
				int t = 5 + sbComfTemp.getProgress();
				ct = "" + t;
				tvComfTemp.setText("" + t);
				Log.v("ComfTemp", t + "");
			}
		});

		sbSaveTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				// TODO Auto-generated method stub
				int t = 5 + sbSaveTemp.getProgress();
				st = "" + t;
				tvSaveTemp.setText("" + t);
				Log.v("SaveTemp", t + "");
			}
		});

		// ���Ʋ���
		detector = new GestureDetector(this, new GestureListener());
		LinearLayout layout = (LinearLayout) findViewById(R.id.programLayout);
		layout.setOnTouchListener(new TouhListener());
		layout.setLongClickable(true);

		// ���ͼƬ�����¼�
		ivTime[0].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[0] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[0] = 2;
					ivTime[0].setImageResource(R.drawable.comf);
					Log.v("0:00", "��Ϊ����");
				}
				else if (time[0] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[0] = 1;
					ivTime[0].setImageResource(R.drawable.save);
					Log.v("0:00", "��Ϊ����");
				}

			}
		});

		ivTime[1].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[1] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[1] = 2;
					ivTime[1].setImageResource(R.drawable.comf);
					Log.v("0:30", "��Ϊ����");
				}
				else if (time[1] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[1] = 1;
					ivTime[1].setImageResource(R.drawable.save);
					Log.v("0:30", "��Ϊ����");
				}

			}
		});

		ivTime[2].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[2] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[2] = 2;
					ivTime[2].setImageResource(R.drawable.comf);
					Log.v("1:00", "��Ϊ����");
				}
				else if (time[2] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[2] = 1;
					ivTime[2].setImageResource(R.drawable.save);
					Log.v("1:00", "��Ϊ����");
				}

			}
		});

		ivTime[3].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[3] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[3] = 2;
					ivTime[3].setImageResource(R.drawable.comf);
					Log.v("1:30", "��Ϊ����");
				}
				else if (time[3] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[3] = 1;
					ivTime[3].setImageResource(R.drawable.save);
					Log.v("1:30", "��Ϊ����");
				}

			}
		});

		ivTime[4].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[4] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[4] = 2;
					ivTime[4].setImageResource(R.drawable.comf);
					Log.v("2:00", "��Ϊ����");
				}
				else if (time[4] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[4] = 1;
					ivTime[4].setImageResource(R.drawable.save);
					Log.v("2:00", "��Ϊ����");
				}

			}
		});

		ivTime[5].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[5] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[5] = 2;
					ivTime[5].setImageResource(R.drawable.comf);
					Log.v("2:30", "��Ϊ����");
				}
				else if (time[5] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[5] = 1;
					ivTime[5].setImageResource(R.drawable.save);
					Log.v("2:30", "��Ϊ����");
				}

			}
		});

		ivTime[6].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[6] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[6] = 2;
					ivTime[6].setImageResource(R.drawable.comf);
					Log.v("3:00", "��Ϊ����");
				}
				else if (time[6] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[6] = 1;
					ivTime[6].setImageResource(R.drawable.save);
					Log.v("3:00", "��Ϊ����");
				}

			}
		});

		ivTime[7].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[7] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[7] = 2;
					ivTime[7].setImageResource(R.drawable.comf);
					Log.v("3:30", "��Ϊ����");
				}
				else if (time[7] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[7] = 1;
					ivTime[7].setImageResource(R.drawable.save);
					Log.v("3:30", "��Ϊ����");
				}

			}
		});

		ivTime[8].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[8] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[8] = 2;
					ivTime[8].setImageResource(R.drawable.comf);
					Log.v("4:00", "��Ϊ����");
				}
				else if (time[8] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[8] = 1;
					ivTime[8].setImageResource(R.drawable.save);
					Log.v("4:00", "��Ϊ����");
				}

			}
		});

		ivTime[9].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[9] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[9] = 2;
					ivTime[9].setImageResource(R.drawable.comf);
					Log.v("4:30", "��Ϊ����");
				}
				else if (time[9] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[9] = 1;
					ivTime[9].setImageResource(R.drawable.save);
					Log.v("4:30", "��Ϊ����");
				}

			}
		});

		ivTime[10].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[10] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[10] = 2;
					ivTime[10].setImageResource(R.drawable.comf);
					Log.v("5:00", "��Ϊ����");
				}
				else if (time[10] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[10] = 1;
					ivTime[10].setImageResource(R.drawable.save);
					Log.v("5:00", "��Ϊ����");
				}

			}
		});

		ivTime[11].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[11] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[11] = 2;
					ivTime[11].setImageResource(R.drawable.comf);
					Log.v("5:30", "��Ϊ����");
				}
				else if (time[11] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[11] = 1;
					ivTime[11].setImageResource(R.drawable.save);
					Log.v("5:30", "��Ϊ����");
				}

			}
		});

		ivTime[12].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[12] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[12] = 2;
					ivTime[12].setImageResource(R.drawable.comf);
					Log.v("6:00", "��Ϊ����");
				}
				else if (time[12] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[12] = 1;
					ivTime[12].setImageResource(R.drawable.save);
					Log.v("6:00", "��Ϊ����");
				}

			}
		});

		ivTime[13].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[13] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[13] = 2;
					ivTime[13].setImageResource(R.drawable.comf);
					Log.v("6:30", "��Ϊ����");
				}
				else if (time[13] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[13] = 1;
					ivTime[13].setImageResource(R.drawable.save);
					Log.v("6:30", "��Ϊ����");
				}

			}
		});

		ivTime[14].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[14] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[14] = 2;
					ivTime[14].setImageResource(R.drawable.comf);
					Log.v("7:00", "��Ϊ����");
				}
				else if (time[14] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[14] = 1;
					ivTime[14].setImageResource(R.drawable.save);
					Log.v("7:00", "��Ϊ����");
				}

			}
		});

		ivTime[15].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[15] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[15] = 2;
					ivTime[15].setImageResource(R.drawable.comf);
					Log.v("7:30", "��Ϊ����");
				}
				else if (time[15] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[15] = 1;
					ivTime[15].setImageResource(R.drawable.save);
					Log.v("7:30", "��Ϊ����");
				}

			}
		});

		ivTime[16].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[16] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[16] = 2;
					ivTime[16].setImageResource(R.drawable.comf);
					Log.v("8:00", "��Ϊ����");
				}
				else if (time[16] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[16] = 1;
					ivTime[16].setImageResource(R.drawable.save);
					Log.v("8:00", "��Ϊ����");
				}

			}
		});

		ivTime[17].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[17] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[17] = 2;
					ivTime[17].setImageResource(R.drawable.comf);
					Log.v("8:30", "��Ϊ����");
				}
				else if (time[17] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[17] = 1;
					ivTime[17].setImageResource(R.drawable.save);
					Log.v("8:30", "��Ϊ����");
				}

			}
		});

		ivTime[18].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[18] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[18] = 2;
					ivTime[18].setImageResource(R.drawable.comf);
					Log.v("9:00", "��Ϊ����");
				}
				else if (time[18] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[18] = 1;
					ivTime[18].setImageResource(R.drawable.save);
					Log.v("9:00", "��Ϊ����");
				}

			}
		});

		ivTime[19].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[19] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[19] = 2;
					ivTime[19].setImageResource(R.drawable.comf);
					Log.v("9:30", "��Ϊ����");
				}
				else if (time[19] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[19] = 1;
					ivTime[19].setImageResource(R.drawable.save);
					Log.v("9:30", "��Ϊ����");
				}

			}
		});

		ivTime[20].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[20] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[20] = 2;
					ivTime[20].setImageResource(R.drawable.comf);
					Log.v("10:00", "��Ϊ����");
				}
				else if (time[20] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[20] = 1;
					ivTime[20].setImageResource(R.drawable.save);
					Log.v("10:00", "��Ϊ����");
				}

			}
		});

		ivTime[21].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[21] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[21] = 2;
					ivTime[21].setImageResource(R.drawable.comf);
					Log.v("10:30", "��Ϊ����");
				}
				else if (time[21] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[21] = 1;
					ivTime[21].setImageResource(R.drawable.save);
					Log.v("10:30", "��Ϊ����");
				}

			}
		});

		ivTime[22].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[22] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[22] = 2;
					ivTime[22].setImageResource(R.drawable.comf);
					Log.v("11:00", "��Ϊ����");
				}
				else if (time[22] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[22] = 1;
					ivTime[22].setImageResource(R.drawable.save);
					Log.v("11:00", "��Ϊ����");
				}

			}
		});

		ivTime[23].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[23] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[23] = 2;
					ivTime[23].setImageResource(R.drawable.comf);
					Log.v("11:30", "��Ϊ����");
				}
				else if (time[23] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[23] = 1;
					ivTime[23].setImageResource(R.drawable.save);
					Log.v("11:30", "��Ϊ����");
				}

			}
		});

		ivTime[24].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[24] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[24] = 2;
					ivTime[24].setImageResource(R.drawable.comf);
					Log.v("12:00", "��Ϊ����");
				}
				else if (time[24] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[24] = 1;
					ivTime[24].setImageResource(R.drawable.save);
					Log.v("12:00", "��Ϊ����");
				}

			}
		});

		ivTime[25].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[25] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[25] = 2;
					ivTime[25].setImageResource(R.drawable.comf);
					Log.v("12:30", "��Ϊ����");
				}
				else if (time[25] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[25] = 1;
					ivTime[25].setImageResource(R.drawable.save);
					Log.v("12:30", "��Ϊ����");
				}

			}
		});

		ivTime[26].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[26] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[26] = 2;
					ivTime[26].setImageResource(R.drawable.comf);
					Log.v("13:00", "��Ϊ����");
				}
				else if (time[26] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[26] = 1;
					ivTime[26].setImageResource(R.drawable.save);
					Log.v("13:00", "��Ϊ����");
				}

			}
		});

		ivTime[27].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[27] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[27] = 2;
					ivTime[27].setImageResource(R.drawable.comf);
					Log.v("13:30", "��Ϊ����");
				}
				else if (time[27] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[27] = 1;
					ivTime[27].setImageResource(R.drawable.save);
					Log.v("13:30", "��Ϊ����");
				}

			}
		});

		ivTime[28].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[28] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[28] = 2;
					ivTime[28].setImageResource(R.drawable.comf);
					Log.v("14:00", "��Ϊ����");
				}
				else if (time[28] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[28] = 1;
					ivTime[28].setImageResource(R.drawable.save);
					Log.v("14:00", "��Ϊ����");
				}

			}
		});

		ivTime[29].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[29] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[29] = 2;
					ivTime[29].setImageResource(R.drawable.comf);
					Log.v("14:30", "��Ϊ����");
				}
				else if (time[29] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[29] = 1;
					ivTime[29].setImageResource(R.drawable.save);
					Log.v("14:30", "��Ϊ����");
				}

			}
		});

		ivTime[30].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[30] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[30] = 2;
					ivTime[30].setImageResource(R.drawable.comf);
					Log.v("15:00", "��Ϊ����");
				}
				else if (time[30] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[30] = 1;
					ivTime[30].setImageResource(R.drawable.save);
					Log.v("15:00", "��Ϊ����");
				}

			}
		});

		ivTime[31].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[31] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[31] = 2;
					ivTime[31].setImageResource(R.drawable.comf);
					Log.v("15:30", "��Ϊ����");
				}
				else if (time[31] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[31] = 1;
					ivTime[31].setImageResource(R.drawable.save);
					Log.v("15:30", "��Ϊ����");
				}

			}
		});

		ivTime[32].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[32] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[32] = 2;
					ivTime[32].setImageResource(R.drawable.comf);
					Log.v("16:00", "��Ϊ����");
				}
				else if (time[32] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[32] = 1;
					ivTime[32].setImageResource(R.drawable.save);
					Log.v("16:00", "��Ϊ����");
				}

			}
		});

		ivTime[33].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[33] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[33] = 2;
					ivTime[33].setImageResource(R.drawable.comf);
					Log.v("16:30", "��Ϊ����");
				}
				else if (time[33] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[33] = 1;
					ivTime[33].setImageResource(R.drawable.save);
					Log.v("16:30", "��Ϊ����");
				}

			}
		});

		ivTime[34].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[34] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[34] = 2;
					ivTime[34].setImageResource(R.drawable.comf);
					Log.v("17:00", "��Ϊ����");
				}
				else if (time[34] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[34] = 1;
					ivTime[34].setImageResource(R.drawable.save);
					Log.v("17:00", "��Ϊ����");
				}

			}
		});

		ivTime[35].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[35] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[35] = 2;
					ivTime[35].setImageResource(R.drawable.comf);
					Log.v("17:30", "��Ϊ����");
				}
				else if (time[35] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[35] = 1;
					ivTime[35].setImageResource(R.drawable.save);
					Log.v("17:30", "��Ϊ����");
				}

			}
		});

		ivTime[36].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[36] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[36] = 2;
					ivTime[36].setImageResource(R.drawable.comf);
					Log.v("18:00", "��Ϊ����");
				}
				else if (time[36] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[36] = 1;
					ivTime[36].setImageResource(R.drawable.save);
					Log.v("18:00", "��Ϊ����");
				}

			}
		});

		ivTime[37].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[37] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[37] = 2;
					ivTime[37].setImageResource(R.drawable.comf);
					Log.v("18:30", "��Ϊ����");
				}
				else if (time[37] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[37] = 1;
					ivTime[37].setImageResource(R.drawable.save);
					Log.v("18:30", "��Ϊ����");
				}

			}
		});

		ivTime[38].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[38] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[38] = 2;
					ivTime[38].setImageResource(R.drawable.comf);
					Log.v("19:00", "��Ϊ����");
				}
				else if (time[38] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[38] = 1;
					ivTime[38].setImageResource(R.drawable.save);
					Log.v("19:00", "��Ϊ����");
				}

			}
		});

		ivTime[39].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[39] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[39] = 2;
					ivTime[39].setImageResource(R.drawable.comf);
					Log.v("19:30", "��Ϊ����");
				}
				else if (time[39] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[39] = 1;
					ivTime[39].setImageResource(R.drawable.save);
					Log.v("19:30", "��Ϊ����");
				}

			}
		});

		ivTime[40].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[40] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[40] = 2;
					ivTime[40].setImageResource(R.drawable.comf);
					Log.v("20:00", "��Ϊ����");
				}
				else if (time[40] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[40] = 1;
					ivTime[40].setImageResource(R.drawable.save);
					Log.v("20:00", "��Ϊ����");
				}

			}
		});

		ivTime[41].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[41] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[41] = 2;
					ivTime[41].setImageResource(R.drawable.comf);
					Log.v("20:30", "��Ϊ����");
				}
				else if (time[41] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[41] = 1;
					ivTime[41].setImageResource(R.drawable.save);
					Log.v("20:30", "��Ϊ����");
				}

			}
		});

		ivTime[42].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[42] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[42] = 2;
					ivTime[42].setImageResource(R.drawable.comf);
					Log.v("21:00", "��Ϊ����");
				}
				else if (time[42] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[42] = 1;
					ivTime[42].setImageResource(R.drawable.save);
					Log.v("21:00", "��Ϊ����");
				}

			}
		});

		ivTime[43].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[43] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[43] = 2;
					ivTime[43].setImageResource(R.drawable.comf);
					Log.v("21:30", "��Ϊ����");
				}
				else if (time[43] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[43] = 1;
					ivTime[43].setImageResource(R.drawable.save);
					Log.v("21:30", "��Ϊ����");
				}

			}
		});

		ivTime[44].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[44] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[44] = 2;
					ivTime[44].setImageResource(R.drawable.comf);
					Log.v("22:00", "��Ϊ����");
				}
				else if (time[44] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[44] = 1;
					ivTime[44].setImageResource(R.drawable.save);
					Log.v("22:00", "��Ϊ����");
				}

			}
		});

		ivTime[45].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[45] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[45] = 2;
					ivTime[45].setImageResource(R.drawable.comf);
					Log.v("22:30", "��Ϊ����");
				}
				else if (time[45] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[45] = 1;
					ivTime[45].setImageResource(R.drawable.save);
					Log.v("22:30", "��Ϊ����");
				}

			}
		});

		ivTime[46].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[46] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[46] = 2;
					ivTime[46].setImageResource(R.drawable.comf);
					Log.v("23:00", "��Ϊ����");
				}
				else if (time[46] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[46] = 1;
					ivTime[46].setImageResource(R.drawable.save);
					Log.v("23:00", "��Ϊ����");
				}

			}
		});

		ivTime[47].setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���ģʽ��ѡ��ť�л�Ϊ�Զ���
				rbModelSelf.setChecked(true);
				if (time[47] == 1)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[47] = 2;
					ivTime[47].setImageResource(R.drawable.comf);
					Log.v("23:30", "��Ϊ����");
				}
				else if (time[47] == 2)
				{// ��ǰΪ�����¶ȣ���Ϊ������
					time[47] = 1;
					ivTime[47].setImageResource(R.drawable.save);
					Log.v("23:30", "��Ϊ����");
				}

			}
		});
	}

	// ����Ӧ��Runnable
	private Runnable mNetRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			String url = baseURL + "refreshProgram.php?roomId=" + roomId
					+ "&WD=" + workDay + "&SAT=" + satDay + "&SUN=" + sunDay
					+ "&t0=" + time[0] + "&t1=" + time[1] + "&t2=" + time[2]
					+ "&t3=" + time[3] + "&t4=" + time[4] + "&t5=" + time[5]
					+ "&t6=" + time[6] + "&t7=" + time[7] + "&t8=" + time[8]
					+ "&t9=" + time[9] + "&t10=" + time[10] + "&t11="
					+ time[11] + "&t12=" + time[12] + "&t13=" + time[13]
					+ "&t14=" + time[14] + "&t15=" + time[15] + "&t16="
					+ time[16] + "&t17=" + time[17] + "&t18=" + time[18]
					+ "&t19=" + time[19] + "&t20=" + time[20] + "&t21="
					+ time[21] + "&t22=" + time[22] + "&t23=" + time[23]
					+ "&t24=" + time[24] + "&t25=" + time[25] + "&t26="
					+ time[26] + "&t27=" + time[27] + "&t28=" + time[28]
					+ "&t29=" + time[29] + "&t30=" + time[30] + "&t31="
					+ time[31] + "&t32=" + time[32] + "&t33=" + time[33]
					+ "&t34=" + time[34] + "&t35=" + time[35] + "&t36="
					+ time[36] + "&t37=" + time[37] + "&t38=" + time[38]
					+ "&t39=" + time[39] + "&t40=" + time[40] + "&t41="
					+ time[41] + "&t42=" + time[42] + "&t43=" + time[43]
					+ "&t44=" + time[44] + "&t45=" + time[45] + "&t46="
					+ time[46] + "&t47=" + time[47] + "&ct=" + ct + "&st=" + st;
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

	// ��ʼ��Runnable
	private Runnable mCreateRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			// ���Ȼ�ȡboilerId
			String url = baseURL + "getProgram.php?roomId=" + roomId;
			MyHttp myhttp = new MyHttp();
			String retStr = myhttp.httpGet(url);
			try
			{
				JSONObject jsonObject = new JSONObject(retStr);
				String re_err = jsonObject.getString("error");
				ct = jsonObject.getString("ct");
				st = jsonObject.getString("st");
				String tempEveryTime[] = new String[48];
				for (int i = 0; i < 48; i++)
				{
					tempEveryTime[i] = jsonObject.getString("t" + i);
				}
				if (re_err.equals("0"))
				{
					for (int i = 0; i < 48; i++)
					{
						if (tempEveryTime[i].equals("2"))
						{// ������
							time[i] = 2;
						}
						else if (tempEveryTime[i].equals("1"))
						{// ������
							time[i] = 1;
						}
						else if (tempEveryTime[i].equals("0"))
						{// ����״̬
							time[i] = 1;
						}
						else if (tempEveryTime[i].equals("")
								|| tempEveryTime[i].equals("null"))
						{// ������
							time[i] = 1;
						}
					}

					mHandler.post(mViewRunnable);
				}
				else
				{// δ��ȡ��json����
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
			// ȡ���Ի���
			ad.cancel();

		}
	};

	// ��ʼ��UI��Runnable
	private Runnable mViewRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			for (int i = 0; i < 48; i++)
			{
				if (time[i] == 2)
				{
					ivTime[i].setImageResource(R.drawable.comf);
				}
				else
				{
					ivTime[i].setImageResource(R.drawable.save);
				}
			}
			if (ct.equals("") || ct.equals("null"))
				ct = "N/A";
			if (st.equals("") || st.equals("null"))
				st = "N/A";
			Log.v("��ʼ��ComfT", ct);
			Log.v("��ʼ��SaveT", st);
			// tvComfTemp.setText(ct);
			// tvSaveTemp.setText(st);
			if (ct.equals("N/A"))
			{
			}
			else
			{
				sbComfTemp.setProgress(Integer.parseInt(ct) - 5);
				if ((Integer.parseInt(ct) - 5) == 0)
					tvComfTemp.setText("5");
			}
			if (st.equals("N/A"))
			{
			}
			else
			{
				sbSaveTemp.setProgress(Integer.parseInt(st) - 5);
				if ((Integer.parseInt(st) - 5) == 0)
					tvSaveTemp.setText("5");
			}
			Toast.makeText(getApplicationContext(), "��ʼ�����", Toast.LENGTH_LONG)
					.show();
		}

	};

	// ģʽ�ı�ʱ�ı�UI
	private Runnable mModelRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			if (checkedNow == 1)
			{// ģʽ1��ѡ��
				for (int i = 0; i < 14; i++)
				{
					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
				for (int i = 14; i < 36; i++)
				{
					time[i] = 1;
					ivTime[i].setImageResource(R.drawable.save);
				}
				for (int i = 36; i < 48; i++)
				{

					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
			}
			else if (checkedNow == 2)
			{// ģʽ2��ѡ��
				for (int i = 0; i < 14; i++)
				{
					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
				for (int i = 14; i < 36; i++)
				{
					time[i] = 1;
					ivTime[i].setImageResource(R.drawable.save);
				}
				for (int i = 23; i < 28; i++)
				{
					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
				for (int i = 36; i < 48; i++)
				{
					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
			}
			else if (checkedNow == 3)
			{// ģʽ3��ѡ��
				for (int i = 0; i < 48; i++)
				{
					time[i] = 2;
					ivTime[i].setImageResource(R.drawable.comf);
				}
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
				Toast.makeText(ProgramUI.this, "Ӧ�óɹ���", Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(ProgramUI.this, "Ӧ��ʧ�ܣ�", Toast.LENGTH_SHORT)
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
				ProgramUI.this.finish();

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case (android.R.id.home):
			ProgramUI.this.finish();
			return true;
		case(R.id.muLogOut):
			Toast.makeText(getApplicationContext(), "ע���˵�����",
					Toast.LENGTH_LONG).show();
			// ��Ҫ�����ݿ���ļ�¼�û��ֶ���Ϊ0����Intent����¼ҳ�棬����������������֮�µ�Activity��
			Logout lgo = new Logout();
			if (lgo.Lgot(getApplicationContext()) == true)
				Toast.makeText(getApplicationContext(), "�ɹ�ע����",
						Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), ChooseLogoUI.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("Logout", "true");
			startActivity(intent);
			return true;
		case (R.id.muMoreSet):
			Toast.makeText(getApplicationContext(), "����˵�����",
					Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
