package com.hikari.net2ttsjava;

import static android.app.PendingIntent.getActivity;
import static com.hikari.net2ttsjava.SmsAndCall.callNo;
import static com.hikari.net2ttsjava.SmsAndCall.sendSMS;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;

import android.provider.Settings;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

class HttpResponseThread extends Thread {
    Context context;
    Socket socket;
    SensorData sensor;
    SpeakClass speakClass;
    DisplayClass displayClass;
    StringBuilder jsonResponse = new StringBuilder("{"), msgLog = new StringBuilder("");
    TextView msgLogView;
    int[] extensionAndOrientation;
    TouchCoordinates coords;
    KeyClass keyClass;
    GenerateAudio generateAudio;
    AudioManager audioManager;
    WifiClass wifiClass;
    GsmInfoClass gsmInfoClass;

    public HttpResponseThread(Context context,SpeakClass speakClass, DisplayClass displayClass, SensorData sensor, TextView msgLogView,int[] extensionAndOrientation, TouchCoordinates coords, KeyClass keyClass, AudioManager audioManager,WifiClass wifiClass,GsmInfoClass gsmInfoClass, Socket socket){
        this.context = context;
        this.speakClass = speakClass;
        this.displayClass = displayClass;
        this.msgLogView = msgLogView;
        this.socket = socket;
        this.sensor = sensor;
        this.extensionAndOrientation = extensionAndOrientation;
        this.coords = coords;
        this.keyClass = keyClass;
        this.audioManager = audioManager;
        this.wifiClass = wifiClass;
        this.gsmInfoClass = gsmInfoClass;
    }

    private void appendResponse(String message){ this.jsonResponse.append(message); }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        BufferedReader is;
        PrintWriter os;
        String request;
        BatteryInfo batteryInfo = new BatteryInfo(context);

