
if (!process.env.VERCEL) {
    require('custom-env').env(process.env.NODE_ENV, './config');
}

const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mongoose = require('mongoose');
const movieRoutes = require('./routes/movie');
const categoryRoutes = require('./routes/category');
const searchRoutes = require('./routes/search');
const users = require('./routes/user');
const tokens = require('./routes/token');

// Enhanced Connection Code
const connectDB = async () => {
  if (mongoose.connection.readyState >= 1) return; // Reuse existing connection in serverless
  try {
    await mongoose.connect(process.env.CONNECTION_STRING, {
      serverSelectionTimeoutMS: 10000,
    });
    console.log('Connected to MongoDB');
  } catch (err) {
    console.error('MongoDB connection error:', err);
    // Do not call process.exit() in serverless — it crashes the function for all requests
  }
};

connectDB(); // Call the function

// Initialize the Express application
const app = express();

// Enable CORS to allow requests from other origins
app.use((req, res, next) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,PATCH,OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type,Authorization');
  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }
  next();
});

app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
}));
app.options('*', cors());

// Use body-parser to parse URL-encoded bodies and JSON
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

// Define routes for the application
app.use('/api/users', users); // Routes for user operations (e.g., create, update, delete users)
app.use('/api/tokens', tokens); // Routes for token operations (e.g., validate, create tokens)
app.use('/api/movies', movieRoutes);
app.use('/api/categories', categoryRoutes);
app.use('/api/movies/search', searchRoutes)

// const uploadsDir = path.join(__dirname, 'public/uploads');
// if (!fs.existsSync(uploadsDir)) {
//     fs.mkdirSync(uploadsDir, { recursive: true });
// }

// // app.use('/public/uploads', express.static(path.join(__dirname, 'public/uploads')));
// // Serve static files from React build
// const publicPath = path.join(__dirname, 'public');
// app.use(express.static(publicPath));

// // Serve React frontend
// app.get('*', (req, res) => {
//     res.sendFile(path.join(publicPath, 'index.html'));
// });

app.use((err, req, res, next) => {
    console.error('Unhandled error:', err.message || JSON.stringify(err));
    res.status(err.status || err.http_code || 500).json({ message: err.message || 'Internal Server Error' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
