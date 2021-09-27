package com.example.busstop;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busstop.model.Driver;
import com.example.busstop.session.preference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Driver> driverList;

    private EditText driverName;
    private EditText driverCarnumber;
    private TextView regDriver;
    private TextView pickPoints;
    private Button loginDriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Paper.init(this);

        driverName = findViewById(R.id.driver_name_text);
        driverCarnumber = findViewById(R.id.car_number_text);
        regDriver = findViewById(R.id.reg_driver);
        loginDriver = findViewById(R.id.login_driver);
        mProgressBar = findViewById(R.id.progress_bar);
        pickPoints = findViewById(R.id.pick_points);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Drivers");
        driverList = new ArrayList<>();

        loginDriver.setOnClickListener(v -> {
            authenticateDriver();
        });

        regDriver.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        pickPoints.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, OTPActivity.class));
        });

    }

    private void authenticateDriver() {
        mProgressBar.setVisibility(VISIBLE);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverList.clear();
                String authname = driverName.getText().toString();
                String authcarnumber = driverCarnumber.getText().toString();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Driver driver = snapshot.getValue(Driver.class);
                    driverList.add(driver);
                }
                for (Driver d : driverList) {
                    String dname = d.getDriver_name();
                    String dcarnumber = d.getDriver_carnumber();
                    if (authname.equalsIgnoreCase(dname) && authcarnumber.equalsIgnoreCase(dcarnumber)) {
                        Paper.book().write("session",1);
                        Paper.book().write("d_name",authname);
                        Paper.book().write("d_carnum",authcarnumber);
                        preference.setDataLogin(LoginActivity.this,true);
                        preference.setDataAs(LoginActivity.this,"driver");
                        mProgressBar.setVisibility(View.GONE);
                        initMainActivity();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "User does not exit", Toast.LENGTH_SHORT).show();
                        preference.setDataLogin(LoginActivity.this,false);
                    }
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preference.getDataLogin(LoginActivity.this)){
            if (preference.getDataAs(LoginActivity.this).equals("driver")){
                initMainActivity();
            } //here also u can check for other login session example either admin or normal user.
        }
    }
}