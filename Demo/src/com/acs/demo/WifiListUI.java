package com.acs.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class WifiListUI extends Activity {

    private Thread mReceiveWork = null;
    private Thread mATWork = null;
    private Handler mUIHandler = null;
    // 单播
    private DatagramSocket ds;
    private List<HashMap<String, Object>> ListData;
    private SimpleAdapter ListAdapter;

    private ListView list = null;

    // 加载中对话框
    private AlertDialog ad;
    private String uid = null;
    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_wifi_list);

	Bundle bundle = this.getIntent().getExtras();
	login = bundle.getBoolean("login");

	mUIHandler = new Handler();

	mATWork = new Thread(mATRunnable);
	mATWork.start();

	list = (ListView) findViewById(R.id.wifi_listview);

	// 加载对话框，用于数据准备期间，显示“搜索热点中，请稍后....”
	LayoutInflater factory = LayoutInflater.from(WifiListUI.this);
	final View dialog = factory.inflate(R.layout.dialog_loading_wifi, null);
	ad = new AlertDialog.Builder(WifiListUI.this).setView(dialog).show();

	list.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		    long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, Object> c = (HashMap<String, Object>) ListData
			.get(arg2);
		String SSID = ((String) c.get("SSID"));
		String CERT = ((String) c.get("CERT"));
		String ENER = ((String) c.get("ENER"));
		Intent it = new Intent();
		it.setClass(WifiListUI.this, WifiKeyUI.class);
		it.putExtra("SSID", SSID);
		it.putExtra("CERT", CERT);
		it.putExtra("ENER", ENER);
		it.putExtra("login", login);
		startActivity(it);
	    }
	});
    }

    // 查询热点
    private Runnable mATRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    try {
		ds = new DatagramSocket();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }

	    // 发送的数据包，局网内的所有地址都可以收到该数据包
	    DatagramPacket dataPacket = null;
	    try {
		// 将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
		byte[] data = ("AT+WSCAN\n").getBytes();
		// 255.255.255.255
		InetAddress address = InetAddress.getByName("10.10.100.254");
		// 这个地方可以输出判断该地址是不是广播类型的地址
		// System.out.println(address.isMulticastAddress());
		dataPacket = new DatagramPacket(data, data.length, address,
			48899);
		ds.send(dataPacket);
		ds.close();
		mReceiveWork = new Thread(mATReceiveRunnable);
		mReceiveWork.start();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    };

    // 查询热点反馈信息
    private Runnable mATReceiveRunnable = new Runnable() {

	@Override
	public void run() {
	    ListData = new ArrayList<HashMap<String, Object>>();
	    // TODO Auto-generated method stub
	    int serverPort = 48899;
	    byte[] message = new byte[1500];
	    DatagramSocket s = null;
	    try {
		s = new DatagramSocket(null);
		s.setReuseAddress(true);
		s.bind(new InetSocketAddress(serverPort));
	    } catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    try {
		boolean mark = true;
		int i = 0;
		while (mark) {
		    i++;
		    DatagramPacket p = new DatagramPacket(message, message.length);
		    s.receive(p);
		    String text = new String(message, 0, p.getLength());
		    String newMsg = text;
		    Log.d("Udp tutorial", "message:" + text);
		    if (newMsg.equals("\n")) {//如果接收到的行信息只有一个回车换行符，停止接收
			mark = false;
		    } else {
			try {
			    if (i > 2) {
				String[] msgArray = newMsg.split(",");
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				String[] CE = msgArray[3].split("/");
				hashMap.put("SSID", msgArray[1]);
				hashMap.put("CERT", CE[0]);
				hashMap.put("ENER", CE[1]);
				Log.d("SIGN", msgArray[4]);
				int sign = Integer.parseInt(msgArray[4].trim());
				if (sign <= 33) {
				    hashMap.put("SIGNMIG", R.drawable.wifilow);
				} else if (sign <= 66) {
				    hashMap.put("SIGNMIG", R.drawable.wifimid);
				} else {
				    hashMap.put("SIGNMIG", R.drawable.wififull);
				}
				//hashMap.put("SIGN", msgArray[4]);
				ListData.add(hashMap);
			    }
			} catch (Exception e) {

			    e.printStackTrace();
			}
		    }
		}
		Log.d("Udp tutorial", "循环跳出，请执行UIHandler");
		mUIHandler.post(mUIRunnable);
		// 循环跳出后为什么走不到这里？？？？！！！
		// 解决，应该在收到“\n”时跳出循环而不是空字符串
		s.close();
		s = null;
		// 加载中对话框取消
		ad.cancel();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    };

    // 热点信息加载入UI
    private Runnable mUIRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    // ListData适配到listView
	    ListAdapter = new SimpleAdapter(WifiListUI.this, ListData,
		    R.layout.wifi_item, new String[] { "SSID", "CERT", "ENER",
			    "SIGNMIG" }, new int[] { R.id.tvSSID, R.id.tvCERT,
			    R.id.tvENER, R.id.ivSIGN});
	    list.setAdapter(ListAdapter);
	}
    };

}
