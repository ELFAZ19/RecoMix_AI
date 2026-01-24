package com.cs218ai.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.cs218ai.myapplication.adapters.ViewPagerAdapter;
import com.cs218ai.myapplication.fragments.TopPicksFragment;
import com.cs218ai.myapplication.fragments.WatchHistoryFragment;
import com.cs218ai.myapplication.fragments.InsightsFragment;
import com.cs218ai.myapplication.fragments.WatchlistFragment;
import com.cs218ai.myapplication.models.Movie;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TopPicksFragment.OnWatchlistUpdateListener {
    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView moviesRatedValue, avgRatingValue, favGenreValue;
    private TextInputEditText userIdInput;
    private Slider recommendationsSlider, minYearSlider;
    private TextView recommendationsLabel, minYearLabel;

    private int currentUserId = 1;
    private int nRecommendations = 12;
    private int minYear = 1980;

    private ViewPagerAdapter viewPagerAdapter;
    private TopPicksFragment topPicksFragment;
    private WatchHistoryFragment watchHistoryFragment;
    private InsightsFragment insightsFragment;
    private WatchlistFragment watchlistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Enable edge-to-edge (may fail on older devices, so wrap in try-catch)
            try {
        EdgeToEdge.enable(this);
            } catch (Exception e) {
                e.printStackTrace();
                // Continue without edge-to-edge if it fails
            }
            
        setContentView(R.layout.activity_main);

            View drawerLayoutView = findViewById(R.id.drawerLayout);
            if (drawerLayoutView != null) {
                try {
                    ViewCompat.setOnApplyWindowInsetsListener(drawerLayoutView, (v, insets) -> {
                        try {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            return insets;
        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Setup components one by one with individual error handling
            setupToolbar();
            setupStats();
            setupViewPager();
            setupDrawer();
            updateUserStats();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error message to user
            try {
                android.widget.Toast.makeText(this, "Error initializing app. Please restart.", android.widget.Toast.LENGTH_LONG).show();
            } catch (Exception toastException) {
                e.printStackTrace();
            }
        }
    }

    private void setupToolbar() {
        try {
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> {
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupStats() {
        try {
            moviesRatedValue = findViewById(R.id.moviesRatedValue);
            avgRatingValue = findViewById(R.id.avgRatingValue);
            favGenreValue = findViewById(R.id.favGenreValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager() {
        try {
            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);

            if (viewPager == null || tabLayout == null) {
                return;
            }

            viewPagerAdapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(viewPagerAdapter);

            try {
                TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    try {
                        switch (position) {
                            case 0:
                                tab.setText(getString(R.string.top_picks));
                                break;
                            case 1:
                                tab.setText(getString(R.string.watch_history));
                                break;
                            case 2:
                                tab.setText(getString(R.string.insights));
                                break;
                            case 3:
                                tab.setText(getString(R.string.watchlist));
                                break;
                            default:
                                tab.setText("Tab " + (position + 1));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Fallback to default text
                        try {
                            tab.setText("Tab " + (position + 1));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                });
                mediator.attach();
            } catch (Exception e) {
                e.printStackTrace();
                // Continue without tabs if mediator fails
            }

            // Get fragments after ViewPager creates them - use a delay to ensure fragments are created
            try {
                viewPager.postDelayed(() -> {
                    try {
                        if (viewPager != null && viewPagerAdapter != null) {
                            updateFragmentReferences();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 100);
            } catch (Exception e) {
                e.printStackTrace();
                // Continue without delayed fragment reference update
            }

            // Also update when page changes to ensure fragments are available
            try {
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        try {
                            updateFragmentReferences();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Continue without page change callback if registration fails
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFragmentReferences() {
        // Update fragment references from adapter
        try {
            if (viewPagerAdapter != null && viewPager != null) {
                // Try to get fragments from adapter
                TopPicksFragment top = viewPagerAdapter.getTopPicksFragment();
                if (top != null && top.isAdded() && top.getView() != null) {
                    topPicksFragment = top;
                }
                
                WatchHistoryFragment history = viewPagerAdapter.getWatchHistoryFragment();
                if (history != null && history.isAdded() && history.getView() != null) {
                    watchHistoryFragment = history;
                }
                
                InsightsFragment insights = viewPagerAdapter.getInsightsFragment();
                if (insights != null && insights.isAdded() && insights.getView() != null) {
                    insightsFragment = insights;
                }
                
                WatchlistFragment watchlist = viewPagerAdapter.getWatchlistFragment();
                if (watchlist != null && watchlist.isAdded() && watchlist.getView() != null) {
                    watchlistFragment = watchlist;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDrawer() {
        try {
            drawerLayout = findViewById(R.id.drawerLayout);
            NavigationView navigationView = findViewById(R.id.navigationView);

            if (navigationView == null || drawerLayout == null) {
                return;
            }

            // Inflate drawer content and add to NavigationView
            View drawerContent = getLayoutInflater().inflate(R.layout.drawer_content, null);
            if (drawerContent == null) {
                return;
            }

            // Get the NavigationView's content container (first child)
            ViewGroup navView = null;
            try {
                View firstChild = navigationView.getChildAt(0);
                if (firstChild instanceof ViewGroup) {
                    navView = (ViewGroup) firstChild;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (navView == null) {
                return;
            }

            // Check if drawer content is already added
            boolean alreadyAdded = false;
            try {
                for (int i = 0; i < navView.getChildCount(); i++) {
                    View child = navView.getChildAt(i);
                    if (child != null && child == drawerContent) {
                        alreadyAdded = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!alreadyAdded) {
                    navView.addView(drawerContent);
                } else {
                    // If already added, find it in the view hierarchy
                    int childCount = navView.getChildCount();
                    if (childCount > 0) {
                        drawerContent = navView.getChildAt(childCount - 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return; // Exit if we can't add the drawer content
            }

            // Find views in drawer content - wrap in try-catch in case drawerContent is null
            if (drawerContent == null) {
                return;
            }
            
            try {
                userIdInput = drawerContent.findViewById(R.id.userIdInput);
                recommendationsSlider = drawerContent.findViewById(R.id.recommendationsSlider);
                minYearSlider = drawerContent.findViewById(R.id.minYearSlider);
                recommendationsLabel = drawerContent.findViewById(R.id.recommendationsLabel);
                minYearLabel = drawerContent.findViewById(R.id.minYearLabel);

                // Initialize views safely
                if (userIdInput != null) {
                    try {
                        userIdInput.setText(String.valueOf(currentUserId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (recommendationsLabel != null) {
                    try {
                        recommendationsLabel.setText("Recommendations: " + nRecommendations);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (minYearLabel != null) {
                    try {
                        minYearLabel.setText("Min Release Year: " + minYear);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return; // Exit if we can't find views
            }

            if (recommendationsSlider != null && recommendationsLabel != null) {
                recommendationsSlider.addOnChangeListener((slider, value, fromUser) -> {
                    try {
                        nRecommendations = (int) value;
                        if (recommendationsLabel != null) {
                            recommendationsLabel.setText("Recommendations: " + nRecommendations);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (minYearSlider != null && minYearLabel != null) {
                minYearSlider.addOnChangeListener((slider, value, fromUser) -> {
                    try {
                        minYear = (int) value;
                        if (minYearLabel != null) {
                            minYearLabel.setText("Min Release Year: " + minYear);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            View randomButton = drawerContent.findViewById(R.id.randomUserButton);
            if (randomButton != null) {
                randomButton.setOnClickListener(v -> {
                    try {
                        Random random = new Random();
                        currentUserId = random.nextInt(943) + 1;
                        if (userIdInput != null) {
                            userIdInput.setText(String.valueOf(currentUserId));
                        }
                        updateUserStats();
                        updateFragments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            // Update user ID when input changes
            if (userIdInput != null) {
                userIdInput.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        try {
                            String text = userIdInput.getText() != null ? userIdInput.getText().toString() : "";
                            if (!text.isEmpty()) {
                                int newUserId = Integer.parseInt(text);
                                if (newUserId >= 1 && newUserId <= 943) {
                                    currentUserId = newUserId;
                                    updateUserStats();
                                    updateFragments();
                                } else {
                                    userIdInput.setText(String.valueOf(currentUserId));
                                }
                            }
                        } catch (NumberFormatException e) {
                            if (userIdInput != null) {
                                userIdInput.setText(String.valueOf(currentUserId));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserStats() {
        try {
            // TODO: Replace with actual API call
            // Mock data for now
            Random random = new Random();
            int moviesRated = 50 + random.nextInt(200);
            double avgRating = 3.0 + random.nextDouble() * 2.0;
            String[] genres = {"Action", "Comedy", "Drama", "Romance", "SciFi", "Thriller", "Horror"};
            String favGenre = genres[random.nextInt(genres.length)];

            if (moviesRatedValue != null) {
                moviesRatedValue.setText(String.valueOf(moviesRated));
            }
            if (avgRatingValue != null) {
                avgRatingValue.setText(String.format("%.1f ⭐", avgRating));
            }
            if (favGenreValue != null) {
                favGenreValue.setText(favGenre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFragments() {
        try {
            if (topPicksFragment != null && topPicksFragment.isAdded()) {
                topPicksFragment.setUserId(currentUserId);
            }
            if (watchHistoryFragment != null && watchHistoryFragment.isAdded()) {
                watchHistoryFragment.setUserId(currentUserId);
            }
            if (insightsFragment != null && insightsFragment.isAdded()) {
                insightsFragment.setUserId(currentUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAddToWatchlist(Movie movie) {
        try {
            // Find watchlist fragment and add movie
            if (watchlistFragment == null || !watchlistFragment.isAdded()) {
                updateFragmentReferences();
            }
            if (watchlistFragment != null && watchlistFragment.isAdded()) {
                watchlistFragment.addToWatchlist(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
}
