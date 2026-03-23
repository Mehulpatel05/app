# Backend Integration Guide

## Overview

The Android app communicates with a Node.js/Express backend server that manages all AI model interactions with Hugging Face API. This architecture provides:

- ✅ **Request Caching** - Avoid duplicate API calls for same inputs
- ✅ **Model Management** - Easy to upgrade/change models without updating app
- ✅ **Video Generation Pipeline** - Text → Image → Video
- ✅ **Error Handling** - Automatic fallbacks and retry logic
- ✅ **Content Storage** - Generated videos and images stored on server
- ✅ **Analytics** - Log all API calls and model usage

---

## Architecture

```
┌─────────────────┐
│   Android App   │
│   (Java/XML)    │
└────────┬────────┘
         │ HTTP/JSON
         ▼
┌─────────────────────────────────────────┐
│  Express.js Backend Server              │
│  - Request validation                   │
│  - Caching layer (SQLite)               │
│  - Error handling & fallbacks           │
└────────┬────────────────────────────────┘
         │ HTTP
         ▼
┌──────────────────────────────────────────┐
│  Hugging Face Inference API              │
│  - Text Generation (Mistral)             │
│  - Image Generation (Stable Diffusion)   │
│  - Sentiment Analysis (DistilBERT)       │
│  - Object Detection (DETR)               │
│  - Video Analysis (VideoMAE)             │
│  - Video Generation (SVD)                │
└──────────────────────────────────────────┘
```

---

## Setup Instructions

### 1. Prerequisites

