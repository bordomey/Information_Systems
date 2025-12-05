import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './components/Dashboard';
import LabWorkForm from './components/LabWorkForm';
import SpecialOperations from './components/SpecialOperations';
import Login from './components/Login';
import ImportHistory from './components/ImportHistory';
import ProtectedRoute from './components/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';
import './App.css';

const router = createBrowserRouter([
  {
    path: "/login",
    element: <Login />
  },
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Dashboard />
      },
      {
        path: "/labworks/new",
        element: <LabWorkForm />
      },
      {
        path: "/labworks/:id",
        element: <LabWorkForm />
      },
      {
        path: "/labworks/:id/edit",
        element: <LabWorkForm />
      },
      {
        path: "/special-operations",
        element: <SpecialOperations />
      },
      {
        path: "/import-history",
        element: <ImportHistory />
      }
    ]
  }
]);

function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  );
}

export default App;