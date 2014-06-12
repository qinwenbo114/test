package com.acs.demo;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WifiListPhoneUI extends Activity {

    private WifiManager wifi;

    private ListView listView;
    private List<ScanResult> results;
    private boolean login;

    private String SSID = null;
    private String CERT = null;
    private String ENER = null;

    // �����жԻ���
    private AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_wifi_list);

	Bundle bundle = this.getIntent().getExtras();
	login = bundle.getBoolean("login");

	// ���ضԻ�����������׼���ڼ䣬��ʾ�������ȵ��У����Ժ�....��
	LayoutInflater factoryAlert = LayoutInflater.from(WifiListPhoneUI.this);
	final View dialog = factoryAlert.inflate(R.layout.dialog_loading_wifi,
		null);
	ad = new AlertDialog.Builder(WifiListPhoneUI.this).setView(dialog)
		.show();

	listView = (ListView) findViewById(R.id.wifi_listview);
	wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
	    // ����ֻ�û�п���WIFI
	    // ���������洰��
	    LayoutInflater factory = LayoutInflater.from(WifiListPhoneUI.this);
	    final View wifiDis = factory.inflate(R.layout.dialog_wifi_disabled,
		    null);
	    new AlertDialog.Builder(WifiListPhoneUI.this)
		    .setView(wifiDis)
		    .setTitle("����WIFI")
		    .setNegativeButton("ȡ��",
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub

				}
			    })
		    .setPositiveButton("ȷ��",
			    new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				    // TODO Auto-generated method stub
				    // ����WIFI
				    wifi.setWifiEnabled(true);
				}
			    }).show();
	}

	// Register a broadcast receiver that listens for scan results.
	registerReceiver(new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
		results = wifi.getScanResults();
		if (results == null) {
		    Toast.makeText(WifiListPhoneUI.this, "wifi is not open!",
			    Toast.LENGTH_LONG).show();
		} else {
		    listView.setAdapter(new MyWifiAdapter(WifiListPhoneUI.this,
			    results));

		    // �����жԻ���ȡ��
		    ad.cancel();
		}
	    }
	}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	// Initiate a scan.
	wifi.startScan();

	listView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		    long arg3) {
		// TODO Auto-generated method stub
		// SSID
		// Toast.makeText(WifiListPhoneUI.this, results.get(arg2).SSID,
		// Toast.LENGTH_LONG).show();
		SSID = results.get(arg2).SSID;
		// ��֤ģʽCERT,�����㷨ENER
		String capa = results.get(arg2).capabilities;
		// �ҵ�capa�еĵ�һ��"]"���ֵ�λ��
		int indexOfEnd = capa.indexOf("]");
		// ��ȡ"["��"]"֮�������
		String capaCut = capa.substring(1, indexOfEnd);
		// �ж��Ƿ�ΪOPEN��WEP���ܷ�ʽ
		if (capaCut.equals("ESS")) {
		    // OPEN����
		   // Toast.makeText(WifiListPhoneUI.this, "δ����",
			  //  Toast.LENGTH_LONG).show();
		    CERT = "ESS";
		    ENER = "";
		} else if (capaCut.equals("WEP")) {
		    // WEP����
		    CERT = "WEP";
		    ENER = "";
		} else {
		    // �������ͣ��������
		    String[] capaArray = capaCut.split("-");
		    if (capaArray[0].equals("WPA")) {
			// ����WPAPSK��֤ģʽ
			//Toast.makeText(WifiListPhoneUI.this, "����WPAPSK��֤ģʽ",
				//Toast.LENGTH_LONG).show();
			CERT = "WPAPSK";
		    } else if (capaArray[0].equals("WPA2")) {
			// ����WPA2PSK��֤ģʽ
			//Toast.makeText(WifiListPhoneUI.this, "����WPA2PSK��֤ģʽ",
				//Toast.LENGTH_LONG).show();
			CERT = "WPA2PSK";
		    }
		    if (capaArray[2].equals("CCMP")) {
			// ����AES�����㷨
			//Toast.makeText(WifiListPhoneUI.this, "����AES�����㷨",
				//Toast.LENGTH_LONG).show();
			ENER = "AES";
		    } else if (capaArray[2].equals("TKIP")) {
			// ����TKIP�����㷨
			//Toast.makeText(WifiListPhoneUI.this, "����TKIP�����㷨",
				//Toast.LENGTH_LONG).show();
			ENER = "TKIP";
		    } else if (capaArray[2].equals("TKIP+CCMP")) {
			// ����TKIP+AES�����㷨
			//Toast.makeText(WifiListPhoneUI.this, "����AES+TKIP�����㷨",
				//Toast.LENGTH_LONG).show();
			ENER = "TKIPAES";
		    }
		}
		// ����Intent
		Intent it = new Intent();
		it.setClass(WifiListPhoneUI.this, WifiKeyUI.class);
		it.putExtra("SSID", SSID);
		it.putExtra("CERT", CERT);
		it.putExtra("ENER", ENER);
		it.putExtra("login", login);
		startActivity(it);
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.wifilist_actionbar, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case (android.R.id.home):
	    WifiListPhoneUI.this.finish();
	    return true;
	case (R.id.muRefresh):
	    wifi.startScan();
	    ad.show();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
