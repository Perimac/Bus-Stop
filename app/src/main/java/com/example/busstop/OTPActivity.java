package com.example.busstop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busstop.model.BusStops;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OTPActivity extends AppCompatActivity {

    private EditText journeyeEditext;
    private EditText bustopEditext;
    private TextView longitudeText;
    private TextView latitudeText;
    private Button uploadBtn;
    private ImageButton refreshLocation;

    private static final int UPDATE_INTERVAL = 500;
    private static final String TAG = OTPActivity.class.getSimpleName();

    private FusedLocationProviderClient fusedProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private ProgressBar progressBar;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        journeyeEditext = findViewById(R.id.journey_editext);
        bustopEditext = findViewById(R.id.bustop_text);
        longitudeText = findViewById(R.id.longitude_text);
        latitudeText = findViewById(R.id.latitude_text);
        uploadBtn = findViewById(R.id.uploadBtn);
        progressBar = findViewById(R.id.bsprogress_bar);
        refreshLocation = findViewById(R.id.refreshloc);

        fusedProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        database = FirebaseDatabase.getInstance().getReference("Bus-Stops");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("TAG", "Location Result is not Available");
            }
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()) {
                    Log.d("TAG", "Location is Available");
                } else {
                    Log.d("TAG", "Location is not Available");
                }
            }
        };

        startLocationRequest();

        uploadBtn.setOnClickListener(v ->{
            uploadBusStops();
        });

        refreshLocation.setOnClickListener(v -> {
           startLocationRequest();
        });

    }

    void startLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, OTPActivity.this.getMainLooper());
            fusedProviderClient.getLastLocation().addOnSuccessListener(location -> {
                currentLocation = location;
                if (currentLocation != null) {
                latitudeText.setText(String.valueOf(currentLocation.getLatitude()));
                longitudeText.setText(String.valueOf(currentLocation.getLongitude()));
                Log.d("Latitude: ", String.valueOf(location.getLatitude()));
                Log.d("Longitude: ", String.valueOf(location.getLongitude()));
                }
            });
            fusedProviderClient.getLastLocation().addOnFailureListener(location -> {
                Log.d("TAG", "Exception while getting location: \n" + location.getMessage());
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(OTPActivity.this, "Permission Needed", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocationRequest();
    }

    void stopLocationRequest() {
        fusedProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationRequest();
    }

    private void uploadBusStops(){
        String journey = journeyeEditext.getText().toString();
        String destinations = bustopEditext.getText().toString();
        String latitude = latitudeText.getText().toString();
        String longitude = longitudeText.getText().toString();
        String bustStopID = database.push().getKey();

        if (journey.isEmpty() || destinations.isEmpty() || latitude.isEmpty() || longitude.isEmpty()){
            Toast.makeText(OTPActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        BusStops busstop = new BusStops();
        busstop.setJourney(journey);
        busstop.setDestination(destinations);
        busstop.setDest_latitude(latitude);
        busstop.setDest_longitude(longitude);

        database.child(bustStopID).setValue(busstop);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(OTPActivity.this, "Bus Stop added successfully", Toast.LENGTH_SHORT).show();
        clearUI();
    }

    private void clearUI() {
        bustopEditext.setText("");
        latitudeText.setText("");
        longitudeText.setText("");
    }

}