const mongoose = require('mongoose');
const counter = require('./counter');

const movieSchema = new mongoose.Schema({
  _id: { type: Number },
  name: {
    type: String,
    required: true,
    trim: true,
  },
  picture: {
    type: String,
    required: true,
  },
  video: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true,
  },
  age: {
    type: Number,
    required: true,
  },
  time: {
    type: String, // e.g., "2h 15m"
    required: true,
  },
  releaseDate: {
    type: Date,
    required: true,
  },
  quality: {
    type: String, // e.g., "HD", "SD", "4K"
    required: true,
  },
  categories: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Category',
      required: true,
    },
  ],
  cast: [
    {
      name: { type: String, required: true },
      role: { type: String },
    },
  ],
  properties: {
    type: Map,
    of: String,
    required:false
  },
  movieData: {
    type: Object,
    required:false
  },
  author: {
    type: String,
    required: true,
  },
}, {
  timestamps: true, // Adds createdAt and updatedAt fields
});

movieSchema.pre('save', async function (next) {
  // Only generate a new ID if the document is new and does not already have an _id
  if (this.isNew && !this._id) {
    this._id = await counter.getNextSequence('movieId');
    console.log(`Assigned new Movie ID: ${this._id}`);
  } else {
    console.log(`Preserving existing Movie ID: ${this._id}`);
  }
  next();
});


const Movie = mongoose.model('Movie', movieSchema);

module.exports = Movie;
