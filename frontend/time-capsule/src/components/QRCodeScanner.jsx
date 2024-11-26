import React, { useState } from 'react';
import { QrReader } from 'react-qr-reader';

const QRCodeScanner = () => {
  const [result, setResult] = useState('Žádný');
  const [scanError, setScanError] = useState('');

  return (
    <div className="space-y-6">
      <div className="w-full justify-end">
        <QrReader
          onResult={(data, error) => {
            if (!!data) {
              setResult(data?.text);
              setScanError(''); 
            }

            if (!!error) {
              console.error(error);
              setScanError('Nepodařilo se naskenovat QR kód. Zkuste to znovu.');
            }
          }}
          constraints={{ facingMode: 'environment' }} 
          style={{ width: '75%' }}
        />
      </div>

      {scanError && <p className="text-red-500 text-sm">{scanError}</p>}
      <p>Výsledený kód: {result}</p>
    </div>
  );
};

export default QRCodeScanner;


