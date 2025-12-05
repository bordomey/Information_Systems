import axios from 'axios';

const getBaseURL = () => {
  if (typeof window !== 'undefined' && window.location.hostname === 'localhost') {
    return 'http://localhost:8080/labwork-system/api';
  }
  return '/labwork-system/api';
};

const apiClient = axios.create({
  baseURL: getBaseURL(),
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  config => {
    const storedUser = localStorage.getItem('user');
    let user = null;
    if (storedUser) {
      try {
        user = JSON.parse(storedUser);
      } catch (e) {
        console.error('Failed to parse user from localStorage', e);
      }
    }
    
    if (user) {
      config.headers['X-User-Name'] = user.username;
      config.headers['X-User-Role'] = user.role;
    }
    
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const labWorkService = {
  getAllLabWorks: async () => {
    try {
      const response = await apiClient.get('/labworks');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to fetch lab works: ${error.message}`);
    }
  },

  getLabWorkById: async (id) => {
    try {
      const response = await apiClient.get(`/labworks/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to fetch lab work: ${error.message}`);
    }
  },

  createLabWork: async (labWork) => {
    try {
      const response = await apiClient.post('/labworks', labWork);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create lab work: ${error.message}`);
    }
  },

  updateLabWork: async (id, labWork) => {
    try {
      const response = await apiClient.put(`/labworks/${id}`, labWork);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to update lab work: ${error.message}`);
    }
  },

  deleteLabWork: async (id) => {
    try {
      await apiClient.delete(`/labworks/${id}`);
      return true;
    } catch (error) {
      throw new Error(`Failed to delete lab work: ${error.message}`);
    }
  },
  
  deleteAllLabWorks: async () => {
    try {
      await apiClient.delete('/labworks');
      return true;
    } catch (error) {
      throw new Error(`Failed to delete all lab works: ${error.message}`);
    }
  },

  getAverageMinimalPoint: async () => {
    try {
      const response = await apiClient.get('/labworks/average');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to calculate average: ${error.message}`);
    }
  },

  filterByDescription: async (description) => {
    try {
      const response = await apiClient.get(`/labworks/filter?description=${encodeURIComponent(description)}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to filter lab works: ${error.message}`);
    }
  },
  
  filterByAuthorGreaterThan: async (authorName) => {
    try {
      const response = await apiClient.get(`/labworks/filter-by-author?authorName=${encodeURIComponent(authorName)}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to filter lab works by author: ${error.message}`);
    }
  },
  
  decreaseDifficulty: async (id, steps) => {
    try {
      const response = await apiClient.post(`/labworks/${id}/decrease-difficulty`, { steps });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to decrease difficulty: ${error.message}`);
    }
  },
  
  addTop10MostDifficultToDiscipline: async (discipline) => {
    try {
      const response = await apiClient.post('/labworks/top10-difficult', discipline);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to add top 10 most difficult lab works: ${error.message}`);
    }
  }
};

export const importService = {
  importLabWorks: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await apiClient.post('/import/labworks', formData);
      return response.data;
    } catch (error) {
      console.error('Import error:', error.response || error);
      
      if (error.response && error.response.status === 429) {
        throw new Error('Maximum concurrent imports reached. Please wait for current imports to complete.');
      }
      
      if (error.response && error.response.status === 401) {
        throw new Error('You must be logged in to import files.');
      }
      
      throw new Error(`Failed to import lab works: ${error.message || error}`);
    }
  },
  
  getImportProgress: async (operationId) => {
    try {
      const response = await apiClient.get(`/import/progress/${operationId}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get import progress: ${error.message}`);
    }
  },
  
  getImportHistory: async (username, isAdmin) => {
    try {
      const response = await apiClient.get('/import/history');
      return response.data;
    } catch (error) {
      if (error.response && error.response.status === 401) {
        throw new Error('You must be logged in to view import history.');
      }
      
      throw new Error(`Failed to get import history: ${error.message}`);
    }
  }
};

export default apiClient;