package com.example.aiapp.api;

import android.util.Log;

import com.example.aiapp.config.ApiConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HuggingFaceApiClient {

    private static final String TAG = "HuggingFaceApiClient";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build();

    private static final Gson gson = new Gson();

    // ============ TEXT GENERATION ============
    public static String generateText(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", prompt);
        
        JsonObject parameters = new JsonObject();
        parameters.addProperty("max_length", ApiConfig.MAX_TOKENS);
        parameters.addProperty("temperature", ApiConfig.TEMPERATURE);
        parameters.addProperty("top_p", ApiConfig.TOP_P);
        requestBody.add("parameters", parameters);

        return callHuggingFaceAPI(ApiConfig.TEXT_GENERATION_URL, requestBody.toString());
    }

    // ============ IMAGE GENERATION ============
    public static String generateImage(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", prompt);

        return callHuggingFaceAPI(ApiConfig.IMAGE_GENERATION_URL, requestBody.toString());
    }

    // ============ SENTIMENT ANALYSIS ============
    public static String analyzeSentiment(String text) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", text);

        return callHuggingFaceAPI(ApiConfig.SENTIMENT_URL, requestBody.toString());
    }

    // ============ OBJECT DETECTION ============
    public static String detectObjects(byte[] imageData) throws IOException {
        // Create request with binary image data
        Request request = new Request.Builder()
                .url(ApiConfig.OBJECT_DETECTION_URL)
                .addHeader("Authorization", "Bearer " + ApiConfig.HF_API_KEY)
                .post(RequestBody.create(imageData, MediaType.parse("image/jpeg")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Object detection failed: " + response.code() + " " + response.message());
            }
            String responseBody = response.body().string();
            Log.d(TAG, "Object Detection Response: " + responseBody);
            return responseBody;
        }
    }

    // ============ VIDEO ANALYSIS ============
    public static String analyzeVideo(byte[] videoFrame) throws IOException {
        // Try primary model first
        try {
            return callVideoAnalysisAPI(ApiConfig.VIDEO_PRIMARY_URL, videoFrame);
        } catch (IOException e) {
            Log.w(TAG, "Primary video model failed, trying fallback: " + e.getMessage());
            // Fallback to frame-by-frame image analysis
            return callVideoAnalysisAPI(ApiConfig.VIDEO_FALLBACK_URL, videoFrame);
        }
    }

    private static String callVideoAnalysisAPI(String url, byte[] frameData) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + ApiConfig.HF_API_KEY)
                .post(RequestBody.create(frameData, MediaType.parse("image/jpeg")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Video analysis failed: " + response.code() + " " + response.message());
            }
            String responseBody = response.body().string();
            Log.d(TAG, "Video Analysis Response: " + responseBody);
            return responseBody;
        }
    }

    // ============ GENERIC API CALL ============
    private static String callHuggingFaceAPI(String url, String jsonBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + ApiConfig.HF_API_KEY)
                .addHeader("Content-Type", ApiConfig.CONTENT_TYPE_JSON)
                .post(RequestBody.create(jsonBody, MediaType.parse(ApiConfig.CONTENT_TYPE_JSON)))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                Log.e(TAG, "API Error " + response.code() + ": " + errorBody);
                throw new IOException("API request failed: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            Log.d(TAG, "API Response: " + responseBody);
            return responseBody;
        }
    }

    // ============ RESPONSE PARSING ============
    public static String parseTextGenerationResponse(String response) {
        try {
            JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
            if (jsonArray.size() > 0) {
                JsonObject firstItem = jsonArray.get(0).getAsJsonObject();
                return firstItem.get("generated_text").getAsString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing text generation response: " + e.getMessage());
        }
        return "No response generated";
    }

    public static String parseImageGenerationResponse(String response) {
        // Image generation returns base64 encoded image
        try {
            JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
            if (jsonArray.size() > 0) {
                return jsonArray.get(0).getAsString(); // Returns base64
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing image response: " + e.getMessage());
        }
        return null;
    }

    public static String parseSentimentResponse(String response) {
        try {
            JsonArray outerArray = gson.fromJson(response, JsonArray.class);
            if (outerArray.size() > 0) {
                JsonArray innerArray = outerArray.get(0).getAsJsonArray();
                if (innerArray.size() > 0) {
                    JsonObject sentiment = innerArray.get(0).getAsJsonObject();
                    String label = sentiment.get("label").getAsString();
                    double score = sentiment.get("score").getAsDouble();
                    return label + " (" + String.format("%.2f", score * 100) + "%)";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing sentiment response: " + e.getMessage());
        }
        return "Unable to analyze sentiment";
    }

    public static String parseObjectDetectionResponse(String response) {
        try {
            JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < jsonArray.size() && i < 5; i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                String label = obj.get("label").getAsString();
                double score = obj.get("score").getAsDouble();
                result.append(label).append(" (").append(String.format("%.0f", score * 100)).append("%)\n");
            }
            return result.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing object detection response: " + e.getMessage());
        }
        return "No objects detected";
    }

    public static String parseVideoAnalysisResponse(String response) {
        try {
            JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
            if (jsonArray.size() > 0) {
                JsonObject firstItem = jsonArray.get(0).getAsJsonObject();
                String label = firstItem.get("label").getAsString();
                double score = firstItem.get("score").getAsDouble();
                return label + " (" + String.format("%.0f", score * 100) + "%)";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing video analysis response: " + e.getMessage());
        }
        return "Unable to analyze video";
    }
}
