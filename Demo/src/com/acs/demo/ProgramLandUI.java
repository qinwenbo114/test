package com.acs.demo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ProgramLandUI extends Activity {
    private String baseURL = "http://192.168.0.97/Android/";
    // private String baseURL = "http://10.0.2.2/Android/";

    // get RoomId form Intent
    private String roomId = "1";

    // 创建数据应用网络连接线程对象
    private Thread mNetWork = null;

    // 初始化线程
    private Thread mCreateWork = null;

    // 单时段更新线程
    private Thread mTimeTempWork = null;

    // seekBar温度更新线程
    private Thread mSeekBarWork = null;

    // handler更新UI
    private Handler mHandler = null;

    // handler弹出网络异常
    private Handler mWarmingHandler = null;

    // Temp状态,半小时一个，共48个点，在程序初始化时被赋值为2(舒适)或1(节能)
    private int time[] = new int[48];
    // 舒适温与节能温
    private String ct = null;
    private String st = null;
    // 控件声明
    private ImageView ivTime[] = new ImageView[48];
    private RadioGroup rgModel = null;
    private RadioButton rbModel1 = null;
    private RadioButton rbModel2 = null;
    private RadioButton rbModel3 = null;
    private RadioButton rbModelSelf = null;
    // private CheckBox cbWorkDay = null;
    // private CheckBox cbSatDay = null;
    // private CheckBox cbSunDay = null;
    // private Button btApply = null;
    private TextView tvComfTemp = null;
    private TextView tvSaveTemp = null;
    private SeekBar sbComfTemp = null;
    private SeekBar sbSaveTemp = null;

    // 记录当前所选RadioButton
    private int checkedNow = 0;
    // 记录应用日期
    // private int workDay = 0;
    // private int satDay = 0;
    // private int sunDay = 0;
    // 记录当前所选time和temp
    private int timeChecked = 0;
    private int tempChecked = 0;
    // 记录当前日期
    private int day = 0;
    // 记录房间名
    private String roomName = null;
    // 初始化模式
    // private int mode;
    // 应用反馈
    // private boolean reApply = false;
    // 手势监测
    private GestureDetector detector;
    // 加载中对话框
    private AlertDialog ad;
    // 网络连接状况对话框
    private AlertDialog adwarming;
    
    private String sun;
    private String mon;
    private String tues;
    private String wed;
    private String thur;
    private String fri;
    private String sat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_program_land2);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从RoomUI中的putExtra()方法传递过来的键值对
	roomId = bundle.getString("ID");// 取出键值对中ID键所对应的值，并赋值给roomId
	roomName = bundle.getString("NAME");
	day = bundle.getInt("day");

	mHandler = new Handler();// 控制器更新UI
	mWarmingHandler = new Handler();

	// 绑定控件
	rgModel = (RadioGroup) findViewById(R.id.radioGroupModel);
	rbModel1 = (RadioButton) findViewById(R.id.rbModel1);
	rbModel2 = (RadioButton) findViewById(R.id.rbModel2);
	rbModel3 = (RadioButton) findViewById(R.id.rbModel3);
	rbModelSelf = (RadioButton) findViewById(R.id.rbModelSelf);
	// cbWorkDay = (CheckBox) findViewById(R.id.cbWorkDay);
	// cbSatDay = (CheckBox) findViewById(R.id.cbSatDay);
	// cbSunDay = (CheckBox) findViewById(R.id.cbSunDay);
	// btApply = (Button) findViewById(R.id.btApply);
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
	
	// 加载中英文
	sun = this.getString(R.string.sunday);
	mon = this.getString(R.string.monday);
	tues = this.getString(R.string.tuesday);
	wed = this.getString(R.string.wednesday);
	thur = this.getString(R.string.thursday);
	fri = this.getString(R.string.friday);
	sat = this.getString(R.string.saturday);

	// 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(ProgramLandUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(ProgramLandUI.this).setView(dialog)
		.create();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(ProgramLandUI.this).setView(
		dialogWarming).create();

	// 启动初始化线程
	// mCreateWork = new Thread(mCreateRunnable);
	// mCreateWork.start();

	// ActionBar标题显示

	ActionBar acb = getActionBar();
	try {
	    String schedule = this.getString(R.string.schedule);
	    acb.setTitle(roomName + " " + schedule);
	    acb.setDisplayHomeAsUpEnabled(true);
	    acb.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	ArrayList<CharSequence> a1 = new ArrayList<CharSequence>();
	a1.add(sun);
	a1.add(mon);
	a1.add(tues);
	a1.add(wed);
	a1.add(thur);
	a1.add(fri);
	a1.add(sat);
	ArrayAdapter<CharSequence> dropDownAdapter = new ArrayAdapter<CharSequence>(
		this, R.layout.spinner_white, a1);
	try {
	    acb.setListNavigationCallbacks(dropDownAdapter,
		    new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(
				int itemPosition, long itemId) {
			    // TODO Auto-generated method stub
			    ad.show();// 显示数据加载对话框

			    switch (itemPosition) {
			    case 0:
				day = 0;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 1:
				/*Toast.makeText(getApplicationContext(), "周一",
					Toast.LENGTH_LONG).show();*/
				// 修改day，重新执行初始化线程
				day = 1;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 2:
				/*Toast.makeText(getApplicationContext(), "周二",
					Toast.LENGTH_LONG).show();*/
				day = 2;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 3:
				/*Toast.makeText(getApplicationContext(), "周三",
					Toast.LENGTH_LONG).show();*/
				day = 3;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 4:
				day = 4;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 5:
				day = 5;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    case 6:
				day = 6;
				mCreateWork = new Thread(mCreateRunnable);
				mCreateWork.start();
				break;
			    }
			    return false;
			}
		    });
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// 单选按钮监听事件
	rgModel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		int rbID = group.getCheckedRadioButtonId();
		if (rbID == R.id.rbModel1) {
		    // UI更新为model1
		    // mode = 0;
		    checkedNow = 1;
		    mHandler.post(mModelRunnable);
		} else if (rbID == R.id.rbModel2) {
		    // UI更新为model2
		    checkedNow = 2;
		    // mode = 1;
		    mHandler.post(mModelRunnable);
		} else if (rbID == R.id.rbModel3) {
		    // UI更新为modle3
		    checkedNow = 3;
		    // mode = 2;
		    mHandler.post(mModelRunnable);
		} else if (rbID == R.id.rbModelSelf) {
		    // UI更新为自定义model
		    checkedNow = 4;
		    // mode = 3;
		}
	    }
	});

	// 温度条监听事件
	sbComfTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// 当手指抬起时 更新舒适温到数据库
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();

	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		// TODO Auto-generated method stub
		int t = 5 + sbComfTemp.getProgress();
		ct = "" + t;
		tvComfTemp.setText("" + t);
		Log.v("ComfTemp", t + "");
	    }
	});

	sbSaveTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// 当手指抬起时 更新舒适温到数据库
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();

	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		// TODO Auto-generated method stub
		int t = 5 + sbSaveTemp.getProgress();
		st = "" + t;
		tvSaveTemp.setText("" + t);
		Log.v("SaveTemp", t + "");
	    }
	});

	// 手势操作
	/*
	 * detector = new GestureDetector(this, new GestureListener());
	 * LinearLayout layout = (LinearLayout)
	 * findViewById(R.id.programLandLayout); layout.setOnTouchListener(new
	 * TouhListener()); layout.setLongClickable(true);
	 */

	// 编程图片监听事件
	ivTime[0].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[0] == 1) {// 当前为节能温度，改为舒适温
		    time[0] = 2;
		    ivTime[0].setImageResource(R.drawable.comf_land);
		    Log.v("0:00", "改为舒适");

		} else if (time[0] == 2) {// 当前为舒适温度，改为防冻温
		    time[0] = 0;
		    ivTime[0].setImageResource(R.drawable.antifreeze_land);
		    Log.v("0:00", "改为防冻");
		} else if (time[0] == 0) {// 当前为防冻温度，改为节能温
		    time[0] = 1;
		    ivTime[0].setImageResource(R.drawable.save_land);
		    Log.v("0:00", "改为节能");
		}
		SetTimeTemp(0);

	    }
	});

	ivTime[1].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[1] == 1) {// 当前为节能温度，改为舒适温
		    time[1] = 2;
		    ivTime[1].setImageResource(R.drawable.comf_land);
		    Log.v("0:30", "改为舒适");

		} else if (time[1] == 2) {// 当前为舒适温度，改为防冻温
		    time[1] = 0;
		    ivTime[1].setImageResource(R.drawable.antifreeze_land);
		    Log.v("0:30", "改为防冻");
		} else if (time[1] == 0) {// 当前为防冻温度，改为节能温
		    time[1] = 1;
		    ivTime[1].setImageResource(R.drawable.save_land);
		    Log.v("0:30", "改为节能");
		}
		SetTimeTemp(1);

	    }
	});

	ivTime[2].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[2] == 1) {// 当前为节能温度，改为舒适温
		    time[2] = 2;
		    ivTime[2].setImageResource(R.drawable.comf_land);
		    Log.v("1:00", "改为舒适");
		} else if (time[2] == 2) {// 当前为舒适温度，改为防冻温
		    time[2] = 0;
		    ivTime[2].setImageResource(R.drawable.antifreeze_land);
		    Log.v("1:00", "改为防冻");
		} else if (time[2] == 0) {// 当前为防冻温度，改为节能温
		    time[2] = 1;
		    ivTime[2].setImageResource(R.drawable.save_land);
		    Log.v("1:00", "改为节能");
		}
		SetTimeTemp(2);

	    }
	});

	ivTime[3].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[3] == 1) {// 当前为节能温度，改为舒适温
		    time[3] = 2;
		    ivTime[3].setImageResource(R.drawable.comf_land);
		    Log.v("1:30", "改为舒适");
		} else if (time[3] == 2) {// 当前为舒适温度，改为防冻温
		    time[3] = 0;
		    ivTime[3].setImageResource(R.drawable.antifreeze_land);
		    Log.v("1:30", "改为防冻");
		} else if (time[3] == 0) {// 当前为防冻温度，改为节能温
		    time[3] = 1;
		    ivTime[3].setImageResource(R.drawable.save_land);
		    Log.v("1:30", "改为节能");
		}
		SetTimeTemp(3);

	    }
	});

	ivTime[4].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[4] == 1) {// 当前为节能温度，改为舒适温
		    time[4] = 2;
		    ivTime[4].setImageResource(R.drawable.comf_land);
		    Log.v("2:00", "改为舒适");
		} else if (time[4] == 2) {// 当前为舒适温度，改为防冻温
		    time[4] = 0;
		    ivTime[4].setImageResource(R.drawable.antifreeze_land);
		    Log.v("2:00", "改为防冻");
		} else if (time[4] == 0) {// 当前为防冻温度，改为节能温
		    time[4] = 1;
		    ivTime[4].setImageResource(R.drawable.save_land);
		    Log.v("2:00", "改为节能");
		}
		SetTimeTemp(4);

	    }
	});

	ivTime[5].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[5] == 1) {// 当前为节能温度，改为舒适温
		    time[5] = 2;
		    ivTime[5].setImageResource(R.drawable.comf_land);
		    Log.v("2:30", "改为舒适");
		} else if (time[5] == 2) {// 当前为舒适温度，改为防冻温
		    time[5] = 0;
		    ivTime[5].setImageResource(R.drawable.antifreeze_land);
		    Log.v("2:30", "改为防冻");
		} else if (time[5] == 0) {// 当前为防冻温度，改为节能温
		    time[5] = 1;
		    ivTime[5].setImageResource(R.drawable.save_land);
		    Log.v("2:30", "改为节能");
		}
		SetTimeTemp(5);

	    }
	});

	ivTime[6].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[6] == 1) {// 当前为节能温度，改为舒适温
		    time[6] = 2;
		    ivTime[6].setImageResource(R.drawable.comf_land);
		    Log.v("3:00", "改为舒适");
		} else if (time[6] == 2) {// 当前为舒适温度，改为防冻温
		    time[6] = 0;
		    ivTime[6].setImageResource(R.drawable.antifreeze_land);
		    Log.v("3:00", "改为防冻");
		} else if (time[6] == 0) {// 当前为防冻温度，改为节能温
		    time[6] = 1;
		    ivTime[6].setImageResource(R.drawable.save_land);
		    Log.v("3:00", "改为节能");
		}
		SetTimeTemp(6);

	    }
	});

	ivTime[7].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[7] == 1) {// 当前为节能温度，改为舒适温
		    time[7] = 2;
		    ivTime[7].setImageResource(R.drawable.comf_land);
		    Log.v("3:30", "改为舒适");
		} else if (time[7] == 2) {// 当前为舒适温度，改为防冻温
		    time[7] = 0;
		    ivTime[7].setImageResource(R.drawable.antifreeze_land);
		    Log.v("3:30", "改为防冻");
		} else if (time[7] == 0) {// 当前为防冻温度，改为节能温
		    time[7] = 1;
		    ivTime[7].setImageResource(R.drawable.save_land);
		    Log.v("3:30", "改为节能");
		}
		SetTimeTemp(7);

	    }
	});

	ivTime[8].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[8] == 1) {// 当前为节能温度，改为舒适温
		    time[8] = 2;
		    ivTime[8].setImageResource(R.drawable.comf_land);
		    Log.v("4:00", "改为舒适");
		} else if (time[8] == 2) {// 当前为舒适温度，改为防冻温
		    time[8] = 0;
		    ivTime[8].setImageResource(R.drawable.antifreeze_land);
		    Log.v("4:00", "改为防冻");
		} else if (time[8] == 0) {// 当前为防冻温度，改为节能温
		    time[8] = 1;
		    ivTime[8].setImageResource(R.drawable.save_land);
		    Log.v("4:00", "改为防冻");
		}
		SetTimeTemp(8);

	    }
	});

	ivTime[9].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[9] == 1) {// 当前为节能温度，改为舒适温
		    time[9] = 2;
		    ivTime[9].setImageResource(R.drawable.comf_land);
		    Log.v("4:30", "改为舒适");
		} else if (time[9] == 2) {// 当前为舒适温度，改为防冻温
		    time[9] = 0;
		    ivTime[9].setImageResource(R.drawable.antifreeze_land);
		    Log.v("4:30", "改为防冻");
		} else if (time[9] == 0) {// 当前为防冻温度，改为节能温
		    time[9] = 1;
		    ivTime[9].setImageResource(R.drawable.save_land);
		    Log.v("4:30", "改为节能");
		}
		SetTimeTemp(9);

	    }
	});

	ivTime[10].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[10] == 1) {// 当前为节能温度，改为舒适温
		    time[10] = 2;
		    ivTime[10].setImageResource(R.drawable.comf_land);
		    Log.v("5:00", "改为舒适");
		} else if (time[10] == 2) {// 当前为舒适温度，改为防冻温
		    time[10] = 0;
		    ivTime[10].setImageResource(R.drawable.antifreeze_land);
		    Log.v("5:00", "改为防冻");
		} else if (time[10] == 0) {// 当前为防冻温度，改为节能温
		    time[10] = 1;
		    ivTime[10].setImageResource(R.drawable.save_land);
		    Log.v("5:00", "改为节能");
		}
		SetTimeTemp(10);

	    }
	});

	ivTime[11].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[11] == 1) {// 当前为节能温度，改为舒适温
		    time[11] = 2;
		    ivTime[11].setImageResource(R.drawable.comf_land);
		    Log.v("5:30", "改为舒适");
		} else if (time[11] == 2) {// 当前为舒适温度，改为防冻温
		    time[11] = 0;
		    ivTime[11].setImageResource(R.drawable.antifreeze_land);
		    Log.v("5:30", "改为防冻");
		} else if (time[11] == 0) {// 当前为防冻温度，改为节能温
		    time[11] = 1;
		    ivTime[11].setImageResource(R.drawable.save_land);
		    Log.v("5:30", "改为节能");
		}
		SetTimeTemp(11);

	    }
	});

	ivTime[12].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[12] == 1) {// 当前为节能温度，改为舒适温
		    time[12] = 2;
		    ivTime[12].setImageResource(R.drawable.comf_land);
		    Log.v("6:00", "改为舒适");
		} else if (time[12] == 2) {// 当前为舒适温度，改为防冻温
		    time[12] = 0;
		    ivTime[12].setImageResource(R.drawable.antifreeze_land);
		    Log.v("6:00", "改为防冻");
		} else if (time[12] == 0) {// 当前为防冻温度，改为节能温
		    time[12] = 1;
		    ivTime[12].setImageResource(R.drawable.save_land);
		    Log.v("6:00", "改为节能");
		}
		SetTimeTemp(12);

	    }
	});

	ivTime[13].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[13] == 1) {// 当前为节能温度，改为舒适温
		    time[13] = 2;
		    ivTime[13].setImageResource(R.drawable.comf_land);
		    Log.v("6:30", "改为舒适");
		} else if (time[13] == 2) {// 当前为舒适温度，改为防冻温
		    time[13] = 0;
		    ivTime[13].setImageResource(R.drawable.antifreeze_land);
		    Log.v("6:30", "改为防冻");
		} else if (time[13] == 0) {// 当前为防冻温度，改为节能温
		    time[13] = 1;
		    ivTime[13].setImageResource(R.drawable.save_land);
		    Log.v("6:30", "改为节能");
		}
		SetTimeTemp(13);

	    }
	});

	ivTime[14].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[14] == 1) {// 当前为节能温度，改为舒适温
		    time[14] = 2;
		    ivTime[14].setImageResource(R.drawable.comf_land);
		    Log.v("7:00", "改为舒适");
		} else if (time[14] == 2) {// 当前为舒适温度，改为防冻温
		    time[14] = 0;
		    ivTime[14].setImageResource(R.drawable.antifreeze_land);
		    Log.v("7:00", "改为防冻");
		} else if (time[14] == 0) {// 当前为防冻温度，改为节能温
		    time[14] = 1;
		    ivTime[14].setImageResource(R.drawable.save_land);
		    Log.v("7:00", "改为节能");
		}
		SetTimeTemp(14);

	    }
	});

	ivTime[15].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[15] == 1) {// 当前为节能温度，改为舒适温
		    time[15] = 2;
		    ivTime[15].setImageResource(R.drawable.comf_land);
		    Log.v("7:30", "改为舒适");
		} else if (time[15] == 2) {// 当前为舒适温度，改为防冻温
		    time[15] = 0;
		    ivTime[15].setImageResource(R.drawable.antifreeze_land);
		    Log.v("7:30", "改为防冻");
		} else if (time[15] == 0) {// 当前为防冻温度，改为节能温
		    time[15] = 1;
		    ivTime[15].setImageResource(R.drawable.save_land);
		    Log.v("7:30", "改为节能");
		}
		SetTimeTemp(15);

	    }
	});

	ivTime[16].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[16] == 1) {// 当前为节能温度，改为舒适温
		    time[16] = 2;
		    ivTime[16].setImageResource(R.drawable.comf_land);
		    Log.v("8:00", "改为舒适");
		} else if (time[16] == 2) {// 当前为舒适温度，改为防冻温
		    time[16] = 0;
		    ivTime[16].setImageResource(R.drawable.antifreeze_land);
		    Log.v("8:00", "改为防冻");
		} else if (time[16] == 0) {// 当前为防冻温度，改为节能温
		    time[16] = 1;
		    ivTime[16].setImageResource(R.drawable.save_land);
		    Log.v("8:00", "改为节能");
		}
		SetTimeTemp(16);

	    }
	});

	ivTime[17].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[17] == 1) {// 当前为节能温度，改为舒适温
		    time[17] = 2;
		    ivTime[17].setImageResource(R.drawable.comf_land);
		    Log.v("8:30", "改为舒适");
		} else if (time[17] == 2) {// 当前为舒适温度，改为防冻温
		    time[17] = 0;
		    ivTime[17].setImageResource(R.drawable.antifreeze_land);
		    Log.v("8:30", "改为防冻");
		} else if (time[17] == 0) {// 当前为防冻温度，改为节能温
		    time[17] = 1;
		    ivTime[17].setImageResource(R.drawable.save_land);
		    Log.v("8:30", "改为节能");
		}
		SetTimeTemp(17);

	    }
	});

	ivTime[18].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[18] == 1) {// 当前为节能温度，改为舒适温
		    time[18] = 2;
		    ivTime[18].setImageResource(R.drawable.comf_land);
		    Log.v("9:00", "改为舒适");
		} else if (time[18] == 2) {// 当前为舒适温度，改为防冻温
		    time[18] = 0;
		    ivTime[18].setImageResource(R.drawable.antifreeze_land);
		    Log.v("9:00", "改为防冻");
		} else if (time[18] == 0) {// 当前为防冻温度，改为节能温
		    time[18] = 1;
		    ivTime[18].setImageResource(R.drawable.save_land);
		    Log.v("9:00", "改为节能");
		}
		SetTimeTemp(18);

	    }
	});

	ivTime[19].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[19] == 1) {// 当前为节能温度，改为舒适温
		    time[19] = 2;
		    ivTime[19].setImageResource(R.drawable.comf_land);
		    Log.v("9:30", "改为舒适");
		} else if (time[19] == 2) {// 当前为舒适温度，改为防冻温
		    time[19] = 0;
		    ivTime[19].setImageResource(R.drawable.antifreeze_land);
		    Log.v("9:30", "改为防冻");
		} else if (time[19] == 0) {// 当前为防冻温度，改为节能温
		    time[19] = 1;
		    ivTime[19].setImageResource(R.drawable.save_land);
		    Log.v("9:30", "改为节能");
		}
		SetTimeTemp(19);

	    }
	});

	ivTime[20].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[20] == 1) {// 当前为节能温度，改为舒适温
		    time[20] = 2;
		    ivTime[20].setImageResource(R.drawable.comf_land);
		    Log.v("10:00", "改为舒适");
		} else if (time[20] == 2) {// 当前为舒适温度，改为防冻温
		    time[20] = 0;
		    ivTime[20].setImageResource(R.drawable.antifreeze_land);
		    Log.v("10:00", "改为节能");
		} else if (time[20] == 0) {// 当前为防冻温度，改为节能温
		    time[20] = 1;
		    ivTime[20].setImageResource(R.drawable.save_land);
		    Log.v("10:00", "改为节能");
		}
		SetTimeTemp(20);

	    }
	});

	ivTime[21].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[21] == 1) {// 当前为节能温度，改为舒适温
		    time[21] = 2;
		    ivTime[21].setImageResource(R.drawable.comf_land);
		    Log.v("10:30", "改为舒适");
		} else if (time[21] == 2) {// 当前为舒适温度，改为防冻温
		    time[21] = 0;
		    ivTime[21].setImageResource(R.drawable.antifreeze_land);
		    Log.v("10:30", "改为防冻");
		} else if (time[21] == 0) {// 当前为防冻温度，改为节能温
		    time[21] = 1;
		    ivTime[21].setImageResource(R.drawable.save_land);
		    Log.v("10:30", "改为节能");
		}
		SetTimeTemp(21);

	    }
	});

	ivTime[22].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[22] == 1) {// 当前为节能温度，改为舒适温
		    time[22] = 2;
		    ivTime[22].setImageResource(R.drawable.comf_land);
		    Log.v("11:00", "改为舒适");
		} else if (time[22] == 2) {// 当前为舒适温度，改为防冻温
		    time[22] = 0;
		    ivTime[22].setImageResource(R.drawable.antifreeze_land);
		    Log.v("11:00", "改为防冻");
		} else if (time[22] == 0) {// 当前为防冻温度，改为节能温
		    time[22] = 1;
		    ivTime[22].setImageResource(R.drawable.save_land);
		    Log.v("11:00", "改为节能");
		}
		SetTimeTemp(22);

	    }
	});

	ivTime[23].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[23] == 1) {// 当前为节能温度，改为舒适温
		    time[23] = 2;
		    ivTime[23].setImageResource(R.drawable.comf_land);
		    Log.v("11:30", "改为舒适");
		} else if (time[23] == 2) {// 当前为舒适温度，改为防冻温
		    time[23] = 0;
		    ivTime[23].setImageResource(R.drawable.antifreeze_land);
		    Log.v("11:30", "改为防冻");
		} else if (time[23] == 0) {// 当前为防冻温度，改为节能温
		    time[23] = 1;
		    ivTime[23].setImageResource(R.drawable.save_land);
		    Log.v("11:30", "改为节能");
		}
		SetTimeTemp(23);

	    }
	});

	ivTime[24].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[24] == 1) {// 当前为节能温度，改为舒适温
		    time[24] = 2;
		    ivTime[24].setImageResource(R.drawable.comf_land);
		    Log.v("12:00", "改为舒适");
		} else if (time[24] == 2) {// 当前为舒适温度，改为防冻温
		    time[24] = 0;
		    ivTime[24].setImageResource(R.drawable.antifreeze_land);
		    Log.v("12:00", "改为防冻");
		} else if (time[24] == 0) {// 当前为防冻温度，改为节能温
		    time[24] = 1;
		    ivTime[24].setImageResource(R.drawable.save_land);
		    Log.v("12:00", "改为节能");
		}
		SetTimeTemp(24);

	    }
	});

	ivTime[25].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[25] == 1) {// 当前为节能温度，改为舒适温
		    time[25] = 2;
		    ivTime[25].setImageResource(R.drawable.comf_land);
		    Log.v("12:30", "改为舒适");
		} else if (time[25] == 2) {// 当前为舒适温度，改为防冻温
		    time[25] = 0;
		    ivTime[25].setImageResource(R.drawable.antifreeze_land);
		    Log.v("12:30", "改为防冻");
		} else if (time[25] == 0) {// 当前为防冻温度，改为节能温
		    time[25] = 1;
		    ivTime[25].setImageResource(R.drawable.save_land);
		    Log.v("12:30", "改为节能");
		}
		SetTimeTemp(25);

	    }
	});

	ivTime[26].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[26] == 1) {// 当前为节能温度，改为舒适温
		    time[26] = 2;
		    ivTime[26].setImageResource(R.drawable.comf_land);
		    Log.v("13:00", "改为舒适");
		} else if (time[26] == 2) {// 当前为舒适温度，改为防冻温
		    time[26] = 0;
		    ivTime[26].setImageResource(R.drawable.antifreeze_land);
		    Log.v("13:00", "改为防冻");
		} else if (time[26] == 0) {// 当前为防冻温度，改为节能温
		    time[26] = 1;
		    ivTime[26].setImageResource(R.drawable.save_land);
		    Log.v("13:00", "改为节能");
		}
		SetTimeTemp(26);

	    }
	});

	ivTime[27].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[27] == 1) {// 当前为节能温度，改为舒适温
		    time[27] = 2;
		    ivTime[27].setImageResource(R.drawable.comf_land);
		    Log.v("13:30", "改为舒适");
		} else if (time[27] == 2) {// 当前为舒适温度，改为防冻温
		    time[27] = 0;
		    ivTime[27].setImageResource(R.drawable.antifreeze_land);
		    Log.v("13:30", "改为防冻");
		} else if (time[27] == 0) {// 当前为防冻温度，改为节能温
		    time[27] = 1;
		    ivTime[27].setImageResource(R.drawable.save_land);
		    Log.v("13:30", "改为节能");
		}
		SetTimeTemp(27);

	    }
	});

	ivTime[28].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[28] == 1) {// 当前为节能温度，改为舒适温
		    time[28] = 2;
		    ivTime[28].setImageResource(R.drawable.comf_land);
		    Log.v("14:00", "改为舒适");
		} else if (time[28] == 2) {// 当前为舒适温度，改为防冻温
		    time[28] = 0;
		    ivTime[28].setImageResource(R.drawable.antifreeze_land);
		    Log.v("14:00", "改为防冻");
		} else if (time[28] == 0) {// 当前为防冻温度，改为节能温
		    time[28] = 1;
		    ivTime[28].setImageResource(R.drawable.save_land);
		    Log.v("14:00", "改为节能");
		}
		SetTimeTemp(28);

	    }
	});

	ivTime[29].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[29] == 1) {// 当前为节能温度，改为舒适温
		    time[29] = 2;
		    ivTime[29].setImageResource(R.drawable.comf_land);
		    Log.v("14:30", "改为舒适");
		} else if (time[29] == 2) {// 当前为舒适温度，改为防冻温
		    time[29] = 0;
		    ivTime[29].setImageResource(R.drawable.antifreeze_land);
		    Log.v("14:30", "改为防冻");
		} else if (time[29] == 0) {// 当前为防冻温度，改为节能温
		    time[29] = 1;
		    ivTime[29].setImageResource(R.drawable.save_land);
		    Log.v("14:30", "改为节能");
		}
		SetTimeTemp(29);

	    }
	});

	ivTime[30].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[30] == 1) {// 当前为节能温度，改为舒适温
		    time[30] = 2;
		    ivTime[30].setImageResource(R.drawable.comf_land);
		    Log.v("15:00", "改为舒适");
		} else if (time[30] == 2) {// 当前为舒适温度，改为防冻温
		    time[30] = 0;
		    ivTime[30].setImageResource(R.drawable.antifreeze_land);
		    Log.v("15:00", "改为防冻");
		} else if (time[30] == 0) {// 当前为防冻温度，改为节能温
		    time[30] = 1;
		    ivTime[30].setImageResource(R.drawable.save_land);
		    Log.v("15:00", "改为节能");
		}
		SetTimeTemp(30);

	    }
	});

	ivTime[31].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[31] == 1) {// 当前为节能温度，改为舒适温
		    time[31] = 2;
		    ivTime[31].setImageResource(R.drawable.comf_land);
		    Log.v("15:30", "改为舒适");
		} else if (time[31] == 2) {// 当前为舒适温度，改为防冻温
		    time[31] = 0;
		    ivTime[31].setImageResource(R.drawable.antifreeze_land);
		    Log.v("15:30", "改为防冻");
		} else if (time[31] == 0) {// 当前为防冻温度，改为节能温
		    time[31] = 1;
		    ivTime[31].setImageResource(R.drawable.save_land);
		    Log.v("15:30", "改为节能");
		}
		SetTimeTemp(31);

	    }
	});

	ivTime[32].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[32] == 1) {// 当前为节能温度，改为舒适温
		    time[32] = 2;
		    ivTime[32].setImageResource(R.drawable.comf_land);
		    Log.v("16:00", "改为舒适");
		} else if (time[32] == 2) {// 当前为舒适温度，改为防冻温
		    time[32] = 0;
		    ivTime[32].setImageResource(R.drawable.antifreeze_land);
		    Log.v("16:00", "改为防冻");
		} else if (time[32] == 0) {// 当前为防冻温度，改为节能温
		    time[32] = 1;
		    ivTime[32].setImageResource(R.drawable.save_land);
		    Log.v("16:00", "改为节能");
		}
		SetTimeTemp(32);

	    }
	});

	ivTime[33].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[33] == 1) {// 当前为节能温度，改为舒适温
		    time[33] = 2;
		    ivTime[33].setImageResource(R.drawable.comf_land);
		    Log.v("16:30", "改为舒适");
		} else if (time[33] == 2) {// 当前为舒适温度，改为防冻温
		    time[33] = 0;
		    ivTime[33].setImageResource(R.drawable.antifreeze_land);
		    Log.v("16:30", "改为防冻");
		} else if (time[33] == 0) {// 当前为防冻温度，改为节能温
		    time[33] = 1;
		    ivTime[33].setImageResource(R.drawable.save_land);
		    Log.v("16:30", "改为节能");
		}
		SetTimeTemp(33);

	    }
	});

	ivTime[34].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[34] == 1) {// 当前为节能温度，改为舒适温
		    time[34] = 2;
		    ivTime[34].setImageResource(R.drawable.comf_land);
		    Log.v("17:00", "改为舒适");
		} else if (time[34] == 2) {// 当前为舒适温度，改为防冻温
		    time[34] = 0;
		    ivTime[34].setImageResource(R.drawable.antifreeze_land);
		    Log.v("17:00", "改为防冻");
		} else if (time[34] == 0) {// 当前为防冻温度，改为节能温
		    time[34] = 1;
		    ivTime[34].setImageResource(R.drawable.save_land);
		    Log.v("17:00", "改为节能");
		}
		SetTimeTemp(34);

	    }
	});

	ivTime[35].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[35] == 1) {// 当前为节能温度，改为舒适温
		    time[35] = 2;
		    ivTime[35].setImageResource(R.drawable.comf_land);
		    Log.v("17:30", "改为舒适");
		} else if (time[35] == 2) {// 当前为舒适温度，改为防冻温
		    time[35] = 0;
		    ivTime[35].setImageResource(R.drawable.antifreeze_land);
		    Log.v("17:30", "改为防冻");
		} else if (time[35] == 0) {// 当前为防冻温度，改为节能温
		    time[35] = 1;
		    ivTime[35].setImageResource(R.drawable.save_land);
		    Log.v("17:30", "改为节能");
		}
		SetTimeTemp(35);

	    }
	});

	ivTime[36].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[36] == 1) {// 当前为节能温度，改为舒适温
		    time[36] = 2;
		    ivTime[36].setImageResource(R.drawable.comf_land);
		    Log.v("18:00", "改为舒适");
		} else if (time[36] == 2) {// 当前为舒适温度，改为防冻温
		    time[36] = 0;
		    ivTime[36].setImageResource(R.drawable.antifreeze_land);
		    Log.v("18:00", "改为防冻");
		} else if (time[36] == 0) {// 当前为防冻温度，改为节能温
		    time[36] = 1;
		    ivTime[36].setImageResource(R.drawable.save_land);
		    Log.v("18:00", "改为节能");
		}
		SetTimeTemp(36);

	    }
	});

	ivTime[37].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[37] == 1) {// 当前为节能温度，改为舒适温
		    time[37] = 2;
		    ivTime[37].setImageResource(R.drawable.comf_land);
		    Log.v("18:30", "改为舒适");
		} else if (time[37] == 2) {// 当前为舒适温度，改为防冻温
		    time[37] = 0;
		    ivTime[37].setImageResource(R.drawable.antifreeze_land);
		    Log.v("18:30", "改为防冻");
		} else if (time[37] == 0) {// 当前为防冻温度，改为节能温
		    time[37] = 1;
		    ivTime[37].setImageResource(R.drawable.save_land);
		    Log.v("18:30", "改为节能");
		}
		SetTimeTemp(37);

	    }
	});

	ivTime[38].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[38] == 1) {// 当前为节能温度，改为舒适温
		    time[38] = 2;
		    ivTime[38].setImageResource(R.drawable.comf_land);
		    Log.v("19:00", "改为舒适");
		} else if (time[38] == 2) {// 当前为舒适温度，改为防冻温
		    time[38] = 0;
		    ivTime[38].setImageResource(R.drawable.antifreeze_land);
		    Log.v("19:00", "改为防冻");
		} else if (time[38] == 0) {// 当前为防冻温度，改为节能温
		    time[38] = 1;
		    ivTime[38].setImageResource(R.drawable.save_land);
		    Log.v("19:00", "改为节能");
		}
		SetTimeTemp(38);

	    }
	});

	ivTime[39].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[39] == 1) {// 当前为节能温度，改为舒适温
		    time[39] = 2;
		    ivTime[39].setImageResource(R.drawable.comf_land);
		    Log.v("19:30", "改为舒适");
		} else if (time[39] == 2) {// 当前为舒适温度，改为防冻温
		    time[39] = 0;
		    ivTime[39].setImageResource(R.drawable.antifreeze_land);
		    Log.v("19:30", "改为防冻");
		} else if (time[39] == 0) {// 当前为防冻温度，改为节能温
		    time[39] = 1;
		    ivTime[39].setImageResource(R.drawable.save_land);
		    Log.v("19:30", "改为节能");
		}
		SetTimeTemp(39);

	    }
	});

	ivTime[40].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[40] == 1) {// 当前为节能温度，改为舒适温
		    time[40] = 2;
		    ivTime[40].setImageResource(R.drawable.comf_land);
		    Log.v("20:00", "改为舒适");
		} else if (time[40] == 2) {// 当前为舒适温度，改为防冻温
		    time[40] = 0;
		    ivTime[40].setImageResource(R.drawable.antifreeze_land);
		    Log.v("20:00", "改为防冻");
		} else if (time[40] == 0) {// 当前为防冻温度，改为节能温
		    time[40] = 1;
		    ivTime[40].setImageResource(R.drawable.save_land);
		    Log.v("20:00", "改为节能");
		}
		SetTimeTemp(40);

	    }
	});

	ivTime[41].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[41] == 1) {// 当前为节能温度，改为舒适温
		    time[41] = 2;
		    ivTime[41].setImageResource(R.drawable.comf_land);
		    Log.v("20:30", "改为舒适");
		} else if (time[41] == 2) {// 当前为舒适温度，改为防冻温
		    time[41] = 0;
		    ivTime[41].setImageResource(R.drawable.antifreeze_land);
		    Log.v("20:30", "改为防冻");
		} else if (time[41] == 0) {// 当前为防冻温度，改为节能温
		    time[41] = 1;
		    ivTime[41].setImageResource(R.drawable.save_land);
		    Log.v("20:30", "改为节能");
		}
		SetTimeTemp(41);

	    }
	});

	ivTime[42].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[42] == 1) {// 当前为节能温度，改为舒适温
		    time[42] = 2;
		    ivTime[42].setImageResource(R.drawable.comf_land);
		    Log.v("21:00", "改为舒适");
		} else if (time[42] == 2) {// 当前为舒适温度，改为防冻温
		    time[42] = 0;
		    ivTime[42].setImageResource(R.drawable.antifreeze_land);
		    Log.v("21:00", "改为防冻");
		} else if (time[42] == 0) {// 当前为防冻温度，改为节能温
		    time[42] = 1;
		    ivTime[42].setImageResource(R.drawable.save_land);
		    Log.v("21:00", "改为节能");
		}
		SetTimeTemp(42);
	    }
	});

	ivTime[43].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[43] == 1) {// 当前为节能温度，改为舒适温
		    time[43] = 2;
		    ivTime[43].setImageResource(R.drawable.comf_land);
		    Log.v("21:30", "改为舒适");
		} else if (time[43] == 2) {// 当前为舒适温度，改为防冻温
		    time[43] = 0;
		    ivTime[43].setImageResource(R.drawable.antifreeze_land);
		    Log.v("21:30", "改为防冻");
		} else if (time[43] == 0) {// 当前为防冻温度，改为节能温
		    time[43] = 1;
		    ivTime[43].setImageResource(R.drawable.save_land);
		    Log.v("21:30", "改为节能");
		}
		SetTimeTemp(43);

	    }
	});

	ivTime[44].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[44] == 1) {// 当前为节能温度，改为舒适温
		    time[44] = 2;
		    ivTime[44].setImageResource(R.drawable.comf_land);
		    Log.v("22:00", "改为舒适");
		} else if (time[44] == 2) {// 当前为舒适温度，改为防冻温
		    time[44] = 0;
		    ivTime[44].setImageResource(R.drawable.antifreeze_land);
		    Log.v("22:00", "改为防冻");
		} else if (time[44] == 0) {// 当前为防冻温度，改为节能温
		    time[44] = 1;
		    ivTime[44].setImageResource(R.drawable.save_land);
		    Log.v("22:00", "改为节能");
		}
		SetTimeTemp(44);

	    }
	});

	ivTime[45].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[45] == 1) {// 当前为节能温度，改为舒适温
		    time[45] = 2;
		    ivTime[45].setImageResource(R.drawable.comf_land);
		    Log.v("22:30", "改为舒适");
		} else if (time[45] == 2) {// 当前为舒适温度，改为防冻温
		    time[45] = 0;
		    ivTime[45].setImageResource(R.drawable.antifreeze_land);
		    Log.v("22:30", "改为防冻");
		} else if (time[45] == 0) {// 当前为防冻温度，改为节能温
		    time[45] = 1;
		    ivTime[45].setImageResource(R.drawable.save_land);
		    Log.v("22:30", "改为节能");
		}
		SetTimeTemp(45);

	    }
	});

	ivTime[46].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[46] == 1) {// 当前为节能温度，改为舒适温
		    time[46] = 2;
		    ivTime[46].setImageResource(R.drawable.comf_land);
		    Log.v("23:00", "改为舒适");
		} else if (time[46] == 2) {// 当前为舒适温度，改为防冻温
		    time[46] = 0;
		    ivTime[46].setImageResource(R.drawable.antifreeze_land);
		    Log.v("23:00", "改为防冻");
		} else if (time[46] == 0) {// 当前为防冻温度，改为节能温
		    time[46] = 1;
		    ivTime[46].setImageResource(R.drawable.save_land);
		    Log.v("23:00", "改为节能");
		}
		SetTimeTemp(46);

	    }
	});

	ivTime[47].setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 编程模式单选按钮切换为自定义
		rbModelSelf.setChecked(true);
		if (time[47] == 1) {// 当前为节能温度，改为舒适温
		    time[47] = 2;
		    ivTime[47].setImageResource(R.drawable.comf_land);
		    Log.v("23:30", "改为舒适");
		} else if (time[47] == 2) {// 当前为舒适温度，改为防冻温
		    time[47] = 0;
		    ivTime[47].setImageResource(R.drawable.antifreeze_land);
		    Log.v("23:30", "改为防冻");
		} else if (time[47] == 0) {// 当前为防冻温度，改为节能温
		    time[47] = 1;
		    ivTime[47].setImageResource(R.drawable.save_land);
		    Log.v("23:30", "改为节能");
		}
		SetTimeTemp(47);

	    }
	});
    }

    // 初始化Runnable
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    // 首先获取boilerId
	    String url = baseURL + "getProgram.php?roomId=" + roomId + "&day="
		    + day;
	    MyHttp myhttp = new MyHttp();
	    String retStr = myhttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		String longCt = jsonObject.getString("ct");
		String ct2[] = longCt.split("\\.");
		ct = ct2[0];
		String longSt = jsonObject.getString("st");
		String st2[] = longSt.split("\\.");
		st = st2[0];
		// String modeS = jsonObject.getString("m");
		// if (modeS.equals("")||modeS.equals("null")){
		// mode = 3;
		// }
		// else {
		// mode = Integer.parseInt(modeS);
		// }
		String tempEveryTime[] = new String[48];
		for (int i = 0; i < 48; i++) {
		    tempEveryTime[i] = jsonObject.getString("t" + i);
		}
		if (re_err.equals("0")) {
		    for (int i = 0; i < 48; i++) {
			if (tempEveryTime[i].equals("2")) {// 舒适温
			    time[i] = 2;
			} else if (tempEveryTime[i].equals("1")) {// 节能温
			    time[i] = 1;
			} else if (tempEveryTime[i].equals("0")) {// 防冻状态
			    time[i] = 0;
			} else if (tempEveryTime[i].equals("")
				|| tempEveryTime[i].equals("null")) {// 空数据
			    time[i] = 0;
			}
		    }

		    mHandler.post(mViewRunnable);
		} else {// 未获取到json数据
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }
	    // 取消对话框
	    ad.cancel();

	}
    };

    // 单时段状态改变更新
    private void SetTimeTemp(int i) {
	// 开辟线程，执行更新网络通信
	// mode = 3;
	timeChecked = i;
	tempChecked = time[i];
	mTimeTempWork = new Thread(mTimeTempRunnable);
	mTimeTempWork.start();
    }

    // 单时段更新Runnable
    private Runnable mTimeTempRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshProgramTimeTemp.php?roomId="
		    + roomId + "&day=" + day + "&time=" + timeChecked
		    + "&temp=" + tempChecked;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("更新TimeTemp状况", re_err);
		if (re_err.equals("1")) {
		    // 送msg到handler，弹出一个toast
		}
		if (adwarming.isShowing())
		    adwarming.dismiss();
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}
    };

    // 预定义模式应用Runnable
    private Runnable mNetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshProgramLandMode.php?roomId="
		    + roomId + "&day=" + day + "&t0=" + time[0] + "&t1="
		    + time[1] + "&t2=" + time[2] + "&t3=" + time[3] + "&t4="
		    + time[4] + "&t5=" + time[5] + "&t6=" + time[6] + "&t7="
		    + time[7] + "&t8=" + time[8] + "&t9=" + time[9] + "&t10="
		    + time[10] + "&t11=" + time[11] + "&t12=" + time[12]
		    + "&t13=" + time[13] + "&t14=" + time[14] + "&t15="
		    + time[15] + "&t16=" + time[16] + "&t17=" + time[17]
		    + "&t18=" + time[18] + "&t19=" + time[19] + "&t20="
		    + time[20] + "&t21=" + time[21] + "&t22=" + time[22]
		    + "&t23=" + time[23] + "&t24=" + time[24] + "&t25="
		    + time[25] + "&t26=" + time[26] + "&t27=" + time[27]
		    + "&t28=" + time[28] + "&t29=" + time[29] + "&t30="
		    + time[30] + "&t31=" + time[31] + "&t32=" + time[32]
		    + "&t33=" + time[33] + "&t34=" + time[34] + "&t35="
		    + time[35] + "&t36=" + time[36] + "&t37=" + time[37]
		    + "&t38=" + time[38] + "&t39=" + time[39] + "&t40="
		    + time[40] + "&t41=" + time[41] + "&t42=" + time[42]
		    + "&t43=" + time[43] + "&t44=" + time[44] + "&t45="
		    + time[45] + "&t46=" + time[46] + "&t47=" + time[47];
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("编程设置更新状况", re_err);

		if (re_err.equals("0")) {
		    // 应用成功，用handler弹出toast
		} else if (re_err.equals("1")) {
		    // 应用失败，用handler弹出toast
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}

    };

    // SeekBar更新线程
    private Runnable mSeekBarRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshCSTemp.php?roomId=" + roomId
		    + "&ct=" + ct + "&st=" + st;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("更新CSTemp状况", re_err);
		if (re_err.equals("1")) {
		    // 送msg到handler，弹出一个toast
		}
		if (adwarming.isShowing())
		    adwarming.dismiss();
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}
    };

    // 初始化UI的Runnable
    private Runnable mViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    for (int i = 0; i < 48; i++) {
		if (time[i] == 2) {
		    ivTime[i].setImageResource(R.drawable.comf_land);
		} else if (time[i] == 1) {
		    ivTime[i].setImageResource(R.drawable.save_land);
		} else {
		    ivTime[i].setImageResource(R.drawable.antifreeze_land);
		}
	    }
	    if (ct.equals("") || ct.equals("null"))
		ct = "N/A";
	    if (st.equals("") || st.equals("null"))
		st = "N/A";
	    Log.v("初始化ComfT", ct);
	    Log.v("初始化SaveT", st);
	    // tvComfTemp.setText(ct);
	    // tvSaveTemp.setText(st);
	    if (ct.equals("N/A")) {
	    } else {
		sbComfTemp.setProgress(Integer.parseInt(ct) - 5);
		if ((Integer.parseInt(ct) - 5) == 0)
		    tvComfTemp.setText("5");
	    }
	    if (st.equals("N/A")) {
	    } else {
		sbSaveTemp.setProgress(Integer.parseInt(st) - 5);
		if ((Integer.parseInt(st) - 5) == 0)
		    tvSaveTemp.setText("5");
	    }
	    /*
	     * switch (mode){ case 0: rbModel1.setChecked(true); break; case 1:
	     * rbModel2.setChecked(true); break; case 2:
	     * rbModel3.setChecked(true); break; case 3:
	     * rbModelSelf.setChecked(true); }
	     */
	    rbModelSelf.setChecked(true);
	    /*Toast.makeText(getApplicationContext(), "初始化完成", Toast.LENGTH_LONG)
		    .show();*/
	}

    };

    // 模式改变时改变UI
    private Runnable mModelRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (checkedNow == 1) {// 模式1被选中
		for (int i = 0; i < 14; i++) {
		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
		for (int i = 14; i < 36; i++) {
		    time[i] = 1;
		    ivTime[i].setImageResource(R.drawable.save_land);
		}
		for (int i = 36; i < 48; i++) {

		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
	    } else if (checkedNow == 2) {// 模式2被选中
		for (int i = 0; i < 14; i++) {
		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
		for (int i = 14; i < 36; i++) {
		    time[i] = 1;
		    ivTime[i].setImageResource(R.drawable.save_land);
		}
		for (int i = 23; i < 28; i++) {
		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
		for (int i = 36; i < 48; i++) {
		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
	    } else if (checkedNow == 3) {// 模式3被选中
		for (int i = 0; i < 48; i++) {
		    time[i] = 2;
		    ivTime[i].setImageResource(R.drawable.comf_land);
		}
	    }
	    mNetWork = new Thread(mNetRunnable);
	    mNetWork.start();
	}
    };

    // 网络异常对话框弹出Runnable
    private Runnable mWarmingRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub

	    // 加载对话框，用于网路线程异常时弹出

	    if (adwarming.isShowing()) {
	    } else
		adwarming.show();
	    // adwarming.show();
	}

    };

    /*
     * // 下面写一个向右滑动返回上层Activity的手势操作 // 触摸屏幕监听 class TouhListener implements
     * OnTouchListener {
     * 
     * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
     * Auto-generated method stub // Toast.makeText(getApplicationContext(),
     * "----?", // event.getAction()).show(); return
     * detector.onTouchEvent(event); }
     * 
     * }
     * 
     * // 手势滑动监听 class GestureListener implements OnGestureListener {
     * 
     * @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float
     * velocityX, float velocityY) { // e1 触摸的起始位置，e2 触摸的结束位置，velocityX
     * X轴每一秒移动的像素速度（大概这个意思） velocityY // 就是Ｙ咯 // 手势左,上为正 ——，右，下为负正 if
     * ((e2.getX() - e1.getX()) > 50) { // 为什么是50？
     * 这个根据你的模拟器大小来定，看看模拟器宽度，e2.getX()-e1.getX()<屏幕宽度就ＯＫ
     * Toast.makeText(getApplicationContext(), "向右滑动,滑动距离：" + (e2.getX() -
     * e1.getX()), Toast.LENGTH_LONG).show(); // 要触发什么事件都在这里写就OK //
     * 如果要跳转到另外一个activity // Intent intent=new Intent(RoomUI.this,
     * RoomListUI.class); // intent.putExtra("uid", "1"); //
     * startActivity(intent); ProgramLandUI.this.finish();
     * 
     * }
     * 
     * if ((e2.getX()-e1.getX())<-50) { Toast.makeText(getApplicationContext(),
     * "向左滑动,滑动距离："+(e2.getX()-e1.getX()), Toast.LENGTH_LONG).show(); Intent
     * intent=new Intent(ProgramUI.this, ProgramUI.class); intent.putExtra("ID",
     * roomId); startActivity(intent); } if ((e2.getY()-e1.getY())<-50) {
     * Toast.makeText(getApplicationContext(), "向上滑动",
     * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
     * Toast.makeText(getApplicationContext(), "向下滑动",
     * Toast.LENGTH_LONG).show(); }
     * 
     * return false; }
     * 
     * @Override public void onLongPress(MotionEvent e) { // TODO Auto-generated
     * method stub\
     * 
     * 长按事件 一切长事件按屏幕想要触发的事件都在这里写
     * 
     * // Toast.makeText(getApplicationContext(), //
     * "------------> onLongPress", Toast.LENGTH_LONG).show(); }
     * 
     * @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float
     * distanceX, float distanceY) { // TODO Auto-generated method stub
     * 
     * 这个函数大概是这样，有误差。distanceX 是X轴路径长度，distanceY 是Y轴路径长度（注意：是路径，不是位移）;
     * 
     * // Toast.makeText(getApplicationContext(), "------------> onScroll", //
     * Toast.LENGTH_LONG).show(); return false; }
     * 
     * @Override public void onShowPress(MotionEvent e) { // TODO Auto-generated
     * method stub
     * 
     * 
     * 
     * }
     * 
     * @Override public boolean onSingleTapUp(MotionEvent e) { // TODO
     * Auto-generated method stub return false; }
     * 
     * @Override public boolean onDown(MotionEvent e) { // TODO Auto-generated
     * method stub return false; } }
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.program_land_actionbar, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    ProgramLandUI.this.finish();
	    return true;
	case (R.id.muNextStep):
	    /*Toast.makeText(getApplicationContext(), "下一步菜单项被点击",
		    Toast.LENGTH_LONG).show();*/
	    // 将48个点的状态通过Intent传递到下一个Activity=>ApplyActivity
	    Intent intent = new Intent();
	    intent.setClass(getApplicationContext(), ApplyUI.class);
	    intent.putExtra("roomId", roomId);
	    intent.putExtra("dayMode", day);
	    // 由传递48个点的状态，转为传递模板日期。
	    /*
	    for (int i = 0; i < 48; i++) {
		intent.putExtra("t" + i, time[i]);
	    }
	    */
	    intent.putExtra("ct", ct);
	    intent.putExtra("st", st);
	    // intent.putExtra("mode", mode);
	    startActivity(intent);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

}
