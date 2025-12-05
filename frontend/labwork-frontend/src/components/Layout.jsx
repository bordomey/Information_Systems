import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Layout.css';

const Layout = () => {
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header className="header">
        <div className="header-content">
          <h1>LabWork Management System</h1>
          {currentUser && (
            <div className="user-info">
              <span>Welcome, {currentUser.username}!</span>
              <nav className="navigation">
                <Link to="/">Dashboard</Link>
                <Link to="/special-operations">Special Operations</Link>
                <Link to="/import-history">Import History</Link>
              </nav>
              <button className="btn btn-secondary" onClick={handleLogout}>
                Logout
              </button>
            </div>
          )}
        </div>
      </header>
      
      <main className="main-content">
        <Outlet />
      </main>
      
      <footer className="footer">
        <p>&copy; 2023 LabWork Management System</p>
      </footer>
    </div>
  );
};

export default Layout;