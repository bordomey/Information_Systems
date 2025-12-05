import React, { useState, useEffect } from 'react';
import { labWorkService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const [labWorks, setLabWorks] = useState([]);
  const [filteredLabWorks, setFilteredLabWorks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
  const [filterText, setFilterText] = useState('');
  
  const { currentUser } = useAuth();

  useEffect(() => {
    fetchLabWorks();
    const intervalId = setInterval(fetchLabWorks, 30000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    applyFiltersAndSorting();
  }, [labWorks, filterText, sortConfig]);

  const fetchLabWorks = async () => {
    try {
      setLoading(true);
      const data = await labWorkService.getAllLabWorks();
      setLabWorks(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const applyFiltersAndSorting = () => {
    let result = [...labWorks];
    
    if (filterText) {
      const lowercasedFilter = filterText.toLowerCase();
      result = result.filter(item =>
        item.name.toLowerCase().includes(lowercasedFilter) ||
        item.description.toLowerCase().includes(lowercasedFilter) ||
        (item.difficulty && item.difficulty.toLowerCase().includes(lowercasedFilter))
      );
    }
    
    if (sortConfig.key) {
      result.sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
          return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
          return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }
    
    setFilteredLabWorks(result);
    setCurrentPage(1); 
  };

  const requestSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredLabWorks.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredLabWorks.length / itemsPerPage);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);
  
  const handleDeleteAll = async () => {
    if (window.confirm('Are you sure you want to delete ALL lab works? This action cannot be undone.')) {
      try {
        await labWorkService.deleteAllLabWorks();
        fetchLabWorks(); 
      } catch (err) {
        alert('Failed to delete all lab works: ' + err.message);
      }
    }
  };

  if (loading && labWorks.length === 0) {
    return <div className="dashboard-loading">Loading lab works...</div>;
  }

  if (error && labWorks.length === 0) {
    return <div className="dashboard-error">Error: {error}</div>;
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>Lab Works</h2>
        <button className="btn btn-primary" onClick={fetchLabWorks}>
          Refresh
        </button>
        <button className="btn btn-primary" onClick={() => window.location.href = '/labworks/new'}>
          Add New Lab Work
        </button>
        {currentUser && currentUser.role === 'admin' && (
          <button className="btn btn-danger" onClick={handleDeleteAll}>
            Delete All Lab Works
          </button>
        )}
      </div>

      <div className="filter-section">
        <input
          type="text"
          placeholder="Filter by name, description, or difficulty..."
          value={filterText}
          onChange={(e) => setFilterText(e.target.value)}
          className="filter-input"
        />
      </div>

      {filteredLabWorks.length === 0 ? (
        <div className="dashboard-empty">
          <p>No lab works found. {filterText ? 'Try adjusting your filter.' : 'Create your first lab work!'}</p>
        </div>
      ) : (
        <>
          <div className="table-container">
            <table className="labworks-table">
              <thead>
                <tr>
                  <th onClick={() => requestSort('id')} className="sortable">
                    ID {sortConfig.key === 'id' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th onClick={() => requestSort('name')} className="sortable">
                    Name {sortConfig.key === 'name' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th onClick={() => requestSort('description')} className="sortable">
                    Description {sortConfig.key === 'description' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th onClick={() => requestSort('difficulty')} className="sortable">
                    Difficulty {sortConfig.key === 'difficulty' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th onClick={() => requestSort('minimalPoint')} className="sortable">
                    Minimal Point {sortConfig.key === 'minimalPoint' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th onClick={() => requestSort('creationDate')} className="sortable">
                    Creation Date {sortConfig.key === 'creationDate' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                  </th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {currentItems.map((labWork) => (
                  <tr key={labWork.id}>
                    <td>{labWork.id}</td>
                    <td>{labWork.name}</td>
                    <td>{labWork.description}</td>
                    <td>{labWork.difficulty || 'N/A'}</td>
                    <td>{labWork.minimalPoint}</td>
                    <td>
                      {(() => {
                        if (!labWork.creationDate) return 'N/A';
                        let dateString = labWork.creationDate;
                        if (dateString.endsWith('[UTC]')) {
                          dateString = dateString.substring(0, dateString.length - 5);
                        }
                        const date = new Date(dateString);
                        return isNaN(date.getTime()) ? 'Invalid Date' : date.toLocaleDateString();
                      })()}
                    </td>
                    <td>
                      <button 
                        className="btn btn-secondary"
                        onClick={() => window.location.href = `/labworks/${labWork.id}`}
                      >
                        View
                      </button>
                      <button 
                        className="btn btn-warning"
                        onClick={() => window.location.href = `/labworks/${labWork.id}/edit`}
                      >
                        Edit
                      </button>
                      <button 
                        className="btn btn-danger"
                        onClick={async () => {
                          if (window.confirm('Are you sure you want to delete this lab work?')) {
                            try {
                              await labWorkService.deleteLabWork(labWork.id);
                              fetchLabWorks(); 
                            } catch (err) {
                              alert('Failed to delete lab work: ' + err.message);
                            }
                          }
                        }}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <span className="pagination-info">
                Showing {indexOfFirstItem + 1} to {Math.min(indexOfLastItem, filteredLabWorks.length)} of {filteredLabWorks.length} entries
              </span>
              <div className="pagination-buttons">
                {[...Array(totalPages).keys()].map(number => (
                  <button
                    key={number + 1}
                    onClick={() => paginate(number + 1)}
                    className={`btn ${currentPage === number + 1 ? 'btn-active' : ''}`}
                  >
                    {number + 1}
                  </button>
                ))}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Dashboard;