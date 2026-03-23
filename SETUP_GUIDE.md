# Complete Setup Guide - AI Features Hub Android App

## 🎯 Quick Start (5 Minutes)

### 1. Get Your API Key
```
Visit: https://huggingface.co/settings/tokens
Create: New "User Access Token" (read-only is fine)
Copy: The token value
```

### 2. Add API Key to Project
**File**: `src/main/java/com/example/aiapp/config/ApiConfig.java`

**Find Line 9**:
```java
public static final String HF_API_KEY = "YOUR_HUGGING_FACE_API_KEY";
```

**Replace with your token**:
```java
public static final String HF_API_KEY = "hf_aBcD1234EfGhIjKlMnOpQrStUvWxYz";
```

### 3. Build and Run
```bash
cd ai
./gradlew build
./gradlew installDebug
```

Done! ✨

---

## 📋 Detailed Setup

### System Requirements
- **Android Studio**: 4.0 or newer
- **Java**: 11 or newer
- **Android SDK**: API 24+ (Android 7.0)
- **Gradle**: 7.0+
- **Internet**: Required for API calls

### Project Structure
```
ai/
├── build.gradle                  ← Update dependencies here
├── src/
│   └── main/
│       ├── java/com/example/aiapp/
│       │   ├── MainActivity.java              ← Hub/launcher
│       │   ├── activities/                    ← 5 feature activities
│       │   ├── api/HuggingFaceApiClient.java ← All API calls
│       │   ├── config/ApiConfig.java          ← API KEY HERE
│       │   └── utils/ImageUtils.java          ← Helpers
│       ├── res/
│       │   ├── layout/                        ← UI layouts
│       │   ├── drawable/                      ← Shapes, backgrounds
│       │   └── values/                        ← Colors, themes, strings
│       └── AndroidManifest.xml
└── README.md
```

---

## 🔑 Getting Hugging Face API Key

