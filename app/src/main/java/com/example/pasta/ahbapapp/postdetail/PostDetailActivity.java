package com.example.pasta.ahbapapp.postdetail;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.comment.CommentListFragment;
import com.example.pasta.ahbapapp.comment.NewCommentFragment;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailActivity extends AppCompatActivity {

    @BindView(R.id.postDetailToolbar)
    Toolbar mToolbar;
    @BindView(R.id.postDetailUserImage)
    ImageView userImage;
    @BindView(R.id.postDetailUserName)
    TextView userName;
    @BindView(R.id.postDetailDate)
    TextView postDate;
    @BindView(R.id.postDetailContent)
    TextView postContent;
    @BindView(R.id.postDetailImage)
    ImageView postImage;
    @BindView(R.id.textViewCityTag)
    TextView cityHashTag;
    @BindView(R.id.textViewCatTag)
    TextView catHashTag;

    private static final String TAG = "PostDetail";
    public static final String POST_ID = "post_id";

    private String postId;
    private FirebaseFirestore mFirestore;
    private DocumentReference mPostDocumentReference;
    private ListenerRegistration mPostRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);

        if (getIntent().getExtras() == null){
            throw new IllegalArgumentException("Must pass extra " + POST_ID);
        }

        postId = getIntent().getExtras().getString(POST_ID);
        initFirestore();
        getPostData();
        initToolbar();
        addFragment();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPostRegistration != null) {
            mPostRegistration.remove();
            mPostRegistration = null;
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mPostDocumentReference = mFirestore.collection("posts").document(postId);
    }

    private void getPostData() {
        mPostRegistration = mPostDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null){
                    Log.d(TAG, "getPostData", e);
                }

                PostModel post = documentSnapshot.toObject(PostModel.class);

                Glide.with(userImage.getContext())
                        .load(post.getAuthor_image())
                        .into(userImage);

                userName.setText(post.getAuthor_name());
                postDate.setText(TimeAgo.getTimeAgo(post.getCreated_at()));
                postContent.setText(post.getContent());
                cityHashTag.setText(String.format("#%s", post.getCity().toLowerCase()));
                catHashTag.setText(String.format("#%s", post.getCategory().toLowerCase()));

                String postImageUrl = post.getImage_url();
                if (postImageUrl == null || !postImageUrl.isEmpty()){
                    postImage.setVisibility(View.VISIBLE);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(8));
                    Glide.with(postImage.getContext()).load(postImageUrl).apply(requestOptions).into(postImage);
                }
                else {
                    postImage.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initToolbar() {
        mToolbar.setElevation(14f);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addFragment() {
        CommentListFragment commentListFragment = new CommentListFragment();
        NewCommentFragment newCommentFragment = new NewCommentFragment();

        Bundle bundle = new Bundle();
        bundle.putString(POST_ID, postId);
        commentListFragment.setArguments(bundle);
        newCommentFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.comments_frame_container, commentListFragment);
        fragmentTransaction.add(R.id.new_comment_frame_container, newCommentFragment);
        fragmentTransaction.commit();
    }
}
