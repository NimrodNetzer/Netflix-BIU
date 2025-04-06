import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login/Login';
import Home from './components/Home/Home';
import Signup from './components/Login/Signup';
import Hero from './components/FirstPage/Hero';
import SearchPage from './components/Utils/SearchPage';
import TopMenu from './components/Utils/TopMenu';
import Admin from './components/Admin/Admin';
import './index.css';

const App = () => {
  // ✅ Set dark mode default to `true` (dark mode by default)
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("theme") === "light" ? false : true;
  });

  useEffect(() => {
    // ✅ Save to localStorage when changed
    localStorage.setItem("theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  return (
    <Router>
      <TopMenu darkMode={darkMode} setDarkMode={setDarkMode} />
    <div className={darkMode ? "dark-mode" : "light-mode"}>
        {/* ✅ Ensure TopMenu is always rendered */}
        <Routes>
          <Route path="/" element={<Hero />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/home" element={<Home />} />
          <Route path="/search/:query" element={<SearchPage />} />
          <Route path="/admin" element={<Admin />} />
        </Routes>
    </div>
    </Router>
  );
};

export default App;
