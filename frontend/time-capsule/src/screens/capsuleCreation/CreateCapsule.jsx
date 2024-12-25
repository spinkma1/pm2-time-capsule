import React, { useState } from 'react';
import {
    ArrowLeft,
    Calendar,
    Lock,
    Check,
    MapPin,
    QrCode
} from 'lucide-react';
import { GoogleMap, Marker } from '@react-google-maps/api';
import Contributor from '../../components/capsulecreation/Contributor';
import CopyLinkButton from '../../components/capsulecreation/CopyLinkButton';
import InfoBox from '../../components/capsulecreation/InfoBox';
import DropdownSelect from '../../components/capsulecreation/DropdownSelect';
import QRGenerator from './QRGenerator';
import { useNavigate } from 'react-router-dom';
import InfoSection from '../../components/capsulecreation/InfoSection';
import Warning from '../../components/capsulecreation/Warning';
import Confirmation from '../../components/capsulecreation/Confirmation';
import { useGoogleMaps } from '../../components/context/GoogleProvider';
import {ApiService as api} from "../../api/api.js";

const CreateCapsule = () => {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [isFirstLoad, setIsFirstLoad] = useState(true);
    const scriptLoaded = useGoogleMaps();
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        openDate: '',
        isPrivate: false,
        contributorsLimit: 5,
        hasGeolocation: false,
        hasQRCode: false,
        qrcode: Math.random().toString(36).substring(2, 18),
        geolocation: null, // To store the selected coordinates
        contributors: [],
    });


    const [errors, setErrors] = useState({});

    const steps = [
        { number: 1, title: 'Základní informace' },
        { number: 2, title: 'Přispěvatelé' },
        { number: 3, title: 'Shrnutí' }
    ];

    const validateStep = (currentStep) => {
        const newErrors = {};

        if (currentStep === 1) {
            if (!formData.title.trim()) {
                newErrors.title = 'Název kapsle je povinný';
            }
            if (!formData.openDate) {
                newErrors.openDate = 'Datum otevření je povinné';
            } else if (new Date(formData.openDate) < new Date()) {
                newErrors.openDate = 'Datum otevření nesmí být starší než dnešní datum';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleNext = () => {
        if (validateStep(step)) {
            setStep(step + 1);
        }
    };

    const handleSubmit = () => {
        if (validateStep(step)) {
            console.log('Form submitted yeah', formData);

            const submit = async () => {
                if (!formData.title || !formData.description || !formData.openDate) {
                    console.error("Required fields are missing.");

                    return;
                }
                console.log(localStorage.getItem('userId'))
                const userIdString = localStorage.getItem('userId');
                const userIdLong = userIdString ? Number(userIdString) : null;

                const capsuleData = {
                    userId: userIdLong,
                    name: formData.title,
                    description: formData.description,
                    capsuleSize: formData.contributorsLimit,
                    unlockTime: formData.openDate ? new Date(formData.openDate).toISOString() : null,
                    qrCodePassword: formData.hasQRCode ? formData.qrcode : null,
                    unlockLat: formData.hasGeolocation ? formData.geolocation?.lat : null,
                    unlockLongit: formData.hasGeolocation ? formData.geolocation?.lng : null,
                    users: formData.contributors.map(contributor => ({
                        email: contributor.email
                    })),
                    unlockMethods: {
                        timeEnabled: true,
                        timeComplete: false,
                        qrCodeEnabled: formData.hasQRCode,
                        qrCodeComplete: false,
                        geolocationEnabled: formData.hasGeolocation,
                        geolocationComplete: false,
                        passwordEnabled: false,
                        passwordComplete: false,
                    },
                    state: "EDIT",
                    teamwork: formData.isPrivate ? true : false
                };
                console.log("Capsule data:", capsuleData)
                try {
                    const response = await api.createCapsule(capsuleData);
                    console.log("Capsule created successfully:", response);
                    navigate('/dashboard');
                } catch (error) {
                    console.error("Error while creating capsule:", error);
                }
            };

            submit();
        }
    };



    const handleDateChange = (e) => {
        const newDate = e.target.value;
        setFormData(prev => ({
            ...prev,
            openDate: newDate
        }));

        // Validate the date immediately
        if (new Date(newDate) < new Date()) {
            setErrors(prevErrors => ({
                ...prevErrors,
                openDate: 'Datum otevření nesmí být starší než dnešní datum'
            }));
        } else {
            setErrors(prevErrors => {
                const { openDate, ...rest } = prevErrors; // remove openDate error if exists
                return rest;
            });
        }
    };

    const handleMapClick = (e) => {
        if (e.latLng) {
            const lat = e.latLng.lat();
            const lng = e.latLng.lng();
            console.log('Map clicked', lat, lng);

            // Nastavení geolokace do formData
            setFormData(prev => ({
                ...prev,
                hasGeolocation: true,
                geolocation: { lat, lng }
            }));
            console.log('Geolocation set', formData.geolocation);
        } else {
            console.log('No latLng data available.');
        }
    };

    const handleMapLoad = () => {
        if (isFirstLoad) {
            // Center the map when it is first loaded
            setIsFirstLoad(false);
        }
    };

    const mapCenter = formData.geolocation || { lat: 50.0755, lng: 14.4378 };

    // Function to handle toggling geolocation
    const handleGeolocationToggle = (e) => {
        const isChecked = e.target.checked;
        setFormData(prev => ({
            ...prev,
            hasGeolocation: isChecked,
            geolocation: isChecked ? prev.geolocation : null,
        }));
    };

    // Delete contributor with id
    const handleDelete = (id) => {
        const updatedContributors = formData.contributors.filter(contributor => contributor.id !== id);
        setFormData({ ...formData, contributors: updatedContributors });
    };

    const handleBack = () => {
        if (step === 1) {
            // If step is 1 (first step), go back to Dashboard
            navigate('/dashboard');
        } else {
            // previous step
            setStep(step - 1);
        }
    };

    const [emailForm, setEmailForm] = useState({ email: '' });
    const [emailErrors, setEmailErrors] = useState({});

    const validateEmailForm = (emailData) => {
        const errors = {};
        if (!emailData.email || !/\S+@\S+\.\S+/.test(emailData.email)) {
            errors.email = 'Zadejte platnou e-mailovou adresu';
        }
        setEmailErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleEmailSubmit = (e) => {
        e.preventDefault();
        const newEmail = emailForm.email.trim();
        if (validateEmailForm({ email: newEmail })) {
            const newContributor = {
                id: formData.contributors.length + 1,
                email: newEmail,
                status: "Neaktivní",
                initial: newEmail.split('@')[0].slice(0, 2).toUpperCase(),
            };
            setFormData((prev) => ({
                ...prev,
                contributors: [...prev.contributors, newContributor],
            }));
            setEmailForm({ email: '' });
            setEmailErrors({});
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
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        {step === 1 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Základní informace</h2>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Název kapsle*</label>
                                    <input
                                        type="text"
                                        value={formData.title}
                                        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                        className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${errors.title ? 'border-red-500' : 'border-gray-300'}`}
                                        placeholder="Např. Maturitní vzpomínky 2024"
                                    />
                                    {errors.title && <p className="text-red-500 text-sm mt-1">{errors.title}</p>}
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Popis kapsle</label>
                                    <textarea
                                        value={formData.description}
                                        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                        rows="3"
                                        placeholder="Popište, co bude kapsle obsahovat..."
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Datum otevření*</label>
                                    <div className="relative">
                                        <Calendar size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                        <input
                                            type="date"
                                            value={formData.openDate}
                                            onChange={handleDateChange}
                                            className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${errors.openDate ? 'border-red-500' : 'border-gray-300'}`}
                                        />
                                    </div>
                                    {errors.openDate && <p className="text-red-500 text-sm mt-1">{errors.openDate}</p>}
                                </div>

                                <div className="flex items-center justify-between">
                                    <div className="flex items-center">
                                        <Lock size={20} className="text-gray-400 mr-2" />
                                        <span className="text-sm text-gray-700">Soukromá kapsle</span>
                                    </div>
                                    <label className="relative inline-flex items-center cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={formData.isPrivate}
                                            onChange={(e) => setFormData({ ...formData, isPrivate: e.target.checked })}
                                            className="sr-only peer"
                                        />
                                        <div className="w-12 h-6 bg-gray-300 rounded-full peer-checked:bg-blue-500 transition-all duration-300"></div>
                                        <div className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all duration-300 peer-checked:translate-x-6"></div>
                                    </label>
                                </div>

                                {/* QR Code */}
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center">
                                        <QrCode size={20} className="text-gray-400 mr-2" />
                                        <span className="text-sm text-gray-700">Otevření QR kódem</span>
                                    </div>
                                    <label className="relative inline-flex items-center cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={formData.hasQRCode}
                                            onChange={(e) => setFormData({ ...formData, hasQRCode: e.target.checked })}
                                            className="sr-only peer"
                                        />
                                        <div className="w-12 h-6 bg-gray-300 rounded-full peer-checked:bg-blue-500 transition-all duration-300"></div>
                                        <div className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all duration-300 peer-checked:translate-x-6"></div>
                                    </label>
                                </div>
                                {/* QR Generator */}
                                {formData.hasQRCode && (
                                    <div className="flex justify-center mt-6">
                                        <QRGenerator randomString={formData.qrcode} />
                                    </div>
                                )}
                                {/* Geolocation */}
                                <div className="flex items-center justify-between mt-4">
                                    <div className="flex items-center">
                                        <MapPin size={20} className="text-gray-400 mr-2" />
                                        <span className="text-sm text-gray-700">Otevření geolokací</span>
                                    </div>
                                    <label className="relative inline-flex items-center cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={formData.hasGeolocation}
                                            onChange={handleGeolocationToggle} // Handle the toggle change
                                            className="sr-only peer"
                                        />
                                        <div className="w-12 h-6 bg-gray-300 rounded-full peer-checked:bg-blue-500 transition-all duration-300"></div>
                                        <div className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all duration-300 peer-checked:translate-x-6"></div>
                                    </label>
                                </div>
                                {/* Google Map - Display only if geolocation is enabled */}
                                {formData.hasGeolocation && (
                                    <div className="mt-4">

                                        <GoogleMap
                                            mapContainerStyle={{ width: '100%', height: '400px' }}
                                            center={mapCenter}
                                            zoom={12}
                                            onClick={handleMapClick}
                                            onLoad={handleMapLoad}
                                        >
                                            <Marker position={formData.geolocation} />
                                        </GoogleMap>
                                    </div>
                                )}
                            </div>
                        )}
                        <div className="bg-white rounded-lg shadow-sm">
                            {step === 2 && (
                                <div className="space-y-6">
                                    <div className="flex flex-col space-y-4">
                                        <h2 className="text-2xl font-bold text-gray-900">
                                            Správa přispěvatelů
                                        </h2>

                                        {/* Input a Error zprávy */}
                                        <div className="flex flex-col space-y-2">
                                            <input
                                                type="email"
                                                placeholder="Zadejte email přispěvatele"
                                                value={emailForm.email}
                                                onChange={(e) => setEmailForm({ ...emailForm, email: e.target.value })}
                                                className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            />

                                            {/* Zobrazení případné chyby */}
                                            {emailErrors.email && (
                                                <p className="text-red-500 text-sm mt-1">{emailErrors.email}</p>
                                            )}

                                            {/* Tlačítko pod inputem */}
                                            <div className="flex items-center justify-start">
                                                <button
                                                    className="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500"
                                                    onClick={handleEmailSubmit}
                                                >
                                                    <Check size={20} className="mr-1" />
                                                    Potvrdit
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    {formData.contributors.map(contributor => (
                                        <Contributor
                                            key={contributor.id}
                                            id={contributor.id}
                                            email={contributor.email}
                                            status={contributor.status}
                                            initial={contributor.initial}
                                            canDelete={true}
                                            onDelete={handleDelete}
                                        />
                                    ))}
                                    <InfoBox
                                        title="Správa přispěvatelů"
                                        description={`Přispěvatelé mohou přidávat obsah do kapsle až do jejího uzavření. Každý přispěvatel může přidat maximálně ${formData.contributorsLimit} souborů.`}
                                    />
                                    <DropdownSelect
                                        label="Limit přispěvatelů"
                                        value={formData.contributorsLimit}
                                        options={['1', '2', '3', '4', '5']}
                                        onChange={newValue => setFormData(prev => ({ ...prev, contributorsLimit: parseInt(newValue, 10) }))}
                                    />

                                </div>
                            )}
                        </div>

                        <div className="bg-white rounded-lg shadow-sm">
                            {step === 3 && (
                                <div className="space-y-6">
                                    <div className="flex flex-col space-y-4">
                                        <h2 className="text-2xl font-bold text-gray-900">
                                            Shrnutí
                                        </h2>
                                        <InfoSection capsule={formData}></InfoSection>
                                        <section className="flex flex-col justify-center mt-6 w-full max-md:max-w-full">
                                            <div className="flex flex-col p-6 w-full bg-gray-50 rounded-lg min-h-[192px] max-md:px-5 max-md:max-w-full">
                                                <h3 className="w-full text-base font-semibold text-gray-900 max-md:max-w-full">Přispěvatelé</h3>
                                                <div className="flex flex-col mt-4 w-full max-md:max-w-full">
                                                    {formData.contributors.map(contributor => (
                                                        <Contributor
                                                            key={contributor.id}
                                                            id={contributor.id}
                                                            email={contributor.email}
                                                            status={contributor.status}
                                                            initial={contributor.initial}
                                                            canDelete={false}
                                                            onDelete={handleDelete}
                                                        />
                                                    ))}
                                                </div>
                                            </div>
                                        </section>
                                    </div>
                                    <Warning />
                                    <Confirmation onClick={handleSubmit} />
                                </div>
                            )}
                        </div>












                        {/* Navigation buttons */}
                        <div className="flex justify-end mt-6">
                            {steps !== 3 && (
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
                                </>
                            )}
                        </div>


                    </div>

                </div>
            </main >
        </div >
    );
};

export default CreateCapsule;
