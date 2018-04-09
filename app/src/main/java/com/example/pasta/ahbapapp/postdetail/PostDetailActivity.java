package com.example.pasta.ahbapapp.postdetail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pasta.ahbapapp.R;

public class PostDetailActivity extends AppCompatActivity {

    public static final String POST_ID = "post_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
    }
}
