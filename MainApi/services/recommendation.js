// recommendationService.js
const socketClient = require('../utils/socketClient'); // Import socket client for communication with Recommendation System
const User = require('../models/user');
const mongoose = require('mongoose'); 
if (!process.env.VERCEL) {
    require('custom-env').env(process.env.NODE_ENV, './config');
}
const serverIp = process.env.RECOMMENDATION_IP;
const serverPort = process.env.RECOMMENDATION_PORT;
const SocketClient = require('../utils/socketClient');

let isReseeding = false;

<<<<<<< HEAD
=======
// Seed a single user's watch history into the C++ server from MongoDB
>>>>>>> feature/my-feature
const seedUser = async (userId) => {
    const user = await User.findById(userId, '_id moviesList');
    if (!user || !user.moviesList || user.moviesList.length === 0) return;

    const socketClient = new SocketClient(serverIp, serverPort);
    try {
        await socketClient.connect();
        for (const { movieId } of user.moviesList) {
            try {
<<<<<<< HEAD
                const postResponse = await socketClient.send(`POST ${userId} ${movieId}\n`);
                if (postResponse.startsWith('404')) {
                    await socketClient.send(`PATCH ${userId} ${movieId}\n`);
                }
            } catch (err) {
                // continue seeding other movies
            }
=======
                const res = await socketClient.send(`POST ${userId} ${movieId}\n`);
                if (res.startsWith('404')) {
                    await socketClient.send(`PATCH ${userId} ${movieId}\n`);
                }
            } catch (err) { /* continue */ }
>>>>>>> feature/my-feature
        }
    } finally {
        socketClient.disconnect();
    }
};

<<<<<<< HEAD
=======
// Seed ALL users into C++ server from MongoDB (full reseed after cold start)
const seedRecommendations = async () => {
    const users = await User.find({}, '_id moviesList');
    let seeded = 0;
    for (const user of users) {
        if (!user.moviesList || user.moviesList.length === 0) continue;
        const socketClient = new SocketClient(serverIp, serverPort);
        try {
            await socketClient.connect();
            for (const { movieId } of user.moviesList) {
                try {
                    const res = await socketClient.send(`POST ${user._id} ${movieId}\n`);
                    if (res.startsWith('201')) seeded++;
                    else if (res.startsWith('404')) {
                        const patch = await socketClient.send(`PATCH ${user._id} ${movieId}\n`);
                        if (patch.startsWith('204')) seeded++;
                    }
                } catch (err) { /* continue */ }
            }
        } finally {
            socketClient.disconnect();
        }
    }
    return seeded;
};

// Assign random watch histories to users with <3 movies, then seed C++ server
const populateAndSeed = async () => {
    const Movie = require('../models/movie');
    const movies = await Movie.find({}, '_id');
    const movieIds = movies.map(m => m._id);
    if (movieIds.length === 0) throw new Error('No movies in database');

    const users = await User.find({}, '_id moviesList');
    for (const user of users) {
        if (user.moviesList && user.moviesList.length >= 3) continue;
        const shuffled = [...movieIds].sort(() => Math.random() - 0.5);
        const count = Math.floor(movieIds.length * (0.4 + Math.random() * 0.3));
        const moviesList = shuffled.slice(0, count).map(id => ({ movieId: id, watchedAt: new Date() }));
        await User.updateOne({ _id: user._id }, { $set: { moviesList } });
    }
    return seedRecommendations();
};

>>>>>>> feature/my-feature
const fetchRecommendations = async (userId, movieId) => {
    const socketClient = new SocketClient(serverIp, serverPort);
    await socketClient.connect();
    const request = `GET ${userId} ${movieId}\n`;
    let response = await socketClient.send(request);
    socketClient.disconnect();

    if (response.startsWith('404')) {
<<<<<<< HEAD
        // User may not exist in C++ server yet — seed them from MongoDB and retry
=======
        // Seed this user's data from MongoDB and retry
>>>>>>> feature/my-feature
        await seedUser(userId);

        const retrySocket = new SocketClient(serverIp, serverPort);
        await retrySocket.connect();
        response = await retrySocket.send(request);
        retrySocket.disconnect();

        if (response.startsWith('404')) {
<<<<<<< HEAD
            // C++ server is cold (Railway restart wiped data) — reseed all users in background
=======
            // C++ server is cold (Railway restart) — trigger full reseed in background
>>>>>>> feature/my-feature
            if (!isReseeding) {
                isReseeding = true;
                populateAndSeed()
                    .catch(err => console.error('Background reseed failed:', err))
                    .finally(() => { isReseeding = false; });
            }
            const error = new Error('User or Movie not found');
            error.status = 404;
            throw error;
        }
    }

    return response.split(',').map((movieId) => parseInt(movieId.trim(), 10));
};

