package com.example.pasta.ahbapapp.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.interfaces.NewPostContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pasta on 29.03.2018.
 */

public class NewPostPresenter implements NewPostContract.NewPostPresenter {

    private NewPostContract.NewPostView mView;

    private static final String USER_ID = "user_id";
    private static final String CONTENT = "content";
    private static final String CITY = "city";
    private static final String CATEGORY = "category";
    private static final String IMAGE_URL = "image_url";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private String currentUserID;
    private FirebaseFirestore firebaseFirestore;


    public NewPostPresenter(NewPostContract.NewPostView mView) {
        this.mView = mView;
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override public String uploadImage(Uri imageUri) {
        return "";
    }

    @Override public void addPost(String content, Uri imageUri, String city, String category) {
        if(TextUtils.isEmpty(content) || content.length() < 3 || content.length() > 1000 || TextUtils.isEmpty(city)
            || TextUtils.isEmpty(category)){

            if (TextUtils.isEmpty(content)) mView.contentError(R.string.empty_content_error);
            else if(content.length() < 3) mView.contentError(R.string.small_content_error);
            else if (content.length() > 1000) mView.contentError(R.string.large_content_error);

            if (TextUtils.isEmpty(city)) mView.cityError(R.string.city_empty_error);
            if (TextUtils.isEmpty(category)) mView.categoryError(R.string.category_empty_error);
        }
        else{
            mView.showProgress();
            String imageUrl = "";

            if (imageUri != null){
                imageUrl = uploadImage(imageUri);
            }

            Map<String, Object> postMap = new HashMap<>();
            postMap.put(USER_ID, currentUserID);
            postMap.put(CONTENT, content);
            postMap.put(CITY, city);
            postMap.put(CATEGORY, category);
            postMap.put(IMAGE_URL, imageUrl);
            postMap.put(CREATED_AT, FieldValue.serverTimestamp());
            postMap.put(UPDATED_AT, FieldValue.serverTimestamp());

            firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override public void onComplete(@NonNull Task<DocumentReference> task) {
                    Log.d("OnComplete","before if");
                    if (task.isSuccessful()){
                        mView.startMain();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override public void onFailure(@NonNull Exception e) {
                    mView.postError(e.getMessage());
                    Log.d("OnComplete","exception" + e.getMessage());
                }
            });
            mView.hideProgress();
        }
    }
}
