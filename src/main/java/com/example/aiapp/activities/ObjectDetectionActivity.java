package com.example.aiapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aiapp.R;
import com.example.aiapp.api.HuggingFaceApiClient;
import com.example.aiapp.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ObjectDetectionActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;

    private ImageView selectedImageView;
    private TextView detectionResultsView;
    private Button cameraButton;
    private Button galleryButton;
    private Button detectButton;
    private ProgressBar progressBar;
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        selectedImageView = findViewById(R.id.iv_selected_image);
        detectionResultsView = findViewById(R.id.tv_detection_results);
        cameraButton = findViewById(R.id.btn_camera);
        galleryButton = findViewById(R.id.btn_gallery);
        detectButton = findViewById(R.id.btn_detect);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        cameraButton.setOnClickListener(v -> checkCameraPermission());
        
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
        
        detectButton.setOnClickListener(v -> {
            if (selectedBitmap == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }
            new ObjectDetectionTask().execute(selectedBitmap);
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                selectedImageView.setImageBitmap(selectedBitmap);
                detectionResultsView.setText("");
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedUri = data.getData();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                    selectedImageView.setImageBitmap(selectedBitmap);
                    detectionResultsView.setText("");
                } catch (IOException e) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ObjectDetectionTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            detectButton.setEnabled(false);
            detectionResultsView.setText("Detecting objects...");
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            try {
                Bitmap bitmap = bitmaps[0];
                
                // Convert bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageData = stream.toByteArray();

                // Call API
                String response = HuggingFaceApiClient.detectObjects(imageData);
                return HuggingFaceApiClient.parseObjectDetectionResponse(response);
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(ProgressBar.GONE);
            detectButton.setEnabled(true);
            detectionResultsView.setText(result);
        }
    }
}
