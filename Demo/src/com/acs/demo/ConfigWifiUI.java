package com.acs.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConfigWifiUI extends Activity {

    private String uid = null;
    private Button btSet = null;
    private Button btSkip = null;
    private boolean login;

    /* ���͹㲥�˵�socket */
    private MulticastSocket ms;
    // ����
    private DatagramSocket ds;
    // �������Ӷ�ʱ��
    private Timer timer = null;
    private Thread mReceiveWork = null;
    private Thread mSendWork = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_config_wifi);

	Bundle bundle = this.getIntent().getExtras();
	try {
	    login = bundle.getBoolean("login");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	btSet = (Button) findViewById(R.id.btSet);
	btSkip = (Button) findViewById(R.id.btSkip);

	btSet.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		mSendWork = new Thread(mServerRunnable);
		mSendWork.start();

		try {
		    timer = new Timer();
		    timer.schedule(keepConTask, 45000, 45000);// ������45���ÿ��45��ˢ��һ��
		} catch (Exception e) {
		}

		Intent it = new Intent();
		it.setClass(ConfigWifiUI.this, WifiListPhoneUI.class);
		it.putExtra("login", login);
		startActivity(it);
	    }
	});

	btSkip.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		/*
		 * Intent it = new Intent(); it.setClass(ConfigWifiUI.this,
		 * ChooseLogoUI.class); it.putExtra("uid", uid);
		 * it.putExtra("Logout", "false"); startActivity(it);
		 */
		ConfigWifiUI.this.finish();
	    }
	});
    }

    // ��ʼ������
    private Runnable mServerRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    try {
		ms = new MulticastSocket();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }

	    // ���͵����ݰ��������ڵ����е�ַ�������յ������ݰ�
	    DatagramPacket dataPacket = null;
	    DatagramPacket dataPacket2 = null;
	    try {
		ms.setTimeToLive(4);
		// ��������IP���������д��̬��ȡ��IP����ַ�ŵ����ݰ����ʵserver�˽��յ����ݰ���Ҳ�ܻ�ȡ����������IP��
		byte[] data = "HF-A11ASSISTHREAD".getBytes();
		// 255.255.255.255
		InetAddress address = InetAddress.getByName("255.255.255.255");
		// ����ط���������жϸõ�ַ�ǲ��ǹ㲥���͵ĵ�ַ
		// System.out.println(address.isMulticastAddress());
		dataPacket = new DatagramPacket(data, data.length, address,
			48899);
		ms.send(dataPacket);

		byte[] data2 = "+ok".getBytes();
		InetAddress address2 = InetAddress.getByName("10.10.100.254");
		dataPacket2 = new DatagramPacket(data2, data2.length, address2,
			48899);
		ms.send(dataPacket2);
		ms.close();
		mReceiveWork = new Thread(mReceiveRunnable);
		mReceiveWork.start();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    };

    // ��ʼ������
    private Runnable mReceiveRunnable = new Runnable() {

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
		s.receive(p);
		String text = new String(message, 0, p.getLength());
		// String newMsg = text;
		// String[] msgArray = newMsg.split(",");
		// IP = msgArray[0];
		Log.d("Udp tutorial", "message:" + text);
		s.close();
		s = null;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    };

    // ���ӱ���
    private TimerTask keepConTask = new TimerTask() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    /* ����socketʵ�� */
	    try {
		ds = new DatagramSocket();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }

	    // ���͵����ݰ��������ڵ����е�ַ�������յ������ݰ�
	    DatagramPacket dataPacket = null;
	    try {
		// 10.10.100.254Ϊwifiģ��̶�IP
		InetAddress address = InetAddress.getByName("10.10.100.254");
		// ����ط���������жϸõ�ַ�ǲ��ǹ㲥���͵ĵ�ַ
		// System.out.println(address.isMulticastAddress());

		byte[] data = "AT+W\n".getBytes();
		dataPacket = new DatagramPacket(data, data.length, address,
			48899);
		ds.send(dataPacket);
		Log.d("��������", "��������");
		ds.close();
		ds = null;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    };

    // Activity�ر�ʱ��ִ�����²������ͷ���Դ
    public void onDestroy() {
	super.onDestroy();
	try {// ��Ϊtimer��һ�����������Դ˴���trycatch
	    if (timer != null) {
		timer.cancel();
		timer = null;
	    }
	} catch (Exception e) {
	}
    }

}
