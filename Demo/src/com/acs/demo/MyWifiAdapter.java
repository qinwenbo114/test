package com.acs.demo;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyWifiAdapter extends BaseAdapter{

    LayoutInflater inflater;  
    List<ScanResult> list;  
    public MyWifiAdapter(Context context, List<ScanResult> list) {  
        // TODO Auto-generated constructor stub  
        this.inflater = LayoutInflater.from(context);  
        this.list = list;  
    } 
    
    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return list.size(); 
    }

    @Override
    public Object getItem(int position) {
	// TODO Auto-generated method stub
	return position;
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	View view = null;
	view = inflater.inflate(R.layout.wifi_item, null);
	ScanResult scanResult = list.get(position);
	TextView tvSSID = (TextView) view.findViewById(R.id.tvSSID);
	TextView tvCapa = (TextView) view.findViewById(R.id.tvCERT);
	ImageView ivSignal = (ImageView) view.findViewById(R.id.ivSIGN);
	tvSSID.setText(scanResult.SSID);
	tvCapa.setText(scanResult.capabilities);
	if (Math.abs(scanResult.level)>70){
	    ivSignal.setImageResource(R.drawable.wififull);
	}
	else if (Math.abs(scanResult.level)>40){
	    ivSignal.setImageResource(R.drawable.wifimid);
	}
	else {
	    ivSignal.setImageResource(R.drawable.wifilow);
	}
	return view;
    }

}
