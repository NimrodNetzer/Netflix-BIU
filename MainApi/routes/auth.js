const {getUserById} = require('../services/user')
const key = process.env.SECRET;
const jwt = require('jsonwebtoken')
const isAuthenticated = async (req, res, next) => {
    if (req.headers.authorization) {
        const token = req.headers.authorization.split(" ")[1];
        try {
            const data = jwt.verify(token, key);
            req.userId = data.userId;
            req.admin = data.admin;
            return next()
        } catch (err) {
            return res.status(401).send("Invalid Token");
        }
    }
    else {
        return res.status(403).send('Token required');
    }
};
module.exports = isAuthenticated;
