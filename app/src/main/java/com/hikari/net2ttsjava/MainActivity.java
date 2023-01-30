package com.hikari.net2ttsjava;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



public class MainActivity extends AppCompatActivity{
    Context context;
    TextView infoIp;
    View viewContainer;
    TouchCoordinates coords;
    TextToSpeech mTts;
    ServerSocket httpServerSocket;
    SensorManager sensorManager;
    Sensor proximitySensor,magneticSensor,lightSensor, accelerometerSensor, pressureSensor, temperatureSensor;
    SensorData multiSensorData;
    KeyClass keyClass;
    WifiClass wifiClass;
    BroadcastReceiver receiver;
    GsmInfoClass gsmInfoClass;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SMS Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
        //Call Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
        //Bluetooth Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, 10);
        //Camera Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(new String[]{Manifest.permission.CAMERA},10);

        viewContainer = findViewById(R.id.viewContainer);
        context = this;
        infoIp = findViewById(R.id.infoip);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiClass = new WifiClass(wifiManager);

        TextView bluetoothTextView = findViewById(R.id.bluetoothTextView);
        BluetoothClass bluetoothClass = new BluetoothClass(bluetoothTextView);
        receiver = bluetoothClass.getReceiver();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        TelephonyManager telephonyManager =((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
        gsmInfoClass = new GsmInfoClass(telephonyManager);



//        bluetoothTextView.setText(bluetoothClass.getList());



        final WebView webView = findViewById(R.id.displayWebView);
        final TextView msgLogView = findViewById(R.id.msg),messageFromUser = findViewById(R.id.messageFromUser),proximityTextView = findViewById(R.id.proximityTextView),
                magneticTextView = findViewById(R.id.magneticTextView), lightTextView = findViewById(R.id.lightTextView), accelerometerTextView = findViewById(R.id.accelerometerTextView),
                pressureTextView = findViewById(R.id.pressureTextView), temperatureTextView = findViewById(R.id.temperatureTextView);
        Map<String, TextView> sensorTextView = new HashMap<>();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mTts = new TextToSpeech(this, i -> {
            if(i == TextToSpeech.SUCCESS) mTts.setLanguage(new Locale("ru"));
        });

        sensorTextView.put("proximity", proximityTextView);
        sensorTextView.put("magnetic", magneticTextView);
        sensorTextView.put("light", lightTextView);
        sensorTextView.put("accelerometer", accelerometerTextView);
        sensorTextView.put("pressure", pressureTextView);
        sensorTextView.put("temperature", temperatureTextView);

        multiSensorData = new SensorData(context,sensorTextView);
        SpeakClass speakClass = new SpeakClass(context, mTts, messageFromUser);
        DisplayClass displayClass = new DisplayClass(webView);

        sensorManager =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        else proximityTextView.setText("Proximity sensor isn't available");

        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        else magneticTextView.setText("Magnetic sensor isn't available");

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        else lightTextView.setText("Light sensor isn't available");

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        else accelerometerTextView.setText("Accelerometer sensor isn't available");

        if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        else pressureTextView.setText("Barometer sensor isn't available");

        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        else temperatureTextView.setText("Temperature sensor isn't available");

        infoIp.setText(getIpAddress() + ":" + HttpServerThread.HttpServerPORT + "\n");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels, width = displayMetrics.widthPixels;
        int orientation = getResources().getConfiguration().orientation;

        int[] extensionAndOrientation = new int[]{height,width,orientation};

        viewContainer.setOnKeyListener((view, i, keyEvent) -> {
            keyClass = new KeyClass(keyEvent.getAction(), i);
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) return keyClass.onKeyDown(i,keyEvent);
            else if(keyEvent.getAction() == KeyEvent.ACTION_UP) return keyClass.onKeyUp(i,keyEvent);

            return false;
        });
        viewContainer.setOnTouchListener((view, motionEvent) -> {
            coords = new TouchCoordinates((int) motionEvent.getX(),(int) motionEvent.getY());
            return false;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);

            }
        }

        HttpServerThread httpServerThread = new HttpServerThread(context,speakClass, displayClass, multiSensorData, msgLogView,extensionAndOrientation,coords, keyClass, audioManager,wifiClass,gsmInfoClass);
        httpServerThread.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (httpServerSocket != null) {
            try {
                httpServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mTts != null){
            mTts.stop();
            mTts.shutdown();
        }
        sensorManager.unregisterListener(multiSensorData);
        unregisterReceiver(receiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(multiSensorData, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(multiSensorData, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(multiSensorData, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(multiSensorData, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(multiSensorData, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(multiSensorData);
        unregisterReceiver(receiver);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

        }
    }
    public void takeSnap(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 7);
    }


    private String getIpAddress() {
        StringBuilder ip = new StringBuilder();
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip.append("SiteLocalAddress: ").append(inetAddress.getHostAddress()).append("\n");
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip.append("Something went Wrong! ").append(e.toString()).append("\n");
        }

        return ip.toString();
    }
}