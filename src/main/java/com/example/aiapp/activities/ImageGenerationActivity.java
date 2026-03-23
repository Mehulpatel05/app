package com.example.aiapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiapp.R;
import com.example.aiapp.api.HuggingFaceApiClient;

import java.io.IOException;

public class ImageGenerationActivity extends AppCompatActivity {

    private EditText promptInput;
    private ImageView generatedImageView;
    private Button generateButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generation);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        promptInput = findViewById(R.id.et_prompt);
        generatedImageView = findViewById(R.id.iv_generated_image);
        generateButton = findViewById(R.id.btn_generate);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        generateButton.setOnClickListener(v -> {
            String prompt = promptInput.getText().toString().trim();
            if (prompt.isEmpty()) {
                Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show();
                return;
            }
            new ImageGenerationTask().execute(prompt);
        });
    }

    private class ImageGenerationTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            generateButton.setEnabled(false);
            generatedImageView.setImageResource(android.R.color.transparent);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String prompt = params[0];
                String response = HuggingFaceApiClient.generateImage(prompt);
                
                // Parse base64 image from response
                String base64Image = HuggingFaceApiClient.parseImageGenerationResponse(response);
                
                if (base64Image == null) {
                    return null;
                }

                // Decode base64 to bitmap
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(ImageGenerationActivity.this, 
                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(ProgressBar.GONE);
            generateButton.setEnabled(true);
            
            if (bitmap != null) {
                generatedImageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(ImageGenerationActivity.this, 
                    "Failed to generate image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
