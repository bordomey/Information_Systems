import React, { useState, useEffect } from 'react';
import { labWorkService, importService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import './SpecialOperations.css';

const SpecialOperations = () => {
  const [averageResult, setAverageResult] = useState(null);
  const [filterResult, setFilterResult] = useState([]);
  const [descriptionFilter, setDescriptionFilter] = useState('');
  const [authorFilter, setAuthorFilter] = useState('');
  const [disciplineName, setDisciplineName] = useState('');
  const [lectureHours, setLectureHours] = useState(0);
  const [labWorkId, setLabWorkId] = useState('');
  const [difficultySteps, setDifficultySteps] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const [selectedFile, setSelectedFile] = useState(null);
  const [importResult, setImportResult] = useState(null);
  const [importProgress, setImportProgress] = useState(null);
  const [progressInterval, setProgressInterval] = useState(null);
  
  const { currentUser } = useAuth();

  const calculateAverage = async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await labWorkService.getAverageMinimalPoint();
      setAverageResult(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filterByDescription = async () => {
    if (!descriptionFilter.trim()) {
      setError('Please enter a description to filter by');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const result = await labWorkService.filterByDescription(descriptionFilter);
      setFilterResult(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const filterByAuthor = async () => {
    if (!authorFilter.trim()) {
      setError('Please enter an author name to filter by');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const result = await labWorkService.filterByAuthorGreaterThan(authorFilter);
      setFilterResult(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const decreaseDifficulty = async () => {
    if (!labWorkId || isNaN(parseInt(labWorkId))) {
      setError('Please enter a valid lab work ID');
      return;
    }
    
    if (difficultySteps < 0) {
      setError('Steps must be a non-negative number');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await labWorkService.decreaseDifficulty(parseInt(labWorkId), difficultySteps);
      setError('Successfully decreased difficulty');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const addTop10ToDiscipline = async () => {
    if (!disciplineName.trim()) {
      setError('Please enter a discipline name');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await labWorkService.addTop10MostDifficultToDiscipline({
        name: disciplineName,
        lectureHours: parseInt(lectureHours) || 0
      });
      setError('Successfully added top 10 most difficult lab works to discipline');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
    setImportResult(null);
    setImportProgress(null);
    setError(null);
    
    if (progressInterval) {
      clearInterval(progressInterval);
      setProgressInterval(null);
    }
  };
  
  const handleImport = async () => {
    if (!currentUser) {
      setError('You must be logged in to import files');
      return;
    }
    
    if (!selectedFile) {
      setError('Please select a file to import');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setImportProgress({ processedItems: 0, totalItems: 0, percentage: 0 });
      
      console.log('Selected file:', selectedFile);
      const result = await importService.importLabWorks(selectedFile);
      
      const interval = setInterval(async () => {
        try {
          const progress = await importService.getImportProgress(result.operationId);
          if (progress) {
            setImportProgress(progress);
          } else {
            clearInterval(interval);
            setImportProgress(null);
            setImportResult(result);
            setError('Import completed successfully');
            setLoading(false);
          }
        } catch (err) {
          clearInterval(interval);
          setImportProgress(null);
          setError(`Import progress check failed: ${err.message}`);
          setLoading(false);
        }
      }, 500); 
      
      setProgressInterval(interval);
    } catch (err) {
      console.error('Import error details:', err);
      setError(`Import failed: ${err.message}`);
      setLoading(false);
      setImportProgress(null);
    }
  };

  useEffect(() => {
    return () => {
      if (progressInterval) {
        clearInterval(progressInterval);
      }
    };
  }, [progressInterval]);

  return (
    <div className="special-operations">
      <h2>Special Operations</h2>
      
      {error && <div className="operations-error">Error: {error}</div>}
      
      <div className="operations-section">
        <h3>Calculate Average Minimal Point</h3>
        <button 
          className="btn btn-primary" 
          onClick={calculateAverage}
          disabled={loading}
        >
          {loading ? 'Calculating...' : 'Calculate Average'}
        </button>
        
        {averageResult !== null && (
          <div className="result-display">
            <p>Average Minimal Point: <strong>{averageResult.toFixed(2)}</strong></p>
          </div>
        )}
      </div>
      
      <div className="operations-section">
        <h3>Filter Lab Works by Description</h3>
        <div className="filter-form">
          <input
            type="text"
            value={descriptionFilter}
            onChange={(e) => setDescriptionFilter(e.target.value)}
            placeholder="Enter description substring"
            className="filter-input"
          />
          <button 
            className="btn btn-primary" 
            onClick={filterByDescription}
            disabled={loading}
          >
            {loading ? 'Filtering...' : 'Filter'}
          </button>
        </div>
      </div>
      
      <div className="operations-section">
        <h3>Filter Lab Works by Author Comparison</h3>
        <div className="filter-form">
          <input
            type="text"
            value={authorFilter}
            onChange={(e) => setAuthorFilter(e.target.value)}
            placeholder="Enter author name"
            className="filter-input"
          />
          <button 
            className="btn btn-primary" 
            onClick={filterByAuthor}
            disabled={loading}
          >
            {loading ? 'Filtering...' : 'Filter Authors >'}
          </button>
        </div>
      </div>
      
      <div className="operations-section">
        <h3>Reduce Difficulty of Lab Work</h3>
        <div className="filter-form">
          <input
            type="number"
            value={labWorkId}
            onChange={(e) => setLabWorkId(e.target.value)}
            placeholder="Enter Lab Work ID"
            className="filter-input"
            min="1"
          />
          <input
            type="number"
            value={difficultySteps}
            onChange={(e) => setDifficultySteps(parseInt(e.target.value) || 0)}
            placeholder="Steps"
            className="filter-input"
            min="0"
          />
          <button 
            className="btn btn-primary" 
            onClick={decreaseDifficulty}
            disabled={loading}
          >
            {loading ? 'Reducing...' : 'Reduce Difficulty'}
          </button>
        </div>
      </div>
      
      <div className="operations-section">
        <h3>Add Top 10 Most Difficult Lab Works to Discipline</h3>
        <div className="filter-form">
          <input
            type="text"
            value={disciplineName}
            onChange={(e) => setDisciplineName(e.target.value)}
            placeholder="Enter discipline name"
            className="filter-input"
          />
          <input
            type="number"
            value={lectureHours}
            onChange={(e) => setLectureHours(e.target.value)}
            placeholder="Lecture hours"
            className="filter-input"
            min="0"
          />
          <button 
            className="btn btn-primary" 
            onClick={addTop10ToDiscipline}
            disabled={loading}
          >
            {loading ? 'Adding...' : 'Add Top 10'}
          </button>
        </div>
      </div>
      
      <div className="operations-section">
        <h3>Import Lab Works from JSON File</h3>
        <div className="filter-form">
          <input
            type="file"
            accept=".json"
            onChange={handleFileChange}
            className="file-input"
          />
          <button 
            className="btn btn-primary" 
            onClick={handleImport}
            disabled={loading || !selectedFile}
          >
            {loading ? 'Importing...' : 'Import JSON File'}
          </button>
        </div>
        <div className="import-info">
          <small>Note: Maximum 2 concurrent imports allowed. Imports taking more than 3 seconds will automatically adjust worker allocation.</small>
        </div>
        
        {importProgress && (
          <div className="progress-display">
            <p>Import in progress: {importProgress.percentage}% ({importProgress.processedItems}/{importProgress.totalItems})</p>
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${importProgress.percentage}%` }}
              ></div>
            </div>
            <p><small>Processing items with 1-second delays to demonstrate worker allocation...</small></p>
          </div>
        )}
        
        {importResult && (
          <div className="result-display">
            <p>Import Result: <strong>{importResult.message}</strong></p>
            <p>Operation ID: <strong>{importResult.operationId}</strong></p>
          </div>
        )}
      </div>
      
      {filterResult.length > 0 && (
        <div className="filter-results">
          <h4>Filter Results ({filterResult.length} items found)</h4>
          <div className="table-container">
            <table className="results-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Difficulty</th>
                  <th>Minimal Point</th>
                  <th>Author</th>
                </tr>
              </thead>
              <tbody>
                {filterResult.map((labWork) => (
                  <tr key={labWork.id}>
                    <td>{labWork.id}</td>
                    <td>{labWork.name}</td>
                    <td>{labWork.description}</td>
                    <td>{labWork.difficulty || 'N/A'}</td>
                    <td>{labWork.minimalPoint}</td>
                    <td>{labWork.author ? labWork.author.name : 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default SpecialOperations;