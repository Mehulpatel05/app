const express = require('express');
const multer = require('multer');
const { body, validationResult } = require('express-validator');
const {
  textGenerationService,
  imageGenerationService,
  sentimentAnalysisService,
  objectDetectionService,
  videoAnalysisService,
  videoGenerationService,
} = require('../services/huggingFaceService');
const { getFromCache, saveToCache } = require('../utils/cache');
const { saveContent, getContent } = require('../utils/storage');

const router = express.Router();
const upload = multer({ storage: multer.memoryStorage() });

// Middleware for validation errors
const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// Text Generation Endpoint
router.post(
  '/text-generation',
  [body('prompt').trim().notEmpty().withMessage('Prompt is required')],
  handleValidationErrors,
  async (req, res) => {
    try {
      const { prompt } = req.body;

      // Check cache
      const cached = await getFromCache('text-generation', { prompt });
      if (cached) {
        return res.json({ success: true, text: cached.text, fromCache: true });
      }

      // Generate text
      const result = await textGenerationService.generate(prompt);

      if (result.success) {
        // Save to cache
        await saveToCache('text-generation', { prompt }, { text: result.text });
        res.json({ success: true, text: result.text, model: result.model });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Text generation error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Image Generation Endpoint
router.post(
  '/image-generation',
  [body('prompt').trim().notEmpty().withMessage('Prompt is required')],
  handleValidationErrors,
  async (req, res) => {
    try {
      const { prompt } = req.body;

      // Check cache
      const cached = await getFromCache('image-generation', { prompt });
      if (cached) {
        return res.json({
          success: true,
          id: cached.id,
          url: cached.url,
          fromCache: true,
        });
      }

      // Generate image
      const result = await imageGenerationService.generate(prompt);

      if (result.success) {
        // Save image
        const saved = await saveContent('image', result.imageBuffer, {
          inputText: prompt,
          model: result.model,
        });

        // Cache the result
        await saveToCache('image-generation', { prompt }, {
          id: saved.id,
          url: saved.url,
        });

        res.json({
          success: true,
          id: saved.id,
          url: saved.url,
          model: result.model,
        });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Image generation error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Sentiment Analysis Endpoint
router.post(
  '/sentiment-analysis',
  [body('text').trim().notEmpty().withMessage('Text is required')],
  handleValidationErrors,
  async (req, res) => {
    try {
      const { text } = req.body;

      // Check cache
      const cached = await getFromCache('sentiment-analysis', { text });
      if (cached) {
        return res.json({
          success: true,
          sentiment: cached.sentiment,
          confidence: cached.confidence,
          fromCache: true,
        });
      }

      // Analyze sentiment
      const result = await sentimentAnalysisService.analyze(text);

      if (result.success) {
        // Save to cache
        await saveToCache('sentiment-analysis', { text }, {
          sentiment: result.sentiment,
          confidence: result.confidence,
        });

        res.json({
          success: true,
          sentiment: result.sentiment,
          confidence: result.confidence,
          allScores: result.allScores,
          model: result.model,
        });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Sentiment analysis error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Object Detection Endpoint
router.post(
  '/object-detection',
  upload.single('image'),
  async (req, res) => {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, error: 'Image file required' });
      }

      // Detect objects
      const result = await objectDetectionService.detect(req.file.buffer);

      if (result.success) {
        res.json({
          success: true,
          detections: result.detections,
          objectCount: result.objectCount,
          model: result.model,
        });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Object detection error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Video Analysis Endpoint
router.post(
  '/video-analysis',
  upload.single('video'),
  async (req, res) => {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, error: 'Video file required' });
      }

      // Analyze video
      const result = await videoAnalysisService.analyze(req.file.buffer);

      if (result.success) {
        res.json({
          success: true,
          analysis: result.analysis,
          model: result.model,
          usingFallback: result.usingFallback || false,
        });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Video analysis error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Video Generation Endpoint
router.post(
  '/video-generation',
  upload.single('image'),
  [body('prompt').optional().trim()],
  handleValidationErrors,
  async (req, res) => {
    try {
      const { prompt } = req.body;
      const imageBuffer = req.file?.buffer;

      if (!prompt && !imageBuffer) {
        return res.status(400).json({
          success: false,
          error: 'Either prompt or image file is required',
        });
      }

      // Generate video
      const result = await videoGenerationService.generateFromPrompt(prompt, imageBuffer);

      if (result.success) {
        // Save video
        const saved = await saveContent('video', result.videoBuffer, {
          inputText: prompt,
          model: result.model,
        });

        res.json({
          success: true,
          id: saved.id,
          url: saved.url,
          model: result.model,
        });
      } else {
        res.status(500).json({ success: false, error: result.error });
      }
    } catch (error) {
      console.error('[API] Video generation error:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  }
);

// Get Content by ID
router.get('/content/:id', async (req, res) => {
  try {
    const filepath = await getContent(req.params.id);

    if (!filepath) {
      return res.status(404).json({ success: false, error: 'Content not found' });
    }

    res.download(filepath);
  } catch (error) {
    console.error('[API] Content retrieval error:', error);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Health check
router.get('/health', (req, res) => {
  res.json({
    success: true,
    status: 'Backend is running',
    timestamp: new Date().toISOString(),
  });
});

module.exports = router;
