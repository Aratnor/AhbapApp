package com.example.pasta.ahbapapp.account;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity implements PostAdapter.OnPostSelectedListener {
    @BindView(R.id.account_user_image);
    CircleImageView account_user_image;
    @BindView(R.id.account_user_name);
    TextView account_user_name;
    @BindView(R.id.account_post_recycler);
    RecyclerView account_post_recycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private PostAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        String user_id = getIntent().getExtras().getString("user_id");

        setUser(user_id);

        getPosts(user_id);
    }

    private void setUser(String id) {
        mFirestore.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(account_user_image.getContext())
                        .load(documentSnapshot.get("image_url"))
                        .into(account_user_image);
                account_user_name.setText(documentSnapshot.get("name").toString());
            }
        });
    }

    private void getPosts(String id) {
        mQuery = mFirestore.collection("posts").whereEqualTo("author_id",id).orderBy("created_at",Query.Direction.DESCENDING);

        mAdapter = new PostAdapter(mQuery,this);

        account_post_recycler.setAdapter(mAdapter);
    }

    @Override
    public void onPostSelected(DocumentSnapshot post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_ID, post.getId());
        startActivity(intent);
    }
}
