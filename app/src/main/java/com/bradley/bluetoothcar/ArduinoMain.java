package com.bradley.bluetoothcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class ArduinoMain extends ActionBarActivity {

    // Declare buttons and editText
    Button reset, forward, reverse, left, right;

    // Member fields
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // Verify that this is the correct UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC address of the bluetooth module
    public String newAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_main);

        // Initialising buttons in the view
        reset = (Button) findViewById(R.id.reset);
        forward = (Button) findViewById(R.id.moveForward);
        reverse = (Button) findViewById(R.id.moveReverse);
        left = (Button) findViewById(R.id.turnLeft);
        right = (Button) findViewById(R.id.turnRight);

        // get the bluetooth adapter value and call checkBt state
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        // setup click listeners on the buttons

        reset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("9");
            }
        });

        forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendData("3:220");
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendData("2");
                    return true;
                }
                return false;
            }
        });

        reverse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendData("1:220");
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendData("2");
                    return true;
                }
                return false;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendData("4:200");
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendData("5");
                    return true;
                }
                return false;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendData("6:200");
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendData("5");
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();

        // Get Mac address from DeviceListActivity
        Intent intent = getIntent();
        newAddress = intent.getStringExtra(DeviceList.EXTRA_DEVICE_ADDRESS);

        // Set up a pointer to the remove device using its address.
        BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);

        // Attempt to create a blueototh socket for coms
        try{
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch(IOException e1){
            Toast.makeText(getBaseContext(), "Error - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
        }

        // Establish the connection
        try{
            btSocket.connect();
        }catch(IOException e){
            try{
                btSocket.close(); // if IO exception occurs attempt to close socket
            } catch(IOException e2){
                Toast.makeText(getBaseContext(), "Error -  Could not close BLuetooth socket", Toast.LENGTH_SHORT).show();
            }
        }

        // Create a data stream
        try{
            outStream = btSocket.getOutputStream();
        }catch(IOException e){
            Toast.makeText(getBaseContext(), "Error - Could not create bluetooth outstream", Toast.LENGTH_SHORT).show();
        }

        // Attempt to send a piece of junk data to see if a failure will happen before a user tries to send data
        sendData("x");
    }

    @Override
    public void onPause(){
        super.onPause();

        //CLose BT socket to device
        try{
            btSocket.close();
        }catch(IOException e2){
            Toast.makeText(getBaseContext(), "Error - failed to close bluetooth socket", Toast.LENGTH_SHORT).show();
        }
    }

    // Takse the UUID and creates a comm socket SEE IF THIS IS NECESSARY
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException{
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    // same as in device list activity
    private void checkBTState(){
        // Check device has bluetooth and that it is turned on
        if(btAdapter==null){
            Toast.makeText(getBaseContext(), "Error - Device does not support bluetooth", Toast.LENGTH_SHORT ).show();
            finish();
        }else{
            if(!btAdapter.isEnabled()){
                //Prompt the user to turn on bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Method to send data
    private void sendData(String message){
        byte[] msgBuffer = message.getBytes();

        try{
            // attempt to place data on the outstream
            outStream.write(msgBuffer);
        }catch (IOException e){
            // if the sending fails it is most likely because the device is no longer there
            Toast.makeText(getBaseContext(), "Error - Device not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
