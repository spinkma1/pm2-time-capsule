import React, { createContext, useState, useContext, useEffect } from 'react';
import { LoadScript } from '@react-google-maps/api';

const GoogleMapsContext = createContext();

export const GoogleMapsProvider = ({ children }) => {
    const [scriptLoaded, setScriptLoaded] = useState(false);

    useEffect(() => {
        if (window.google && window.google.maps) {
            setScriptLoaded(true);
        }
    }, []);

    return (
        <GoogleMapsContext.Provider value={scriptLoaded}>
            <LoadScript googleMapsApiKey="AIzaSyCC-fxRVR03IQn1i5RD9rkyu91uz2eTXuc">
                {children}
            </LoadScript>
        </GoogleMapsContext.Provider>
    );
};

export const useGoogleMaps = () => {
    return useContext(GoogleMapsContext);
};