package com.example.busstop;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.busstop.model.Driver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText driverName;
    private EditText carNumber;
    private EditText carType;
    private Button registerBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Driver> driverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        driverName = findViewById(R.id.reg_driver_name);
        carNumber = findViewById(R.id.reg_car_number);
        carType = findViewById(R.id.reg_car_type);
        progressBar = findViewById(R.id.otp_progress_bar);
        registerBtn = findViewById(R.id.reg_driver_btn);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Drivers");
        driverList = new ArrayList<>();


        registerBtn.setOnClickListener(v ->{
            String drivername = driverName.getText().toString();
            String drivercarnumber = carNumber.getText().toString();
            String cartype = carType.getText().toString();
            String driverID = mDatabaseRef.push().getKey();

            if (drivername.isEmpty() || drivercarnumber.isEmpty() || cartype.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            Driver driver = new Driver();
            driver.setDriver_id(driverID);
            driver.setDriver_name(drivername);
            driver.setDriver_carnumber(drivercarnumber);
            driver.setCartype(cartype);

            mDatabaseRef.child(driverID).setValue(driver);
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(RegisterActivity.this,"Account created Successfully",Toast.LENGTH_SHORT).show();
            initMainActivity();
        });
    }

    private void initMainActivity() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}