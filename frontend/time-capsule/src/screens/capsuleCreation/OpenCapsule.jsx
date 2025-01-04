import React, { useState } from 'react';
import {
    ArrowLeft,
    Calendar,
    Upload,
    Users,
    Lock,
    Check,
    MapPin,
    Plus,
    Copy,
    QrCode,
    CirclePlus,
    Circle,
    LockOpen
} from 'lucide-react';
import { GoogleMap, LoadScript, Marker } from '@react-google-maps/api';
import Contributor from '../../components/capsulecreation/Contributor';
import CopyLinkButton from '../../components/capsulecreation/CopyLinkButton';
import InfoBox from '../../components/capsulecreation/InfoBox';
import DropdownSelect from '../../components/capsulecreation/DropdownSelect';
import QRGenerator from './QRGenerator';
import { useNavigate, useLocation } from 'react-router-dom';
import InfoSection from '../../components/capsulecreation/InfoSection';
import Warning from '../../components/capsulecreation/Warning';
import Confirmation from '../../components/capsulecreation/Confirmation';
import { format } from 'date-fns';
import { cs } from 'date-fns/locale';
import { useParams } from 'react-router-dom';
import QRCodeScanner from '../../components/QRCodeScanner';
import {ApiService as api} from "../../api/api.js";


