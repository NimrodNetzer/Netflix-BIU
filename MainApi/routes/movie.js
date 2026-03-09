const express = require('express');
const {
    createMovieController,
    getMovie,
    deleteMovie,
    updateMovie,
    getRecommendations,
    addRecommendation,
    seedRecommendationsController,
    populateAndSeedController,
    getMovies
} = require('../controllers/movie');
const { upload } = require('../middleware/uploadMiddleware'); // Adjust the path if necessary

const isAuthenticated = require('./auth'); // Import the authentication middleware

const router = express.Router();
router.use(isAuthenticated);

// These routes must come before /:id to avoid being captured as a param
router.post('/seed-recommendations', seedRecommendationsController);
router.post('/populate-and-seed', populateAndSeedController);

// Movie Routes
router
  .route('/')
  .post(
    upload.fields([
      { name: 'image', maxCount: 1 },
      { name: 'video', maxCount: 1 }
    ]),
    createMovieController
  )
  .get(getMovies);

  router
  .route('/:id')
  .get(getMovie)
  .delete(deleteMovie)
  .put(
    upload.fields([
      { name: 'image', maxCount: 1 },
      { name: 'video', maxCount: 1 }
    ]),
    updateMovie
  );

// Recommendation Routes
router
    .route('/:id/recommend')
    .get(getRecommendations)
    .post(addRecommendation);

module.exports = router;
