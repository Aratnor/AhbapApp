package com.example.pasta.ahbapapp.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.Message;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends FirestoreAdapter<MessageAdapter.ViewHolder> {
    public MessageAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.message_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.message_date)
        TextView messageDate;
        @BindView(R.id.message_layout)
        LinearLayout messageLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(DocumentSnapshot snapShot) {
            Message messageSnapshot = snapShot.toObject(Message.class);

            message.setText(messageSnapshot.getMessage());

            messageDate.setText(TimeAgo.getTimeAgo(messageSnapshot.getTimeStamp()));

            if(messageSnapshot.getSender()){
                message.setBackgroundColor(Color.parseColor("#FF03A9F4"));
                messageLayout.setGravity(Gravity.END);
            }
            else {
                message.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        }
    }
}
