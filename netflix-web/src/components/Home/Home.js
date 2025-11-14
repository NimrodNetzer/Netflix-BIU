import React, { useState, useEffect } from 'react';
import CategoryRow from './categoryRow';
import './Home.css';
import TopMenu from '../Utils/TopMenu';
import FeaturedVideo from './FeaturedVideo'; // Import the new component
const API_URL = process.env.REACT_APP_API_URL;
function Home({ isAdmin = false }) {  // Accept isAdmin prop, default to false
  const [categories, setCategories] = useState([]);
  const [featuredMovie, setFeaturedMovie] = useState(null);
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const response = await fetch(`${API_URL}/api/movies/random?type=movie`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'authorization': 'Bearer ' + localStorage.getItem('jwt')
          },
        });

        if (!response.ok) {
          throw new Error(`Network response was not ok (status: ${response.status})`);
        }

        const data = await response.json();
        setCategories(data.movies);

        if (!isAdmin && data.movies.length > 0 && data.movies[0].movies.length > 0) {
          setFeaturedMovie(data.movies[0].movies[0]); // Only set featured movie when not admin
        }
      } catch (error) {
        console.error('Error fetching movies:', error);
      }
    };

    fetchMovies();
  }, [isAdmin]);

  return (
    <div className="home">
      {/* Hide Featured Video for Admin */}
      {!isAdmin && featuredMovie && <FeaturedVideo movie={featuredMovie} />}

      {/* Pass isAdmin to CategoryRow */}
      {categories.map((category) => (
        <CategoryRow
          key={category.category_id}
          category={category.category}
          movies={category.movies}
          isAdmin={isAdmin} // ✅ Pass isAdmin prop
        />
      ))}
    </div>
  );
}


export default Home;
