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

    // �û����༭��
    private EditText etRegUN = null;
    // ����༭��
    private EditText etRegUP = null;
    // ȷ������༭��
    private EditText etRegReUP = null;
    // �ֻ��ű༭��
    private EditText etRegPhone = null;
    // Email�༭��
    private EditText etRegEmail = null;
    // ��ַ�༭��
    private EditText etRegAdd = null;
    // ע�ᰴť
    private Button btReg = null;
    // ������Ϣ��ʾ�ı���
    private TextView tvRegInfo = null;

    // �û���
    private String RegUN = null;
    // ����
    private String RegUP = null;
    // �ֻ���
    private String RegPhone = null;
    // Email
    private String RegEmail = null;
    // ��ַ
    private String RegAdd = null;

    // ����ͨ���߳�
    private Thread mNetWork = null;

    // URL
    private String baseURL = "http://192.168.0.97/Android/";

    // ���Ƶ����쳣״̬��Handler
    private Handler mWarmingHandler = null;
    // ����UI���µ�Handler
    private Handler mHandler = null;

    // ��������״���Ի���
    private AlertDialog adwarming;
    private AlertDialog regInfo;

    // ע�ᷴ����Ϣ
    private String re_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_register);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	
	// HandlerҪ��onCreate�����ﴴ������
	mWarmingHandler = new Handler();
	mHandler = new Handler();

	// �����Ի���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RegisterUI.this).setView(
		dialogWarming).create();

	// �����Ի���������ʾע��״��
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

	// �󶨿ؼ�
	etRegUN = (EditText) findViewById(R.id.etRegUN);
	etRegUP = (EditText) findViewById(R.id.etRegUP);
	etRegReUP = (EditText) findViewById(R.id.etRegReUP);
	etRegPhone = (EditText) findViewById(R.id.etRegPhone);
	etRegEmail = (EditText) findViewById(R.id.etRegEmail);
	etRegAdd = (EditText) findViewById(R.id.etRegAdd);
	btReg = (Button) findViewById(R.id.btReg);
	tvRegInfo = (TextView) dialogInfo.findViewById(R.id.tvRegInfo);

	// ע�ᰴť�����¼�
	btReg.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (etRegUP.getText().toString()
			.equals(etRegReUP.getText().toString())) {
		    // ���������������һ�£���ȡ����������ݣ�׼������ͨ��
		    RegUN = etRegUN.getText().toString();
		    RegUP = etRegUP.getText().toString();
		    RegEmail = etRegEmail.getText().toString();
		    RegPhone = etRegPhone.getText().toString();
		    RegAdd = etRegAdd.getText().toString();
		    if (RegUN.equals("") || RegUP.equals("")) {
			// ����û����������Ϊ�գ������û��������벻��Ϊ�նԻ���
			regInfo.show();
			tvRegInfo.setText(R.string.UNorPWCannotBeNull);

		    } else if (RegEmail.equals("")) {
			// �������༭��Ϊ�գ��������䲻��Ϊ��
			regInfo.show();
			tvRegInfo.setText(R.string.emailCannotBeNull);
		    } else if (!is_email(RegEmail)) {
			// ��������ʽ��֤ʧ�ܣ�����������֤ʧ��
			regInfo.show();
			tvRegInfo.setText(R.string.emailError);
		    } else if (!is_username(RegUN)) {
			// ����û�����ʽ��֤ʧ�ܣ������û�����ʽ����
			regInfo.show();
			tvRegInfo.setText(R.string.UNError);
		    } else if (!is_password(RegUP)) {
			// ��������ʽ��֤ʧ�ܣ����������ʽ����
			regInfo.show();
			tvRegInfo.setText(R.string.PWError);
		    } else {
			// ���ȫ����ȷ������ע���̣߳����û���Ϣ�������ݿ�
			mNetWork = new Thread(mRegRunnable);
			mNetWork.start();
		    }
		} else {
		    // ��������������벻һ�£������������벻һ�¶Ի���
		    regInfo.show();
		    tvRegInfo.setText(R.string.RePwError);
		}
	    }
	});
    }

    private boolean is_email(String email) {
	Log.v("�����ʽ", email);
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
		Log.v("ע��״��", re_err);
		mHandler.post(mReRegRunnable);

	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }

	}
    };

    // ע�ᷴ������Runnable
    private Runnable mReRegRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (re_err.equals("0")) {
		// ע��ɹ�����ת����¼ҳ�棬finish��ҳ��
		Toast.makeText(getApplicationContext(),
			R.string.registSuccessed, Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setClass(RegisterUI.this, LoginUI.class);
		startActivity(intent);
	    } else if (re_err.equals("1")) {
		// ע��ʧ�ܣ����û����ѱ�ռ��
		Toast.makeText(getApplicationContext(), R.string.registFailed1,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed1);
	    } else if (re_err.equals("2")) {
		// ע��ʧ�ܣ��������ѱ�ע��
		Toast.makeText(getApplicationContext(), R.string.registFailed2,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed2);
	    } else if (re_err.equals("3")) {
		// ע��ʧ�ܣ�����ԭ��
		Toast.makeText(getApplicationContext(), R.string.registFailed,
			Toast.LENGTH_LONG).show();
		regInfo.show();
		tvRegInfo.setText(R.string.registFailed);
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
