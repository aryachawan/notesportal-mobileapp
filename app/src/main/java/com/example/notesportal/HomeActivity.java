package com.example.notesportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private TextView tempString;
    private Button logoutBtn;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(HomeActivity.this);
        if(!sessionManager.isLoggedIn()){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            finish();
            return;
        }

        tempString = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);

        String username = sessionManager.getUsername();
        tempString.setText("Welcome "+username);

        logoutBtn.setOnClickListener(v->{
            sessionManager.DestroySession();
            Toast.makeText(this,"Logout Successfull",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            finish();
        });

    }
}
