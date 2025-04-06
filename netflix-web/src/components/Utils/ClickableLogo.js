import React from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../../assets/LOGO.jpg';// Replace with your actual logo path

const ClickableLogo = () => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/');
  };

  return (
    <img
      src={logo}
      alt="Logo"
      className="logo"
      onClick={handleClick}
      style={{ cursor: 'pointer' }}
    />
  );
};

export default ClickableLogo;
