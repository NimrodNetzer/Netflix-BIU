// recommendationService.js
const socketClient = require('../utils/socketClient'); // Import socket client for communication with Recommendation System
const User = require('../models/user');
const mongoose = require('mongoose'); 
require('custom-env').env(process.env.NODE_ENV, './config');
const serverIp = process.env.RECOMMENDATION_IP;
const serverPort = process.env.RECOMMENDATION_PORT;
const SocketClient = require('../utils/socketClient');

const fetchRecommendations = async (userId, movieId) => {
    const socketClient = new SocketClient(serverIp, serverPort);
    await socketClient.connect();
    const request = `GET ${userId} ${movieId}\n`;
    const response = await socketClient.send(request);
    socketClient.disconnect();

    if (response.startsWith('404')) {
        const error = new Error('User or Movie not found');
        error.status = 404;
        throw error;
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
   

module.exports = {
    fetchRecommendations,
    addRecommendation,
    deleteWatchedMovie
};