### Step-by-Step
1. Open https://huggingface.co/settings/tokens
2. Click **"New Token"**
3. Fill in:
   - **Name**: "AI Features Hub" (or any name)
   - **Type**: Select "Read" (don't need write)
   - **Expiration**: "No expiration" or set date
4. Click **"Create Token"**
5. Copy the generated token (starts with `hf_`)
6. Save it safely - you'll use it below

### Example Token
```
hf_rA8qPm2kL9jD4bN6xE1oY5tW3vZ7cS0hF
```

---

## ⚙️ Configuration

### API Key Configuration
**File**: `src/main/java/com/example/aiapp/config/ApiConfig.java`

```java
// Line 9
public static final String HF_API_KEY = "hf_YOUR_TOKEN_HERE";
```

### API Endpoints (Already Configured)

| Feature | Model | Endpoint |
|---------|-------|----------|
| Text | GPT-2 | `gpt2` |
| Image | Stable Diffusion | `stabilityai/stable-diffusion-2-1` |
| Sentiment | DistilBERT | `distilbert-base-uncased-finetuned-sst-2-english` |
| Objects | DETR | `facebook/detr-resnet-50` |
| Video | VideoMAE | `sayakpaul/videomae-base-finetuned-kinetics` |
| Video Fallback | ViT | `google/vit-base-patch16-224` |

**All endpoints use**: `https://api-inference.huggingface.co/models/`

### Timeout Settings
**File**: `src/main/java/com/example/aiapp/config/ApiConfig.java`

```java
public static final int TIMEOUT_SECONDS = 60;  // Increase if needed
```

---

## 🏗️ Building the App

### Using Android Studio
1. Open `File > Open...`
2. Select the `ai` folder
3. Wait for Gradle to sync
4. Click **Build > Build Bundle(s) / Build APK(s)**

### Using Command Line
```bash
cd ai

# Build APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build Release
./gradlew assembleRelease
```

---

## 📱 Running the App

### On Emulator
1. Open **Android Studio**
2. **Tools > Device Manager**
3. Create or select an emulator (API 24+)
4. Click **Play** to start
5. Run the app

### On Physical Device
1. Enable **Developer Mode**:
   - Settings > About Phone
   - Tap Build Number 7 times
2. Enable **USB Debugging**:
   - Settings > Developer Options > USB Debugging
3. Connect via USB
4. Run from Android Studio or:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing Each Feature

### 1. Text Generation
- **Input**: "The weather today is"
- **Expected**: Generated sentence completion
- **Time**: 10-20 seconds (first run slower)

### 2. Image Generation
- **Input**: "A cat wearing sunglasses"
- **Expected**: Generated image displayed
- **Time**: 30-60 seconds (first run)
- **Note**: Requires more processing power

### 3. Sentiment Analysis
- **Input**: "I absolutely love this app!"
- **Expected**: "POSITIVE (99%)"
- **Time**: 5-10 seconds

### 4. Object Detection
- **Input**: Image of objects
- **Expected**: List of detected objects with scores
- **Time**: 15-30 seconds
- **Actions**: Camera or Gallery

### 5. Video Analysis
- **Input**: Video file
- **Expected**: Analysis of first frame
- **Time**: 20-40 seconds
- **Note**: Fallback to image model if needed

---

## 🔐 Security Checklist

- ✅ API Key: In `ApiConfig.java` (change before production)
- ✅ HTTPS: All requests use HTTPS
- ✅ Permissions: Properly requested at runtime
- ✅ Input Validation: All user inputs validated
- ✅ Error Handling: Proper exception handling

### For Production Deployment
1. **Move API Key to server** (don't put in app)
2. **Use encrypted storage** for sensitive data
3. **Implement token refresh** logic
4. **Add certificate pinning** for HTTPS
5. **Review data privacy policy** before release

---

## 🚀 Optimization Tips

### Performance
- Close background apps while using video features
- First API call to each model takes 20-60 seconds (model loading)
- Subsequent calls are faster
- Image generation is slower than text analysis

### Storage
- Set proper image quality:
  ```java
  // In ImageGenerationActivity.java
  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);  // 90 = quality
  ```

### Memory
- Video frame extraction is memory-intensive
- Resize large images before sending:
  ```java
  Bitmap resized = ImageUtils.resizeBitmap(bitmap, 512, 512);
  ```

---

## 🐛 Common Issues & Solutions

### Issue: "401 Unauthorized"
**Cause**: Invalid API key
**Solution**:
1. Check token in `ApiConfig.java`
2. Verify token is active on HF website
3. Ensure no extra spaces or characters

### Issue: "Model is loading"
**Cause**: First request to a model
**Solution**:
- Wait 30-60 seconds
- Try again
- Models cache after first use

### Issue: "Connection timeout"
**Cause**: Network or API slow
**Solution**:
```java
// Increase timeout in ApiConfig.java
public static final int TIMEOUT_SECONDS = 120;  // From 60
```

### Issue: "Out of memory"
**Cause**: Too many large images in memory
**Solution**:
- Restart the app
- Close other apps
- Use smaller images

### Issue: "Camera not working"
**Cause**: Missing permissions
**Solution**:
1. Check `AndroidManifest.xml` has permissions
2. Grant permissions in app settings
3. Try restarting app

### Issue: "No internet"
**Cause**: Device offline
**Solution**:
- Connect to WiFi or mobile data
- Check Android Settings > Network

---

## 📊 API Rate Limits

**Hugging Face Free Tier**:
- ~30 requests per minute
- If exceeded, wait 1 minute

**Pro Tier** (paid):
- Higher limits
- Faster inference
- Priority queuing

---

## 📚 Useful Resources

### Hugging Face
- API Docs: https://huggingface.co/docs/hub/inference-api
- Model Hub: https://huggingface.co/models
- Status: https://status.huggingface.co

### Android Development
- Official Docs: https://developer.android.com
- Android API Reference: https://developer.android.com/reference
- Stack Overflow: https://stackoverflow.com/questions/tagged/android

### Libraries Used
- OkHttp: https://square.github.io/okhttp/
- GSON: https://github.com/google/gson
- Picasso: https://square.github.io/picasso/

---

## 🎓 Learning Path

### For Beginners
1. Read `README.md` first
2. Set up with quick start guide
3. Test each feature one by one
4. Read source code comments

### For Intermediate
1. Modify API timeout settings
2. Add custom error handling
3. Implement image caching
4. Add result history/save feature

### For Advanced
1. Add authentication layer
2. Implement token management
3. Add database for results
4. Create custom models
5. Deploy to Play Store

---

## ✅ Pre-Launch Checklist

Before deploying to production:

- [ ] API key set correctly
- [ ] All features tested
- [ ] HTTPS enabled
- [ ] Permissions verified
- [ ] Error handling works
- [ ] Privacy policy added
- [ ] Terms of service added
- [ ] Rate limiting implemented
- [ ] Logging secured
- [ ] App icon added
- [ ] Version number set
- [ ] Release notes written

---

## 📞 Getting Help

### If something doesn't work:
1. Check this guide
2. Read code comments
3. Check Hugging Face status
4. Search Stack Overflow
5. Review Android logcat output

### Check Logcat
```bash
# View logs while app is running
./gradlew installDebug -t

# Or in Android Studio: View > Tool Windows > Logcat
```

---

## 🎉 You're Ready!

Your AI-powered Android app is ready to use. Start building! 🚀

Questions? Check the README.md or review the source code - it's well-commented.
