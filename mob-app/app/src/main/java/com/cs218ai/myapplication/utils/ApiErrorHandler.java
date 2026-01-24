package com.cs218ai.myapplication.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import retrofit2.HttpException;

public class ApiErrorHandler {
    private static final String TAG = "ApiErrorHandler";
    
    public static void handleError(Context context, Throwable throwable) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot show error message");
            return;
        }
        
        String errorMessage = "An error occurred";
        
        try {
            if (throwable instanceof UnknownHostException || throwable instanceof IOException) {
                // Network error
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    errorMessage = "No internet connection. Please check your network settings.";
                    NetworkUtils.showNetworkError(context);
                } else {
                    errorMessage = "Unable to connect to server. Please try again later.";
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            } else if (throwable instanceof SocketTimeoutException) {
                errorMessage = "Request timed out. Please check your connection and try again.";
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            } else if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int code = httpException.code();
                if (code >= 500) {
                    errorMessage = "Server error. Please try again later.";
                } else if (code == 404) {
                    errorMessage = "Service not found. Please check the server URL.";
                } else if (code == 401 || code == 403) {
                    errorMessage = "Authentication failed. Please check your credentials.";
                } else {
                    errorMessage = "Server returned an error. Please try again.";
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                errorMessage = "An unexpected error occurred. Please try again.";
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
            
            Log.e(TAG, "API Error: " + errorMessage, throwable);
        } catch (Exception e) {
            Log.e(TAG, "Error handling API error", e);
        }
    }
    
    public static void handleErrorSilently(Throwable throwable) {
        try {
            if (throwable instanceof UnknownHostException || throwable instanceof IOException) {
                Log.w(TAG, "Network error: " + throwable.getMessage());
            } else if (throwable instanceof SocketTimeoutException) {
                Log.w(TAG, "Request timeout: " + throwable.getMessage());
            } else if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Log.w(TAG, "HTTP error: " + httpException.code() + " - " + httpException.message());
            } else {
                Log.e(TAG, "Unexpected error", throwable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in error handler", e);
        }
    }
}
