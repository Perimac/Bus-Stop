package com.example.busstop.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busstop.R;
import com.example.busstop.model.Trip;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View view;

    private static final int UPDATE_INTERVAL = 500;
    private FusedLocationProviderClient fusedProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private GoogleMap mMap;
    private LatLng latlng;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference tripReference;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.mRecyclerView);
        tripReference = FirebaseDatabase.getInstance().getReference("Trips").child("Kwame Mystic").child("Asuofia-Abrepo");



        fusedProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

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

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(this::echoTripsAsync);
        echoTripsAsync();

        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    @SuppressLint("MissingPermission")
    private void startLocationRequest() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, requireContext().getMainLooper());
            fusedProviderClient.getLastLocation().addOnSuccessListener(location -> {
                currentLocation = location;
                Log.d("MapLocation", String.valueOf(currentLocation.getLongitude()) + String.valueOf(currentLocation.getLatitude()));
                latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latlng).title("User"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12.0f));
            });
            fusedProviderClient.getLastLocation().addOnFailureListener(location -> {
                Log.d("TAG", "Exception while getting location: \n" + location.getMessage());
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(requireContext(), "Permission Needed", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            }
        }
    }

    void stopLocationRequest() {
        fusedProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocationRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationRequest();
    }
    @Override
    public void onStart() {
        super.onStart();
        startLocationRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationRequest();
    }

    void animateMapCamera(){
        LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
    }

    private void echoTripsAsync(){
        swipeRefreshLayout.setRefreshing(true);
        FirebaseRecyclerAdapter<Trip,TripsViewHolder> tripAdapter = new
                FirebaseRecyclerAdapter<Trip, TripsViewHolder>(
                        Trip.class,
                        R.layout.trip_custom_layout,
                        TripsViewHolder.class,
                        tripReference
                ) {
                    @Override
                    protected void populateViewHolder(TripsViewHolder tripsViewHolder, Trip trip, int position) {
                            tripsViewHolder.setDestination(trip.getDestination());
                            tripsViewHolder.setTripSeatNum(trip.getSeat_number());
                            tripsViewHolder.setTripLatitude(String.valueOf(trip.getDestination_lat()));
                            tripsViewHolder.setTripLongitude(String.valueOf(trip.getDestination_lng()));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                };
        recyclerView.setAdapter(tripAdapter);
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder {
        View adapterView;
        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            adapterView = itemView;
        }

        public void setDestination(String desti){
            TextView tripDest = adapterView.findViewById(R.id.destination);
            tripDest.setText(desti);
        }

        public void setTripSeatNum(String seatNum){
            TextView tripSeatNum = adapterView.findViewById(R.id.seat_num);
            tripSeatNum.setText(seatNum);
        }

        public void setTripLongitude(String longitude){
            TextView tripLongitude = adapterView.findViewById(R.id.longitude);
            tripLongitude.setText(longitude);
        }

        public void setTripLatitude(String latitude){
            TextView tripLatitude = adapterView.findViewById(R.id.latitude);
            tripLatitude.setText(latitude);
        }
    }

}