        try {
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = is.readLine();

            Uri uri = Uri.parse("http://fakehost.it" + request);
            //Speak
            String message = uri.getQueryParameter("speak");
            if(message != null && !message.isEmpty()) speakClass.speak(message);
            //Stop speaking
            String stopCode = uri.getQueryParameter("stop");
            if(stopCode != null && !stopCode.isEmpty()) speakClass.stopSpeaking(stopCode);
            //Display
            String toDisplay = uri.getQueryParameter("display");
            if(toDisplay!=null && !toDisplay.isEmpty()) displayClass.display(toDisplay);
            //Proximity
            String proximityStr = uri.getQueryParameter("proximity");
            if(proximityStr != null && !proximityStr.isEmpty()) appendResponse("\"proximity\":"+sensor.getProximity(proximityStr)+",");
            //Magnetic
            String magneticStr = uri.getQueryParameter("magnetic");
            if(magneticStr != null && !magneticStr.isEmpty()) appendResponse("\"magnetic\":"+ Arrays.toString(sensor.getMagnetic(magneticStr))+",");
            //Light
            String lightStr = uri.getQueryParameter("light");
            if(lightStr != null && !lightStr.isEmpty()) appendResponse("\"light\":"+ sensor.getLight(lightStr)+",");
            //Tilt
            String tiltStr = uri.getQueryParameter("tilt");
            if(tiltStr != null && !tiltStr.isEmpty()) {
                tiltStr = tiltStr.replace("HTTP/1.1" , "").trim();
                if(tiltStr.equals("true")) appendResponse("\"landscape\":"+ (extensionAndOrientation[2] == Configuration.ORIENTATION_LANDSCAPE)+", \"portrait:\""+(extensionAndOrientation[2] == Configuration.ORIENTATION_PORTRAIT)+",");
            }
            //Acceleration
            String accelerationStr = uri.getQueryParameter("acceleration");
            if(accelerationStr != null && !accelerationStr.isEmpty()) appendResponse("\"acceleration\":"+ Arrays.toString(sensor.getAccelerometer(accelerationStr))+",");
            //Barometer
            String pressureStr = uri.getQueryParameter("pressure");
            if(pressureStr != null && !pressureStr.isEmpty()) appendResponse("\"pressure\":"+ sensor.getPressure(pressureStr)+",");
            //Screen
            String extensionStr = uri.getQueryParameter("extension");
            if(extensionStr != null && !extensionStr.isEmpty()) {
                extensionStr = extensionStr.replace("HTTP/1.1" , "").trim();
                if (extensionStr.equals("true")) appendResponse("\"height\":"+ extensionAndOrientation[0]+", \"width\": "+ extensionAndOrientation[1] +",");
            }
            //Date
            String dateStr = uri.getQueryParameter("date");
            if(dateStr != null && !dateStr.isEmpty()) {
                dateStr = dateStr.replace("HTTP/1.1" , "").trim();
                Date date = new Date();
                String datetime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
                if(dateStr.equals("true")) appendResponse("\"datetime\": \""+datetime+"\" ,");
            }
            //Sensor
            String sensorStr = uri.getQueryParameter("sensor");
            if(sensorStr != null && !sensorStr.isEmpty()) {
                sensorStr = sensorStr.replace("HTTP/1.1" , "").trim();
                if(sensorStr.equals("true")) appendResponse(coords.toString()+",");
            }
            //temp
            String temperatureStr = uri.getQueryParameter("temperature");
            if(temperatureStr != null && !temperatureStr.isEmpty()) appendResponse("\"temperature\":"+ sensor.getTemperature(temperatureStr)+",");

            //cam1
            String cameraStr = uri.getQueryParameter("camera");
            if(temperatureStr != null && !temperatureStr.isEmpty()) {
                context.takeSnap();
            }
//                appendResponse("\"temperature\":"+ sensor.getTemperature(temperatureStr)+",");
            //cam2
            //color1
            //color2

            //frequency
            //record
            //amplitude

            //hear_text

            //Key
            String keyStr = uri.getQueryParameter("sensor");
            if(sensorStr != null && !sensorStr.isEmpty()) appendResponse("\"key_states\":"+ keyClass.getKeyStates(keyStr)+",");
            //tone&length
            String toneStr = uri.getQueryParameter("tone"), lengthStr = uri.getQueryParameter("length");
            if((toneStr != null && !toneStr.isEmpty()) && (lengthStr != null && !lengthStr.isEmpty())){
                toneStr = toneStr.replace("HTTP/1.1" , "").trim();
                lengthStr = lengthStr.replace("HTTP/1.1" , "").trim();
                generateAudio = new GenerateAudio(Integer.parseInt(toneStr), Integer.parseInt(lengthStr));
                generateAudio.generateTone();
            }
            //Stop Audio
            String stopAudio = uri.getQueryParameter("stop_audio");
            if(stopAudio != null && !stopAudio.isEmpty()) generateAudio.stopTrack(stopAudio);
            //flash

            //proxy

            //gsm
            String gsmStr = uri.getQueryParameter("gsm");
            if(gsmStr != null && !gsmStr.isEmpty()) appendResponse("\"gsm_info\":{"+gsmInfoClass.getGsmInfo(gsmStr)+"},");
            //gps

            //wifi list
            String wifiListStr = uri.getQueryParameter("wifi_list");
            if(wifiListStr != null && !wifiListStr.isEmpty()) appendResponse("\"wifi_list\":"+ wifiClass.getList(wifiListStr)+",");
            //wifi info
            String wifiInfoStr = uri.getQueryParameter("wifi_info");
            if(wifiInfoStr != null && !wifiInfoStr.isEmpty()) appendResponse("\"wifi_info\":{"+ wifiClass.getCurrentInfo(wifiInfoStr)+"},");
            //bluetooth

            //ir

            //ir out

            //Power
            String powerStr = uri.getQueryParameter("power");
            if(powerStr != null && !powerStr.isEmpty()){
                powerStr = powerStr.replace("HTTP/1.1" , "").trim();
                if(powerStr.equals("true")) appendResponse("\"power\": "+batteryInfo.isPluggedIn()+" ,");
            }
            //battery
            String batteryStr = uri.getQueryParameter("battery");
            if(batteryStr != null && !batteryStr.isEmpty()){
                batteryStr = batteryStr.replace("HTTP/1.1" , "").trim();
                if(batteryStr.equals("true")) appendResponse("\"battery\": "+batteryInfo.getBatteryPercentage()+" ,");
            }
            //Set volume
            String volStr = uri.getQueryParameter("volume");
            if(volStr != null && !volStr.isEmpty()){
                volStr = volStr.replace("HTTP/1.1" , "").trim();
                int volume = Integer.parseInt(volStr);
                if(volume >= 0 && volume <= 100 ) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }
            //set brightness
            String brightStr = uri.getQueryParameter("brightness");
            if(brightStr != null && !brightStr.isEmpty()){
                brightStr = brightStr.replace("HTTP/1.1" , "").trim();
                int brightness = Integer.parseInt(brightStr);
                if(brightness >= 0 && brightness <= 255 )  Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
            //call
            String callStr = uri.getQueryParameter("call");
            String simIdStr = uri.getQueryParameter("sim_id");
            int simId = 0;
            if(callStr != null && !callStr.isEmpty()) {
                callStr = callStr.replace("HTTP/1.1", "").trim();
                if(simIdStr != null && !simIdStr.isEmpty()) simId = Integer.parseInt(simIdStr.replace("HTTP/1.1", "").trim());
                boolean callStatus = callNo(context, callStr, simId);
                appendResponse("\"call_status\": " + callStatus + " ,");
            }
            //sms
            String smsStr = uri.getQueryParameter("sms"), smsTextStr = uri.getQueryParameter("text");
            if((smsStr != null && !smsStr.isEmpty()) && (smsTextStr != null && !smsTextStr.isEmpty())){
                smsStr = smsStr.replace("HTTP/1.1" , "").trim();
                smsTextStr = smsTextStr.replace("HTTP/1.1" , "").trim();
                boolean smsStatus = sendSMS(smsStr,smsTextStr);
                appendResponse("\"sms_status\": "+smsStatus+" ,");
            }
            //email
            //nfc
            //scan qr or bar




            jsonResponse.append("\"success\":true }");
            String response = "<html><head></head><body>" + jsonResponse.toString() + "</body></html>";

            os = new PrintWriter(socket.getOutputStream(), true);
            os.print("HTTP/1.0 200" + "\r\n");
            os.print("Content type: text/html" + "\r\n");
            os.print("Content length: " + response.length() + "\r\n");
            os.print("\r\n");
            os.print(response + "\r\n");
            os.flush();
            socket.close();
            msgLog.append("Request of " + request + " from " + socket.getInetAddress().toString() + "\n");
            msgLogView.post(() -> msgLogView.append(msgLog.toString()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}