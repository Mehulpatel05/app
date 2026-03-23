# AI Features Hub - Android App

A complete Android application built in **Java** and **XML** that integrates **5 different AI models** from Hugging Face. Each feature uses a **separate, dedicated API endpoint** with proper error handling and fallback mechanisms.

## 🚀 Features

### 1. **Text Generation** (GPT-2)
- Generate creative text from prompts
- Model: `gpt2`
- Max tokens: 100
- Temperature: 0.7

### 2. **Image Generation** (Stable Diffusion 2.1)
- Generate images from text descriptions
- Model: `stabilityai/stable-diffusion-2-1`
- Output: Base64 encoded images
- Display generated images directly in the app

### 3. **Sentiment Analysis** (DistilBERT)
- Analyze text sentiment (Positive/Negative)
- Model: `distilbert-base-uncased-finetuned-sst-2-english`
- Shows confidence percentage
- Fast and lightweight NLP model

### 4. **Object Detection** (DETR - Detection Transformer)
- Detect objects in images
- Model: `facebook/detr-resnet-50`
- Camera and gallery support
- Shows detected objects with confidence scores

### 5. **Video Analysis** (VideoMAE + Fallback)
- Analyze video content
- Primary Model: `sayakpaul/videomae-base-finetuned-kinetics`
- Fallback Model: `google/vit-base-patch16-224` (for frame analysis)
- Automatic fallback if primary model fails
- Extracts and analyzes first frame

## 📋 Architecture

```
ai/
├── src/
│   ├── main/
│   │   ├── java/com/example/aiapp/
│   │   │   ├── MainActivity.java                    (Feature hub)
│   │   │   ├── activities/
│   │   │   │   ├── TextGenerationActivity.java     (GPT-2)
│   │   │   │   ├── ImageGenerationActivity.java    (Stable Diffusion)
│   │   │   │   ├── SentimentAnalysisActivity.java  (DistilBERT)
│   │   │   │   ├── ObjectDetectionActivity.java    (DETR)
│   │   │   │   └── VideoAnalysisActivity.java      (VideoMAE + Fallback)
│   │   │   ├── api/
│   │   │   │   └── HuggingFaceApiClient.java       (All API calls)
│   │   │   ├── config/
│   │   │   │   └── ApiConfig.java                  (Configuration)
│   │   │   └── utils/
│   │   │       └── ImageUtils.java                 (Helper functions)
│   │   ├── res/
│   │   │   ├── layout/                             (5 activity layouts)
│   │   │   ├── drawable/                           (UI components)
│   │   │   └── values/                             (Colors, themes, strings)
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── README.md (this file)
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio 4.0+
- Java 11+
- Minimum API Level: 24 (Android 7.0)
- Target API Level: 34 (Android 14)

### Step 1: Get Hugging Face API Key
1. Go to https://huggingface.co/settings/tokens
2. Create a new **User Access Token** (read-only is sufficient)
3. Copy the token

### Step 2: Configure API Key
1. Open `/ai/src/main/java/com/example/aiapp/config/ApiConfig.java`
2. Replace `YOUR_HUGGING_FACE_API_KEY` with your actual token:
```java
public static final String HF_API_KEY = "hf_YourActualTokenHere123456789";
```

### Step 3: Enable Cleartext Traffic (For HTTP)
The app uses HTTPS for Hugging Face API calls, but if needed for testing:
- The `AndroidManifest.xml` already includes `android:usesCleartextTraffic="true"`

### Step 4: Add Permissions
Required permissions are already in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Step 5: Build and Run
```bash
# Build
./gradlew build

# Run on emulator/device
./gradlew installDebug
```

## 📡 API Endpoints

| Feature | Model | Endpoint | Input | Output |
|---------|-------|----------|-------|--------|
| Text Gen | GPT-2 | `api-inference.huggingface.co/models/gpt2` | JSON prompt | Text |
| Image Gen | Stable Diffusion | `api-inference.huggingface.co/models/stabilityai/stable-diffusion-2-1` | JSON prompt | Base64 image |
| Sentiment | DistilBERT | `api-inference.huggingface.co/models/distilbert-base-uncased-finetuned-sst-2-english` | JSON text | JSON scores |
| Objects | DETR | `api-inference.huggingface.co/models/facebook/detr-resnet-50` | Binary image | JSON detections |
| Video | VideoMAE | `api-inference.huggingface.co/models/sayakpaul/videomae-base-finetuned-kinetics` | Binary frame | JSON classification |
| Video (Fallback) | ViT | `api-inference.huggingface.co/models/google/vit-base-patch16-224` | Binary frame | JSON classification |

## 🔄 Request/Response Flow

### Text Generation Example
```
Request:
{
  "inputs": "The future of AI is",
  "parameters": {
    "max_length": 100,
    "temperature": 0.7,
    "top_p": 0.9
  }
}