const OpenCapsule = () => {
    const location = useLocation();
    const { id } = useParams();
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [isFirstLoad, setIsFirstLoad] = useState(true);
    const { capsule } = location.state || {};

    const [formData, setFormData] = useState({
        hasGeolocation: false,
        hasQRCode: false,
        qrcode: null,
        geolocation: null,
    });


    const [errors, setErrors] = useState({});

    const steps = [
        { number: 1, title: 'Čas', condition: true }, // Každý krok bude mít podmínku pro zobrazení
        { number: 2, title: 'Geolokace', condition: capsule.unlockMethods.geolocationEnabled }, //
        { number: 3, title: 'QR kód', condition: capsule.unlockMethods.qrCodeEnabled },
    ].filter(step => step.condition); // Filtrujeme kroky, které mají podmínku "true"


    const handleUnlock = async ()=> {
        try {
            const response = await api.unlockCapsule(id);
            if (response) {
                console.log('Capsule locked successfully:', response);
            } else {
                console.error('No response returned from lockCapsule API call');
            }
        } catch (error) {
            console.error('Error while locking the capsule:', error);
        }
    };

    const validateStep = (currentStep) => {
        const newErrors = {};

        if (currentStep === 1) {
            if (new Date(capsule.unlockTime) > new Date()) {
                newErrors.openDate = 'Datum otevření nesmí být starší než dnešní datum';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleNext = () => {
        if (validateStep(step)) {
            const nextStep = steps.find(s => s.number > step)?.number;
            if (nextStep) {
                setStep(nextStep);
            }
        }
    };


    const handleSubmit = () => {
        if (validateStep(step)) {
            handleUnlock();
            navigate('/dashboard');
        }
    };

    const handleGeolocationCheck = () => {
        if (!navigator.geolocation) {
            setErrors(prev => ({ ...prev, geolocation: 'Geolokace není podporována vaším prohlížečem.' }));
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const userLocation = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude,
                };

                const unlockLocation = {
                    lat: capsule.unlockLat,
                    lng: capsule.unlockLongit,
                };

                // Tolerance v kilometrech pro přibližné porovnání
                const distanceTolerance = 0.1; // cca 100 metrů

                const isCloseEnough = (capsule.unlockLat && capsule.unlockLongit &&
                    calculateDistance(userLocation, unlockLocation) <= distanceTolerance);

                if (isCloseEnough) {
                    setFormData(prev => ({
                        ...prev,
                        hasGeolocation: true,
                        geolocation: userLocation,
                    }));
                    setErrors(prev => {
                        const { geolocation, ...rest } = prev;
                        return rest;
                    });
                } else {
                    setErrors(prev => ({ ...prev, geolocation: 'Vaše poloha neodpovídá požadované lokaci kapsle.' }));
                }
            },
            () => {
                setErrors(prev => ({ ...prev, geolocation: 'Nepodařilo se získat vaši polohu.' }));
            }
        );
    };

    const calculateDistance = (loc1, loc2) => {
        const R = 6371; // Poloměr Země v kilometrech
        const dLat = toRad(loc2.lat - loc1.lat);
        const dLon = toRad(loc2.lng - loc1.lng);
        const a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(loc1.lat)) * Math.cos(toRad(loc2.lat)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Vzdálenost v kilometrech
    };

    const toRad = (value) => (value * Math.PI) / 180;

    const handleScanQRCode = () => {
        // Simulace čtení QR kódu kamerou
        simulateQRCodeScan()
            .then((decodedCode) => {
                verifyQRCode(decodedCode);
            })
            .catch(() => {
                setErrors((prev) => ({
                    ...prev,
                    qrcode: 'Nepodařilo se načíst QR kód.',
                }));
            });
    };



    const handleBack = () => {
        const prevStep = [...steps].reverse().find(s => s.number < step)?.number;
        if (prevStep) {
            setStep(prevStep);
        } else {
            navigate('/dashboard'); // If there's no previous step, go back to the dashboard
        }
    };






    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={() => navigate('/dashboard')}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět na přehled
                    </button>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="max-w-3xl mx-auto">
                    {/* Progress steps */}
                    <div className="mb-8 hidden sm:block">
                        <div className="flex justify-between items-center">
                            {steps.map((s, index) => (
                                <div key={s.number} className="flex items-center">
                                    <div className={`flex items-center justify-center w-8 h-8 rounded-full ${step >= s.number ? 'bg-blue-900 text-white' : 'bg-gray-200 text-gray-600'}`}>
                                        {step > s.number ? <Check size={16} /> : s.number}
                                    </div>
                                    <div className="ml-2 text-sm">
                                        {s.title}
                                    </div>
                                    {index < steps.length - 1 && (
                                        <div className={`w-24 h-1 mx-4 ${step > s.number ? 'bg-blue-900' : 'bg-gray-200'}`}></div>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Form content */}
                    <div className="bg-white rounded-lg p-6">
                        {step === 1 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Čas</h2>

                                <div className="flex items-center justify-between">
                                    <div className="flex items-center">
                                        <LockOpen size={20} className="text-gray-400 mr-2" />
                                        <span className="text-sm text-gray-700">Datum otevření</span>
                                    </div>
                                    <label className="relative inline-flex items-center">
                                        {format(new Date(capsule.unlockTime), 'd. MMMM yyyy', { locale: cs })}

                                    </label>
                                </div>
                            </div>
                        )}
                        <div className="bg-white rounded-lg">
                            {step === 2 && (
                                <div className="space-y-6">
                                    <div className="flex flex-col space-y-4">
                                        <h2 className="text-2xl font-bold text-gray-900">
                                            Geolokace
                                        </h2>
                                        <p className="text-gray-700">
                                            Pro pokračování ověříme vaši aktuální polohu. Klikněte na tlačítko níže pro povolení přístupu k vaší GPS.{' '}
                                            {capsule.unlockLat && capsule.unlockLongit
                                                ? `Šířka: ${capsule.unlockLat}, Délka: ${capsule.unlockLongit}`
                                                : 'Geolokace není k dispozici.'}
                                        </p>

                                        <div className="flex items-center justify-center">
                                            <button
                                                onClick={() => handleGeolocationCheck()}
                                                className="px-4 py-2  bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                            >
                                                Získat aktuální polohu
                                            </button>
                                        </div>
                                        {errors.geolocation && (
                                            <p className="text-red-500 text-sm mt-1">{errors.geolocation}</p>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>


                        <div className="bg-white rounded-lg">
                            {step === 3 && (
                                <div className="space-y-6">
                                    <div className="flex flex-col space-y-4">
                                        <h2 className="text-2xl font-bold text-gray-900">
                                            QR kód
                                        </h2>
                                        <p className="text-gray-700">
                                            Nahrajte kód z QR kódu nebo jej načtěte pomocí zadní kamery. Kód bude ověřen.
                                        </p>

                                        {/* Input pro zadání QR kódu ručně */}
                                        <div className="space-y-2">
                                            <label className="block text-sm font-medium text-gray-700">
                                                Zadejte kód z QR kódu
                                            </label>
                                            <input
                                                type="text"
                                                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                                                placeholder="Zadejte kód"
                                                value={formData.qrcode || ''}
                                                onChange={(e) =>
                                                    setFormData((prev) => ({ ...prev, qrcode: e.target.value }))
                                                }
                                            />
                                        </div>

                                        <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0 w-max">

                                            <QRCodeScanner
                                                onScanSuccess={(scannedCode) => {
                                                    console.log('Naskenovaný kód:', scannedCode);
                                                    verifyQRCode(scannedCode);
                                                }}
                                            />
                                        </div>


                                        {errors.qrcode && (
                                            <p className="text-red-500 text-sm mt-1">{errors.qrcode}</p>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>




                        {/* Navigation buttons */}
                        <div className="flex justify-end mt-6">
                            <>
                                {step > 1 && (
                                    <button
                                        onClick={handleBack}
                                        className="px-6 py-2 mx-6 text-base text-center text-black bg-white rounded-lg border border-solid border-neutral-700 hover:bg-gray-200"
                                    >
                                        Zpět
                                    </button>
                                )}
                                {step < steps.length && (
                                    <button
                                        onClick={handleNext}
                                        className="px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                    >
                                        Pokračovat
                                    </button>
                                )}
                                {step === steps.length && (
                                    <button
                                        onClick={handleSubmit}
                                        className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-600"
                                    >
                                        Dokončit
                                    </button>
                                )}
                            </>
                        </div>


                    </div>

                </div>
            </main >
        </div >
    );
};

export default OpenCapsule;