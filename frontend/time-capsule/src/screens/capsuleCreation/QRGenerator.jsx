import React, { useState, useEffect } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { useLocation } from 'react-router-dom'; // Pro kontrolu URL

const QRGenerator = () => {
    const randomString = "https://www.google.com/";
    const location = useLocation(); // Získání aktuální URL

    // Zkontrolujeme, jestli je komponenta otevřená samostatně
    const isFullScreen = location.pathname === '/qrcode';

    return (
        <div 
            className={`${
                isFullScreen ? 'w-full h-screen flex justify-center items-center' : 'w-[200px] h-[200px] mx-auto'
            }`}
        >
            {randomString ? (
                <QRCodeSVG 
                value={randomString} 
                height="100%" 
                width="100%" 
            />
            ) : (
                <p>Načítání...</p>
            )}
        </div>
    );
};

export default QRGenerator;

