package com.hikari.net2ttsjava;

import android.content.Context;
import android.media.AudioManager;
import android.widget.TextView;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class HttpServerThread extends Thread {
    Context context;
    ServerSocket httpServerSocket;
    SpeakClass speakClass;
    DisplayClass displayClass;
    SensorData sensor;
    TextView msgLogView;
    int[] extensionAndOrientation;
    TouchCoordinates coords;
    KeyClass keyClass;
    AudioManager audioManager;
    WifiClass wifiClass;
    GsmInfoClass gsmInfoClass;

    static final int HttpServerPORT = 8080;

    public HttpServerThread(Context context, SpeakClass speakClass,DisplayClass displayClass,SensorData sensor, TextView msgLogView,int[] extensionAndOrientation,TouchCoordinates coords,KeyClass keyClass,AudioManager audioManager,WifiClass wifiClass,GsmInfoClass gsmInfoClass) {
        this.context = context;
        this.speakClass = speakClass;
        this.displayClass = displayClass;
        this.msgLogView = msgLogView;
        this.sensor = sensor;
        this.extensionAndOrientation = extensionAndOrientation;
        this.coords = coords;
        this.keyClass = keyClass;
        this.audioManager = audioManager;
        this.wifiClass = wifiClass;
        this.gsmInfoClass = gsmInfoClass;
    }


    @Override
    public void run() {
        Socket socket;

        try {
            httpServerSocket = new ServerSocket(HttpServerPORT);

            while(true){
                socket = httpServerSocket.accept();

                HttpResponseThread httpResponseThread = new HttpResponseThread(context,speakClass, displayClass, sensor ,msgLogView,extensionAndOrientation, coords, keyClass,audioManager,wifiClass,gsmInfoClass, socket);
                httpResponseThread.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}