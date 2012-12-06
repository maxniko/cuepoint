package com.cuepoint.clases;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver  {

	@Override
	public void onReceive(Context context, Intent intent) {
		//boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        //String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        //boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

        NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        //NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
        
        if(currentNetworkInfo.isConnected()){
        	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        	if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
        		Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
        		if (wifiInfo.getSSID().equals("CuePoint"))
        	    {
        	    	
        	    }
        	}
        }else{
            Toast.makeText(context, "Not Connected", Toast.LENGTH_LONG).show();
        }
	}

	public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;  //<--  --  -- Connected
                    }
                }
            }
        }
        return false;  //<--  --  -- NOT Connected
    }
	
	public static boolean CheckInternet(Context context) 
	{
	    ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if (wifi.isConnected()) {
	        return true;
	    } else if (mobile.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
