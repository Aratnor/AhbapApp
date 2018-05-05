package com.example.pasta.ahbapapp.account;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.example.pasta.ahbapapp.chat.message.MessageActivity;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity implements PostAdapter.OnPostSelectedListener {
    private static final String TAG = "AccountActivity" ;
    @BindView(R.id.account_user_image)
    CircleImageView userImage;
    @BindView(R.id.account_user_name)
    TextView userName;
    @BindView(R.id.account_post_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.account_toolbar)
    Toolbar mToolbar;

    private FirebaseFirestore mFirestore;
    private PostAdapter mAdapter;
    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        user_id = getIntent().getExtras().getString("user_id");
        initToolbar();
        initAppBar();
        setUser(user_id);
        getPosts(user_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {

        super.onStop();
        mAdapter.stopListening();
    }

    @OnClick(R.id.account_message_button)
    public void sendMessage() {
        Intent intent = new Intent(AccountActivity.this, MessageActivity.class);
        intent.putExtra("userID",user_id);
        startActivity(intent);
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAppBar(){
        AppBarLayout mAppBarLayout = findViewById(R.id.account_app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.account_collapsing_toolbar);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 100) {
                    isShow = true;
                    //TODO set title to username
                    collapsingToolbarLayout.setTitle("Mehmet Ali Ocak");
                } else if (isShow) {
                    isShow = false;
                    collapsingToolbarLayout.setTitle("");
                    Log.d(TAG, "setTitle");
                    Log.d(TAG,String.valueOf(scrollRange + verticalOffset));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, "Ayarlar", Toast.LENGTH_SHORT).show();
                return true;

            default:return false;
        }
    }

    private void setUser(String id) {
        mFirestore.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(userImage.getContext())
                        .load(documentSnapshot.get("image_url"))
                        .into(userImage);
                userName.setText(documentSnapshot.get("name").toString());
            }
        });
    }

    private void getPosts(String id) {
        Query mQuery = mFirestore.collection("posts")
                .whereEqualTo("author_id", id)
                .orderBy("created_at", Query.Direction.DESCENDING);

        mAdapter = new PostAdapter(mQuery, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                , LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mRecyclerView.getContext(),layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onPostSelected(DocumentSnapshot post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_ID, post.getId());
        startActivity(intent);
    }
}
