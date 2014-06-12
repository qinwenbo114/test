package com.acs.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddDeviceUI extends Activity {

    // 声明控件
    // 设备号编辑框
    private EditText etDevId = null;
    // 注册状态显示文本域
    private TextView regDevStatus = null;
    // 注册按钮
    private Button btRegDev = null;

    // 设备ID
    private String devId = null;
    // 用户ID
    private String uId = null;
    // 反馈信息
    private String re_err = null;
    // 网络线程
    private Thread mNetWork = null;
    // UI更新Handler
    private Handler mHandler = null;
    // handler弹出网络异常
    private Handler mWarmingHandler = null;
    // 网络连接状况对话框
    private AlertDialog adwarming;
    // 服务器URL
    private String baseURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_add_device);
	// 读取baseURL
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	// 获取上个页面传递的uid
	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从RoomListUI中的putExtra()方法传递过来的键值对
	uId = bundle.getString("uid");

	// UI更新Handler
	mHandler = new Handler();
	// 异常信息对话框弹出Handler
	mWarmingHandler = new Handler();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(AddDeviceUI.this).setView(
		dialogWarming).create();

	// 绑定控件
	etDevId = (EditText) findViewById(R.id.etDevId);
	regDevStatus = (TextView) findViewById(R.id.regDevStatus);
	btRegDev = (Button) findViewById(R.id.btRegDev);

	// 注册按钮单击事件
	btRegDev.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		devId = etDevId.getText().toString();
		if (devId.equals("")) {
		    regDevStatus.setText(R.string.deviceNumCannotBeNull);
		} else {
		    mNetWork = new Thread(mNetRunnable);
		    mNetWork.start();
		}
	    }
	});
    }

    // 注册网络操作Runnable
    Runnable mNetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "addDevice.php?uId=" + uId + "&devId="
		    + devId;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		re_err = jsonObject.getString("error");
		Log.v("绑定设备状况", re_err);
		mHandler.post(mRunnable);
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

    // 控制UI Runnable
    private Runnable mRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (re_err.equals("0")) {// 此设备可以绑定
		Toast.makeText(AddDeviceUI.this, R.string.addDeviceSuccessed,
			Toast.LENGTH_SHORT).show();
		// 跳转到ChooseLogo页面
		Intent it = new Intent(AddDeviceUI.this, ChooseLogoUI.class);
		it.putExtra("uid", uId);
		it.putExtra("Logout", "towifi");
		startActivity(it);
		// 跳转到配置wifi模块页面
		// Intent it = new Intent(AddDeviceUI.this, ConfigWifiUI.class);
		// it.putExtra("uid", uId);
		// startActivity(it);
		AddDeviceUI.this.finish();
	    } else if (re_err.equals("1")) {// 此设备已被绑定
		regDevStatus.setText(R.string.deviceHasBeenBound);
	    } else if (re_err.equals("2")) {// 返回无此设备
		regDevStatus.setText(R.string.dontHaveThisDevice);
	    } else if (re_err.equals("3")) {// 返回用户ID设备号不能为空
		regDevStatus.setText(R.string.deviceNumCannotBeNull);
	    }
	}
    };

}
