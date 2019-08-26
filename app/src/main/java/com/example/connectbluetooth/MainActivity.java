package com.example.connectbluetooth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothDevice device;
    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> btDeviceArrayList;
    private ArrayList<UUID> uuidArrayList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList deviceStrs = new ArrayList();
        final ArrayList<String> devices = new ArrayList();
        btDeviceArrayList = new ArrayList<>();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
                btDeviceArrayList.add(device);
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));


        final UUID uuid = UUID.fromString("0000110F-0000-1000-8000-00805F9B34FB");

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                 position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String deviceAddress = devices.get(position);
                Toast.makeText(MainActivity.this,deviceAddress+"---"+deviceStrs.get(position), Toast.LENGTH_SHORT).show();


                device = btAdapter.getRemoteDevice(deviceAddress);
                new ConnectToDevice().execute(btDeviceArrayList.get(position));

                uuidArrayList = new ArrayList<>();
                //uuidArrayList.add(btDeviceArrayList.get(position).getUuids()[0].getUuid());

            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private class ConnectToDevice extends AsyncTask<BluetoothDevice, Void, Void>{

        @Override
        protected Void doInBackground(BluetoothDevice... devices) {
            //BluetoothSocket socket = null;
            try {
                /*socket = device.createInsecureRfcommSocketToServiceRecord(uuids[0]);
                socket.connect();*/
                //new BluetoothConnector(device,false,btAdapter,null).connect();


                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                if (btAdapter.isEnabled()) {
                    //SharedPreferences prefs_btdev = getSharedPreferences("btdev", 0);
                    String btdevaddr= devices[0].getAddress();

                    if (btdevaddr != "?")
                    {
                        BluetoothDevice device = btAdapter.getRemoteDevice(btdevaddr);
                        Log.d("rough","device name: "+device.getName());
                        UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service
                        //UUID SERIAL_UUID = device.getUuids()[0].getUuid(); //if you don't know the UUID of the bluetooth device service, you can get it like this from android cache

                        BluetoothSocket socket = null;

                        try {
                            //socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                            socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                        } catch (Exception e) {Log.e("","Error creating socket");}

                        try {
                            socket.connect();

                            Log.e("rough","Connected"+socket.isConnected());
                        } catch (IOException e) {
                            Log.e("rough",e.getMessage());
                            try {
                                Log.e("rough","trying fallback...");

                                socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                                socket.connect();

                                Log.e("rough","Connected");
                            }
                            catch (Exception e2) {
                                Log.e("rough", "Couldn't establish Bluetooth connection!");
                            }
                        }
                    }
                    else
                    {
                        Log.e("rough","BT device not selected");
                    }
                }



               // Log.d("rough","Connected...");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("rough","Exception: "+e.toString());

            }
            return null;
        }
    }
}
