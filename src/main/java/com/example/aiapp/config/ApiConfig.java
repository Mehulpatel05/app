package com.example.aiapp.config;

/**
 * API Configuration for Backend Server
 * All requests route through the Node.js/Express backend for better reliability,
 * caching, and easy model management
 */
public class ApiConfig {

    // ============ BACKEND SERVER CONFIGURATION ============
    // Update these based on your deployment environment
    // Development (Emulator): http://10.0.2.2:3001
    // Development (Device): http://YOUR_LOCAL_IP:3001
    // Production: https://your-backend-domain.com
    public static final String BACKEND_BASE_URL = "http://10.0.2.2:3001";

    // ============ BACKEND API ENDPOINTS ============
    // All feature endpoints are routed through the backend
    public static final String TEXT_GENERATION_URL = BACKEND_BASE_URL + "/api/text-generation";
    public static final String IMAGE_GENERATION_URL = BACKEND_BASE_URL + "/api/image-generation";
    public static final String SENTIMENT_ANALYSIS_URL = BACKEND_BASE_URL + "/api/sentiment-analysis";
    public static final String OBJECT_DETECTION_URL = BACKEND_BASE_URL + "/api/object-detection";
    public static final String VIDEO_ANALYSIS_URL = BACKEND_BASE_URL + "/api/video-analysis";
    public static final String VIDEO_GENERATION_URL = BACKEND_BASE_URL + "/api/video-generation";
    public static final String HEALTH_CHECK_URL = BACKEND_BASE_URL + "/api/health";

    // ============ MODELS (Configured on Backend) ============
    // These are for reference only - actual models are managed in backend/config/ApiConfig.js
    public static final String TEXT_GENERATION_MODEL = "mistralai/Mistral-7B-Instruct-v0.1";
    public static final String IMAGE_GENERATION_MODEL = "stabilityai/stable-diffusion-2-1";
    public static final String SENTIMENT_ANALYSIS_MODEL = "distilbert-base-uncased-finetuned-sst-2-english";
    public static final String OBJECT_DETECTION_MODEL = "facebook/detr-resnet-50";
    public static final String VIDEO_ANALYSIS_MODEL = "MCG-NJU/VideoMAE-base";
    public static final String VIDEO_GENERATION_MODEL = "stabilityai/stable-video-diffusion-img2vid";

    // ============ REQUEST TIMEOUTS (milliseconds) ============
    public static final int REQUEST_TIMEOUT = 30000;           // Standard requests
    public static final int IMAGE_TIMEOUT = 60000;              // Image generation
    public static final int VIDEO_ANALYSIS_TIMEOUT = 120000;   // Video analysis
    public static final int VIDEO_GENERATION_TIMEOUT = 180000; // Video generation (longest)

    // ============ HELPER METHOD ============
    /**
     * Get the full URL to retrieve generated content
     * @param contentId The unique ID returned from generation endpoint
     * @return Full URL to download/stream the content
     */
    public static String getContentUrl(String contentId) {
        return BACKEND_BASE_URL + "/api/content/" + contentId;
    }
}
