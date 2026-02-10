package com.azizul.assignment.individual.mygovcare;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences, themePreferences;
    private static final String USER_PREFS_NAME = "user_prefs";
    private static final String THEME_PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themePreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE);
        currentTheme = themePreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        setTheme(currentTheme);

        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);

        // Enable Edge-to-Edge with white icons
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        setContentView(R.layout.activity_settings);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- DrawerLayout Setup ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // --- Safe Header Initialization ---
        setupNavHeader();

        // --- ActionBarDrawerToggle Setup ---
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Spinner Setup ---
        Spinner spinnerColors = findViewById(R.id.spinner_colors);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.color_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColors.setAdapter(adapter);

        int currentThemeResId = themePreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        if (currentThemeResId == R.style.Theme_MyGovCare_Blue) {
            spinnerColors.setSelection(0);
        } else if (currentThemeResId == R.style.Theme_MyGovCare_Orange) {
            spinnerColors.setSelection(1);
        } else if (currentThemeResId == R.style.Theme_MyGovCare_Green) {
            spinnerColors.setSelection(2);
        }

        spinnerColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedTheme = 0;
                String selectedAlias = null;

                switch (position) {
                    case 0: // Blue
                        selectedTheme = R.style.Theme_MyGovCare_Blue;
                        selectedAlias = ".LoginActivityBlue";
                        break;
                    case 1: // Orange
                        selectedTheme = R.style.Theme_MyGovCare_Orange;
                        selectedAlias = ".LoginActivityOrange";
                        break;
                    case 2: // Green
                        selectedTheme = R.style.Theme_MyGovCare_Green;
                        selectedAlias = ".LoginActivityGreen";
                        break;
                }

                if (themePreferences.getInt(THEME_KEY, 0) != selectedTheme) {
                    SharedPreferences.Editor editor = themePreferences.edit();
                    editor.putInt(THEME_KEY, selectedTheme);
                    editor.apply();

                    if (selectedAlias != null) {
                        updateAppIcon(selectedAlias);
                    }

                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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

        navigationView.setCheckedItem(R.id.nav_settings);
    }

    private void setupNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            // Update Username
            TextView navUsername = headerView.findViewById(R.id.tv_nav_username);
            String username = sharedPreferences.getString(KEY_USERNAME, "User");
            navUsername.setText("Hello, " + username);

            // Update Logo
            updateNavHeaderLogo(currentTheme, headerView);

            // Setup Logout Button
            Button btnLogout = headerView.findViewById(R.id.btn_logout);
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> handleLogout());
            }
        }
    }

    private void handleLogout() {
        // Use clear() and commit() for a robust, synchronous logout
        sharedPreferences.edit().clear().commit();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateAppIcon(String selectedAlias) {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();

        // Disable all aliases
        pm.setComponentEnabledSetting(new ComponentName(packageName, packageName + ".LoginActivityBlue"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(packageName, packageName + ".LoginActivityOrange"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(packageName, packageName + ".LoginActivityGreen"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        // Enable the selected alias
        pm.setComponentEnabledSetting(new ComponentName(packageName, packageName + selectedAlias),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void updateNavHeaderLogo(int themeResId, View headerView) {
        ImageView ivNavHeaderLogo = headerView.findViewById(R.id.iv_nav_header_logo);
        if (themeResId == R.style.Theme_MyGovCare_Blue) {
            ivNavHeaderLogo.setImageResource(R.drawable.logo_blue);
        } else if (themeResId == R.style.Theme_MyGovCare_Orange) {
            ivNavHeaderLogo.setImageResource(R.drawable.logo_purple);
        } else if (themeResId == R.style.Theme_MyGovCare_Green) {
            ivNavHeaderLogo.setImageResource(R.drawable.logo_green);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int newTheme = themePreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        if (this.currentTheme != newTheme) {
            recreate();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            // Already here
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}