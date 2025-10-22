package com.example.notesportal;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText firstname, lastname, username, email, password, confirmPassword;
    private Button btnSignup;
    private FirebaseFirestore db;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        if (db == null) {
            Log.e(TAG, "Firestore instance is null!");
        } else {
            Log.d(TAG, "Firestore initialized successfully");
        }

        // Bind views
        firstname = findViewById(R.id.inputFirstname);
        lastname = findViewById(R.id.inputLastname);
        username = findViewById(R.id.inputUsername);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);

        // Safe insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_main), (v, insets) -> {
            if (v != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        btnSignup.setOnClickListener(v -> validateAndSignup());
    }

    private void validateAndSignup() {
        String first = firstname.getText().toString().trim();
        String last = lastname.getText().toString().trim();
        String user = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirm = confirmPassword.getText().toString().trim();

        // Basic validations
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) || TextUtils.isEmpty(user) ||
                TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (first.matches(".*\\d.*") || last.matches(".*\\d.*")) {
            Toast.makeText(this, "Name cannot contain numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for existing email
        db.collection("users")
                .whereEqualTo("email", mail)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Email already exists: " + mail);
                    } else {
                        createUser(first, last, user, mail, pass);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking user", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error checking existing email", e);
                });
    }

    private void createUser(String first, String last, String user, String mail, String pass) {
        String hashed = hashPassword(pass);

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstname", first);
        userData.put("lastname", last);
        userData.put("username", user);
        userData.put("email", mail);
        userData.put("password", hashed);
        userData.put("subjects", Collections.emptyList());
        userData.put("favs", Collections.emptyList());
        userData.put("uploads", Collections.emptyList());

        db.collection("users").add(userData)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User added with ID: " + ref.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating user", e);
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
