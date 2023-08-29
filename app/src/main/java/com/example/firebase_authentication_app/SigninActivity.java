package com.example.firebase_authentication_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {

    TextView getSignup;
    private ProgressDialog progressDialog;
    EditText email,password;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        findAllid();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData()){
                    signinWithEmailandPassword();
                }
            }
        });

        getSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private boolean checkData() {
        if(email.getText().toString().isEmpty()){
            email.requestFocus();
            Toast.makeText(this, "Please Provide Email", Toast.LENGTH_SHORT).show();
            return false;
        }else if(password.getText().toString().isEmpty()){
            password.requestFocus();
            Toast.makeText(this, "Please Provide Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signinWithEmailandPassword() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Check Credentials");
        progressDialog.setMessage("Wait Please");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email.getText().toString().trim(),
                password.getText().toString().trim()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(SigninActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SigninActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findAllid() {
        email = findViewById(R.id.lemailET);
        password = findViewById(R.id.lpasswordET);
        signInButton = findViewById(R.id.signinBTN);
        getSignup = findViewById(R.id.createAccTV);
    }
}