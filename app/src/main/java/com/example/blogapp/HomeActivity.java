package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference, for_like_post;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Boolean is_like = false;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("postData");
        for_like_post = FirebaseDatabase.getInstance().getReference().child("post_like");
        databaseReference.keepSynced(true);
        for_like_post.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        /*query = FirebaseDatabase.getInstance().getReference().child("postData");*/
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        recyclerView = findViewById(R.id.recyclerview_on_homeActivity_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.recyclerview_layout,
                BlogViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {

                final String post_key = getRef(i).getKey();

                blogViewHolder.setImage(getApplicationContext(),blog.getPostImageLink());
                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setDes(blog.getDescription());
                blogViewHolder.setUsername(blog.getUsername());
                blogViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),SinglePostActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("blog id", post_key);
                        startActivity(intent);
                    }
                });
                blogViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                blogViewHolder.likeiamgebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        is_like = true;
                        for_like_post.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (is_like) {
                                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                        for_like_post.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        is_like = false;
                                    } else {
                                        for_like_post.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).setValue("like");
                                        is_like = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                blogViewHolder.setLike(post_key);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    /*--update code--
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Blog> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Blog>().setQuery(query, Blog.class).build();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setTitle(model.getTitle());
                holder.setDes(model.getDescription());
                holder.setImage(getApplicationContext(),model.getPostImageLink());
            }

            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
                return new BlogViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }*/

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        ImageButton likeiamgebtn;
        DatabaseReference databaseReference;
        FirebaseAuth firebaseAuth;
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textView = view.findViewById(R.id.title_id);
            likeiamgebtn = view.findViewById(R.id.like_btn_id);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("post_like");
            firebaseAuth = FirebaseAuth.getInstance();
        }

        public void setTitle(String title){
            textView.setText(title);
        }
        public void setDes(String des){
            TextView textView1 = view.findViewById(R.id.des_id);
            textView1.setText(des);
        }
        public void setImage(final Context context, final String imageLink){
            final ImageView imageView = view.findViewById(R.id.image_id);

            Picasso.with(context).load(imageLink).into(imageView);

            /*Picasso.with(context).load(imageLink).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(imageLink).into(imageView);
                }
            });*/
        }
        public void setUsername(String username){
            TextView usernametv = view.findViewById(R.id.post_username_text_id);
            usernametv.setText(username);
        }
        public void setLike(final String post_key){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){
                        likeiamgebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    }else{
                        likeiamgebtn.setImageResource(R.drawable.ic_baseline_gray_thumb_up_24);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post_button_id){
            Intent intent = new Intent(HomeActivity.this,PostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.userPost_button_id){
            Intent intent = new Intent(HomeActivity.this,UserPostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.signout_button_id){
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        firebaseAuth.signOut();
    }
}