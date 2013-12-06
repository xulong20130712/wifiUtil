package com.mywififutil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MyWifiUtil {
	
	
	public static final String TAG = "WifiUtil";
	public static final int OPEN_WIFI_RESULT = 10001;
	public static final int OPEN_WIFI_ERROR = 10002;
	
	/**查找wifi成功**/
	public static final int SCAN_WIFI_RESULT = 10003;
	/**查找wifi失败**/
	public static final int SCAN_WIFI_ERROR = 10004;

	/**连接wifi不存在**/
	public static final int CONNECT_WIFI_NULL = 20000;
	/**连接wifi成功**/
	public static final int CONNECT_WIFI_RESULT = 20001;
	/**连接wifi错误**/
	public static final int CONNECT_WIFI_ERROR = 20002;
	/**连接wifi超时**/
	public static final int CONNECT_WIFI_TIMEOUT = 20003;
	
	private WifiManager wifiManager;
	private Context context;
    
	//定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType
	{
		WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}
	 
	//构造函数
	public MyWifiUtil(WifiManager wifiManager)
	{
		this.wifiManager = wifiManager;
	}
	
	public MyWifiUtil(Context context){
		this.context = context;
		this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	//打开wifi功能
    public boolean OpenWifi()
    {
    	boolean bRet = true;
    	if (!wifiManager.isWifiEnabled())
        {
    		bRet = wifiManager.setWifiEnabled(true);  
        }
        return bRet;
    }
    
    public boolean getIsOpen(){
    	return wifiManager.isWifiEnabled();
    }
    
    /**检查当前WIFI状态   **/
    public int checkState() {  
        return wifiManager.getWifiState();  
    }
    /**断开当前网络**/
    public void disconnectCurrWifi(){
    	if(this.isWifiConnect()){
    		int networkId = wifiManager.getConnectionInfo().getNetworkId();
    		disconnectWifi(networkId);
    	}
    }
    
	/**断开指定ID的网络   **/
    public void disconnectWifi(int netId) {  
    	wifiManager.disableNetwork(netId);  
    	wifiManager.disconnect();  
    } 
    
    /**删除wifi记录**/
    public boolean removeWifi(int netId){
    	Log.v(TAG, "removeWifi:"+netId);
    	return wifiManager.removeNetwork(netId);
    }
    
    /**扫描wifi,返回是否存在**/
    public boolean isScanExist(WifiInfoObject wifiInfo){
    	boolean result = false;
    	//获取当前可用wifi
    	wifiManager.startScan();
    	List<ScanResult> wifiList = wifiManager.getScanResults();
    	if(wifiList != null && wifiList.size() > 0){
    		for(int i = 0;i<wifiList.size();i++){
    			//如果指定wifi存在，那么链接
    			Log.v(TAG, "connectWifi SSID:"+wifiList.get(i).SSID+",tSSID:"+wifiInfo.getSSID());
    			if(wifiList.get(i).SSID.equals(wifiInfo.getSSID())){
    				result = true;
    				break;
    			}
    		}
    	}
    	return result;
    }
    
    /**链接指定wifi,返回是否连接成功**/
    public boolean connectWifi(WifiInfoObject wifiInfo){
    	boolean result = false;
    	
    	if(isScanExist(wifiInfo)){
    		result = connectWifi(wifiInfo.getNetID());
    	}
    	return result;
    }
    /**链接指定wifi**/
    public boolean connectWifi(int netId){
    	return wifiManager.enableNetwork(netId, true);
    }
    
    public void OpenWifiConnect(){
    	if(!getIsOpen()){
    		if(this.OpenWifi())
    		{
    			//最长等待时间
    			int waitCount = 0;
        		//开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        		//状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        		while(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING && waitCount <= 100)
        		{
        			try{
        				waitCount++;
        				//为了避免程序一直while循环，让它睡个100毫秒在检测……
        				Thread.currentThread();
        				Thread.sleep(100);
        			}
        			catch(InterruptedException ie){
        			}
        		}
    			
    		}
    	}
    }
    
    /**异步等待重新连接**/
    public void ConnectAsync(){
    	disconnectCurrWifi();
    	OpenWifiConnect();
    }

    /**提供一个外部接口，传入要连接的无线网**/
    public void Connect(final String SSID,final String Password,final WifiCipherType Type)
    {
    	if(this.isWifiConnect()){
        	boolean isConnect = false;
    		String currSSID = wifiManager.getConnectionInfo().getSSID();
    		if(!currSSID.equals(SSID)){
    			disconnectWifi(wifiManager.getConnectionInfo().getNetworkId());
    			
    		}else{
    			isConnect = true;
    		}
    		
    		if(!isConnect){
        		if(ConnectHandler(SSID,Password,Type)){
        		}else{
        		}
        	}else{
        	}
    		
    	}else{
    		
    		OpenWifiConnect();
    	}
    	
    	
    }
    
    public boolean ConnectHandler(String SSID,String Password, WifiCipherType Type){
    	WifiConfiguration wifi = this.IsExsits(SSID);
    	if(wifi != null){
    		wifiManager.removeNetwork(wifi.networkId);
    	}
    		
    	WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
    	if(wifiConfig == null)return false;
    		
    	int netID = wifiManager.addNetwork(wifiConfig);
    	connectWifi(netID);
		return true;
    } 
    
	//查看以前是否也配置过这个网络
    public WifiConfiguration IsExsits(String SSID)
    {
    	List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) 
        {
        	if (existingConfig.SSID.equals("\""+SSID+"\""))
        	{
        		return existingConfig;
        	}
        }
        return null;
	}
    
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type)
    {
    	WifiConfiguration config = new WifiConfiguration();  
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";  
	     if(Type == WifiCipherType.WIFICIPHER_NOPASS)
	     {
	       config.wepKeys[0] = "";
	       config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	       config.wepTxKeyIndex = 0;
	     }
	     if(Type == WifiCipherType.WIFICIPHER_WEP)
	     {
	      config.preSharedKey = "\""+Password+"\""; 
	      config.hiddenSSID = true;  
	      config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
	      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
	      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	      config.wepTxKeyIndex = 0;
	     }
	     if(Type == WifiCipherType.WIFICIPHER_WPA)
	     {
		     config.hiddenSSID = true;  
		     config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
		     if(Password == null || Password.equals("")){
		    	 config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);                
			     //config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);  
		     }else{
			     config.preSharedKey = "\""+Password+"\"";
			     config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
			     config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                        
			     config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP); 
			     //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);      
			     config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);   
		         config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
		     }                        
		     config.status = WifiConfiguration.Status.ENABLED;  
	     }
	     else
	     {
	    	 return null;
	     }
	     return config;
    }
    
    
    /**
	 * check is wifi
	 * **/
	public boolean isWifiConnect() {  
		ConnectivityManager connManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	
	/**
	 * 获取ip
	 * **/
	public String getLocalIpAddress() {  
	    try {  
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
	            NetworkInterface intf = en.nextElement();  
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                InetAddress inetAddress = enumIpAddr.nextElement();  
	                if (!inetAddress.isLoopbackAddress()) {  
	                    return inetAddress.getHostAddress().toString();  
	                }  
	            }  
	        }  
	    } catch (SocketException ex) {  
	        Log.e(TAG, ex.toString());  
	    }  
	    return null;  
	}
    

	
	/**
	 * 获取wifi IP地址
	 * **/
	public String getWifiIpAddress(){
		if(isWifiConnect()){
			WifiInfo wifiinfo= this.wifiManager.getConnectionInfo();  
			String ip = intToIp(wifiinfo.getIpAddress());
			return ip;
		}else{
			return null;
		}
	}
	
public static String intToIp(int ip)  
    
    {   return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
	              + ((ip >> 24) & 0xFF);
  
    }
	
	public String getBSSID(){
		if(isWifiConnect()){
			return wifiManager.getConnectionInfo().getBSSID();
		}else{
			return null;
		}
	}
	
	public String getSSID(){
		if(isWifiConnect()){
			return wifiManager.getConnectionInfo().getSSID();
		}else{
			return null;
		}
	}
	
	public WifiInfo getWifiInfo(){
		if(isWifiConnect()){
			return wifiManager.getConnectionInfo();
		}else{
			return null;
		}
	}
	
	public WifiInfoObject getWifiInfoObject(){
		WifiInfo info = getWifiInfo();
		if(info != null){
			return new WifiInfoObject(info);
		}else{
			return null;
		}
	}
}
