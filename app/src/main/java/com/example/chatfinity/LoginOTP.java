package com.example.chatfinity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTP extends AppCompatActivity {

    public static class AndroidUtil{

        public static void showToast(Context context,String message){
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
    }


    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    Timer timer;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginotp);

        otpInput = findViewById(R.id.send_otp_btn);
        nextBtn = findViewById(R.id.otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp);

        phoneNumber = getIntent().getExtras().getString("phone");

        sendOtp(phoneNumber,false);

        nextBtn.setOnClickListener(v -> {
            String enteredOtp  = otpInput.getText().toString();
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });

    }


    void sendOtp(String phoneNumber,boolean isResend){
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginOTP.this,LoginUsername.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });


    }

   void startResendTimer() {
        // Disable the Resend OTP TextView
        resendOtpTextView.setEnabled(false);

        // Create a new Timer instance
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> {
                    if (timeoutSeconds > 0) {
                        resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    } else {
                        timeoutSeconds = 60L;
                        resendOtpTextView.setText("Resend OTP");
                        resendOtpTextView.setEnabled(true);
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

}