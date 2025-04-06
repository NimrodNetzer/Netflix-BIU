import React, { useState } from 'react';
import './Hero.css';
import { useNavigate } from 'react-router-dom';

function Hero() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');

    const handleGetStarted = (e) => {
        e.preventDefault();
        navigate('/signup', { state: { email } });
    };

    return (
        <div className="hero">
            {/* Remove the logo */}
            {/* <img src={logo} alt="Logo" className="hero-logo" /> */}

            {/* Sign In button */}
            <button onClick={() => navigate('/login')} className="sign-in-button">
                Sign In
            </button>

            <div className="hero-content">
                <h1 className="hero-title">
                    Unlimited movies, <br /> TV shows, and more
                </h1>
                <h2>Watch anywhere. Cancel anytime.</h2>
                <p>Ready to watch? Enter your email to create or restart your membership.</p>
                <form onSubmit={handleGetStarted} className="input-container">
                    <input
                        type="email"
                        id="email"
                        placeholder="Email address"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$"
                    />
                    <button type="submit">
                        Get Started &gt;
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Hero;