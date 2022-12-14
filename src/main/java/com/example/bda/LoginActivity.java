package com.example.bda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PrivateKey;

public class LoginActivity extends AppCompatActivity {
    private TextView register;
    private TextInputEditText loginemail;
    private TextInputEditText loginpassword;
    private Button loginbutton;
    private TextView forgotpassword;
    private ProgressDialog loader;
    private FirebaseAuth mauth;

    // if user has already login then we direct user direct to MainActivity skipping loginactivity
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // checking user already present or not
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mauth.getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        register=findViewById(R.id.backbutton);
        loginemail=findViewById(R.id.loginemailid);
        loginpassword=findViewById(R.id.loginpassword);
        loginbutton=findViewById(R.id.loginbutton);
        forgotpassword=findViewById(R.id.forgotpassword);
        loader=new ProgressDialog(this);
        mauth=FirebaseAuth.getInstance();

        //login button
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=loginemail.getText().toString().trim();
                final String password=loginpassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    loginemail.setError("email is required");
                }
                if(TextUtils.isEmpty(password)){
                    loginpassword.setError("Password is required");
                }
                else{
                    loader.setMessage("loader is in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login is successfull", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }

                    });
                }
            }
        });

        // register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,selectregistrationactivity.class);
                startActivity(intent);
            }
        });
    }
    // checking firebaseauthlistener on start(jumping off login activity)
    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mauth.removeAuthStateListener(authStateListener);
    }
}