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
    // �������������̶߳���
    private Thread mNetWork = null;

    // �����ؼ�����
    // �û����༭��
    private EditText etUN;
    // ����༭��
    private EditText etPW;
    // ��¼��ť
    private Button btLogIn;
    // ��ס�Ҹ�ѡ��
    private CheckBox cbRemMe;
    // ע���ı���
    private TextView tvSignUp;
    // ���������ı���
    private TextView tvForgot;

    // ���ݿ�������
    SQLiteOpenHelper dbhepler = null;
    // ���ݿ���
    SQLiteDatabase db = null;

    // �û���������
    private String un;
    private String pw;
    // �豸�Ƿ��Ѱ�
    private String dev;
    // ��¼�Ƿ��ס�û�
    private int rem = 0;
    // handler���������쳣
    private Handler mWarmingHandler = null;
    // �����жԻ���
    private AlertDialog ad;
    // ��������״���Ի���
    private AlertDialog adwarming;
    // �ɹ���¼�������û�id
    private String re_user_id;

    // BaseURL url = new BaseURL();
    // ������URL
    private String baseURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// ��ȡbaseURL
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	// �쳣�Ի��򵯳�����handler
	mWarmingHandler = new Handler();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(LoginUI.this)
		.setView(dialogWarming).create();

	// ����SQLLite����ѯ�Ƿ����Զ���¼��Ϣ
	Cursor cursor;
	dbhepler = new DBHelper(LoginUI.this);// ����һ��DBHelper����dbhelper
	db = dbhepler.getReadableDatabase();// ����һ��SQLiteDatabase����db����ȡһ�����Խ��ж��������ݿ����
	cursor = db.query(
		// ��ѯ���ݿ����Ƿ��м�¼�û������������Ϣ
		DBHelper.USER_TABLE_NAME, // ʹ��query()������ѯ���ݿ⣬����һ���α긳ֵ��cursor
		new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD1,
			DBHelper.USER_TABLE_NAME_FIELD2,
			DBHelper.USER_TABLE_NAME_FIELD3,
			DBHelper.USER_TABLE_NAME_FIELD4 }, "rem='1'", null,
		null, null, "_id asc");// ����1����ѯ�ı���������2��������Щ�У�����3���޶�����������7����������

	// ������ݿ����и��û����Զ���¼��Ϣ������ȡ���û���������
	if (cursor.moveToFirst() == true) {
	    un = cursor.getString(cursor
		    .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD2));
	    pw = cursor.getString(cursor
		    .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD3));

	    // ��ȡURL����Ϊ���Խ׶�Ϊ����ת�Ʒ�������ַ����
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��*******************************
	    //
	    /*
	    Cursor cursor2;
	    cursor2 = db.query(
		    DBHelper.USER_TABLE_NAME, // ʹ��query()������ѯ���ݿ⣬����һ���α긳ֵ��cursor
		    new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD5 },
		    null, null, null, null, "_id asc");

	    if (cursor2.moveToFirst() == false) {
		// baseURL = cursor2.getString(cursor2
		// .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		// ������URL�Ի���
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
		// url����BaseURL
		baseURL = cursor2.getString(cursor2
			.getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		((BaseURL) getApplication()).setBaseURL(baseURL);
	    }
	    */
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //

	    // ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	    LayoutInflater factory = LayoutInflater.from(LoginUI.this);
	    final View dialog = factory.inflate(R.layout.dialog_logining, null);
	    ad = new AlertDialog.Builder(LoginUI.this).setView(dialog).show();

	    // ִ������ͨ���߳�
	    mNetWork = new Thread(mNetRunnable);
	    mNetWork.start();
	    cursor.close();

	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //
	    /*
	    cursor2.close();
	    */
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //
	} else {
	    // ��ȡURL
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //
	    /*
	    Cursor cursor2;
	    cursor2 = db.query(
		    DBHelper.USER_TABLE_NAME, // ʹ��query()������ѯ���ݿ⣬����һ���α긳ֵ��cursor
		    new String[] { "_id", DBHelper.USER_TABLE_NAME_FIELD5 },
		    null, null, null, null, "_id asc");

	    if (cursor2.moveToFirst() == false) {
		// baseURL = cursor2.getString(cursor2
		// .getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		// ������URL�Ի���
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
		// url����BaseURL
		baseURL = cursor2.getString(cursor2
			.getColumnIndex(DBHelper.USER_TABLE_NAME_FIELD5));
		((BaseURL) getApplication()).setBaseURL(baseURL);
	    }
	    */
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //
	    Toast.makeText(LoginUI.this, "SQLlite���޼�¼", Toast.LENGTH_SHORT)
		    .show();
	    cursor.close();
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
	    //
	    /*
	    cursor2.close();
	    */
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
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
		    // ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
		    LayoutInflater factory = LayoutInflater.from(LoginUI.this);
		    final View dialog = factory.inflate(
			    R.layout.dialog_logining, null);
		    ad = new AlertDialog.Builder(LoginUI.this).setView(dialog)
			    .show();

		    // ִ������ͨ���߳�
		    mNetWork = new Thread(mNetRunnable);
		    mNetWork.start();

		} else if (un.length() != 0) {
		    // ���벻��Ϊ��
		    Toast.makeText(LoginUI.this, R.string.passwordCannotBeNull,
			    Toast.LENGTH_SHORT).show();
		} else if (pw.length() != 0) {
		    // �û�������Ϊ��
		    Toast.makeText(LoginUI.this, R.string.usernameCannotBeNull,
			    Toast.LENGTH_SHORT).show();
		} else {
		    // �������û���������
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
		// �������
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
	    // ȡ���Ի���
	    ad.cancel();
	}

    };

    private Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);
	    if (msg.what == 0) {
		// ��¼�ɹ�
		// Toast.makeText(LoginUI.this,
		// "��¼�ɹ���",Toast.LENGTH_SHORT).show();

		if (cbRemMe.isChecked()) {
		    rem = 1;
		    Log.d("��¼��¼", "��ס");
		    Cursor cursor;
		    SQLiteOpenHelper dbhepler = new DBHelper(LoginUI.this);// ����һ��DBHelper����dbhelper
		    SQLiteDatabase db = dbhepler.getReadableDatabase();// ����һ��SQLiteDatabase����db����ȡһ�����Խ��ж��������ݿ����

		    // ɾ������rem=1�ļ�¼
		    String sqlDelete = "delete from "
			    + DBHelper.USER_TABLE_NAME + " where rem='1'";
		    db.execSQL(sqlDelete);
		    Log.d("ɾ������rem=1�ļ�¼", "ִ�гɹ�");

		    cursor = db.query(
			    DBHelper.USER_TABLE_NAME, // ʹ��query()������ѯ���ݿ⣬����һ���α긳ֵ��cursor
			    new String[] { "_id",
				    DBHelper.USER_TABLE_NAME_FIELD1,
				    DBHelper.USER_TABLE_NAME_FIELD2,
				    DBHelper.USER_TABLE_NAME_FIELD3,
				    DBHelper.USER_TABLE_NAME_FIELD4 },
			    "user_id='" + re_user_id + "'", null, null, null,
			    "_id asc");// ����1����ѯ�ı���������2��������Щ�У�����7����������

		    if (cursor.moveToFirst() == false) {
			String sql = "insert into " + DBHelper.USER_TABLE_NAME
				+ " values ( null,'" + re_user_id + "','" + un
				+ "','" + pw + "','" + rem + "','" + baseURL
				+ "')";
			db.execSQL(sql);// ִ��SQL���
		    } else {
			String sql = "update " + DBHelper.USER_TABLE_NAME
				+ " set rem='" + rem + "',user_name='" + un
				+ "',password='" + pw + "' where user_id='"
				+ re_user_id + "'";
			db.execSQL(sql);// ִ��SQL���
		    }
		    cursor.close();

		}

		if (dev.equals("1")) {
		    // ��ת��ChooseLogoUI
		    Intent it = new Intent(LoginUI.this, ChooseLogoUI.class);
		    it.putExtra("uid", re_user_id);
		    it.putExtra("Logout", "false");
		    startActivity(it);
		} else if (dev.equals("0")) {
		    // ��ת�����豸UI
		    Intent it = new Intent(LoginUI.this, AddDeviceUI.class);
		    it.putExtra("uid", re_user_id);
		    startActivity(it);
		}
		LoginUI.this.finish();
	    } else if (msg.what == 1) {
		// �û������������
		Toast.makeText(LoginUI.this, R.string.UNorPWError,
			Toast.LENGTH_SHORT).show();

	    } else if (msg.what == 2) {
		// ��ȡ�û���Ϣʧ��
		Toast.makeText(LoginUI.this, R.string.getUserInfoError,
			Toast.LENGTH_SHORT).show();
	    }
	}
    };

    // �����쳣�Ի��򵯳�Runnable
    private Runnable mWarmingRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub

	    // ���ضԻ���������·�߳��쳣ʱ����

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
	SQLiteOpenHelper dbhepler = new DBHelper(LoginUI.this);// ����һ��DBHelper����dbhelper
	SQLiteDatabase db = dbhepler.getReadableDatabase();// ����һ��SQLiteDatabase����db����ȡһ�����Խ��ж��������ݿ����
	cursor = db.query(DBHelper.USER_TABLE_NAME, // ʹ��query()������ѯ���ݿ⣬����һ���α긳ֵ��cursor
		new String[] { "_id" }, null, null, null, null, "_id asc");// ����1����ѯ�ı���������2��������Щ�У�����7����������
	if (cursor.moveToFirst() == false) {
	    String sql = "insert into " + DBHelper.USER_TABLE_NAME
		    + " values ( null,null,null,null,null,'" + newURL + "')";
	    db.execSQL(sql);// ִ��SQL���
	} else {
	    String sql = "update " + DBHelper.USER_TABLE_NAME + " set url='"
		    + newURL + "'";
	    db.execSQL(sql);// ִ��SQL���
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
	// ��ʽ����ʱ���˴�Ҫ���ò����޸�URL���ܵ�menu�ļ�*************************
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	    //
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
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
	    // ��ʽ����ʱ�˴���������Ҫɾ��********************************
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
