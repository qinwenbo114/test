package com.acs.demo;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class RoomUI extends Activity {
    // 绑定一下控件
    private TextView tvTempNow = null;
    private TextView tvTempSet = null;
    private TextView tvMode = null;
    private ImageButton btUp = null;
    // private Button btUp = null;
    private ImageButton btDown = null;
    private TextView tvHeatingStatus = null;
    private TextView tvTTC = null;
    // private TextView tvTTT = null;
    private ImageView ivSchedule = null;
    private ImageView ivParty = null;
    private ImageView ivOut = null;
    private ImageView ivSwitch = null;
    private LinearLayout llModel0 = null;
    private LinearLayout llModel1 = null;
    private ImageView ivAuto = null;
    private ImageView ivSave = null;
    private ImageView ivSwitch2 = null;
    private TextView tvComfTemp = null;
    private TextView tvSaveTemp = null;
    private SeekBar sbComfTemp = null;
    private SeekBar sbSaveTemp = null;
    // private Button btHeatingS = null;
    // private ToggleButton tbHeatingS = null;
    // private ImageView ivHeatingS = null;
    // 编辑对话框控件
    private EditText etRoomName = null;
    private TextView tvEngineMark = null;
    private TextView tvConnectErr = null;

    // 初始化和温度更新线程
    private Thread mNetWork = null;
    // heatingStatus更新线程，建立多个线程是因为考虑到用户的多点触控= =！
    private Thread mHeatingWork = null;
    // heatingMode更新线程
    private Thread mModeWork = null;
    // seekBar温度更新线程
    private Thread mSeekBarWork = null;
    // 实时更新线程用定时器实现
    private Timer timer = null;
    // UI延迟更新计时器
    private Timer UIpauseTimer = null;
    // UI更新允许标识
    public boolean UIrefreshAllow = true;

    // handler更新UI
    private Handler mHandler = null;
    // handler弹出网络异常
    private Handler mWarmingHandler = null;

    // 当前温度
    private String tempNow;
    private String tempSet;
    // 节能舒适温度
    private String ct;
    private String st;
    int tpSet;
    // 供暖模式
    private String mode;
    // 供暖状态
    int heatingStatus;
    // 温控器类型
    int thModel;
    // 连接状态
    int con;
    // URL
    // BaseURL url = new BaseURL();
    private String baseURL = null;
    // 从Intent获取到的RoomID
    private String roomId = null;
    // 手势监测
    private GestureDetector detector;
    // 从Intent获取的roomName
    private String roomName = null;
    private String roomMark = null;
    // 加载中对话框
    private AlertDialog ad;
    // 网络连接状况对话框
    private AlertDialog adwarming;

    private ActionBar acb = null;

    // 语言文字
    private String stopped = null;
    private String running = null;
    private String scheduleModel = null;
    private String partyModel = null;
    private String outModel = null;
    private String switchModel = null;
    private String autoModel = null;
    private String saveModel = null;
    private String roomInfo = null;
    private String cancel = null;
    private String save = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_room_temp_2);

	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从RoomListUI中的putExtra()方法传递过来的键值对
	roomId = bundle.getString("ID");// 取出键值对中ID键所对应的值，并赋值给roomId
	roomName = bundle.getString("NAME");

	mHandler = new Handler();
	mWarmingHandler = new Handler();

	tvTempNow = (TextView) findViewById(R.id.tvTempNow);
	tvTempSet = (TextView) findViewById(R.id.tvTempSet);
	tvTTC = (TextView) findViewById(R.id.tvTTC);
	// tvTTT = (TextView) findViewById(R.id.tvTTT);
	tvMode = (TextView) findViewById(R.id.tvMode);
	btUp = (ImageButton) findViewById(R.id.btUp);
	btDown = (ImageButton) findViewById(R.id.btDown);
	tvHeatingStatus = (TextView) findViewById(R.id.tvHeatingStatus);
	// btHeatingS = (Button) findViewById(R.id.btHeatingS);
	// tbHeatingS = (ToggleButton) findViewById(R.id.tbHeatingS);
	// ivHeatingS = (ImageView) findViewById(R.id.ivHeatingS);
	ivSchedule = (ImageView) findViewById(R.id.ivSchedule);
	ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
	ivParty = (ImageView) findViewById(R.id.ivParty);
	ivOut = (ImageView) findViewById(R.id.ivOut);
	llModel0 = (LinearLayout) findViewById(R.id.llModel0);
	llModel1 = (LinearLayout) findViewById(R.id.llModel1);
	ivAuto = (ImageView) findViewById(R.id.ivAuto);
	ivSave = (ImageView) findViewById(R.id.ivSave);
	ivSwitch2 = (ImageView) findViewById(R.id.ivSwitch2);
	tvComfTemp = (TextView) findViewById(R.id.tvComfTemp);
	tvSaveTemp = (TextView) findViewById(R.id.tvSaveTemp);
	sbComfTemp = (SeekBar) findViewById(R.id.sbComfTemp);
	sbSaveTemp = (SeekBar) findViewById(R.id.sbSaveTemp);
	tvConnectErr = (TextView) findViewById(R.id.tvConnectErr);

	// 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(RoomUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(RoomUI.this).setView(dialog).show();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RoomUI.this).setView(dialogWarming)
		.create();

	// 初始化线程
	mNetWork = new Thread(mCreateRunnable);
	mNetWork.start();

	// ActionBar标题显示
	try {
	    acb = getActionBar();
	    acb.setTitle(roomName);
	    acb.setDisplayHomeAsUpEnabled(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	stopped = this.getString(R.string.stopped);
	running = this.getString(R.string.running);
	scheduleModel = this.getString(R.string.scheduleModel);
	partyModel = this.getString(R.string.partyModel);
	outModel = this.getString(R.string.outModel);
	switchModel = this.getString(R.string.switchModel);
	autoModel = this.getString(R.string.autoModel);
	saveModel = this.getString(R.string.saveModel);
	roomInfo = this.getString(R.string.roomInfo);
	cancel = this.getString(R.string.cancel);
	save = this.getString(R.string.save);

	// 实时更新线程
	timer = new Timer();
	timer.schedule(refreshTask, 5000, 5000);// 程序开启5秒后每隔5秒刷新一次

	// 两个Button的onclick事件，更新tvTempSet的数据，并开启通信线程，将该数据存入数据库
	btUp.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempSet.getText().toString().equals("n/a")) {
		    tpSet = 20;// 默认温度20
		    tvTTC.setText("℃");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempSet.getText().toString());

		// 把当前温度+1，再显示到tvTempSet控件上
		if (tpSet < 45)
		    tpSet++;
		tvTempSet.setText("" + tpSet);// 加上空字符串可以将int类型转换为String类型
		// 温度颜色变换
		if (tpSet >= 30) {
		    tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		} else if (tpSet > 20) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
		// 开启线程 更新温度到数据库
		mHeatingWork = new Thread(mTempSetRunnable);
		mHeatingWork.start();
		UIPause();
		/*
		UIrefreshAllow = false;
		if (UIpauseTimer != null) {
		    UIpauseTimer.cancel();
		    UIpauseTimer = null;
		    UIpauseTimer = new Timer();
		    if (UIallowTask != null)
			UIallowTask.cancel();
		    UIallowTask = new TimerTask() {

			@Override
			public void run() {
			    // TODO Auto-generated method stub
			    UIrefreshAllow = true;
			}
		    };
		    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
		} else {
		    UIpauseTimer = new Timer();
		    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
		}
		*/
	    }
	});

	btDown.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempSet.getText().toString().equals("n/a")) {
		    tpSet = 20;// 默认温度20
		    tvTTC.setText("℃");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempSet.getText().toString());

		// 把当前温度+1，再显示到tvTempSet控件上
		if (tpSet > 5)
		    tpSet--;
		// 温度颜色变换
		tvTempSet.setText("" + tpSet);// 加上空字符串可以将int类型转换为String类型
		if (tpSet <= 20) {
		    tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		} else if (tpSet < 30) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
		// 开启线程 更新温度到数据库
		mNetWork = new Thread(mTempSetRunnable);
		mNetWork.start();
		UIPause();
		/*
		UIrefreshAllow = false;
		if (UIpauseTimer != null) {
		    UIpauseTimer.cancel();
		    UIpauseTimer = null;
		    UIpauseTimer = new Timer();
		    if (UIallowTask != null)
			UIallowTask.cancel();
		    UIallowTask = new TimerTask() {

			@Override
			public void run() {
			    // TODO Auto-generated method stub
			    UIrefreshAllow = true;
			}
		    };
		    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
		} else {
		    UIpauseTimer = new Timer();
		    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
		}
		*/
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

	// heating状态开关按钮监
	/*
	 * ivHeatingS.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub if (heatingStatus == 0) { tvHeatingStatus.setText("Running");
	 * heatingStatus = 1; ivHeatingS.setImageResource(R.drawable.on); //
	 * tbHeatingS.setChecked(true); // 开启网络线程更改数据库 mNetWork = new
	 * Thread(mHeatingRunnable); mNetWork.start(); } else {
	 * tvHeatingStatus.setText("Stoped"); heatingStatus = 0;
	 * ivHeatingS.setImageResource(R.drawable.off); //
	 * tbHeatingS.setChecked(false); // 开启网络线程更改数据库 mNetWork = new
	 * Thread(mHeatingRunnable); mNetWork.start(); } } });
	 */

	// heating状态开关按钮监
	// tbHeatingS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (heatingStatus == 0)
	// {
	// tvHeatingStatus.setText("Heating");
	// heatingStatus = 1;
	// //tbHeatingS.setChecked(true);
	// // 开启网络线程更改数据库
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// else
	// {
	// tvHeatingStatus.setText("Heating has been stoped");
	// heatingStatus = 0;
	// //tbHeatingS.setChecked(false);
	// // 开启网络线程更改数据库
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// }
	// });

	// btHeatingS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (heatingStatus == 0)
	// {
	// tvHeatingStatus.setText("供暖中");
	// heatingStatus = 1;
	// // 开启网络线程更改数据库
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// else
	// {
	// tvHeatingStatus.setText("供暖已停止");
	// heatingStatus = 0;
	// // 开启网络线程更改数据库
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// }
	// });

	// 供暖状态切换(定时模式)
	ivSchedule.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("0")) {

		} else {
		    mode = "0";
		    tvMode.setText(scheduleModel);
		    ivSchedule.setImageResource(R.drawable.sc2);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// 供暖状态切换(酒会模式)
	ivParty.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("1")) {

		} else {
		    mode = "1";
		    tvMode.setText(partyModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh2);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}

	    }
	});

	// 供暖状态切换(外出模式)
	ivOut.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("2")) {

		} else {
		    mode = "2";
		    tvMode.setText(outModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out2);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}

	    }
	});

	// 供暖状态切换(关机模式)
	ivSwitch.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("3")) {

		} else {
		    mode = "3";
		    tvMode.setText(switchModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw2);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// 供暖状态切换（自动模式）
	ivAuto.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("0")) {

		} else {
		    mode = "0";
		    tvMode.setText(autoModel);
		    ivAuto.setImageResource(R.drawable.zd2);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// 供暖模式切换（节能模式）
	ivSave.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("1")) {

		} else {
		    mode = "1";
		    tvMode.setText(saveModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn2);
		    ivSwitch2.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// 供暖模式切换（关机模式）
	ivSwitch2.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("2")) {

		} else {
		    mode = "2";
		    tvMode.setText(switchModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw2);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// 手势操作
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.roomLinearLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);

    }

    // 至少需要四个线程通信Runnable，一个用来刷新tempNow，一个用来初始化，一个用来更新tempSet，一个用来更新供暖开关

    // 下面是初始化线程
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "getRoomTemp.php?roomId=" + roomId;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		tempNow = jsonObject.getString("tempNow");
		tempSet = jsonObject.getString("tempSet");
		String heating = jsonObject.getString("heating");
		mode = jsonObject.getString("mode");
		roomMark = jsonObject.getString("roomId");
		String thModelS = jsonObject.getString("thModel");
		String longCt = jsonObject.getString("comfT");
		String ct2[] = longCt.split("\\.");
		ct = ct2[0];
		String longSt = jsonObject.getString("saveT");
		String st2[] = longSt.split("\\.");
		st = st2[0];
		String conS = jsonObject.getString("con");

		Log.v("初始化总状况", re_err);
		Log.v("当前温度", tempNow);
		Log.v("设定温度", tempSet);
		Log.v("供暖状态", heating);
		Log.v("供暖模式", mode);
		Log.v("mark", roomMark);
		Log.v("thModel", thModelS);

		// 把获取到的温度显示在控件上,并将tempSet赋值给tpSet,heating赋值给heatingStatus
		if (tempSet.equals("") || tempSet.equals("null"))
		    tpSet = 20;
		else {
		    String ts[] = tempSet.split("\\."); // 注意！以“.”为分隔符时需要转义
		    Log.v("tp前", ts[0]);
		    tpSet = Integer.parseInt(ts[0]);
		}
		if (heating.equals("") || heating.equals("null"))
		    heatingStatus = 0;
		else
		    heatingStatus = Integer.parseInt(heating);
		Log.v("供暖状态初始化", "" + heatingStatus);
		if (thModelS.equals("") || thModelS.equals("null")) {
		    thModel = 0;
		} else {
		    thModel = Integer.parseInt(thModelS);
		}
		if (conS.equals("1")) {
		    con = 1;
		} else {
		    con = 0;
		}

		mHandler.post(mViewRunnable);

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
	    // 取消加载中对话框
	    ad.cancel();
	}

    };

    // 下面是更新tempSet的线程，此线程由两个Button的onClick事件调用
    private Runnable mTempSetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomTemp.php?roomId=" + roomId
		    + "&tempSet=" + tpSet;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("更新temp状况", re_err);
		if (re_err.equals("1")) {
		    // 送msg到handler，弹出一个toast
		}
		if (adwarming.isShowing())
		    adwarming.dismiss();
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
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

    // 下面是更改HeatingStatus的线程runnable
    /*
     * private Runnable mHeatingRunnable = new Runnable() {
     * 
     * @Override public void run() { // TODO Auto-generated method stub String
     * url = baseURL + "refreshRoomHeating.php?roomId=" + roomId + "&heating=" +
     * heatingStatus; MyHttp myHttp = new MyHttp(); String retStr =
     * myHttp.httpGet(url); try { JSONObject jsonObject = new
     * JSONObject(retStr); String re_err = jsonObject.getString("error");
     * Log.v("更新heating状况", re_err); if (re_err.equals("1")) { //
     * 送msg到handler，弹出一个toast } if (adwarming.isShowing()) adwarming.dismiss();
     * } catch (JSONException e) { // TODO Auto-generated catch block //
     * e.printStackTrace(); mWarmingHandler.post(mWarmingRunnable); } catch
     * (Exception e) { mWarmingHandler.post(mWarmingRunnable); } }
     * 
     * };
     */

    // 下面是更改Mode线程
    private Runnable mModeRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomMode.php?roomId=" + roomId
		    + "&mode=" + mode;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("更新mode状况", re_err);
		if (re_err.equals("1")) {
		    // 送msg到handler，弹出一个toast
		}
		if (adwarming.isShowing())
		    adwarming.dismiss();
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}

    };

    // 下面是数据实时刷新线程
    private TimerTask refreshTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomUI.php?roomId=" + roomId;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		tempNow = jsonObject.getString("tempNow");
		tempSet = jsonObject.getString("tempSet");
		String heating = jsonObject.getString("heating");
		mode = jsonObject.getString("mode");
		String conS = jsonObject.getString("con");

		Log.v("实时刷新状况", re_err);
		Log.v("实时刷新温度", tempNow);
		Log.v("实时刷新设定温度", tempSet);
		Log.v("实时刷新供暖状态", heating);
		Log.v("实时刷新供暖模式", mode);

		if (heating.equals("") || heating.equals("null"))
		    heatingStatus = 0;
		else
		    heatingStatus = Integer.parseInt(heating);
		if (conS.equals("1")) {
		    con = 1;
		} else {
		    con = 0;
		}

		// 将获取到的状态更新到UI
		mHandler.post(mReViewRunnable);

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
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}
    };
    
    // 下面是UI暂停更新模块
    private void UIPause(){
	UIrefreshAllow = false;
	if (UIpauseTimer != null) {
	    UIpauseTimer.cancel();
	    UIpauseTimer = null;
	    UIpauseTimer = new Timer();
	    if (UIallowTask != null)
		UIallowTask.cancel();
	    UIallowTask = new TimerTask() {

		@Override
		public void run() {
		    // TODO Auto-generated method stub
		    UIrefreshAllow = true;
		}
	    };
	    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
	} else {
	    UIpauseTimer = new Timer();
	    UIpauseTimer.schedule(UIallowTask, 10000);// 10秒内UI不允许更新
	}
    }

    // 下面是UI更新允许倒计时Task
    private TimerTask UIallowTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    UIrefreshAllow = true;
	}
    };

    // 编辑房间信息
    private Runnable mEditRoomRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "editRoom.php?roomId=" + roomId
		    + "&roomName=" + roomName;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		if (re_err.equals("0")) {
		    // 更新成功，刷新房间名
		    acb = null;
		    acb = getActionBar();
		    acb.setTitle(roomName);
		    acb.setDisplayHomeAsUpEnabled(true);
		    acb.show();
		} else {
		    // 更新失败
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    };

    // handler用来初始化UI数据
    private Runnable mViewRunnable = new Runnable() {
	@Override
	public void run() {
	    if (thModel == 0) {
		llModel0.setVisibility(View.VISIBLE);
		llModel1.setVisibility(View.GONE);
	    } else if (thModel == 1) {
		llModel0.setVisibility(View.GONE);
		llModel1.setVisibility(View.VISIBLE);
	    }
	    if (tempNow.equals("null") || tempNow.equals(""))
		tvTempNow.setText("n/a");
	    else
		tvTempNow.setText(tempNow);
	    if (tempSet.equals("null") || tempSet.equals("")) {
		tvTempSet.setText("n/a");
		tvTTC.setText("");
		// tvTTT.setText("");
	    } else {
		String ts[] = tempSet.split("\\.");
		tvTempSet.setText(ts[0]);
		int tp = Integer.parseInt(ts[0]);
		if (tp >= 30) {
		    tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		} else if (tp <= 20) {
		    tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		} else if (tp > 20 && tp < 30) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
	    }
	    if (thModel == 0) {
		if (mode.equals("0")) {
		    tvMode.setText(scheduleModel);
		    ivSchedule.setImageResource(R.drawable.sc2);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("1")) {
		    tvMode.setText(partyModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh2);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("2")) {
		    tvMode.setText(outModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out2);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("3")) {
		    tvMode.setText(switchModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw2);
		}
	    } else if (thModel == 1) {
		if (mode.equals("0")) {
		    tvMode.setText(autoModel);
		    ivAuto.setImageResource(R.drawable.zd2);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw);
		}
		if (mode.equals("1")) {
		    tvMode.setText(saveModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn2);
		    ivSwitch2.setImageResource(R.drawable.sw);
		}
		if (mode.equals("2")) {
		    tvMode.setText(switchModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw2);
		}
	    }
	    if (heatingStatus == 0) {
		// ivHeatingS.setImageResource(R.drawable.off);
		// tbHeatingS.setChecked(false)

		tvHeatingStatus.setText(stopped);
	    } else {
		// ivHeatingS.setImageResource(R.drawable.on);
		// tbHeatingS.setChecked(true);
		tvHeatingStatus.setText(running);
	    }
	    if (ct.equals("") || ct.equals("null"))
		ct = "n/a";
	    if (st.equals("") || st.equals("null"))
		st = "n/a";
	    Log.v("初始化ComfT", ct);
	    Log.v("初始化SaveT", st);
	    // tvComfTemp.setText(ct);
	    // tvSaveTemp.setText(st);
	    if (ct.equals("n/a")) {
	    } else {
		sbComfTemp.setProgress(Integer.parseInt(ct) - 5);
		if ((Integer.parseInt(ct) - 5) == 0)
		    tvComfTemp.setText("5");
	    }
	    if (st.equals("n/a")) {
	    } else {
		sbSaveTemp.setProgress(Integer.parseInt(st) - 5);
		if ((Integer.parseInt(st) - 5) == 0)
		    tvSaveTemp.setText("5");
	    }
	    if (con == 1) {
		tvConnectErr.setVisibility(View.INVISIBLE);
	    } else {
		tvConnectErr.setVisibility(View.VISIBLE);
	    }
	}
    };

    // 实时刷新UI数据
    private Runnable mReViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (UIrefreshAllow == true) {
		if (tempNow.equals("null") || tempNow.equals(""))
		    tvTempNow.setText("n/a");
		else
		    tvTempNow.setText(tempNow);
		if (tempSet.equals("null") || tempSet.equals("")) {
		    tvTempSet.setText("n/a");
		    tvTTC.setText("");
		    // tvTTT.setText("");
		} else {
		    String ts[] = tempSet.split("\\.");
		    tvTempSet.setText(ts[0]);
		    int tp = Integer.parseInt(ts[0]);
		    if (tp >= 30) {
			tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		    } else if (tp <= 20) {
			tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		    } else if (tp > 20 && tp < 30) {
			tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		    }
		}
		if (heatingStatus == 0) {
		    // ivHeatingS.setImageResource(R.drawable.off);
		    // tbHeatingS.setChecked(false);
		    tvHeatingStatus.setText(stopped);
		} else {
		    // ivHeatingS.setImageResource(R.drawable.on);
		    // tbHeatingS.setChecked(true);
		    tvHeatingStatus.setText(running);
		}
		if (thModel == 0) {
		    if (mode.equals("0")) {
			tvMode.setText(scheduleModel);
			ivSchedule.setImageResource(R.drawable.sc2);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("1")) {
			tvMode.setText(partyModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh2);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("2")) {
			tvMode.setText(outModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out2);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("3")) {
			tvMode.setText(switchModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw2);
		    }
		} else if (thModel == 1) {
		    if (mode.equals("0")) {
			tvMode.setText(autoModel);
			ivAuto.setImageResource(R.drawable.zd2);
			ivSave.setImageResource(R.drawable.jn);
			ivSwitch2.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("1")) {
			tvMode.setText(saveModel);
			ivAuto.setImageResource(R.drawable.zd);
			ivSave.setImageResource(R.drawable.jn2);
			ivSwitch2.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("2")) {
			tvMode.setText(switchModel);
			ivAuto.setImageResource(R.drawable.zd);
			ivSave.setImageResource(R.drawable.jn);
			ivSwitch2.setImageResource(R.drawable.sw2);
		    }
		}
		if (con == 1) {
		    tvConnectErr.setVisibility(View.INVISIBLE);
		} else {
		    tvConnectErr.setVisibility(View.VISIBLE);
		}
	    }
	}

    };

    // 网络异常对话框弹出Runnable
    private Runnable mWarmingRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub

	    // 加载对话框，用于网路线程异常时弹出

	    if (adwarming.isShowing()) {
	    } else {
		try {
		    adwarming.show();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    // adwarming.show();
	}

    };

    // 下面写一个向右滑动返回上层Activity的手势操作
    // 触摸屏幕监听
    class TouhListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    // Toast.makeText(getApplicationContext(), "----?",
	    // event.getAction()).show();
	    return detector.onTouchEvent(event);
	}

    }

    // 手势滑动监听
    class GestureListener implements OnGestureListener {

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	    // e1 触摸的起始位置，e2 触摸的结束位置，velocityX X轴每一秒移动的像素速度（大概这个意思） velocityY
	    // 就是Ｙ咯
	    // 手势左,上为正 ——，右，下为负正
	    if ((e2.getX() - e1.getX()) > 50) {
		// 为什么是50？ 这个根据你的模拟器大小来定，看看模拟器宽度，e2.getX()-e1.getX()<屏幕宽度就ＯＫ
		/*
		 * Toast.makeText(getApplicationContext(), "向右滑动,滑动距离：" +
		 * (e2.getX() - e1.getX()), Toast.LENGTH_LONG).show();
		 */
		// 要触发什么事件都在这里写就OK
		// 如果要跳转到另外一个activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		RoomUI.this.finish();

	    }
	    if ((e2.getX() - e1.getX()) < -50) {
		/*
		 * Toast.makeText(getApplicationContext(), "向左滑动,滑动距离：" +
		 * (e2.getX() - e1.getX()), Toast.LENGTH_LONG).show();
		 */
		if (thModel == 0) {
		    Intent intent = new Intent(RoomUI.this, ProgramLandUI.class);
		    intent.putExtra("ID", roomId);
		    intent.putExtra("NAME", roomName);
		    startActivity(intent);
		}
	    }
	    /*
	     * if ((e2.getY()-e1.getY())<-50) {
	     * Toast.makeText(getApplicationContext(), "向上滑动",
	     * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
	     * Toast.makeText(getApplicationContext(), "向下滑动",
	     * Toast.LENGTH_LONG).show(); }
	     */
	    return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	    // TODO Auto-generated method stub\
	    /*
	     * 长按事件 一切长事件按屏幕想要触发的事件都在这里写
	     */
	    // Toast.makeText(getApplicationContext(),
	    // "------------> onLongPress", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
		float distanceX, float distanceY) {
	    // TODO Auto-generated method stub
	    /*
	     * 这个函数大概是这样，有误差。distanceX 是X轴路径长度，distanceY 是Y轴路径长度（注意：是路径，不是位移）;
	     */
	    // Toast.makeText(getApplicationContext(), "------------> onScroll",
	    // Toast.LENGTH_LONG).show();
	    return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	    // TODO Auto-generated method stub
	    /*  
             *   
             */
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    // TODO Auto-generated method stub
	    return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
	    // TODO Auto-generated method stub
	    return false;
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	if (thModel == 0) {
	    inflater.inflate(R.menu.room_actionbar, menu);
	} else if (thModel == 1) {
	    inflater.inflate(R.menu.room_without_program_actionbar, menu);
	}
	Log.d("TH_Model", thModel + "");
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    RoomUI.this.finish();
	    return true;
	case (R.id.muSetting):
	    LayoutInflater factory = LayoutInflater.from(RoomUI.this);
	    final View editRoom = factory.inflate(R.layout.dialog_edit_room,
		    null);
	    new AlertDialog.Builder(RoomUI.this)
		    .setView(editRoom)
		    .setTitle(roomInfo)
		    .setNegativeButton(cancel,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub

				}
			    })
		    .setPositiveButton(save,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub
				    roomName = etRoomName.getText().toString();
				    // 开启网络通信，保存房间信息
				    mNetWork = new Thread(mEditRoomRunnable);
				    mNetWork.start();
				}
			    }).show();

	    etRoomName = (EditText) editRoom.findViewById(R.id.etRoomName);
	    tvEngineMark = (TextView) editRoom.findViewById(R.id.tvEngineMark);
	    etRoomName.setText(roomName);
	    tvEngineMark.setText(roomMark);
	    return true;
	case (R.id.muSchedule):
	    if (thModel == 0) {
		Intent intent2 = new Intent(RoomUI.this, ProgramLandUI.class);
		intent2.putExtra("ID", roomId);
		intent2.putExtra("NAME", roomName);
		startActivity(intent2);
	    } else {
	    }
	    return true;
	case (R.id.muLogOut):
	    /*
	     * Toast.makeText(getApplicationContext(), "注销菜单项被点击",
	     * Toast.LENGTH_LONG).show();
	     */
	    // 需要将数据库里的记录用户字段置为0，并Intent到登录页面，而后消除所有在其之下的Activity。
	    Logout lgo = new Logout();
	    if (lgo.Lgot(getApplicationContext()) == true)
		Toast.makeText(getApplicationContext(), "成功注销！",
			Toast.LENGTH_LONG).show();
	    Intent intent = new Intent();
	    intent.setClass(getApplicationContext(), ChooseLogoUI.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("Logout", "true");
	    startActivity(intent);
	    return true;
	case (R.id.muWifiSet):
	    Intent intent3 = new Intent();
	    intent3.setClass(getApplicationContext(), ConfigWifiUI.class);
	    intent3.putExtra("login", true);
	    startActivity(intent3);
	    return true;
	case (R.id.muMoreSet):
	    /*
	     * Toast.makeText(getApplicationContext(), "更多菜单项被点击",
	     * Toast.LENGTH_LONG).show();
	     */
	    return true;
	case (R.id.muHelp):
	    Intent intent4 = new Intent();
	    intent4.setClass(getApplicationContext(), HelpWebUI.class);
	    startActivity(intent4);
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    // Activity关闭时，执行以下操作以释放资源
    public void onDestroy() {
	super.onDestroy();
	if (timer != null) {
	    timer.cancel();
	    timer = null;
	}
    }

}
