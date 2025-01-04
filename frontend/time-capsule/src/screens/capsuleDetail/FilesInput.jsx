import React, {useEffect, useState} from 'react';
import { File, Image, Video, FileText, Trash2, ArrowLeft, Upload } from 'lucide-react';
import {useLocation, useNavigate} from 'react-router-dom';
import {ApiService as api} from "../../api/api.js";

const FileUpload = ({ capsule, setSelectedCapsule }) => {
    const navigate = useNavigate();
    const [files, setFiles] = useState([]);
    const [uploadType, setUploadType] = useState('drag');
    const [dragActive, setDragActive] = useState(false);
    const [message, setMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const location = useLocation();
    const { selectedCapsule } = location.state || {};

    useEffect(() => {
        if (selectedCapsule) {
            setSelectedCapsule(selectedCapsule);
        }
    }, [selectedCapsule, setSelectedCapsule]);

    // Allowed file types
    const allowedFileTypes = [
        'application/pdf', // PDF
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // DOCX
        'video/mp4', // MP4
        'audio/mp3', // MP3
        'text/plain', // TXT
        'image/jpeg', // JPG and JPEG
    ];

    // Validate file format
    const isFileTypeValid = (file) => allowedFileTypes.includes(file.type);

    // Validate maximum item count
    const isWithinMaxItemsLimit = (newFilesCount) => {
        return capsule.content.length + files.length + newFilesCount <= capsule.capsuleSize;
    };

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(e.type === "dragenter" || e.type === "dragover");
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);
        
        const droppedFiles = Array.from(e.dataTransfer.files);
        const validFiles = droppedFiles.filter(isFileTypeValid);
        const invalidFiles = droppedFiles.filter((file) => !isFileTypeValid(file));
        
        if (!isWithinMaxItemsLimit(validFiles.length)) {
            setErrorMessage(`Překročen maximální počet položek: ${capsule.capsuleSize}.`);
            return;
        }

        if (invalidFiles.length > 0) {
            setErrorMessage("Tento formát není podporován.");
        } else {
            setErrorMessage('');
        }

        setFiles((prevFiles) => [...prevFiles, ...validFiles]);
    };

    const handleFileUpload = (e) => {
        const selectedFiles = Array.from(e.target.files);
        const validFiles = selectedFiles.filter(isFileTypeValid);
        const invalidFiles = selectedFiles.filter((file) => !isFileTypeValid(file));

        if (!isWithinMaxItemsLimit(validFiles.length)) {
            setErrorMessage(`Překročen maximální počet položek: ${capsule.capsuleSize}.`);
            return;
        }

        if (invalidFiles.length > 0) {
            setErrorMessage("Tento formát není podporován.");
        } else {
            setErrorMessage('');
        }

        setFiles((prevFiles) => [...prevFiles, ...validFiles]);
    };

    const removeFile = (index) => {
        setFiles(files.filter((_, i) => i !== index));
    };

    const getFileIcon = (file) => {
        const fileType = file.type;

        if (fileType.startsWith("image/")) return <Image className="text-blue-500" />;
        if (fileType.startsWith("video/")) return <Video className="text-red-500" />;
        if (fileType === "application/pdf") return <File className="text-gray-500" />;
        if (fileType.startsWith("text/") || fileType.includes("word")) return <FileText className="text-green-500" />;

        return <File className="text-gray-400" />;
    };

    const formatFileSize = (size) => {
        if (size < 1024) return `${size} B`;
        if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
        return `${(size / (1024 * 1024)).toFixed(1)} MB`;
    };

    const handleContentSubmit = async () => {
        if (files.length === 0 && !message) {
            setErrorMessage("Musíte nahrát soubor");
            return;
        }

        const fileToBase64 = (file) =>
            new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => resolve(reader.result.split(',')[1]); // Get base64 content
                reader.onerror = (error) => reject(error);
                reader.readAsDataURL(file);
            });

        try {
            for (const file of files) {
                // Convert the file to Base64
                const base64Data = await fileToBase64(file);

                // Map the file type to DataType (ensure this function is implemented)
                const dataType = mapToDataType(file.type);

                // Construct the ContentDto object
                const contentDto = {
                    dataType,
                    dateOfUpload: new Date().toISOString(),
                    name: file.name,
                    url: "", // Optional if files are stored in the database directly
                    data: base64Data,
                };

                // Log the ContentDto to console
                console.log("Uploading ContentDto:", contentDto);

                // Make the API call to upload the content
                const response = await api.uploadContentToCapsule(capsule.id, contentDto);

                // Log the response
                console.log("Response from server:", response);
            }

            // If a message is provided, handle it separately (optional)
            if (message) {
                console.log("Message to include:", message);
                // You can make an API call for the message or handle it as needed
            }

            // Success feedback to the user (optional)
            console.log("All files uploaded successfully!");
        } catch (error) {
            console.error("Error uploading files:", error);
        }
    };

    const mapToDataType = (fileType) => {
        if (fileType === "application/pdf") return "PDF";
        if (fileType.startsWith("video/")) return "VIDEO";
        if (fileType.startsWith("audio/")) return "AUDIO";
        if (fileType.startsWith("image/")) return "IMAGE";
        if (fileType.startsWith("text/plain")) return "PLAIN_TEXT";
        return null; // Return null or handle unsupported types
    };

    const ContentUploadStep = () => (
        <div className="space-y-6">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Přidat obsah do kapsle</h2>
            </div>
            {uploadType === 'drag' ? (
                <div>
                    <div
                        onDragEnter={handleDrag}
                        onDragLeave={handleDrag}
                        onDragOver={handleDrag}
                        onDrop={handleDrop}
                        className={`border-2 border-dashed rounded-lg p-8 text-center ${dragActive ? 'border-blue-900 bg-blue-50' : 'border-gray-300'}`}
                    >
                        <Upload size={32} className="mx-auto mb-4 text-gray-400" />
                        <p className="text-gray-600 mb-2">
                            Přetáhněte soubory sem nebo
                        </p>
                        <label className="text-blue-900 font-medium hover:underline cursor-pointer">
                            vyberte ze zařízení
                            <input type="file" multiple onChange={handleFileUpload} className="hidden" accept=".pdf, .docx, .mp4, .mp3, .txt, .jpeg, .jpg" />
                        </label>
                        <p className="text-sm text-gray-500 mt-2">
                            Podporované formáty: PDF, DOCX, MP4, MP3, JPEG, JPG, TXT
                        </p>
                        {errorMessage && <p className="text-red-500 mt-2">{errorMessage}</p>}
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
            {files.length > 0 && (
                <div className="mt-8">
                    <h3 className="font-medium text-gray-900 mb-4">Nahrané soubory</h3>
                    <div className="space-y-3">
                        {files.map((file, index) => (
                            <div key={index} className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center space-x-3">
                                        {getFileIcon(file)}
                                        <div>
                                            <div className="font-medium">{file.name}</div>
                                            <div className="text-sm text-gray-500">{formatFileSize(file.size)}</div>
                                        </div>
                                    </div>
                                    <button
                                        onClick={() => removeFile(index)}
                                        className="text-gray-400 hover:text-red-500"
                                    >
                                        <Trash2 size={20} />
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50">
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
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <ContentUploadStep />
                        <div className="flex justify-end mt-6">
                            <button
                                onClick={() => {
                                    navigate(`/capsuleDetail/${capsule.id}`);
                                }}
                                className="px-6 py-2 mx-6 text-base text-center text-black bg-white rounded-lg border border-solid border-neutral-700 hover:bg-gray-200"
                            >
                                Zpět
                            </button>
                            <button
                                onClick={() => handleContentSubmit()}
                                className="px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                            >
                                Pokračovat
                            </button>
                        </div>
                    </div>
                </div>
            </main>                           
        </div>
    );
};

export default FileUpload;


