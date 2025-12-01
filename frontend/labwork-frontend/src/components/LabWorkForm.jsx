import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { labWorkService } from '../services/api';
import { LabWork, Coordinates, Discipline, Person, Location, Difficulty, Color } from '../utils/models';
import './LabWorkForm.css';

const LabWorkForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [labWork, setLabWork] = useState(new LabWork());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isEdit) {
      fetchLabWork();
    }
  }, [id, isEdit]);

  const fetchLabWork = async () => {
    try {
      setLoading(true);
      const data = await labWorkService.getLabWorkById(id);
      
      // Convert date string to Date object for proper handling
      // Handle the backend date format: 2025-12-01T02:32:00.763Z[UTC]
      if (data.creationDate) {
        // Remove the [UTC] suffix if present
        let dateString = data.creationDate;
        if (dateString.endsWith('[UTC]')) {
          dateString = dateString.substring(0, dateString.length - 5);
        }
        // Parse the date and convert to local datetime input format
        const date = new Date(dateString);
        if (!isNaN(date.getTime())) {
          data.creationDate = date.toISOString().slice(0, 16);
        }
      }
      
      // Ensure author has proper default values if it exists
      if (data.author) {
        // Make sure eyeColor and hairColor have default values
        if (!data.author.eyeColor) {
          data.author.eyeColor = Color.GREEN;
        }
        if (!data.author.hairColor) {
          data.author.hairColor = Color.BLACK;
        }
        // Ensure location exists
        if (!data.author.location) {
          data.author.location = new Location();
        }
      }
      
      setLabWork(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Handle nested object properties
    if (name.includes('.')) {
      const [parent, child] = name.split('.');
      let convertedValue = value;
      
      // Convert numeric fields to appropriate types
      if ((parent === 'coordinates' && (child === 'x' || child === 'y')) ||
          (parent === 'discipline' && child === 'lectureHours') ||
          (parent === 'author' && child === 'minimalPoint') ||
          (parent === 'author' && child === 'height')) {
        convertedValue = value === '' ? 0 : Number(value);
      }
      
      setLabWork(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: convertedValue
        }
      }));
    } else {
      let convertedValue = value;
      
      // Convert numeric fields to appropriate types
      if (name === 'minimalPoint') {
        convertedValue = value === '' ? 0 : Number(value);
      }
      
      setLabWork(prev => ({
        ...prev,
        [name]: convertedValue
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      if (!labWork.name || !labWork.description || !labWork.discipline.name) {
        throw new Error('Please fill in all required fields');
      }
      if (labWork.minimalPoint <= 0) {
        throw new Error('Minimal point must be greater than 0');
      }
      if (labWork.author && labWork.author.name) {
        if (!labWork.author.passportID) {
          throw new Error('Passport ID is required for author');
        }
        if (!labWork.author.hairColor) {
          throw new Error('Hair color is required for author');
        }
        if (labWork.author.height <= 0) {
          throw new Error('Author height must be greater than 0');
        }
      }
      
      if (isEdit) {
        await labWorkService.updateLabWork(id, labWork);
      } else {
        await labWorkService.createLabWork(labWork);
      }
      
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && isEdit) {
    return <div className="form-loading">Loading lab work...</div>;
  }

  return (
    <div className="labwork-form-container">
      <h2>{isEdit ? 'Edit Lab Work' : 'Add New Lab Work'}</h2>
      
      {error && <div className="form-error">Error: {error}</div>}
      
      <form onSubmit={handleSubmit} className="labwork-form">
        <div className="form-section">
          <h3>Basic Information</h3>
          
          {isEdit && labWork.creationDate && (
            <div className="form-group">
              <label>Creation Date</label>
              <input
                type="datetime-local"
                value={labWork.creationDate}
                readOnly
                disabled
              />
            </div>
          )}
          
          <div className="form-group">
            <label htmlFor="name">Name *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={labWork.name}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              name="description"
              value={labWork.description}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="minimalPoint">Minimal Point *</label>
            <input
              type="number"
              id="minimalPoint"
              name="minimalPoint"
              value={labWork.minimalPoint}
              onChange={handleChange}
              min="1"
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="difficulty">Difficulty</label>
            <select
              id="difficulty"
              name="difficulty"
              value={labWork.difficulty || ''}
              onChange={handleChange}
            >
              <option value="">Select Difficulty</option>
              {Object.values(Difficulty).map(diff => (
                <option key={diff} value={diff}>{diff}</option>
              ))}
            </select>
          </div>
        </div>
        
        <div className="form-section">
          <h3>Coordinates</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="coordinates.x">X Coordinate</label>
              <input
                type="number"
                id="coordinates.x"
                name="coordinates.x"
                value={labWork.coordinates.x}
                onChange={handleChange}
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="coordinates.y">Y Coordinate</label>
              <input
                type="number"
                id="coordinates.y"
                name="coordinates.y"
                value={labWork.coordinates.y}
                onChange={handleChange}
              />
            </div>
          </div>
        </div>
        
        <div className="form-section">
          <h3>Discipline *</h3>
          
          <div className="form-group">
            <label htmlFor="discipline.name">Name *</label>
            <input
              type="text"
              id="discipline.name"
              name="discipline.name"
              value={labWork.discipline.name}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="discipline.lectureHours">Lecture Hours</label>
            <input
              type="number"
              id="discipline.lectureHours"
              name="discipline.lectureHours"
              value={labWork.discipline.lectureHours}
              onChange={handleChange}
            />
          </div>
        </div>
        
        <div className="form-section">
          <h3>Author</h3>
          
          <div className="form-group">
            <label htmlFor="author.name">Name</label>
            <input
              type="text"
              id="author.name"
              name="author.name"
              value={labWork.author?.name || ''}
              onChange={(e) => {
                const author = labWork.author || new Person();
                author.name = e.target.value;
                setLabWork(prev => ({ ...prev, author }));
              }}
            />
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="author.height">Height</label>
              <input
                type="number"
                id="author.height"
                name="author.height"
                value={labWork.author?.height || ''}
                onChange={(e) => {
                  const author = labWork.author || new Person();
                  author.height = parseFloat(e.target.value) || 0;
                  setLabWork(prev => ({ ...prev, author }));
                }}
                step="0.01"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="author.passportID">Passport ID</label>
              <input
                type="text"
                id="author.passportID"
                name="author.passportID"
                value={labWork.author?.passportID || ''}
                onChange={(e) => {
                  const author = labWork.author || new Person();
                  author.passportID = e.target.value;
                  setLabWork(prev => ({ ...prev, author }));
                }}
              />
            </div>
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="author.eyeColor">Eye Color</label>
              <select
                id="author.eyeColor"
                name="author.eyeColor"
                value={labWork.author?.eyeColor || Color.GREEN}
                onChange={(e) => {
                  const author = labWork.author || new Person();
                  author.eyeColor = e.target.value;
                  setLabWork(prev => ({ ...prev, author }));
                }}
              >
                <option value={Color.GREEN}>{Color.GREEN}</option>
                <option value={Color.BLACK}>{Color.BLACK}</option>
                <option value={Color.YELLOW}>{Color.YELLOW}</option>
              </select>
            </div>
            
            <div className="form-group">
              <label htmlFor="author.hairColor">Hair Color *</label>
              <select
                id="author.hairColor"
                name="author.hairColor"
                value={labWork.author?.hairColor || Color.BLACK}
                onChange={(e) => {
                  const author = labWork.author || new Person();
                  author.hairColor = e.target.value;
                  setLabWork(prev => ({ ...prev, author }));
                }}
              >
                <option value={Color.GREEN}>{Color.GREEN}</option>
                <option value={Color.BLACK}>{Color.BLACK}</option>
                <option value={Color.YELLOW}>{Color.YELLOW}</option>
              </select>
            </div>
          </div>
          
          {labWork.author && (
            <>
              <h4>Author Location</h4>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="author.location.x">X Coordinate</label>
                  <input
                    type="number"
                    id="author.location.x"
                    name="author.location.x"
                    value={labWork.author.location.x}
                    onChange={(e) => {
                      const author = labWork.author || new Person();
                      const location = author.location || new Location();
                      location.x = parseInt(e.target.value) || 0;
                      author.location = location;
                      setLabWork(prev => ({ ...prev, author }));
                    }}
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="author.location.y">Y Coordinate *</label>
                  <input
                    type="number"
                    id="author.location.y"
                    name="author.location.y"
                    value={labWork.author.location.y}
                    onChange={(e) => {
                      const author = labWork.author || new Person();
                      const location = author.location || new Location();
                      location.y = parseInt(e.target.value) || 0;
                      author.location = location;
                      setLabWork(prev => ({ ...prev, author }));
                    }}
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="author.location.z">Z Coordinate *</label>
                  <input
                    type="number"
                    id="author.location.z"
                    name="author.location.z"
                    value={labWork.author.location.z}
                    onChange={(e) => {
                      const author = labWork.author || new Person();
                      const location = author.location || new Location();
                      location.z = parseInt(e.target.value) || 0;
                      author.location = location;
                      setLabWork(prev => ({ ...prev, author }));
                    }}
                  />
                </div>
              </div>
            </>
          )}
        </div>
        
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/')}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Saving...' : (isEdit ? 'Update Lab Work' : 'Create Lab Work')}
          </button>
        </div>
      </form>
    </div>
  );
};

export default LabWorkForm;