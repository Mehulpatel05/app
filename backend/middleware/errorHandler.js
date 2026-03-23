const logger = require('../utils/logger');

/**
 * Global error handler middleware
 * Catches all errors and returns formatted JSON response
 */
function errorHandler(err, req, res, next) {
  const statusCode = err.statusCode || err.status || 500;
  const message = err.message || 'Internal server error';

  // Log the error
  logger.error('ErrorHandler', `${req.method} ${req.path} - ${statusCode}: ${message}`);

  // Send error response
  res.status(statusCode).json({
    success: false,
    error: message,
    statusCode: statusCode,
    timestamp: new Date().toISOString(),
  });
}

/**
 * Create custom error class for API errors
 */
class ApiError extends Error {
  constructor(message, statusCode = 500) {
    super(message);
    this.statusCode = statusCode;
    this.name = 'ApiError';
  }
}

/**
 * Async route wrapper to catch errors
 */
function asyncHandler(fn) {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
}

module.exports = {
  errorHandler,
  ApiError,
  asyncHandler,
};
