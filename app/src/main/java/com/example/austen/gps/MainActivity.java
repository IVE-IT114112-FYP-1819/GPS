package com.example.austen.gps;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 1000;
    private static final int LOCATION_UPDATE_MIN_TIME = 50;
    private LocationManager mLocationManager;
    private Button send, save;
    private EditText phone;
    private String messages;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                messages = "https://www.google.com/maps/search/" + location.getLatitude() + "," + location.getLongitude();
            } else {
                Log.d("Test", "Location is null");
                messages = "Can't get lcation";
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String p) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.btnsend);
        phone = (EditText) findViewById(R.id.txtPhone);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        requestPermission();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phone.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                getCurrentLocation();
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED && !number.isEmpty() ){
                    smsManager.sendTextMessage(number,null,messages+"",null,null);
                }
            }
        });
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this,permissions, 1);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable( false );
            builder.setTitle("Prompt")
                    .setMessage("Please open location")
                    .setPositiveButton("GO to setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0);
                        }
                    })
                    .show();
        } else {
            Log.d("Test", "Location is opening");
        }
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location = null;
        if (!isGPSEnabled) {
            Log.d("Test", "Location do not open");
        } else if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            messages = "https://www.google.com/maps/search/" + location.getLatitude() + "," + location.getLongitude();
        } else {
            Log.d("Test", "Location is null");
            messages = "Can't get lcation";
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestPermission();
    }

}