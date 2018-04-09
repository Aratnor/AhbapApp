package com.example.pasta.ahbapapp.newpost;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by pasta on 29.03.2018.
 */

public class NewPostPresenter implements NewPostContract.NewPostPresenter {

    private static final String AUTHOR_ID = "author_id";
    private static final String AUTHOR_NAME = "author_name";
    private static final String AUTHOR_IMAGE = "author_image";
    private static final String CONTENT = "content";
    private static final String CITY = "city";
    private static final String CATEGORY = "category";
    private static final String IMAGE_URL = "image_url";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private NewPostContract.NewPostView mView;
    private Map<String, Object> postMap;
    private String currentUserID;
    private FirebaseFirestore firebaseFirestore;
    private String contentError = "";


    public NewPostPresenter(NewPostContract.NewPostView mView) {
        this.mView = mView;
        postMap = new HashMap<>();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setUserData();
    }

    @Override public void uploadImageAndPost(final String content, Uri imageUri, final String city, final String category) {

        final String randomName = UUID.randomUUID().toString();
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

        byte[] imageData = compressImage(imageUri);

        UploadTask filePath = mStorageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
        filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    final String imageUrl = task.getResult().getDownloadUrl().toString();
                    uploadPost(content,imageUrl, city,category);
                }
            }
        });
    }

    private byte[] compressImage(Uri imageUri) {
        Bitmap compressedImageFile = null;
        File newImageFile = new File(imageUri.getPath());
        try {
            compressedImageFile = new Compressor((Context) mView)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50,baos);
        byte[] imageData = baos.toByteArray();
        return imageData;
    }

    private void setUserData() {
        firebaseFirestore.collection("users").document(currentUserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String name = task.getResult().getString("name");
                            String imageUrl = task.getResult().getString("image_url");
                            postMap.put(AUTHOR_ID, currentUserID);
                            postMap.put(AUTHOR_NAME,  name);
                            postMap.put(AUTHOR_IMAGE, imageUrl);
                        }
                        else{
                            //TODO Error handling
                        }
                    }
                });
    }

    @Override public void addPost(String content, Uri imageUri, String city, String category) {
        if(TextUtils.isEmpty(content) || content.length() < 3 || content.length() > 1000
                || TextUtils.isEmpty(city) || city.equals("İl Seçiniz")
                || TextUtils.isEmpty(category) || category.equals("Kategori")){

            if (TextUtils.isEmpty(content)) contentError += ("İçerik boş bırakılamaz."+ "\n");
            else if(content.length() < 3) contentError += ("İçerik 3 karakterden küçük olamaz."+ "\n");
            else if (content.length() > 1000) contentError += ("İçerik 1000 karakterden büyük olamaz."+ "\n");

            if (TextUtils.isEmpty(city) || city.equals("İl Seçiniz")) contentError += ("Şehir boş bırakılamaz."+ "\n");
            if (TextUtils.isEmpty(category) || category.equals("Kategori")) contentError += ("Kategori boş bırakılamaz."+ "\n");

            mView.contentError(contentError);
            contentError = "";
        }
        else{
            if (postMap.get(AUTHOR_ID) != null && postMap.get(AUTHOR_NAME) != null
                    && postMap.get(AUTHOR_IMAGE) != null  ){

                mView.showProgress();
                if (imageUri != null){
                    uploadImageAndPost(content, imageUri,city,category);
                }
                else{
                    String imageUrl = "";
                    uploadPost(content,imageUrl, city, category);
                }
            }
        }
    }

    @Override public void uploadPost(String content, String imageUrl, String city, String category) {
        //TODO add author name and image url to post
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
                    mView.hideProgress();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                mView.uploadError(e.getMessage());
                mView.hideProgress();
                Log.d("OnComplete","exception" + e.getMessage());
            }
        });
    }
}
