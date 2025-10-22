package com.example.notesportal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText loginInput, passwordInput;
    private Button loginBtn;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        loginInput = findViewById(R.id.inputLogin);
        passwordInput = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.loginProgress);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {
        String userInput = loginInput.getText().toString().trim();
        String userPass = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(userInput) || TextUtils.isEmpty(userPass)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        String hashedPass = hashPassword(userPass);
        String inputType = userInput.contains("@") ? "email" : "username";

        db.collection("users")
                .whereEqualTo(inputType, userInput)
                .get()
                .addOnSuccessListener(query -> {
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    if (query.isEmpty()) {
                        Toast.makeText(this, "No account found with this " + inputType, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : query) {
                        String storedPass = doc.getString("password");
                        String username = doc.getString("username");
                        String emailid =  doc.getString("email");
                        if (storedPass != null && storedPass.equals(hashedPass)) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            SessionManager sessionManager = new SessionManager(this);
                            sessionManager.SaveSession(username,emailid);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            return;
                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login error", e);
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA-256 not supported", e);
            return password;
        }
    }
}
