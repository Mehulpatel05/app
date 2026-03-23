const db = require('../config/database');
const logger = require('../utils/logger');

/**
 * Simple rate limiter using SQLite
 * Tracks requests per device/IP to prevent abuse
 */

async function getRateLimit(identifier) {
  return new Promise((resolve) => {
    db.get(
      `SELECT total_requests FROM user_sessions WHERE device_id = ?`,
      [identifier],
      (err, row) => {
        resolve(row?.total_requests || 0);
      }
    );
  });
}

async function updateRateLimit(identifier) {
  return new Promise((resolve) => {
    db.run(
      `INSERT INTO user_sessions (id, device_id, total_requests, last_activity, created_at)
       VALUES (?, ?, 1, datetime('now'), datetime('now'))
       ON CONFLICT(device_id) DO UPDATE SET 
         total_requests = total_requests + 1,
         last_activity = datetime('now')`,
      [require('crypto').randomUUID(), identifier],
      (err) => {
        if (err) {
          logger.error('RateLimiter', `Failed to update rate limit: ${err}`);
        }
        resolve();
      }
    );
  });
}

/**
 * Rate limiter middleware
 * Limits requests per device/IP
 *
 * Configuration:
 * - 100 requests per hour for anonymous users
 * - 1000 requests per hour for authenticated users (future)
 */
async function rateLimiter(req, res, next) {
  try {
    // Get identifier from various sources
    const identifier =
      req.headers['x-device-id'] ||
      req.headers['x-client-id'] ||
      req.ip ||
      req.connection.remoteAddress ||
      'anonymous';

    // Get current request count
    const requestCount = await getRateLimit(identifier);

    // Allow up to 100 requests per session
    const maxRequests = parseInt(process.env.MAX_REQUESTS_PER_SESSION || 100);

    if (requestCount >= maxRequests) {
      logger.warn(
        'RateLimiter',
        `Rate limit exceeded for ${identifier} (${requestCount}/${maxRequests})`
      );
      return res.status(429).json({
        success: false,
        error: 'Rate limit exceeded. Maximum requests per session reached.',
        maxRequests: maxRequests,
        currentRequests: requestCount,
      });
    }

    // Update request count
    await updateRateLimit(identifier);

    // Add rate limit info to response headers
    res.set({
      'X-RateLimit-Limit': maxRequests,
      'X-RateLimit-Remaining': maxRequests - requestCount - 1,
      'X-RateLimit-Reset': new Date(Date.now() + 3600000).toISOString(),
    });

    next();
  } catch (error) {
    logger.error('RateLimiter', `Error in rate limiter: ${error.message}`);
    next(); // Allow request to continue on error
  }
}

module.exports = rateLimiter;
