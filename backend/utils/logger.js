const db = require('../config/database');

// Color codes for terminal
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m',
};

function formatTime() {
  return new Date().toISOString().slice(11, 19);
}

const logger = {
  // Info level
  info: (tag, message) => {
    console.log(
      `${colors.cyan}[${formatTime()}]${colors.reset} ${colors.blue}[${tag}]${colors.reset} ${message}`
    );
  },

  // Success level
  success: (tag, message) => {
    console.log(
      `${colors.cyan}[${formatTime()}]${colors.reset} ${colors.green}[${tag}]${colors.reset} ${message}`
    );
  },

  // Warning level
  warn: (tag, message) => {
    console.warn(
      `${colors.cyan}[${formatTime()}]${colors.reset} ${colors.yellow}[${tag}]${colors.reset} ${message}`
    );
  },

  // Error level
  error: (tag, message) => {
    console.error(
      `${colors.cyan}[${formatTime()}]${colors.reset} ${colors.red}[${tag}]${colors.reset} ${message}`
    );
  },

  // Log API request
  logApiCall: (endpoint, method, statusCode, responseTime, error = null) => {
    const id = require('crypto').randomUUID();
    db.run(
      `INSERT INTO api_logs (id, endpoint, method, status_code, response_time, error_message, timestamp)
       VALUES (?, ?, ?, ?, ?, ?, datetime('now'))`,
      [id, endpoint, method, statusCode, responseTime, error],
      (err) => {
        if (err) console.error('[Logger] Failed to log API call:', err);
      }
    );

    const timeStr = `${responseTime}ms`;
    if (statusCode >= 200 && statusCode < 300) {
      logger.success('HTTP', `${method} ${endpoint} ${statusCode} (${timeStr})`);
    } else if (statusCode >= 400) {
      logger.error('HTTP', `${method} ${endpoint} ${statusCode} (${timeStr}) - ${error}`);
    } else {
      logger.warn('HTTP', `${method} ${endpoint} ${statusCode} (${timeStr})`);
    }
  },
};

module.exports = logger;
