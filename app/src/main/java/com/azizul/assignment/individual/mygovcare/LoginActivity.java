package com.azizul.assignment.individual.mygovcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private Button btnLoginTab;
    private Button btnSignupTab;
    private LinearLayout llLoginSection;
    private LinearLayout llSignupSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        btnLoginTab = findViewById(R.id.btn_login_tab);
        btnSignupTab = findViewById(R.id.btn_signup_tab);
        llLoginSection = findViewById(R.id.ll_login_section);
        llSignupSection = findViewById(R.id.ll_signup_section);

        btnLoginTab.setOnClickListener(v -> showLoginSection());

        btnSignupTab.setOnClickListener(v -> showSignupSection());

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Set initial state
        showLoginSection();
    }

    private void showLoginSection() {
        llLoginSection.setVisibility(View.VISIBLE);
        llSignupSection.setVisibility(View.GONE);

        btnLoginTab.setBackgroundResource(R.drawable.button_active);
        btnLoginTab.setTextColor(Color.WHITE);
        btnLoginTab.setActivated(true);

        btnSignupTab.setBackgroundResource(R.drawable.button_inactive);
        btnSignupTab.setTextColor(Color.BLACK);
        btnSignupTab.setActivated(false);
    }

    private void showSignupSection() {
        llLoginSection.setVisibility(View.GONE);
        llSignupSection.setVisibility(View.VISIBLE);

        btnSignupTab.setBackgroundResource(R.drawable.button_active);
        btnSignupTab.setTextColor(Color.WHITE);
        btnSignupTab.setActivated(true);

        btnLoginTab.setBackgroundResource(R.drawable.button_inactive);
        btnLoginTab.setTextColor(Color.BLACK);
        btnLoginTab.setActivated(false);
    }
}