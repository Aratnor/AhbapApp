package com.example.pasta.ahbapapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.CommentModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Date;

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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(DocumentSnapshot snapshot) {
            CommentModel commentModel = snapshot.toObject(CommentModel.class);

            Glide.with(commentUserImage.getContext())
                    .load(commentModel.getAuthor_image_url())
                    .into(commentUserImage);
            commentUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "User Image Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            commentUserName.setText(commentModel.getAuthor_name());
            commentUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Username Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            try{
                long millisecond = commentModel.getCreated_at().getTime();
                String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
                commentDate.setText(dateString);
            }catch (Exception e){
                Log.d("commentAdapter", "time problem");
            }

            commentBody.setText(commentModel.getComment_body());
        }
    }
}
