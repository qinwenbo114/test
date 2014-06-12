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

    // �����ؼ�
    // �豸�ű༭��
    private EditText etDevId = null;
    // ע��״̬��ʾ�ı���
    private TextView regDevStatus = null;
    // ע�ᰴť
    private Button btRegDev = null;

    // �豸ID
    private String devId = null;
    // �û�ID
    private String uId = null;
    // ������Ϣ
    private String re_err = null;
    // �����߳�
    private Thread mNetWork = null;
    // UI����Handler
    private Handler mHandler = null;
    // handler���������쳣
    private Handler mWarmingHandler = null;
    // ��������״���Ի���
    private AlertDialog adwarming;
    // ������URL
    private String baseURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_add_device);
	// ��ȡbaseURL
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	// ��ȡ�ϸ�ҳ�洫�ݵ�uid
	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�RoomListUI�е�putExtra()�������ݹ����ļ�ֵ��
	uId = bundle.getString("uid");

	// UI����Handler
	mHandler = new Handler();
	// �쳣��Ϣ�Ի��򵯳�Handler
	mWarmingHandler = new Handler();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(AddDeviceUI.this).setView(
		dialogWarming).create();

	// �󶨿ؼ�
	etDevId = (EditText) findViewById(R.id.etDevId);
	regDevStatus = (TextView) findViewById(R.id.regDevStatus);
	btRegDev = (Button) findViewById(R.id.btRegDev);

	// ע�ᰴť�����¼�
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

    // ע���������Runnable
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
		Log.v("���豸״��", re_err);
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

    // ����UI Runnable
    private Runnable mRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (re_err.equals("0")) {// ���豸���԰�
		Toast.makeText(AddDeviceUI.this, R.string.addDeviceSuccessed,
			Toast.LENGTH_SHORT).show();
		// ��ת��ChooseLogoҳ��
		Intent it = new Intent(AddDeviceUI.this, ChooseLogoUI.class);
		it.putExtra("uid", uId);
		it.putExtra("Logout", "towifi");
		startActivity(it);
		// ��ת������wifiģ��ҳ��
		// Intent it = new Intent(AddDeviceUI.this, ConfigWifiUI.class);
		// it.putExtra("uid", uId);
		// startActivity(it);
		AddDeviceUI.this.finish();
	    } else if (re_err.equals("1")) {// ���豸�ѱ���
		regDevStatus.setText(R.string.deviceHasBeenBound);
	    } else if (re_err.equals("2")) {// �����޴��豸
		regDevStatus.setText(R.string.dontHaveThisDevice);
	    } else if (re_err.equals("3")) {// �����û�ID�豸�Ų���Ϊ��
		regDevStatus.setText(R.string.deviceNumCannotBeNull);
	    }
	}
    };

}
