package com.example.pasta.ahbapapp.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pasta on 3.04.2018.
 */

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postList;
    private FirebaseFirestore firebaseFirestore;

    public PostRecyclerAdapter(List<PostModel> postList){
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String contentData = postList.get(position).getContent();
        holder.setContentText(contentData);
        String imageUrl = postList.get(position).getImage_url();
        if (imageUrl == null || !imageUrl.isEmpty()) holder.setPostImage(imageUrl);
        String postUserId = postList.get(position).getUser_id();
        setUserData(holder,postUserId);
        Date postCreatedAt = postList.get(position).getCreated_at();
        String createdAtString = DateFormat.format("MM/dd/yyyy", postCreatedAt).toString();
        holder.setTime(createdAtString);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void setUserData(@NonNull final ViewHolder holder, String postUserId) {

        firebaseFirestore.collection("users").document(postUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String name = task.getResult().getString("name");
                            String imageUrl = task.getResult().getString("image_url");
                            holder.setUserData(name,imageUrl);
                        }
                        else{
                            //TODO Error handling
                        }
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView contentView;
        private ImageView postImageView;
        private TextView postDate;
        private TextView postUserName;
        private CircleImageView postUserImage;
        private ImageView postCommentBtn;
        //private TextView postCommentCount;

        ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            postCommentBtn = mView.findViewById(R.id.post_comment_icon);
        }

        void setContentText(String contentText){
            contentView = mView.findViewById(R.id.post_content);
            contentView.setText(contentText);
        }

        void setPostImage(String downloadUri){
            postImageView = mView.findViewById(R.id.post_image);
            postImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(downloadUri).into(postImageView);
        }

        void setTime(String date) {
            postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }

        void setUserData(String name, String image){
            postUserImage = mView.findViewById(R.id.post_user_image);
            postUserName = mView.findViewById(R.id.post_user_name);

            postUserName.setText(name);
            Glide.with(context).load(image).into(postUserImage);
        }
    }
}
