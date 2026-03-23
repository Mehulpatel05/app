require('dotenv').config();
const express = require('express');
const cors = require('cors');
const path = require('path');

// Import configurations and routes
const db = require('./config/database');
const apiRoutes = require('./routes/api');

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  credentials: true,
}));

app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));

// Static files for generated content
app.use('/storage', express.static(path.join(__dirname, 'storage')));

// API Routes
app.use('/api', apiRoutes);

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    message: 'AI App Backend API Server',
    version: '1.0.0',
    endpoints: {
      'POST /api/text-generation': 'Generate text from prompt',
      'POST /api/image-generation': 'Generate image from prompt',
      'POST /api/sentiment-analysis': 'Analyze sentiment of text',
      'POST /api/object-detection': 'Detect objects in image',
      'POST /api/video-analysis': 'Analyze video content',
      'POST /api/video-generation': 'Generate video from prompt or image',
      'GET /api/content/:id': 'Retrieve generated content',
      'GET /api/health': 'Health check',
    },
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('[Error]', err);
  res.status(err.status || 500).json({
    success: false,
    error: err.message || 'Internal server error',
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: 'Endpoint not found',
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`
╔════════════════════════════════════════╗
║      AI App Backend Server Started     ║
║                                        ║
║  Server: http://localhost:${PORT}       ║
║  Environment: ${process.env.NODE_ENV || 'development'}            ║
║  Database: ${process.env.DATABASE_PATH || './data/app.db'}  ║
║                                        ║
║  Available Endpoints:                  ║
║  • POST   /api/text-generation         ║
║  • POST   /api/image-generation        ║
║  • POST   /api/sentiment-analysis      ║
║  • POST   /api/object-detection        ║
║  • POST   /api/video-analysis          ║
║  • POST   /api/video-generation        ║
║  • GET    /api/content/:id             ║
║  • GET    /api/health                  ║
║                                        ║
╚════════════════════════════════════════╝
  `);
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n[Server] Shutting down gracefully...');
  db.close((err) => {
    if (err) {
      console.error('[Database] Error closing:', err);
    } else {
      console.log('[Database] Connection closed');
    }
    process.exit(0);
  });
});

module.exports = app;
