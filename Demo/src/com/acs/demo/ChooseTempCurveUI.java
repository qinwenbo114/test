package com.acs.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChooseTempCurveUI extends Activity {

    // ��ʼ���߳�
    private Thread mCreateWork = null;
    // �����߳�
    private Thread mTouchWork = null;
    // seekBar�����߳�
    private Thread mSeekBarWork = null;
    // handler����UI
    private Handler mHandler = null;
    // handler���������쳣
    private Handler mWarmingHandler = null;
    // private SeekBar sbChoosePic = null;
    private ImageView ivPic = null;
    private ImageView ivCurveUp = null;
    private ImageView ivCurveDown = null;

    private String boilerId = null;
    private String baseURL = null;
    private String tempCurve = null;

    // �����жԻ���
    private AlertDialog ad;
    // ��������״���Ի���
    private AlertDialog adwarming;
    // ���Ƽ��
    private GestureDetector detector;
    // ͼƬ��� 0-9
    private int migNum;
    // ͼƬ�л���λ
    private int i = 0;
    // ���Ƴ�ʼλ��
    private float start;
    private boolean fast = false;
    private int mPointerId; 
    private int mMaxVelocity;
    private VelocityTracker verTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_choose_temp_curve);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�BoilerUI�е�putExtra()�������ݹ����ļ�ֵ��
	boilerId = bundle.getString("ID");// ȡ����ֵ����ID������Ӧ��ֵ������ֵ��boilerId

	// sbChoosePic = (SeekBar) findViewById(R.id.sbChoosePic);
	ivPic = (ImageView) findViewById(R.id.ivPic);
	ivCurveUp = (ImageView) findViewById(R.id.ivCurveUp);
	ivCurveDown = (ImageView) findViewById(R.id.ivCurveDown);

	mHandler = new Handler();// ����������UI
	mWarmingHandler = new Handler();

	// ������ʼ���߳�
	mCreateWork = new Thread(mCreateRunnable);
	mCreateWork.start();

	// ActionBar������ʾ
	ActionBar acb = getActionBar();
	acb.setTitle(R.string.curve);
	acb.setDisplayHomeAsUpEnabled(true);

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	LayoutInflater factory = LayoutInflater.from(ChooseTempCurveUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(ChooseTempCurveUI.this).setView(dialog)
		.create();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(ChooseTempCurveUI.this).setView(
		dialogWarming).create();

	// ���Ʋ���
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.tempCurveLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);
	
	ivCurveUp.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		i++;
		changeMig(i);
		int trueMigNum = (i + migNum + 100) % 10;
		migNum = trueMigNum;
		i = 0;
		fast = false;
		Log.d("i", "��λ");
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();
	    }
	});
	
	ivCurveDown.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		i--;
		changeMig(i);int trueMigNum = (i + migNum + 100) % 10;
		migNum = trueMigNum;
		i = 0;
		fast = false;
		Log.d("i", "��λ");
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();
	    }
	});

	/*
	 * sbChoosePic.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
	 * {
	 * 
	 * @Override public void onStopTrackingTouch(SeekBar seekBar) { // TODO
	 * Auto-generated method stub // ����ָ̧��ʱ ���������µ����ݿ� mSeekBarWork = new
	 * Thread(mSeekBarRunnable); mSeekBarWork.start();
	 * 
	 * }
	 * 
	 * @Override public void onStartTrackingTouch(SeekBar seekBar) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onProgressChanged(SeekBar seekBar, int
	 * progress, boolean fromUser) { // TODO Auto-generated method stub int
	 * cn = sbChoosePic.getProgress(); switch (cn) { case 0: tempCurve =
	 * "2"; ivPic.setImageResource(R.drawable.pic0); break; case 1:
	 * tempCurve = "4"; ivPic.setImageResource(R.drawable.pic1); break; case
	 * 2: tempCurve = "6"; ivPic.setImageResource(R.drawable.pic2); break;
	 * case 3: tempCurve = "8"; ivPic.setImageResource(R.drawable.pic3);
	 * break; case 4: tempCurve = "10";
	 * ivPic.setImageResource(R.drawable.pic4); break; case 5: tempCurve =
	 * "12"; ivPic.setImageResource(R.drawable.pic5); break; case 6:
	 * tempCurve = "15"; ivPic.setImageResource(R.drawable.pic6); break;
	 * case 7: tempCurve = "20"; ivPic.setImageResource(R.drawable.pic7);
	 * break; case 8: tempCurve = "25";
	 * ivPic.setImageResource(R.drawable.pic8); break; case 9: tempCurve =
	 * "30"; ivPic.setImageResource(R.drawable.pic9); break; } } });
	 */
	mMaxVelocity = ViewConfiguration.get(this).getMaximumFlingVelocity(); 
    }

    // ��ʼ���߳�
    private Runnable mCreateRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "getTempCurve.php?boilerId=" + boilerId;
	    MyHttp myhttp = new MyHttp();
	    String retStr = myhttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		String tempCurveS = jsonObject.getString("tempCurve");
		if (re_err.equals("0")) {
		    if (tempCurveS.equals("") || tempCurveS.equals("null")) {
			tempCurve = "2";
		    } else {
			tempCurve = tempCurveS;
		    }

		    mHandler.post(mViewRunnable);
		} else {// δ��ȡ��json����
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		mWarmingHandler.post(mWarmingRunnable);
	    } catch (Exception e) {
		mWarmingHandler.post(mWarmingRunnable);
	    }
	    // ȡ���Ի���
	    ad.cancel();
	}
    };

    // ui�����߳�
    private Runnable mViewRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    if (tempCurve.equals("2")) {
		ivPic.setImageResource(R.drawable.pic0);
		migNum = 0;
		// sbChoosePic.setProgress(0);
	    } else if (tempCurve.equals("4")) {
		ivPic.setImageResource(R.drawable.pic1);
		migNum = 1;
		// sbChoosePic.setProgress(1);
	    } else if (tempCurve.equals("6")) {
		ivPic.setImageResource(R.drawable.pic2);
		migNum = 2;
		// sbChoosePic.setProgress(2);
	    } else if (tempCurve.equals("8")) {
		ivPic.setImageResource(R.drawable.pic3);
		migNum = 3;
		// sbChoosePic.setProgress(3);
	    } else if (tempCurve.equals("10")) {
		ivPic.setImageResource(R.drawable.pic4);
		migNum = 4;
		// sbChoosePic.setProgress(4);
	    } else if (tempCurve.equals("12")) {
		ivPic.setImageResource(R.drawable.pic5);
		migNum = 5;
		// sbChoosePic.setProgress(5);
	    } else if (tempCurve.equals("15")) {
		ivPic.setImageResource(R.drawable.pic6);
		migNum = 6;
		// sbChoosePic.setProgress(6);
	    } else if (tempCurve.equals("20")) {
		ivPic.setImageResource(R.drawable.pic7);
		migNum = 7;
		// sbChoosePic.setProgress(7);
	    } else if (tempCurve.equals("25")) {
		ivPic.setImageResource(R.drawable.pic8);
		migNum = 8;
		// sbChoosePic.setProgress(8);
	    } else if (tempCurve.equals("30")) {
		ivPic.setImageResource(R.drawable.pic9);
		migNum = 9;
		// sbChoosePic.setProgress(9);
	    }
	}
    };

    // SeekBar�����¶������߳�
    private Runnable mSeekBarRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "refreshTempCurve.php?boilerId=" + boilerId
		    + "&tempCurve=" + tempCurve;
	    MyHttp myHttp = new MyHttp();
	    String retStr = myHttp.httpGet(url);
	    try {
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		Log.v("����TempCurve״��", re_err);
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

    // ����дһ�����һ��������ϲ�Activity�����Ʋ���
    // ������Ļ����
    class TouhListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    // Toast.makeText(getApplicationContext(), "----?",
	    // event.getAction()).show();
	    switch (event.getAction()) { 
	    /*case MotionEvent.ACTION_DOWN: 
		mPointerId = event.getPointerId(0);  
		break;*/
	    case MotionEvent.ACTION_UP:
		Log.d("��ָ̧��", "�����ɹ�");
		// ��ΪonFling����ͻ����ٶ����ƣ���onScroll��û����ָ̧��ļ���������
		// ������onScorllִ����Ϻ��i��λ���������ݿ���²�������д�ڴ˴���
		int trueMigNum = (i + migNum + 100) % 10;
		migNum = trueMigNum;
		i = 0;
		fast = false;
		Log.d("i", "��λ");
		// ����ָ̧��ʱ ���������µ����ݿ�
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();
		break;
	    /*case MotionEvent.ACTION_MOVE:
		verTracker.computeCurrentVelocity(1000, mMaxVelocity);
		float velocityY = verTracker.getYVelocity(mPointerId); 
		Log.d("�ٶ�", ""+verTracker);
		break;*/
	    }
	    /*if (event.getAction() == MotionEvent.ACTION_UP) {
		Log.d("��ָ̧��", "�����ɹ�");
		// ��ΪonFling����ͻ����ٶ����ƣ���onScroll��û����ָ̧��ļ���������
		// ������onScorllִ����Ϻ��i��λ���������ݿ���²�������д�ڴ˴���
		int trueMigNum = (i + migNum + 10) % 10;
		migNum = trueMigNum;
		i = 0;
		fast = false;
		Log.d("i", "��λ");
		// ����ָ̧��ʱ ���������µ����ݿ�
		mSeekBarWork = new Thread(mSeekBarRunnable);
		mSeekBarWork.start();
	    }*/
	    return detector.onTouchEvent(event);
	}

    }

    class GestureListener implements OnGestureListener {

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	    // e1 ��������ʼλ�ã�e2 �����Ľ���λ�ã�velocityX X��ÿһ���ƶ��������ٶȣ���������˼�� velocityY
	    // ���ǣٿ�
	    // ������,��Ϊ�� �������ң���Ϊ����
	    if ((e2.getX() - e1.getX()) > 100) {
		// Ϊʲô��50�� ����������ģ������С����������ģ������ȣ�e2.getX()-e1.getX()<��Ļ��Ⱦͣϣ�
		/*Toast.makeText(getApplicationContext(),
			"���һ���,�������룺" + (e2.getX() - e1.getX()),
			Toast.LENGTH_LONG).show();*/
		// Ҫ����ʲô�¼���������д��OK
		// ���Ҫ��ת������һ��activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		ChooseTempCurveUI.this.finish();

	    }

	   // if ((e2.getY() - e1.getY()) > 40 && Math.abs(velocityY) > 360) {
		/*
		 * Toast.makeText(getApplicationContext(), "���»���,�������룺" +
		 * (e2.getY() - e1.getY()), Toast.LENGTH_LONG).show();
		 */
	//	Log.d("�����ٶ�", "�㹻");
	    //}

	   // if ((e2.getY() - e1.getY()) < -40 && Math.abs(velocityY) > 360) {
		/*
		 * Toast.makeText(getApplicationContext(), "���ϻ���,�������룺" +
		 * (e2.getY() - e1.getY()), Toast.LENGTH_LONG).show();
		 */
	//	Log.d("�����ٶ�", "�㹻");
	   // }

	    /*
	     * if ((e2.getX()-e1.getX())<-50) {
	     * Toast.makeText(getApplicationContext(),
	     * "���󻬶�,�������룺"+(e2.getX()-e1.getX()), Toast.LENGTH_LONG).show();
	     * Intent intent=new Intent(ProgramUI.this, ProgramUI.class);
	     * intent.putExtra("ID", roomId); startActivity(intent); } if
	     * ((e2.getY()-e1.getY())<-50) {
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

	    // * �����¼� һ�г��¼�����Ļ��Ҫ�������¼���������д

	    // Toast.makeText(getApplicationContext(),
	    // "------------> onLongPress", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
		float distanceX, float distanceY) {
	    // TODO Auto-generated method stub

	    // * ����������������������distanceX ��X��·�����ȣ�distanceY ��Y��·�����ȣ�ע�⣺��·��������λ�ƣ�;

	    // Toast.makeText(getApplicationContext(), "------------> onScroll",
	    // Toast.LENGTH_LONG).show();

	    if ((e2.getY() - start) < -50 && fast==false) {
		i++;
		Log.d("����", "" + i);
		start = e2.getY();
		changeMig(i);
	    }
	    if ((e2.getY() - start) > 50 && fast==false) {
		i--;
		Log.d("����", "" + i);
		start = e2.getY();
		changeMig(i);
	    }
	    return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	    // TODO Auto-generated method stub

	    // *

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    // TODO Auto-generated method stub
	    return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
	    // TODO Auto-generated method stub
	    start = e.getY();
	    return true;
	}
    }

    private void changeMig(int i) {
	int trueMigNum = (i + migNum + 100) % 10;
	switch (trueMigNum) {
	case 0:
	    tempCurve = "2";
	    ivPic.setImageResource(R.drawable.pic0);
	    break;
	case 1:
	    tempCurve = "4";
	    ivPic.setImageResource(R.drawable.pic1);
	    break;
	case 2:
	    tempCurve = "6";
	    ivPic.setImageResource(R.drawable.pic2);
	    break;
	case 3:
	    tempCurve = "8";
	    ivPic.setImageResource(R.drawable.pic3);
	    break;
	case 4:
	    tempCurve = "10";
	    ivPic.setImageResource(R.drawable.pic4);
	    break;
	case 5:
	    tempCurve = "12";
	    ivPic.setImageResource(R.drawable.pic5);
	    break;
	case 6:
	    tempCurve = "15";
	    ivPic.setImageResource(R.drawable.pic6);
	    break;
	case 7:
	    tempCurve = "20";
	    ivPic.setImageResource(R.drawable.pic7);
	    break;
	case 8:
	    tempCurve = "25";
	    ivPic.setImageResource(R.drawable.pic8);
	    break;
	case 9:
	    tempCurve = "30";
	    ivPic.setImageResource(R.drawable.pic9);
	    break;
	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    ChooseTempCurveUI.this.finish();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

}
