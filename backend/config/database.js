const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');

const dbPath = process.env.DATABASE_PATH || path.join(__dirname, '../data/app.db');

// Ensure data directory exists
const dataDir = path.dirname(dbPath);
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

const db = new sqlite3.Database(dbPath, (err) => {
  if (err) {
    console.error('[Database] Error opening database:', err);
  } else {
    console.log('[Database] Connected to SQLite database');
    initializeTables();
  }
});

function initializeTables() {
  // Generated content table
  db.run(`
    CREATE TABLE IF NOT EXISTS generated_content (
      id TEXT PRIMARY KEY,
      type TEXT NOT NULL,
      input_text TEXT,
      input_image_path TEXT,
      output_path TEXT,
      output_url TEXT,
      model_used TEXT,
      processing_time INTEGER,
      status TEXT DEFAULT 'completed',
      error_message TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      expires_at DATETIME
    )
  `, (err) => {
    if (err) console.error('[Database] Error creating generated_content table:', err);
  });

  // Cache table
  db.run(`
    CREATE TABLE IF NOT EXISTS cache (
      id TEXT PRIMARY KEY,
      hash TEXT UNIQUE,
      type TEXT NOT NULL,
      input_data TEXT,
      output_data TEXT,
      model_used TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      expires_at DATETIME
    )
  `, (err) => {
    if (err) console.error('[Database] Error creating cache table:', err);
  });

  // API call logs
  db.run(`
    CREATE TABLE IF NOT EXISTS api_logs (
      id TEXT PRIMARY KEY,
      endpoint TEXT,
      method TEXT,
      status_code INTEGER,
      response_time INTEGER,
      error_message TEXT,
      timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `, (err) => {
    if (err) console.error('[Database] Error creating api_logs table:', err);
  });

  // User sessions
  db.run(`
    CREATE TABLE IF NOT EXISTS user_sessions (
      id TEXT PRIMARY KEY,
      device_id TEXT UNIQUE,
      last_activity DATETIME DEFAULT CURRENT_TIMESTAMP,
      total_requests INTEGER DEFAULT 0,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `, (err) => {
    if (err) console.error('[Database] Error creating user_sessions table:', err);
  });

  // Create indexes for faster queries
  db.run(`CREATE INDEX IF NOT EXISTS idx_content_type ON generated_content(type)`);
  db.run(`CREATE INDEX IF NOT EXISTS idx_cache_hash ON cache(hash)`);
  db.run(`CREATE INDEX IF NOT EXISTS idx_logs_timestamp ON api_logs(timestamp)`);
  db.run(`CREATE INDEX IF NOT EXISTS idx_session_device ON user_sessions(device_id)`);
}

module.exports = db;
