import React, { useState } from 'react';
import QrScanner from 'react-qr-scanner';

const QRCodeScanner = ({ onScanSuccess }) => {
    const [scanError, setScanError] = useState(null);
    const [camera, setCamera] = useState('user'); // Stav pro výběr kamery

    const handleScan = (data) => {
        if (data) {
            console.log('Načtený QR kód:', data.text);
            alert(data.text);
            if (onScanSuccess) {
                onScanSuccess(data.text);
            }
        }
    };

    const handleError = (err) => {
        console.error('Chyba při čtení QR kódu:', err);
        setScanError('Chyba při čtení QR kódu.');
    };

    const toggleCamera = () => {
        setCamera((prevCamera) => (prevCamera === 'user' ? 'environment' : 'user')); // Přepnutí mezi přední a zadní kamerou
    };

    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-gray-900">Naskenujte QR kód pomocí kamery</h2>
            <button 
                className="p-2 bg-blue-500 text-white rounded-md" 
                onClick={toggleCamera}
            >
                Přepnout kameru
            </button>
            <div className="w-full">
                <QrScanner
                    delay={300}
                    style={{ width: '100%' }}
                    facingMode={camera}  // Nastavení kamery podle stavu
                    onError={handleError}
                    onScan={handleScan}
                />
            </div>
            {scanError && <p className="text-red-500 text-sm">{scanError}</p>}
        </div>
    );
};

export default QRCodeScanner;

