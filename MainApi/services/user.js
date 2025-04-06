// Import mongoose to validate ObjectId and the User model
const mongoose = require('mongoose');
const User = require('../models/user');
const user = require('../models/user');

/**
 * Create a new user
 * @param {String} email - User's email address
 * @param {String} password - User's password
 * @param {String} nickname - User's nickname
 * @param {String} picture - URL of the user's picture
 * @returns {Object} The created user
 */
const createUser = async (email, password, nickname, picture) => {
    try {
        // Validate required fields
        if (!email) {
            throw new Error('Email is required.');
        }
        if (!password) {
            throw new Error('Password is required.');
        }
        if (!nickname) {
            throw new Error('Nickname is required.');
        }

        // Validate email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            throw new Error('Invalid email format.');
        }

        // Validate password: At least 8 characters, allow special characters, English letters only
        const passwordRegex = /^[\x20-\x7E]{8,}$/;
        if (!passwordRegex.test(password)) {
            throw new Error('Password must be at least 8 characters long and contain only English letters, numbers, or special characters.');
        }

        // Create a new user instance
        const user = new User({ email, password, nickname, picture });
        return await user.save(); // Save the user to the database
    } catch (error) {
        // Handle specific errors
        if (error.name === 'ValidationError') {
            throw new Error('Invalid user data provided.');
        }
        if (error.code === 11000) {
            throw new Error('A user with this email already exists.');
        }
        // Generic error message for other cases
        throw new Error(`Error creating user: ${error.message}`);
    }
};




/**
 * Get a user by ID
 * @param {String} id - User's ID
 * @returns {Object|null} The user object if found, otherwise null
 */
const getUserById = async (id) => {
    try {
        const user = await User.findById(id);
        if (!user) {
            return null;
        }
        return user;
    } catch (error) {
        throw new Error(`Error fetching user by ID: ${error.message}`);
    }
};

/**
 * Get all users
 * @returns {Array} List of all users
 */
const getUsers = async () => {
    try {
        return await User.find({}); // Retrieve all users from the database
    } catch (error) {
        throw new Error(`Error fetching users: ${error.message}`);
    }
};
const getUserByEmail = async (email) => {
    return await User.findOne({ email }); // Query the database for a user with the given email
};

module.exports = { createUser, getUserById, getUsers, getUserByEmail };