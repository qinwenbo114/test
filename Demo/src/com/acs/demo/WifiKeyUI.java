package com.acs.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class WifiKeyUI extends Activity {

    private TextView tvSSID = null;
    private EditText etKey = null;
    private RadioGroup rgDS = null;
    private RadioButton rbDHCP = null;
    private RadioButton rbStatic = null;
    private EditText etIP = null;
    private EditText etMask = null;
    private EditText etGateway = null;
    private Button btApply = null;
    private LinearLayout llStatic = null;
    private Spinner spKeyMode = null;// 加密类型
    private Spinner spKeyStyle = null;// 加密算法

    private ArrayAdapter<?> adapterKeyMode;// 下拉列表适配器
    private ArrayAdapter<?> adapterKeyStyle;// 下拉列表适配器

    private String SSID = null;
    private String CERT = null;
    private String ENER = null;
    private String key = null;
    private String IP = null;
    private String mask = null;
    private String gateway = null;
    private String uid = null;
    private Boolean login;
    private Boolean exitKey;// 标识该热点是否有密码

    private Thread mSendWork = null;
    private Thread mReceiveWork = null;
    // 单播
    private DatagramSocket ds;

    // 当前单选所在
    private int rb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_wifi_key);
	Bundle bundle = this.getIntent().getExtras();
	SSID = bundle.getString("SSID");
	CERT = bundle.getString("CERT");
	ENER = bundle.getString("ENER");
	login = bundle.getBoolean("login");

	// 如果CERT为无密码，那么直接调用配置WIFI的模块
	if (CERT.equals("ESS")) {
	    exitKey = false;
	    mSendWork = new Thread(mATRunnable);
	    mSendWork.start();
	} else {
	    exitKey = true;
	}
	// CERT为认证模式，收到的信息可能是WPAPSK、WPAPSKWPA2PSK、WPA2PSK、OPEN、SHARED(已过期)
	// CERT为认证模式，收到的信息可能是WPAPSK、WPA2PSK、WEP
	// 对于WPAPSKWPA2PSK，我们将其划为WPA2PSK(已过期)
	if (CERT.equals("WPAPSKWPA2PSK"))
	    CERT = "WPA2PSK";
	// 对于WEP认证模式，分为OPENWEP和SHAREDWEP，但手机只能搜到WEP，我们将其默认值设为OPEN
	if (CERT.equals("WEP")) {
	    CERT = "OPEN";
	    // 对于WEP的加密算法，可分为WEP-A和WEP-H,但手机搜到的两者皆为WEP，默认将其设为WEP-A
	    ENER = "WEP-A";
	}

	// ENER为加密算法，收到的信息可能是AES、TKIPAES、TKIP、WEP、NONE(已过期)
	// 对于TKIPAES，我们将其划为AES
	if (ENER.equals("TKIPAES"))
	    ENER = "AES";

	tvSSID = (TextView) findViewById(R.id.tvSSID);
	etKey = (EditText) findViewById(R.id.etKey);
	rgDS = (RadioGroup) findViewById(R.id.radioGroupDS);
	rbDHCP = (RadioButton) findViewById(R.id.rbDHCP);
	rbStatic = (RadioButton) findViewById(R.id.rbStatic);
	etIP = (EditText) findViewById(R.id.etIP);
	etMask = (EditText) findViewById(R.id.etMask);
	etGateway = (EditText) findViewById(R.id.etGateway);
	btApply = (Button) findViewById(R.id.btApply);
	llStatic = (LinearLayout) findViewById(R.id.llStatic);
	spKeyMode = (Spinner) findViewById(R.id.spinnerKeyMode);
	spKeyStyle = (Spinner) findViewById(R.id.spinnerKeyStyle);

	// 设置标题
	tvSSID.setText(SSID);

	// 设置Spinner适配器，对于不同的加密方式（CERT），应该加载不同的适配器
	adapterKeyMode = ArrayAdapter.createFromResource(WifiKeyUI.this,
		R.array.keyMode, android.R.layout.simple_spinner_item);
	if (CERT.equals("OPEN")) {
	    adapterKeyStyle = ArrayAdapter.createFromResource(WifiKeyUI.this,
		    R.array.keyStyleWep, android.R.layout.simple_spinner_item);
	} else {
	    adapterKeyStyle = ArrayAdapter.createFromResource(WifiKeyUI.this,
		    R.array.keyStyleWpa, android.R.layout.simple_spinner_item);
	}

	// 设置下拉列表的风格
	adapterKeyMode
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	adapterKeyStyle
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	// 将adapter 添加到spinner中
	spKeyMode.setAdapter(adapterKeyMode);
	spKeyStyle.setAdapter(adapterKeyStyle);

	// 设置默认值
	spKeyMode.setVisibility(View.VISIBLE);
	spKeyStyle.setVisibility(View.VISIBLE);
	if (CERT.equals("WPAPSK")) {
	    spKeyMode.setSelection(2);
	} else if (CERT.equals("WPA2PSK")) {
	    spKeyMode.setSelection(3);
	} else {
	    spKeyMode.setSelection(0);
	}
	if (ENER.equals("TKIP")) {
	    spKeyStyle.setSelection(1);
	} else {
	    spKeyStyle.setSelection(0);
	}

	// Spinner事件监听
	spKeyMode
		.setOnItemSelectedListener(new SpinnerKeyModeXMLSelectedListener());

	rgDS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		int rbID = group.getCheckedRadioButtonId();
		if (rbID == R.id.rbDHCP) {
		    llStatic.setVisibility(View.GONE);
		    rb = 0;
		} else if (rbID == R.id.rbStatic) {
		    llStatic.setVisibility(View.VISIBLE);
		    rb = 1;
		}

	    }
	});

	rbDHCP.setChecked(true);// 此处若变，int rb也要变

	btApply.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		key = etKey.getText().toString();
		if (rb == 1) {
		    IP = etIP.getText().toString();
		    mask = etMask.getText().toString();
		    gateway = etGateway.getText().toString();
		}
		Log.d("key", key);
		if (key.equals("")) {
		    // 不能为空
		} else {
		    // UDP通信设密码啊
		    mSendWork = new Thread(mATRunnable);
		    mSendWork.start();

		}
	    }
	});
    }

    // 使用XML形式操作 SpinnerKeyMode
    class SpinnerKeyModeXMLSelectedListener implements OnItemSelectedListener {
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	    //Toast.makeText(getApplicationContext(), arg2 + "",
		//    Toast.LENGTH_LONG).show();
	    if (arg2 == 0 || arg2 == 1) {
		// 如果选中OPENWEP认证模式
		//Toast.makeText(getApplicationContext(), "切换到WEP",
			//Toast.LENGTH_LONG).show();
		adapterKeyStyle = ArrayAdapter.createFromResource(
			WifiKeyUI.this, R.array.keyStyleWep,
			android.R.layout.simple_spinner_item);
		if (arg2 == 0) {
		    // 更改CERT变量为OPEN
		    CERT = "OPEN";
		} else if (arg2 == 1) {
		    // 更改CERT变量为SHARED
		    CERT = "SHARED";
		}
	    } else {
		adapterKeyStyle = ArrayAdapter.createFromResource(
			WifiKeyUI.this, R.array.keyStyleWpa,
			android.R.layout.simple_spinner_item);
		if (arg2 == 2) {
		    CERT = "WPAPSK";
		} else {
		    CERT = "WPA2PSK";
		}
	    }

	    adapterKeyStyle
		    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spKeyStyle.setAdapter(adapterKeyStyle);
	    // view.setText("你使用什么样的手机："+adapter.getItem(arg2));
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

    }

    // 使用XML形式操作 SpinnerKeyStyle
    class SpinnerKeyStyleXMLSelectedListener implements OnItemSelectedListener {
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	    //Toast.makeText(getApplicationContext(), arg2 + "",
		  //  Toast.LENGTH_LONG).show();
	    if (arg2 == 0) {
		// 如果选中WEP认证模式,加密算法改为WEP-A
		if (CERT == "OPEN" || CERT == "SHARED") {
		    ENER = "WEP-A";
		}
		// 如果选中WPA认证模式，加密算法改为AES
		else {
		    ENER = "AES";
		}
	    } else {
		// 如果选中WEP认证模式，加密算法改为WEP-H
		if (CERT == "OPEN" || CERT == "SHARED") {
		    ENER = "WEP-H";
		} else {
		    ENER = "TKIP";
		}
	    }

	    adapterKeyStyle
		    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spKeyStyle.setAdapter(adapterKeyStyle);
	    // view.setText("你使用什么样的手机："+adapter.getItem(arg2));
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

    }

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
	    DatagramPacket dataPacket2 = null;
	    DatagramPacket dataPacket3 = null;
	    DatagramPacket dataPacket4 = null;
	    DatagramPacket dataPacket5 = null;
	    DatagramPacket dataPacket6 = null;
	    try {
		// 设置关联SSID
		byte[] data = ("AT+WSSSID=" + SSID + "\n").getBytes();
		Log.d("AT+WSSSID=", SSID);
		InetAddress address = InetAddress.getByName("10.10.100.254");
		dataPacket = new DatagramPacket(data, data.length, address,
			48899);
		ds.send(dataPacket);

		// 如果有密码设置密码
		byte[] dataKey;
		if (exitKey == true) {
		    dataKey = ("AT+WSKEY=" + CERT + "," + ENER + "," + key + "\n")
			    .getBytes();
		    Log.d("AT+WSKEY=", CERT + "," + ENER + "," + key);
		} else {
		    dataKey = ("AT+WSKEY=OPEN,NONE\n").getBytes();
		}
		dataPacket2 = new DatagramPacket(dataKey, dataKey.length,
			address, 48899);
		ds.send(dataPacket2);

		// 设置DHCP/STATIC
		if (rb == 0) {
		    byte[] dataDS = ("AT+WANN=DHCP\n").getBytes();
		    dataPacket4 = new DatagramPacket(dataDS, dataDS.length,
			    address, 48899);
		    ds.send(dataPacket4);
		} else if (rb == 1) {
		    // byte[] dataDS = ("AT+WANN=static," + IP + "," + mask +
		    // ","
		    // + "gateway" + "\n").getBytes();
		    String dsS = "AT+WANN=static," + IP + "," + mask + ","
			    + gateway + "\n";
		    byte[] dataDS = (dsS).getBytes();
		    // byte[] dataDS =
		    // ("AT+WANN=static,192.168.1.110,255.255.255.0,192.168.1.1\n").getBytes();
		    Log.d("IP=", IP);
		    Log.d("mask=", mask);
		    Log.d("gateway=", gateway);
		    Log.d("dsS=", dsS);
		    dataPacket4 = new DatagramPacket(dataDS, dataDS.length,
			    address, 48899);
		    ds.send(dataPacket4);
		}

		// 查询当前SSID
		byte[] data3 = ("AT+WSSSID\n").getBytes();
		Log.d("AT+WSSSID", "");
		dataPacket3 = new DatagramPacket(data3, data3.length, address,
			48899);
		ds.send(dataPacket3);

		// 查询当前WANN
		byte[] data5 = ("AT+WANN\n").getBytes();
		Log.d("AT+WANN", "");
		dataPacket5 = new DatagramPacket(data5, data5.length, address,
			48899);
		ds.send(dataPacket5);

		// 重启wifi模块
		byte[] data6 = ("AT+Z\n").getBytes();
		Log.d("AT+Z", "");
		dataPacket6 = new DatagramPacket(data6, data6.length, address,
			48899);
		ds.send(dataPacket6);

		ds.close();
		mReceiveWork = new Thread(mATReceiveRunnable);
		mReceiveWork.start();

		// 跳转到ChooseLogo页面
		Intent it = new Intent();
		if (login == true) {
		    it.setClass(WifiKeyUI.this, ChooseLogoUI.class);
		    it.putExtra("Logout", "overwifi");
		} else {
		    it.setClass(WifiKeyUI.this, LoginUI.class);
		}
		startActivity(it);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    };

    private Runnable mATReceiveRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    int serverPort = 48899;
	    byte[] message = new byte[1500];
	    DatagramPacket p = new DatagramPacket(message, message.length);
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
		while (mark) {
		    s.receive(p);
		    String text = new String(message, 0, p.getLength());
		    String newMsg = text;
		    Log.d("Udp tutorial", "message:" + text);
		    if (newMsg.equals("\n")) {
			mark = false;
		    } else {
		    }
		}
		s.close();
		s = null;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    };

}
