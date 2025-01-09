package com.example.a;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    BottomNavigationView bottomnavinhome;
    FirebaseAuth auth;
    TextView userwelcome_inhome;
    RecyclerView liked_recycler,visited_recycler;
    LikedPlacesAdapter likedPlacesAdapter;
    VisitedPlacesAdapter visitedPlacesAdapter;
    List<LikedPlace> likedPlaces;
    List<VisitedPlace> visitedPlaces;
    private AlertDialog dialog;
    TextView empty_text,empty_visit_text;
    ImageView empty_view,empty_visit_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        bottomnavinhome = findViewById(R.id.bottomnavinhome);
        userwelcome_inhome = findViewById(R.id.userwelcome_inhome);
        liked_recycler = findViewById(R.id.liked_recycler);
        visited_recycler = findViewById(R.id.visited_recycler);
        empty_text = findViewById(R.id.empty_like_text);
        empty_visit_text = findViewById(R.id.empty_visit_text);
        empty_view = findViewById(R.id.empty_like_view);
        empty_visit_view = findViewById(R.id.empty_visit_view);

        visitedPlaces = new ArrayList<>();
        visitedPlacesAdapter = new VisitedPlacesAdapter(this, visitedPlaces);
        visited_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        visited_recycler.setAdapter(visitedPlacesAdapter);

        likedPlaces = new ArrayList<>();
        likedPlacesAdapter = new LikedPlacesAdapter(this, likedPlaces);
        liked_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        liked_recycler.setAdapter(likedPlacesAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), logsignin.class);
            startActivity(intent);
            finish();
        } else {
            fetchUserData(user);
            fetchLikedPlaces(user.getUid());
            fetchVisitedPlaces(user.getUid());
        }

        setupBottomNavigation();
    }

    private void fetchVisitedPlaces(String userId){

        showProgressDialog();

        DatabaseReference userVisitsRef = FirebaseDatabase.getInstance()
                .getReference("Users_Visits")
                .child(userId);


        userVisitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    visitedPlaces.clear();

                    for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                        String placeId = placeSnapshot.getKey();
                        Boolean isVisited = placeSnapshot.getValue(Boolean.class);

                        if (isVisited != null && isVisited) {
                            fetchVisitedPlaceDetails(placeId);
                        }
                    }
                } else {
                    empty_visit_text.setVisibility(View.VISIBLE);
                    empty_visit_view.setVisibility(View.VISIBLE);
                    visited_recycler.setVisibility(View.GONE);
                    //Toast.makeText(Homepage.this, "No Visited places found", Toast.LENGTH_SHORT).show();
                }

                dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dismissProgressDialog();
                Toast.makeText(Homepage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fetchVisitedPlaceDetails(String placeId){

        DatabaseReference placeRef = FirebaseDatabase.getInstance()
                .getReference("historical_places")
                .child(placeId);

        placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String imageUrl = snapshot.child("iconHome").getValue(String.class);

                    if (name != null && imageUrl != null) {
                        visitedPlaces.add(new VisitedPlace(placeId, name, imageUrl));
                        visitedPlacesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Homepage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchUserData(FirebaseUser user) {
        String userId = user.getUid();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("name").getValue(String.class);
                            userwelcome_inhome.setText("Hello " + name + "!!");
                        } else {
                            Toast.makeText(Homepage.this, "Unable to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Homepage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchLikedPlaces(String userId) {
        showProgressDialog();

        DatabaseReference userLikesRef = FirebaseDatabase.getInstance()
                .getReference("Users_Likes")
                .child(userId);

        userLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likedPlaces.clear();

                    for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                        String placeId = placeSnapshot.getKey();
                        Boolean isLiked = placeSnapshot.getValue(Boolean.class);

                        if (isLiked != null && isLiked) {
                            fetchLikedPlaceDetails(placeId);
                        }
                    }
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    empty_view.setVisibility(View.VISIBLE);
                    liked_recycler.setVisibility(View.GONE);
                    //Toast.makeText(Homepage.this, "No liked places found", Toast.LENGTH_SHORT).show();
                }

                dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dismissProgressDialog();
                Toast.makeText(Homepage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLikedPlaceDetails(String placeId) {
        DatabaseReference placeRef = FirebaseDatabase.getInstance()
                .getReference("historical_places")
                .child(placeId);

        placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String imageUrl = snapshot.child("iconHome").getValue(String.class);

                    if (name != null && imageUrl != null) {
                        likedPlaces.add(new LikedPlace(placeId, name, imageUrl));
                        likedPlacesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Homepage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomnavinhome.setSelectedItemId(R.id.Home);

        bottomnavinhome.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.Map) {
                    Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent1);
                    finish();

                } else if (itemId == R.id.Profile) {
                    Intent intent = new Intent(getApplicationContext(), profileactivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }

    private void showProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // Avoid duplicates
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout); // Reference your custom progress layout
        dialog = builder.create();
        dialog.show();
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
