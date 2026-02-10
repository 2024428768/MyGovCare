package com.azizul.assignment.individual.mygovcare;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // URLs for the PHP scripts
    private static final String SIGNUP_URL = "http://10.0.2.2/MyGovCare/signup.php";
    private static final String LOGIN_URL = "http://10.0.2.2/MyGovCare/login.php";

    // SharedPreferences keys
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "user_prefs";
    private static final String THEME_PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REMEMBER_ME = "remember_me";

    // UI Elements
    private Button btnLoginTab, btnSignupTab, btnLogin, btnSignup;
    private LinearLayout llLoginSection, llSignupSection;
    private EditText etLoginUsername, etLoginPassword, etSignupUsername, etSignupPassword, etSignupVerify;
    private CheckBox cbRememberMe;
    private TextView errorBannerText;
    private ImageView ivLogo;
    private View errorBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePrefs = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE);
        int themeResId = themePrefs.getInt(THEME_KEY, R.style.Theme_MyGovCare);
        setTheme(themeResId);

        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Auto-login check
        if (sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Skip the rest of onCreate
        }

        // Enable Edge-to-Edge with white icons
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        setContentView(R.layout.activity_login);

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        updateLogo(themeResId);

        // Set initial state and listeners
        setupListeners();
        llLoginSection.setVisibility(View.VISIBLE);
        llSignupSection.setVisibility(View.GONE);
        updateButtonStyles(true);
    }

    private void initViews() {
        ivLogo = findViewById(R.id.iv_logo);
        btnLoginTab = findViewById(R.id.btn_login_tab);
        btnSignupTab = findViewById(R.id.btn_signup_tab);
        llLoginSection = findViewById(R.id.ll_login_section);
        llSignupSection = findViewById(R.id.ll_signup_section);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        etLoginUsername = findViewById(R.id.et_login_username);
        etLoginPassword = findViewById(R.id.et_login_password);
        etSignupUsername = findViewById(R.id.et_signup_username);
        etSignupPassword = findViewById(R.id.et_signup_password);
        etSignupVerify = findViewById(R.id.et_signup_verify);
        cbRememberMe = findViewById(R.id.cb_remember_me);

        // Correctly find the banner view and the text view within it
        errorBanner = findViewById(R.id.error_banner);
        errorBannerText = findViewById(R.id.error_banner); // Both variables point to the same TextView using the correct ID
    }

    private void setupListeners() {
        btnLoginTab.setOnClickListener(v -> showLoginSection());
        btnSignupTab.setOnClickListener(v -> showSignupSection());
        btnLogin.setOnClickListener(v -> handleLogin());
        btnSignup.setOnClickListener(v -> handleSignup());
    }

    private void handleLogin() {
        final String username = etLoginUsername.getText().toString().trim();
        final String password = etLoginPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorBanner("Username and password cannot be empty.");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            int userId = jsonObject.getInt("user_id");
                            String user = jsonObject.getString("username");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(KEY_USER_ID, userId);
                            editor.putString(KEY_USERNAME, user);
                            editor.putBoolean(KEY_REMEMBER_ME, cbRememberMe.isChecked());
                            editor.commit(); // Use commit for synchronous save before navigating

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }, 4000);

                        } else {
                            String message = jsonObject.getString("message");
                            showErrorBanner(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorBanner("An error occurred. Please try again.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    showErrorBanner("Could not connect to the server.");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleSignup() {
        final String username = etSignupUsername.getText().toString().trim();
        final String password = etSignupPassword.getText().toString().trim();
        final String verifyPassword = etSignupVerify.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || verifyPassword.isEmpty()) {
            showSignupPopup("All fields are required to be filled.", false);
            return;
        }

        if (!password.equals(verifyPassword)) {
            showSignupPopup("Passwords do not match.", false);
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGNUP_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        showSignupPopup(message, status.equals("success"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSignupPopup("An error occurred.", false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    showSignupPopup("Could not connect to the server.", false);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void showErrorBanner(String message) {
        errorBannerText.setText(message);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        errorBanner.startAnimation(fadeIn);
        errorBanner.setVisibility(View.VISIBLE);

        // Click to dismiss
        errorBanner.setOnClickListener(v -> hideErrorBanner());

        // Auto-dismiss after 4 seconds
        new Handler(Looper.getMainLooper()).postDelayed(this::hideErrorBanner, 4000);
    }

    private void hideErrorBanner() {
        if (errorBanner.getVisibility() == View.VISIBLE) {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            errorBanner.startAnimation(fadeOut);
            errorBanner.setVisibility(View.GONE);
        }
    }

    private void showSignupPopup(String message, final boolean isSuccess) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Set the dialog size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.75f);
        int dialogWindowHeight = (int) (displayHeight * 0.45f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        dialog.getWindow().setAttributes(layoutParams);

        TextView messageText = dialog.findViewById(R.id.popup_message_text);
        Button closeButton = dialog.findViewById(R.id.popup_close_button);

        messageText.setText(message);

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (isSuccess) {
                showLoginSection();
            }
        });

        dialog.show();
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
        if (llLoginSection.getVisibility() == View.VISIBLE) return;
        updateButtonStyles(true);
        switchViews(llSignupSection, llLoginSection);
    }

    private void showSignupSection() {
        if (llSignupSection.getVisibility() == View.VISIBLE) return;
        updateButtonStyles(false);
        switchViews(llLoginSection, llSignupSection);
    }

    private void switchViews(final View viewToFadeOut, final View viewToFadeIn) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                viewToFadeOut.setVisibility(View.GONE);
                Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                viewToFadeIn.startAnimation(fadeIn);
                viewToFadeIn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        viewToFadeOut.startAnimation(fadeOut);
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