import React, { useState } from 'react';
import './FeaturedVideo.css';
import MovieDetailsModal from './movieDetailsModal'; // Import the modal component
const API_URL = process.env.REACT_APP_API_URL;

function FeaturedVideo({ movie }) {
  const [isModalOpen, setModalOpen] = useState(false); // Modal state
  const [selectedMovie, setSelectedMovie] = useState(movie); // Track the selected movie

  if (!movie) return null; // Prevent rendering if no movie is available

  const handleVideoClick = () => {
    setSelectedMovie(movie); // Set the clicked movie as the selected one
    setModalOpen(true);
  };

  const updateMovie = (newMovie) => {
    setSelectedMovie(newMovie);
  };

  return (
    <div className="featured-video">
      <video
        src={`${API_URL}/${movie.video}`}
        type="video/mp4"
        autoPlay
        muted
        loop
        className="featured-video-player"
        onClick={handleVideoClick} // Ensure this updates state properly
      />
      <div className="video-overlay" onClick={handleVideoClick}>
        <h1 className="featured-title">{movie.title}</h1>
        <p className="featured-description">{movie.description}</p>
      </div>

      {/* Ensure the modal gets the correct state and update function */}
      {isModalOpen && (
        <MovieDetailsModal
          movie={selectedMovie}
          isOpen={isModalOpen}
          onClose={() => setModalOpen(false)} // Ensure modal closes
          updateMovie={updateMovie} // Pass updateMovie function
        />
      )}
    </div>
  );
}

export default FeaturedVideo;