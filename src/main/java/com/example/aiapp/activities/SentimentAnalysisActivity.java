package com.example.aiapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiapp.R;
import com.example.aiapp.api.HuggingFaceApiClient;

import java.io.IOException;

public class SentimentAnalysisActivity extends AppCompatActivity {

    private EditText textInput;
    private TextView resultOutput;
    private Button analyzeButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_analysis);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        textInput = findViewById(R.id.et_text);
        resultOutput = findViewById(R.id.tv_result);
        analyzeButton = findViewById(R.id.btn_analyze);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        analyzeButton.setOnClickListener(v -> {
            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter text to analyze", Toast.LENGTH_SHORT).show();
                return;
            }
            new SentimentAnalysisTask().execute(text);
        });
    }

    private class SentimentAnalysisTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            analyzeButton.setEnabled(false);
            resultOutput.setText("Analyzing sentiment...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String text = params[0];
                String response = HuggingFaceApiClient.analyzeSentiment(text);
                return HuggingFaceApiClient.parseSentimentResponse(response);
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(ProgressBar.GONE);
            analyzeButton.setEnabled(true);
            resultOutput.setText(result);
        }
    }
}
