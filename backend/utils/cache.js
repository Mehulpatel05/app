const crypto = require('crypto');
const db = require('../config/database');

// Generate hash for cache key
function generateHash(input) {
  return crypto
    .createHash('sha256')
    .update(JSON.stringify(input))
    .digest('hex');
}

// Get from cache
async function getFromCache(type, input) {
  return new Promise((resolve, reject) => {
    if (!process.env.ENABLE_CACHE || process.env.ENABLE_CACHE === 'false') {
      resolve(null);
      return;
    }

    const hash = generateHash(input);
    const now = new Date();

    db.get(
      `SELECT output_data FROM cache 
       WHERE hash = ? AND type = ? AND expires_at > ?`,
      [hash, type, now],
      (err, row) => {
        if (err) {
          console.error('[Cache] Get error:', err);
          resolve(null);
        } else {
          if (row) {
            console.log('[Cache] Hit for type:', type);
            resolve(JSON.parse(row.output_data));
          } else {
            resolve(null);
          }
        }
      }
    );
  });
}

// Save to cache
async function saveToCache(type, input, output) {
  return new Promise((resolve, reject) => {
    if (!process.env.ENABLE_CACHE || process.env.ENABLE_CACHE === 'false') {
      resolve();
      return;
    }

    const hash = generateHash(input);
    const id = crypto.randomUUID();
    const expiryHours = parseInt(process.env.CACHE_EXPIRY_HOURS || 24);
    const expiresAt = new Date(Date.now() + expiryHours * 60 * 60 * 1000);

    db.run(
      `INSERT OR REPLACE INTO cache (id, hash, type, input_data, output_data, created_at, expires_at)
       VALUES (?, ?, ?, ?, ?, datetime('now'), ?)`,
      [
        id,
        hash,
        type,
        JSON.stringify(input),
        JSON.stringify(output),
        expiresAt,
      ],
      (err) => {
        if (err) {
          console.error('[Cache] Save error:', err);
        } else {
          console.log('[Cache] Saved for type:', type);
        }
        resolve();
      }
    );
  });
}

// Clean expired cache
async function cleanExpiredCache() {
  return new Promise((resolve) => {
    db.run(
      `DELETE FROM cache WHERE expires_at < datetime('now')`,
      (err) => {
        if (err) {
          console.error('[Cache] Cleanup error:', err);
        } else {
          console.log('[Cache] Expired entries cleaned');
        }
        resolve();
      }
    );
  });
}

// Run cleanup every hour
setInterval(cleanExpiredCache, 60 * 60 * 1000);

module.exports = {
  getFromCache,
  saveToCache,
  cleanExpiredCache,
  generateHash,
};
