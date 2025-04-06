import React from 'react';
import '../styles/Header.css';  
import './Hero.css';


function Header() {
  return (
    <header className="header">
      <img
        src="https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg"
        alt="Netflix Logo"
        className="logo"
      />
      <div className="header-right">
        <select className="language-select">
          <option>English</option>
          <option>Español</option>
        </select>
        <button className="sign-in">Sign In</button>
      </div>
    </header>
  );
}

export default Header;
