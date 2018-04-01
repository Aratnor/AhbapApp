package com.example.pasta.ahbapapp.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.interfaces.NewPostContract;
import com.example.pasta.ahbapapp.presenter.NewPostPresenter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewPostActivity extends AppCompatActivity implements NewPostContract.NewPostView,
    View.OnClickListener {

    private ProgressBar progressBar;
    private EditText content;
    private EditText city;
    private EditText category;
    private TextView contentErrorText;
    private TextView cityErrorText;
    private TextView catErrorText;
    private Uri imageUri;
    private ImageView imageView;
    private NewPostPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        presenter = new NewPostPresenter(this);

        imageUri = null;

        progressBar = findViewById(R.id.progressBarPost);
        contentErrorText = findViewById(R.id.contentErrorText);
        cityErrorText = findViewById(R.id.cityErrorText);
        catErrorText = findViewById(R.id.catErrorText);
        content = findViewById(R.id.contentEditText);
        city = findViewById(R.id.cityEditText);
        category = findViewById(R.id.catEditText);
        imageView = findViewById(R.id.newImage);
        findViewById(R.id.newImage).setOnClickListener(this);
        findViewById(R.id.addPost).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void contentError(@StringRes int resId) {
        contentErrorText.setText(resId);
    }

    @Override public void cityError(@StringRes int resId) {
        cityErrorText.setText(resId);
    }

    @Override public void categoryError(@StringRes int resId) {
        catErrorText.setText(resId);
    }

    @Override public void postError(String err) {
        Toast.makeText(NewPostActivity.this, err, Toast.LENGTH_SHORT).show();
    }

    @Override public void startMain() {
        Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override public void onClick(View v) {

        switch (v.getId()){
            case R.id.addPost:
                addPost();
                break;
            case R.id.newImage:
                newImage();
                break;
        }

    }

    private void addPost() {
        String contentS = content.getText().toString();
        String cityS = city.getText().toString();
        String categoryS = category.getText().toString();

        presenter.addPost(contentS, imageUri, cityS, categoryS);
    }


    private void newImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);
    }


}
