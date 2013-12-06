package com.mywififutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.database.Cursor;
import android.net.wifi.WifiInfo;

public class WifiInfoObject {
	private int NETID;
	private String SSID;
	private String BSSID;
	private String drivesId;
	private String Encrypt;
	private String TkipAes;
	private String IpAddress;
	private String Mask;
	private String Gateway;
	private String DNS1;
	private String DNS2;
	private int RSSI;
	private String Ensure;
	private String Password;
	private int Channel;
	private int Status;
	private boolean isConnect;
	private String WiredType;
	private String stringValue;
	
	public int getNetID(){
		return NETID;
	}
	public void setNetID(int value){
		NETID = value;
	}
	
	public String getSSID(){
		return SSID;
	}
	public void setSSID(String value){
		SSID = value;
	}
	/**WIFI MAC**/
	public String getBSSID(){
		return BSSID;
	}
	public void setBSSID(String value){
		BSSID = value;
	}
	
	public String getDrivesId(){
		return drivesId;
	}
	public void setDrivesId(String value){
		drivesId = value;
	}
	
	public String getTkipAes(){
		return TkipAes;
	}
	public void setTkipAes(String value){
		TkipAes = value;
	}
	public String getIpAddress(){
		return IpAddress;
	}
	public void setIpAddress(String value){
		IpAddress = value;
	}
	
	public String getMask(){
		return Mask;
	}
	public void setMask(String value){
		Mask = value;
	}
	
	public String getGateway(){
		return Gateway;
	}
	public void setGateway(String value){
		Gateway = value;
	}
	
	public String getDNS1(){
		return DNS1;
	}
	public void setDNS1(String value){
		DNS1 = value;
	}
	
	public String getDNS2(){
		return DNS2;
	}
	public void setDNS2(String value){
		DNS2 = value;
	}

	public String getEncrypt(){
		return Encrypt;
	}
	public void setEncrypt(String value){
		Encrypt = value;
	}
	
	public int getRSSI(){
		return RSSI;
	}
	public void setRSSI(int value){
		RSSI = value;
	}
	
	public String getEnsure(){
		return Ensure;
	}
	public void setEnsure(String value){
		Ensure = value;
	}
	
	public String getPassword(){
		if(Password == null)Password = "";
		return Password;
	}
	public String getPasswordAtReal(){
		return Password;
	}
	public void setPassword(String value){
		Password = value;
	}
	
	public int getChannel(){
		return Channel;
	}
	public void setChannel(int value){
		Channel = value;
	}
	
	public int getStatus(){
		return Status;
	}
	public void setStatus(int value){
		Status = value;
	}
	
	public boolean getIsConnect(){
		return isConnect;
	}
	public void setIsConnect(boolean value){
		isConnect = value;
	}
	
	public void setWiredType(String value){
		WiredType = value;
	}
	public String getWiredType(){
		return WiredType;
	}
	
	public void setString(String value){
		stringValue = value;
	}

	public WifiInfoObject(){
		
	}
	
	public WifiInfoObject(String nfcInfo){
		if(nfcInfo != null){
			String[] _info = nfcInfo.split(",");
			if(_info.length >= 2){
				try {
					SSID = URLDecoder.decode(_info[0],"UTF-8");
					if(_info.length > 1){
						Password = URLDecoder.decode(_info[1],"UTF-8");
					}else{
						Password = "";
					}
					
					if(_info.length > 2)
						drivesId = _info[2];
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
		        }
			}
		}
	}
	
	public WifiInfoObject(WifiInfo wifiInfo){
		if(wifiInfo != null){
			NETID = wifiInfo.getNetworkId();
			BSSID = wifiInfo.getBSSID();
			SSID = wifiInfo.getSSID();
			IpAddress = MyWifiUtil.intToIp(wifiInfo.getIpAddress());
			if(SSID.startsWith("\"") && SSID.endsWith("\""))
				SSID = SSID.substring(1, SSID.length() - 1);
		}
	}
	
	public WifiInfoObject(Cursor cursor){
		NETID = cursor.getInt(0);
		SSID = cursor.getString(1);
		drivesId = cursor.getString(2);
		BSSID = cursor.getString(3);
	}
	
	@Override
	public String toString() {  
		if(stringValue != null)
			return stringValue;
        return "NetId:" + NETID + ", SSID:" + SSID + ", BSSID:" + BSSID+",RSSI:"+RSSI;  
    }

    public static WifiInfoObject getDefaultObject(){
        WifiInfoObject result = new WifiInfoObject();
        result.setSSID("Disabled");
        result.setEncrypt("WPA-PSK");
        result.setTkipAes("aes");
        result.setPassword("88888888");
        result.setChannel(6);
        return result;
    }

    public boolean isDefault(){
        if("Disabled".equals(SSID) && "88888888".equals(Password)){
            return true;
        }else{
            return false;
        }
    }
	
	public void Copy(WifiInfoObject obj){
		NETID = obj.getNetID();
		SSID = obj.getSSID();
		drivesId = obj.getDrivesId();
		BSSID = obj.getBSSID();
	}
	
	public String toNfcInfo(){
		StringBuilder nfcInfo = new StringBuilder();
		try {
			nfcInfo.append(URLEncoder.encode(SSID,"UTF-8"));
			if(Password != null){
				nfcInfo.append("," + URLEncoder.encode(Password,"UTF-8"));
			}else{
				nfcInfo.append(",");
			}
			nfcInfo.append("," + drivesId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
        }
		return nfcInfo.toString();
	}
}
