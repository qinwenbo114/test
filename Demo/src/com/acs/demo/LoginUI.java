package com.acs.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginUI extends Activity {
    // 创建网络连接线程对象
    private Thread mNetWork = null;

    // 声明控件对象
    // 用户名编辑框
    private EditText etUN;
    // 密码编辑框
    private EditText etPW;
    // 登录按钮
    private Button btLogIn;
    // 记住我复选框
    private CheckBox cbRemMe;
    // 注册文本域
    private TextView tvSignUp;
    // 忘记密码文本域
    private TextView tvForgot;

    // 数据库助手类
    SQLiteOpenHelper dbhepler = null;
    // 数据库类
    SQLiteDatabase db = null;

    // 用户名和密码
    private String un;
    private String pw;
    // 设备是否已绑定
    private String dev;
    // 记录是否记住用户
    private int rem = 0;
    // handler弹出网络异常
    private Handler mWarmingHandler = null;
    // 加载中对话框
    private AlertDialog ad;
    // 网络连接状况对话框
    private AlertDialog adwarming;
    // 成功登录后反馈的用户id
    private String re_user_id;

    // BaseURL url = new BaseURL();
    // 服务器URL
    private String baseURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// 获取baseURL
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	// 异常对话框弹出控制handler
	mWarmingHandler = new Handler();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(LoginUI.this)
		.setView(dialogWarming).create();

	// 加载SQLLite，查询是否有自动登录信息
	Cursor cursor;
	dbhepler = new DBHelper(LoginUI.this);// 建立一个DBHelper对象dbhelper
	db = dbhepler.getReadableDatabase();// 建立一个SQLiteDatabase对象db，获取一个可以进行读操作数据库对象
	cursor = db.query(
		// 查询数据库中是否有记录用户名和密码的信息
		DBHelper.USER_TABLE_NAME, // 使用query()方法查询数据库，返回一个游标赋值给cursor
		new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD1,
			DBHelper.USER_TABLE_NAME_FIELD2,
			DBHelper.USER_TABLE_NAME_FIELD3,
			DBHelper.USER_TABLE_NAME_FIELD4 }, "rem='1'", null,
		null, null, "_id asc");// 参数1：查询的表名；参数2：返回哪些列；参数3：限定条件；参数7：排序依据

	// 如果数据库中有该用户的自动登录信息，则提取出用户名和密码
	if (cursor.moveToFirst() == true) {
	    un = cursor.getString(cursor
		    .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD2));
	    pw = cursor.getString(cursor
		    .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD3));

	    // 获取URL，此为测试阶段为方便转移服务器地址所设
	    //
	    // 正式发布时此处以下内容要删除*******************************
	    //
	    /*
	    Cursor cursor2;
	    cursor2 = db.query(
		    DBHelper.USER_TABLE_NAME, // 使用query()方法查询数据库，返回一个游标赋值给cursor
		    new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD5 },
		    null, null, null, null, "_id asc");

	    if (cursor2.moveToFirst() == false) {
		// baseURL = cursor2.getString(cursor2
		// .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		// 弹出新URL对话框
		LayoutInflater factory2 = LayoutInflater.from(LoginUI.this);
		final View editURL = factory2.inflate(R.layout.dialog_edit_url,
			null);
		new AlertDialog.Builder(LoginUI.this)
			.setView(editURL)
			.setTitle("NEW URL")
			.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog,
					    int which) {
					// TODO Auto-generated method stub

				    }
				})
			.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog,
					    int which) {
					// TODO Auto-generated method stub
					EditText etIP = (EditText) editURL
						.findViewById(R.id.etIP);
					String urlNew = "http://"
						+ etIP.getText().toString()
						+ "/Android/";
					upURL(urlNew);
				    }
				}).show();
	    } else {
		// url存入BaseURL
		baseURL = cursor2.getString(cursor2
			.getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		((BaseURL) getApplication()).setBaseURL(baseURL);
	    }
	    */
	    //
	    // 正式发布时此处以上内容要删除********************************
	    //

	    // 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	    LayoutInflater factory = LayoutInflater.from(LoginUI.this);
	    final View dialog = factory.inflate(R.layout.dialog_logining, null);
	    ad = new AlertDialog.Builder(LoginUI.this).setView(dialog).show();

	    // 执行网络通信线程
	    mNetWork = new Thread(mNetRunnable);
	    mNetWork.start();
	    cursor.close();

	    //
	    // 正式发布时此处以下内容要删除********************************
	    //
	    /*
	    cursor2.close();
	    */
	    //
	    // 正式发布时此处以上内容要删除********************************
	    //
	} else {
	    // 获取URL
	    //
	    // 正式发布时此处以下内容要删除********************************
	    //
	    /*
	    Cursor cursor2;
	    cursor2 = db.query(
		    DBHelper.USER_TABLE_NAME, // 使用query()方法查询数据库，返回一个游标赋值给cursor
		    new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD5 },
		    null, null, null, null, "_id asc");

	    if (cursor2.moveToFirst() == false) {
		// baseURL = cursor2.getString(cursor2
		// .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		// 弹出新URL对话框
		LayoutInflater factory2 = LayoutInflater.from(LoginUI.this);
		final View editURL = factory2.inflate(R.layout.dialog_edit_url,
			null);
		new AlertDialog.Builder(LoginUI.this)
			.setView(editURL)
			.setTitle("NEW URL")
			.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog,
					    int which) {
					// TODO Auto-generated method stub

				    }
				})
			.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog,
					    int which) {
					// TODO Auto-generated method stub
					EditText etIP = (EditText) editURL
						.findViewById(R.id.etIP);
					String urlNew = "http://"
						+ etIP.getText().toString()
						+ "/Android/";
					upURL(urlNew);
				    }
				}).show();
	    } else {
		// url存入BaseURL
		baseURL = cursor2.getString(cursor2
			.getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		((BaseURL) getApplication()).setBaseURL(baseURL);
	    }
	    */
	    //
	    // 正式发布时此处以上内容要删除********************************
	    //
	    Toast.makeText(LoginUI.this, "SQLlite中无记录", Toast.LENGTH_SHORT)
		    .show();
	    cursor.close();
	    //
	    // 正式发布时此处以下内容要删除********************************
	    //
	    /*
	    cursor2.close();
	    */
	    //
	    // 正式发布时此处以上内容要删除********************************
	    //
	}

	setContentView(R.layout.activity_login_ui);

	etUN = (EditText) findViewById(R.id.uN);
	etPW = (EditText) findViewById(R.id.uP);
	btLogIn = (Button) findViewById(R.id.buttonLogIn);
	cbRemMe = (CheckBox) findViewById(R.id.checkBoxRemMe);
	tvSignUp = (TextView) findViewById(R.id.textViewSignUp);
	tvForgot = (TextView) findViewById(R.id.textViewForget);
	// cbRemMe = (CheckBox) findViewById(R.id.checkBoxRemMe);

	btLogIn.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		un = etUN.getText().toString();
		pw = etPW.getText().toString();
		if (un.length() == 0)
		    Log.w("un is null", "true");
		if (un.length() != 0 && pw.length() != 0) {
		    // 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
		    LayoutInflater factory = LayoutInflater.from(LoginUI.this);
		    final View dialog = factory.inflate(
			    R.layout.dialog_logining, null);
		    ad = new AlertDialog.Builder(LoginUI.this).setView(dialog)
			    .show();

		    // 执行网络通信线程
		    mNetWork = new Thread(mNetRunnable);
		    mNetWork.start();

		} else if (un.length() != 0) {
		    // 密码不能为空
		    Toast.makeText(LoginUI.this, R.string.passwordCannotBeNull,
			    Toast.LENGTH_SHORT).show();
		} else if (pw.length() != 0) {
		    // 用户名不能为空
		    Toast.makeText(LoginUI.this, R.string.usernameCannotBeNull,
			    Toast.LENGTH_SHORT).show();
		} else {
		    // 请输入用户名和密码
		    Toast.makeText(LoginUI.this, R.string.pleaseInputUNPW,
			    Toast.LENGTH_SHORT).show();
		}
	    }
	});

	tvSignUp.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), RegisterUI.class);
		startActivity(intent);
	    }
	});

	tvForgot.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 打开浏览器
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(baseURL + "jump2forgot.php");
		intent.setData(content_url);
		startActivity(intent);
	    }
	});
    }

    private Runnable mNetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    HttpClient client = new DefaultHttpClient();
	    StringBuilder builder = new StringBuilder();
	    // un = etUN.getText().toString();
	    // pw = etPW.getText().toString();
	    Log.w("input username", un);
	    Log.w("input password", pw);
	    HttpGet myget = new HttpGet(baseURL + "login.php?un=" + un + "&pw="
		    + pw);
	    try {
		HttpResponse response = client.execute(myget);
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		for (String s = reader.readLine(); s != null; s = reader
			.readLine()) {
		    builder.append(s);
		}
		JSONObject jsonObject = new JSONObject(builder.toString());
		String re_err = jsonObject.getString("error");
		// String re_password = jsonObject.getString("user_id");
		re_user_id = jsonObject.getString("user_id");
		dev = jsonObject.getString("dev");
		Log.w("url response", "re_error=" + re_err);
		Log.w("url response", "re_uid=" + re_user_id);
		Log.w("device", dev);
		if (re_err.equals("0")) {
		    Message msg = new Message();
		    msg.what = 0;
		    mHandler.sendMessage(msg);
		} else if (re_err.equals("1")) {
		    Message msg = new Message();
		    msg.what = 1;
		    mHandler.sendMessage(msg);
		} else if (re_err.equals("2")) {
		    Message msg = new Message();
		    msg.what = 2;
		    mHandler.sendMessage(msg);
		}

	    } catch (Exception e) {
		Log.v("url response", "false");
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    }
	    // mNetWork.stop();
	    // 取消对话框
	    ad.cancel();
	}

    };

    private Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);
	    if (msg.what == 0) {
		// 登录成功
		// Toast.makeText(LoginUI.this,
		// "登录成功！",Toast.LENGTH_SHORT).show();

		if (cbRemMe.isChecked()) {
		    rem = 1;
		    Log.d("登录记录", "记住");
		    Cursor cursor;
		    SQLiteOpenHelper dbhepler = new DBHelper(LoginUI.this);// 建立一个DBHelper对象dbhelper
		    SQLiteDatabase db = dbhepler.getReadableDatabase();// 建立一个SQLiteDatabase对象db，获取一个可以进行读操作数据库对象

		    // 删除所有rem=1的记录
		    String sqlDelete = "delete from "
			    + DBHelper.USER_TABLE_NAME + " where rem='1'";
		    db.execSQL(sqlDelete);
		    Log.d("删除所有rem=1的记录", "执行成功");

		    cursor = db.query(
			    DBHelper.USER_TABLE_NAME, // 使用query()方法查询数据库，返回一个游标赋值给cursor
			    new String[] { "_id",
				    DBHelper.USER_TABLE_NAME_FIELD1,
				    DBHelper.USER_TABLE_NAME_FIELD2,
				    DBHelper.USER_TABLE_NAME_FIELD3,
				    DBHelper.USER_TABLE_NAME_FIELD4 },
			    "user_id='" + re_user_id + "'", null, null, null,
			    "_id asc");// 参数1：查询的表名；参数2：返回哪些列；参数7：排序依据

		    if (cursor.moveToFirst() == false) {
			String sql = "insert into " + DBHelper.USER_TABLE_NAME
				+ " values ( null,'" + re_user_id + "','" + un
				+ "','" + pw + "','" + rem + "','" + baseURL
				+ "')";
			db.execSQL(sql);// 执行SQL语句
		    } else {
			String sql = "update " + DBHelper.USER_TABLE_NAME
				+ " set rem='" + rem + "',user_name='" + un
				+ "',password='" + pw + "' where user_id='"
				+ re_user_id + "'";
			db.execSQL(sql);// 执行SQL语句
		    }
		    cursor.close();

		}

		if (dev.equals("1")) {
		    // 跳转到ChooseLogoUI
		    Intent it = new Intent(LoginUI.this, ChooseLogoUI.class);
		    it.putExtra("uid", re_user_id);
		    it.putExtra("Logout", "false");
		    startActivity(it);
		} else if (dev.equals("0")) {
		    // 跳转到绑定设备UI
		    Intent it = new Intent(LoginUI.this, AddDeviceUI.class);
		    it.putExtra("uid", re_user_id);
		    startActivity(it);
		}
		LoginUI.this.finish();
	    } else if (msg.what == 1) {
		// 用户名或密码错误
		Toast.makeText(LoginUI.this, R.string.UNorPWError,
			Toast.LENGTH_SHORT).show();

	    } else if (msg.what == 2) {
		// 获取用户信息失败
		Toast.makeText(LoginUI.this, R.string.getUserInfoError,
			Toast.LENGTH_SHORT).show();
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
		try {
		    adwarming.show();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    // adwarming.show();
	}

    };

    private void upURL(String newURL) {
	Cursor cursor;
	SQLiteOpenHelper dbhepler = new DBHelper(LoginUI.this);// 建立一个DBHelper对象dbhelper
	SQLiteDatabase db = dbhepler.getReadableDatabase();// 建立一个SQLiteDatabase对象db，获取一个可以进行读操作数据库对象
	cursor = db.query(DBHelper.USER_TABLE_NAME, // 使用query()方法查询数据库，返回一个游标赋值给cursor
		new String[] { "_id" }, null, null, null, null, "_id asc");// 参数1：查询的表名；参数2：返回哪些列；参数7：排序依据
	if (cursor.moveToFirst() == false) {
	    String sql = "insert into " + DBHelper.USER_TABLE_NAME
		    + " values ( null,null,null,null,null,'" + newURL + "')";
	    db.execSQL(sql);// 执行SQL语句
	} else {
	    String sql = "update " + DBHelper.USER_TABLE_NAME + " set url='"
		    + newURL + "'";
	    db.execSQL(sql);// 执行SQL语句
	}
	cursor.close();
	((BaseURL) getApplication()).setBaseURL(newURL);
	baseURL = newURL;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	//getMenuInflater().inflate(R.menu.login_ui, menu);
	getMenuInflater().inflate(R.menu.login_ui_without_url, menu);
	// 正式发布时，此处要引用不带修改URL功能的menu文件*************************
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	    //
	    // 正式发布时此处以下内容要删除********************************
	    //
	/*
	case (R.id.action_settings):
	    LayoutInflater factory = LayoutInflater.from(LoginUI.this);
	    final View editURL = factory
		    .inflate(R.layout.dialog_edit_url, null);
	    new AlertDialog.Builder(LoginUI.this)
		    .setView(editURL)
		    .setTitle("NEW URL")
		    .setNegativeButton(R.string.cancel,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub

				}
			    })
		    .setPositiveButton(R.string.save,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub
				    EditText etIP = (EditText) editURL
					    .findViewById(R.id.etIP);
				    String urlNew = "http://"
					    + etIP.getText().toString()
					    + "/Android/";
				    upURL(urlNew);
				}
			    }).show();
	    return true;
	    */
	    //
	    // 正式发布时此处以上内容要删除********************************
	    //
	case (R.id.muWifiSet):
	    Intent intent3 = new Intent();
	    intent3.setClass(getApplicationContext(), ConfigWifiUI.class);
	    intent3.putExtra("login", false);
	    startActivity(intent3);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}

    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	if (dbhepler != null) {
	    dbhepler.close();
	}
    }

}
