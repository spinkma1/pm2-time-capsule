import React, { useState } from 'react';
import {
    Lock,
    ArrowLeft,
    Calendar,
    Users,
    Plus,
    Image,
    FileText,
    Video,
    Music,
    Unlock,
    ChevronRight
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import ConfirmPopup from './ConfirmPopup';
import { useParams } from 'react-router-dom';
import {ApiService as api} from "../../api/api.js";


const CapsuleDetail = ({  }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [showContributors, setShowContributors] = useState(false);
    const [isPopupOpen, setIsPopupOpen] = useState(false);

    const capsule = {
        id: "1",
        title: "Moje Digitální Časová Kapsle",
        description: "Sbírka vzpomínek a dokumentů z mých cest.",
        createdDate: "2024-12-01T10:00:00Z",
        status: "editing", // Options: 'editing', 'closed', 'opened'
        openDate: "2025-12-31T10:00:00Z",
        maxItems: 10,
        contributors: [
            { id: 1, avatar: "A", email: "creator@example.com" },
            { id: 2, avatar: "B", email: "contributor1@example.com" },
            { id: 3, avatar: "C", email: "contributor2@example.com" },
        ],
        items: [
            {
                id: "item1",
                type: "image",
                title: "Fotografie z dovolené",
                thumbnail: "https://via.placeholder.com/150",
                addedBy: "creator@example.com",
                addedDate: "2024-12-02T14:30:00Z",
            },
            {
                id: "item2",
                type: "text",
                title: "Deník z výletu",
                addedBy: "contributor1@example.com",
                addedDate: "2024-12-03T09:00:00Z",
            },
            {
                id: "item3",
                type: "video",
                title: "Rodinné video",
                thumbnail: "https://via.placeholder.com/150",
                addedBy: "creator@example.com",
                addedDate: "2024-12-04T16:45:00Z",
            },
            {
                id: "item4",
                type: "audio",
                title: "Oblíbená píseň",
                addedBy: "contributor2@example.com",
                addedDate: "2024-12-05T11:15:00Z",
            },
        ],
    };


    const handleNavigateToFollower = (id) => {
        navigate(`/user/${id}`);
    };

    const getItemIcon = (type) => {
        switch (type) {
            case 'image': return <Image size={20} />;
            case 'video': return <Video size={20} />;
            case 'text': return <FileText size={20} />;
            case 'audio': return <Music size={20} />;
            default: return <FileText size={20} />;
        }
    };

    const getTimeRemaining = (openDate) => {
        const now = new Date();
        const open = new Date(openDate);
        const diff = open - now;
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        if (days <= 0) {
            return "Otevřít";
        }
        return `${days} dní`;
    };


    const handleEarlyOpen = () => {
        setCapsuleStatus('opened');
    };
    const handleLockCapsule = () => {
        api.lockCapsule(id);
        setIsPopupOpen(false); // Zavřít popup po potvrzení
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <button
                            onClick={() => navigate('/dashboard')}
                            className="flex items-center text-gray-600 hover:text-blue-900"
                        >
                            <ArrowLeft size={20} className="mr-2" />
                            Zpět na přehled
                        </button>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* Capsule Header */}
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-4">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900 mb-2">{capsule.title}</h1>
                            <p className="text-gray-600 mb-4">{capsule.description}</p>
                            <div className="flex flex-col sm:flex-row items-start sm:items-center space-x-0 sm:space-x-4 text-sm text-gray-600">
                                <div className="flex items-center mb-2 sm:mb-0">
                                    <Calendar size={16} className="mr-1" />
                                    Vytvořeno {new Date(capsule.createdDate).toLocaleDateString()}
                                </div>
                                <div className="flex items-center mb-2 sm:mb-0">
                                    <Users size={16} className="mr-1" />
                                    {capsule.contributors.length} přispěvatelů

                                </div>
                            </div>
                        </div>
                        {capsule.status === 'closed' ? ( //////////////////// NORMAL STATE
                            <div
                                className="bg-blue-50 rounded-lg p-4 text-center mt-4 md:mt-0 cursor-pointer" // Přidání cursor-pointer pro indikaci kliknutí
                                onClick={() => navigate(`/capsule/open/${capsule.id}`)}
                            >
                                <div className="flex items-center justify-center mb-2">
                                    <Lock size={20} className="text-blue-900" />
                                </div>
                                <div className="text-sm font-medium text-blue-900 mb-1">
                                    {getTimeRemaining(capsule.openDate) === "Otevřít"
                                        ? "Otevřít"
                                        : `Zbývá ${getTimeRemaining(capsule.openDate)}`}
                                </div>

                            </div>) : (<></>)}

                        {capsule.status === 'opened' ? ( //////////////////// NORMAL STATE
                            <div className="bg-blue-50 rounded-lg p-4 text-center mt-4 md:mt-0">
                                <div className="flex items-center justify-center mb-2">
                                    <Unlock size={20} className="text-blue-900" />
                                </div>
                                <div className="text-sm font-medium text-blue-900 mb-1">
                                    Otevřeno
                                </div>
                            </div>) : (<></>)}
                    </div>

                    {/* Progress bar */}
                    {capsule.status === 'closed' ? (
                        <div className="w-full bg-gray-200 rounded-full h-2 mb-4">
                            <div
                                className="bg-blue-900 rounded-full h-2"
                                style={{ width: '60%' }}
                            ></div>
                        </div>)
                        : (<></>)}


                    {/* Action buttons */}
                    {capsule.status === 'editing' ? (
                        <div className="flex flex-col sm:flex-row sm:space-x-4 mb-6">
                            {capsule.status === 'editing' && capsule.items.length < capsule.maxItems ? (
                                <button
                                    className="flex items-center mb-4 sm:mb-0 px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                    onClick={() => { navigate('/addFiles') }}>
                                    <Plus size={20} className="mr-2" />
                                    Přidat obsah
                                </button>) : (<></>)}
                            <button className="flex items-center px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                                onClick={() => navigate('/addContributors')}>
                                <Users size={20} className="mr-2" />
                                Pozvat přispěvatele
                            </button>
                            <button className="flex items-center px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                                onClick={() => setIsPopupOpen(true)}>
                                <Lock size={20} className="mr-2" />
                                Uzamknout
                            </button>
                        </div>) : (
                        <div className="flex flex-col space-y-4 sm:flex-row sm:space-y-0 sm:space-x-4 mb-6">
                            <button
                                className="flex items-center px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                                onClick={() => navigate('/addContributors')}
                            >
                                <Users size={20} className="mr-2" />
                                Pozvat přispěvatele
                            </button>
                            {capsule.status === 'closed' && (
                                <button
                                    onClick={handleEarlyOpen}
                                    className="px-3 py-1 text-white bg-blue-900 rounded-lg hover:bg-blue-600"
                                >
                                    Předčasně otevřít
                                </button>
                            )}
                        </div>

                    )}


                    {capsule.status === 'closed' ? (
                        <div className="flex justify-center items-center h-64 bg-blue-50 rounded-lg">
                            <Lock size={100} className="text-gray-600" />
                        </div>
                    ) : (
                        <>
                            {/* Content Grid */}
                            <div className="mb-8">
                                <h2 className="text-xl font-semibold mb-4">Obsah kapsle</h2>
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                                    {capsule.items.map((item) => (
                                        <div key={item.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                                            {(item.type === 'image' || item.type === 'video') && (
                                                <div className="relative h-48">
                                                    <img
                                                        src={item.thumbnail}
                                                        alt={item.title}
                                                        className="w-full h-full object-cover"
                                                    />
                                                    {item.type === 'video' && (
                                                        <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-30">
                                                            <Video size={40} className="text-white" />
                                                        </div>
                                                    )}
                                                </div>
                                            )}
                                            {(item.type === 'text' || item.type === 'audio') && (
                                                <div className="h-48 bg-gray-100 flex items-center justify-center">
                                                    {getItemIcon(item.type)}
                                                </div>
                                            )}
                                            <div className="p-4">
                                                <div className="flex items-center justify-between mb-2">
                                                    <h3 className="font-medium">{item.title}</h3>
                                                </div>
                                                <div className="flex items-center justify-between text-sm text-gray-600">
                                                    <span>Přidal(a) {item.addedBy}</span>
                                                    <span>{new Date(item.addedDate).toLocaleDateString()}</span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </>
                    )}
                </div>

                {/* Contributors */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h2 className="text-xl font-semibold mb-4">Přispěvatelé</h2>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        {capsule.contributors.map((contributor) => (
                            <div key={contributor.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                <div className="flex items-center">
                                    <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center mr-3">
                                        {contributor.avatar}
                                    </div>
                                    <div>
                                        <div className="font-medium">{contributor.email}</div>
                                        {contributor.id === 1 && (
                                            <div className="text-sm text-gray-600">Tvůrce kapsle</div>
                                        )}
                                    </div>
                                </div>
                                <button className="text-gray-400 hover:text-gray-600"
                                    onClick={() => handleNavigateToFollower(contributor.id)} >
                                    <ChevronRight size={16} />
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            </main >
            {/* Confirm Popup */}
            < ConfirmPopup
                isOpen={isPopupOpen}
                onClose={() => setIsPopupOpen(false)}
                onConfirm={handleLockCapsule}
                text="Opravdu chcete uzamknout kapsli?"
            />
        </div >
    );
};

export default CapsuleDetail;

