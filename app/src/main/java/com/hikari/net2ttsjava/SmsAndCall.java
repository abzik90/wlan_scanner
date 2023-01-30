package com.hikari.net2ttsjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class SmsAndCall {
    public static boolean sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean callNo(Context context, String phoneNo, int simId){
        try{
            Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("com.android.phone.extra.slot", simId);
            intent.setData(Uri.parse("tel:" + phoneNo));
            context.startActivity(intent);
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
