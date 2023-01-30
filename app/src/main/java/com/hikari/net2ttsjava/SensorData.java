package com.hikari.net2ttsjava;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Map;


public class SensorData implements SensorEventListener {
    Context context;
    SensorManager sensorManager;
    Map<String, TextView> sensorTextView;

    float proximity = -1f, light = -1f,pressure = -1f,temperature = -1f;
    float[] magnetic = new float[]{0f, 0f, 0f};
    float[] accelerometer = new float[]{0f,0f,0f};

    public SensorData(Context context, Map<String, TextView> sensorTextView){
        this.context = context;
        this.sensorTextView = sensorTextView;
        sensorManager =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
    public float getProximity(String proximityReq){
        proximityReq = proximityReq.replace("HTTP/1.1" , "").trim();
        if(!proximityReq.equals("true"))
            return -1;
        Log.d("Sensor","proximity:" + proximity);
        return this.proximity;
    }
    public float[] getMagnetic(String magneticReq){
        magneticReq = magneticReq.replace("HTTP/1.1" , "").trim();
        if(!magneticReq.equals("true"))
            return new float[]{0,0,0};
        Log.d("Sensor","magnetic:" + Arrays.toString(magnetic));
        return this.magnetic;
    }
    public float[] getAccelerometer(String accelerometerReq){
        accelerometerReq = accelerometerReq.replace("HTTP/1.1" , "").trim();
        if(!accelerometerReq.equals("true"))
            return new float[]{0,0,0};
        Log.d("Sensor","accelerometer:" + Arrays.toString(accelerometer));
        return this.accelerometer;
    }
    public float getLight(String lightReq){
        lightReq = lightReq.replace("HTTP/1.1" , "").trim();
        if(!lightReq.equals("true"))
            return -1;
        Log.d("Sensor","light:" + light);
        return this.light;
    }
    public float getPressure(String pressureReq){
        pressureReq = pressureReq.replace("HTTP/1.1" , "").trim();
        if(!pressureReq.equals("true"))
            return -1;
        Log.d("Sensor","pressure:" + pressure);
        return this.pressure;
    }
    public float getTemperature(String temperatureReq){
        temperatureReq = temperatureReq.replace("HTTP/1.1" , "").trim();
        if(!temperatureReq.equals("true"))
            return -1;
        Log.d("Sensor","temperature:" + temperature);
        return this.temperature;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_PROXIMITY:
                proximity = sensorEvent.values[0];
//                Log.d("Sensor","proximity:" + proximity);
                sensorTextView.get("proximity").post(() -> sensorTextView.get("proximity").setText("Proximity is: " + proximity + "cm"));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetic = sensorEvent.values;
//                Log.d("Sensor","magnetic:" + Arrays.toString(magnetic));
                sensorTextView.get("magnetic").post(() -> sensorTextView.get("magnetic").setText("magnetic field is (in uT):\n x= " + magnetic[0] + " y= "+magnetic[1]+" z=" + magnetic[2]));
                break;
            case Sensor.TYPE_LIGHT:
                light = sensorEvent.values[0];
//                Log.d("Sensor","light:" + light);
                sensorTextView.get("light").post(() -> sensorTextView.get("light").setText("Light is(in SI lux): " + light));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerometer = sensorEvent.values;
//                Log.d("Sensor","accelerometer:" + Arrays.toString(accelerometer));
                sensorTextView.get("accelerometer").post(() -> sensorTextView.get("accelerometer").setText("Accelerometer vectors:\n x= " + accelerometer[0] + " y= "+accelerometer[1]+" z=" + accelerometer[2]));
                break;
            case Sensor.TYPE_PRESSURE:
                pressure = sensorEvent.values[0];
//                Log.d("Sensor","pressure:" + pressure);
                sensorTextView.get("pressure").post(() -> sensorTextView.get("pressure").setText("Pressure (in hPa or millibar): " + pressure));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temperature = sensorEvent.values[0];
//                Log.d("Sensor","temperature:" + temperature);
                sensorTextView.get("temperature").post(() -> sensorTextView.get("temperature").setText("Ambient temperature is (in Celsius): " + temperature));
                break;
            default:
                Log.d("Sensor", "Type Incompatible");
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
