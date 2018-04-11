package com.example.pasta.ahbapapp.adapter;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.postlist.HomeFragment;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by pasta on 6.04.2018.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {

    private static final String TAG = "Firestore Adapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();
    private DocumentSnapshot lastVisible;
    private boolean isFirstLoaded;
    private Activity activity;
    public FirestoreAdapter(Query query, Activity activity) {
        mQuery = query;
        isFirstLoaded = true;
        this.activity = activity;
        mRegistration = mQuery.addSnapshotListener(this);
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
        mSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existinkodig data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        isFirstLoaded = true;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }
    public DocumentSnapshot getLastVisible(){
        return lastVisible;
    }


    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    protected void onError(FirebaseFirestoreException e) {}

    protected void onDataChanged() {}
    @Override
    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }
        // Dispatch the event
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            // Snapshot of the changed document
            Log.d(TAG, "OnEvent");

            if (isFirstLoaded) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    Log.d(TAG, "firsPageLoaded");
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
            }

            DocumentSnapshot snapshot = change.getDocument();
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }
        isFirstLoaded = false;
        onDataChanged();
    }

    protected void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
        Log.d(TAG, "onDocumentAdded");
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
            Log.d(TAG, "onDocumentModified");

        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
            Log.d(TAG, "onDocumentModified");
        }
    }

    protected void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
        Log.d(TAG, "onDocumentRemoved");
    }

    public void loadMore(Query query){
        query.addSnapshotListener(activity,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            mSnapshots.add(getItemCount(),doc.getDocument());
                            notifyItemInserted(getItemCount());
                            Log.d(TAG, "loadMore");
                        }
                    }
                }
            }
        });
    }
}
