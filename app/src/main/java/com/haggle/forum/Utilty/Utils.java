package com.haggle.forum.Utilty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.google.firebase.auth.FirebaseAuth;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;

public class Utils {

    private static final String DATE_FORMAT_1 = "hh:mm a";
    private static final String DATE_FORMAT_2 = "dd/M/yyyy";

    private Context context;
    private String UID ="";
    private SharedPreferences contact;

    private FirebaseAuth firebaseAuth;

    public Utils(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();

        contact = context.getSharedPreferences("Joined",MODE_PRIVATE);

        UID = firebaseAuth.getUid();
    }

    public String IP(){

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ipAddress;

    }

    public String Mac(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }

    public String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2);
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }


    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
