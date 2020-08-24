package com.example.nirzhor.automaticgatecontroller;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.test.mock.MockPackageManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import android.telephony.SmsManager;

import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity {

    Button btnShowLocation;
    int i = 0;
    Button Exit;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;


    EditText lat1, lng1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    GPSTracker gps;


    //int i=0;
    public int btn = 0;

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.button);
        Exit = (Button) findViewById(R.id.exit);

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // btn=0;
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }

        });

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {


                btn = 1;

                if (btn == 1) {

                    final Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // int i  =1;
                            loc();

                            //Do something after 4 seconds
                            handler.postDelayed(this, 3000);
                        }
                    }, 3000);  //the time is in miliseconds
                }


            }


        });
    }


    public void loc() {


        // create class object
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //double lat1 = 0;

            lat1 = (EditText) findViewById(R.id.lat1);
            lng1 = (EditText) findViewById(R.id.lng1);

            Double lat = Double.parseDouble(lat1.getText().toString());
            Double lng = Double.parseDouble(lng1.getText().toString());

            double distance = Cal2(lat, lng, latitude, longitude);


            if (distance < 20) {

                i++;


                if (i == 1) {
                    sentMsg();
                    i++;

                }


                alart();


                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();


                //flag = 1;
            } else if (distance >= 20) {
                i = 0;
                normal();

            }


            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude + "\n\n Distance :  " + distance, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }


    public double Cal2(double lat1, double lng1, double lat2, double lng2) {
        double pk = (float) (180 / 3.14169);
        double a1 = lat1 / pk;
        double a2 = lng2 / pk;
        double b1 = lat2 / pk;
        double b2 = lng2 / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public void alart() {

        RadioButton rb1 = (RadioButton) findViewById(R.id.red);
        rb1.setChecked(true);

        RadioButton rb2 = (RadioButton) findViewById(R.id.green);
        rb2.setChecked(false);
    }

    public void normal() {

        RadioButton rb1 = (RadioButton) findViewById(R.id.red);
        rb1.setChecked(false);

        RadioButton rb2 = (RadioButton) findViewById(R.id.green);
        rb2.setChecked(true);

    }


    public void sentMsg() {

        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:0123456789"));
        sendIntent.putExtra("sms_body", " Danger  , Train in Range !!");
        startActivity(sendIntent);

    }
}


