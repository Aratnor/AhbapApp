package com.example.pasta.ahbapapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.Message;
import com.example.pasta.ahbapapp.util.TimeAgo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends FirestoreAdapter<MessageAdapter.ViewHolder> {
    public MessageAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0: return new ViewHolder(inflater.inflate(R.layout.message_list_sender_item,parent,false));
            case 1: return new ViewHolder(inflater.inflate(R.layout.message_list_receiver_item,parent,false));
            default:return new ViewHolder(inflater.inflate(R.layout.message_list_receiver_item,parent,false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        boolean sender = (boolean) getSnapshot(position).get("sender");
        if (sender) return 0;
        else return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_list)
        TextView message;
        @BindView(R.id.message_date)
       TextView messageDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(DocumentSnapshot snapShot) {
            Message messageSnapshot = snapShot.toObject(Message.class);

            message.setText(messageSnapshot.getMessage());
            messageDate.setText(TimeAgo.getTimeAgo(messageSnapshot.getTimeStamp()));
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messageDate.getVisibility() == View.VISIBLE){
                        messageDate.setVisibility(View.GONE);
                    }
                    else{
                        messageDate.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
