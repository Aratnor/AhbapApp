package com.example.pasta.ahbapapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.account.AccountActivity;
import com.example.pasta.ahbapapp.bookmark.BookMarkActivity;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.example.pasta.ahbapapp.postlist.PostDialogFragment;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BookMarkAdapter extends FirestoreAdapter<BookMarkAdapter.ViewHolder>{

    private final Activity activity;


    public BookMarkAdapter(Query query, Activity activity) {
        super(query);
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BookMarkAdapter.ViewHolder(inflater.inflate(R.layout.post_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.post_user_image)
        CircleImageView postUserImage;
        @BindView(R.id.post_user_name)
        TextView postUserName;
        @BindView(R.id.post_date)
        TextView postDate;
        @BindView(R.id.post_content)
        TextView postContent;
        @BindView(R.id.post_image)
        ImageView postImage;
        @BindView(R.id.post_comment_icon)
        ImageView commentIcon;
        @BindView(R.id.post_comment_count)
        TextView commentCount;
        @BindView(R.id.textViewCityTag)
        TextView cityHashTag;
        @BindView(R.id.textViewCatTag)
        TextView catHashTag;
        @BindView(R.id.dialog_image)
        ImageView dialogImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
        public void bind(final DocumentSnapshot snapshot){

            final String postID = (String) snapshot.get("post_id");

            FirebaseFirestore.getInstance().collection("posts").document(postID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()){
                        final PostModel postModel = documentSnapshot.toObject(com.example.pasta.ahbapapp.model.PostModel.class);

                        Glide.with(postUserImage.getContext())
                                .load(postModel.getAuthor_image())
                                .into(postUserImage);
                        postUserImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent startIntent = new Intent(itemView.getContext(), AccountActivity.class);
                                startIntent.putExtra("user_id",postModel.getAuthor_id());
                                itemView.getContext().startActivity(startIntent);
                            }
                        });

                        postUserName.setText(postModel.getAuthor_name());
                        postUserName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent startIntent = new Intent(itemView.getContext(), AccountActivity.class);
                                startIntent.putExtra("user_id",postModel.getAuthor_id());
                                itemView.getContext().startActivity(startIntent);
                            }
                        });

                        postDate.setText(TimeAgo.getTimeAgo(postModel.getCreated_at()));

                        postContent.setText(postModel.getContent());
                        cityHashTag.setText(String.format("#%s", postModel.getCity().toLowerCase()));
                        catHashTag.setText(String.format("#%s", postModel.getCategory().toLowerCase()));
                        dialogImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PostDialogFragment postDialogFragment = new PostDialogFragment();
                                postDialogFragment.setUserID(postModel.getAuthor_id());
                                postDialogFragment.setPostID(snapshot.getReference().getId());
                                postDialogFragment.show(activity.getFragmentManager(), "postDialog" );
                            }
                        });
                        String postImageUrl = postModel.getImage_url();
                        if (postImageUrl == null || !postImageUrl.isEmpty()){
                            postImage.setVisibility(View.VISIBLE);
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(8));
                            Glide.with(postImage.getContext()).load(postImageUrl).apply(requestOptions).into(postImage);
                        }
                        else {
                            postImage.setVisibility(View.GONE);
                        }

                        commentCount.setText(String.valueOf(postModel.getComment_count()));
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, PostDetailActivity.class);
                                intent.putExtra(PostDetailActivity.POST_ID, postID);
                                activity.startActivity(intent);

                            }
                        });
                    }
                }
            });

        }
    }
}