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
            console.log('Form submitted', formData);
            setCurrentPage('dashboard');
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
                                        <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-900 rounded-full peer dark:bg-gray-300 peer-checked:bg-blue-600 transition-all duration-200 ease-in-out">
                                            <span className="absolute left-0.5 top-0.5 w-5 h-5 bg-white rounded-full transition-all duration-200 ease-in-out"></span>
                                        </div>
                                    </label>
                                </div>
                            </div>
                        )}

                        {step === 2 && (
                            <div className="space-y-6">
                                <h2 className="text-2xl font-bold text-gray-900">Přidat obsah</h2>

                                <div className="border border-gray-300 rounded-lg p-4">
                                    <h3 className="text-lg font-semibold text-gray-800">Nahrajte soubory</h3>
                                    <div
                                        className={`flex items-center justify-center h-32 border-2 border-dashed rounded-lg ${dragActive ? 'border-blue-500' : 'border-gray-300'} cursor-pointer`}
                                        onDragEnter={handleDrag}
                                        onDragOver={handleDrag}
                                        onDragLeave={handleDrag}
                                        onDrop={handleDrop}
                                        onClick={() => document.getElementById('fileUpload').click()}
                                    >
                                        <Upload size={40} className="text-gray-400" />
                                        <p className="text-gray-600">Přetáhněte soubory sem nebo klikněte pro výběr</p>
                                        <input
                                            type="file"
                                            id="fileUpload"
                                            multiple
                                            onChange={(e) => {
                                                const files = Array.from(e.target.files);
                                                setFormData(prev => ({
                                                    ...prev,
                                                    files: [...prev.files, ...files.map(file => ({
                                                        id: Date.now() + Math.random(),
                                                        name: file.name,
                                                        type: file.type.split('/')[0],
                                                        size: file.size,
                                                        file: file
                                                    }))]
                                                }));
                                                e.target.value = null; // Clear input
                                            }}
                                            className="hidden"
                                        />
                                    </div>

                                    <div className="mt-4">
                                        {formData.files.map(file => (
                                            <div key={file.id} className="flex items-center justify-between py-2 border-b">
                                                <div className="flex items-center">
                                                    {getFileIcon(file.type)}
                                                    <span className="ml-2 text-gray-700">{file.name}</span>
                                                </div>
                                                <button onClick={() => removeFile(file.id)} className="text-red-600 hover:underline">Odebrat</button>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Add additional steps here for collaborators and summary */}
                        
                        {/* Navigation buttons */}
                        <div className="mt-6 flex justify-between">
                            {step > 1 && (
                                <button onClick={() => setStep(step - 1)} className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400">Zpět</button>
                            )}
                            {step < steps.length ? (
                                <button onClick={handleNext} className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Další</button>
                            ) : (
                                <button onClick={handleSubmit} className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Odeslat</button>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default CreateCapsule;
