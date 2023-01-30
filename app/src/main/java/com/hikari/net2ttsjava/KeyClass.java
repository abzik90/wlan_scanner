package com.hikari.net2ttsjava;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class KeyClass extends KeyEvent {
    Map<String,Boolean> keyStates = new HashMap<>();
    public KeyClass(int action, int code) {
        super(action, code);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                keyStates.put("volumeUp", true);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                keyStates.put("volumeDown", true);
                break;
            case KeyEvent.KEYCODE_POWER:
                keyStates.put("power", true);
                break;
            default:
                return false;
        }
        return true;
    }
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                keyStates.put("volumeUp", false);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                keyStates.put("volumeDown", false);
                break;
            case KeyEvent.KEYCODE_POWER:
                keyStates.put("power", false);
                break;
            default:
                return false;
        }
        return true;
    }
    public String getKeyStates(String keyReq){
        keyReq = keyReq.replace("HTTP/1.1" , "").trim();
        if(!keyReq.equals("true"))
            return keyStates.toString();
        return "No data";
    }
}
