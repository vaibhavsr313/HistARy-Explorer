package com.example.a;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class profileactivity extends AppCompatActivity {

    Button logoutinprofile;
    FirebaseAuth auth;
    BottomNavigationView bottomnavinprofile;
    DatabaseReference databaseReference;
    TextView Nameinprofile;
    TextView emailinprofile;
    private AlertDialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        logoutinprofile = findViewById(R.id.logoutinprofile);
        bottomnavinprofile = findViewById(R.id.bottomnavinprofile);
        emailinprofile = findViewById(R.id.emailinprofile);
        Nameinprofile = findViewById(R.id.Nameinprofile);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        showProgressDialog(); // Show the loading dialog before fetching data

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch data
                    String name = snapshot.child("name").getValue(String.class);

                    // Update UI
                    Nameinprofile.setText(name);
                    emailinprofile.setText(user.getEmail());
                } else {
                    Toast.makeText(getApplicationContext(), "No user data found", Toast.LENGTH_SHORT).show();
                }

                dismissProgressDialog(); // Hide the loading dialog
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dismissProgressDialog(); // Hide the loading dialog even in case of failure
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        logoutinprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(getApplicationContext(),logsignin.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });

        bottomnavinprofile.setSelectedItemId(R.id.Profile);

        bottomnavinprofile.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.Map) {
                    Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent1);
                    finish();
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (itemId == R.id.Home) {
                    Intent i = new Intent(getApplicationContext(), Homepage.class);
                    startActivity(i);
                    finish();
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }
                return true;
            }
        });

    }

    private void showProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // Dismiss any existing dialog to avoid duplicates
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout); // Reference your progress layout
        dialog = builder.create();
        dialog.show();
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}