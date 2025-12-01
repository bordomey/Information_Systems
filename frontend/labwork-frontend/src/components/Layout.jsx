import React from 'react';
import { Outlet } from 'react-router-dom';
import './Layout.css';

const Layout = () => {
  return (
    <div className="app-layout">
      <header className="app-header">
        <h1>LabWork Management System</h1>
        <nav className="app-navigation">
          <ul>
            <li><a href="/">Dashboard</a></li>
            <li><a href="/labworks/new">Add LabWork</a></li>
            <li><a href="/special-operations">Special Operations</a></li>
          </ul>
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
      <footer className="app-footer">
        <p>&copy; 2025 LabWork Management System</p>
      </footer>
    </div>
  );
};

export default Layout;