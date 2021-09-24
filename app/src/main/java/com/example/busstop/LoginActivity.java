package com.example.busstop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mCountryCode;
    private EditText mPhoneNumber;

    private Button mGenerateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCountryCode = findViewById(R.id.country_code_text);
        mPhoneNumber = findViewById(R.id.phone_number_text);
        mGenerateBtn = findViewById(R.id.generate_btn);

        mGenerateBtn.setOnClickListener(v -> {
            String country_code = mCountryCode.getText().toString();
            String phone_number = mPhoneNumber.getText().toString();
            String complete_phone_number = "+" + country_code + phone_number;

            if(country_code.isEmpty() || phone_number.isEmpty()){
                Toast.makeText(LoginActivity.this,"Please phone number and country code is required",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(LoginActivity.this,OTPActivity.class);
            intent.putExtra("phone",complete_phone_number);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            initMain();
        }
    }

    private void initMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}