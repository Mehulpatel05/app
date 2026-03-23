# API Documentation

Complete reference for all backend endpoints.

## Base URL

```
http://localhost:3001  (development)
http://10.0.2.2:3001   (Android emulator)
https://your-domain.com (production)
```

## Authentication

Currently no authentication required. Set `X-Device-ID` header for rate limiting (optional):

```
X-Device-ID: android-device-uuid
```

## Response Format

All responses follow this format:

### Success Response
```json
{
  "success": true,
  "data": { /* endpoint-specific */ },
  "timestamp": "2024-01-15T10:30:45.123Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message describing what went wrong",
  "statusCode": 400,
  "timestamp": "2024-01-15T10:30:45.123Z"
}
```

## Endpoints

### 1. Text Generation

Generate text from a prompt using Mistral AI.

**Endpoint:**
```
POST /api/text-generation
```

**Headers:**
```
Content-Type: application/json
X-Device-ID: optional-device-uuid
```

**Request Body:**
```json
{
  "prompt": "Write a short poem about artificial intelligence",
  "maxLength": 256,
  "temperature": 0.7
}
```

**Query Parameters:**
- `prompt` (required) - Text prompt for generation
- `maxLength` (optional) - Max tokens to generate (default: 512)
- `temperature` (optional) - Creativity level 0-1 (default: 0.7)

**Response:**
```json
{
  "success": true,
  "text": "Artificial intelligence, a marvel of our age...",
  "model": "mistralai/Mistral-7B-Instruct-v0.1",
  "fromCache": false,
  "processingTime": 2500
}
```

**Possible Errors:**
- `400` - Invalid prompt
- `500` - API error from Hugging Face

**Example cURL:**
```bash
curl -X POST http://localhost:3001/api/text-generation \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Hello world"
  }'
```

**Timeout:** 30 seconds

---

### 2. Image Generation

Generate images from text prompts using Stable Diffusion.

**Endpoint:**
```
POST /api/image-generation
```

**Request Body:**
```json
{
  "prompt": "A cyberpunk cityscape at night with neon signs"
}
```

**Response:**
```json
{
  "success": true,
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "url": "/api/content/550e8400-e29b-41d4-a716-446655440000",
  "downloadUrl": "http://localhost:3001/api/content/550e8400-e29b-41d4-a716-446655440000",
  "model": "stabilityai/stable-diffusion-2-1",
  "contentType": "image/jpeg",
  "fromCache": false,
  "processingTime": 45000
}
```

**Download Generated Image:**
```bash
curl http://localhost:3001/api/content/550e8400-e29b-41d4-a716-446655440000 \
  -o generated-image.jpg
```

**Example cURL:**
```bash
curl -X POST http://localhost:3001/api/image-generation \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "A futuristic city"
  }'
```

**Timeout:** 60 seconds

---

### 3. Sentiment Analysis

Analyze the sentiment of text.

**Endpoint:**
```
POST /api/sentiment-analysis
```

**Request Body:**
```json
{
  "text": "I absolutely love this product! It's amazing."
}
```

**Response:**
```json
{
  "success": true,
  "sentiment": "POSITIVE",
  "confidence": "99.85",
  "allScores": [
    {
      "label": "POSITIVE",
      "score": 0.9985
    },
    {
      "label": "NEGATIVE",
      "score": 0.0015
    }
  ],
  "model": "distilbert-base-uncased-finetuned-sst-2-english",
  "fromCache": false
}
```

**Possible Sentiments:**
- `POSITIVE` - Positive sentiment (0.5-1.0 confidence)
- `NEGATIVE` - Negative sentiment (0.5-1.0 confidence)
- `NEUTRAL` - Neutral sentiment (some models)

**Example cURL:**
```bash
curl -X POST http://localhost:3001/api/sentiment-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "text": "This is great!"
  }'
```

**Timeout:** 30 seconds

---

### 4. Object Detection

Detect objects in images using DETR.

**Endpoint:**
```
POST /api/object-detection
```

**Headers:**
```
Content-Type: multipart/form-data
```

**Form Data:**
- `image` (required) - Image file (JPEG, PNG, etc.)

**Response:**
```json
{
  "success": true,
  "detections": [
    {
      "label": "person",
      "score": 0.9876,
      "box": {
        "xmin": 100,
        "ymin": 50,
        "xmax": 300,
        "ymax": 400
      }
    },
    {
      "label": "dog",
      "score": 0.9543,
      "box": {
        "xmin": 320,
        "ymin": 200,
        "xmax": 450,
        "ymax": 380
      }
    }
  ],
  "objectCount": 2,
  "model": "facebook/detr-resnet-50"
}
```

**Example cURL:**
```bash
curl -X POST http://localhost:3001/api/object-detection \
  -F "image=@/path/to/image.jpg"
```

**Timeout:** 30 seconds

