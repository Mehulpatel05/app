package com.example.aiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiapp.activities.ImageGenerationActivity;
import com.example.aiapp.activities.ObjectDetectionActivity;
import com.example.aiapp.activities.SentimentAnalysisActivity;
import com.example.aiapp.activities.TextGenerationActivity;
import com.example.aiapp.activities.VideoAnalysisActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user has completed onboarding
        SharedPreferences sharedPreferences = getSharedPreferences("aiapp_prefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("onboarding_complete", false)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        setupButtons();
    }

    private void setupButtons() {
        // Text Generation
        Button textGenerationBtn = findViewById(R.id.btn_text_generation);
        textGenerationBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TextGenerationActivity.class));
        });

        // Image Generation
        Button imageGenerationBtn = findViewById(R.id.btn_image_generation);
        imageGenerationBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ImageGenerationActivity.class));
        });

        // Sentiment Analysis
        Button sentimentAnalysisBtn = findViewById(R.id.btn_sentiment_analysis);
        sentimentAnalysisBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SentimentAnalysisActivity.class));
        });

        // Object Detection
        Button objectDetectionBtn = findViewById(R.id.btn_object_detection);
        objectDetectionBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ObjectDetectionActivity.class));
        });

        // Video Analysis
        Button videoAnalysisBtn = findViewById(R.id.btn_video_analysis);
        videoAnalysisBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, VideoAnalysisActivity.class));
        });
    }
}
