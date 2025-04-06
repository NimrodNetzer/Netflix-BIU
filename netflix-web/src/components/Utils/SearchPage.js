import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import MovieBox from '../Home/movieBox';
import './SearchPage.css';
import { jwtDecode } from 'jwt-decode';

const API_URL = process.env.REACT_APP_API_URL;

function SearchPage() {
  const { query } = useParams();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState(query || '');
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);

  // ✅ Decode token to check if user is admin
  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        setIsAdmin(decodedToken.admin === true);
      } catch (error) {
        console.error("Error decoding token:", error);
      }
    }
  }, []);

  // ✅ Update search term from URL and reset movies when query changes
  useEffect(() => {
    if (query !== searchTerm) {
      setSearchTerm(query || '');
      setMovies([]); // Clear results on new search
    }
  }, [query]);

  // ✅ Redirect safely only when user **stops typing**
  useEffect(() => {
    if (searchTerm.trim() === '') {
      const delayRedirect = setTimeout(() => {
        navigate('/home', { replace: true });
      }, 500); // Delay navigation to prevent glitches

      return () => clearTimeout(delayRedirect);
    }
  }, [searchTerm, navigate]);

  // ✅ Fetch movies when user **stops typing**
  useEffect(() => {
    if (!searchTerm.trim()) return; // Prevent empty searches

    const fetchMovies = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`${API_URL}/api/movies/search/${searchTerm}`, {
          headers: { authorization: 'Bearer ' + localStorage.getItem('jwt') },
        });
        if (!response.ok) throw new Error('Failed to fetch search results');
        const data = await response.json();
        setMovies(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    const delaySearch = setTimeout(fetchMovies, 300); // ✅ Wait before calling API

    return () => clearTimeout(delaySearch);
  }, [searchTerm]);

  return (
    <div className="search-page">
      <h1 className="search-title">Search Results for "{searchTerm}"</h1>
      {loading && <p className="loading-message">Loading...</p>}
      {error && <p className="error-message">{error}</p>}
      {!loading && !error && movies.length === 0 && <p className="no-results-message">No movies found.</p>}
      <div className="search-results-container">
        {movies.map((movie) => (
          <MovieBox key={movie._id} movie={movie} isAdmin={isAdmin} />
        ))}
      </div>
    </div>
  );
}

export default SearchPage;
