package com.azizul.assignment.individual.mygovcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

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

        RadioGroup rgAuthType = findViewById(R.id.rg_auth_type);
        LinearLayout llLoginSection = findViewById(R.id.ll_login_section);
        LinearLayout llSignupSection = findViewById(R.id.ll_signup_section);

        rgAuthType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_login) {
                llLoginSection.setVisibility(View.VISIBLE);
                llSignupSection.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_signup) {
                llLoginSection.setVisibility(View.GONE);
                llSignupSection.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }
}