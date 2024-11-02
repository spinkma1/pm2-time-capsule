import React, { useState } from 'react';
import {
    ArrowLeft,
    Calendar,
    Upload,
    Users,
    Lock,
    Image as ImageIcon,
    Video,
    FileText,
    Music,
    X,
    Check,
    Info
} from 'lucide-react';

const CreateCapsule = ({ setCurrentPage }) => {
    const [step, setStep] = useState(1);
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        openDate: '',
        isPrivate: true,
        contributorsLimit: 5,
        files: [],
        collaborators: []
    });

    const [dragActive, setDragActive] = useState(false);
    const [errors, setErrors] = useState({});

    const steps = [
        { number: 1, title: 'Základní informace' },
        { number: 2, title: 'Přidat obsah' },
        { number: 3, title: 'Přispěvatelé' },
        { number: 4, title: 'Shrnutí' }
    ];

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === "dragenter" || e.type === "dragover") {
            setDragActive(true);
        } else {
            setDragActive(false);
        }
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);

        // Zde by byla implementace nahrávání souborů
        const droppedFiles = Array.from(e.dataTransfer.files);
        setFormData(prev => ({
            ...prev,
            files: [...prev.files, ...droppedFiles.map(file => ({
                id: Date.now(),
                name: file.name,
                type: file.type.split('/')[0],
                size: file.size,
                file: file
            }))]
        }));
    };

    const validateStep = (currentStep) => {
        const newErrors = {};

        if (currentStep === 1) {
            if (!formData.title.trim()) {
                newErrors.title = 'Název kapsle je povinný';
            }
            if (!formData.openDate) {
                newErrors.openDate = 'Datum otevření je povinné';
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
            console.log('Form submitted', formData);
            setCurrentPage('dashboard');
        }
    };

    const removeFile = (fileId) => {
        setFormData(prev => ({
            ...prev,
            files: prev.files.filter(file => file.id !== fileId)
        }));
    };

    const getFileIcon = (type) => {
        switch(type) {
            case 'image': return <ImageIcon size={20} />;
            case 'video': return <Video size={20} />;
            case 'text': return <FileText size={20} />;
            case 'audio': return <Music size={20} />;
            default: return <FileText size={20} />;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={() => setCurrentPage('dashboard')}
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
                    <div className="mb-8">
                        <div className="flex justify-between items-center">
                            {steps.map((s, index) => (
                                <div key={s.number} className="flex items-center">
                                    <div className={`
                    flex items-center justify-center w-8 h-8 rounded-full
                    ${step >= s.number ? 'bg-blue-900 text-white' : 'bg-gray-200 text-gray-600'}
                  `}>
                                        {step > s.number ? <Check size={16} /> : s.number}
                                    </div>
                                    <div className="ml-2 text-sm">
                                        {s.title}
                                    </div>
                                    {index < steps.length - 1 && (
                                        <div className={`
                      w-24 h-1 mx-4
                      ${step > s.number ? 'bg-blue-900' : 'bg-gray-200'}
                    `}></div>
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
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Název kapsle*
                                    </label>
                                    <input
                                        type="text"
                                        value={formData.title}
                                        onChange={(e) => setFormData({...formData, title: e.target.value})}
                                        className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                            errors.title ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="Např. Maturitní vzpomínky 2024"
                                    />
                                    {errors.title && <p className="text-red-500 text-sm mt-1">{errors.title}</p>}
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Popis kapsle
                                    </label>
                                    <textarea
                                        value={formData.description}
                                        onChange={(e) => setFormData({...formData, description: e.target.value})}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                        rows="3"
                                        placeholder="Popište, co bude kapsle obsahovat..."
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Datum otevření*
                                    </label>
                                    <div className="relative">
                                        <Calendar size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                        <input
                                            type="date"
                                            value={formData.openDate}
                                            onChange={(e) => setFormData({...formData, openDate: e.target.value})}
                                            className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                                errors.openDate ? 'border-red-500' : 'border-gray-300'
                                            }`}
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
                                            onChange={(e) => setFormData({...formData, isPrivate: e.target.checked})}
                                            className="sr-only peer"
                                        />
                                        <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-900"></div>
                                    </label>
                                </div>
                            </div>
                        )}

                        {step === 2 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Přidat obsah</h2>

                                <div
                                    onDragEnter={handleDrag}
                                    onDragLeave={handleDrag}
                                    onDragOver={handleDrag}
                                    onDrop={handleDrop}
                                    className={`
                    border-2 border-dashed rounded-lg p-8 text-center
                    ${dragActive ? 'border-blue-900 bg-blue-50' : 'border-gray-300'}
                  `}
                                >
                                    <Upload size={32} className="mx-auto mb-4 text-gray-400" />
                                    <p className="text-gray-600 mb-2">
                                        Přetáhněte soubory sem nebo
                                    </p>
                                    <button className="text-blue-900 font-medium hover:underline">
                                        vyberte ze zařízení
                                    </button>
                                </div>

                                {formData.files.length > 0 && (
                                    <div className="space-y-3">
                                        <h3 className="font-medium text-gray-900">Nahrané soubory</h3>
                                        {formData.files.map(file => (
                                            <div key={file.id} className="flex items-center justify-between bg-gray-50 p-3 rounded-lg">
                                                <div className="flex items-center">
                                                    {getFileIcon(file.type)}
                                                    <span className="ml-2">{file.name}</span>
                                                </div>
                                                <button
                                                    onClick={() => removeFile(file.id)}
                                                    className="text-gray-400 hover:text-red-500"
                                                >
                                                    <X size={20} />
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}

                        {step === 3 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Přispěvatelé</h2>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Limit příspěvků na osobu
                                    </label>
                                    <input
                                        type="number"
                                        value={formData.contributorsLimit}
                                        onChange={(e) => setFormData({...formData, contributorsLimit: parseInt(e.target.value)})}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                        min="1"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Pozvat přispěvatele
                                    </label>
                                    <div className="flex space-x-2">
                                        <input
                                            type="email"
                                            className="flex-grow px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                            placeholder="E-mailová adresa"
                                        />
                                        <button className="bg-blue-900 text-white px-4 py-2 rounded-lg hover:bg-blue-800">
                                            Pozvat
                                        </button>
                                    </div>
                                </div>

                                <div className="bg-blue-50 p-4 rounded-lg flex items-start">
                                    <Info size={20} className="text-blue-900 mr-2 mt-1" />
                                    <p className="text-sm text-blue-900">
                                        Přispěvatelé budou moci nahrávat obsah do kapsle až do jejího uzavření.
                                        Po uzavření kapsle už nebude možné přidávat další obsah.
                                    </p>
                                </div>
                            </div>
                        )}

                        {step === 4 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Shrnutí</h2>

                                <div className="space-y-4">
                                    <div className="bg-gray-50 p-4 rounded-lg">
                                        <h3 className="font-medium text-gray-900 mb-2">Základní informace</h3>
                                        <div className="grid grid-cols-2 gap-2 text-sm">
                                            <div className="text-gray-600">Název:</div>
                                            <div>{formData.title}</div>
                                            <div className="text-gray-600">Datum otevření:</div>
                                            <div>{formData.openDate}</div>
                                            <div className="text-gray-600">Viditelnost:</div>
                                            <div>{formData.isPrivate ? 'Soukromá' : 'Veřejná'}</div>
                                        </div>
                                    </div>

                                    <div className="bg-gray-50 p-4 rounded-lg">
                                        <h3 className="font-medium text-gray-900 mb-2">Obsah</h3>
                                        <div>{formData.files.length} souborů</div>
                                    </div>

                                    <div className="bg-gray-50 p-4 rounded-lg">
                                        <h3 className="font-medium text-gray-900 mb-2">Přispěvatelé</h3>
                                        <div>Limit {formData.contributorsLimit} příspěvků na osobu</div>
                                    </div>
                                </div>

                                <div className="bg-blue-50 p-4 rounded-lg flex items-start">
                                    <Info size={20} className="text-blue-900 mr-2 mt-1" />
                                    <p className="text-sm text-blue-900">
                                        Po vytvoření kapsle budete moci dál přidávat obsah a zvát přispěvatele
                                        až do data uzavření. Obsah kapsle bude dostupný až v den otevření.
                                    </p>
                                </div>
                            </div>
                        )}

                        {/* Navigation buttons */}
                        <div className="mt-8 flex justify-between">
                            {step > 1 && (
                                <button
                                    onClick={() => setStep(step - 1)}
                                    className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
                                >
                                    Zpět
                                </button>
                            )}
                            {step < 4 ? (
                                <button
                                    onClick={handleNext}
                                    className="ml-auto px-6 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                >
                                    Pokračovat
                                </button>
                            ) : (
                                <button
                                    onClick={handleSubmit}
                                    className="ml-auto px-6 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                >
                                    Vytvořit kapsli
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default CreateCapsule;