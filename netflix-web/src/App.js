import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Login from './components/Login/Login';
import Home from './components/Home/Home';
import Signup from './components/Login/Signup';
import Hero from './components/FirstPage/Hero';
import SearchPage from './components/Utils/SearchPage';
import TopMenu from './components/Utils/TopMenu';
import Admin from './components/Admin/Admin';
import DemoPage from './components/FirstPage/DemoPage';
import './index.css';

// Separate component so useLocation works inside Router context
function AppLayout({ darkMode, setDarkMode }) {
  const location = useLocation();
  const hideTopMenu = location.pathname === '/demo';

  return (
    <div className={darkMode ? "dark-mode" : "light-mode"}>
      {!hideTopMenu && <TopMenu darkMode={darkMode} setDarkMode={setDarkMode} />}
      <Routes>
        <Route path="/" element={<Hero />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/home" element={<Home />} />
        <Route path="/search/:query" element={<SearchPage />} />
        <Route path="/admin" element={<Admin />} />
        <Route path="/demo" element={<DemoPage />} />
      </Routes>
    </div>
  );
}

const App = () => {
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("theme") === "light" ? false : true;
  });

  useEffect(() => {
    localStorage.setItem("theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  return (
    <Router>
      <AppLayout darkMode={darkMode} setDarkMode={setDarkMode} />
    </Router>
  );
};

export default App;
