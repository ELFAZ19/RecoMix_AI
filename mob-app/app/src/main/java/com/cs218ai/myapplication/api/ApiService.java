package com.cs218ai.myapplication.api;

import com.cs218ai.myapplication.models.Movie;
import com.cs218ai.myapplication.models.UserStats;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // Note: These endpoints would need to be created on your Streamlit backend
    // For now, this is the interface structure
    
    @GET("api/recommendations")
    Call<List<Movie>> getRecommendations(
        @Query("user_id") int userId,
        @Query("n_recommendations") int nRecommendations,
        @Query("min_year") int minYear
    );
    
    @GET("api/user/stats")
    Call<UserStats> getUserStats(@Query("user_id") int userId);
    
    @GET("api/user/history")
    Call<List<Movie>> getUserHistory(@Query("user_id") int userId);
}
