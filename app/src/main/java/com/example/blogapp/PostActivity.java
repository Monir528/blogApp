package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.spec.ECField;

public class PostActivity extends AppCompatActivity {

    private Button postbutton;
    private ImageButton postSetImageButtton;
    private EditText TitleET, DesET;
    private static final int REQUEST_CODE = 12;
    private StorageReference storageReference, storageReference1;
    private DatabaseReference databaseReference, newPost, databaseReference1;
    private ProgressDialog progressDialog;
    private String imageLink;
    private Uri downloadURI, uri = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://blogapp-b2f7c.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("user").child(firebaseUser.getUid());

        postbutton = findViewById(R.id.postbutton_id);
        postSetImageButtton = findViewById(R.id.post_image_button_id);
        TitleET = findViewById(R.id.title_edittext_id);
        DesET = findViewById(R.id.Des_edittext_id);
        progressDialog = new ProgressDialog(this);

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPost();
            }
        });

        postSetImageButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            try {
                uri = data.getData();
                postSetImageButtton.setImageURI(uri);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startPost(){

        try {
            final String title = TitleET.getText().toString();
            final String des = DesET.getText().toString();
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(des) && uri != null){
                progressDialog.setMessage("Uploading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                storageReference1 = storageReference.child("postImage").child(uri.getLastPathSegment());
                storageReference1.putFile(uri);
                storageReference1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        try {
                            try {
                                downloadURI = task.getResult();
                            }catch (Exception e){
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Exceptiontask: "+e, Toast.LENGTH_LONG).show();
                            }
                            imageLink = downloadURI.toString();
                            newPost  =  databaseReference.child("postData").push();

                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    newPost.child("Title").setValue(title);
                                    newPost.child("Description").setValue(des);
                                    newPost.child("PostImageLink").setValue(imageLink);
                                    newPost.child("UID").setValue(firebaseUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            progressDialog.dismiss();
                            Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "Post Successfully", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /*uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageReference1.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                    if(!task.isSuccessful()){
                        downloadURI = task.getResult();
                        try {
                            imageLink = downloadURI.toString();
                            newPost  =  databaseReference.child("postData").push();
                            newPost.child("Title").setValue(title);
                            newPost.child("Description").setValue(des);
                            newPost.child("PostImageLink").setValue(imageLink);
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Post Successfully", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                    }
                });*/
            }else{
                Toast.makeText(getApplicationContext(), "Please fillup", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
        super.onBackPressed();
    }*/
}