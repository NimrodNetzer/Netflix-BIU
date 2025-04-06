import React, { useState, useEffect } from 'react';
import './CreateCategoryForm.css'; // Ensure correct CSS import
const API_URL = process.env.REACT_APP_API_URL;

const CreateCategoryForm = ({ onSubmit, onCancel }) => {
  const [categoryName, setCategoryName] = useState('');
  const [isPromoted, setIsPromoted] = useState(false);
  const [loading, setLoading] = useState(false); // Track loading state
  const [successMessage, setSuccessMessage] = useState(''); // Track success message
  const API_URL = process.env.REACT_APP_API_URL;

  // Prevent page scroll when modal is open
  useEffect(() => {
    document.body.style.overflow = 'hidden'; // Disable scroll
    return () => {
      document.body.style.overflow = 'auto'; // Re-enable scroll when modal closes
    };
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const categoryData = {
      name: categoryName,
      promoted: isPromoted,
    };

    setLoading(true); // Show loading state
    setSuccessMessage(''); // Clear previous success message

    try {
      const response = await fetch(`${API_URL}/api/categories`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          authorization: 'Bearer ' + localStorage.getItem('jwt'), // Include JWT if needed
        },
        body: JSON.stringify(categoryData),
      });

      if (response.status === 201) {
        setSuccessMessage('Category Added Successfully'); // Set success message
        setCategoryName(''); // Clear form
        setIsPromoted(false);
        setTimeout(() => {
          onSubmit(); // Close modal after 2 seconds
        }, 2000); // Delay to show success message briefly
      } else {
        console.error('Failed to create category. Status:', response.status);
        alert('Error: Unable to create category');
      }
    } catch (error) {
      console.error('Error creating category:', error);
      alert('Failed to create category. Please try again.');
    } finally {
      setLoading(false); // Remove loading state
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="close-button" onClick={onCancel}>
          ✕
        </button>
        <h2>Create Category</h2>

        <form onSubmit={handleSubmit} className="category-form">
          {/* Category Name Input */}
          <label className="form-label">Category Name:</label>
          <input
            type="text"
            className="form-input"
            placeholder="Enter category name"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            required
          />

          {/* Promoted Toggle with Label */}
          <div className="toggle-section">
            <label className="form-label promoted-label">Promoted:</label>
            <div className="toggle-switch">
              <input
                type="checkbox"
                id="promoted-toggle"
                checked={isPromoted}
                onChange={() => setIsPromoted(!isPromoted)}
              />
              <label htmlFor="promoted-toggle" className="toggle-slider"></label>
            </div>
          </div>

          {/* Success Message */}
          {successMessage && <div className="success-message">{successMessage}</div>}

          {/* Submit Button */}
          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? 'Creating...' : 'Create'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateCategoryForm;