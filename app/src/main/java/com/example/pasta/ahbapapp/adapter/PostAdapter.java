package com.example.pasta.ahbapapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.account.AccountActivity;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pasta on 3.04.2018.
 */

public class PostAdapter extends FirestoreAdapter<PostAdapter.ViewHolder> {

    public interface OnPostSelectedListener {

        void onPostSelected(DocumentSnapshot post);

    }

    private OnPostSelectedListener mListener;


    public PostAdapter(Query query, OnPostSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.post_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_user_image)
        CircleImageView  postUserImage;
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnPostSelectedListener listener) {
            final PostModel postModel = snapshot.toObject(PostModel.class);

            Glide.with(postUserImage.getContext())
                    .load(postModel.getAuthor_image())
                    .into(postUserImage);
            postUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(itemView.getContext(), AccountActivity.class);
                    Bundle extras = startIntent.getExtras();
                    extras.putString("user_id",postModel.getAuthor_id());
                    startIntent.putExtras(extras);
                    itemView.getContext().startActivity(startIntent);
                }
            });

            postUserName.setText(postModel.getAuthor_name());
            postUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(itemView.getContext(), AccountActivity.class);
                    Bundle extras = startIntent.getExtras();
                    extras.putString("user_id",postModel.getAuthor_id());
                    startIntent.putExtras(extras);
                    itemView.getContext().startActivity(startIntent);
                }
            });

            postDate.setText(TimeAgo.getTimeAgo(postModel.getCreated_at()));

            postContent.setText(postModel.getContent());
            cityHashTag.setText(String.format("#%s", postModel.getCity().toLowerCase()));
            catHashTag.setText(String.format("#%s", postModel.getCategory().toLowerCase()));
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
                    if (listener != null){
                        listener.onPostSelected(snapshot);
                    }
                }
            });
        }
    }
}
