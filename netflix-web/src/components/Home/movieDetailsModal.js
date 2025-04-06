import React, { useEffect, useState } from 'react';
import VideoPlayer from '../Utils/VideoPlayer'; // ✅ Ensure correct path
import './movieDetailsModal.css';

function MovieDetailsModal({ movie, isOpen, onClose, updateMovie, autoPlay }) {
  const [relatedMovies, setRelatedMovies] = useState([]);
  const [isVideoOpen, setIsVideoOpen] = useState(autoPlay || false); // ✅ Start video if autoPlay is true

  const API_URL = process.env.REACT_APP_API_URL;

  useEffect(() => {
    const fetchRelatedMovies = async () => {
      if (!isOpen || !movie?._id) return;

      try {
        const response = await fetch(`${API_URL}/api/movies/${movie._id}/recommend`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            authorization: 'Bearer ' + localStorage.getItem('jwt'),
          },
        });

        if (!response.ok) {
          throw new Error(`Network response was not ok (status: ${response.status})`);
        }

        const data = await response.json();

        const moviesDetails = await Promise.all(
          data.recommendedMovies.map(async (id) => {
            const movieResponse = await fetch(`${API_URL}/api/movies/${id}`, {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json',
                authorization: 'Bearer ' + localStorage.getItem('jwt'),
              },
            });

            if (!movieResponse.ok) {
              throw new Error(`Failed to fetch movie with ID: ${id}`);
            }

            return movieResponse.json();
          })
        );

        setRelatedMovies(moviesDetails);
      } catch (error) {
        console.error('Error fetching related movies:', error);
      }
    };

    fetchRelatedMovies();
  }, [isOpen, movie]);

  const addToRecommendations = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) {
      console.error("No authentication token found.");
      return;
    }

    try {
      const response = await fetch(`${API_URL}:/api/movies/${movie._id}/recommend`, {
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

  const handleMovieClick = (newMovie) => {
    updateMovie(newMovie);
    setIsVideoOpen(false); // ✅ Stop video when switching movies
  };

  const handlePlayVideo = async () => {
    setIsVideoOpen(true);
    await addToRecommendations(); // ✅ Add movie to recommendations when played
  };
  const handleCloseVideo = (e) => {
    if (e && e.stopPropagation) {
      e.stopPropagation();
    }
    setIsVideoOpen(false);
  };

  useEffect(() => {
    setIsVideoOpen(autoPlay); // ✅ Ensure video starts if autoPlay is true
  }, [autoPlay]);

  if (!isOpen || !movie) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>✕</button>
        <div className="movie-banner" style={{ height: '420px' }}>
          <img 
            src={`${API_URL}/${movie.picture}`} 
            alt={movie.name} 
            className="movie-banner-image"
            style={{ height: '100%', objectFit: 'cover' }}
          />
        </div>

        <div className="movie-details-container">
          <div className="movie-details-header">
            <h1 className="movie-title">{movie.name}</h1>
            <div className="action-buttons">
              <button className="play-button" onClick={handlePlayVideo}>
                <span className="play-icon">▶</span> Play
              </button>
            </div>
          </div>

          <p className="movie-description">{movie.description}</p>
          <div className="movie-meta">
            <span><strong>Year:</strong> {new Date(movie.releaseDate).getFullYear()}</span>
            <span><strong>Duration:</strong> {movie.time} minutes</span>
            <span><strong>Age Rating:</strong> {movie.age}+</span>
            <span><strong>Language:</strong> {movie.properties?.language || 'Unknown'}</span>
          </div>

          <div className="related-movies">
            <h2>More Like This</h2>
            <div className="related-movie-list">
              {relatedMovies.length > 0 ? (
                relatedMovies.map((relatedMovie) => (
                  <div 
                    key={relatedMovie._id} 
                    className="related-movie-item" 
                    onClick={() => handleMovieClick(relatedMovie)} 
                    style={{ cursor: 'pointer' }} 
                  >
                    <img
                      src={`${API_URL}/${relatedMovie.picture}`}
                      alt={relatedMovie.name}
                      className="related-movie-poster"
                    />
                    <p className="related-movie-title">{relatedMovie.name}</p>
                  </div>
                ))
              ) : (
                <p className="no-related-movies">No related movies found.</p>
              )}
            </div>
          </div>
        </div>
      </div>

      {isVideoOpen && (
        <div className="video-overlay" onClick={(e) => e.stopPropagation()}>
          <VideoPlayer
            videoUrl={`${API_URL}/${movie.video}`}
            videoName={movie.name}
            play={true}
            startFullscreen={true}
            onClose={handleCloseVideo}
          />
        </div>
      )}
    </div>
  );
}

export default MovieDetailsModal;
