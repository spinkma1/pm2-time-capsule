import React, { useState } from 'react';
import {
    Upload,
    Users,
    Image as ImageIcon,
    Video,
    FileText,
    Music,
    X,
    Check,
    Info,
    Link,
    Mail,
    Plus,
    AlertCircle,
    Calendar,
    Lock,
    Unlock
} from 'lucide-react';

const CreateCapsuleSteps = ({ currentStep, capsuleData, onStepComplete }) => {
    const [uploadType, setUploadType] = useState('drag');
    const [showInviteForm, setShowInviteForm] = useState(false);
    const [dragActive, setDragActive] = useState(false);
    const [emailInput, setEmailInput] = useState('');
    const [message, setMessage] = useState('');

    // Mock data for demonstration
    const [uploadedFiles, setUploadedFiles] = useState([
        { id: 1, name: 'rodinne_foto.jpg', type: 'image', size: '2.4 MB', status: 'complete' },
        { id: 2, name: 'vzpominka.mp4', type: 'video', size: '15.8 MB', status: 'uploading', progress: 65 },
        { id: 3, name: 'dopis.pdf', type: 'text', size: '156 KB', status: 'complete' }
    ]);

    const [contributors, setContributors] = useState([
        { id: 1, name: 'Jana Nováková', email: 'jana@email.cz', status: 'accepted', avatar: null },
        { id: 2, name: 'Petr Svoboda', email: 'petr@email.cz', status: 'pending', avatar: null }
    ]);

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(e.type === "dragenter" || e.type === "dragover");
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);
        // Implement file upload logic here
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

    const ContentUploadStep = () => (
        <div className="space-y-6">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Přidat obsah do kapsle</h2>
                <div className="flex space-x-2">
                    <button
                        onClick={() => setUploadType('drag')}
                        className={`px-4 py-2 rounded-lg ${
                            uploadType === 'drag' ? 'bg-blue-900 text-white' : 'border border-gray-300 text-gray-700'
                        }`}
                    >
                        Nahrát soubory
                    </button>
                    <button
                        onClick={() => setUploadType('text')}
                        className={`px-4 py-2 rounded-lg ${
                            uploadType === 'text' ? 'bg-blue-900 text-white' : 'border border-gray-300 text-gray-700'
                        }`}
                    >
                        Napsat zprávu
                    </button>
                </div>
            </div>

            {uploadType === 'drag' ? (
                <div>
                    <div
                        onDragEnter={handleDrag}
                        onDragLeave={handleDrag}
                        onDragOver={handleDrag}
                        onDrop={handleDrop}
                        className={`border-2 border-dashed rounded-lg p-8 text-center ${
                            dragActive ? 'border-blue-900 bg-blue-50' : 'border-gray-300'
                        }`}
                    >
                        <Upload size={32} className="mx-auto mb-4 text-gray-400" />
                        <p className="text-gray-600 mb-2">Přetáhněte soubory sem nebo</p>
                        <button className="text-blue-900 font-medium hover:underline">
                            vyberte ze zařízení
                        </button>
                        <p className="text-sm text-gray-500 mt-2">Podporované formáty: JPG, PNG, PDF, MP4, MP3</p>
                    </div>
                </div>
            ) : (
                <div className="space-y-4">
                    <textarea
                        placeholder="Napište svoji zprávu..."
                        className="w-full h-40 px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                    />
                    <div className="flex justify-end">
                        <button className="bg-blue-900 text-white px-6 py-2 rounded-lg hover:bg-blue-800">
                            Přidat zprávu
                        </button>
                    </div>
                </div>
            )}

            {uploadedFiles.length > 0 && (
                <div className="mt-8">
                    <h3 className="font-medium text-gray-900 mb-4">Nahrané soubory</h3>
                    <div className="space-y-3">
                        {uploadedFiles.map(file => (
                            <div key={file.id} className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center space-x-3">
                                        {getFileIcon(file.type)}
                                        <div>
                                            <div className="font-medium">{file.name}</div>
                                            <div className="text-sm text-gray-500">{file.size}</div>
                                        </div>
                                    </div>
                                    {file.status === 'uploading' ? (
                                        <div className="w-32">
                                            <div className="h-2 bg-gray-200 rounded-full">
                                                <div
                                                    className="h-2 bg-blue-900 rounded-full"
                                                    style={{ width: `${file.progress}%` }}
                                                ></div>
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="flex items-center space-x-3">
                                            <Check size={20} className="text-green-500" />
                                            <button
                                                onClick={() => {/* Implement deletion logic here */}}
                                                className="text-gray-400 hover:text-red-500"
                                            >
                                                <X size={20} />
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );

    const ContributorsStep = () => (
        <div className="space-y-6">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Správa přispěvatelů</h2>
                <button
                    onClick={() => setShowInviteForm(!showInviteForm)}
                    className="bg-blue-900 text-white px-4 py-2 rounded-lg hover:bg-blue-800 flex items-center"
                >
                    <Plus size={20} className="mr-2" />
                    Pozvat přispěvatele
                </button>
            </div>

            {showInviteForm && (
                <div className="bg-gray-50 p-6 rounded-lg mb-6">
                    <h3 className="font-medium text-gray-900 mb-4">Pozvat nové přispěvatele</h3>
                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">E-mailové adresy</label>
                            <input
                                type="text"
                                value={emailInput}
                                onChange={(e) => setEmailInput(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                placeholder="Zadejte e-mailové adresy oddělené čárkou"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Osobní zpráva (volitelné)</label>
                            <textarea
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                rows="3"
                                placeholder="Napište osobní zprávu pro pozvané přispěvatele"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                            />
                        </div>
                        <div className="flex justify-end">
                            <button className="bg-blue-900 text-white px-4 py-2 rounded-lg hover:bg-blue-800">
                                Odeslat pozvánku
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="mt-4">
                <h3 className="font-medium text-gray-900 mb-4">Seznam přispěvatelů</h3>
                <div className="space-y-3">
                    {contributors.map(contributor => (
                        <div key={contributor.id} className="bg-gray-50 p-4 rounded-lg">
                            <div className="flex justify-between items-center">
                                <div className="flex items-center space-x-3">
                                    <div className="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center text-gray-700 font-medium">
                                        {contributor.avatar ? <img src={contributor.avatar} alt={contributor.name} className="rounded-full" /> : contributor.name[0]}
                                    </div>
                                    <div>
                                        <div className="font-medium">{contributor.name}</div>
                                        <div className="text-sm text-gray-500">{contributor.email}</div>
                                    </div>
                                </div>
                                <div>
                                    {contributor.status === 'accepted' ? (
                                        <span className="text-green-500 font-medium">Přijato</span>
                                    ) : (
                                        <span className="text-yellow-500 font-medium">Čeká na přijetí</span>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );

    const SummaryStep = () => (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-gray-900">Shrnutí</h2>
            <div>
                <h3 className="text-lg font-semibold text-gray-800">Přidaný obsah:</h3>
                <ul className="list-disc list-inside">
                    {uploadedFiles.map(file => (
                        <li key={file.id}>{file.name}</li>
                    ))}
                </ul>
            </div>
            <div>
                <h3 className="text-lg font-semibold text-gray-800">Přispěvatelé:</h3>
                <ul className="list-disc list-inside">
                    {contributors.map(contributor => (
                        <li key={contributor.id}>{contributor.name}</li>
                    ))}
                </ul>
            </div>
        </div>
    );

    return (
        <div className="p-6 bg-white rounded-lg shadow-md">
            {currentStep === 'content' && <ContentUploadStep />}
            {currentStep === 'contributors' && <ContributorsStep />}
            {currentStep === 'summary' && <SummaryStep />}
            <div className="flex justify-end mt-6">
                <button
                    onClick={onStepComplete}
                    className="bg-blue-900 text-white px-4 py-2 rounded-lg hover:bg-blue-800"
                >
                    Pokračovat
                </button>
            </div>
        </div>
    );
};

export default CreateCapsuleSteps;
