package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistarActivity extends AppCompatActivity {

    private EditText nameEDT, emailEDT, passwordEDT;
    private Button registrationBTN;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        firebaseAuth = FirebaseAuth.getInstance();

        nameEDT = findViewById(R.id.registration_name_edt_id);
        emailEDT = findViewById(R.id.registration_email_edt_id);
        passwordEDT = findViewById(R.id.registration_password_edt_id);
        registrationBTN = findViewById(R.id.registration_btn_id);

        progressDialog = new ProgressDialog(this);

        registrationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameEDT.getText().toString().trim();
                String email = emailEDT.getText().toString().trim();
                String password = passwordEDT.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                progressDialog.setMessage("Registration in progress");
                progressDialog.setCancelable(false);
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String user_is = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference databaseReference1 = databaseReference.child(user_is);
                            databaseReference1.child("name").setValue(name);
                            databaseReference1.child("image").setValue("default");
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }
                });

                }else{
                    Toast.makeText(getApplicationContext(), "please fill up", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}