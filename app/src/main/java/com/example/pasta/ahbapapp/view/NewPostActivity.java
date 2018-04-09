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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.interfaces.NewPostContract;
import com.example.pasta.ahbapapp.presenter.NewPostPresenter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewPostActivity extends AppCompatActivity implements NewPostContract.NewPostView {

    private ProgressBar progressBar;
    private EditText content;
    private EditText city;
    private EditText category;
    private TextView contentErrorText;
    private TextView cityErrorText;
    private TextView catErrorText;
    private Uri imageUri;
    private ImageView newImage;
    private ImageView newImagePreview;
    private NewPostPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        imageUri = null;
        initializeView();
        initializeNewImageBtn();
        initializeAddBtn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new NewPostPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter = null;
    }

    private void initializeView() {
        //progressBar = findViewById(R.id.progressBarPost);
        //contentErrorText = findViewById(R.id.contentErrorText);
        //cityErrorText = findViewById(R.id.cityErrorText);
        //catErrorText = findViewById(R.id.catErrorText);
        content = findViewById(R.id.contentEditText);
        //city = findViewById(R.id.cityEditText);
        //category = findViewById(R.id.catEditText);
        newImage = findViewById(R.id.newImage);
        newImagePreview = findViewById(R.id.postImagePreview);
    }

    private void initializeNewImageBtn() {
        findViewById(R.id.newImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);
            }
        });
    }

    private void initializeAddBtn() {
        findViewById(R.id.addPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentS = content.getText().toString();
                String cityS = city.getText().toString();
                String categoryS = category.getText().toString();

                presenter.addPost(contentS, imageUri, cityS, categoryS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(8));
                Glide.with(getApplicationContext()).load(imageUri).apply(requestOptions).into(newImagePreview);
                newImagePreview.setVisibility(View.VISIBLE);

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
}
