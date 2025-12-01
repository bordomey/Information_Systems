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

export default apiClient;