const axios = require('axios');
const crypto = require('crypto');

const BASE_URL = 'https://api-inference.huggingface.co/models';
const API_KEY = process.env.HUGGING_FACE_API_KEY;

if (!API_KEY) {
  console.warn('[HuggingFace] Warning: HUGGING_FACE_API_KEY not set in environment');
}

const headers = {
  'Authorization': `Bearer ${API_KEY}`,
  'Content-Type': 'application/json',
};

// Service for Text Generation
const textGenerationService = {
  async generate(prompt) {
    const model = process.env.TEXT_GENERATION_MODEL || 'mistralai/Mistral-7B-Instruct-v0.1';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        {
          inputs: prompt,
          parameters: {
            max_length: 512,
            temperature: 0.7,
            top_p: 0.9,
          },
        },
        {
          headers,
          timeout: parseInt(process.env.INFERENCE_TIMEOUT || 120000),
        }
      );

      return {
        success: true,
        text: response.data[0]?.generated_text || '',
        model: model,
      };
    } catch (error) {
      console.error('[TextGeneration] Error:', error.message);
      return {
        success: false,
        error: error.message,
        model: model,
      };
    }
  },
};

// Service for Image Generation
const imageGenerationService = {
  async generate(prompt) {
    const model = process.env.IMAGE_GENERATION_MODEL || 'stabilityai/stable-diffusion-2-1';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        { inputs: prompt },
        {
          headers,
          timeout: parseInt(process.env.IMAGE_GENERATION_TIMEOUT || 60000),
          responseType: 'arraybuffer',
        }
      );

      return {
        success: true,
        imageBuffer: response.data,
        model: model,
        contentType: response.headers['content-type'] || 'image/jpeg',
      };
    } catch (error) {
      let errorMessage = error.message;
      if (error.response?.data) {
        try {
          const parsed = JSON.parse(Buffer.from(error.response.data).toString());
          errorMessage = parsed.error || JSON.stringify(parsed);
        } catch (e) {
          errorMessage = Buffer.from(error.response.data).toString();
        }
      }
      console.error('[ImageGeneration] CLEAN ERROR:', errorMessage);
      return { success: false, error: errorMessage, model };
    }
  },
};

// Service for Sentiment Analysis
const sentimentAnalysisService = {
  async analyze(text) {
    const model = process.env.SENTIMENT_ANALYSIS_MODEL || 'distilbert-base-uncased-finetuned-sst-2-english';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        { inputs: text },
        {
          headers,
          timeout: parseInt(process.env.INFERENCE_TIMEOUT || 120000),
        }
      );

      const scores = response.data[0];
      const sentiment = scores.reduce((prev, current) =>
        prev.score > current.score ? prev : current
      );

      return {
        success: true,
        sentiment: sentiment.label,
        confidence: (sentiment.score * 100).toFixed(2),
        allScores: scores,
        model: model,
      };
    } catch (error) {
      console.error('[SentimentAnalysis] Error:', error.message);
      return {
        success: false,
        error: error.message,
        model: model,
      };
    }
  },
};

// Service for Object Detection
const objectDetectionService = {
  async detect(imageBuffer) {
    const model = process.env.OBJECT_DETECTION_MODEL || 'facebook/detr-resnet-50';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        imageBuffer,
        {
          headers: {
            ...headers,
            'Content-Type': 'image/jpeg',
          },
          timeout: parseInt(process.env.INFERENCE_TIMEOUT || 120000),
        }
      );

      return {
        success: true,
        detections: response.data,
        model: model,
        objectCount: response.data.length,
      };
    } catch (error) {
      console.error('[ObjectDetection] Error:', error.message);
      return {
        success: false,
        error: error.message,
        model: model,
      };
    }
  },
};

// Service for Video Analysis
const videoAnalysisService = {
  async analyze(videoBuffer) {
    const model = process.env.VIDEO_ANALYSIS_MODEL || 'MCG-NJU/VideoMAE-base';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        videoBuffer,
        {
          headers: {
            ...headers,
            'Content-Type': 'video/mp4',
          },
          timeout: parseInt(process.env.INFERENCE_TIMEOUT || 120000),
        }
      );

      return {
        success: true,
        analysis: response.data,
        model: model,
      };
    } catch (error) {
      console.error('[VideoAnalysis] Error trying primary model:', error.message);
      // Fallback to alternative model
      return await videoAnalysisService.fallback(videoBuffer);
    }
  },

  async fallback(videoBuffer) {
    const fallbackModel = 'facebook/timesformer-base-finetuned-k600';
    const url = `${BASE_URL}/${fallbackModel}`;

    try {
      const response = await axios.post(
        url,
        videoBuffer,
        {
          headers: {
            ...headers,
            'Content-Type': 'video/mp4',
          },
          timeout: parseInt(process.env.INFERENCE_TIMEOUT || 120000),
        }
      );

      return {
        success: true,
        analysis: response.data,
        model: fallbackModel,
        usingFallback: true,
      };
    } catch (error) {
      console.error('[VideoAnalysis] Fallback model also failed:', error.message);
      return {
        success: false,
        error: error.message,
        model: fallbackModel,
        usingFallback: true,
      };
    }
  },
};

// Service for Video Generation (Text -> Image -> Video pipeline)
const videoGenerationService = {
  async generateFromPrompt(prompt, imageBuffer = null) {
    try {
      // Step 1: Generate or use provided image
      let imgBuffer = imageBuffer;
      if (!imgBuffer) {
        const imgResult = await imageGenerationService.generate(prompt);
        if (!imgResult.success) {
          return { success: false, error: 'Failed to generate image from prompt' };
        }
        imgBuffer = imgResult.imageBuffer;
      }

      // Step 2: Generate video from image
      const videoResult = await this.generateFromImage(imgBuffer);
      return videoResult;
    } catch (error) {
      console.error('[VideoGeneration] Error:', error.message);
      return {
        success: false,
        error: error.message,
      };
    }
  },

  async generateFromImage(imageBuffer) {
    const model = process.env.VIDEO_GENERATION_MODEL || 'stabilityai/stable-video-diffusion-img2vid';
    const url = `${BASE_URL}/${model}`;

    try {
      const response = await axios.post(
        url,
        imageBuffer,
        {
          headers: {
            ...headers,
            'Content-Type': 'image/jpeg',
          },
          timeout: parseInt(process.env.VIDEO_GENERATION_TIMEOUT || 180000),
          responseType: 'arraybuffer',
        }
      );

      return {
        success: true,
        videoBuffer: response.data,
        model: model,
        contentType: response.headers['content-type'] || 'video/mp4',
      };
    } catch (error) {
      let errorMessage = error.message;
      if (error.response?.data) {
        try {
          const parsed = JSON.parse(Buffer.from(error.response.data).toString());
          errorMessage = parsed.error || JSON.stringify(parsed);
        } catch (e) {
          errorMessage = Buffer.from(error.response.data).toString();
        }
      }
      console.error('[VideoGeneration] CLEAN ERROR:', errorMessage);
      return { success: false, error: errorMessage, model };
    }
  },
};

module.exports = {
  textGenerationService,
  imageGenerationService,
  sentimentAnalysisService,
  objectDetectionService,
  videoAnalysisService,
  videoGenerationService,
};
