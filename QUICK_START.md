# Quick Start Guide - AI Android App with Backend

Get your AI app running in 5 minutes!

## 🚀 5-Minute Setup

### Step 1: Get Hugging Face API Key (1 min)

1. Go to https://huggingface.co/settings/tokens
2. Click "New token"
3. Give it a name (e.g., "AI App")
4. Copy the token

### Step 2: Start Backend Server (2 min)

```bash
# Navigate to backend
cd ai/backend

# Install dependencies
pnpm install

# Create .env file
cp .env.example .env

# Edit .env and add your HF API key
# HUGGING_FACE_API_KEY=hf_xxxxxxxxxxxxx

# Start server
pnpm run dev
```

**Expected output:**
```
╔════════════════════════════════════════╗
║      AI App Backend Server Started     ║
║  Server: http://localhost:3001         ║
╚════════════════════════════════════════╝
```

✅ Backend is ready!

### Step 3: Open Android Studio (2 min)

1. Open Android Studio
2. Open project: `ai/` folder
3. Build the app: `Build > Make Project`
4. Run on emulator: `Run > Run 'app'`

✅ App should now work!

---

## 🧪 Test the App

### Test Text Generation
1. Open app → "Text Generation"
2. Enter: "Write a short poem about AI"
3. Click "Generate"
4. Wait 15-30 seconds

### Test Image Generation
1. Open app → "Image Generation"
2. Enter: "A futuristic city at night"
3. Click "Generate"
4. Image appears after ~45 seconds

### Test Sentiment Analysis
1. Open app → "Sentiment Analysis"
2. Enter: "I love this app!"
3. Click "Analyze"
4. See result: POSITIVE (confidence: 99%)

---

## 🛠️ Troubleshooting

### Backend won't start
```bash
# Check Node.js version (need 16+)
node --version

# Try clearing node_modules
rm -rf node_modules
pnpm install

# Check port 3001 is free
lsof -i :3001
```

### Android app can't connect to backend

**For Emulator:**
- Use `http://10.0.2.2:3001` ✅ (already in code)

**For Physical Device:**
1. Find your computer's IP:
   ```bash
   ifconfig  # macOS/Linux
   ipconfig  # Windows
   ```
   Example: `192.168.1.100`

2. Update in `ApiConfig.java`:
   ```java
   public static final String BACKEND_BASE_URL = "http://192.168.1.100:3001";
   ```

3. Make sure device is on same Wi-Fi

**Test connection:**
```bash
# From your computer
curl http://localhost:3001/api/health

# From Android device (replace with your IP)
curl http://192.168.1.100:3001/api/health
```

### API requests timing out

Try these in order:
1. Check internet connection
2. Wait - Hugging Face API sometimes takes time on cold start
3. Increase timeout in `ApiConfig.java`:
   ```java
   public static final int REQUEST_TIMEOUT = 60000; // 60 seconds
   ```

---

## 📱 Features

| Feature | Status | Time |
|---------|--------|------|
| Text Generation | ✅ | 15-30s |
| Image Generation | ✅ | 45-60s |
| Sentiment Analysis | ✅ | 5-10s |
| Object Detection | ✅ | 10-20s |
| Video Analysis | ✅ | 30-60s |
| Video Generation | ✅ | 2-3 min |

---

## 📚 Project Structure

```
ai/
├── backend/                 # Node.js Express server
│   ├── server.js           # Main server file
│   ├── services/           # Hugging Face API calls
│   ├── routes/api.js       # API endpoints
│   ├── config/database.js  # SQLite setup
│   ├── utils/cache.js      # Caching logic
│   ├── utils/storage.js    # File storage
│   └── package.json        # Dependencies
│
└── src/main/               # Android Java/XML
    ├── java/com/example/aiapp/
    │   ├── MainActivity.java
    │   ├── activities/      # Feature screens
    │   ├── api/             # API client
    │   └── config/          # API config
    └── res/
        ├── layout/          # XML layouts
        ├── drawable/        # UI resources
        └── values/          # Colors, strings
```

---

## 🔄 Workflow

```
User Input (Android)
        ↓
API Request (HTTP/JSON)
        ↓
Backend Server (Node.js)
        ↓
Hugging Face API
        ↓
Process (varies by model)
        ↓
Return Result (JSON)
        ↓
Display in App (Android)
        ↓
Cache Result (for reuse)
```

---

## 🎓 Learning Path

1. **Understand API Flow** → Read `backend/INTEGRATION_GUIDE.md`
2. **Explore Models** → Check `backend/services/huggingFaceService.js`
3. **Add New Feature** → Create new Activity + API endpoint
4. **Deploy to Cloud** → Follow deployment section in guide

---

## 📚 Resources

- **Hugging Face Docs:** https://huggingface.co/docs/hub/inference-api
- **Android Docs:** https://developer.android.com/docs
- **Express.js Docs:** https://expressjs.com/
- **SQLite Docs:** https://www.sqlite.org/docs.html

---

## 🐛 Debug Mode

Enable detailed logging:

1. **Backend:** Already enabled in `server.js`
   ```
   Look for [v0], [API], [Cache], [Storage] prefixes
   ```

2. **Android:** Add debug logs in `HuggingFaceApiClient.java`
   ```java
   Log.d("API", "Response: " + response);
   ```

---

## ✅ Checklist Before Deploy

- [ ] Backend running locally: `http://localhost:3001/api/health` works
- [ ] All 6 features tested and working
- [ ] API key valid (test at huggingface.co)
- [ ] App can connect to backend
- [ ] Images/videos save properly
- [ ] No sensitive data in code
- [ ] Error messages user-friendly

---

## 🚀 Next Steps

1. **Customize Models** → Edit `backend/services/huggingFaceService.js`
2. **Add Authentication** → Add login screen to Android app
3. **Deploy Backend** → Use Vercel, Heroku, or AWS
4. **Publish App** → Submit to Google Play Store
5. **Monitor Usage** → Check API logs in database

---

**Happy Building! 🎉**

For detailed info, see:
- 📖 `ai/backend/INTEGRATION_GUIDE.md` - Complete backend reference
- 📖 `ai/README.md` - Android app overview
- 📖 `ai/backend/SETUP_GUIDE.md` - Detailed setup instructions
