import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in (from localStorage)
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        setCurrentUser(user);
      } catch (e) {
        console.error('Failed to parse user from localStorage', e);
      }
    }
    setLoading(false);
  }, []);

  const login = (username, password) => {
    // Simple authentication - in a real app, this would call an API
    if (username && password) {
      const user = { 
        username, 
        role: username === 'admin' ? 'admin' : 'user',
        isAuthenticated: true 
      };
      localStorage.setItem('user', JSON.stringify(user));
      setCurrentUser(user);
      return Promise.resolve(user);
    }
    return Promise.reject(new Error('Invalid credentials'));
  };

  const signup = (username, password) => {
    // Simple signup - in a real app, this would call an API
    if (username && password) {
      const user = { 
        username, 
        role: 'user',
        isAuthenticated: true 
      };
      localStorage.setItem('user', JSON.stringify(user));
      setCurrentUser(user);
      return Promise.resolve(user);
    }
    return Promise.reject(new Error('Username and password are required'));
  };

  const logout = () => {
    localStorage.removeItem('user');
    setCurrentUser(null);
  };

  const value = {
    currentUser,
    login,
    signup,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};