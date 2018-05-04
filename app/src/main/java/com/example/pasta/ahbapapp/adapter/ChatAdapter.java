package com.example.pasta.ahbapapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.UserModel;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pasta on 3.05.2018.
 */

public class ChatAdapter extends FirestoreAdapter<ChatAdapter.ViewHolder>{
    public interface OnChatSelectedListener {

        void onChatSelected(DocumentSnapshot chat);

    }

    private ChatAdapter.OnChatSelectedListener mListener;

    public ChatAdapter(Query query, OnChatSelectedListener mListener ) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChatAdapter.ViewHolder(inflater.inflate(R.layout.chat_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_user_image)
        CircleImageView chatUserImage;
        @BindView(R.id.chat_user_name)
        TextView chatUserName;
        @BindView(R.id.chat_date)
        TextView chatDate;
        @BindView(R.id.chat_content)
        TextView chatContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot
                , final OnChatSelectedListener mListener) {

            FirebaseFirestore.getInstance().collection("users")
                    .document(snapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            UserModel user = snapshot.toObject(UserModel.class);

                            Glide.with(chatUserImage.getContext())
                                    .load(user.getImage_url())
                                    .into(chatUserImage);

                            chatUserName.setText(user.getName());
                        }
                    });

            Date date = (Date) snapshot.get("updated_at");
            String message = (String) snapshot.get("last_message");

            chatDate.setText(TimeAgo.getTimeAgo(date));
            chatContent.setText(message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onChatSelected(snapshot);
                    }
                }
            });
        }
    }
}
