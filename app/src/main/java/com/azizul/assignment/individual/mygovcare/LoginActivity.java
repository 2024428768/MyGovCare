package com.azizul.assignment.individual.mygovcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private Button btnLoginTab;
    private Button btnSignupTab;
    private LinearLayout llLoginSection;
    private LinearLayout llSignupSection;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeResId = sharedPreferences.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        setTheme(themeResId);

        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge with white icons (dark status bar style)
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        setContentView(R.layout.activity_login);

        // Apply insets to the root view so content stays within safe areas
        // while the background (brand_blue) spans the whole screen.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivLogo = findViewById(R.id.iv_logo);
        btnLoginTab = findViewById(R.id.btn_login_tab);
        btnSignupTab = findViewById(R.id.btn_signup_tab);
        llLoginSection = findViewById(R.id.ll_login_section);
        llSignupSection = findViewById(R.id.ll_signup_section);

        updateLogo(themeResId);

        btnLoginTab.setOnClickListener(v -> showLoginSection());

        btnSignupTab.setOnClickListener(v -> showSignupSection());

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Set initial state
        llLoginSection.setVisibility(View.VISIBLE);
        llSignupSection.setVisibility(View.GONE);
        updateButtonStyles(true);
    }

    private void updateLogo(int themeResId) {
        if (themeResId == R.style.Theme_MyGovCare_Blue) {
            ivLogo.setImageResource(R.drawable.logo_blue);
        } else if (themeResId == R.style.Theme_MyGovCare_Orange) {
            ivLogo.setImageResource(R.drawable.logo_purple);
        } else if (themeResId == R.style.Theme_MyGovCare_Green) {
            ivLogo.setImageResource(R.drawable.logo_green);
        }
    }

    private void showLoginSection() {
        if (llLoginSection.getVisibility() == View.VISIBLE) {
            return;
        }
        updateButtonStyles(true);

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                llSignupSection.setVisibility(View.GONE);
                Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                llLoginSection.startAnimation(fadeIn);
                llLoginSection.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        llSignupSection.startAnimation(fadeOut);
    }

    private void showSignupSection() {
        if (llSignupSection.getVisibility() == View.VISIBLE) {
            return;
        }
        updateButtonStyles(false);

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                llLoginSection.setVisibility(View.GONE);
                Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                llSignupSection.startAnimation(fadeIn);
                llSignupSection.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        llLoginSection.startAnimation(fadeOut);
    }

    private void updateButtonStyles(boolean isLoginActive) {
        if (isLoginActive) {
            btnLoginTab.setBackgroundResource(R.drawable.button_active);
            btnLoginTab.setTextColor(Color.WHITE);
            btnLoginTab.setActivated(true);

            btnSignupTab.setBackgroundResource(R.drawable.button_inactive);
            btnSignupTab.setTextColor(Color.BLACK);
            btnSignupTab.setActivated(false);
        } else {
            btnSignupTab.setBackgroundResource(R.drawable.button_active);
            btnSignupTab.setTextColor(Color.WHITE);
            btnSignupTab.setActivated(true);

            btnLoginTab.setBackgroundResource(R.drawable.button_inactive);
            btnLoginTab.setTextColor(Color.BLACK);
            btnLoginTab.setActivated(false);
        }
    }
}
