// controllers/token.js

// Import the token service to handle the logic
const tokenService = require('../services/token');

/**
 * Authenticate a user by email and password
 * @param {Object} req - The request object containing email and password in the body
 * @param {Object} res - The response object to send the user ID if authenticated or an error
 */
const authenticateUser = async (req, res) => {
    try {
        const { email, password } = req.body; // Extract email and password from request
        const token = await tokenService.authenticateUser(email, password); // Authenticate the user

        if (!token) {
            return res.status(401).json({ errors: ['Invalid email or password'] }); // Handle invalid credentials
        }

        res.status(200).json({token }); // Respond with the authenticated user's ID
    } catch (error) {
        res.status(500).json({ errors: [error.message] }); // Handle server errors
    }
};

// Export the authentication function
module.exports = { authenticateUser };
