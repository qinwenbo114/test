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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseLogoUI extends Activity {
    // 初始化线程
    private Thread mCreateWork = null;
    // handler更新UI
    private Handler mHandler = null;
    // handler弹出网络异常
    private Handler mWarmingHandler = null;
    // 加载中对话框
    private AlertDialog ad;
    // 网络连接状况对话框
    private AlertDialog adwarming;

    // 声明控件
    // 选择房间图片
    private ImageView ivRoomChoice;
    // 选择锅炉图片
    private ImageView ivBoilerChoice;
    // 房间图片下的文字
    private TextView tvRoomChoice;
    // 锅炉图片下的文字
    private TextView tvBoilerChoice;
    // 服务器URL
    private String baseURL = "http://192.168.0.97/Android/";
    // 反馈情况
    private String num = null;
    ActionBar acb;

    // 存储Intent传来的UID
    private String uid;

    // 点击允许（为true时图标可以点击，否则不能）
    private boolean clickRoom = true;
    private boolean clickBoiler = true;

    // 语言文字
    private String enterRoom = null;
    private String controBoiler = null;
    private String upLoading = null;
    private String noConnect = null;
    private String noMainController = null;

    @Override
    protected void onNewIntent(Intent intent) {
	// TODO Auto-generated method stub
	super.onNewIntent(intent);
	setIntent(intent);
	// try{
	// Toast.makeText(getApplicationContext(), "注销跳转！",
	// Toast.LENGTH_LONG).show();
	// Log.v("Logout", bundle2.getBoolean("Logout"));
	String logout = getIntent().getStringExtra("Logout");
	// uid = getIntent().getStringExtra("uid");
	Log.v("Logout", logout);
	if (logout.equals("true")) {
	    // 此Activity设置为SingleTask，所以在跳回到此Activity时不会新建Activity，
	    // 而是跳回到栈底的Activity,并清空此Activity之上的Activity
	    // 之后执行跳转到新的Activity（LoginUI），再finish掉此Activity即可实现注销
	    Intent newIntent = new Intent();
	    newIntent.setClass(getApplicationContext(), LoginUI.class);
	    startActivity(newIntent);
	    ChooseLogoUI.this.finish();
	}
	/*
	 * else if (logout.equals("towifi")) { Intent it = new Intent();
	 * it.setClass(getApplicationContext(), ConfigWifiUI.class);
	 * it.putExtra("login", true); startActivity(it); }
	 */
	// }catch(Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_choose_logo);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从CRMActivity中的putExtra()方法传递过来的键值对
	uid = bundle.getString("uid");// 取出键值对中uid键所对应的值，并赋值给uid

	try {
	    acb = getActionBar();
	    acb.setDisplayShowTitleEnabled(false);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// 适配语言文字
	enterRoom = this.getString(R.string.enterRoom);
	controBoiler = this.getString(R.string.controlBoiler);
	upLoading = this.getString(R.string.uploading);
	noConnect = this.getString(R.string.noconnect);
	noMainController = this.getString(R.string.noMainController);

	// UI更新handler
	mHandler = new Handler();
	mWarmingHandler = new Handler();

	// 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(ChooseLogoUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(ChooseLogoUI.this).setView(dialog).show();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(ChooseLogoUI.this).setView(
		dialogWarming).create();

	// 启动初始化线程
	mCreateWork = new Thread(mCreateRunnable);
	mCreateWork.start();

	try {
	    String logout = bundle.getString("Logout");
	    if (logout.equals("towifi")) { // 跳转到ConfigWifiUI 
		Intent it = new	Intent();
		it.setClass(getApplicationContext(), ConfigWifiUI.class);
		it.putExtra("login", true);
		startActivity(it);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	ivRoomChoice = (ImageView) findViewById(R.id.ivRoomChoice);
	ivBoilerChoice = (ImageView) findViewById(R.id.ivBoilerChoice);
	tvRoomChoice = (TextView) findViewById(R.id.tvRoomChoice);
	tvBoilerChoice = (TextView) findViewById(R.id.tvBoilerChoice);

	ivRoomChoice.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (clickRoom == true) {
		    Intent intent = new Intent();
		    intent.setClass(ChooseLogoUI.this, RoomListUI.class);
		    intent.putExtra("uid", uid);
		    startActivity(intent);
		}
	    }
	});

	ivBoilerChoice.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (clickBoiler == true) {
		    Intent intent = new Intent();
		    intent.setClass(ChooseLogoUI.this, BoilerUI.class);
		    intent.putExtra("uid", uid);
		    startActivity(intent);
		}
	    }
	});

    }

    // 下面是初始化线程
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "checkRoomBoiler.php?uid=" + uid;
	    Log.v("http请求", url);
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		num = jsonObject.getString("num");
		Log.v("初始化总状况", re_err);
		Log.v("房间与锅炉反馈情况", num);

		// 反馈情况有五种，0：有房间有锅炉，1：有房间无锅炉，2：无房间有锅炉，3：无房间无锅炉，4：主控制器未连接

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

    // UI Runnable
    private Runnable mViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (num.equals("0")) {
		// 都可以点击，正常显示
		clickRoom = true;
		clickBoiler = true;
		tvRoomChoice.setText(enterRoom);
		tvBoilerChoice.setText(controBoiler);
	    } else if (num.equals("1")) {
		// 有房间无锅炉
		clickRoom = true;
		clickBoiler = false;
		tvRoomChoice.setText(enterRoom);
		tvBoilerChoice.setText(noConnect);
	    } else if (num.equals("2")) {
		// 无房间有锅炉
		clickRoom = false;
		clickBoiler = true;
		tvRoomChoice.setText(upLoading);
		tvBoilerChoice.setText(controBoiler);
	    } else if (num.equals("3")) {
		// 无房间无锅炉
		clickRoom = false;
		clickBoiler = false;
		tvRoomChoice.setText(upLoading);
		tvBoilerChoice.setText(noConnect);
	    } else if (num.equals("4")) {
		// 主控制器未连接
		clickRoom = false;
		clickBoiler = false;
		tvRoomChoice.setText(noMainController);
		tvBoilerChoice.setText(noMainController);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_actionbar, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (R.id.muLogOut):
	    // Toast.makeText(getApplicationContext(), "注销菜单项被点击",
	    // Toast.LENGTH_LONG).show();
	    // 需要将数据库里的记录用户字段置为0，并Intent到登录页面，而后消除所有在其之下的Activity。
	    Logout lgo = new Logout();
	    if (lgo.Lgot(getApplicationContext()) == true)
		Toast.makeText(getApplicationContext(), "成功注销！",
			Toast.LENGTH_LONG).show();
	    Intent intent = new Intent();
	    intent.setClass(getApplicationContext(), LoginUI.class);
	    startActivity(intent);
	    ChooseLogoUI.this.finish();
	    return true;
	case (R.id.muWifiSet):
	    Intent intent3 = new Intent();
	    intent3.setClass(getApplicationContext(), ConfigWifiUI.class);
	    intent3.putExtra("login", true);
	    startActivity(intent3);
	    return true;
	case (R.id.muMoreSet):
	    // Toast.makeText(getApplicationContext(), "更多菜单项被点击",
	    // Toast.LENGTH_LONG).show();
	    return true;
	case (R.id.muRefresh):
	    mCreateWork = new Thread(mCreateRunnable);
	    mCreateWork.start();
	    return true;
	case (R.id.muHelp):
	    Intent intent4 = new Intent();
	    intent4.setClass(getApplicationContext(), HelpWebUI.class);
	    startActivity(intent4);
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

}
