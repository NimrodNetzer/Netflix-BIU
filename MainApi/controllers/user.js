// controllers/user.js

// Import the user service to handle the logic
const userService = require('../services/user');

/**
 * Create a new user
 * @param {Object} req - The request object containing user data (email, password, nickname, picture)
 * @param {Object} res - The response object to send the created user or errors
 */
const createUser = async (req, res) => {
    try {
        const { email, password, nickname, picture } = req.body; // Extract user data from request
        
        // Validate the presence of the email field
        if (!email) {
            return res.status(400).json({ errors: ['Invalid request: "email" field is required'] });
        }

        // Validate the email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Standard email regex
        if (!emailRegex.test(email)) {
            return res.status(400).json({ errors: ['Invalid request: Invalid email format'] });
        }

        // Check if the email already exists in the database
        const existingUser = await userService.getUserByEmail(email);
        if (existingUser) {
            return res.status(400).json({ errors: ['A user with this email already exists.'] });
        }


        // Create the new user
        const user = await userService.createUser(email, password, nickname, picture);
        res.status(201)
            .location(`/api/users/${user._id}`)
            .send();
    } catch (error) {
        res.status(400).json({ errors: [error.message] }); // Handle errors
    }
};


/**
 * Retrieve all users
 * @param {Object} req - The request object
 * @param {Object} res - The response object to send the list of users
 */
const getUsers = async (req, res) => {
    try {
        const users = await userService.getUsers(); // Fetch all users from the service
        res.status(200).json(users); // Respond with the list of users
    } catch (error) {
        res.status(400).json({ errors: [error.message] }); // Handle server errors
    }
};

/**
 * Retrieve a specific user by ID
 * @param {Object} req - The request object containing user ID in the params
 * @param {Object} res - The response object to send the user data or errors
 */
const getUser = async (req, res) => {
    try {
        const user = await userService.getUserById(req.params.id); // Fetch user by ID
        if (!user) {
            return res.status(404).json({ errors: ['User not found'] }); // Handle not found case
        }
        res.status(200).json(user); // Respond with the user data
    } catch (error) {
        res.status(400).json({ errors: [error.message] }); // Handle errors
    }
};

// Export all user-related functions
module.exports = { createUser, getUsers, getUser };