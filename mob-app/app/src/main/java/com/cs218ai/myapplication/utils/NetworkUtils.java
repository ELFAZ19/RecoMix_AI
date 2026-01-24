package com.cs218ai.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {
    
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;
            
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void showNetworkError(Context context) {
        if (context != null) {
            Toast.makeText(context, "No internet connection. Please check your network settings.", 
                Toast.LENGTH_LONG).show();
        }
    }
    
    public static void showServerError(Context context) {
        if (context != null) {
            Toast.makeText(context, "Server error. Please try again later.", 
                Toast.LENGTH_LONG).show();
        }
    }
}
