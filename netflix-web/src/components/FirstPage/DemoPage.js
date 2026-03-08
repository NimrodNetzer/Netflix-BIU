import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DemoPage.css';

function CredentialCard({ title, email, password, badge, badgeColor }) {
  const [copiedEmail, setCopiedEmail] = useState(false);
  const [copiedPassword, setCopiedPassword] = useState(false);

  const copy = (text, setFlag) => {
    navigator.clipboard.writeText(text);
    setFlag(true);
    setTimeout(() => setFlag(false), 1500);
  };

  return (
    <div className="demo-card">
      <div className="demo-card-header">
        <span className="demo-card-title">{title}</span>
        <span className="demo-badge" style={{ backgroundColor: badgeColor }}>{badge}</span>
      </div>
      <div className="demo-field">
        <span className="demo-label">Email</span>
        <div className="demo-value-row">
          <code>{email}</code>
          <button className="copy-btn" onClick={() => copy(email, setCopiedEmail)}>
            {copiedEmail ? '✓ Copied' : 'Copy'}
          </button>
        </div>
      </div>
      <div className="demo-field">
        <span className="demo-label">Password</span>
        <div className="demo-value-row">
          <code>{password}</code>
          <button className="copy-btn" onClick={() => copy(password, setCopiedPassword)}>
            {copiedPassword ? '✓ Copied' : 'Copy'}
          </button>
        </div>
      </div>
    </div>
  );
}

function DemoPage() {
  const navigate = useNavigate();

  return (
    <div className="demo-page">
      <div className="demo-hero-bar">
        <span className="demo-logo" onClick={() => navigate('/')}>NETFLIX-BIU</span>
        <button className="demo-launch-btn" onClick={() => navigate('/login')}>Launch App →</button>
      </div>

      <div className="demo-content">
        <h1 className="demo-title">Netflix-BIU</h1>
        <p className="demo-subtitle">
          A full-stack Netflix clone built as part of the Advanced Programming course at Bar-Ilan University.
          Stream movies, manage categories, and get personalised recommendations.
        </p>

        {/* Tech Stack */}
        <div className="demo-section">
          <h2>Tech Stack</h2>
          <div className="demo-tags">
            {['React', 'Node.js', 'Express', 'MongoDB Atlas', 'Cloudinary', 'JWT Auth', 'Vercel'].map(t => (
              <span key={t} className="demo-tag">{t}</span>
            ))}
          </div>
        </div>

        {/* Features */}
        <div className="demo-section">
          <h2>Features</h2>
          <ul className="demo-features">
            <li>Browse movies organised by promoted categories</li>
            <li>Stream video content directly in the browser</li>
            <li>Personalised recommendations based on watch history</li>
            <li>Admin dashboard — create, edit, and delete movies &amp; categories</li>
            <li>JWT-based authentication with role-based access control</li>
            <li>Dark / light mode toggle</li>
          </ul>
        </div>

        {/* Demo Credentials */}
        <div className="demo-section">
          <h2>Try it yourself</h2>
          <p className="demo-hint">Use one of the accounts below to log in:</p>
          <div className="demo-cards">
            <CredentialCard
              title="Admin Account"
              email="admin@example.com"
              password="admin123"
              badge="Admin"
              badgeColor="#e50914"
            />
            <CredentialCard
              title="Regular User"
              email="nimrod@gmail.com"
              password="123123aa"
              badge="User"
              badgeColor="#555"
            />
          </div>
        </div>

        <button className="demo-big-launch-btn" onClick={() => navigate('/login')}>
          Launch the App
        </button>
      </div>
    </div>
  );
}

export default DemoPage;
