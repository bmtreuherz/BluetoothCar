package com.bradley.bluetoothcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

// Might need to change this to extend Activity
public class DeviceList extends ActionBarActivity {

    // textview for connection status
    TextView textConnectionStatus;
    ListView pairedListView;

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    // An EXTRA to ake the device MAC to the next activity
    public static String EXTRA_DEVICE_ADDRESS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        textConnectionStatus = (TextView) findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);

        // Initalize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
    }

    @Override
    public void onResume(){
        super.onResume();

        // check the BT status in case something has changed
        checkBTState();

        // clear the array so items aren't duplicated
        mPairedDevicesArrayAdapter.clear();

        textConnectionStatus.setText(" "); // makes the textView blank

        // Get the local BT Adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to pairedDevices List
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previously paired devices to the array
        if(pairedDevices.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE); // make title visible
            for(BluetoothDevice device : pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" +device.getAddress() );
            }
        }else{
            mPairedDevicesArrayAdapter.add("no devices paired");
        }
    }

    // method to check if the device has bluetooth and if it is on
    // prompts the user to turn it on if it is off
    private void checkBTState(){
        // Check device has bluetooth and that it is turned on
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(!mBtAdapter.isEnabled()){
                // prompt user to turn on bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Set up on-click listenr for the listview
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            textConnectionStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the view
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);

            // Make an intent to start the next activity while taking an extra (the MAC address)
            Intent i = new Intent(DeviceList.this, ArduinoMain.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);

        }
    };

}
