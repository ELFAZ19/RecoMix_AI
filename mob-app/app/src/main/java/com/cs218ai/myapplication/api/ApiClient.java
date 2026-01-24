package com.cs218ai.myapplication.api;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "https://recomixai.streamlit.app/";
    private static final String TAG = "ApiClient";
    private static ApiService apiService;
    
    public static ApiService getApiService() {
        try {
            if (apiService == null) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                
                apiService = retrofit.create(ApiService.class);
            }
            return apiService;
        } catch (Exception e) {
            Log.e(TAG, "Error creating API service", e);
            return null;
        }
    }
}
