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
    // ��ʼ���߳�
    private Thread mCreateWork = null;
    // handler����UI
    private Handler mHandler = null;
    // handler���������쳣
    private Handler mWarmingHandler = null;
    // �����жԻ���
    private AlertDialog ad;
    // ��������״���Ի���
    private AlertDialog adwarming;

    // �����ؼ�
    // ѡ�񷿼�ͼƬ
    private ImageView ivRoomChoice;
    // ѡ���¯ͼƬ
    private ImageView ivBoilerChoice;
    // ����ͼƬ�µ�����
    private TextView tvRoomChoice;
    // ��¯ͼƬ�µ�����
    private TextView tvBoilerChoice;
    // ������URL
    private String baseURL = "http://192.168.0.97/Android/";
    // �������
    private String num = null;
    ActionBar acb;

    // �洢Intent������UID
    private String uid;

    // �������Ϊtrueʱͼ����Ե���������ܣ�
    private boolean clickRoom = true;
    private boolean clickBoiler = true;

    // ��������
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
	// Toast.makeText(getApplicationContext(), "ע����ת��",
	// Toast.LENGTH_LONG).show();
	// Log.v("Logout", bundle2.getBoolean("Logout"));
	String logout = getIntent().getStringExtra("Logout");
	// uid = getIntent().getStringExtra("uid");
	Log.v("Logout", logout);
	if (logout.equals("true")) {
	    // ��Activity����ΪSingleTask�����������ص���Activityʱ�����½�Activity��
	    // �������ص�ջ�׵�Activity,����մ�Activity֮�ϵ�Activity
	    // ֮��ִ����ת���µ�Activity��LoginUI������finish����Activity����ʵ��ע��
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

	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�CRMActivity�е�putExtra()�������ݹ����ļ�ֵ��
	uid = bundle.getString("uid");// ȡ����ֵ����uid������Ӧ��ֵ������ֵ��uid

	try {
	    acb = getActionBar();
	    acb.setDisplayShowTitleEnabled(false);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// ������������
	enterRoom = this.getString(R.string.enterRoom);
	controBoiler = this.getString(R.string.controlBoiler);
	upLoading = this.getString(R.string.uploading);
	noConnect = this.getString(R.string.noconnect);
	noMainController = this.getString(R.string.noMainController);

	// UI����handler
	mHandler = new Handler();
	mWarmingHandler = new Handler();

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	LayoutInflater factory = LayoutInflater.from(ChooseLogoUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(ChooseLogoUI.this).setView(dialog).show();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(ChooseLogoUI.this).setView(
		dialogWarming).create();

	// ������ʼ���߳�
	mCreateWork = new Thread(mCreateRunnable);
	mCreateWork.start();

	try {
	    String logout = bundle.getString("Logout");
	    if (logout.equals("towifi")) { // ��ת��ConfigWifiUI 
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

    // �����ǳ�ʼ���߳�
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "checkRoomBoiler.php?uid=" + uid;
	    Log.v("http����", url);
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		num = jsonObject.getString("num");
		Log.v("��ʼ����״��", re_err);
		Log.v("�������¯�������", num);

		// ������������֣�0���з����й�¯��1���з����޹�¯��2���޷����й�¯��3���޷����޹�¯��4����������δ����

		mHandler.post(mViewRunnable);

		if (re_err.equals("1")) {
		    // ��msg��handler������һ��toast
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
	    // ȡ�������жԻ���
	    ad.cancel();
	}

    };

    // UI Runnable
    private Runnable mViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (num.equals("0")) {
		// �����Ե����������ʾ
		clickRoom = true;
		clickBoiler = true;
		tvRoomChoice.setText(enterRoom);
		tvBoilerChoice.setText(controBoiler);
	    } else if (num.equals("1")) {
		// �з����޹�¯
		clickRoom = true;
		clickBoiler = false;
		tvRoomChoice.setText(enterRoom);
		tvBoilerChoice.setText(noConnect);
	    } else if (num.equals("2")) {
		// �޷����й�¯
		clickRoom = false;
		clickBoiler = true;
		tvRoomChoice.setText(upLoading);
		tvBoilerChoice.setText(controBoiler);
	    } else if (num.equals("3")) {
		// �޷����޹�¯
		clickRoom = false;
		clickBoiler = false;
		tvRoomChoice.setText(upLoading);
		tvBoilerChoice.setText(noConnect);
	    } else if (num.equals("4")) {
		// ��������δ����
		clickRoom = false;
		clickBoiler = false;
		tvRoomChoice.setText(noMainController);
		tvBoilerChoice.setText(noMainController);
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
	    // Toast.makeText(getApplicationContext(), "ע���˵�����",
	    // Toast.LENGTH_LONG).show();
	    // ��Ҫ�����ݿ���ļ�¼�û��ֶ���Ϊ0����Intent����¼ҳ�棬����������������֮�µ�Activity��
	    Logout lgo = new Logout();
	    if (lgo.Lgot(getApplicationContext()) == true)
		Toast.makeText(getApplicationContext(), "�ɹ�ע����",
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
	    // Toast.makeText(getApplicationContext(), "����˵�����",
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