Response:
[
  {
    "generated_text": "The future of AI is bright and full of possibilities..."
  }
]
```

### Image Generation Example
```
Request:
{
  "inputs": "A beautiful sunset over mountains"
}

Response:
[
  "iVBORw0KGgoAAAANSUhEUgAAA..." // Base64 encoded image
]
```

### Sentiment Analysis Example
```
Request:
{
  "inputs": "I love this product!"
}

Response:
[
  [
    {
      "label": "POSITIVE",
      "score": 0.9998
    }
  ]
]
```

## 🛡️ Error Handling

### Network Errors
- Handled with try-catch in AsyncTasks
- User-friendly error messages displayed
- Timeout: 60 seconds for all requests

### Video Fallback
- If VideoMAE fails, automatically uses ViT-Base for frame analysis
- Frame extraction from video using `MediaMetadataRetriever`
- Logs warning but continues processing

### Response Parsing
- JSON parsing with GSON library
- Null checks on all responses
- Default messages if parsing fails

## 🎨 UI/UX Design

- **Modern Dark Theme**: Slate gray background (#0F172A)
- **Color Scheme**: 
  - Primary: Indigo (#6366F1)
  - Secondary: Gray (#475569)
  - Text: Light (#F1F5F9)
- **Responsive**: Works on phones and tablets
- **Loading States**: Progress indicators for all API calls
- **Input Validation**: All features validate before API calls

## 📦 Dependencies

```gradle
// Android
androidx.appcompat:appcompat:1.6.1
androidx.constraintlayout:constraintlayout:2.1.4
com.google.android.material:material:1.10.0

// Networking
com.squareup.okhttp3:okhttp:4.11.0
com.google.code.gson:gson:2.10.1

// Image Loading
com.squareup.picasso:picasso:2.8

// Lifecycle & Coroutines
androidx.lifecycle:lifecycle-runtime:2.6.2
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
```

## 🔐 Security

- ✅ HTTPS for all API calls
- ✅ API key stored in config file (change in production)
- ✅ No sensitive data in logs
- ✅ Input validation on all user inputs
- ✅ Proper permission requests (Runtime for camera/storage)

## 📝 Code Style

- **Language**: Java 11+
- **XML Layout**: Modern constraint-based design
- **Async Processing**: AsyncTask for all API calls
- **Resource Files**: Properly organized drawables, layouts, values

## 🚨 Important Notes

1. **API Rate Limits**: Hugging Face has rate limits on free tier (~30 req/min)
2. **Model Loading**: First request to each model may take 20-30 seconds (cold start)
3. **Video Analysis**: Extracts first frame for analysis, full video processing not supported
4. **Storage**: Generated images are not persisted (show only in memory)
5. **Offline Mode**: App requires internet connection for all features

## 🐛 Troubleshooting

### "401 Unauthorized" Error
- Check your API key in `ApiConfig.java`
- Verify key is active on https://huggingface.co/settings/tokens

### "Model is loading" Response
- Models start after first use (can take 30+ seconds)
- Try again after waiting

### "Out of Memory" (Image Generation)
- Reduce image size or quality
- Close other apps to free up RAM

### Camera/Gallery Not Working
- Check permissions in Android Settings
- Grant camera and storage permissions explicitly

### Video Playback Issues
- Ensure video format is supported (MP4 recommended)
- Try using frames from camera instead

## 📄 License

This project is provided as-is for educational and commercial use.

## 🤝 Contributing

To extend with more features:
1. Add new Activity class
2. Create corresponding layout XML
3. Add API endpoint in `ApiConfig.java`
4. Implement API call in `HuggingFaceApiClient.java`
5. Add button in MainActivity

## 📞 Support

For issues with Hugging Face API:
- Check: https://huggingface.co/docs/hub/inference-api
- Status: https://status.huggingface.co

For Android development help:
- Android Docs: https://developer.android.com
- Stack Overflow: https://stackoverflow.com/questions/tagged/android
