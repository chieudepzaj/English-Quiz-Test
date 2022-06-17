package com.example.quiz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quiz.Constant;
import com.example.quiz.R;
import com.example.quiz.helper.Utils;

public class LearningZoneActivity extends AppCompatActivity {


    public ProgressBar prgLoading;
    public WebView mWebView;
    public String id;
    public Toolbar toolbar;
    TextView tvStartGame;


    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_privacy_policy);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        prgLoading = findViewById(R.id.prgLoading);
        tvStartGame = findViewById(R.id.tvStartGame);
        tvStartGame.setVisibility(View.VISIBLE);
        mWebView = findViewById(R.id.webView1);

        try {

            mWebView.setClickable(true);
            mWebView.setFocusableInTouchMode(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            getSupportActionBar().setTitle(Constant.cate_name);
            /*GetPrivacyAndTerms();*/
            if (Utils.isNetworkAvailable(this)) {
                if (!prgLoading.isShown()) {
                    prgLoading.setVisibility(View.VISIBLE);
                }
                mWebView.setVerticalScrollBarEnabled(true);
                mWebView.loadDataWithBaseURL("", getIntent().getStringExtra("message"), "text/html", "UTF-8", "");
                mWebView.setBackgroundColor(getResources().getColor(R.color.bg_color));
            } else {
                //setSnackBar();
            }
            prgLoading.setVisibility(View.GONE);
            tvStartGame.setOnClickListener(v -> {
                System.out.println("==== learn id "+id);
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("fromQue", "learning");
                intent.putExtra("learning_id", id);
                startActivity(intent);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();

    }
}