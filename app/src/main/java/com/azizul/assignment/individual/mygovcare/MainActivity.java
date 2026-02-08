package com.azizul.assignment.individual.mygovcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentTheme = sharedPreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        setTheme(currentTheme);

        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge with white icons
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        setContentView(R.layout.activity_main);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- DrawerLayout Setup ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // --- ActionBarDrawerToggle Setup ---
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Manual Inset Handling for Styling and Padding ---
        View mainContent = findViewById(R.id.main_content);
        View toolbarContainer = findViewById(R.id.toolbar_container);
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            mainContent.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            toolbarContainer.setPadding(0, systemBars.top, 0, 0);
            if (navigationView.getHeaderCount() > 0) {
                View header = navigationView.getHeaderView(0);
                float density = getResources().getDisplayMetrics().density;
                int padding16 = (int) (16 * density);
                header.setPadding(padding16, systemBars.top + padding16, padding16, padding16);
            }
            navigationView.setPadding(navigationView.getPaddingLeft(), navigationView.getPaddingTop(),
                    navigationView.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // --- Back Press Handling ---
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // --- Map Initialization ---
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure Home is selected when returning to this activity
        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
        int newTheme = sharedPreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        if (this.currentTheme != newTheme) {
            recreate();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng kl = new LatLng(3.1390, 101.6869);
        mMap.addMarker(new MarkerOptions().position(kl).title("Marker in Kuala Lumpur"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kl, 10f));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already here
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