- Node.js 16+ and npm/pnpm
- Hugging Face API key (free at https://huggingface.co/settings/tokens)
- 500MB+ disk space for generated content

### 2. Backend Installation

```bash
# Navigate to backend directory
cd ai/backend

# Install dependencies
pnpm install
# or npm install

# Copy environment template
cp .env.example .env

# Edit .env with your settings
nano .env
```

### 3. Configure Environment Variables

Edit `ai/backend/.env`:

```env
# REQUIRED: Your Hugging Face API key
HUGGING_FACE_API_KEY=hf_xxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Server settings
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://10.0.2.2:8080

# Database location
DATABASE_PATH=./data/app.db

# Optional: Use Replicate for video generation (higher quality)
REPLICATE_API_KEY=r8_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### 4. Start Backend Server

```bash
# Development with auto-reload
pnpm run dev

# Or production start
pnpm start
```

Expected output:
```
╔════════════════════════════════════════╗
║      AI App Backend Server Started     ║
║                                        ║
║  Server: http://localhost:3001         ║
║  Environment: development              ║
║  Database: ./data/app.db               ║
│
│  Available Endpoints:
│  • POST   /api/text-generation
│  • POST   /api/image-generation
│  ...
╚════════════════════════════════════════╝
```

### 5. Configure Android App

Update `ai/src/main/java/com/example/aiapp/config/ApiConfig.java`:

```java
// For local development on emulator
public static final String BACKEND_BASE_URL = "http://10.0.2.2:3001";

// For device on same Wi-Fi
public static final String BACKEND_BASE_URL = "http://192.168.x.x:3001";

// For production
public static final String BACKEND_BASE_URL = "https://your-domain.com";
```

---

## API Endpoints Reference

### Text Generation

**Endpoint:** `POST /api/text-generation`

**Request:**
```json
{
  "prompt": "Write a short story about AI"
}
```

**Response:**
```json
{
  "success": true,
  "text": "Once upon a time...",
  "model": "mistralai/Mistral-7B-Instruct-v0.1",
  "fromCache": false
}
```

**Timeout:** 30 seconds

---

### Image Generation

**Endpoint:** `POST /api/image-generation`

**Request:**
```json
{
  "prompt": "A futuristic city at sunset, cyberpunk style"
}
```

**Response:**
```json
{
  "success": true,
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "url": "/api/content/550e8400-e29b-41d4-a716-446655440000",
  "model": "stabilityai/stable-diffusion-2-1",
  "fromCache": false
}
```

**Download Image:**
```
GET /api/content/550e8400-e29b-41d4-a716-446655440000
```

**Timeout:** 60 seconds

---

### Sentiment Analysis

**Endpoint:** `POST /api/sentiment-analysis`

**Request:**
```json
{
  "text": "I absolutely love this product!"
}
```

**Response:**
```json
{
  "success": true,
  "sentiment": "POSITIVE",
  "confidence": "99.85",
  "allScores": [
    { "label": "POSITIVE", "score": 0.9985 },
    { "label": "NEGATIVE", "score": 0.0015 }
  ],
  "model": "distilbert-base-uncased-finetuned-sst-2-english",
  "fromCache": false
}
```

**Timeout:** 30 seconds

---

### Object Detection

**Endpoint:** `POST /api/object-detection`

**Request:** Multipart form data with image

```
Content-Type: multipart/form-data
image: [binary image file]
```

**Response:**
```json
{
  "success": true,
  "detections": [
    {
      "label": "person",
      "score": 0.98,
      "box": { "xmin": 100, "ymin": 50, "xmax": 300, "ymax": 400 }
    },
    {
      "label": "dog",
      "score": 0.95,
      "box": { "xmin": 320, "ymin": 200, "xmax": 450, "ymax": 380 }
    }
  ],
  "objectCount": 2,
  "model": "facebook/detr-resnet-50"
}
```

**Timeout:** 30 seconds

---

### Video Analysis

**Endpoint:** `POST /api/video-analysis`

**Request:** Multipart form data with video

```
Content-Type: multipart/form-data
video: [binary video file]
```

**Response:**
```json
{
  "success": true,
  "analysis": [
    { "label": "action", "score": 0.87 },
    { "label": "adventure", "score": 0.72 }
  ],
  "model": "MCG-NJU/VideoMAE-base",
  "usingFallback": false
}
```

**Timeout:** 120 seconds (with automatic fallback if fails)

---

### Video Generation

**Endpoint:** `POST /api/video-generation`

Two modes:

**Option 1: From text prompt**
```json
{
  "prompt": "A spaceship flying through stars"
}
```

**Option 2: From image file**
```
Content-Type: multipart/form-data
image: [binary image file]
prompt: [optional text prompt]
```

**Response:**
```json
{
  "success": true,
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "url": "/api/content/660e8400-e29b-41d4-a716-446655440001",
  "model": "stabilityai/stable-video-diffusion-img2vid"
}
```

**Download Video:**
```
GET /api/content/660e8400-e29b-41d4-a716-446655440001
```

**Timeout:** 180 seconds (3 minutes)

---

## Caching System

The backend automatically caches results to speed up repeated requests:

- **Text results:** Cached for 24 hours
- **Images:** File + metadata cached for 7 days
- **Videos:** File + metadata cached for 7 days
- **Cache key:** SHA256 hash of input

**Disable caching:**
```env
ENABLE_CACHE=false
```

**Clear cache manually:**
```bash
# Delete database
rm data/app.db

# Restart server
pnpm run dev
```

---

## Database Schema

### generated_content
- `id` - UUID of generated content
- `type` - 'text', 'image', or 'video'
- `input_text` - Original prompt
- `output_path` - File path in storage
- `model_used` - Which model generated it
- `processing_time` - Time in milliseconds
- `created_at` - Timestamp
- `expires_at` - When to delete (7 days)

### cache
- `id` - UUID
- `hash` - SHA256 of input
- `type` - Feature type
- `input_data` - Original input
- `output_data` - JSON result
- `expires_at` - When to expire (24 hours)

### api_logs
- `endpoint` - Which API was called
- `method` - GET/POST
- `status_code` - HTTP response
- `response_time` - Milliseconds
- `timestamp` - When request occurred

### user_sessions
- `device_id` - Android device identifier
- `total_requests` - Count of API calls
- `last_activity` - Last request time

---

## Troubleshooting

### "Connection Refused" Error

```
⚠️ Problem: Cannot connect to backend
```

**Solutions:**

1. Check backend is running:
   ```bash
   curl http://localhost:3001/api/health
   ```

2. For Android emulator, use `10.0.2.2` instead of `localhost`

3. For device, use your computer's local IP:
   ```bash
   ifconfig  # macOS/Linux
   ipconfig  # Windows
   ```
   Then update `ApiConfig.java`:
   ```java
   public static final String BACKEND_BASE_URL = "http://192.168.1.100:3001";
   ```

4. Ensure firewall allows port 3001

### "Invalid API Key" Error

```
⚠️ Problem: Hugging Face API key not working
```

**Solutions:**

1. Check `.env` has correct key:
   ```bash
   cat .env | grep HUGGING_FACE_API_KEY
   ```

2. Verify key at https://huggingface.co/settings/tokens

3. Restart backend after updating `.env`

### Timeout Errors

```
⚠️ Problem: Requests timing out
```

**Solutions:**

1. Increase timeout in `ApiConfig.java`:
   ```java
   public static final int VIDEO_GENERATION_TIMEOUT = 300000; // 5 min
   ```

2. Check Hugging Face API status: https://huggingface.co/status

3. Check internet connection speed

### "Model not Found" Error

```
⚠️ Problem: Model endpoint invalid
```

**Solutions:**

1. Verify model names in `backend/services/huggingFaceService.js`

2. Check model exists at https://huggingface.co/models

3. Some models require agreement (check model page)

---

## Performance Tips

### Reduce API Costs

1. **Enable Caching:** Avoid duplicate requests
   ```env
   ENABLE_CACHE=true
   CACHE_EXPIRY_HOURS=72
   ```

2. **Use Quantized Models:** Faster inference
   ```js
   // In huggingFaceService.js
   const model = 'distilbert/distilbert-base-uncased-finetuned-sst-2-english';
   ```

3. **Batch Requests:** Send multiple prompts together

### Faster Responses

1. **Use Smaller Models:** Trade quality for speed
2. **Run Locally:** Self-host models using Ollama or LocalAI
3. **Add Redis Cache:** For sub-second responses

---

## Deployment

### Deploy to Vercel

```bash
# Install Vercel CLI
pnpm add -g vercel

# Deploy
vercel --prod
```

### Deploy to Heroku

```bash
# Install Heroku CLI
heroku login
heroku create your-app-name
git push heroku main
```

### Docker Deployment

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
EXPOSE 3001
CMD ["node", "server.js"]
```

```bash
docker build -t ai-backend .
docker run -p 3001:3001 -e HUGGING_FACE_API_KEY=your_key ai-backend
```

---

## Support

For issues or questions:

1. Check logs: `pnpm run dev` shows all errors
2. Test endpoint: `curl http://localhost:3001/api/health`
3. Verify Hugging Face API: Try request directly at https://huggingface.co/inference-api
