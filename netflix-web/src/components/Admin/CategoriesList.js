import React, { useEffect, useState } from 'react';
import CategoryBox from './categoryBox';
import './categoriesList.css';
const API_URL = process.env.REACT_APP_API_URL;

function CategoriesList() {
  const [categories, setCategories] = useState([]);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await fetch(`${API_URL}/api/categories`, {
        headers: { authorization: 'Bearer ' + localStorage.getItem('jwt') },
      });

      if (!response.ok) throw new Error('Failed to fetch categories');
      const data = await response.json();
      setCategories(data);
    } catch (error) {
      console.error('Error fetching categories:', error);
      setMessage({ type: 'error', text: 'Failed to load categories.' });
    }
  };

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) return;

    try {
      const response = await fetch(`${API_URL}/api/categories/${id}`, {
        method: 'DELETE',
        headers: { authorization: 'Bearer ' + localStorage.getItem('jwt') },
      });

      if (response.status === 204 || response.ok) {
        setMessage({ type: 'success', text: 'Category deleted successfully!' });
        fetchCategories();
        setTimeout(() => setMessage({ type: '', text: '' }), 5000);
      } else {
        const data = await response.json();
        throw new Error(data.error || 'Failed to delete category');
      }
    } catch (error) {
      console.error('Error deleting category:', error);
      setMessage({ type: 'error', text: error.message });
      setTimeout(() => setMessage({ type: '', text: '' }), 5000);
    }
  };

  // ✅ New function: Refresh categories after edit
  const handleEditCategory = () => {
    fetchCategories(); // Refresh the list after an edit
  };

  return (
    <div className="categories-container">
      {message.type === 'error' && <p className="delete-error-message">{message.text}</p>}

      <div className="categories-list">
        {categories.map((category) => (
          <CategoryBox 
            key={category._id} 
            category={category} 
            onDelete={handleDeleteCategory} 
            onEditCategory={handleEditCategory} // ✅ Pass edit function
          />
        ))}
      </div>
    </div>
  );
}

export default CategoriesList;
