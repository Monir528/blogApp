package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ImageView image;
    private TextView title, des, username;
    private Button removeButton;
    private ImageButton like_button;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        post_key = getIntent().getExtras().getString("blog id");

        image = findViewById(R.id.SinglePost_iv_id);
        title = findViewById(R.id.SinglePost_title_id);
        des = findViewById(R.id.SinglePost_des_id);
        username = findViewById(R.id.SinglePost_username_text_id);
        removeButton = findViewById(R.id.SinglePost_remove_btn_id);
        like_button = findViewById(R.id.SinglePost_like_btn_id);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("postData");
        databaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String postTitle = dataSnapshot.child("Title").getValue().toString();
                String postDes = dataSnapshot.child("Description").getValue().toString();
                String postImage = dataSnapshot.child("PostImageLink").getValue().toString();
                String postUsername = dataSnapshot.child("username").getValue().toString();
                String uid = dataSnapshot.child("UID").getValue().toString();

                title.setText(postTitle);
                des.setText(postDes);
                username.setText(postUsername);
                Picasso.with(getApplicationContext()).load(postImage).into(image);
                if (firebaseAuth.getCurrentUser().getUid().equals(uid)){
                    removeButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(post_key).removeValue();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}