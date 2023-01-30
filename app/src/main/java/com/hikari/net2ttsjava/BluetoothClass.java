package com.hikari.net2ttsjava;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import java.util.Set;

public class BluetoothClass {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    TextView bluetoothTextView;
    BluetoothClass(TextView bluetoothTextView){
        this.bluetoothTextView = bluetoothTextView;
    }
    public String getList(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        String response = "";
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                response += deviceName + " : " + deviceHardwareAddress + "\n";
            }
        }
        return response;
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                bluetoothTextView.post(() -> bluetoothTextView.append("Device name:" + deviceName + ":" + deviceHardwareAddress + "\n"));
            }
        }
    };
    public BroadcastReceiver getReceiver(){
        return this.receiver;
    }

}
