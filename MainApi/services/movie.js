const Movie = require('../models/movie'); // Path to your Movie model
const Category = require('../models/category'); // Path to your Category model
const User = require('../models/user'); // Path to your User model

const createMovie = async (movieData) => {
  try {
    // Required field validation
    const requiredFields = ['name', 'description', 'picture', 'age', 'time', 'releaseDate', 'quality', 'categories', 'author'];
    for (const field of requiredFields) {
      if (!movieData[field]) {
        throw new Error(`Missing required field: ${field}`);
      }
    }

    // Validate categories
    if (!Array.isArray(movieData.categories) || movieData.categories.length === 0) {
      throw new Error('Invalid categories: must be a non-empty array of category IDs');
    }

    const categories = await Category.find({ _id: { $in: movieData.categories } });
    if (categories.length !== movieData.categories.length) {
      throw new Error('One or more categories provided do not exist');
    }

    // Specific field validations
    if (typeof movieData.name !== 'string' || movieData.name.trim() === '') {
      throw new Error('Invalid name: must be a non-empty string');
    }
    if (typeof movieData.description !== 'string' || movieData.description.length < 20) {
      throw new Error('Invalid description: must be at least 20 characters long');
    }

    if (!Number.isInteger(movieData.age) || movieData.age < 0 || movieData.age > 18) {
      throw new Error('Invalid age: must be an integer between 0 and 18');
    }
    if (!/^\d+h \d+m$/.test(movieData.time)) {
      throw new Error('Invalid time format: must be in the format "Xh Ym"');
    }
    if (isNaN(new Date(movieData.releaseDate).getTime())) {
      throw new Error('Invalid releaseDate: must be a valid date');
    }
    if (!['HD', 'SD', '4K'].includes(movieData.quality)) {
      throw new Error('Invalid quality: must be one of "HD", "SD", or "4K"');
    }

    // Optional validations
    if (movieData.cast) {
      if (!Array.isArray(movieData.cast)) {
        throw new Error('Invalid cast: must be an array');
      }
      movieData.cast.forEach((member, index) => {
        if (!member.name) {
          throw new Error(`Invalid cast member at index ${index}: missing name`);
        }
      });
    }

    // Check for duplicates
    const existingMovie = await Movie.findOne({ name: movieData.name, releaseDate: movieData.releaseDate });
    if (existingMovie) {
      throw new Error('A movie with the same name and release date already exists');
    }

    // Create and save the movie
    const newMovie = new Movie({ ...movieData });
    const savedMovie = await newMovie.save();

    return savedMovie;
  } catch (error) {
    console.error('Error creating movie:', error);
    throw new Error(error.message || 'Failed to create movie');
  }
};

const getMovieById = async (movieId) => {
  try {
    // Populate categories and any other referenced fields if needed
    const movie = await Movie.findById(movieId).populate('categories', 'name promoted');
    if (!movie) {
      return null;
    }
    return movie;
  } catch (error) {
    console.error('Error in getMovieById service:', error);
    throw new Error(error.message || 'Failed to fetch movie');
  }
};

const deleteMovieById = async (id) => {
  try {
    const movie = await Movie.findById(id);
    if (!movie) {
      return null;
    }

    await movie.deleteOne();
    return movie;
  } catch (error) {
    throw new Error(`Error deleting movie: ${error.message}`);
  }
};

const replaceMovieById = async (id, movieUpdates) => {
  try {
    // Remove _id from the updates if it exists
    const { _id, ...updates } = movieUpdates;

    // Use findByIdAndUpdate with overwrite to replace the document entirely
    const updatedMovie = await Movie.findByIdAndUpdate(
      id,
      updates,
      {
        new: true,           // Return the updated document
        runValidators: true, // Run schema validations on update
        overwrite: true      // Replace the document completely
      }
    );

    if (!updatedMovie) {
      throw new Error('Movie not found');
    }

    return updatedMovie;
  } catch (error) {
    console.error('Error replacing movie:', error);
    throw error;
  }
};


const getMoviesByPromotedCategories = async (userId) => {
  // 1. Fetch the user and their watched movies
  const user = await User.findById(userId)
    .populate({
      path: 'moviesList.movieId',
      populate: { path: 'categories', select: 'name promoted' } // ✅ Populate categories for watched movies
    })
    .exec();

  if (!user) {
    throw new Error('User not found.');
  }

  // 2. Get the last 20 watched movies (sorted by `watchedAt` descending)
  const recentlyWatchedMovies = user.moviesList
    .sort((a, b) => new Date(b.watchedAt) - new Date(a.watchedAt))
    .slice(0, 20);

  // 3. Fetch all promoted categories
  const promotedCategories = await Category.find({ promoted: true });
  if (promotedCategories.length === 0) {
    throw new Error('No promoted categories found.');
  }

  // 4. Collect IDs of the watched movies
  const watchedMovieIds = recentlyWatchedMovies.map(
    (movieRecord) => movieRecord.movieId._id
  );

  // 5. Fetch movies from promoted categories that are not in the watched list
  const promotedCategoryIds = promotedCategories.map((cat) => cat._id);
  const unWatchedPromotedMovies = await Movie.find({
    categories: { $in: promotedCategoryIds }, // Check if any promoted category is in the movie's categories
    _id: { $nin: watchedMovieIds }, // Exclude already watched movies
  })
    .populate('categories', 'name promoted') // ✅ Populate category details
    .exec();

  // 6. Group promoted movies by category, picking up to 20 random for each
  const promotedMoviesGrouped = promotedCategories.map((category) => {
    const moviesInCategory = unWatchedPromotedMovies
      .filter((movie) =>
        movie.categories.some((cat) => cat._id.toString() === category._id.toString())
      )
      .sort(() => 0.5 - Math.random()) // Randomize
      .slice(0, 20); // Take up to 20

    return {
      category: category.name,
      category_id: category._id,
      promoted: category.promoted,
      movies: moviesInCategory,
    };
  });

  // 7. Randomize the watched movies array
  const shuffledWatchedMovies = recentlyWatchedMovies.sort(() => 0.5 - Math.random());

  // 8. Create a special category for watched movies, ensuring categories are populated
  const watchedCategory = {
    category: 'Watched Movies',
    category_id: null, // or any placeholder
    promoted: false,
    movies: shuffledWatchedMovies.map((entry) => ({
      ...entry.movieId.toObject(), // ✅ Convert Mongoose document to plain JS object
      categories: entry.movieId.categories || [] // ✅ Ensure categories is an array of objects
    })),
  };

  // 9. Combine results and return
  return [...promotedMoviesGrouped, watchedCategory];
};



module.exports = {
  createMovie,
  getMovieById,
  deleteMovieById,
  replaceMovieById,
  getMoviesByPromotedCategories,
};
