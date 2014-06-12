package com.acs.demo;

import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BoilerUI extends Activity {
    // 声明控件
    private TextView tvTempBoiler = null;
    private TextView tvTempOut = null;
    private TextView tvTempTar = null;
    private TextView tvBoilerDo = null;
    private TextView tvTTC = null;
    private TextView tvError = null;
    // private TextView tvTTT = null;
    // private Button btHS = null;
    // private Button btBS = null;
    // private Button btRS = null;
    // private ToggleButton tbHS = null;
    // private ToggleButton tbBS = null;
    private ImageButton btUp = null;
    private ImageButton btDown = null;
    private ImageView ivHeating = null;
    private ImageView ivFire = null;
    private ImageView ivError = null;
    private ImageView ivLock = null;
    private ImageView ivHS = null;
    // private ImageView ivBS = null;
    private ImageView ivRS = null;
    private ImageView ivCurve = null;

    // 初始化状态
    private String tempNow = null;
    private String tempOut = null;
    private String tempTar = null;
    private int errorStatus = 0;
    // private int boilerDo = 0;
    private int con = 0;
    private int fire = 0;
    private int error = 0;
    private int lock = 0;
    private int warmEnable = 0;
    private String baseURL = "http://192.168.0.97/Android/";
    // private String baseURL = "http://10.0.2.2/Android/";

    // 初始化线程
    private Thread mCreateWork = null;
    // 实时刷新定时器线程
    private Timer timer = null;
    // UI延迟更新计时器
    private Timer UIpauseTimer = null;
    // UI更新允许标识
    public boolean UIrefreshAllow = true;
    // 目标温度设定线程
    private Thread mTempSetWork = null;

    // handler更新UI
    private Handler mHandler = null;
    private Handler mWarmingHandler = null;

    // 从Intent获取到的boilerId或者UID
    private String boilerId = null;
    private String uid = null;
    // 手势监测
    private GestureDetector detector;

    private int tpSet;

    // 加载中对话框
    private AlertDialog ad;
    private AlertDialog adwarming;

    // 通信中标识，防止线程阻塞
    // private boolean running = false;

    // 语言文字
    private String connecting = null;
    private String connectError = null;

    // 用户是否存在温控器设备
    private boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_boiler_temp);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从RoomListUI中的putExtra()方法传递过来的键值对
	// boilerId = bundle.getString("ID");//取出键值对中ID键所对应的值，并赋值给boilerId
	uid = bundle.getString("uid");// 如果通过UID获取，初始化时先将boilerId赋值

	mHandler = new Handler();// 控制器更新UI
	mWarmingHandler = new Handler();

	tvTempBoiler = (TextView) findViewById(R.id.tvTempBoiler);
	tvTempOut = (TextView) findViewById(R.id.tvTempOut);
	tvTempTar = (TextView) findViewById(R.id.tvTempTar);
	tvBoilerDo = (TextView) findViewById(R.id.tvBoilerDo);
	tvTTC = (TextView) findViewById(R.id.tvTTC);
	tvError = (TextView) findViewById(R.id.tvError);
	// tvTTT = (TextView) findViewById(R.id.tvTTT);
	// tbHS = (ToggleButton) findViewById(R.id.tbHS);
	// tbBS = (ToggleButton) findViewById(R.id.tbBS);
	// btHS = (Button) findViewById(R.id.btHS);
	// btBS = (Button) findViewById(R.id.btBS);
	// btRS = (Button) findViewById(R.id.btRS);
	btUp = (ImageButton) findViewById(R.id.btUpBo);
	btDown = (ImageButton) findViewById(R.id.btDownBo);
	ivHeating = (ImageView) findViewById(R.id.ivHeating);
	ivFire = (ImageView) findViewById(R.id.ivFire);
	ivError = (ImageView) findViewById(R.id.ivError);
	ivLock = (ImageView) findViewById(R.id.ivLock);
	ivHS = (ImageView) findViewById(R.id.ivHS);
	// ivBS = (ImageView) findViewById(R.id.ivBS);
	ivRS = (ImageView) findViewById(R.id.ivRS);
	ivCurve = (ImageView) findViewById(R.id.ivCurve);

	// 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(BoilerUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(BoilerUI.this).setView(dialog).show();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(BoilerUI.this).setView(
		dialogWarming).create();

	// 启动初始化线程
	mCreateWork = new Thread(mCreateRunnable);
	mCreateWork.start();

	// ActionBar标题显示
	ActionBar acb = getActionBar();
	acb.setTitle(R.string.boiler);
	acb.setDisplayHomeAsUpEnabled(true);

	// 手势操作
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.boilerLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);

	connecting = this.getString(R.string.connecting);
	connectError = this.getString(R.string.connectError);

	// 供暖开关按钮监听
	// tbHS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // 关机状态下不能开启供暖，有错状态下不能开启供暖
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // 此处有图像更改项，稍后添加~
	// tbHS.setChecked(true);
	// heatingStatus = 1;
	// ivHeating.setImageResource(R.drawable.heating40);
	// }
	// else
	// {
	// tbHS.setChecked(false);
	// heatingStatus = 0;
	// ivHeating.setImageResource(R.drawable.noheating40);
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // 此处为关机状态，错误状况未知
	// // 弹出请先开机Toast
	// Toast.makeText(BoilerUI.this, "请先开机！", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // 此处为开机状态，且有错
	// // 弹出Toast，状态异常，请先复位
	// Toast.makeText(BoilerUI.this, "状态异常，请先复位！",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });

	ivHS.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// if (boilerDo == 1 && error == 0)
		// {
		if (warmEnable == 0) {
		    // 此处有图像更改项，稍后添加~
		    // tbHS.setChecked(true);
		    ivHS.setImageResource(R.drawable.heatingon2);
		    warmEnable = 1;
		    Toast.makeText(BoilerUI.this,
			    R.string.sendHeatingStartSignal, Toast.LENGTH_SHORT)
			    .show();
		    // ivHeating.setImageResource(R.drawable.heating40);
		} else {
		    // tbHS.setChecked(false);
		    ivHS.setImageResource(R.drawable.heatingoff2);
		    warmEnable = 0;
		    Toast.makeText(BoilerUI.this,
			    R.string.sendHeatingStopSignal, Toast.LENGTH_SHORT)
			    .show();
		    // ivHeating.setImageResource(R.drawable.noheating40);
		}
		// 使用初始化线程
		mCreateWork = new Thread(mHSBDRunnable);
		mCreateWork.start();
		/*
		 * } else if (boilerDo == 0) { // 此处为关机状态，错误状况未知 // 弹出请先开机Toast
		 * Toast.makeText(BoilerUI.this, "请先开机！", Toast.LENGTH_SHORT)
		 * .show(); } else if (error != 0) { // 此处为开机状态，且有错 //
		 * 弹出Toast，状态异常，请先复位 Toast.makeText(BoilerUI.this, "状态异常，请先复位！",
		 * Toast.LENGTH_SHORT).show(); }
		 */
		UIPause();
	    }
	});

	ivCurve.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClass(getApplicationContext(), ChooseTempCurveUI.class);
		it.putExtra("ID", boilerId);
		startActivity(it);
	    }
	});

	// tbHS.setOnCheckedChangeListener(new OnCheckedChangeListener()
	// {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView, boolean
	// isChecked)
	// {
	// // TODO Auto-generated method stub
	// // 关机状态下不能开启供暖，有错状态下不能开启供暖
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // 此处有图像更改项，稍后添加~
	// tbHS.setChecked(false);
	// heatingStatus = 1;
	// ivHeating.setImageResource(R.drawable.heating40);
	// }
	// else
	// {
	// tbHS.setChecked(true);
	// heatingStatus = 0;
	// ivHeating.setImageResource(R.drawable.noheating40);
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // 此处为关机状态，错误状况未知
	// // 弹出请先开机Toast
	// Toast.makeText(BoilerUI.this, "请先开机！", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // 此处为开机状态，且有错
	// // 弹出Toast，状态异常，请先复位
	// Toast.makeText(BoilerUI.this, "状态异常，请先复位！",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });

	// 供暖开关按钮监听
	// btHS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // 关机状态下不能开启供暖，有错状态下不能开启供暖
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // 此处有图像更改项，稍后添加~
	// heatingStatus = 1;
	// ivHeating.setImageResource(R.drawable.heating40);
	// }
	// else
	// {
	// heatingStatus = 0;
	// ivHeating.setImageResource(R.drawable.noheating40);
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // 此处为关机状态，错误状况未知
	// // 弹出请先开机Toast
	// Toast.makeText(BoilerUI.this, "请先开机！", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // 此处为开机状态，且有错
	// // 弹出Toast，状态异常，请先复位
	// Toast.makeText(BoilerUI.this, "状态异常，请先复位！",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });

	// tbBS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (boilerDo == 1)
	// {
	// tbBS.setChecked(false);
	// tbHS.setChecked(false);
	// boilerDo = 0;
	// fire = 0;
	// heatingStatus = 0;
	// tvBoilerDo.setText("待机中");
	// // 此处有三个图像变换，稍后再做
	// ivHeating.setImageResource(R.drawable.noheating40);
	// ivFire.setImageResource(R.drawable.nofire40);
	// }
	// else if (boilerDo == 0)
	// {
	// tbBS.setChecked(true);
	// boilerDo = 1;
	// tvBoilerDo.setText("运行中");
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// ivBS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (boilerDo == 1)
	// {
	// //tbBS.setChecked(false);
	// //tbHS.setChecked(false);
	// ivHS.setImageResource(R.drawable.heatingoff2);
	// ivBS.setImageResource(R.drawable.off);
	// boilerDo = 0;
	// fire = 0;
	// heatingStatus = 0;
	// tvBoilerDo.setText("Standby");
	// // 此处有三个图像变换，稍后再做
	// ivHeating.setImageResource(R.drawable.noheating40);
	// ivFire.setImageResource(R.drawable.nofire40);
	// }
	// else if (boilerDo == 0)
	// {
	// //tbBS.setChecked(true);
	// ivBS.setImageResource(R.drawable.on);
	// boilerDo = 1;
	// tvBoilerDo.setText("Running");
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// tbBS.setOnCheckedChangeListener(new OnCheckedChangeListener()
	// {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView, boolean
	// isChecked)
	// {
	// // TODO Auto-generated method stub
	// if (boilerDo == 1)
	// {
	// tbBS.setChecked(true);
	// boilerDo = 0;
	// fire = 0;
	// heatingStatus = 0;
	// tvBoilerDo.setText("待机中");
	// // 此处有三个图像变换，稍后再做
	// }
	// else if (boilerDo == 0)
	// {
	// tbBS.setChecked(false);
	// boilerDo = 1;
	// tvBoilerDo.setText("运行中");
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// 总开关按钮监听
	// btBS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (boilerDo == 1)
	// {
	// boilerDo = 0;
	// fire = 0;
	// heatingStatus = 0;
	// tvBoilerDo.setText("待机中");
	// // 此处有三个图像变换，稍后再做
	// }
	// else if (boilerDo == 0)
	// {
	// boilerDo = 1;
	// tvBoilerDo.setText("运行中");
	// }
	// // 使用初始化线程
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// 复位按钮监听
	// btRS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // 使用初始化线程
	// if (error == 0)
	// Toast.makeText(BoilerUI.this, "当前运行正常", Toast.LENGTH_SHORT)
	// .show();
	// if(error != 0&&lock==0){
	// Toast.makeText(BoilerUI.this, "壁挂炉正在自动修复", Toast.LENGTH_SHORT)
	// .show();
	// }
	// if(lock==1)
	// {
	// //lock = 0;
	// // 此处补一个图像变换，稍后再做~
	// //ivError.setImageResource(R.drawable.nowarming40);
	// //ivLock.setAlpha(0);
	// Toast.makeText(BoilerUI.this, "已发送复位信号，请稍候...", Toast.LENGTH_SHORT)
	// .show();
	// }
	// mCreateWork = new Thread(mRSRunnable);
	// mCreateWork.start();
	// }
	// });

	ivRS.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (error == 0)
		    Toast.makeText(BoilerUI.this, R.string.runningOk,
			    Toast.LENGTH_SHORT).show();
		if (error != 0 && lock == 0) {
		    Toast.makeText(BoilerUI.this, R.string.autoRepair,
			    Toast.LENGTH_SHORT).show();
		}
		if (lock == 1 && error != 0) {
		    // lock = 0;
		    // 此处补一个图像变换，稍后再做~
		    // ivError.setImageResource(R.drawable.nowarming40);
		    // ivLock.setAlpha(0);
		    Toast.makeText(BoilerUI.this, R.string.sendResetSignal,
			    Toast.LENGTH_SHORT).show();
		    mCreateWork = new Thread(mRSRunnable);
		    mCreateWork.start();
		    UIPause();
		}
	    }
	});

	// 升温按钮监听事件
	btUp.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempTar.getText().toString().equals("n/a")) {
		    tpSet = 20;// 默认温度20
		    tvTTC.setText("℃");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempTar.getText().toString());

		// 把当前温度+1，再显示到tvTempSet控件上
		if (tpSet < 80)
		    tpSet++;
		tvTempTar.setText("" + tpSet);// 加上空字符串可以将int类型转换为String类型
		// 温度颜色变换
		if (tpSet >= 60) {
		    tvTempTar.setTextColor(Color.rgb(255, 102, 31));
		} else if (tpSet > 40) {
		    tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		}
		// 开启线程更新温度
		mTempSetWork = new Thread(mTempSetRunnable);
		mTempSetWork.start();
		UIPause();
	    }
	});

	// 降温按钮监听事件
	btDown.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempTar.getText().toString().equals("n/a")) {
		    tpSet = 20;// 默认温度20
		    tvTTC.setText("℃");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempTar.getText().toString());

		// 把当前温度+1，再显示到tvTempSet控件上
		if (tpSet > 5)
		    tpSet--;
		tvTempTar.setText("" + tpSet);// 加上空字符串可以将int类型转换为String类型

		// 温度颜色变换
		if (tpSet <= 40) {
		    tvTempTar.setTextColor(Color.rgb(7, 187, 108));
		} else if (tpSet < 60) {
		    tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		}
		// 开启线程更新温度
		mTempSetWork = new Thread(mTempSetRunnable);
		mTempSetWork.start();
		UIPause();
	    }
	});
    }

    // 初始化Runnable
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    // 首先获取boilerId
	    String urlGetBid = baseURL + "getBoilerDID.php?uid=" + uid;
	    MyHttp myhttpGetBid = new MyHttp();
	    try {
		String retStrGetBid = myhttpGetBid.httpGet(urlGetBid);
		JSONObject jsonObject = new JSONObject(retStrGetBid);
		String re_err = jsonObject.getString("error");
		String bid = jsonObject.getString("bid");
		String existS = jsonObject.getString("engine");
		if (re_err.equals("0")) {
		    if (bid.equals("") || bid.equals("null")) {
			boilerId = "00";// 等于00说明该用户尚未添加锅炉
			Log.v("锅炉", "未添加");
		    } else {
			boilerId = bid;
		    }
		    if (existS.equals("0")) {
			// 该用户不存在温控器设备，锅炉可以调温
			exist = false;
		    } else if (existS.equals("1")) {
			// 该用户存在温控器设备，锅炉不能调温
			exist = true;
		    }
		} else {
		    boilerId = "01";// 等于01说明获取信息出错
		}
		Log.v("所获取锅炉ID", boilerId);
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    }
	    if (boilerId.equals("00")) {
		// 该用户尚未绑定锅炉,跳转至绑定锅炉页面
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), IfAddBoiler.class);
		startActivity(intent);
		BoilerUI.this.finish();
	    } else if (boilerId.equals("01")) {
		// 获取锅炉信息出错，跳转至出错页面
	    } else {
		// 获取用户锅炉信息
		String url = baseURL + "getBoiler.php?deviceId=" + boilerId;
		MyHttp myHttp = new MyHttp();
		try {
		    String retStr = myHttp.httpGet(url);
		    JSONObject jsonObject = new JSONObject(retStr);
		    String re_err = jsonObject.getString("error");
		    tempNow = jsonObject.getString("tempNow");
		    tempOut = jsonObject.getString("tempOut");
		    tempTar = jsonObject.getString("tempTar");
		    String errorSS = jsonObject.getString("eS");
		    // String bDoS = jsonObject.getString("bDo");
		    String bConS = jsonObject.getString("con");
		    String fireS = jsonObject.getString("fire");
		    String errS = jsonObject.getString("err");
		    String lockS = jsonObject.getString("lock");
		    String weS = jsonObject.getString("we");
		    if (errorSS.equals("") || errorSS.equals("null"))
			errorStatus = 0;
		    else
			errorStatus = Integer.parseInt(errorSS);
		    /*
		     * if (bDoS.equals("") || bDoS.equals("null")) boilerDo = 0;
		     * else boilerDo = Integer.parseInt(bDoS);
		     */
		    if (bConS.equals("") || bConS.equals("null"))
			con = 0;
		    else
			con = Integer.parseInt(bConS);
		    if (fireS.equals("") || fireS.equals("null"))
			fire = 0;
		    else
			fire = Integer.parseInt(fireS);
		    if (errS.equals("") || errS.equals("null"))
			error = 0;
		    else
			error = Integer.parseInt(errS);
		    if (lockS.equals("") || lockS.equals("null"))
			lock = 0;
		    else
			lock = Integer.parseInt(lockS);
		    if (weS.equals("") || weS.equals("null"))
			warmEnable = 0;
		    else
			warmEnable = Integer.parseInt(weS);

		    Log.v("初始化状况", re_err);
		    Log.v("当前锅炉温", tempNow);
		    Log.v("室外温度", tempOut);
		    Log.v("目标温度", tempTar);
		    Log.v("供暖状态", weS);
		    // Log.v("开关状态", bDoS);
		    Log.v("连接状态", bConS);
		    Log.v("火焰状态", fireS);
		    Log.v("错误状态1", errorSS);
		    Log.v("错误状态2", errS);
		    Log.v("锁状态", lockS);

		    mHandler.post(mViewRunnable);

		    // 启动实时刷新定时器,定时器应该在初始化成功后开启
		    timer = new Timer();
		    timer.schedule(refreshTask, 5000, 5000);// 程序开启5秒后每隔5秒刷新一次

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
		// 加载中对话框取消
		ad.cancel();
	    }
	}
    };

    // 实时更新TimerTask
    private TimerTask refreshTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "getBoiler.php?deviceId=" + boilerId;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		tempNow = jsonObject.getString("tempNow");
		tempOut = jsonObject.getString("tempOut");
		tempTar = jsonObject.getString("tempTar");
		String errorSS = jsonObject.getString("eS");
		// String bDoS = jsonObject.getString("bDo");
		String bConS = jsonObject.getString("con");
		String fireS = jsonObject.getString("fire");
		String errS = jsonObject.getString("err");
		String lockS = jsonObject.getString("lock");
		String weS = jsonObject.getString("we");
		if (errorSS.equals("") || errorSS.equals("null"))
		    errorStatus = 0;
		else
		    errorStatus = Integer.parseInt(errorSS);
		/*
		 * if (bDoS.equals("") || bDoS.equals("null")) boilerDo = 0;
		 * else boilerDo = Integer.parseInt(bDoS);
		 */
		if (bConS.equals("") || bConS.equals("null"))
		    con = 0;
		else
		    con = Integer.parseInt(bConS);
		if (fireS.equals("") || fireS.equals("null"))
		    fire = 0;
		else
		    fire = Integer.parseInt(fireS);
		if (errS.equals("") || errS.equals("null"))
		    error = 0;
		else
		    error = Integer.parseInt(errS);
		if (lockS.equals("") || lockS.equals("null"))
		    lock = 0;
		else
		    lock = Integer.parseInt(lockS);
		if (weS.equals("") || weS.equals("null"))
		    warmEnable = 0;
		else
		    warmEnable = Integer.parseInt(weS);

		Log.v("刷新状况", re_err);
		Log.v("当前锅炉温", tempNow);
		Log.v("室外温度", tempOut);
		Log.v("目标温度", tempTar);
		Log.v("供暖状态", weS);
		// Log.v("开关状态", bDoS);
		Log.v("连接状态", bConS);
		Log.v("连接状态2", "" + con);
		Log.v("火焰状态", fireS);
		Log.v("错误状态1", errorSS);
		Log.v("错误状态2", errS);
		Log.v("锁状态", lockS);

		mHandler.post(mReViewRunnable);

		if (re_err.equals("1")) {
		    // 送msg到handler，弹出一个toast
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}

    };

    // 供暖开关和开关机按钮的Runnable
    private Runnable mHSBDRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshBoiler.php?deviceId=" + boilerId
		    + "&we=" + warmEnable;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");

		if (re_err.equals("0")) {
		    Log.v("更新锅炉状态", "成功！");
		}

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

    // 复位按钮Runnable
    private Runnable mRSRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "resetBoiler.php?deviceId=" + boilerId;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");

		if (re_err.equals("0")) {
		    Log.v("执行复位操作", "成功！");
		}

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

    // 下面是更新tempSet的线程，此线程由两个Button的onClick事件调用
    private Runnable mTempSetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshBoilerTemp.php?deviceId=" + boilerId
		    + "&tempSet=" + tpSet;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("更新锅炉temp状况", re_err);
		// running = false;
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

    // 下面是UI暂停更新模块
    private void UIPause() {
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

    // 初始化UI的Runnable
    private Runnable mViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (tempNow.equals("") || tempNow.equals("null"))
		tvTempBoiler.setText("n/a");
	    else
		tvTempBoiler.setText(tempNow);
	    if (tempOut.equals("") || tempOut.equals("null"))
		tvTempOut.setText("n/a");
	    else
		tvTempOut.setText(tempOut);
	    if (tempTar.equals("") || tempTar.equals("null")) {
		tvTempTar.setText("n/a");
		tvTTC.setText("");
		// tvTTT.setText("");
	    } else {
		String tt[] = tempTar.split("\\.");
		tvTempTar.setText(tt[0]);
		// tvTempTar.setText(tempTar);
		int tp = Integer.parseInt(tt[0]);
		if (tp >= 60) {
		    tvTempTar.setTextColor(Color.rgb(255, 102, 31));
		} else if (tp <= 40) {
		    tvTempTar.setTextColor(Color.rgb(7, 187, 108));
		} else if (tp > 40 && tp < 60) {
		    tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		}
	    }
	    if (con == 0) {
		// tbBS.setChecked(false);
		// ivBS.setImageResource(R.drawable.off);
		tvBoilerDo.setText(connectError);
	    } else {
		// tbBS.setChecked(true);
		// ivBS.setImageResource(R.drawable.on);
		tvBoilerDo.setText(connecting);
	    }
	    // 控制图片状态，稍后补充~
	    if (warmEnable == 0) {// 显示停止供暖图片
		ivHeating.setImageResource(R.drawable.noheating40);
		// tbHS.setChecked(false);
		ivHS.setImageResource(R.drawable.heatingoff2);
	    } else if (warmEnable == 1) {// 显示正在供暖图片
		ivHeating.setImageResource(R.drawable.heating40);
		// tbHS.setChecked(true);
		ivHS.setImageResource(R.drawable.heatingon2);
	    }
	    if (fire == 0) {// 显示火焰熄灭图片
		ivFire.setImageResource(R.drawable.nofire40);
	    } else if (fire == 1) {// 显示火焰燃烧图片
		ivFire.setImageResource(R.drawable.fire40);
	    }
	    if (errorStatus == 1) {
		tvError.setText("" + error);
		ivError.setImageResource(R.drawable.warningon);
	    } else {
		tvError.setText("");
		ivError.setImageResource(R.drawable.warming);
	    }
	    if (lock == 0) {
		// 不显示锁
		ivLock.setAlpha(0);
	    } else {
		// 显示锁
		ivLock.setAlpha(200);
		// ivLock.setImageResource(R.drawable.lock);
	    }
	    if (exist == true) {
		// 不能调温，调温按钮隐藏
		btUp.setVisibility(View.GONE);
		btDown.setVisibility(View.GONE);
	    } else {
		// 可以调温，调温按钮显示
	    }

	}

    };

    // 刷新UI的Runnable
    private Runnable mReViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (UIrefreshAllow == true) {
		if (tempNow.equals("") || tempNow.equals("null"))
		    tvTempBoiler.setText("n/a");
		else
		    tvTempBoiler.setText(tempNow);
		if (tempOut.equals("") || tempOut.equals("null"))
		    tvTempOut.setText("n/a");
		else
		    tvTempOut.setText(tempOut);
		if (tempTar.equals("") || tempTar.equals("null")) {
		    tvTempTar.setText("n/a");
		    tvTTC.setText("");
		    // tvTTT.setText("");
		} else {
		    String tt[] = tempTar.split("\\.");
		    tvTempTar.setText(tt[0]);
		    // tvTempTar.setText(tempTar);
		    int tp = Integer.parseInt(tt[0]);
		    if (tp >= 60) {
			tvTempTar.setTextColor(Color.rgb(255, 102, 31));
		    } else if (tp <= 40) {
			tvTempTar.setTextColor(Color.rgb(7, 187, 108));
		    } else if (tp > 40 && tp < 60) {
			tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		    }
		}
		if (con == 0) {
		    // tbBS.setChecked(false);
		    // ivBS.setImageResource(R.drawable.off);
		    tvBoilerDo.setText(connectError);
		} else {
		    // tbBS.setChecked(true);
		    // ivBS.setImageResource(R.drawable.on);
		    tvBoilerDo.setText(connecting);
		}
		// 控制图片状态，稍后补充~
		if (warmEnable == 0) {// 显示停止供暖图片
				      // tbHS.setChecked(false);
		    ivHS.setImageResource(R.drawable.heatingoff2);
		    ivHeating.setImageResource(R.drawable.noheating40);
		} else if (warmEnable == 1) {// 显示正在供暖图片
					     // tbHS.setChecked(true);
		    ivHS.setImageResource(R.drawable.heatingon2);
		    ivHeating.setImageResource(R.drawable.heating40);
		}
		if (fire == 0) {// 显示火焰熄灭图片
		    ivFire.setImageResource(R.drawable.nofire40);
		} else if (fire == 1) {// 显示火焰燃烧图片
		    ivFire.setImageResource(R.drawable.fire40);
		}
		if (errorStatus == 1) {
		    tvError.setText("" + error);
		    ivError.setImageResource(R.drawable.warningon);
		} else {
		    tvError.setText("");
		    ivError.setImageResource(R.drawable.warming);
		}
		if (adwarming.isShowing())
		    adwarming.dismiss();

		if (lock == 0) {
		    // 不显示锁
		    ivLock.setAlpha(0);
		} else {
		    // 显示锁
		    ivLock.setAlpha(200);
		    // ivLock.setImageResource(R.drawable.lock);
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
	    } else if (ad.isShowing()) {
		try {
		    ad.dismiss();
		    adwarming.show();
		} catch (Exception e) {
		    e.printStackTrace();
		}
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
	    if (e2.getX() - e1.getX() > 50) {
		// 为什么是50？ 这个根据你的模拟器大小来定，看看模拟器宽度，e2.getX()-e1.getX()<屏幕宽度就ＯＫ
		/*
		 * Toast.makeText(getApplicationContext(), "向右滑动",
		 * Toast.LENGTH_LONG).show();
		 */
		// 要触发什么事件都在这里写就OK
		// 如果要跳转到另外一个activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		BoilerUI.this.finish();
	    }

	    if ((e2.getX() - e1.getX()) < -50) {
		/*
		 * Toast.makeText(getApplicationContext(), "向左滑动",
		 * Toast.LENGTH_LONG).show();
		 */
		Intent it = new Intent();
		it.setClass(BoilerUI.this, ChooseTempCurveUI.class);
		it.putExtra("ID", boilerId);
		startActivity(it);
	    }
	    /*
	     * if (Math.abs(e2.getY()-e1.getY())>50) {
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
	inflater.inflate(R.menu.boiler_actionbar, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    BoilerUI.this.finish();
	    return true;
	    /*
	     * case (R.id.muTempCurve): Toast.makeText(getApplicationContext(),
	     * "温度曲线菜单项被点击", Toast.LENGTH_LONG).show(); Intent it = new
	     * Intent(); it.setClass(getApplicationContext(),
	     * ChooseTempCurveUI.class); it.putExtra("ID", boilerId);
	     * startActivity(it); return true;
	     */
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
