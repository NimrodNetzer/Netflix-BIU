import './Login.css'; // Import the CSS file for styling
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const API_URL = process.env.REACT_APP_API_URL;

const Login = () => {
  const navigate = useNavigate(); // Hook for navigation
  const [email, setEmail] = useState(''); // State for username
  const [password, setPassword] = useState(''); // State for password
  const [errorMessage, setErrorMessage] = useState(''); // State for error messages
  const [isLoading, setIsLoading] = useState(false); // State for loading indicator

  const handleLogin = async (event) => {
    event.preventDefault(); // Prevent page refresh
    setIsLoading(true); // Start loading

    try {
      const response = await fetch(`${API_URL}/api/tokens`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem('jwt', data.token); // Save JWT token locally
        navigate('/home'); // Navigate to the home page
      } else {
        setErrorMessage('Invalid username or password');
      }
    } catch (error) {
      setErrorMessage('An error occurred. Please try again.');
    } finally {
      setIsLoading(false); // Stop loading
    }
  };

  return (
    <div className="login">
      <div className="login-container">
        <h2>Sign in</h2>
        <form onSubmit={handleLogin}>
          {/* Username field */}
          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
              type="text"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required // Make the field required
              pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$" // Ensure valid email format
              title="Password must be at least 8 characters long, contain at least one letter and one number, and use only English letters and numbers."
            />
          </div>

          {/* Password field */}
          <div className="form-group">
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required // Required field
              pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"
              title="Password must be at least 8 characters long, contain at least one letter and one number, and use only English letters and numbers."
            />
          </div>

          {/* Error message */}
          {errorMessage && <p className="error-message">{errorMessage}</p>}

          {/* Login button */}
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Loading...' : 'Sign In'}
          </button>

          <div className="additional-options">
            <a onClick={() => navigate('/')}>New to Netflix? Sign up now</a>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
