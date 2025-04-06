import React, { useState, useEffect } from 'react';
import './CreateMovieForm.css';
const API_URL = process.env.REACT_APP_API_URL;

const CreateMovieForm = ({ onSubmit, onCancel, movieData }) => {
  const isUpdate = Boolean(movieData);

  // Initialize fields from movieData if available (update mode), otherwise empty for creation
  const [name, setName] = useState(movieData?.name || '');
  const [description, setDescription] = useState(movieData?.description || '');
  const [age, setAge] = useState(movieData?.age?.toString() || '');
  // For duration, you may parse movieData.time (e.g., "2h 15m") if available
  const [hours, setHours] = useState('');
  const [minutes, setMinutes] = useState('');
  const [releaseDate, setReleaseDate] = useState(movieData?.releaseDate || '');
  const [quality, setQuality] = useState(movieData?.quality || '');
  // Additional properties
  const [genre, setGenre] = useState(movieData?.properties?.genre || '');
  const [language, setLanguage] = useState(movieData?.properties?.language || '');
  const [selectedCategories, setSelectedCategories] = useState(movieData?.categories || []);
  const [categories, setCategories] = useState([]);
  const [cast, setCast] = useState(movieData?.cast || [{ name: '', role: '' }]);
  const [picture, setPicture] = useState(null);
  const [video, setVideo] = useState(null);
  const [author, setAuthor] = useState(movieData?.author || '');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const API_URL = process.env.REACT_APP_API_URL;

  // Fetch available categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const token = localStorage.getItem('jwt');
        const response = await fetch(`${API_URL}/api/categories`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
        });
        if (!response.ok) {
          throw new Error(`Failed to fetch categories (status: ${response.status})`);
        }
        const data = await response.json();
        setCategories(data);
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };
    fetchCategories();
  }, []);

  // Prevent scrolling when modal is open
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = 'auto'; };
  }, []);

  // Helper for duration input
  const handleHoursChange = (e) => {
    const value = e.target.value.replace(/\D/, '');
    setHours(value ? Math.min(parseInt(value, 10), 3) : '');
  };

  const handleMinutesChange = (e) => {
    const value = e.target.value.replace(/\D/, '');
    setMinutes(value ? Math.min(parseInt(value, 10), 59) : '');
  };

  // Handle cast input changes
  const handleCastChange = (index, field, value) => {
    const updatedCast = [...cast];
    updatedCast[index][field] = value;
    setCast(updatedCast);
  };

  const addCastMember = () => {
    setCast([...cast, { name: '', role: '' }]);
  };

  const removeCastMember = (index) => {
    setCast(cast.filter((_, i) => i !== index));
  };

  // Handle category checkbox changes
  const handleCategorySelection = (id) => {
    if (selectedCategories.includes(id)) {
      setSelectedCategories(selectedCategories.filter(catId => catId !== id));
    } else {
      setSelectedCategories([...selectedCategories, id]);
    }
  };

  // On form submission, send a PUT request in update mode or a POST request in create mode
  const handleSubmit = async (e) => {
    e.preventDefault();

    const ageValue = parseInt(age, 10);
    if (isNaN(ageValue) || ageValue < 0 || ageValue > 18) {
      alert("Invalid age! Age must be between 0 and 18.");
      return;
    }
    const formattedTime = `${hours || 0}h ${minutes || 0}m`;

    // Validate cast
    if (!Array.isArray(cast) || cast.length === 0) {
      alert("Invalid cast! At least one cast member is required.");
      return;
    }
    for (let i = 0; i < cast.length; i++) {
      if (!cast[i].name.trim()) {
        alert(`Invalid cast member at index ${i}: Name is required.`);
        return;
      }
    }

    const moviePayload = {
      name,
      description,
      age: ageValue,
      time: formattedTime,
      releaseDate,
      quality,
      categories: selectedCategories,
      cast,
      properties: { genre, language },
      author,
    };

    const formData = new FormData();
    formData.append("data", JSON.stringify(moviePayload));
    if (picture) formData.append("image", picture);
    if (video) formData.append("video", video);

    setLoading(true);
    setSuccessMessage('');

    try {
      const token = localStorage.getItem("jwt");
      let response;
      if (isUpdate) {
        // Send PUT request to update the movie
        response = await fetch(`${API_URL}/api/movies/${movieData._id}`, {
          method: "PUT",
          headers: {
            'Authorization': `Bearer ${token}`,
          },
          body: formData,
        });
      } else {
        // Send POST request to create a new movie
        response = await fetch(`${API_URL}/api/movies`, {
          method: "POST",
          headers: {
            'Authorization': `Bearer ${token}`,
          },
          body: formData,
        });
      }

      if (response.ok) {
        setSuccessMessage(isUpdate ? "Movie Updated Successfully!" : "Movie Added Successfully!");
        setTimeout(() => {
          onSubmit(); // Callback after successful update/creation
        }, 2000);
      } else {
        const errorResponse = await response.json();
        console.error("Error:", errorResponse);
        alert(`Failed to ${isUpdate ? 'update' : 'create'} movie: ${errorResponse.message}`);
      }
    } catch (error) {
      console.error("Error:", error);
      alert(`Failed to ${isUpdate ? 'update' : 'create'} movie. Please try again.`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="createmovie-modal-overlay">
      <div className="createmovie-modal-content">
        <button className="createmovie-close-button" onClick={onCancel}>✕</button>
        <h2>{isUpdate ? "Update Movie" : "Create Movie"}</h2>
        <form onSubmit={handleSubmit} className="createmovie-form">
          {/* Movie Name, Age Rating & Author - Same Row */}
          <div className="createmovie-row-group createmovie-row-group-wider">
            <div className="createmovie-form-group createmovie-wide">
              <label className="createmovie-form-label">Movie Name:</label>
              <input
                type="text"
                className="createmovie-form-input"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="createmovie-form-group">
              <label className="createmovie-form-label">Age Rating:</label>
              <input
                type="number"
                className="createmovie-form-input"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                required
              />
            </div>
            <div className="createmovie-form-group createmovie-wide">
              <label className="createmovie-form-label">Author:</label>
              <input
                type="text"
                className="createmovie-form-input"
                value={author}
                onChange={(e) => setAuthor(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Description - Full Row */}
          <div className="createmovie-form-group createmovie-full-width">
            <label className="createmovie-form-label">Description:</label>
            <textarea
              className="createmovie-form-input"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </div>

          {/* Duration & Release Date - Same Row */}
          <div className="createmovie-row-group">
            <div className="createmovie-form-group">
              <label className="createmovie-form-label">Duration:</label>
              <div className="createmovie-time-input-container">
                <input
                  type="text"
                  className="createmovie-time-input"
                  placeholder="Hours"
                  value={hours}
                  onChange={handleHoursChange}
                  maxLength="1"
                />
                <span>h</span>
                <input
                  type="text"
                  className="createmovie-time-input"
                  placeholder="Minutes"
                  value={minutes}
                  onChange={handleMinutesChange}
                  maxLength="2"
                />
                <span>m</span>
              </div>
            </div>
            <div className="createmovie-form-group">
              <label className="createmovie-form-label">Release Date:</label>
              <input
                type="date"
                className="createmovie-form-input"
                value={releaseDate}
                onChange={(e) => setReleaseDate(e.target.value)}
                required
              />
            </div>
            <div className="createmovie-form-group">
              <label className="createmovie-form-label">Quality:</label>
              <select
                className="createmovie-form-input"
                value={quality}
                onChange={(e) => setQuality(e.target.value)}
                required
              >
                <option value="">Select Quality</option>
                <option value="HD">HD</option>
                <option value="SD">SD</option>
                <option value="4K">4K</option>
              </select>
            </div>
            <div className="createmovie-form-group">
              <label className="createmovie-form-label">Language:</label>
              <select
                className="createmovie-form-input"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                required
              >
                <option value="">Select Language</option>
                <option value="English">English</option>
                <option value="Spanish">Spanish</option>
                <option value="French">French</option>
                <option value="Hebrew">Hebrew</option>
              </select>
            </div>
         
          </div>

          {/* Categories - Full Row */}
          <div className="createmovie-form-group createmovie-full-width">
            <label className="createmovie-form-label">Categories:</label>
            <div className="createmovie-scroll-wrapper">
              <div className="createmovie-categories-container1">
                {categories.map(category => (
                  <label key={category._id}>
                    <input
                      type="checkbox"
                      checked={selectedCategories.includes(category._id)}
                      onChange={() => handleCategorySelection(category._id)}
                    />
                    {category.name}
                  </label>
                ))}
              </div>
            </div>
          </div>

          {/* Cast Section - Full Row */}
          <div className="createmovie-form-group createmovie-full-width">
            <label className="createmovie-form-label">Cast:</label>
            {cast.map((member, index) => (
              <div key={index} className="createmovie-cast-member">
                <input
                  type="text"
                  className="createmovie-form-input"
                  placeholder="Actor Name"
                  value={member.name}
                  onChange={(e) => handleCastChange(index, 'name', e.target.value)}
                  required
                />
                <input
                  type="text"
                  className="createmovie-form-input"
                  placeholder="Role"
                  value={member.role}
                  onChange={(e) => handleCastChange(index, 'role', e.target.value)}
                  required
                />
                <button
                  type="button"
                  className="createmovie-remove-cast-button"
                  onClick={() => removeCastMember(index)}
                >
                  🗑
                </button>
              </div>
            ))}
            <button type="button" className="createmovie-add-cast-button" onClick={addCastMember}>
              + Add Cast Member
            </button>
          </div>

          {/* Picture Upload - Full Row */}
          <div className="createmovie-form-group createmovie-full-width">
            <label className="createmovie-form-label">Picture:</label>
            <input
              type="file"
              className="createmovie-form-input"
              accept="image/*"
              onChange={(e) => setPicture(e.target.files[0])}
            />
          </div>

          {/* Video Upload - Full Row */}
          <div className="createmovie-form-group createmovie-full-width">
            <label className="createmovie-form-label">Video:</label>
            <input
              type="file"
              className="createmovie-form-input"
              accept="video/*"
              onChange={(e) => setVideo(e.target.files[0])}
            />
          </div>

          {/* Success Message */}
          {successMessage && (
            <div className="createmovie-success-message createmovie-full-width">
              {successMessage}
            </div>
          )}

          {/* Submit Button */}
          <button type="submit" className="createmovie-submit-button">
            {loading ? (isUpdate ? 'Updating...' : 'Creating...') : (isUpdate ? 'Update' : 'Create')}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateMovieForm;
