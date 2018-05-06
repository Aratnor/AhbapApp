package com.example.pasta.ahbapapp.newpost;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.pasta.ahbapapp.model.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by pasta on 29.03.2018.
 */

public class NewPostPresenter implements NewPostContract.NewPostPresenter {

    private NewPostContract.NewPostView mView;
    private PostModel post;
    private String currentUserID;
    private FirebaseFirestore firebaseFirestore;
    private String contentError = "";


    public NewPostPresenter(NewPostContract.NewPostView mView) {
        this.mView = mView;
        post = new PostModel();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
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

    @Override public void uploadPost(String content, String imageUrl, String city, String category) {

        post.setContent(content);
        post.setCity(city);
        post.setCategory(category);
        post.setImage_url(imageUrl);
        post.setComment_count(0l);
        post.setCreated_at(new Date());
        post.setUpdated_at(new Date());

        firebaseFirestore.collection("posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                HashMap<String,String> map = new HashMap<>();
                map.put("currentID",currentUserID);
                documentReference.collection("user_ids").add(map);
                mView.sendBack();
                mView.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                mView.uploadError(e.getMessage());
                mView.hideProgress();
                Log.d("OnComplete","exception" + e.getMessage());
            }
        });
    }

    @Override
    public void userData(String userID, String userName, String userImage) {
        if (currentUserID.equals(userID)){
            post.setAuthor_id(userID);
            post.setAuthor_name(userName);
            post.setAuthor_image(userImage);
        }
    }
}
