import React, { useState, useEffect } from 'react';
import { importService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import './ImportHistory.css';

const ImportHistory = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { currentUser } = useAuth();

  useEffect(() => {
    fetchImportHistory();
  }, [currentUser]);

  const fetchImportHistory = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await importService.getImportHistory(currentUser.username, currentUser.role === 'admin');
      setHistory(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (timestamp) => {
    if (!timestamp) return 'N/A';
    return new Date(timestamp).toLocaleString();
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'SUCCESS':
        return 'status-badge success';
      case 'FAILED':
        return 'status-badge failed';
      case 'IN_PROGRESS':
        return 'status-badge in-progress';
      default:
        return 'status-badge';
    }
  };

  if (loading) {
    return <div className="import-history-loading">Loading import history...</div>;
  }

  if (error) {
    return <div className="import-history-error">Error: {error}</div>;
  }

  return (
    <div className="import-history">
      <div className="import-history-header">
        <h2>Import History</h2>
        <button className="btn btn-primary" onClick={fetchImportHistory}>
          Refresh
        </button>
      </div>

      {history.length === 0 ? (
        <div className="import-history-empty">
          <p>No import history found.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="import-history-table">
            <thead>
              <tr>
                <th>Operation ID</th>
                <th>Status</th>
                <th>Username</th>
                <th>Objects Count</th>
                <th>Timestamp</th>
                <th>Error Message</th>
              </tr>
            </thead>
            <tbody>
              {history.map((entry) => (
                <tr key={entry.operationId}>
                  <td>{entry.operationId}</td>
                  <td>
                    <span className={getStatusBadgeClass(entry.status)}>
                      {entry.status}
                    </span>
                  </td>
                  <td>{entry.username}</td>
                  <td>{entry.objectsCount || 'N/A'}</td>
                  <td>{formatDate(entry.timestamp)}</td>
                  <td>
                    {entry.errorMessage ? (
                      <div className="error-message">{entry.errorMessage}</div>
                    ) : (
                      'N/A'
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ImportHistory;