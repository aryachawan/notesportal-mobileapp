package com.example.notesportal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class LandingActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        sessionManager = new SessionManager(this);
        if(sessionManager.isLoggedIn()){
            startActivity(new Intent(LandingActivity.this, HomeActivity.class));
            finish();
        }

        // NOTESPORTAL DUAL COLOUR LOGO CODE
        TextView logo = findViewById(R.id.logo);
        String text = "notesportal";
        SpannableString ss = new SpannableString(text);
        // "notes" in white
        ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // "portal" in purple #583DA1
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7c5bf7")), 5, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(ss);

        // NAVBUTTONS
        Button signupBtn = findViewById(R.id.signupBtn);
        Button loginBtn = findViewById(R.id.loginBtn);

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
