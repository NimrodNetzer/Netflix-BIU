import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './HelperTopMenu.css';

function HelperTopMenu() {
  const [isMenuVisible, setIsMenuVisible] = useState(false);
  const menuRef = useRef(null);
  const hoverTimeoutRef = useRef(null);
  const navigate = useNavigate();

  // Check if the mouse is near the menu
  const isMouseNearMenu = (event) => {
    const buffer = 50; // Buffer zone in pixels
    if (!menuRef.current) return false;

    const rect = menuRef.current.getBoundingClientRect();
    const mouseX = event.clientX;
    const mouseY = event.clientY;

    // Check if the mouse is within the buffer zone
    return (
      mouseX >= rect.left - 5*buffer &&
      mouseX <= rect.right + 5*buffer &&
      mouseY <= rect.top + 5*buffer &&
      mouseY >= rect.bottom - 5*buffer
    );
  };

  const handleMouseEnterMenu = () => {
    if (hoverTimeoutRef.current) {
      clearTimeout(hoverTimeoutRef.current); // Cancel any close timeout
    }
    setIsMenuVisible(true); // Keep the menu open
  };

  const handleMouseLeaveMenu = (event) => {
    // If the mouse is near the menu or profile button, don't close it
    if (isMouseNearMenu(event)) return;

    // Otherwise, close the menu after a short delay
    hoverTimeoutRef.current = setTimeout(() => {
      setIsMenuVisible(false);
    }, 300);
  };

  const handleLogout = () => {
    // Clear session or authentication data
    localStorage.removeItem('authToken');
    sessionStorage.clear();
    navigate('/'); // Redirect to home page
  };

  return (
    <div
      className="helper-menu-container"
      onMouseEnter={handleMouseEnterMenu}
      onMouseLeave={handleMouseLeaveMenu}
      ref={menuRef}
    >
      <div className="profile-button">
        <img
          src="/assets/p.png" // Path to your profile icon
          alt="Profile"
          className="profile-icon"
        />
        <i className={`arrow-icon ${isMenuVisible ? 'open' : ''}`}></i>
      </div>

      {isMenuVisible && (
        <div
          className="helper-dropdown"
          onMouseEnter={handleMouseEnterMenu}
          onMouseLeave={handleMouseLeaveMenu}
        >
          <div className="menu-item" onClick={() => navigate('/')}>
            <img src="../assets/homePage.png" alt="Home Page" />
            <span>Home Page</span>
          </div>
          <div className="menu-item avatar-menu">
            <img src="../assets/p.png" alt="Avatar Menu" />
            <span>Avatar Menu</span>
          </div>
          <div className="menu-item">
            <img src="../assets/account.jpg" alt="Account" />
            <span>Account</span>
          </div>
          <div className="menu-item">
            <img src="/assets/help-icon.png" alt="Help Center" />
            <span>Help Center</span>
          </div>
          <div className="menu-item sign-out" onClick={handleLogout}>
            <span>Sign out of Netflix</span>
          </div>
        </div>
      )}
    </div>
  );
}

export default HelperTopMenu;
