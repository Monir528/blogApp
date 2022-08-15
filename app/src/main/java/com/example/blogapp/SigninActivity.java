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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninActivity extends AppCompatActivity {

    private EditText emailEDT, passwordEDT;
    private TextView signupButton;
    private Button Signin_BTN;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        emailEDT = findViewById(R.id.signin_email_edt_id);
        passwordEDT = findViewById(R.id.signin_password_edt_id);
        Signin_BTN = findViewById(R.id.signin_btn_id);
        progressDialog = new ProgressDialog(this);
        signupButton = findViewById(R.id.signup_button_id);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegistarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        Signin_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEDT.getText().toString().trim();
                String password = passwordEDT.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                progressDialog.setCancelable(false);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            checkUser();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "email or password is not valied", Toast.LENGTH_SHORT).show();
                            passwordEDT.setText(null);

                        }
                    }
                });
                }else{
                    Toast.makeText(getApplicationContext(), "please fill up", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void checkUser(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_id = firebaseAuth.getCurrentUser().getUid();
                if (dataSnapshot.hasChild(user_id)){
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "please signup", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}