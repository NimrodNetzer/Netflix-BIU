// MovieBox.js
import React, { useState } from 'react';
import './movieBox.css';
import MovieDetailsModal from './movieDetailsModal';
import CreateMovieForm from '../Admin/CreateMovieForm'; // Import the form component for creation/updating

function MovieBox({ movie, width, isAdmin = false }) {
  const [isModalOpen, setModalOpen] = useState(false);
  const [isEditModalOpen, setEditModalOpen] = useState(false);
  const [selectedMovie, setSelectedMovie] = useState(movie);
  const [autoPlay, setAutoPlay] = useState(false);

  const API_URL = process.env.REACT_APP_API_URL;

  const addToRecommendations = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) {
      console.error("No authentication token found.");
      return;
    }

    try {
      const response = await fetch(`${API_URL}/api/movies/${movie._id}/recommend`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to add movie to recommendations (status: ${response.status})`);
      }

      console.log("Movie successfully added to recommendations.");
    } catch (error) {
      console.error("Error adding movie to recommendations:", error);
    }
  };

  const handleInfoClick = () => {
    setSelectedMovie(movie);
    setAutoPlay(false);
    setModalOpen(true);
  };

  const handlePlayClick = async () => {
    setSelectedMovie(movie);
    setAutoPlay(true);
    setModalOpen(true);

    await addToRecommendations();
  };

  // Open edit modal and pass movie data to form
  const handleEditClick = () => {
    setEditModalOpen(true);
  };

  const handleDeleteClick = async () => {
    const confirmDelete = window.confirm("Are you sure you want to delete this movie?");
    if (!confirmDelete) return;

    try {
      const response = await fetch(`${API_URL}/api/movies/${movie._id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('jwt'),
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to delete movie (status: ${response.status})`);
      }

      alert("Movie deleted successfully");
      window.location.reload();
    } catch (error) {
      console.error("Error deleting movie:", error);
      alert("Failed to delete movie");
    }
  };

  // Update the local movie state after an update
  const updateMovie = (updatedMovie) => {
    setSelectedMovie(updatedMovie);
    setAutoPlay(false);
    // Optionally, you could refresh the movie list or update parent state here
  };

  const closeModal = () => {
    setModalOpen(false);
    setAutoPlay(false);
  };

  // Callback for closing the edit form
  const closeEditModal = () => {
    setEditModalOpen(false);
  };

  // When the movie is updated via the form, close the edit modal and update state
  const handleEditSubmit = () => {
    closeEditModal();
    // Optionally refresh page or update state as needed
  };

  return (
    <div className="movie-box" style={{ width }}>
      <img
        src={`${API_URL}/${movie.picture}`}
        alt={movie.name}
        className="default-image"
      />
      <div className="movie-hover-overlay">
        <div className="movie-hover-content">
          <div className="movie-title2">{movie.name}</div>
          <div className="movie-buttons">
            {!isAdmin && (
              <button className="play-button" onClick={handlePlayClick}>▶ Play</button>
            )}
            <button className="info-button" onClick={handleInfoClick}>Info</button>
            {isAdmin && (
              <>
                <button className="edit-button" onClick={handleEditClick}>✏ Edit</button>
                <button className="delete-button" onClick={handleDeleteClick}>🗑 Delete</button>
              </>
            )}
          </div>
          <div className="movie-info">
            <div className="movie-detail-box">
              <span className="movie-age">{movie.age}+</span>
            </div>
            <div className="movie-detail-box">
              <span className="movie-quality">{movie.quality}</span>
            </div>
            <div className="movie-detail-box">
              <span className="movie-time">{movie.time}</span>
            </div>
          </div>
        </div>
      </div>
      <MovieDetailsModal 
        movie={selectedMovie} 
        isOpen={isModalOpen} 
        onClose={closeModal} 
        updateMovie={updateMovie} 
        autoPlay={autoPlay}
      />
      {isEditModalOpen && (
        <CreateMovieForm 
          movieData={movie} 
          onSubmit={handleEditSubmit} 
          onCancel={closeEditModal} 
        />
      )}
    </div>
  );
}

export default MovieBox;
