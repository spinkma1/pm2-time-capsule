import React, { useState, useEffect } from 'react';
import API_CONFIG from '../config/api.config';

const HealthCheck = () => {
    const [health, setHealth] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.HEALTH}`)
            .then(response => response.json())
            .then(data => setHealth(data))
            .catch(err => setError(err.message));
    }, []);

    if (error) return <div>Error: {error}</div>;
    if (!health) return <div>Loading...</div>;

    return (
        <div>
            <h2>Backend Status: {health.status}</h2>
            <p>Last Check: {new Date(health.timestamp).toLocaleString()}</p>
        </div>
    );
};

export default HealthCheck;