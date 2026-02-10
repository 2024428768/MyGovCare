package com.azizul.assignment.individual.mygovcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class AboutUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private ImageView ivLogo2;

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

        setContentView(R.layout.activity_about_us);

        ivLogo2 = findViewById(R.id.iv_logo2);
        updateLogo(currentTheme);

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

        // --- Manual Inset Handling for Styling and Padding ---
        View mainContent = findViewById(R.id.main_content);
        View toolbarContainer = findViewById(R.id.toolbar_container);
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // 1. Main Content: Apply side and bottom padding
            mainContent.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);

            // 2. Toolbar Container: Apply top padding
            toolbarContainer.setPadding(0, systemBars.top, 0, 0);

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

        navigationView.setCheckedItem(R.id.nav_about);
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

        Intent intent = new Intent(AboutUsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateLogo(int themeResId) {
        if (themeResId == R.style.Theme_MyGovCare_Blue) {
            ivLogo2.setImageResource(R.drawable.logo_blue);
        } else if (themeResId == R.style.Theme_MyGovCare_Orange) {
            ivLogo2.setImageResource(R.drawable.logo_purple);
        } else if (themeResId == R.style.Theme_MyGovCare_Green) {
            ivLogo2.setImageResource(R.drawable.logo_green);
        }
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
            startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AboutUsActivity.this, SettingsActivity.class));
            finish();
        } else if (id == R.id.nav_about) {
            // Already here
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}