package com.azizul.assignment.individual.mygovcare;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable Edge-to-Edge with white icons (dark status bar style)
        EdgeToEdge.enable(this, 
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        
        setContentView(R.layout.activity_main);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- DrawerLayout Setup ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // --- ActionBarDrawerToggle Setup ---
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Manual Inset Handling for Styling and Padding ---
        View mainContent = findViewById(R.id.main_content);
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // 1. Main Content: Apply side and bottom padding to clear nav bar
            mainContent.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);

            // 2. Toolbar: Apply top padding to push content below status bar while styling it
            toolbar.setPadding(toolbar.getPaddingLeft(), systemBars.top, 
                               toolbar.getPaddingRight(), toolbar.getPaddingBottom());

            // 3. Side Menu (NavigationView)
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

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
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
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About Us selected", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}