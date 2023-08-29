package com.example.firebase_authentication_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int MY_READ_PERMISSION_CODE = 123; // Use any value you prefer
    private static final int REQUEST_GELLARY = 33;

    EditText pName,pEmail,pPassword,pAge,pGender;
    Button submit_button,uploadButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findAllId();
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmitUserDetail();
            }
        });
        getUserDetail();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
            }
        });

    }
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MY_READ_PERMISSION_CODE);
        } else {
            selectImage();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_GELLARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_GELLARY){
            if(data.getData()!=null){
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Delete Account");
                progressDialog.setMessage("loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final StorageReference reference = FirebaseStorage.getInstance().getReference()
                        .child("profileImage")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.putFile(data.getData())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Profile uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_button){
            doLogoutUser();
        } else if (item.getItemId() == R.id.delete_account_button) {
            doDeleteAccount();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doDeleteAccount() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Delete Account");
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                                            startActivity(intent);
                                        }else{

                                        }
                                    }
                                });
                    }
                });
    }

    private void doLogoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,SigninActivity.class);
        startActivity(intent);
    }

    private void getUserDetail() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            pName.setText(userModel.getName());
                            pEmail.setText(userModel.getEmail());
                            pPassword.setText(userModel.getPassword());
                            pAge.setText(userModel.getAge());
                            pGender.setText(userModel.getGender());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void doSubmitUserDetail() {
        Map<String,Object> map = new HashMap<>();
        map.put("name",pName.getText().toString().trim());
        map.put("email",pEmail.getText().toString().trim());
        map.put("password",pPassword.getText().toString().trim());
        map.put("age",pAge.getText().toString().trim());
        map.put("gender",pGender.getText().toString().trim());

        FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void findAllId() {
        pName = findViewById(R.id.pnameET);
        pEmail = findViewById(R.id.pemailET);
        pPassword = findViewById(R.id.ppasswordET);
        pAge = findViewById(R.id.pageET);
        pGender = findViewById(R.id.pgenderET);
        submit_button = findViewById(R.id.submitBTN);
        uploadButton = findViewById(R.id.uploadBTN);
    }
}