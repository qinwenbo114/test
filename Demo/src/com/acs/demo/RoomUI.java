package com.acs.demo;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class RoomUI extends Activity {
    // ��һ�¿ؼ�
    private TextView tvTempNow = null;
    private TextView tvTempSet = null;
    private TextView tvMode = null;
    private ImageButton btUp = null;
    // private Button btUp = null;
    private ImageButton btDown = null;
    private TextView tvHeatingStatus = null;
    private TextView tvTTC = null;
    // private TextView tvTTT = null;
    private ImageView ivSchedule = null;
    private ImageView ivParty = null;
    private ImageView ivOut = null;
    private ImageView ivSwitch = null;
    private LinearLayout llModel0 = null;
    private LinearLayout llModel1 = null;
    private ImageView ivAuto = null;
    private ImageView ivSave = null;
    private ImageView ivSwitch2 = null;
    private TextView tvComfTemp = null;
    private TextView tvSaveTemp = null;
    private SeekBar sbComfTemp = null;
    private SeekBar sbSaveTemp = null;
    // private Button btHeatingS = null;
    // private ToggleButton tbHeatingS = null;
    // private ImageView ivHeatingS = null;
    // �༭�Ի���ؼ�
    private EditText etRoomName = null;
    private TextView tvEngineMark = null;
    private TextView tvConnectErr = null;

    // ��ʼ�����¶ȸ����߳�
    private Thread mNetWork = null;
    // heatingStatus�����̣߳���������߳�����Ϊ���ǵ��û��Ķ�㴥��= =��
    private Thread mHeatingWork = null;
    // heatingMode�����߳�
    private Thread mModeWork = null;
    // seekBar�¶ȸ����߳�
    private Thread mSeekBarWork = null;
    // ʵʱ�����߳��ö�ʱ��ʵ��
    private Timer timer = null;
    // UI�ӳٸ��¼�ʱ��
    private Timer UIpauseTimer = null;
    // UI���������ʶ
    public boolean UIrefreshAllow = true;

    // handler����UI
    private Handler mHandler = null;
    // handler���������쳣
    private Handler mWarmingHandler = null;

    // ��ǰ�¶�
    private String tempNow;
    private String tempSet;
    // ���������¶�
    private String ct;
    private String st;
    int tpSet;
    // ��ůģʽ
    private String mode;
    // ��ů״̬
    int heatingStatus;
    // �¿�������
    int thModel;
    // ����״̬
    int con;
    // URL
    // BaseURL url = new BaseURL();
    private String baseURL = null;
    // ��Intent��ȡ����RoomID
    private String roomId = null;
    // ���Ƽ��
    private GestureDetector detector;
    // ��Intent��ȡ��roomName
    private String roomName = null;
    private String roomMark = null;
    // �����жԻ���
    private AlertDialog ad;
    // ��������״���Ի���
    private AlertDialog adwarming;

    private ActionBar acb = null;

    // ��������
    private String stopped = null;
    private String running = null;
    private String scheduleModel = null;
    private String partyModel = null;
    private String outModel = null;
    private String switchModel = null;
    private String autoModel = null;
    private String saveModel = null;
    private String roomInfo = null;
    private String cancel = null;
    private String save = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_room_temp_2);

	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();
	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�RoomListUI�е�putExtra()�������ݹ����ļ�ֵ��
	roomId = bundle.getString("ID");// ȡ����ֵ����ID������Ӧ��ֵ������ֵ��roomId
	roomName = bundle.getString("NAME");

	mHandler = new Handler();
	mWarmingHandler = new Handler();

	tvTempNow = (TextView) findViewById(R.id.tvTempNow);
	tvTempSet = (TextView) findViewById(R.id.tvTempSet);
	tvTTC = (TextView) findViewById(R.id.tvTTC);
	// tvTTT = (TextView) findViewById(R.id.tvTTT);
	tvMode = (TextView) findViewById(R.id.tvMode);
	btUp = (ImageButton) findViewById(R.id.btUp);
	btDown = (ImageButton) findViewById(R.id.btDown);
	tvHeatingStatus = (TextView) findViewById(R.id.tvHeatingStatus);
	// btHeatingS = (Button) findViewById(R.id.btHeatingS);
	// tbHeatingS = (ToggleButton) findViewById(R.id.tbHeatingS);
	// ivHeatingS = (ImageView) findViewById(R.id.ivHeatingS);
	ivSchedule = (ImageView) findViewById(R.id.ivSchedule);
	ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
	ivParty = (ImageView) findViewById(R.id.ivParty);
	ivOut = (ImageView) findViewById(R.id.ivOut);
	llModel0 = (LinearLayout) findViewById(R.id.llModel0);
	llModel1 = (LinearLayout) findViewById(R.id.llModel1);
	ivAuto = (ImageView) findViewById(R.id.ivAuto);
	ivSave = (ImageView) findViewById(R.id.ivSave);
	ivSwitch2 = (ImageView) findViewById(R.id.ivSwitch2);
	tvComfTemp = (TextView) findViewById(R.id.tvComfTemp);
	tvSaveTemp = (TextView) findViewById(R.id.tvSaveTemp);
	sbComfTemp = (SeekBar) findViewById(R.id.sbComfTemp);
	sbSaveTemp = (SeekBar) findViewById(R.id.sbSaveTemp);
	tvConnectErr = (TextView) findViewById(R.id.tvConnectErr);

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	LayoutInflater factory = LayoutInflater.from(RoomUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(RoomUI.this).setView(dialog).show();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RoomUI.this).setView(dialogWarming)
		.create();

	// ��ʼ���߳�
	mNetWork = new Thread(mCreateRunnable);
	mNetWork.start();

	// ActionBar������ʾ
	try {
	    acb = getActionBar();
	    acb.setTitle(roomName);
	    acb.setDisplayHomeAsUpEnabled(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	stopped = this.getString(R.string.stopped);
	running = this.getString(R.string.running);
	scheduleModel = this.getString(R.string.scheduleModel);
	partyModel = this.getString(R.string.partyModel);
	outModel = this.getString(R.string.outModel);
	switchModel = this.getString(R.string.switchModel);
	autoModel = this.getString(R.string.autoModel);
	saveModel = this.getString(R.string.saveModel);
	roomInfo = this.getString(R.string.roomInfo);
	cancel = this.getString(R.string.cancel);
	save = this.getString(R.string.save);

	// ʵʱ�����߳�
	timer = new Timer();
	timer.schedule(refreshTask, 5000, 5000);// ������5���ÿ��5��ˢ��һ��

	// ����Button��onclick�¼�������tvTempSet�����ݣ�������ͨ���̣߳��������ݴ������ݿ�
	btUp.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempSet.getText().toString().equals("n/a")) {
		    tpSet = 20;// Ĭ���¶�20
		    tvTTC.setText("��");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempSet.getText().toString());

		// �ѵ�ǰ�¶�+1������ʾ��tvTempSet�ؼ���
		if (tpSet < 45)
		    tpSet++;
		tvTempSet.setText("" + tpSet);// ���Ͽ��ַ������Խ�int����ת��ΪString����
		// �¶���ɫ�任
		if (tpSet >= 30) {
		    tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		} else if (tpSet > 20) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
		// �����߳� �����¶ȵ����ݿ�
		mHeatingWork = new Thread(mTempSetRunnable);
		mHeatingWork.start();
		UIPause();
		/*
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
		*/
	    }
	});

	btDown.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tvTempSet.getText().toString().equals("n/a")) {
		    tpSet = 20;// Ĭ���¶�20
		    tvTTC.setText("��");
		    // tvTTT.setText("target\ntemp");
		} else
		    tpSet = Integer.parseInt(tvTempSet.getText().toString());

		// �ѵ�ǰ�¶�+1������ʾ��tvTempSet�ؼ���
		if (tpSet > 5)
		    tpSet--;
		// �¶���ɫ�任
		tvTempSet.setText("" + tpSet);// ���Ͽ��ַ������Խ�int����ת��ΪString����
		if (tpSet <= 20) {
		    tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		} else if (tpSet < 30) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
		// �����߳� �����¶ȵ����ݿ�
		mNetWork = new Thread(mTempSetRunnable);
		mNetWork.start();
		UIPause();
		/*
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
		*/
	    }
	});

	// �¶��������¼�
	sbComfTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// ����ָ̧��ʱ ���������µ����ݿ�
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();

	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		// TODO Auto-generated method stub
		int t = 5 + sbComfTemp.getProgress();
		ct = "" + t;
		tvComfTemp.setText("" + t);
		Log.v("ComfTemp", t + "");
	    }
	});

	sbSaveTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// ����ָ̧��ʱ ���������µ����ݿ�
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();

	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		// TODO Auto-generated method stub
		int t = 5 + sbSaveTemp.getProgress();
		st = "" + t;
		tvSaveTemp.setText("" + t);
		Log.v("SaveTemp", t + "");
	    }
	});

	// heating״̬���ذ�ť��
	/*
	 * ivHeatingS.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub if (heatingStatus == 0) { tvHeatingStatus.setText("Running");
	 * heatingStatus = 1; ivHeatingS.setImageResource(R.drawable.on); //
	 * tbHeatingS.setChecked(true); // ���������̸߳������ݿ� mNetWork = new
	 * Thread(mHeatingRunnable); mNetWork.start(); } else {
	 * tvHeatingStatus.setText("Stoped"); heatingStatus = 0;
	 * ivHeatingS.setImageResource(R.drawable.off); //
	 * tbHeatingS.setChecked(false); // ���������̸߳������ݿ� mNetWork = new
	 * Thread(mHeatingRunnable); mNetWork.start(); } } });
	 */

	// heating״̬���ذ�ť��
	// tbHeatingS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (heatingStatus == 0)
	// {
	// tvHeatingStatus.setText("Heating");
	// heatingStatus = 1;
	// //tbHeatingS.setChecked(true);
	// // ���������̸߳������ݿ�
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// else
	// {
	// tvHeatingStatus.setText("Heating has been stoped");
	// heatingStatus = 0;
	// //tbHeatingS.setChecked(false);
	// // ���������̸߳������ݿ�
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// }
	// });

	// btHeatingS.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// // TODO Auto-generated method stub
	// if (heatingStatus == 0)
	// {
	// tvHeatingStatus.setText("��ů��");
	// heatingStatus = 1;
	// // ���������̸߳������ݿ�
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// else
	// {
	// tvHeatingStatus.setText("��ů��ֹͣ");
	// heatingStatus = 0;
	// // ���������̸߳������ݿ�
	// mNetWork = new Thread(mHeatingRunnable);
	// mNetWork.start();
	// }
	// }
	// });

	// ��ů״̬�л�(��ʱģʽ)
	ivSchedule.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("0")) {

		} else {
		    mode = "0";
		    tvMode.setText(scheduleModel);
		    ivSchedule.setImageResource(R.drawable.sc2);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// ��ů״̬�л�(�ƻ�ģʽ)
	ivParty.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("1")) {

		} else {
		    mode = "1";
		    tvMode.setText(partyModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh2);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}

	    }
	});

	// ��ů״̬�л�(���ģʽ)
	ivOut.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("2")) {

		} else {
		    mode = "2";
		    tvMode.setText(outModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out2);
		    ivSwitch.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}

	    }
	});

	// ��ů״̬�л�(�ػ�ģʽ)
	ivSwitch.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("3")) {

		} else {
		    mode = "3";
		    tvMode.setText(switchModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw2);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// ��ů״̬�л����Զ�ģʽ��
	ivAuto.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("0")) {

		} else {
		    mode = "0";
		    tvMode.setText(autoModel);
		    ivAuto.setImageResource(R.drawable.zd2);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// ��ůģʽ�л�������ģʽ��
	ivSave.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("1")) {

		} else {
		    mode = "1";
		    tvMode.setText(saveModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn2);
		    ivSwitch2.setImageResource(R.drawable.sw);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// ��ůģʽ�л����ػ�ģʽ��
	ivSwitch2.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mode.equals("2")) {

		} else {
		    mode = "2";
		    tvMode.setText(switchModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw2);
		    mModeWork = new Thread(mModeRunnable);
		    mModeWork.start();
		    UIPause();
		}
	    }
	});

	// ���Ʋ���
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.roomLinearLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);

    }

    // ������Ҫ�ĸ��߳�ͨ��Runnable��һ������ˢ��tempNow��һ��������ʼ����һ����������tempSet��һ���������¹�ů����

    // �����ǳ�ʼ���߳�
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "getRoomTemp.php?roomId=" + roomId;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		tempNow = jsonObject.getString("tempNow");
		tempSet = jsonObject.getString("tempSet");
		String heating = jsonObject.getString("heating");
		mode = jsonObject.getString("mode");
		roomMark = jsonObject.getString("roomId");
		String thModelS = jsonObject.getString("thModel");
		String longCt = jsonObject.getString("comfT");
		String ct2[] = longCt.split("\\.");
		ct = ct2[0];
		String longSt = jsonObject.getString("saveT");
		String st2[] = longSt.split("\\.");
		st = st2[0];
		String conS = jsonObject.getString("con");

		Log.v("��ʼ����״��", re_err);
		Log.v("��ǰ�¶�", tempNow);
		Log.v("�趨�¶�", tempSet);
		Log.v("��ů״̬", heating);
		Log.v("��ůģʽ", mode);
		Log.v("mark", roomMark);
		Log.v("thModel", thModelS);

		// �ѻ�ȡ�����¶���ʾ�ڿؼ���,����tempSet��ֵ��tpSet,heating��ֵ��heatingStatus
		if (tempSet.equals("") || tempSet.equals("null"))
		    tpSet = 20;
		else {
		    String ts[] = tempSet.split("\\."); // ע�⣡�ԡ�.��Ϊ�ָ���ʱ��Ҫת��
		    Log.v("tpǰ", ts[0]);
		    tpSet = Integer.parseInt(ts[0]);
		}
		if (heating.equals("") || heating.equals("null"))
		    heatingStatus = 0;
		else
		    heatingStatus = Integer.parseInt(heating);
		Log.v("��ů״̬��ʼ��", "" + heatingStatus);
		if (thModelS.equals("") || thModelS.equals("null")) {
		    thModel = 0;
		} else {
		    thModel = Integer.parseInt(thModelS);
		}
		if (conS.equals("1")) {
		    con = 1;
		} else {
		    con = 0;
		}

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

    // �����Ǹ���tempSet���̣߳����߳�������Button��onClick�¼�����
    private Runnable mTempSetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomTemp.php?roomId=" + roomId
		    + "&tempSet=" + tpSet;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("����temp״��", re_err);
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

    // SeekBar�����߳�
    private Runnable mSeekBarRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshCSTemp.php?roomId=" + roomId
		    + "&ct=" + ct + "&st=" + st;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("����CSTemp״��", re_err);
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
	}
    };

    // �����Ǹ���HeatingStatus���߳�runnable
    /*
     * private Runnable mHeatingRunnable = new Runnable() {
     * 
     * @Override public void run() { // TODO Auto-generated method stub String
     * url = baseURL + "refreshRoomHeating.php?roomId=" + roomId + "&heating=" +
     * heatingStatus; MyHttp myHttp = new MyHttp(); String retStr =
     * myHttp.httpGet(url); try { JSONObject jsonObject = new
     * JSONObject(retStr); String re_err = jsonObject.getString("error");
     * Log.v("����heating״��", re_err); if (re_err.equals("1")) { //
     * ��msg��handler������һ��toast } if (adwarming.isShowing()) adwarming.dismiss();
     * } catch (JSONException e) { // TODO Auto-generated catch block //
     * e.printStackTrace(); mWarmingHandler.post(mWarmingRunnable); } catch
     * (Exception e) { mWarmingHandler.post(mWarmingRunnable); } }
     * 
     * };
     */

    // �����Ǹ���Mode�߳�
    private Runnable mModeRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomMode.php?roomId=" + roomId
		    + "&mode=" + mode;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("����mode״��", re_err);
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

    // ����������ʵʱˢ���߳�
    private TimerTask refreshTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshRoomUI.php?roomId=" + roomId;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		tempNow = jsonObject.getString("tempNow");
		tempSet = jsonObject.getString("tempSet");
		String heating = jsonObject.getString("heating");
		mode = jsonObject.getString("mode");
		String conS = jsonObject.getString("con");

		Log.v("ʵʱˢ��״��", re_err);
		Log.v("ʵʱˢ���¶�", tempNow);
		Log.v("ʵʱˢ���趨�¶�", tempSet);
		Log.v("ʵʱˢ�¹�ů״̬", heating);
		Log.v("ʵʱˢ�¹�ůģʽ", mode);

		if (heating.equals("") || heating.equals("null"))
		    heatingStatus = 0;
		else
		    heatingStatus = Integer.parseInt(heating);
		if (conS.equals("1")) {
		    con = 1;
		} else {
		    con = 0;
		}

		// ����ȡ����״̬���µ�UI
		mHandler.post(mReViewRunnable);

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
		mWarmingHandler.post(mWarmingRunnable);
	    }
	}
    };
    
    // ������UI��ͣ����ģ��
    private void UIPause(){
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

    // �༭������Ϣ
    private Runnable mEditRoomRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "editRoom.php?roomId=" + roomId
		    + "&roomName=" + roomName;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		if (re_err.equals("0")) {
		    // ���³ɹ���ˢ�·�����
		    acb = null;
		    acb = getActionBar();
		    acb.setTitle(roomName);
		    acb.setDisplayHomeAsUpEnabled(true);
		    acb.show();
		} else {
		    // ����ʧ��
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    };

    // handler������ʼ��UI����
    private Runnable mViewRunnable = new Runnable() {
	@Override
	public void run() {
	    if (thModel == 0) {
		llModel0.setVisibility(View.VISIBLE);
		llModel1.setVisibility(View.GONE);
	    } else if (thModel == 1) {
		llModel0.setVisibility(View.GONE);
		llModel1.setVisibility(View.VISIBLE);
	    }
	    if (tempNow.equals("null") || tempNow.equals(""))
		tvTempNow.setText("n/a");
	    else
		tvTempNow.setText(tempNow);
	    if (tempSet.equals("null") || tempSet.equals("")) {
		tvTempSet.setText("n/a");
		tvTTC.setText("");
		// tvTTT.setText("");
	    } else {
		String ts[] = tempSet.split("\\.");
		tvTempSet.setText(ts[0]);
		int tp = Integer.parseInt(ts[0]);
		if (tp >= 30) {
		    tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		} else if (tp <= 20) {
		    tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		} else if (tp > 20 && tp < 30) {
		    tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		}
	    }
	    if (thModel == 0) {
		if (mode.equals("0")) {
		    tvMode.setText(scheduleModel);
		    ivSchedule.setImageResource(R.drawable.sc2);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("1")) {
		    tvMode.setText(partyModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh2);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("2")) {
		    tvMode.setText(outModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out2);
		    ivSwitch.setImageResource(R.drawable.sw);
		}
		if (mode.equals("3")) {
		    tvMode.setText(switchModel);
		    ivSchedule.setImageResource(R.drawable.sc);
		    ivParty.setImageResource(R.drawable.jh);
		    ivOut.setImageResource(R.drawable.out);
		    ivSwitch.setImageResource(R.drawable.sw2);
		}
	    } else if (thModel == 1) {
		if (mode.equals("0")) {
		    tvMode.setText(autoModel);
		    ivAuto.setImageResource(R.drawable.zd2);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw);
		}
		if (mode.equals("1")) {
		    tvMode.setText(saveModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn2);
		    ivSwitch2.setImageResource(R.drawable.sw);
		}
		if (mode.equals("2")) {
		    tvMode.setText(switchModel);
		    ivAuto.setImageResource(R.drawable.zd);
		    ivSave.setImageResource(R.drawable.jn);
		    ivSwitch2.setImageResource(R.drawable.sw2);
		}
	    }
	    if (heatingStatus == 0) {
		// ivHeatingS.setImageResource(R.drawable.off);
		// tbHeatingS.setChecked(false)

		tvHeatingStatus.setText(stopped);
	    } else {
		// ivHeatingS.setImageResource(R.drawable.on);
		// tbHeatingS.setChecked(true);
		tvHeatingStatus.setText(running);
	    }
	    if (ct.equals("") || ct.equals("null"))
		ct = "n/a";
	    if (st.equals("") || st.equals("null"))
		st = "n/a";
	    Log.v("��ʼ��ComfT", ct);
	    Log.v("��ʼ��SaveT", st);
	    // tvComfTemp.setText(ct);
	    // tvSaveTemp.setText(st);
	    if (ct.equals("n/a")) {
	    } else {
		sbComfTemp.setProgress(Integer.parseInt(ct) - 5);
		if ((Integer.parseInt(ct) - 5) == 0)
		    tvComfTemp.setText("5");
	    }
	    if (st.equals("n/a")) {
	    } else {
		sbSaveTemp.setProgress(Integer.parseInt(st) - 5);
		if ((Integer.parseInt(st) - 5) == 0)
		    tvSaveTemp.setText("5");
	    }
	    if (con == 1) {
		tvConnectErr.setVisibility(View.INVISIBLE);
	    } else {
		tvConnectErr.setVisibility(View.VISIBLE);
	    }
	}
    };

    // ʵʱˢ��UI����
    private Runnable mReViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (UIrefreshAllow == true) {
		if (tempNow.equals("null") || tempNow.equals(""))
		    tvTempNow.setText("n/a");
		else
		    tvTempNow.setText(tempNow);
		if (tempSet.equals("null") || tempSet.equals("")) {
		    tvTempSet.setText("n/a");
		    tvTTC.setText("");
		    // tvTTT.setText("");
		} else {
		    String ts[] = tempSet.split("\\.");
		    tvTempSet.setText(ts[0]);
		    int tp = Integer.parseInt(ts[0]);
		    if (tp >= 30) {
			tvTempSet.setTextColor(Color.rgb(255, 102, 31));
		    } else if (tp <= 20) {
			tvTempSet.setTextColor(Color.rgb(7, 187, 108));
		    } else if (tp > 20 && tp < 30) {
			tvTempSet.setTextColor(Color.rgb(00, 153, 204));
		    }
		}
		if (heatingStatus == 0) {
		    // ivHeatingS.setImageResource(R.drawable.off);
		    // tbHeatingS.setChecked(false);
		    tvHeatingStatus.setText(stopped);
		} else {
		    // ivHeatingS.setImageResource(R.drawable.on);
		    // tbHeatingS.setChecked(true);
		    tvHeatingStatus.setText(running);
		}
		if (thModel == 0) {
		    if (mode.equals("0")) {
			tvMode.setText(scheduleModel);
			ivSchedule.setImageResource(R.drawable.sc2);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("1")) {
			tvMode.setText(partyModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh2);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("2")) {
			tvMode.setText(outModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out2);
			ivSwitch.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("3")) {
			tvMode.setText(switchModel);
			ivSchedule.setImageResource(R.drawable.sc);
			ivParty.setImageResource(R.drawable.jh);
			ivOut.setImageResource(R.drawable.out);
			ivSwitch.setImageResource(R.drawable.sw2);
		    }
		} else if (thModel == 1) {
		    if (mode.equals("0")) {
			tvMode.setText(autoModel);
			ivAuto.setImageResource(R.drawable.zd2);
			ivSave.setImageResource(R.drawable.jn);
			ivSwitch2.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("1")) {
			tvMode.setText(saveModel);
			ivAuto.setImageResource(R.drawable.zd);
			ivSave.setImageResource(R.drawable.jn2);
			ivSwitch2.setImageResource(R.drawable.sw);
		    }
		    if (mode.equals("2")) {
			tvMode.setText(switchModel);
			ivAuto.setImageResource(R.drawable.zd);
			ivSave.setImageResource(R.drawable.jn);
			ivSwitch2.setImageResource(R.drawable.sw2);
		    }
		}
		if (con == 1) {
		    tvConnectErr.setVisibility(View.INVISIBLE);
		} else {
		    tvConnectErr.setVisibility(View.VISIBLE);
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
	    if ((e2.getX() - e1.getX()) > 50) {
		// Ϊʲô��50�� ����������ģ������С����������ģ������ȣ�e2.getX()-e1.getX()<��Ļ��Ⱦͣϣ�
		/*
		 * Toast.makeText(getApplicationContext(), "���һ���,�������룺" +
		 * (e2.getX() - e1.getX()), Toast.LENGTH_LONG).show();
		 */
		// Ҫ����ʲô�¼���������д��OK
		// ���Ҫ��ת������һ��activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		RoomUI.this.finish();

	    }
	    if ((e2.getX() - e1.getX()) < -50) {
		/*
		 * Toast.makeText(getApplicationContext(), "���󻬶�,�������룺" +
		 * (e2.getX() - e1.getX()), Toast.LENGTH_LONG).show();
		 */
		if (thModel == 0) {
		    Intent intent = new Intent(RoomUI.this, ProgramLandUI.class);
		    intent.putExtra("ID", roomId);
		    intent.putExtra("NAME", roomName);
		    startActivity(intent);
		}
	    }
	    /*
	     * if ((e2.getY()-e1.getY())<-50) {
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
	if (thModel == 0) {
	    inflater.inflate(R.menu.room_actionbar, menu);
	} else if (thModel == 1) {
	    inflater.inflate(R.menu.room_without_program_actionbar, menu);
	}
	Log.d("TH_Model", thModel + "");
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    RoomUI.this.finish();
	    return true;
	case (R.id.muSetting):
	    LayoutInflater factory = LayoutInflater.from(RoomUI.this);
	    final View editRoom = factory.inflate(R.layout.dialog_edit_room,
		    null);
	    new AlertDialog.Builder(RoomUI.this)
		    .setView(editRoom)
		    .setTitle(roomInfo)
		    .setNegativeButton(cancel,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub

				}
			    })
		    .setPositiveButton(save,
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub
				    roomName = etRoomName.getText().toString();
				    // ��������ͨ�ţ����淿����Ϣ
				    mNetWork = new Thread(mEditRoomRunnable);
				    mNetWork.start();
				}
			    }).show();

	    etRoomName = (EditText) editRoom.findViewById(R.id.etRoomName);
	    tvEngineMark = (TextView) editRoom.findViewById(R.id.tvEngineMark);
	    etRoomName.setText(roomName);
	    tvEngineMark.setText(roomMark);
	    return true;
	case (R.id.muSchedule):
	    if (thModel == 0) {
		Intent intent2 = new Intent(RoomUI.this, ProgramLandUI.class);
		intent2.putExtra("ID", roomId);
		intent2.putExtra("NAME", roomName);
		startActivity(intent2);
	    } else {
	    }
	    return true;
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
