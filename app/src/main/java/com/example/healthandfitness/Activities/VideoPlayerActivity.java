package com.example.healthandfitness.Activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Initialize the WebView
        webView = findViewById(R.id.webView);

        // Get the video URL passed from the previous activity
        String videoUrl = getIntent().getStringExtra("video_url");

        if (videoUrl != null && !videoUrl.isEmpty()) {
            setupWebView(videoUrl);
        } else {
            // If no URL is passed, close the activity or show an error
            finish();
        }
    }

    private void setupWebView(String videoUrl) {
        // Enable JavaScript for the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Load the video URL
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(videoUrl);
    }

    @Override
    public void onBackPressed() {
        // Handle back navigation in WebView
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
