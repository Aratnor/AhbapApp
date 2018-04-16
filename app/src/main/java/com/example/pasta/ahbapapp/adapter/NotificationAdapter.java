package com.example.pasta.ahbapapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.NotificationModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends FirestoreAdapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationSelectedListener{
        void onNotificationSelected(DocumentSnapshot documentSnapshot);
    }

    OnNotificationSelectedListener mListener;
    public NotificationAdapter(Query query, OnNotificationSelectedListener onNotificationSelectedListener) {
        super(query);
        this.mListener = onNotificationSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.notification_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position),mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_user_image)
        CircleImageView user_image;
        @BindView(R.id.notification_image)
        CircleImageView notification_image;
        @BindView(R.id.notification_text)
        TextView notification_text;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot documentSnapshot, final OnNotificationSelectedListener listener) {
            NotificationModel model = documentSnapshot.toObject(NotificationModel.class);

            FirebaseFirestore.getInstance().collection("users")
                    .document(model.getFrom()).get().addOnSuccessListener(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Glide.with(user_image.getContext())
                            .load(documentSnapshot.get("image_url"))
                            .into(user_image);
                }
            });
            notification_text.setText(model.getMessage());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onNotificationSelected(documentSnapshot);
                    }
                }
            });
        }
    }
}
