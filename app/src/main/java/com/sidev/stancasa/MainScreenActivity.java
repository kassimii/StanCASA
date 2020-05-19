package com.sidev.stancasa;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InputMismatchException;
import java.util.UUID;

public class MainScreenActivity extends AppCompatActivity {

    SeekBar brightness;
    Button btn, btn2;
    TextView tv;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private int progressBar = 0;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothConnectActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        setContentView(R.layout.activity_main);

        brightness = (SeekBar)findViewById(R.id.lightSeekBar);

        tv = (TextView)findViewById(R.id.DisplayAmountOfLight);

        new ConnectBT().execute(); //Call the class to connect

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    progressBar = progress;
                    tv.setText(Integer.toString(progress) + " %");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btn = (Button)findViewById(R.id.setLightButton);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new CommunicateBT().execute(1); // mobile -> Arduino
            }
        });

        btn2 = (Button)findViewById(R.id.fetchDataButton);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new CommunicateBT().execute(-1); // Arduino -> mobile
            }
        });

    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {}
        }
        finish(); //return to the first layout

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainScreenActivity.this, "Connecting...", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                    // cod de trimis ceva prin bt
//                    String s = "abcdef";
//                    byte[] s2 = s.getBytes("us-ascii");
//                    OutputStream os = btSocket.getOutputStream();
//                    DataOutputStream dos = new DataOutputStream(os);
//                    os.write(s2);

                    // cod de primit ceva..
//                    byte[] ans = new byte[1];
//                    InputStream is = btSocket.getInputStream();
//                    DataInputStream dis = new DataInputStream(is);
//                    dis.readFully(ans);
//                    System.out.println("daf");
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                finish();
            }
            else
            {
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private class CommunicateBT extends AsyncTask<Integer, Void, Void>  // UI thread
    {

        @Override
        protected void onPreExecute()
        { }

        @Override
        protected Void doInBackground(Integer... devices) //while the progress dialog is shown, the connection is done in background
        {
            int BTway = devices[0];

            if (BTway == 1) {
                try {

                    byte x = (byte) (progressBar & 0xff);
                    btSocket.getOutputStream().write(x);

                } catch (IOException ex) { ex.printStackTrace(); }

            } else if (BTway == -1) {
                InputStream is = null;
                try {
                    is = btSocket.getInputStream();
                    DataInputStream dis = new DataInputStream(is);

                    if (is.available() > 0) {
                        byte[] b = new byte[1];
                        dis.readFully(b);
                        Log.i("inputBT", b.toString());
                        // poti pune debugger break aci
                    }

                } catch (IOException e) { e.printStackTrace(); }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
        }
    }

}


