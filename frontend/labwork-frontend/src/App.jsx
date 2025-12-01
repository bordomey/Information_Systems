import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './components/Dashboard';
import LabWorkForm from './components/LabWorkForm';
import SpecialOperations from './components/SpecialOperations';
import './App.css';

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
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
      }
    ]
  }
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;