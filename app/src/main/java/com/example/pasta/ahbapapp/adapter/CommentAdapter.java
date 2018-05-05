package com.example.pasta.ahbapapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.account.AccountActivity;
import com.example.pasta.ahbapapp.chat.message.MessageActivity;
import com.example.pasta.ahbapapp.model.CommentModel;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pasta on 14.04.2018.
 */

public class CommentAdapter extends FirestoreAdapter<CommentAdapter.ViewHolder> {


    public CommentAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.comment_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_user_image)
        CircleImageView commentUserImage;
        @BindView(R.id.comment_user_name)
        TextView commentUserName;
        @BindView(R.id.comment_date)
        TextView commentDate;
        @BindView(R.id.comment_body)
        TextView commentBody;
        @BindView(R.id.message_image)
        ImageView messageBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(DocumentSnapshot snapshot) {
            final CommentModel commentModel = snapshot.toObject(CommentModel.class);

            Glide.with(commentUserImage.getContext())
                    .load(commentModel.getAuthor_image_url())
                    .into(commentUserImage);
            commentUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(commentModel.getAuthor_id(), AccountActivity.class);
                }
            });

            commentUserName.setText(commentModel.getAuthor_name());
            commentUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(commentModel.getAuthor_id(), AccountActivity.class);
                }
            });

            commentDate.setText(TimeAgo.getTimeAgo(commentModel.getCreated_at()));
            commentBody.setText(commentModel.getComment_body());
            messageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
                    intent.putExtra("userID" ,commentModel.getAuthor_id());
                    itemView.getContext().startActivity(intent);                }
            });
        }

        private void startActivity(String userID, Class context){
            Intent intent = new Intent(itemView.getContext(), context);
            intent.putExtra("user_id" ,userID);
            itemView.getContext().startActivity(intent);
        }
    }
}
