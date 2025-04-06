import React, { useState } from "react";
import MoviesAdminPage from "./moviesAdminPage";
import CategoriesAdminPage from "./categoriesAdminPage";
import CreateCategoryForm from "./CreateCategoryForm";
import CreateMovieForm from "./CreateMovieForm"; 
import "./Admin.css";

function Admin() {
  const [isMoviesView, setIsMoviesView] = useState(true);
  const [isCategoryModalOpen, setCategoryModalOpen] = useState(false);
  const [refreshCategories, setRefreshCategories] = useState(false); // ✅ New state to trigger refresh

  const handleToggle = () => {
    setIsMoviesView(!isMoviesView);
  };

  const [isMovieModalOpen, setMovieModalOpen] = useState(false);

  const handleAddCategoryClick = () => setCategoryModalOpen(true);
  const closeCategoryModal = () => setCategoryModalOpen(false);

  const handleAddMovieClick = () => setMovieModalOpen(true);
  const closeMovieModal = () => setMovieModalOpen(false);

  // ✅ Function to trigger category refresh
  const triggerCategoryRefresh = () => {
    setRefreshCategories(prev => !prev);
  };

  return (
    <div className="admin-container">
      <div className="toggle-switch-container">
        <span className="toggle-label">Movies</span>
        <label className="toggle-switch">
          <input type="checkbox" checked={!isMoviesView} onChange={handleToggle} />
          <span className="toggle-slider"></span>
        </label>
        <span className="toggle-label">Categories</span>
      </div>

      <div className="admin-actions">
        <button className="add-button" onClick={handleAddMovieClick}>
          Add Movie
        </button>
        <button className="add-button" onClick={handleAddCategoryClick}>
          Add Category
        </button>
      </div>

      <div className="admin-content">
        {isMoviesView ? <MoviesAdminPage /> : <CategoriesAdminPage refreshTrigger={refreshCategories} />}
      </div>

      {isCategoryModalOpen && (
        <div className="modal-overlay" onClick={closeCategoryModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="close-button" onClick={closeCategoryModal}>✕</button>
            {/* ✅ Pass the trigger function to refresh after adding a category */}
            <CreateCategoryForm onSubmit={() => { closeCategoryModal(); triggerCategoryRefresh(); }} onCancel={closeCategoryModal} />
          </div>
        </div>
      )}

      {isMovieModalOpen && (
        <div className="modal-overlay" onClick={closeMovieModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="close-button" onClick={closeMovieModal}>✕</button>
            <CreateMovieForm onSubmit={closeMovieModal} onCancel={closeMovieModal} />
          </div>
        </div>
      )}
    </div>
  );
}

export default Admin;