---

### 5. Video Analysis

Analyze video content to detect actions/activities.

**Endpoint:**
```
POST /api/video-analysis
```

**Headers:**
```
Content-Type: multipart/form-data
```

**Form Data:**
- `video` (required) - Video file (MP4, WebM, etc.)

**Response:**
```json
{
  "success": true,
  "analysis": [
    {
      "label": "action",
      "score": 0.8732
    },
    {
      "label": "adventure",
      "score": 0.7291
    }
  ],
  "model": "MCG-NJU/VideoMAE-base",
  "usingFallback": false
}
```

**Note:** If primary model fails, automatically tries fallback model (`facebook/timesformer-base-finetuned-k600`).

**Example cURL:**
```bash
curl -X POST http://localhost:3001/api/video-analysis \
  -F "video=@/path/to/video.mp4"
```

**Timeout:** 120 seconds

---

### 6. Video Generation

Generate videos from prompts or images using Stable Video Diffusion.

**Endpoint:**
```
POST /api/video-generation
```

**Two Usage Modes:**

#### Option A: From Text Prompt
```json
{
  "prompt": "A spaceship flying through a galaxy"
}
```

#### Option B: From Image File
```
Content-Type: multipart/form-data
image: [binary image file]
prompt: "A spaceship flying through a galaxy" (optional)
```

**Response:**
```json
{
  "success": true,
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "url": "/api/content/660e8400-e29b-41d4-a716-446655440001",
  "downloadUrl": "http://localhost:3001/api/content/660e8400-e29b-41d4-a716-446655440001",
  "model": "stabilityai/stable-video-diffusion-img2vid",
  "contentType": "video/mp4",
  "processingTime": 150000
}
```

**Download Generated Video:**
```bash
curl http://localhost:3001/api/content/660e8400-e29b-41d4-a716-446655440001 \
  -o generated-video.mp4
```

**Example cURL (from prompt):**
```bash
curl -X POST http://localhost:3001/api/video-generation \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "A butterfly flying in a garden"
  }'
```

**Example cURL (from image):**
```bash
curl -X POST http://localhost:3001/api/video-generation \
  -F "image=@/path/to/image.jpg" \
  -F "prompt=Make it move smoothly"
```

**Timeout:** 180 seconds (3 minutes)

---

### 7. Retrieve Generated Content

Download previously generated images or videos.

**Endpoint:**
```
GET /api/content/:id
```

**Parameters:**
- `id` (required) - Content ID from generation endpoint

**Response:**
Binary file content with appropriate `Content-Type` header.

**Example cURL:**
```bash
curl http://localhost:3001/api/content/550e8400-e29b-41d4-a716-446655440000 \
  -o my-image.jpg
```

---

### 8. Health Check

Check if server is running and healthy.

**Endpoint:**
```
GET /api/health
```

**Response:**
```json
{
  "success": true,
  "status": "Backend is running",
  "timestamp": "2024-01-15T10:30:45.123Z",
  "database": "connected",
  "uptime": 3600
}
```

---

## Error Codes

| Code | Meaning | Solution |
|------|---------|----------|
| 400 | Bad Request | Check request body and parameters |
| 401 | Unauthorized | Future: Add authentication token |
| 404 | Not Found | Content ID doesn't exist or URL is wrong |
| 429 | Rate Limited | Wait before making more requests |
| 500 | Server Error | Check backend logs, restart server |
| 503 | Service Unavailable | Hugging Face API is down |
| 504 | Gateway Timeout | Request took too long, increase timeout |

## Rate Limiting

Default: 100 requests per session

Headers returned:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
X-RateLimit-Reset: 2024-01-15T11:30:45.123Z
```

## Caching

Responses are cached based on input hash:

- **Text responses:** 24 hours
- **Images/Videos:** 7 days

Set header to disable:
```
X-No-Cache: true
```

Or environment variable:
```env
ENABLE_CACHE=false
```

## Performance Tips

1. **Batch similar requests:** Reuse cached responses
2. **Monitor response times:** Adjust timeouts if needed
3. **Check API status:** https://huggingface.co/status
4. **Use smaller prompts:** Faster processing
5. **Compress images:** Reduce upload size

## Example Integration (JavaScript)

```javascript
async function generateImage(prompt) {
  const response = await fetch('http://localhost:3001/api/image-generation', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Device-ID': 'my-device-id'
    },
    body: JSON.stringify({ prompt })
  });

  const result = await response.json();
  
  if (result.success) {
    const imageUrl = 'http://localhost:3001' + result.url;
    console.log('Generated image:', imageUrl);
  } else {
    console.error('Error:', result.error);
  }
}
```

## Support

- Documentation: See `INTEGRATION_GUIDE.md`
- Issues: Check logs with `pnpm run dev`
- API Status: https://huggingface.co/status