const addRecommendation = async (userId, movieId) => {
    const socketClient = new SocketClient(serverIp, serverPort);

    try {
      await updateMongoDBMoviesList(userId, movieId);
   
      await socketClient.connect();
      const postResponse = await socketClient.send(`POST ${userId} ${movieId}\n`);
      if (postResponse.startsWith('201')) {
        return 'Recommendation added successfully';
      }
      
      if (postResponse.startsWith('404')) {
        const patchResponse = await socketClient.send(`PATCH ${userId} ${movieId}\n`);
        if (patchResponse.startsWith('204')) {
          return 'Recommendation created successfully via PATCH';
        }
        throw new Error(`PATCH failed: ${patchResponse}`);
      }

      throw new Error(`Unexpected response: ${postResponse}`); 
    } catch (error) {
      console.error('Recommendation System error:', error);
      error.status = 500;
      throw error;
    }
    finally {
      socketClient.disconnect(); // Ensure the socket is disconnected

    }
};

const updateMongoDBMoviesList = async (userId, movieId) => {
    try {
      await User.updateOne(
        { _id: userId },
        { $pull: { moviesList: { movieId } } }
      );
      const result = await User.findOneAndUpdate(
        { _id: userId },
        {
          $push: {
            moviesList: {
              $each: [{ movieId, watchedAt: new Date() }],
              $position: 0,   // Insert at the beginning
            }
          }
        },
        { new: true }
      );
        if (!result) {
            throw new Error(`User with ID ${userId} not found`);
        }

        return result;
    } catch (error) {
        console.error(`Error adding movie to user ${userId}:`, error.message);
        throw error;
    }
   
};


const deleteWatchedMovie = async (movieId) => {
    const socketClient = new SocketClient(serverIp, serverPort);

    try {
      const users = await User.find({});
      await User.updateMany({}, { $pull: { moviesList: { movieId } } });
      await socketClient.connect();
      for (const user of users) {
          try {
              await socketClient.send(`DELETE ${user._id} ${movieId}\n`);
          } catch (err) {
              console.error(`Failed to delete recommendation for user ${user._id}:`, err.message);
          }
      }
      
      return `Movie ${movieId} deleted from ${users.length} users`;
    } catch (error) {
      console.error('Delete recommendation error:', error);
      error.status = 500;
      throw error;
    } finally{
      socketClient.disconnect();
    }
};
   

const populateAndSeed = async () => {
    const Movie = require('../models/movie');

    // Get all movie IDs from MongoDB
    const movies = await Movie.find({}, '_id');
    const movieIds = movies.map(m => m._id);
    if (movieIds.length === 0) throw new Error('No movies in database');

    const users = await User.find({}, '_id moviesList');
    if (users.length === 0) throw new Error('No users in database');

    // Assign each user a random 40-70% slice of all movies as their watch history
    // Only populate users who have fewer than 3 movies watched
    for (const user of users) {
        if (user.moviesList && user.moviesList.length >= 3) continue;

        const shuffled = [...movieIds].sort(() => Math.random() - 0.5);
        const count = Math.floor(movieIds.length * (0.4 + Math.random() * 0.3));
        const assigned = shuffled.slice(0, count);

        const moviesList = assigned.map(id => ({ movieId: id, watchedAt: new Date() }));
        await User.updateOne({ _id: user._id }, { $set: { moviesList } });
    }

    // Now seed the C++ server from the updated MongoDB data
    return seedRecommendations();
};

const seedRecommendations = async () => {
    const users = await User.find({}, '_id moviesList');
    let seeded = 0;

    for (const user of users) {
        if (!user.moviesList || user.moviesList.length === 0) continue;
        const socketClient = new SocketClient(serverIp, serverPort);
        try {
            await socketClient.connect();
            for (const { movieId } of user.moviesList) {
                try {
                    const postResponse = await socketClient.send(`POST ${user._id} ${movieId}\n`);
                    if (postResponse.startsWith('201')) {
                        seeded++;
                    } else if (postResponse.startsWith('404')) {
                        const patchResponse = await socketClient.send(`PATCH ${user._id} ${movieId}\n`);
                        if (patchResponse.startsWith('204')) seeded++;
                    }
                } catch (err) {
                    console.error(`Seed error user ${user._id} movie ${movieId}:`, err.message);
                }
            }
        } finally {
            socketClient.disconnect();
        }
    }

    return seeded;
};

module.exports = {
    fetchRecommendations,
    addRecommendation,
    deleteWatchedMovie,
    seedRecommendations,
    populateAndSeed
};
