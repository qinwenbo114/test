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
    // 创建网络连接线程对象
    private Thread mNetWork = null;

    private Handler mHandler = null;

    // handler弹出网络异常
    private Handler mWarmingHandler = null;

    private ListView list;

    private List<HashMap<String, Object>> ListData;
    SimpleAdapter ListAdapter;
    SimpleAdapter ListItemAdapter;

    // 手势监测
    private GestureDetector detector;

    // 加载中对话框
    private AlertDialog ad;
    // 网络连接状况对话框
    private AlertDialog adwarming;

    // get intent uid
    private String uid;

    private String baseURL = "http://192.168.0.97/Android/";// 10.0.2.2虚拟机地址

    // 编辑对话框控件
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

	Bundle bundle = this.getIntent().getExtras();// Bundle存放的是一个键值对，此处接受从CRMActivity中的putExtra()方法传递过来的键值对
	uid = bundle.getString("uid");// 取出键值对中uid键所对应的值，并赋值给uid
	/*Toast.makeText(RoomListUI.this, "uid:" + uid, Toast.LENGTH_SHORT)
		.show();*/

	// 加载对话框，用于数据准备期间，显示“加载中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(RoomListUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading, null);
	ad = new AlertDialog.Builder(RoomListUI.this).setView(dialog).show();

	// 加载对话框，用于显示网络连接状况
	final View dialogWarming = getLayoutInflater().inflate(
		R.layout.dialog_warming, null);
	adwarming = new AlertDialog.Builder(RoomListUI.this).setView(
		dialogWarming).create();

	// 执行网络通信线程
	mNetWork = new Thread(mNetRunnable);
	mNetWork.start();

	// 手势操作
	detector = new GestureDetector(this, new GestureListener());
	LinearLayout layout = (LinearLayout) findViewById(R.id.roomListLayout);
	// LinearLayout itemLayout = (LinearLayout)
	// findViewById(R.id.itemLayout);
	layout.setOnTouchListener(new TouhListener());
	layout.setLongClickable(true);
	// itemLayout.setOnTouchListener(new TouhListener());
	// itemLayout.setLongClickable(false);
	// 此处有问题啊，在item上手势操作无效。。。

	// List短按事件
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
			"获取到的ID：" + getId + "，类型：" + getType,
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
		Log.v("长按Item", getName);
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
					// 开启网络通信，保存房间信息
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
	// 执行网络通信线程
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
	    // 取消加载中对话框
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
			// 该用户尚未添加放假，跳转到是否添加房间页面，finish此页面
			Log.v("用户房间数", "0");
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
				|| (name.equals("未命名") && type.equals("room"))) {
			    name = this.getString(R.string.RoomWithoutName);
			}
			if (name.equals("") && type.equals("boiler"))
			    name = "未命名锅炉";
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
			    hashMap.put("c", "℃");
			if (type.equals("room")) {
			    // hashMap.put("title", "Room temp ");
			    // hashMap.put("ItemImage",
			    // R.drawable.icon);//加入房间图片
			}
			if (type.equals("boiler")) {
			    hashMap.put("title", "当前锅炉温度");
			    // hashMap.put("ItemImage",
			    // R.drawable.icon);//加入锅炉图片
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
	     * "未命名锅炉"; if(temp.equals("null")) temp = "n/a"; HashMap<String,
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

    // 编辑房间信息
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
		    // 更新成功，刷新当前Activity，即重新执行初始化线程。
		    mNetWork = new Thread(mNetRunnable);
		    mNetWork.start();
		} else {
		    // 更新失败
		}
	    } catch (Exception e) {
		e.printStackTrace();
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

    // 下面写一个向右滑动返回上层Activity的手势操作
    // 触摸屏幕监听
    class TouhListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    // Toast.makeText(getApplicationContext(), "----?",
	    // event.getAction()).show();
	    return detector.onTouchEvent(event);
	}

    }

    // 手势滑动监听
    class GestureListener implements OnGestureListener {

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	    // e1 触摸的起始位置，e2 触摸的结束位置，velocityX X轴每一秒移动的像素速度（大概这个意思） velocityY
	    // 就是Ｙ咯
	    // 手势左,上为正 ——，右，下为负正
	    if (e2.getX() - e1.getX() > 50) {
		// 为什么是50？ 这个根据你的模拟器大小来定，看看模拟器宽度，e2.getX()-e1.getX()<屏幕宽度就ＯＫ
		/*Toast.makeText(getApplicationContext(), "向右滑动",
			Toast.LENGTH_LONG).show();*/
		// 要触发什么事件都在这里写就OK
		// 如果要跳转到另外一个activity
		// Intent intent=new Intent(RoomUI.this, RoomListUI.class);
		// intent.putExtra("uid", "1");
		// startActivity(intent);
		RoomListUI.this.finish();
	    }
	    /*
	     * if (Math.abs(e2.getX()-e1.getX())>50) {
	     * Toast.makeText(getApplicationContext(), "向左滑动",
	     * Toast.LENGTH_LONG).show(); } if
	     * (Math.abs(e2.getY()-e1.getY())>50) {
	     * Toast.makeText(getApplicationContext(), "向上滑动",
	     * Toast.LENGTH_LONG).show(); } if (e2.getY()-e1.getY()>50) {
	     * Toast.makeText(getApplicationContext(), "向下滑动",
	     * Toast.LENGTH_LONG).show(); }
	     */
	    return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	    // TODO Auto-generated method stub\
	    /*
	     * 长按事件 一切长事件按屏幕想要触发的事件都在这里写
	     */
	    // Toast.makeText(getApplicationContext(),
	    // "------------> onLongPress", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
		float distanceX, float distanceY) {
	    // TODO Auto-generated method stub
	    /*
	     * 这个函数大概是这样，有误差。distanceX 是X轴路径长度，distanceY 是Y轴路径长度（注意：是路径，不是位移）;
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
	    /*Toast.makeText(getApplicationContext(), "注销菜单项被点击",
		    Toast.LENGTH_LONG).show();*/
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
	case (R.id.muWifiSet):
	    Intent intent3 = new Intent();
	    intent3.setClass(getApplicationContext(), ConfigWifiUI.class);
	    intent3.putExtra("login", true);
	    startActivity(intent3);
	    return true;
	case (R.id.muMoreSet):
	    /*Toast.makeText(getApplicationContext(), "更多菜单项被点击",
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
