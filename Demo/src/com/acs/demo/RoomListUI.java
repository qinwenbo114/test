package com.acs.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RoomListUI extends Activity {
    // �������������̶߳���
    private Thread mNetWork = null;

    private Handler mHandler = null;

    // handler���������쳣
    private Handler mWarmingHandler = null;

    private ListView list;

    private List<HashMap<String, Object>> ListData;
    SimpleAdapter ListAdapter;
    SimpleAdapter ListItemAdapter;

    // ���Ƽ��
    private GestureDetector detector;

    // �����жԻ���
    private AlertDialog ad;
    // ��������״���Ի���
    private AlertDialog adwarming;

    // get intent uid
    private String uid;

    private String baseURL = "http://192.168.0.97/Android/";// 10.0.2.2�������ַ

    // �༭�Ի���ؼ�
    private EditText etRoomName = null;
    private TextView tvEngineMark = null;
    private String roomName = null;
    private String editRoomID = null;
    ActionBar acb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_room_list);
	baseURL = ((BaseURL) getApplicationContext()).getBaseURL();

	mHandler = new Handler();
	mWarmingHandler = new Handler();

	list = (ListView) findViewById(R.id.room_listview);

	Bundle bundle = this.getIntent().getExtras();// Bundle��ŵ���һ����ֵ�ԣ��˴����ܴ�CRMActivity�е�putExtra()�������ݹ����ļ�ֵ��
	uid = bundle.getString("uid");// ȡ����ֵ����uid������Ӧ��ֵ������ֵ��uid
	/*Toast.makeText(RoomListUI.this, "uid:" + uid, Toast.LENGTH_SHORT)
		.show();*/

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������У����Ժ�....��
	LayoutInflater factory = LayoutInflater.from(RoomListUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(RoomListUI.this).setView(dialog).show();

	// ���ضԻ���������ʾ��������״��
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RoomListUI.this).setView(
		dialogWarming).create();

	// ִ������ͨ���߳�
	mNetWork = new Thread(mNetRunnable);
	mNetWork.start();

	// ���Ʋ���
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.roomListLayout);
	// LinearLayout itemLayout = (LinearLayout)
	// findViewById(R.id.itemLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);
	// itemLayout.setOnTouchListener(new TouhListener());
	// itemLayout.setLongClickable(false);
	// �˴������Ⱑ����item�����Ʋ�����Ч������

	// List�̰��¼�
	list.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		    long arg3) {
		// TODO Auto-generated method stub
		// HashMap<String, Object> c = (HashMap<String, Object>)
		// ListAdapter
		// .getItem(arg2);
		HashMap<String, Object> c = (HashMap<String, Object>) ListData
			.get(arg2);
		String getId = ((String) c.get("ID"));
		String getType = ((String) c.get("type"));
		String getName = ((String) c.get("room_name"));
		/*Toast.makeText(RoomListUI.this,
			"��ȡ����ID��" + getId + "�����ͣ�" + getType,
			Toast.LENGTH_SHORT).show();*/
		Intent it = new Intent();
		if (getType.equals("room")) {
		    it.setClass(RoomListUI.this, RoomUI.class);
		} else {
		    it.setClass(RoomListUI.this, BoilerUI.class);
		}
		it.putExtra("ID", getId);
		it.putExtra("NAME", getName);
		startActivity(it);
	    }
	});

	list.setOnItemLongClickListener(new OnItemLongClickListener() {

	    @Override
	    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		    int arg2, long arg3) {
		// TODO Auto-generated method stub
		// HashMap<String, Object> c = (HashMap<String, Object>)
		// ListAdapter
		// .getItem(arg2);
		HashMap<String, Object> c = (HashMap<String, Object>) ListData
			.get(arg2);
		final String getId = ((String) c.get("ID"));
		String getName = ((String) c.get("room_name"));
		String getMark = ((String) c.get("mark"));
		Log.v("����Item", getName);
		Log.v("Mark", getMark);
		LayoutInflater factory = LayoutInflater.from(RoomListUI.this);
		final View editRoom = factory.inflate(
			R.layout.dialog_edit_room, null);
		new AlertDialog.Builder(RoomListUI.this)
			.setView(editRoom)
			.setTitle(R.string.roomInfo)
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
					editRoomID = getId;
					roomName = etRoomName.getText()
						.toString();
					// ��������ͨ�ţ����淿����Ϣ
					mNetWork = new Thread(mEditRoomRunnable);
					mNetWork.start();
				    }
				}).show();

		etRoomName = (EditText) editRoom.findViewById(R.id.etRoomName);
		tvEngineMark = (TextView) editRoom
			.findViewById(R.id.tvEngineMark);
		etRoomName.setText(getName);
		tvEngineMark.setText(getMark);

		return true;
	    }
	});

	try {
	    acb = getActionBar();
	    acb.setTitle(R.string.roomList);
	    acb.setDisplayHomeAsUpEnabled(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	// ִ������ͨ���߳�
	mNetWork = new Thread(mNetRunnable);
	mNetWork.start();
    }

    private Runnable mNetRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    ListData = getListData();

	    ListAdapter = new SimpleAdapter(RoomListUI.this, ListData,
		    R.layout.list_item, new String[] { "room_name", "temp",
			    "ID", "type", "mark", "tempSet", "heating", "c" },
		    new int[] { R.id.tvRoomName, R.id.tvRoomTemp, R.id.tvId,
			    R.id.tvType, R.id.tvMark, R.id.tvTempSet,
			    R.id.tvRun, R.id.tvC });

	    mHandler.post(mViewRunnable);
	    // ȡ�������жԻ���
	    ad.cancel();
	}

    };

    private Runnable mViewRunnable = new Runnable() {
	@Override
	public void run() {
	    list.setAdapter(ListAdapter);
	}
    };

    private List<HashMap<String, Object>> getListData() {
	List<HashMap<String, Object>> Data = new ArrayList<HashMap<String, Object>>();
	String url = baseURL + "getRoomList.php?uid=" + uid;
	// String urlBo = baseURL+"getBoilerList.php?uid="+uid;
	MyHttp myHttp = new MyHttp();
	String retStr = myHttp.httpGet(url);
	// String retStrBo = myHttp.httpGet(urlBo);

	try {
	    JSONArray jsonArray = new JSONArray(retStr);
	    for (int i = 0; i < jsonArray.length(); i++) {
		JSONObject jsonObject = jsonArray.getJSONObject(i);
		{
		    if (jsonObject.getString("errAll").equals("1")) {
			// ���û���δ��ӷż٣���ת���Ƿ���ӷ���ҳ�棬finish��ҳ��
			Log.v("�û�������", "0");
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(),
				AddingRoomUI.class);
			intent.putExtra("uid", uid);
			startActivity(intent);
			RoomListUI.this.finish();
			break;
		    }
		    if (jsonObject.getString("error").equals("0")) {
			String name = jsonObject.getString("name");
			String temp = jsonObject.getString("tempNow");
			String type = jsonObject.getString("type");
			String mark = jsonObject.getString("mark");
			String id = jsonObject.getString("id");
			String tempSet = jsonObject.getString("tempSet");
			String heating = jsonObject.getString("heating");
			if ((name.equals("") && type.equals("room"))
				|| (name.equals("δ����") && type.equals("room"))) {
			    name = this.getString(R.string.RoomWithoutName);
			}
			if (name.equals("") && type.equals("boiler"))
			    name = "δ������¯";
			if (temp.equals("null"))
			    temp = "n/a";
			if (tempSet.equals("null"))
			    tempSet = "n/a";
			else {
			    String ts[] = tempSet.split("\\.");
			    tempSet = ts[0];
			}
			if (heating.equals("null") || heating.equals("0"))
			    heating = this.getString(R.string.stopped);
			else
			    heating = this.getString(R.string.running);
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("room_name", name);
			hashMap.put("temp", temp);
			hashMap.put("tempSet", tempSet);
			hashMap.put("heating", heating);
			if (tempSet.equals("n/a"))
			    hashMap.put("c", "");
			else
			    hashMap.put("c", "��");
			if (type.equals("room")) {
			    // hashMap.put("title", "Room temp ");
			    // hashMap.put("ItemImage",
			    // R.drawable.icon);//���뷿��ͼƬ
			}
			if (type.equals("boiler")) {
			    hashMap.put("title", "��ǰ��¯�¶�");
			    // hashMap.put("ItemImage",
			    // R.drawable.icon);//�����¯ͼƬ
			}
			hashMap.put("ID", id);
			hashMap.put("mark", mark);
			hashMap.put("type", type);
			Data.add(hashMap);
		    }
		}
	    }
	    /*
	     * JSONObject jsonObject = new JSONObject(retStrBo);
	     * if(jsonObject.getString("error").equals("0")){ String name =
	     * jsonObject.getString("bname"); String temp =
	     * jsonObject.getString("tempNow"); if(name.equals("")) name =
	     * "δ������¯"; if(temp.equals("null")) temp = "n/a"; HashMap<String,
	     * Object> hashMap = new HashMap<String, Object>();
	     * hashMap.put("room_name", name); hashMap.put("temp", temp);
	     * Data.add(hashMap); }
	     */

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    mWarmingHandler.post(mWarmingRunnable);
	} catch (Exception e) {
	    e.printStackTrace();
	    mWarmingHandler.post(mWarmingRunnable);
	}

	return Data;

    }

    // �༭������Ϣ
    private Runnable mEditRoomRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    String url = baseURL + "editRoom.php?roomId=" + editRoomID
		    + "&roomName=" + roomName;
	    MyHttp myHttp = new MyHttp();
	    try {
		String retStr = myHttp.httpGet(url);
		JSONObject jsonObject = new JSONObject(retStr);
		String re_err = jsonObject.getString("error");
		if (re_err.equals("0")) {
		    // ���³ɹ���ˢ�µ�ǰActivity��������ִ�г�ʼ���̡߳�
		    mNetWork = new Thread(mNetRunnable);
		    mNetWork.start();
		} else {
		    // ����ʧ��
		}
	    } catch (Exception e) {
		e.printStackTrace();
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
		/*Toast.makeText(getApplicationContext(), "���һ���",
			Toast.LENGTH_LONG).show();*/
		// Ҫ����ʲô�¼���������д��OK
		// ���Ҫ��ת������һ��activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		RoomListUI.this.finish();
	    }
	    /*
	     * if (Math.abs(e2.getX()-e1.getX())>50) {
	     * Toast.makeText(getApplicationContext(), "���󻬶�",
	     * Toast.LENGTH_LONG).show(); } if
	     * (Math.abs(e2.getY()-e1.getY())>50) {
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
	inflater.inflate(R.menu.roomlist_actionbar, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    RoomListUI.this.finish();
	    return true;
	case (R.id.muLogOut):
	    /*Toast.makeText(getApplicationContext(), "ע���˵�����",
		    Toast.LENGTH_LONG).show();*/
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
	    /*Toast.makeText(getApplicationContext(), "����˵�����",
		    Toast.LENGTH_LONG).show();*/
	    return true;
	case (R.id.muRefresh):
	    mNetWork = new Thread(mNetRunnable);
	    mNetWork.start();
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
