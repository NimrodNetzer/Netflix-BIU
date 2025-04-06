import React, { useRef, useState, useEffect } from 'react';
import MovieBox from './movieBox';
import './categoryRow.css';

function CategoryRow({ category, movies, isAdmin = false }) {
  const rowRef = useRef(null);
  const [moviesPerView, setMoviesPerView] = useState(6);
  const scrollStep = 3;

  useEffect(() => {
    const updateMoviesPerView = () => {
      if (rowRef.current) {
        const containerWidth = rowRef.current.offsetWidth;
        const movieBoxWidth = 300;
        const calculatedMoviesPerView = Math.max(1, Math.floor(containerWidth / movieBoxWidth));
        setMoviesPerView(calculatedMoviesPerView);
      }
    };

    updateMoviesPerView();
    window.addEventListener('resize', updateMoviesPerView);
    return () => window.removeEventListener('resize', updateMoviesPerView);
  }, []);

  if (!movies || movies.length === 0) {
    return null;
  }

  const handleScroll = (direction) => {
    if (!rowRef.current || movies.length <= moviesPerView) return;

    const container = rowRef.current;
    const scrollAmount = (container.offsetWidth / moviesPerView) * scrollStep;
    const maxScrollLeft = container.scrollWidth - container.clientWidth;

    let targetScroll = container.scrollLeft + direction * scrollAmount;

    // Wrap around for infinite effect without duplication
    if (targetScroll < 0) {
      targetScroll = maxScrollLeft;
    } else if (targetScroll > maxScrollLeft) {
      targetScroll = 0;
    }

    container.scrollTo({ left: targetScroll, behavior: 'smooth' });
  };

  return (
    <div className="category-row">
      <h2 className="category-title">{category}</h2>
      <div className="row-container">
        {movies.length > moviesPerView && (
          <button className="scroll-button left" onClick={() => handleScroll(-1)}>
            &#9664;
          </button>
        )}
        <div className="movie-list" ref={rowRef}>
          {movies.map((movie, index) => (
            <MovieBox key={index} movie={movie} isAdmin={isAdmin} />
          ))}
        </div>
        {movies.length > moviesPerView && (
          <button className="scroll-button right" onClick={() => handleScroll(1)}>
            &#9654;
          </button>
        )}
      </div>
    </div>
  );
}

export default CategoryRow;
