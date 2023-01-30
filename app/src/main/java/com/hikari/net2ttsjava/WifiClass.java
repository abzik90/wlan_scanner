package com.hikari.net2ttsjava;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiClass {
    WifiManager wifiManager;
    WifiInfo wifiInfo;

    public WifiClass(WifiManager wifiManager){
        this.wifiManager = wifiManager;
        this.wifiInfo = wifiManager.getConnectionInfo();

    }

    public String getList(String listReq){
        listReq = listReq.replace("HTTP/1.1" , "").trim();
        if(listReq.equals("true")) {
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> scanResultList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                scanResultList.add("\""+scanResult.SSID+"\"");
            }
            return scanResultList.toString();
        }
        return "";
    }

    public String getCurrentInfo(String infoReq){
        String response="";
        infoReq = infoReq.replace("HTTP/1.1" , "").trim();
        if(infoReq.equals("true")) {
            response = "\"SSID\":"+wifiInfo.getSSID();
            response += ",\"BSSID\":\""+wifiInfo.getBSSID()+"\"";
            response += ",\"RSSI\":"+wifiInfo.getRssi();
            response += ",\"link_speed\":"+wifiInfo.getLinkSpeed();
        }
        return response;
    }
}
