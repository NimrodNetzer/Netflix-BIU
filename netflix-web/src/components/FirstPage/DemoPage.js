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

  const webTech = ['React', 'Node.js', 'Express', 'MongoDB Atlas', 'Cloudinary', 'JWT Auth', 'Vercel'];
  const androidTech = ['Java / XML', 'MVVM Architecture', 'Room DB', 'LiveData', 'Retrofit'];

  const webFeatures = [
    'Browse movies organised by promoted categories',
    'Stream video content directly in the browser',
    'Personalised recommendations based on watch history',
    'Admin dashboard — create, edit, and delete movies & categories',
    'JWT-based authentication with role-based access control',
    'Dark / Light mode toggle',
    'Search movies by title',
    'User registration and login',
  ];

  const androidFeatures = [
    'Native Android app (Java/XML)',
    'MVVM architecture with Room DB & LiveData',
    'Communicates with the same backend API',
    'Browse & stream movies on mobile',
  ];

  return (
    <div className="demo-page">
      {/* Top bar */}
      <div className="demo-hero-bar">
        <span className="demo-logo" onClick={() => navigate('/')}>NETFLIX-BIU</span>
        <div className="demo-bar-actions">
          <a
            href="https://github.com/NimrodNetzer/Netflix-BIU"
            target="_blank"
            rel="noreferrer"
            className="demo-github-btn"
          >
            <svg height="18" width="18" viewBox="0 0 16 16" fill="currentColor" style={{marginRight: 7, verticalAlign: 'middle'}}>
              <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38
              0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52
              -.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2
              -3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82
              .64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08
              2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01
              1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
            </svg>
            GitHub
          </a>
          <button className="demo-launch-btn" onClick={() => navigate('/login')}>Launch App →</button>
        </div>
      </div>

      <div className="demo-content">
        <h1 className="demo-title">Netflix-BIU</h1>
        <p className="demo-subtitle">
          A full-stack Netflix-style streaming platform built as part of the Advanced Programming
          course at Bar-Ilan University. Combines a React web app, an Android native app, and a
          Node.js + MongoDB backend — supporting dynamic movie browsing, user authentication,
          admin management, and multi-platform access.
        </p>

        {/* Tech Stack */}
        <div className="demo-section">
          <h2>Tech Stack</h2>
          <div className="demo-tech-group">
            <div>
              <p className="demo-tech-label">Web App</p>
              <div className="demo-tags">
                {webTech.map(t => (
                  <span key={t} className="demo-tag">{t}</span>
                ))}
              </div>
            </div>
            <div style={{marginTop: 16}}>
              <p className="demo-tech-label">Android App</p>
              <div className="demo-tags">
                {androidTech.map(t => (
                  <span key={t} className="demo-tag demo-tag-android">{t}</span>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Features */}
        <div className="demo-section">
          <h2>Features</h2>
          <p className="demo-tech-label" style={{marginBottom: 10}}>Web App</p>
          <ul className="demo-features">
            {webFeatures.map(f => <li key={f}>{f}</li>)}
          </ul>
          <p className="demo-tech-label" style={{marginTop: 20, marginBottom: 10}}>Android App</p>
          <ul className="demo-features demo-features-android">
            {androidFeatures.map(f => <li key={f}>{f}</li>)}
          </ul>
        </div>

        {/* Demo Credentials */}
        <div className="demo-section">
          <h2>Try it yourself</h2>
          <p className="demo-hint">Use one of the accounts below — credentials are pre-filled on click:</p>
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
