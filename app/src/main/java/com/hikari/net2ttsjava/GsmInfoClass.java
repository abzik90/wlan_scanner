package com.hikari.net2ttsjava;

import android.telephony.TelephonyManager;

public class GsmInfoClass {
    TelephonyManager telephonyManager;
    public GsmInfoClass(TelephonyManager telephonyManager){
        this.telephonyManager = telephonyManager;
    }
    public String getGsmInfo(String gsmReq){
        String response = "";
        gsmReq = gsmReq.replace("HTTP/1.1" , "").trim();
        if(!gsmReq.equals("true")) return response;

        String operatorName = telephonyManager.getNetworkOperatorName(),simOperatorName = telephonyManager.getSimOperatorName(),
//                simSerial = telephonyManager.getSimSerialNumber(),
                cellInfo = telephonyManager.getAllCellInfo().toString();
        //TODO: JSON-ify the GSM response(cell_info part)
        response = "\"operator_name\":\""+operatorName+"\","
                +"\"sim_operator_name\":\""+simOperatorName+"\","
//                +"\"imei\":"+imei+","
//                +"\"sim_serial\":"+simSerial+","
                +"\"cellInfo\":"+cellInfo;
        return response;
    }
}
