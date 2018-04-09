package com.example.pasta.ahbapapp.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewPostActivity extends AppCompatActivity implements NewPostContract.NewPostView {

    @BindView(R.id.newPostProgress)
    ProgressBar progressBar;
    @BindView(R.id.contentEditText)
    EditText content;
    @BindView(R.id.newImage)
    ImageView newImage;
    @BindView(R.id.postImagePreview)
    ImageView newImagePreview;
    @BindView(R.id.addPost)
    Button addBtn;
    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;
    @BindView(R.id.spinnerCat)
    Spinner spinnerCat;
    @BindView(R.id.newPostErrors)
    TextView contentErrors;

    private Uri imageUri;
    private NewPostPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        ButterKnife.bind(this);

        imageUri = null;
        newImageClickEvent();
        addBtnClickEvent();
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

    private void newImageClickEvent() {
        newImage.setOnClickListener(new View.OnClickListener() {
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

    private void addBtnClickEvent() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sContent = content.getText().toString();
                String sCity = spinnerCity.getSelectedItem().toString();
                String sCategory = spinnerCat.getSelectedItem().toString();
                presenter.addPost(sContent, imageUri, sCity, sCategory);
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

    @Override
    public void contentError(String err) {
        contentErrors.setText(err);
        contentErrors.setVisibility(View.VISIBLE);
    }
    @Override
    public void uploadError(String err) {
        Toast.makeText(NewPostActivity.this, err, Toast.LENGTH_SHORT).show();
    }

    @Override public void startMain() {
        Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
