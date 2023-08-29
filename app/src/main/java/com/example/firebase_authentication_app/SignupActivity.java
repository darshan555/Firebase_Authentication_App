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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText name,email,password;
    Button signUpButton;
    TextView loginPage;
    private ProgressDialog progressDialog;

    public SignupActivity(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findAllViewid();
        isUserAlreadySignIn();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataValid()){
                    signupWithEmailandPassword();
                }
            }
        });

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this,SigninActivity.class);
                startActivity(intent);
            }
        });
    }

    private void isUserAlreadySignIn() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean isDataValid() {
        if(name.getText().toString().isEmpty()){
            name.requestFocus();
            Toast.makeText(this, "Please Provide Name", Toast.LENGTH_SHORT).show();
            return false;
        }else if(email.getText().toString().isEmpty()){
            email.requestFocus();
            Toast.makeText(this, "Please Provide Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            password.requestFocus();
            Toast.makeText(this, "Please Provide Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signupWithEmailandPassword() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We create your account");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email.getText().toString().trim(),
                password.getText().toString().trim()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",name.getText().toString().trim());
                    map.put("email",email.getText().toString().trim());
                    map.put("password",password.getText().toString().trim());
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });


                    Toast.makeText(SignupActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void findAllViewid() {
        name = findViewById(R.id.nameET);
        email = findViewById(R.id.lemailET);
        password = findViewById(R.id.lpasswordET);
        signUpButton = findViewById(R.id.signinBTN);
        loginPage = findViewById(R.id.alreadyAccTV);
    }
}