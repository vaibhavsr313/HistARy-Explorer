package com.example.a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class logsignin extends AppCompatActivity {

    Button signininlogsignin;
    TextView logininlogsignin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logsignin);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // UI components
        signininlogsignin = findViewById(R.id.signininlogsignin);
        logininlogsignin = findViewById(R.id.logininlogsignin);

        // Button click listeners
        signininlogsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin bottomSheet = new signin();
                bottomSheet.show(getSupportFragmentManager(), "SignIn");
            }
        });

        logininlogsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login bottomSheet = new login();
                bottomSheet.show(getSupportFragmentManager(), "LogIn");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to homepage
            Intent intent = new Intent(this, Homepage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
