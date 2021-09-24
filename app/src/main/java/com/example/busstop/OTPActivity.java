package com.example.busstop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Intent getIntent;

    private String numberFromLogin;
    private String mVerificationId;
    private EditText smsEditext;
    private Button verifyOTPBtn;
    private ProgressBar progressBar;
    private TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        mAuth = FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting();

        mCurrentUser = mAuth.getCurrentUser();

        smsEditext = findViewById(R.id.otp_text_view);
        verifyOTPBtn = findViewById(R.id.verify_btn);
        progressBar = findViewById(R.id.otp_progress_bar);
        status = findViewById(R.id.otp_form_feedback);

        getIntent = getIntent();
        numberFromLogin = getIntent.getStringExtra("phone");
        sendVerificationCode(numberFromLogin);

        verifyOTPBtn.setOnClickListener(v -> {
            String code = smsEditext.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                smsEditext.setError("Enter valid code");
                smsEditext.requestFocus();
                return;
            }
            verifyVerificationCode(code);
        });


    }

    private void sendVerificationCode(String number) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+233" + number,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallbacks);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        smsEditext.setText(code);
                        verifyVerificationCode(code);
                    }
                }
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    mVerificationId = s;
                }
            };

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        status.setText("Verifying...");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPActivity.this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(OTPActivity.this, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String message = "Somthing is wrong, we will fix it soon...";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered...";
                        }
                        status.setText(message);
                    }
                });
    }


}