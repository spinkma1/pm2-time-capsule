import React, { useState, useEffect } from 'react';
import { QRCodeSVG } from 'qrcode.react';

const QRGenerator= () => {
    const randomString="https://www.google.com/"
  /*
  useEffect(() => {
    // Získání náhodného řetězce z API
    const fetchRandomString = async () => {
      try {
        const response = await fetch('/api/randomString'); // Api  volání
        const data = await response.json();
        setRandomString(data.randomString); 
      } catch (error) {
        console.error('Chyba při získávání řetězce:', error);
      }
    };

    fetchRandomString(); 
  }, []);*/

  return (
    <div>
      {randomString ? (
        <div>
          <QRCodeSVG  value={randomString} /> 
        </div>
      ) : (
        <p>Načítání...</p>
      )}
    </div>
  );
};

export default QRGenerator;
