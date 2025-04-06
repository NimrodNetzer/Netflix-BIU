import React, { useState, useEffect } from 'react';
import CategoriesList from './CategoriesList';
import './categoriesAdminPage.css';
const API_URL = process.env.REACT_APP_API_URL;

function CategoriesAdminPage({ refreshTrigger }) { // ✅ Receive refresh trigger prop
  const [editCategory, setEditCategory] = useState(null);
  const [editCategoryName, setEditCategoryName] = useState('');
  const [editCategoryPromoted, setEditCategoryPromoted] = useState(false);
  const [refresh, setRefresh] = useState(false); // ✅ Local refresh trigger

  useEffect(() => {
    setRefresh(prev => !prev); // ✅ When refreshTrigger changes, force re-render
  }, [refreshTrigger]);

  const handleEditCategory = (category) => {
    setEditCategory(category);
    setEditCategoryName(category.name);
    setEditCategoryPromoted(category.promoted);
  };

  const handleUpdateCategory = async () => {
    if (!editCategoryName.trim()) return;
  
    try {
      await fetch(`${API_URL}/api/categories/${editCategory._id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          authorization: 'Bearer ' + localStorage.getItem('jwt'),
        },
        body: JSON.stringify({
          name: editCategoryName,
          promoted: editCategoryPromoted,
        }),
      });
  
      setEditCategory(null);
      window.location.reload(); // 🔥 Refresh the page after editing
    } catch (error) {
      console.error('Error updating category:', error);
    }
  };
  
  return (
    <div className="admin-page">
      <h1>Categories Management</h1>
      {/* ✅ Pass refresh state to CategoriesList */}
      <CategoriesList key={refresh} onEditCategory={handleEditCategory} />

      {editCategory && (
        <div className="edit-category-form">
          <h2>Edit Category</h2>
          <input type="text" value={editCategoryName} onChange={(e) => setEditCategoryName(e.target.value)} />
          <button onClick={handleUpdateCategory}>Update</button>
        </div>
      )}
    </div>
  );
}

export default CategoriesAdminPage;
