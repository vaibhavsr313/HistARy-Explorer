package com.example.a;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ola.mapsdk.camera.MapControlSettings;
import com.ola.mapsdk.interfaces.MarkerEventListener;
import com.ola.mapsdk.interfaces.OlaMapCallback;
import com.ola.mapsdk.listeners.OlaMapsListenerManager;
import com.ola.mapsdk.model.MarkerView;
import com.ola.mapsdk.model.OlaLatLng;
import com.ola.mapsdk.model.OlaMarkerOptions;
import com.ola.mapsdk.view.Marker;
import com.ola.mapsdk.view.OlaMap;
import com.ola.mapsdk.view.OlaMapView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    OlaMapView olaMapView;
    BottomNavigationView bottomnavinmap;
    FloatingActionButton mylocationfloatingbutton;
    OlaMap olaMap2;
    FusedLocationProviderClient fusedLocationClient;
    private static final float GEOFENCE_RADIUS = 100.0f;
    private com.google.android.gms.location.LocationCallback locationCallback;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private boolean isUserInteractingWithMap = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        olaMapView = findViewById(R.id.mapView);
        bottomnavinmap = findViewById(R.id.bottomnavinmap);
        mylocationfloatingbutton = findViewById(R.id.mylocationfloatingbutton);




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permissions at runtime if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mylocationfloatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        MapControlSettings controlSettings = new MapControlSettings.Builder()
                .setZoomGesturesEnabled(true)        // Enable zoom gestures
                .setTiltGesturesEnabled(true)        // Enable tilt gestures
                .setScrollGesturesEnabled(true)      // Enable scroll gestures
                .setRotateGesturesEnabled(true)      // Enable rotate gestures
                .setCompassEnabled(true)             // Enable compass
                .setDoubleTapGesturesEnabled(true)   // Enable double-tap gestures
                .build();

        olaMapView.getMap("sMqJpfGQSpPg82LEbAr3jQN3wlVtkExTCQvXglZo", new OlaMapCallback() {
            @Override
            public void onMapReady(OlaMap olaMap) {
                olaMap2 = olaMap;

                olaMap2.setOnOlaMapsCameraMoveListener(() -> {
                    isUserInteractingWithMap = true; // User starts moving the camera
                });

                // Add listener for camera idle state
                olaMap2.setOnOlaMapsCameraIdleListener(() -> {
                    isUserInteractingWithMap = false; // User stops moving the camera
                });

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("historical_places");

// Fetch places from Firebase
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                            // Extract data
                            String name = placeSnapshot.child("name").getValue(String.class);
                            Double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = placeSnapshot.child("longitude").getValue(Double.class);
                            String imageUrl = placeSnapshot.child("markerIcon").getValue(String.class); // Image URL

                            // Check for null values
                            if (latitude != null && longitude != null && imageUrl != null) {
                                // Create marker position
                                OlaLatLng markerPosition = new OlaLatLng(latitude, longitude, 0.0);

                                // Load the image from Firebase Storage
                                Glide.with(MapActivity.this)
                                        .asBitmap()
                                        .load(imageUrl) // The image URL from Firebase Storage
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                // Once the image is loaded, create the marker with the image as the icon

                                                int width = 100; // Set the desired width for your marker icon
                                                int height = 100; // Set the desired height for your marker icon
                                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(resource, width, height, false);


                                                OlaMarkerOptions markerOptions1 = new OlaMarkerOptions.Builder()
                                                        .setMarkerId(name)
                                                        .setPosition(markerPosition)
                                                        .setIsIconClickable(true)
                                                        .setSnippet(name)
                                                        .setIconRotation(0f)
                                                        .setIsAnimationEnable(true)
                                                        .setIsInfoWindowDismissOnClick(true)
                                                        .setIconBitmap(resizedBitmap) // Set the bitmap as the marker icon
                                                        .build();

                                                Marker marker1 = olaMap.addMarker(markerOptions1);

                                                // Optional: Set a listener for the marker click
                                                olaMap2.setMarkerListener(new MarkerEventListener() {
                                                    @Override
                                                    public void onMarkerClicked(@NonNull String markerId) {
                                                        // Iterate over all places to find the clicked marker
                                                        for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                                                            String placeName = placeSnapshot.child("name").getValue(String.class);
                                                            if (placeName != null && placeName.equals(markerId)) {
                                                                // Show the BottomSheet for the clicked place
                                                                showBottomSheetFragment(placeSnapshot);
                                                                break; // Exit loop after finding the correct place
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                Toast.makeText(MapActivity.this, "Error Loading Marker Image", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(MapActivity.this, latitude+" "+longitude+" "+imageUrl, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MapActivity.this, "Error loading places: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onMapError(String error) {
                Toast.makeText(MapActivity.this, "Error while loading map: " + error, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                startActivity(intent);
                finish();
                olaMap2 = null; // Prevent further map usage
            }
        }, controlSettings);

        bottomnavinmap.setSelectedItemId(R.id.Map);

        bottomnavinmap.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.Home) {
                    Intent intent1 = new Intent(getApplicationContext(), Homepage.class);
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


        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(5000); // Update every 5 seconds
        locationRequest.setFastestInterval(2000); // Fastest update every 2 seconds
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize LocationCallback
        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        // Check geofences whenever a new location is received
                        checkGeofences(location);
                    }
                }
            }
        };


    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGpsEnabled || isNetworkEnabled) {
                com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create()
                        .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000) // 10 seconds
                        .setFastestInterval(5000);

                com.google.android.gms.location.LocationCallback locationCallback = new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            OlaLatLng currentLocation = new OlaLatLng(location.getLatitude(), location.getLongitude(), 0.0);

                            if (olaMap2 != null && !isUserInteractingWithMap) {
                                // Only move the camera if auto-follow is enabled
                                olaMap2.moveCameraToLatLong(currentLocation, 15.0, 2000);
                                olaMap2.showCurrentLocation();
                            }
                            checkGeofences(location);
                        }
                    }
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                showEnableLocationDialog();
            }
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEnableLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom_layout, null);

        // Set the custom view to the AlertDialog
        builder.setView(dialogView)
                .setCancelable(false); // Optionally make it non-cancelable

        // Set button click listeners
        Button turnOnButton = dialogView.findViewById(R.id.dialog_turn_on);
        Button noThanksButton = dialogView.findViewById(R.id.dialog_no_thanks);

        AlertDialog dialog = builder.create(); // This creates the dialog
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background); // Apply rounded corners
        dialog.show();

        turnOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Turn On" action
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });

        noThanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "No Thanks" action, dismiss dialog
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheetFragment(DataSnapshot placeSnapshot) {

        String placeId = placeSnapshot.getKey();
        String name = placeSnapshot.child("name").getValue(String.class);
        String info = placeSnapshot.child("description").getValue(String.class);
        String history = placeSnapshot.child("history").getValue(String.class);
        String url = placeSnapshot.child("ARModel").getValue(String.class);
        List<String> facts = new ArrayList<>();

        for (DataSnapshot factSnapshot : placeSnapshot.child("facts").getChildren()) {
            facts.add(factSnapshot.getValue(String.class));
        }

        List<String> imageUrls = new ArrayList<>();
        for (DataSnapshot imageSnapshot : placeSnapshot.child("images").getChildren()) {
            imageUrls.add(imageSnapshot.getValue(String.class));
        }

        // Fetch trivia data
        String question = placeSnapshot.child("trivia").child("question").getValue(String.class);
        List<String> options = new ArrayList<>();
        for (DataSnapshot optionSnapshot : placeSnapshot.child("trivia").child("options").getChildren()) {
            options.add(optionSnapshot.getValue(String.class));
        }
        String correctAnswer = placeSnapshot.child("trivia").child("answer").getValue(String.class);

        // Create the fragment using the newInstance method
        PlaceBottomSheetFragment bottomSheetFragment = PlaceBottomSheetFragment.newInstance(
                placeId,
                name,
                info,
                history,
                imageUrls,
                facts,
                question,
                options,
                correctAnswer,
                url
        );

        // Show the fragment
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    private boolean isWithinGeofence(Location userLocation, double placeLat, double placeLng) {
        // Create a Location object for the place
        Location placeLocation = new Location("Place Location");
        placeLocation.setLatitude(placeLat);
        placeLocation.setLongitude(placeLng);

        // Calculate the distance
        float distance = userLocation.distanceTo(placeLocation);

        // Return true if within radius
        return distance <= GEOFENCE_RADIUS;
    }

    private void checkGeofences(Location userLocation) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("historical_places");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    String name = placeSnapshot.child("name").getValue(String.class);
                    Double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = placeSnapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null && name != null) {
                        if (isWithinGeofence(userLocation, latitude, longitude)) {
                            // Send notification if within the geofence
                            sendGeofenceNotification(name);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this, "Error loading places: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final List<String> notifiedPlaces = new ArrayList<>();

    private void sendGeofenceNotification(String placeName) {
        if (!notifiedPlaces.contains(placeName)) {
            notifiedPlaces.add(placeName);

            String channelId = "geofence_alerts";
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, "Geofence Notifications", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher) // Replace with your notification icon
                    .setContentTitle("You are near " + placeName)
                    .setContentText("Explore " + placeName + " now!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(placeName.hashCode(), builder.build());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

}