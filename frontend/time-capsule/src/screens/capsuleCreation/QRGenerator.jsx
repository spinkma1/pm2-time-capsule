import React from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { useLocation, useNavigate } from 'react-router-dom';


const QRGenerator = (randomString) =>{
    const location = useLocation();
    const navigate = useNavigate();

    const isFullScreen = location.pathname === '/qrcode';

    const handleBack = () => {
        navigate(-1);
    };

    return (
        <div
            className={`${
                isFullScreen ? 'w-full h-screen flex justify-center items-center' : 'w-[200px] h-[200px] mx-auto'
            }`}
        >
            {isFullScreen && (
                <button
                    onClick={handleBack}
                    className="absolute top-4 left-4 bg-blue-500 text-white px-4 py-2 rounded-md"
                >
                    Zpět
                </button>
            )}

            {randomString ? (
                <QRCodeSVG
                    value={randomString}
                    height="100%"
                    width="100%"
                />
            ) : (
                <div className="loader"></div>
            )}
        </div>
    );
};

export default QRGenerator;

