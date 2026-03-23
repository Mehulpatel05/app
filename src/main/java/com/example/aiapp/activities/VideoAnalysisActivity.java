package com.example.aiapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aiapp.R;
import com.example.aiapp.api.HuggingFaceApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class VideoAnalysisActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;

    private VideoView videoView;
    private TextView analysisResultsView;
    private Button recordButton;
    private Button galleryButton;
    private Button analyzeButton;
    private ProgressBar progressBar;
    private Uri selectedVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analysis);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        videoView = findViewById(R.id.vv_selected_video);
        analysisResultsView = findViewById(R.id.tv_analysis_results);
        recordButton = findViewById(R.id.btn_record);
        galleryButton = findViewById(R.id.btn_gallery);
        analyzeButton = findViewById(R.id.btn_analyze);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        recordButton.setOnClickListener(v -> checkCameraPermission());
        
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });
        
        analyzeButton.setOnClickListener(v -> {
            if (selectedVideoUri == null) {
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
                return;
            }
            new VideoAnalysisTask().execute(selectedVideoUri);
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            recordVideo();
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordVideo();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_REQUEST_CODE) {
                selectedVideoUri = data.getData();
                videoView.setVideoURI(selectedVideoUri);
                analysisResultsView.setText("");
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                selectedVideoUri = data.getData();
                videoView.setVideoURI(selectedVideoUri);
                analysisResultsView.setText("");
            }
        }
    }

    private class VideoAnalysisTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            analyzeButton.setEnabled(false);
            analysisResultsView.setText("Analyzing video (extracting key frame)...");
        }

        @Override
        protected String doInBackground(Uri... uris) {
            try {
                Uri videoUri = uris[0];
                
                // Extract first frame from video
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(VideoAnalysisActivity.this, videoUri);
                Bitmap frame = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
                retriever.release();

                if (frame == null) {
                    return "Error: Could not extract video frame";
                }

                // Convert frame to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                frame.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] frameData = stream.toByteArray();

                // Analyze frame using video model (with fallback)
                String response = HuggingFaceApiClient.analyzeVideo(frameData);
                return HuggingFaceApiClient.parseVideoAnalysisResponse(response);
                
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(ProgressBar.GONE);
            analyzeButton.setEnabled(true);
            analysisResultsView.setText(result);
        }
    }
}
