package com.acs.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUI extends Activity {

    // 用户名编辑框
    private EditText etRegUN = null;
    // 密码编辑框
    private EditText etRegUP = null;
    // 确认密码编辑框
    private EditText etRegReUP = null;
    // 手机号编辑框
    private EditText etRegPhone = null;
    // Email编辑框
    private EditText etRegEmail = null;
    // 地址编辑框
    private EditText etRegAdd = null;
    // 注册按钮
    private Button btReg = null;
    // 反馈信息显示文本域
    private TextView tvRegInfo = null;

    // 用户名
    private String RegUN = null;
    // 密码
    private String RegUP = null;
    // 手机号
    private String RegPhone = null;
    // Email
    private String RegEmail = null;
    // 地址
    private String RegAdd = null;

    // 网络通信线程
    private Thread mNetWork = null;

    // URL
    private String baseURL = "http://192.168.0.97/Android/";

    // 控制弹出异常状态的Handler
    private Handler mWarmingHandler = null;
    // 控制UI更新的Handler
    private Handler mHandler = null;

    // 网络连接状况对话框
    private AlertDialog adwarming;
    private AlertDialog regInfo;

    // 注册反馈信息
    private String re_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_register);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	
	// Handler要在onCreate方法里创建对象
	mWarmingHandler = new Handler();
	mHandler = new Handler();

	// 创建对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RegisterUI.this).setView(
		dialogWarming).create();

	// 创建对话框，用于显示注册状况
	final View dialogInfo = getLayoutInflater().inflate(
		R.layout.dialog_reg_info, null);
	regInfo = new AlertDialog.Builder(RegisterUI.this)
		.setView(dialogInfo)
		.setNeutralButton(R.string.ok,
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				// TODO Auto-generated method stub

			    }
			}).create();

	// 绑定控件
	etRegUN = (EditText) findViewById(R.id.etRegUN);
	etRegUP = (EditText) findViewById(R.id.etRegUP);
	etRegReUP = (EditText) findViewById(R.id.etRegReUP);
	etRegPhone = (EditText) findViewById(R.id.etRegPhone);
	etRegEmail = (EditText) findViewById(R.id.etRegEmail);
	etRegAdd = (EditText) findViewById(R.id.etRegAdd);
	btReg = (Button) findViewById(R.id.btReg);
	tvRegInfo = (TextView) dialogInfo.findViewById(R.id.tvRegInfo);

	// 注册按钮单击事件
	btReg.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (etRegUP.getText().toString()
			.equals(etRegReUP.getText().toString())) {
		    // 如果两次密码输入一致，获取各输入框内容，准备网络通信
		    RegUN = etRegUN.getText().toString();
		    RegUP = etRegUP.getText().toString();
		    RegEmail = etRegEmail.getText().toString();
		    RegPhone = etRegPhone.getText().toString();
		    RegAdd = etRegAdd.getText().toString();
		    if (RegUN.equals("") || RegUP.equals("")) {
			// 如果用户名或密码框为空，弹出用户名或密码不能为空对话框
			regInfo.show();
			tvRegInfo.setText(R.string.UNorPWCannotBeNull);

		    } else if (RegEmail.equals("")) {
			// 如果邮箱编辑框为空，弹出邮箱不能为空
			regInfo.show();
			tvRegInfo.setText(R.string.emailCannotBeNull);
		    } else if (!is_email(RegEmail)) {
			// 如果邮箱格式验证失败，弹出邮箱验证失败
			regInfo.show();
			tvRegInfo.setText(R.string.emailError);
		    } else if (!is_username(RegUN)) {
			// 如果用户名格式验证失败，弹出用户名格式错误
			regInfo.show();
			tvRegInfo.setText(R.string.UNError);
		    } else if (!is_password(RegUP)) {
			// 如果密码格式验证失败，弹出密码格式错误
			regInfo.show();
			tvRegInfo.setText(R.string.PWError);
		    } else {
			// 如果全部正确，开启注册线程，将用户信息存入数据库
			mNetWork = new Thread(mRegRunnable);
			mNetWork.start();
		    }
		} else {
		    // 如果两次密码输入不一致，弹出密码输入不一致对话框
		    regInfo.show();
		    tvRegInfo.setText(R.string.RePwError);
		}
	    }
	});
    }

    private boolean is_email(String email) {
	Log.v("邮箱格式", email);
	String regEx = "[a-zA-Z0-9]+[a-zA-Z0-9._-]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
	boolean result = Pattern.matches(regEx, email);
	return result;
    }

    private boolean is_username(String un) {
	String regEx = "^[a-zA-Z0-9_]{2,30}$";
	Pattern pat = Pattern.compile(regEx);
	final Matcher matcher = pat.matcher(un);
	if (!matcher.find()) {
	    return false;
	} else {
	    return true;
	}
    }

    private boolean is_password(String pw) {
	String regEx = "^[a-zA-Z0-9]{6,40}$";
	Pattern pat = Pattern.compile(regEx);
	final Matcher matcher = pat.matcher(pw);
	if (!matcher.find()) {
	    return false;
	} else {
	    return true;
	}
    }

    Runnable mRegRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "register.php?un=" + RegUN + "&up=" + RegUP
		    + "&email=" + RegEmail + "&phone=" + RegPhone + "&add="
		    + RegAdd;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		re_err = jsonObject.getString("error");
		Log.v("注册状况", re_err);
		mHandler.post(mReRegRunnable);

	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }

	}
    };

    // 注册反馈控制Runnable
    private Runnable mReRegRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (re_err.equals("0")) {
		// 注册成功，跳转到登录页面，finish此页面
		Toast.makeText(getApplicationContext(),
			R.string.registSuccessed, Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setClass(RegisterUI.this, LoginUI.class);
		startActivity(intent);
	    } else if (re_err.equals("1")) {
		// 注册失败，此用户名已被占用
		Toast.makeText(getApplicationContext(), R.string.registFailed1,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed1);
	    } else if (re_err.equals("2")) {
		// 注册失败，此邮箱已被注册
		Toast.makeText(getApplicationContext(), R.string.registFailed2,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed2);
	    } else if (re_err.equals("3")) {
		// 注册失败，其他原因
		Toast.makeText(getApplicationContext(), R.string.registFailed,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed);
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
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    RegisterUI.this.finish();
	    return true;
	case (R.id.muLogOut):
	    Toast.makeText(getApplicationContext(), "注销菜单项被点击",
		    Toast.LENGTH_LONG).show();
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
	case (R.id.muMoreSet):
	    Toast.makeText(getApplicationContext(), "更多菜单项被点击",
		    Toast.LENGTH_LONG).show();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

}
