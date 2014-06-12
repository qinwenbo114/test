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
    // �����ؼ�
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

    // ��ʼ��״̬
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

    // ��ʼ���߳�
    private Thread mCreateWork = null;
    // ʵʱˢ�¶�ʱ���߳�
    private Timer timer = null;
    // UI�ӳٸ��¼�ʱ��
    private Timer UIpauseTimer = null;
    // UI���������ʶ
    public boolean UIrefreshAllow = true;
    // Ŀ���¶��趨�߳�
    private Thread mTempSetWork = null;

    // handler����UI
    private Handler mHandler = null;
    private Handler mWarmingHandler = null;

    // ��Intent��ȡ����boilerId����UID
    private String boilerId = null;
    private String uid = null;
    // ���Ƽ��
    private GestureDetector detector;

    private int tpSet;

    // �����жԻ���
    private AlertDialog ad;
    private AlertDialog adwarming;

    // ͨ���б�ʶ����ֹ�߳�����
    // private boolean running = false;

    // ��������
    private String connecting = null;
    private String connectError = null;

    // �û��Ƿ�����¿����豸
    private boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_boiler_temp);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�RoomListUI�е�putExtra()�������ݹ����ļ�ֵ��
	// boilerId = bundle.getString("ID");//ȡ����ֵ����ID������Ӧ��ֵ������ֵ��boilerId
	uid = bundle.getString("uid");// ���ͨ��UID��ȡ����ʼ��ʱ�Ƚ�boilerId��ֵ

	mHandler = new Handler();// ����������UI
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

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	LayoutInflater factory = LayoutInflater.from(BoilerUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(BoilerUI.this).setView(dialog).show();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(BoilerUI.this).setView(
		dialogWarming).create();

	// ������ʼ���߳�
	mCreateWork = new Thread(mCreateRunnable);
	mCreateWork.start();

	// ActionBar������ʾ
	ActionBar acb = getActionBar();
	acb.setTitle(R.string.boiler);
	acb.setDisplayHomeAsUpEnabled(true);

	// ���Ʋ���
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.boilerLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);

	connecting = this.getString(R.string.connecting);
	connectError = this.getString(R.string.connectError);

	// ��ů���ذ�ť����
	// tbHS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // �ػ�״̬�²��ܿ�����ů���д�״̬�²��ܿ�����ů
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // �˴���ͼ�������Ժ����~
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
	// // ʹ�ó�ʼ���߳�
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // �˴�Ϊ�ػ�״̬������״��δ֪
	// // �������ȿ���Toast
	// Toast.makeText(BoilerUI.this, "���ȿ�����", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // �˴�Ϊ����״̬�����д�
	// // ����Toast��״̬�쳣�����ȸ�λ
	// Toast.makeText(BoilerUI.this, "״̬�쳣�����ȸ�λ��",
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
		    // �˴���ͼ�������Ժ����~
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
		// ʹ�ó�ʼ���߳�
		mCreateWork = new Thread(mHSBDRunnable);
		mCreateWork.start();
		/*
		 * } else if (boilerDo == 0) { // �˴�Ϊ�ػ�״̬������״��δ֪ // �������ȿ���Toast
		 * Toast.makeText(BoilerUI.this, "���ȿ�����", Toast.LENGTH_SHORT)
		 * .show(); } else if (error != 0) { // �˴�Ϊ����״̬�����д� //
		 * ����Toast��״̬�쳣�����ȸ�λ Toast.makeText(BoilerUI.this, "״̬�쳣�����ȸ�λ��",
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
	// // �ػ�״̬�²��ܿ�����ů���д�״̬�²��ܿ�����ů
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // �˴���ͼ�������Ժ����~
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
	// // ʹ�ó�ʼ���߳�
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // �˴�Ϊ�ػ�״̬������״��δ֪
	// // �������ȿ���Toast
	// Toast.makeText(BoilerUI.this, "���ȿ�����", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // �˴�Ϊ����״̬�����д�
	// // ����Toast��״̬�쳣�����ȸ�λ
	// Toast.makeText(BoilerUI.this, "״̬�쳣�����ȸ�λ��",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });

	// ��ů���ذ�ť����
	// btHS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // �ػ�״̬�²��ܿ�����ů���д�״̬�²��ܿ�����ů
	// if (boilerDo == 1 && error == 0)
	// {
	// if (heatingStatus == 0)
	// {
	// // �˴���ͼ�������Ժ����~
	// heatingStatus = 1;
	// ivHeating.setImageResource(R.drawable.heating40);
	// }
	// else
	// {
	// heatingStatus = 0;
	// ivHeating.setImageResource(R.drawable.noheating40);
	// }
	// // ʹ�ó�ʼ���߳�
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// else if (boilerDo == 0)
	// {
	// // �˴�Ϊ�ػ�״̬������״��δ֪
	// // �������ȿ���Toast
	// Toast.makeText(BoilerUI.this, "���ȿ�����", Toast.LENGTH_SHORT)
	// .show();
	// }
	// else if (error != 0)
	// {
	// // �˴�Ϊ����״̬�����д�
	// // ����Toast��״̬�쳣�����ȸ�λ
	// Toast.makeText(BoilerUI.this, "״̬�쳣�����ȸ�λ��",
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
	// tvBoilerDo.setText("������");
	// // �˴�������ͼ��任���Ժ�����
	// ivHeating.setImageResource(R.drawable.noheating40);
	// ivFire.setImageResource(R.drawable.nofire40);
	// }
	// else if (boilerDo == 0)
	// {
	// tbBS.setChecked(true);
	// boilerDo = 1;
	// tvBoilerDo.setText("������");
	// }
	// // ʹ�ó�ʼ���߳�
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
	// // �˴�������ͼ��任���Ժ�����
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
	// // ʹ�ó�ʼ���߳�
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
	// tvBoilerDo.setText("������");
	// // �˴�������ͼ��任���Ժ�����
	// }
	// else if (boilerDo == 0)
	// {
	// tbBS.setChecked(false);
	// boilerDo = 1;
	// tvBoilerDo.setText("������");
	// }
	// // ʹ�ó�ʼ���߳�
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// �ܿ��ذ�ť����
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
	// tvBoilerDo.setText("������");
	// // �˴�������ͼ��任���Ժ�����
	// }
	// else if (boilerDo == 0)
	// {
	// boilerDo = 1;
	// tvBoilerDo.setText("������");
	// }
	// // ʹ�ó�ʼ���߳�
	// mCreateWork = new Thread(mHSBDRunnable);
	// mCreateWork.start();
	// }
	// });

	// ��λ��ť����
	// btRS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// // ʹ�ó�ʼ���߳�
	// if (error == 0)
	// Toast.makeText(BoilerUI.this, "��ǰ��������", Toast.LENGTH_SHORT)
	// .show();
	// if(error != 0&&lock==0){
	// Toast.makeText(BoilerUI.this, "�ڹ�¯�����Զ��޸�", Toast.LENGTH_SHORT)
	// .show();
	// }
	// if(lock==1)
	// {
	// //lock = 0;
	// // �˴���һ��ͼ��任���Ժ�����~
	// //ivError.setImageResource(R.drawable.nowarming40);
	// //ivLock.setAlpha(0);
	// Toast.makeText(BoilerUI.this, "�ѷ��͸�λ�źţ����Ժ�...", Toast.LENGTH_SHORT)
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
		    // �˴���һ��ͼ��任���Ժ�����~
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

	// ���°�ť�����¼�
	btUp.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempTar.getText().toString().equals("n/a")) {
		    tpSet = 20;// Ĭ���¶�20
		    tvTTC.setText("��");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempTar.getText().toString());

		// �ѵ�ǰ�¶�+1������ʾ��tvTempSet�ؼ���
		if (tpSet < 80)
		    tpSet++;
		tvTempTar.setText("" + tpSet);// ���Ͽ��ַ������Խ�int����ת��ΪString����
		// �¶���ɫ�任
		if (tpSet >= 60) {
		    tvTempTar.setTextColor(Color.rgb(255, 102, 31));
		} else if (tpSet > 40) {
		    tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		}
		// �����̸߳����¶�
		mTempSetWork = new Thread(mTempSetRunnable);
		mTempSetWork.start();
		UIPause();
	    }
	});

	// ���°�ť�����¼�
	btDown.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempTar.getText().toString().equals("n/a")) {
		    tpSet = 20;// Ĭ���¶�20
		    tvTTC.setText("��");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempTar.getText().toString());

		// �ѵ�ǰ�¶�+1������ʾ��tvTempSet�ؼ���
		if (tpSet > 5)
		    tpSet--;
		tvTempTar.setText("" + tpSet);// ���Ͽ��ַ������Խ�int����ת��ΪString����

		// �¶���ɫ�任
		if (tpSet <= 40) {
		    tvTempTar.setTextColor(Color.rgb(7, 187, 108));
		} else if (tpSet < 60) {
		    tvTempTar.setTextColor(Color.rgb(00, 153, 204));
		}
		// �����̸߳����¶�
		mTempSetWork = new Thread(mTempSetRunnable);
		mTempSetWork.start();
		UIPause();
	    }
	});
    }

    // ��ʼ��Runnable
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    // ���Ȼ�ȡboilerId
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
			boilerId = "00";// ����00˵�����û���δ��ӹ�¯
			Log.v("��¯", "δ���");
		    } else {
			boilerId = bid;
		    }
		    if (existS.equals("0")) {
			// ���û��������¿����豸����¯���Ե���
			exist = false;
		    } else if (existS.equals("1")) {
			// ���û������¿����豸����¯���ܵ���
			exist = true;
		    }
		} else {
		    boilerId = "01";// ����01˵����ȡ��Ϣ����
		}
		Log.v("����ȡ��¯ID", boilerId);
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
		// ���û���δ�󶨹�¯,��ת���󶨹�¯ҳ��
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), IfAddBoiler.class);
		startActivity(intent);
		BoilerUI.this.finish();
	    } else if (boilerId.equals("01")) {
		// ��ȡ��¯��Ϣ������ת������ҳ��
	    } else {
		// ��ȡ�û���¯��Ϣ
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

		    Log.v("��ʼ��״��", re_err);
		    Log.v("��ǰ��¯��", tempNow);
		    Log.v("�����¶�", tempOut);
		    Log.v("Ŀ���¶�", tempTar);
		    Log.v("��ů״̬", weS);
		    // Log.v("����״̬", bDoS);
		    Log.v("����״̬", bConS);
		    Log.v("����״̬", fireS);
		    Log.v("����״̬1", errorSS);
		    Log.v("����״̬2", errS);
		    Log.v("��״̬", lockS);

		    mHandler.post(mViewRunnable);

		    // ����ʵʱˢ�¶�ʱ��,��ʱ��Ӧ���ڳ�ʼ���ɹ�����
		    timer = new Timer();
		    timer.schedule(refreshTask, 5000, 5000);// ������5���ÿ��5��ˢ��һ��

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
		// �����жԻ���ȡ��
		ad.cancel();
	    }
	}
    };

    // ʵʱ����TimerTask
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

		Log.v("ˢ��״��", re_err);
		Log.v("��ǰ��¯��", tempNow);
		Log.v("�����¶�", tempOut);
		Log.v("Ŀ���¶�", tempTar);
		Log.v("��ů״̬", weS);
		// Log.v("����״̬", bDoS);
		Log.v("����״̬", bConS);
		Log.v("����״̬2", "" + con);
		Log.v("����״̬", fireS);
		Log.v("����״̬1", errorSS);
		Log.v("����״̬2", errS);
		Log.v("��״̬", lockS);

		mHandler.post(mReViewRunnable);

		if (re_err.equals("1")) {
		    // ��msg��handler������һ��toast
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

    // ��ů���غͿ��ػ���ť��Runnable
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
		    Log.v("���¹�¯״̬", "�ɹ���");
		}

		if (re_err.equals("1")) {
		    // ��msg��handler������һ��toast
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

    // ��λ��ťRunnable
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
		    Log.v("ִ�и�λ����", "�ɹ���");
		}

		if (re_err.equals("1")) {
		    // ��msg��handler������һ��toast
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

    // �����Ǹ���tempSet���̣߳����߳�������Button��onClick�¼�����
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
		Log.v("���¹�¯temp״��", re_err);
		// running = false;
		if (re_err.equals("1")) {
		    // ��msg��handler������һ��toast
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

    // ������UI��ͣ����ģ��
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
	    UIpauseTimer.schedule(UIallowTask, 10000);// 10����UI���������
	} else {
	    UIpauseTimer = new Timer();
	    UIpauseTimer.schedule(UIallowTask, 10000);// 10����UI���������
	}
    }

    // ������UI����������ʱTask
    private TimerTask UIallowTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    UIrefreshAllow = true;
	}
    };

    // ��ʼ��UI��Runnable
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
	    // ����ͼƬ״̬���Ժ󲹳�~
	    if (warmEnable == 0) {// ��ʾֹͣ��ůͼƬ
		ivHeating.setImageResource(R.drawable.noheating40);
		// tbHS.setChecked(false);
		ivHS.setImageResource(R.drawable.heatingoff2);
	    } else if (warmEnable == 1) {// ��ʾ���ڹ�ůͼƬ
		ivHeating.setImageResource(R.drawable.heating40);
		// tbHS.setChecked(true);
		ivHS.setImageResource(R.drawable.heatingon2);
	    }
	    if (fire == 0) {// ��ʾ����Ϩ��ͼƬ
		ivFire.setImageResource(R.drawable.nofire40);
	    } else if (fire == 1) {// ��ʾ����ȼ��ͼƬ
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
		// ����ʾ��
		ivLock.setAlpha(0);
	    } else {
		// ��ʾ��
		ivLock.setAlpha(200);
		// ivLock.setImageResource(R.drawable.lock);
	    }
	    if (exist == true) {
		// ���ܵ��£����°�ť����
		btUp.setVisibility(View.GONE);
		btDown.setVisibility(View.GONE);
	    } else {
		// ���Ե��£����°�ť��ʾ
	    }

	}

    };

    // ˢ��UI��Runnable
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
		// ����ͼƬ״̬���Ժ󲹳�~
		if (warmEnable == 0) {// ��ʾֹͣ��ůͼƬ
				      // tbHS.setChecked(false);
		    ivHS.setImageResource(R.drawable.heatingoff2);
		    ivHeating.setImageResource(R.drawable.noheating40);
		} else if (warmEnable == 1) {// ��ʾ���ڹ�ůͼƬ
					     // tbHS.setChecked(true);
		    ivHS.setImageResource(R.drawable.heatingon2);
		    ivHeating.setImageResource(R.drawable.heating40);
		}
		if (fire == 0) {// ��ʾ����Ϩ��ͼƬ
		    ivFire.setImageResource(R.drawable.nofire40);
		} else if (fire == 1) {// ��ʾ����ȼ��ͼƬ
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
		    // ����ʾ��
		    ivLock.setAlpha(0);
		} else {
		    // ��ʾ��
		    ivLock.setAlpha(200);
		    // ivLock.setImageResource(R.drawable.lock);
		}
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

    // ����дһ�����һ��������ϲ�Activity�����Ʋ���
    // ������Ļ����
    class TouhListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    // Toast.makeText(getApplicationContext(), "----?",
	    // event.getAction()).show();
	    return detector.onTouchEvent(event);
	}

    }

    // ���ƻ�������
    class GestureListener implements OnGestureListener {

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	    // e1 ��������ʼλ�ã�e2 �����Ľ���λ�ã�velocityX X��ÿһ���ƶ��������ٶȣ���������˼�� velocityY
	    // ���ǣٿ�
	    // ������,��Ϊ�� �������ң���Ϊ����
	    if (e2.getX() - e1.getX() > 50) {
		// Ϊʲô��50�� ����������ģ������С����������ģ������ȣ�e2.getX()-e1.getX()<��Ļ��Ⱦͣϣ�
		/*
		 * Toast.makeText(getApplicationContext(), "���һ���",
		 * Toast.LENGTH_LONG).show();
		 */
		// Ҫ����ʲô�¼���������д��OK
		// ���Ҫ��ת������һ��activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		BoilerUI.this.finish();
	    }

	    if ((e2.getX() - e1.getX()) < -50) {
		/*
		 * Toast.makeText(getApplicationContext(), "���󻬶�",
		 * Toast.LENGTH_LONG).show();
		 */
		Intent it = new Intent();
		it.setClass(BoilerUI.this, ChooseTempCurveUI.class);
		it.putExtra("ID", boilerId);
		startActivity(it);
	    }
	    /*
	     * if (Math.abs(e2.getY()-e1.getY())>50) {
	     * Toast.makeText(getApplicationContext(), "���ϻ���",
	     * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
	     * Toast.makeText(getApplicationContext(), "���»���",
	     * Toast.LENGTH_LONG).show(); }
	     */
	    return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	    // TODO Auto-generated method stub\
	    /*
	     * �����¼� һ�г��¼�����Ļ��Ҫ�������¼���������д
	     */
	    // Toast.makeText(getApplicationContext(),
	    // "------------> onLongPress", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
		float distanceX, float distanceY) {
	    // TODO Auto-generated method stub
	    /*
	     * ����������������������distanceX ��X��·�����ȣ�distanceY ��Y��·�����ȣ�ע�⣺��·��������λ�ƣ�;
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
	     * "�¶����߲˵�����", Toast.LENGTH_LONG).show(); Intent it = new
	     * Intent(); it.setClass(getApplicationContext(),
	     * ChooseTempCurveUI.class); it.putExtra("ID", boilerId);
	     * startActivity(it); return true;
	     */
	case (R.id.muLogOut):
	    /*
	     * Toast.makeText(getApplicationContext(), "ע���˵�����",
	     * Toast.LENGTH_LONG).show();
	     */
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
	case (R.id.muWifiSet):
	    Intent intent3 = new Intent();
	    intent3.setClass(getApplicationContext(), ConfigWifiUI.class);
	    intent3.putExtra("login", true);
	    startActivity(intent3);
	    return true;
	case (R.id.muMoreSet):
	    /*
	     * Toast.makeText(getApplicationContext(), "����˵�����",
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

    // Activity�ر�ʱ��ִ�����²������ͷ���Դ
    public void onDestroy() {
	super.onDestroy();
	if (timer != null) {
	    timer.cancel();
	    timer = null;
	}
    }

}
