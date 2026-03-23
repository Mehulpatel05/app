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

public class TextGenerationActivity extends AppCompatActivity {

    private EditText promptInput;
    private TextView resultOutput;
    private Button generateButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_generation);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        promptInput = findViewById(R.id.et_prompt);
        resultOutput = findViewById(R.id.tv_result);
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
            new TextGenerationTask().execute(prompt);
        });
    }

    private class TextGenerationTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            generateButton.setEnabled(false);
            resultOutput.setText("Generating...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String prompt = params[0];
                String response = HuggingFaceApiClient.generateText(prompt);
                return HuggingFaceApiClient.parseTextGenerationResponse(response);
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(ProgressBar.GONE);
            generateButton.setEnabled(true);
            resultOutput.setText(result);
        }
    }
}
