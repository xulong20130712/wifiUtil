package com.mywififutil;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class MainActivity extends Activity {

	
	private WifiManager wifiManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initParams();
		
		
	}
	
	private void initParams() {
		
		wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
	}
	
	public boolean openWifi() {
		
		if(!wifiManager.isWifiEnabled()) {
			
			return wifiManager.setWifiEnabled(true);
		}
		return false;
	}
	
	//断开wifi
	public void disConnectedWifi() {
		
		if(isConnectedWifi()) {
			
			int networkId= wifiManager.getConnectionInfo().getNetworkId();
			disConnectedWifi(networkId);
		}
	}
	
	public void disConnectedWifi(int netId) {
		
		wifiManager.disableNetwork(netId);
		wifiManager.disconnect();
	}
	
	//检查指定的wifi是否存在列表中
	public boolean isScanExist(String SSid) {
		
		List<ScanResult> lists= getLists();
		if(lists!= null&& lists.size()> 0) {
			
			for(int i= 0; i< lists.size(); i++) {
				
				if(lists.get(i).SSID.equals(SSid)) {

					return true;
				}
			}
			
		}
		return false;
	}
	
	public List<ScanResult> getLists() {
		
		wifiManager.startScan();
		List<ScanResult> lists= wifiManager.getScanResults();
		return lists;
	}
	
	
	//连接到指定的wifi
	public boolean connectToWifi(int netId) {
		
		if(isScanExist(SSid)) {
			
			return wifiManager.enableNetwork(netId, true);
		}
		
	}
	
	public void netIdTOSSID() {
		
		List<ScanResult> lists= getLists();
		for(int i= 0; i< lists.size(); i++) {
			
			
		}
	}
			
	
	
	//检查是否已经连接wifi
	public boolean isConnectedWifi() {
		
		
		ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return netWorkInfo.isConnected();
	}
			
}