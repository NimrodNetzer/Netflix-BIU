import './Signup.css'; 
import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import profile1 from '../../assets/profile1.webp'; 
import profile2 from '../../assets/profile2.webp';
import profile3 from '../../assets/profile3.webp';

// Avatar mapping for easy reference
const avatarMap = {
    profile1: profile1,
    profile2: profile2,
    profile3: profile3
};

const API_URL = process.env.REACT_APP_API_URL;

const Signup = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [picture, setSelectedPicture] = useState('profile2'); // Default profile picture
    const [email, setEmail] = useState(location.state?.email || '');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');

    const handleSignup = async (event) => {
        event.preventDefault();
        setIsLoading(true);

        try {
            const userAvatar = avatarMap[picture]; // Get full image URL

            console.log("Saving avatar:", userAvatar); // Debugging

            const response = await fetch(`${API_URL}/api/users`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password, nickname, picture: userAvatar }),
            });

            if (response.ok) {
                setErrorMessage('');
                setSuccessMessage('Signup successful! Redirecting to login...');

                // Save user info in localStorage
                localStorage.setItem('user', JSON.stringify({ email, nickname, picture: userAvatar }));

                setTimeout(() => { navigate('/Login'); }, 1500);
            } else {
                const data = await response.json();
                setErrorMessage(data.errors || 'Signup failed.');
            }
        } catch (error) {
            setErrorMessage('An error occurred during signup.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="signup">
            <div className="signup-container">
                <h2>Sign Up</h2>
                <form onSubmit={handleSignup}>
                    <div className="form-group">
                        <label>Email:</label>
                        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>

                    <div className="form-group">
                        <label>Password:</label>
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                    </div>

                    <div className="form-group">
                        <label>Nickname:</label>
                        <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
                    </div>

                    <div className="form-group">
                        <label>Choose a Profile Picture:</label>
                        <div className="profile-picture-options">
                            {Object.keys(avatarMap).map((key) => (
                                <img
                                    key={key}
                                    src={avatarMap[key]}
                                    alt={`Profile ${key}`}
                                    className={`profile-thumbnail ${picture === key ? 'selected' : ''}`}
                                    onClick={() => setSelectedPicture(key)}
                                />
                            ))}
                        </div>
                    </div>

                    {successMessage && <div className="success-message">{successMessage}</div>}
                    {errorMessage && <p className="error-message">{errorMessage}</p>}

                    <button type="submit" disabled={isLoading}>{isLoading ? 'Signing up...' : 'Signup'}</button>

                    <div className="additional-options">
                        <a onClick={() => navigate('/login')}>Already have an account? Sign in</a>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Signup;
