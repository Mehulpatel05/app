const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const db = require('../config/database');

const STORAGE_DIR = path.join(__dirname, '../storage');

// Ensure storage directory exists
if (!fs.existsSync(STORAGE_DIR)) {
  fs.mkdirSync(STORAGE_DIR, { recursive: true });
}

// Save generated content (image, video, etc.)
async function saveContent(type, buffer, metadata = {}) {
  return new Promise((resolve, reject) => {
    const id = crypto.randomUUID();
    const ext = getFileExtension(type);
    const filename = `${id}${ext}`;
    const filepath = path.join(STORAGE_DIR, filename);

    fs.writeFile(filepath, buffer, (err) => {
      if (err) {
        console.error('[Storage] Save error:', err);
        reject(err);
        return;
      }

      // Save metadata to database
      const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7 days
      db.run(
        `INSERT INTO generated_content 
         (id, type, input_text, output_path, model_used, status, created_at, expires_at)
         VALUES (?, ?, ?, ?, ?, 'completed', datetime('now'), ?)`,
        [
          id,
          type,
          metadata.inputText || null,
          filename,
          metadata.model || null,
          expiresAt,
        ],
        (err) => {
          if (err) {
            console.error('[Storage] DB error:', err);
          }
          resolve({
            id,
            filename,
            path: filepath,
            url: `/api/content/${id}`,
          });
        }
      );
    });
  });
}

// Get content by ID
async function getContent(id) {
  return new Promise((resolve) => {
    db.get(
      `SELECT output_path FROM generated_content WHERE id = ?`,
      [id],
      (err, row) => {
        if (err || !row) {
          resolve(null);
          return;
        }

        const filepath = path.join(STORAGE_DIR, row.output_path);
        if (fs.existsSync(filepath)) {
          resolve(filepath);
        } else {
          resolve(null);
        }
      }
    );
  });
}

// Get file extension based on type
function getFileExtension(type) {
  const extensions = {
    image: '.jpg',
    video: '.mp4',
    audio: '.mp3',
  };
  return extensions[type] || '.bin';
}

// Clean expired content
async function cleanExpiredContent() {
  return new Promise((resolve) => {
    db.all(
      `SELECT id, output_path FROM generated_content WHERE expires_at < datetime('now')`,
      (err, rows) => {
        if (err || !rows) {
          resolve();
          return;
        }

        rows.forEach((row) => {
          const filepath = path.join(STORAGE_DIR, row.output_path);
          if (fs.existsSync(filepath)) {
            fs.unlink(filepath, (err) => {
              if (err) console.error('[Storage] Delete error:', err);
            });
          }
        });

        db.run(
          `DELETE FROM generated_content WHERE expires_at < datetime('now')`,
          (err) => {
            if (err) {
              console.error('[Storage] DB cleanup error:', err);
            } else {
              console.log('[Storage] Expired content cleaned');
            }
            resolve();
          }
        );
      }
    );
  });
}

// Run cleanup daily
setInterval(cleanExpiredContent, 24 * 60 * 60 * 1000);

module.exports = {
  saveContent,
  getContent,
  cleanExpiredContent,
  STORAGE_DIR,
};
