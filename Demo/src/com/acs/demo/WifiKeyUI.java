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
    private Spinner spKeyMode = null;// ��������
    private Spinner spKeyStyle = null;// �����㷨

    private ArrayAdapter<?> adapterKeyMode;// �����б�������
    private ArrayAdapter<?> adapterKeyStyle;// �����б�������

    private String SSID = null;
    private String CERT = null;
    private String ENER = null;
    private String key = null;
    private String IP = null;
    private String mask = null;
    private String gateway = null;
    private String uid = null;
    private Boolean login;
    private Boolean exitKey;// ��ʶ���ȵ��Ƿ�������

    private Thread mSendWork = null;
    private Thread mReceiveWork = null;
    // ����
    private DatagramSocket ds;

    // ��ǰ��ѡ����
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

	// ���CERTΪ�����룬��ôֱ�ӵ�������WIFI��ģ��
	if (CERT.equals("ESS")) {
	    exitKey = false;
	    mSendWork = new Thread(mATRunnable);
	    mSendWork.start();
	} else {
	    exitKey = true;
	}
	// CERTΪ��֤ģʽ���յ�����Ϣ������WPAPSK��WPAPSKWPA2PSK��WPA2PSK��OPEN��SHARED(�ѹ���)
	// CERTΪ��֤ģʽ���յ�����Ϣ������WPAPSK��WPA2PSK��WEP
	// ����WPAPSKWPA2PSK�����ǽ��仮ΪWPA2PSK(�ѹ���)
	if (CERT.equals("WPAPSKWPA2PSK"))
	    CERT = "WPA2PSK";
	// ����WEP��֤ģʽ����ΪOPENWEP��SHAREDWEP�����ֻ�ֻ���ѵ�WEP�����ǽ���Ĭ��ֵ��ΪOPEN
	if (CERT.equals("WEP")) {
	    CERT = "OPEN";
	    // ����WEP�ļ����㷨���ɷ�ΪWEP-A��WEP-H,���ֻ��ѵ������߽�ΪWEP��Ĭ�Ͻ�����ΪWEP-A
	    ENER = "WEP-A";
	}

	// ENERΪ�����㷨���յ�����Ϣ������AES��TKIPAES��TKIP��WEP��NONE(�ѹ���)
	// ����TKIPAES�����ǽ��仮ΪAES
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

	// ���ñ���
	tvSSID.setText(SSID);

	// ����Spinner�����������ڲ�ͬ�ļ��ܷ�ʽ��CERT����Ӧ�ü��ز�ͬ��������
	adapterKeyMode = ArrayAdapter.createFromResource(WifiKeyUI.this,
		R.array.keyMode, android.R.layout.simple_spinner_item);
	if (CERT.equals("OPEN")) {
	    adapterKeyStyle = ArrayAdapter.createFromResource(WifiKeyUI.this,
		    R.array.keyStyleWep, android.R.layout.simple_spinner_item);
	} else {
	    adapterKeyStyle = ArrayAdapter.createFromResource(WifiKeyUI.this,
		    R.array.keyStyleWpa, android.R.layout.simple_spinner_item);
	}

	// ���������б�ķ��
	adapterKeyMode
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	adapterKeyStyle
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	// ��adapter ��ӵ�spinner��
	spKeyMode.setAdapter(adapterKeyMode);
	spKeyStyle.setAdapter(adapterKeyStyle);

	// ����Ĭ��ֵ
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

	// Spinner�¼�����
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

	rbDHCP.setChecked(true);// �˴����䣬int rbҲҪ��

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
		    // ����Ϊ��
		} else {
		    // UDPͨ�������밡
		    mSendWork = new Thread(mATRunnable);
		    mSendWork.start();

		}
	    }
	});
    }

    // ʹ��XML��ʽ���� SpinnerKeyMode
    class SpinnerKeyModeXMLSelectedListener implements OnItemSelectedListener {
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	    //Toast.makeText(getApplicationContext(), arg2 + "",
		//    Toast.LENGTH_LONG).show();
	    if (arg2 == 0 || arg2 == 1) {
		// ���ѡ��OPENWEP��֤ģʽ
		//Toast.makeText(getApplicationContext(), "�л���WEP",
			//Toast.LENGTH_LONG).show();
		adapterKeyStyle = ArrayAdapter.createFromResource(
			WifiKeyUI.this, R.array.keyStyleWep,
			android.R.layout.simple_spinner_item);
		if (arg2 == 0) {
		    // ����CERT����ΪOPEN
		    CERT = "OPEN";
		} else if (arg2 == 1) {
		    // ����CERT����ΪSHARED
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
	    // view.setText("��ʹ��ʲô�����ֻ���"+adapter.getItem(arg2));
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

    }

    // ʹ��XML��ʽ���� SpinnerKeyStyle
    class SpinnerKeyStyleXMLSelectedListener implements OnItemSelectedListener {
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	    //Toast.makeText(getApplicationContext(), arg2 + "",
		  //  Toast.LENGTH_LONG).show();
	    if (arg2 == 0) {
		// ���ѡ��WEP��֤ģʽ,�����㷨��ΪWEP-A
		if (CERT == "OPEN" || CERT == "SHARED") {
		    ENER = "WEP-A";
		}
		// ���ѡ��WPA��֤ģʽ�������㷨��ΪAES
		else {
		    ENER = "AES";
		}
	    } else {
		// ���ѡ��WEP��֤ģʽ�������㷨��ΪWEP-H
		if (CERT == "OPEN" || CERT == "SHARED") {
		    ENER = "WEP-H";
		} else {
		    ENER = "TKIP";
		}
	    }

	    adapterKeyStyle
		    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spKeyStyle.setAdapter(adapterKeyStyle);
	    // view.setText("��ʹ��ʲô�����ֻ���"+adapter.getItem(arg2));
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

	    // ���͵����ݰ��������ڵ����е�ַ�������յ������ݰ�
	    DatagramPacket dataPacket = null;
	    DatagramPacket dataPacket2 = null;
	    DatagramPacket dataPacket3 = null;
	    DatagramPacket dataPacket4 = null;
	    DatagramPacket dataPacket5 = null;
	    DatagramPacket dataPacket6 = null;
	    try {
		// ���ù���SSID
		byte[] data = ("AT+WSSSID=" + SSID + "\n").getBytes();
		Log.d("AT+WSSSID=", SSID);
		InetAddress address = InetAddress.getByName("10.10.100.254");
		dataPacket = new DatagramPacket(data, data.length, address,
			48899);
		ds.send(dataPacket);

		// �����������������
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

		// ����DHCP/STATIC
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

		// ��ѯ��ǰSSID
		byte[] data3 = ("AT+WSSSID\n").getBytes();
		Log.d("AT+WSSSID", "");
		dataPacket3 = new DatagramPacket(data3, data3.length, address,
			48899);
		ds.send(dataPacket3);

		// ��ѯ��ǰWANN
		byte[] data5 = ("AT+WANN\n").getBytes();
		Log.d("AT+WANN", "");
		dataPacket5 = new DatagramPacket(data5, data5.length, address,
			48899);
		ds.send(dataPacket5);

		// ����wifiģ��
		byte[] data6 = ("AT+Z\n").getBytes();
		Log.d("AT+Z", "");
		dataPacket6 = new DatagramPacket(data6, data6.length, address,
			48899);
		ds.send(dataPacket6);

		ds.close();
		mReceiveWork = new Thread(mATReceiveRunnable);
		mReceiveWork.start();

		// ��ת��ChooseLogoҳ��
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
