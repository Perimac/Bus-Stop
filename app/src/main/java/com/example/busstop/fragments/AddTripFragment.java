package com.example.busstop.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busstop.R;
import com.example.busstop.model.BusStops;
import com.example.busstop.model.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class AddTripFragment extends Fragment {

    private View view;
    private Button getData;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private List<BusStops> busStopsList;

    //Views
    private Spinner journeySpinner;
    private Spinner busStopSpinner;
    private Spinner seatNumberSpinner;
    private TextView latitudeText;
    private TextView longitudeText;
    private Button uploadButton;
    private ProgressBar progressBar;

    public AddTripFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_trip, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        busStopsList = new ArrayList<>();

        journeySpinner = view.findViewById(R.id.spinner_journey);
        busStopSpinner = view.findViewById(R.id.spinner_bus_stops);
        seatNumberSpinner = view.findViewById(R.id.spinner_seat_number);
        latitudeText = view.findViewById(R.id.lat_id);
        longitudeText = view.findViewById(R.id.long_id);
        uploadButton = view.findViewById(R.id.upload_trip);
        progressBar = view.findViewById(R.id.progressbar_id);

        busStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        latitudeText.setText("");
                        longitudeText.setText("");
                        break;
                    case 1:
                        latitudeText.setText("6.7618001");
                        longitudeText.setText("-1.6843967");
                        break;
                    case 2:
                        latitudeText.setText("6.7617965");
                        longitudeText.setText("-1.6845908");
                        break;

                    case 3:
                        latitudeText.setText("6.764029");
                        longitudeText.setText("-1.6813908");
                        break;

                    case 4:
                        latitudeText.setText("6.7619479");
                        longitudeText.setText("-1.6844031");
                        break;

                    case 5:
                        latitudeText.setText("6.7664167");
                        longitudeText.setText("-1.6826167");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        uploadButton.setOnClickListener(v -> {
            pushToRealtime();
        });

        return view;
    }

    private void pushToRealtime() {
        String journey = journeySpinner.getSelectedItem().toString();
        String destination = busStopSpinner.getSelectedItem().toString();
        String seatNumber = seatNumberSpinner.getSelectedItem().toString();
        String latitude = latitudeText.getText().toString();
        String logitude = longitudeText.getText().toString();
        String driverName = Paper.book().read("d_name");
        String carnumber = Paper.book().read("d_carnum");
        String tripID = databaseReference.push().getKey();

        if (journey.isEmpty()) {
            Toast.makeText(getActivity(), "Journey is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getActivity(), "Destination is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (seatNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Seat Number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude.isEmpty()) {
            Toast.makeText(getActivity(), "Latitude is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (logitude.isEmpty()) {
            Toast.makeText(getActivity(), "Longitude is required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Trip trip = new Trip();
        trip.setTripID(tripID);
//        trip.setDriver_name(driverName);
        trip.setDriver_carnumber(carnumber);
        trip.setSeat_number(seatNumber);
//        trip.setJourney(journey);
        trip.setDestination(destination);
        trip.setDestination_lat(Double.parseDouble(latitude));
        trip.setDestination_lng(Double.parseDouble(logitude));

        databaseReference.child(driverName).child(journey).child(tripID)
                .setValue(trip).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Trip Added Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

}