package com.cs218ai.myapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cs218ai.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

public class InsightsFragment extends Fragment {
    private BarChart ratingChart;
    private PieChart genreChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_insights, container, false);
            
            if (view == null) {
                return createErrorView(inflater);
            }
            
            ratingChart = view.findViewById(R.id.ratingChart);
            genreChart = view.findViewById(R.id.genreChart);
            
            if (ratingChart != null && genreChart != null && isAdded()) {
                setupRatingChart();
                setupGenreChart();
            }
            
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorView(inflater);
        }
    }
    
    private View createErrorView(LayoutInflater inflater) {
        try {
            return new View(inflater.getContext());
        } catch (Exception e) {
            return null;
        }
    }

    private void setupRatingChart() {
        try {
            if (ratingChart == null) return;
            
            // TODO: Load actual data from API
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, 5));
            entries.add(new BarEntry(1, 10));
            entries.add(new BarEntry(2, 15));
            entries.add(new BarEntry(3, 20));
            entries.add(new BarEntry(4, 12));
            
            BarDataSet dataSet = new BarDataSet(entries, "Rating Distribution");
            dataSet.setColor(Color.parseColor("#818CF8"));
            dataSet.setValueTextColor(Color.WHITE);
            
            BarData barData = new BarData(dataSet);
            ratingChart.setData(barData);
            
            ratingChart.getDescription().setEnabled(false);
            ratingChart.getLegend().setEnabled(false);
            ratingChart.setBackgroundColor(Color.parseColor("#0F172A"));
            ratingChart.getXAxis().setTextColor(Color.WHITE);
            ratingChart.getAxisLeft().setTextColor(Color.WHITE);
            ratingChart.getAxisRight().setEnabled(false);
            
            XAxis xAxis = ratingChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"1", "2", "3", "4", "5"}));
            
            ratingChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGenreChart() {
        try {
            if (genreChart == null) return;
            
            // TODO: Load actual data from API
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(25f, "Action"));
            entries.add(new PieEntry(20f, "Comedy"));
            entries.add(new PieEntry(15f, "Drama"));
            entries.add(new PieEntry(10f, "Romance"));
            entries.add(new PieEntry(30f, "SciFi"));
            
            PieDataSet dataSet = new PieDataSet(entries, "Genres");
            dataSet.setColors(new int[]{
                Color.parseColor("#4F46E5"),
                Color.parseColor("#7C3AED"),
                Color.parseColor("#818CF8"),
                Color.parseColor("#C084FC"),
                Color.parseColor("#312E81")
            });
            dataSet.setValueTextColor(Color.WHITE);
            
            PieData pieData = new PieData(dataSet);
            genreChart.setData(pieData);
            
            genreChart.getDescription().setEnabled(false);
            genreChart.setBackgroundColor(Color.parseColor("#0F172A"));
            genreChart.getLegend().setTextColor(Color.WHITE);
            genreChart.setEntryLabelColor(Color.WHITE);
            
            genreChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserId(int userId) {
        try {
            // Reload charts when user changes
            if (isAdded() && ratingChart != null && genreChart != null) {
                setupRatingChart();
                setupGenreChart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
