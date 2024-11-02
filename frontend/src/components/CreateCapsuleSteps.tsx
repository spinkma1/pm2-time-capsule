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
    Calendar
} from 'lucide-react';

const CreateCapsuleSteps = ({ currentStep, capsuleData, onStepComplete }) => {
    const [uploadType, setUploadType] = useState('drag');
    const [showInviteForm, setShowInviteForm] = useState(false);
    const [dragActive, setDragActive] = useState(false);
    const [emailInput, setEmailInput] = useState('');
    const [message, setMessage] = useState('');

    // Mock data pro demonstraci
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
        // Implementace nahrávání souborů
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
                            uploadType === 'drag'
                                ? 'bg-blue-900 text-white'
                                : 'border border-gray-300 text-gray-700'
                        }`}
                    >
                        Nahrát soubory
                    </button>
                    <button
                        onClick={() => setUploadType('text')}
                        className={`px-4 py-2 rounded-lg ${
                            uploadType === 'text'
                                ? 'bg-blue-900 text-white'
                                : 'border border-gray-300 text-gray-700'
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
                        <p className="text-sm text-gray-500 mt-2">
                            Podporované formáty: JPG, PNG, PDF, MP4, MP3
                        </p>
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
                                                onClick={() => {/* Implementace odstranění */}}
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
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                E-mailové adresy
                            </label>
                            <input
                                type="text"
                                value={emailInput}
                                onChange={(e) => setEmailInput(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                placeholder="Zadejte e-mailové adresy oddělené čárkou"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Osobní zpráva (volitelné)
                            </label>
                            <textarea
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                rows="3"
                                placeholder="Napište osobní zprávu pro pozvané přispěvatele..."
                            ></textarea>
                        </div>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setShowInviteForm(false)}
                                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
                            >
                                Zrušit
                            </button>
                            <button className="bg-blue-900 text-white px-4 py-2 rounded-lg hover:bg-blue-800">
                                Odeslat pozvánky
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="space-y-4">
                <div className="flex items-center justify-between">
                    <h3 className="font-medium text-gray-900">Seznam přispěvatelů</h3>
                    <button className="text-sm text-blue-900 hover:underline flex items-center">
                        <Link size={16} className="mr-1" />
                        Kopírovat odkaz pro přispěvatele
                    </button>
                </div>

                {contributors.map(contributor => (
                    <div key={contributor.id} className="bg-gray-50 p-4 rounded-lg flex items-center justify-between">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center">
                                {contributor.name.charAt(0)}
                            </div>
                            <div>
                                <div className="font-medium">{contributor.name}</div>
                                <div className="text-sm text-gray-500">{contributor.email}</div>
                            </div>
                        </div>
                        <div className="flex items-center space-x-3">
              <span className={`px-2 py-1 rounded-full text-sm ${
                  contributor.status === 'accepted'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-yellow-100 text-yellow-800'
              }`}>
                {contributor.status === 'accepted' ? 'Přijato' : 'Čeká na přijetí'}
              </span>
                            <button className="text-gray-400 hover:text-red-500">
                                <X size={20} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            <div className="bg-blue-50 p-4 rounded-lg flex items-start">
                <Info size={20} className="text-blue-900 mr-2 mt-1" />
                <div className="text-sm text-blue-900">
                    <p className="font-medium mb-1">Správa přispěvatelů</p>
                    <p>Přispěvatelé mohou přidávat obsah do kapsle až do jejího uzavření.
                        Každý přispěvatel může přidat maximálně 5 souborů.</p>
                </div>
            </div>
        </div>
    );

    const SummaryStep = () => (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-gray-900">Shrnutí kapsle</h2>

            <div className="grid gap-6">
                <div className="bg-gray-50 p-6 rounded-lg">
                    <h3 className="font-medium text-gray-900 mb-4">Základní informace</h3>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <div className="text-sm text-gray-500">Název kapsle</div>
                            <div className="font-medium">{capsuleData?.title || 'Maturitní vzpomínky 2024'}</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-500">Datum otevření</div>
                            <div className="font-medium">30. června 2025</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-500">Počet souborů</div>
                            <div className="font-medium">{uploadedFiles.length}</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-500">Počet přispěvatelů</div>
                            <div className="font-medium">{contributors.length}</div>
                        </div>
                    </div>
                </div>

                <div className="bg-gray-50 p-6 rounded-lg">
                    <h3 className="font-medium text-gray-900 mb-4">Nahrané soubory</h3>
                    <div className="space-y-3">
                        {uploadedFiles.map(file => (
                            <div key={file.id} className="flex items-center justify-between p-3 bg-white rounded-lg">
                                <div className="flex items-center space-x-3">
                                    {getFileIcon(file.type)}
                                    <div>
                                        <div className="font-medium">{file.name}</div>
                                        <div className="text-sm text-gray-500">{file.size}</div>
                                    </div>
                                </div>
                                <Check size={20} className="text-green-500" />
                            </div>
                        ))}
                    </div>
                </div>

                <div className="bg-gray-50 p-6 rounded-lg">
                    <h3 className="font-medium text-gray-900 mb-4">Přisp<h3 className="font-medium text-gray-900 mb-4">Přispěvatelé</h3>
                        <div className="space-y-3">
                            {contributors.map(contributor => (
                                <div key={contributor.id} className="flex items-center justify-between p-3 bg-white rounded-lg">
                                    <div className="flex items-center space-x-3">
                                        <div className="w-8 h-8 bg-blue-900 text-white rounded-full flex items-center justify-center">
                                            {contributor.name.charAt(0)}
                                        </div>
                                        <div>
                                            <div className="font-medium">{contributor.name}</div>
                                            <div className="text-sm text-gray-500">{contributor.email}</div>
                                        </div>
                                    </div>
                                    <span className={`px-2 py-1 rounded-full text-sm ${
                                        contributor.status === 'accepted'
                                            ? 'bg-green-100 text-green-800'
                                            : 'bg-yellow-100 text-yellow-800'
                                    }`}>
                    {contributor.status === 'accepted' ? 'Přijato' : 'Čeká na přijetí'}
                  </span>
                                </div>
                            ))}
                        </div>
                </div>

                <div className="bg-blue-50 p-6 rounded-lg">
                    <h3 className="font-medium text-blue-900 mb-4 flex items-center">
                        <Calendar size={20} className="mr-2" />
                        Časová osa
                    </h3>
                    <div className="space-y-4">
                        <div className="flex items-center">
                            <div className="w-8 h-8 rounded-full bg-blue-200 flex items-center justify-center">
                                <Check size={16} className="text-blue-900" />
                            </div>
                            <div className="ml-3">
                                <div className="font-medium">Vytvoření kapsle</div>
                                <div className="text-sm text-gray-500">15. ledna 2024</div>
                            </div>
                        </div>
                        <div className="w-px h-8 bg-blue-200 ml-4"></div>
                        <div className="flex items-center">
                            <div className="w-8 h-8 rounded-full bg-blue-900 flex items-center justify-center">
                                <Lock size={16} className="text-white" />
                            </div>
                            <div className="ml-3">
                                <div className="font-medium">Uzavření kapsle</div>
                                <div className="text-sm text-gray-500">29. června 2025</div>
                            </div>
                        </div>
                        <div className="w-px h-8 bg-blue-200 ml-4"></div>
                        <div className="flex items-center opacity-50">
                            <div className="w-8 h-8 rounded-full bg-blue-200 flex items-center justify-center">
                                <Unlock size={16} className="text-blue-900" />
                            </div>
                            <div className="ml-3">
                                <div className="font-medium">Otevření kapsle</div>
                                <div className="text-sm text-gray-500">30. června 2025</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="bg-yellow-50 p-6 rounded-lg mt-6">
                    <div className="flex items-start">
                        <AlertCircle size={24} className="text-yellow-700 mr-3 mt-1" />
                        <div>
                            <h4 className="font-medium text-yellow-900 mb-2">Před uzavřením kapsle zkontrolujte:</h4>
                            <ul className="space-y-2 text-yellow-800">
                                <li className="flex items-center">
                                    <Check size={16} className="mr-2" />
                                    Všechny soubory byly úspěšně nahrány
                                </li>
                                <li className="flex items-center">
                                    <Check size={16} className="mr-2" />
                                    Všichni přispěvatelé byli pozváni
                                </li>
                                <li className="flex items-center">
                                    <Check size={16} className="mr-2" />
                                    Datum otevření je správně nastaveno
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div className="mt-8 p-6 bg-gray-50 rounded-lg">
                    <h3 className="font-medium text-gray-900 mb-4">Finální potvrzení</h3>
                    <div className="space-y-4">
                        <div className="flex items-start">
                            <input
                                type="checkbox"
                                id="confirmCreation"
                                className="mt-1 h-4 w-4 text-blue-900 rounded border-gray-300 focus:ring-blue-900"
                            />
                            <label htmlFor="confirmCreation" className="ml-3 text-gray-700">
                                Potvrzuji, že jsem zkontroloval(a) všechny nahrané soubory a nastavení kapsle.
                                Rozumím tomu, že po uzavření kapsle nebude možné přidávat další obsah.
                            </label>
                        </div>
                        <button className="w-full bg-blue-900 text-white py-3 rounded-lg hover:bg-blue-800 flex items-center justify-center">
                            <Lock size={20} className="mr-2" />
                            Uzavřít a finalizovat kapsli
                        </button>
                    </div>
                </div>
            </div>
        </div>
</div>
);

    // Render appropriate step content
    const renderStepContent = () => {
        switch (currentStep) {
            case 2:
                return <ContentUploadStep />;
            case 3:
                return <ContributorsStep />;
            case 4:
                return <SummaryStep />;
            default:
                return null;
        }
    };

    return (
        <div className="max-w-4xl mx-auto">
            {renderStepContent()}
        </div>
    );
};

export default CreateCapsuleSteps;