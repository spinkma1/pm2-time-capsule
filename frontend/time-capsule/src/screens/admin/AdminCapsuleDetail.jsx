import React, { useState } from 'react';
import {
    ArrowLeft,
    Calendar,
    Clock,
    Share2,
    Eye,
    Edit,
    Trash2,
    Lock,
    Unlock,
    User,
    Mail,
    Image,
    FileText,
    Video,
    Music,
    MoreVertical,
    Check
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

const AdminCapsuleDetail = () => {
    const {capsuleId} = useParams();
    const navigate = useNavigate();

    // Mock data pro demonstraci
    const capsule = {
        id: 1,
        title: "Maturitní vzpomínky 2024",
        description: "Vzpomínky na společná školní léta.",
        status: "closed",
        createdDate: "2024-01-15",
        openDate: "2024-12-24",
        creator: {
            email: "jan.novak@email.cz",
            id: 1
        },
        contributors: [
            { id: 1, email: "jan.novak@email.cz", status: "active" },
            { id: 2, email: "petra.svobodova@email.cz", status: "pending" }
        ],
        items: [
            {
                id: 1,
                type: "image",
                title: "Třídní foto",
                addedBy: "jan.novak@email.cz",
                addedDate: "2024-01-16",
                thumbnail: "/api/placeholder/400/300"
            },
            {
                id: 2,
                type: "video",
                title: "Maturitní video",
                addedBy: "petra.svobodova@email.cz",
                addedDate: "2024-01-17",
                thumbnail: "/api/placeholder/400/300"
            }
        ]
    };

    const getItemIcon = (type) => {
        switch(type) {
            case 'image': return <Image size={20} />;
            case 'video': return <Video size={20} />;
            case 'text': return <FileText size={20} />;
            case 'audio': return <Music size={20} />;
            default: return <FileText size={20} />;
        }
    };

    const handleStatusChange = (newStatus) => {
        // Implementace změny stavu kapsle
        console.log(`Changing capsule status to ${newStatus}`);
    };

    const handleDeleteCapsule = () => {
        // TODO
        console.log('Deleting capsule');
        navigate('/admin');
    };

    const handleNotifyContributors = () => {
        // TODO
        console.log('Notifying contributors');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <button
                            onClick={() => navigate(-1)}
                            className="flex items-center text-gray-600 hover:text-blue-900"
                        >
                            <ArrowLeft size={20} className="mr-2" />
                            Zpět
                        </button>
                        <div className="text-2xl font-bold text-blue-900">
                            Detail kapsle
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* Capsule Info Card */}
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <div className="flex justify-between items-start mb-6">
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-2">{capsule.title}</h2>
                            <p className="text-gray-600 mb-4">{capsule.description}</p>
                            <div className="grid grid-cols-2 gap-4 text-sm">
                                <div className="flex items-center">
                                    <Calendar size={16} className="mr-2 text-gray-400" />
                                    <span className="text-gray-500">Vytvořeno: </span>
                                    <span className="ml-1">{capsule.createdDate}</span>
                                </div>
                                <button
                                    onClick={handleNotifyContributors}
                                    className="flex items-center justify-center px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                >
                                    <Mail size={20} className="mr-2" />
                                    Notifikovat přispěvatele
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Contributors Section */}
                    <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                        <h3 className="text-xl font-bold text-gray-900 mb-6">Přispěvatelé</h3>
                        <div className="space-y-4">
                            {capsule.contributors.map((contributor) => (
                                <div key={contributor.id}
                                     className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                                    <div className="flex items-center">
                                        <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center">
                                            {contributor.email.substring(0, 2).toUpperCase()}
                                        </div>
                                        <div className="ml-4">
                                            <div className="font-medium">{contributor.email}</div>
                                            <div className="text-sm text-gray-500">
                                                {contributor.status === 'active' ? 'Aktivní' : 'Čeká na potvrzení'}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                        <button
                                            className={`px-3 py-1 rounded-full text-sm font-medium
                      ${contributor.status === 'active'
                                                ? 'bg-green-100 text-green-800'
                                                : 'bg-yellow-100 text-yellow-800'}`}
                                        >
                                            {contributor.status === 'active' ? 'Aktivní' : 'Čeká'}
                                        </button>
                                        <button className="p-2 text-gray-400 hover:text-gray-600">
                                            <MoreVertical size={20} />
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Content Section */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h3 className="text-xl font-bold text-gray-900 mb-6">Obsah kapsle</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {capsule.items.map((item) => (
                                <div key={item.id} className="bg-gray-50 rounded-lg overflow-hidden border border-gray-200">
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
                                            <h4 className="font-medium">{item.title}</h4>
                                            <button className="text-gray-400 hover:text-gray-600">
                                                <MoreVertical size={16} />
                                            </button>
                                        </div>
                                        <div className="flex items-center justify-between text-sm text-gray-600">
                                            <span>Přidal(a): {item.addedBy}</span>
                                            <span>{item.addedDate}</span>
                                        </div>
                                        <div className="mt-4 flex justify-end space-x-2">
                                            <button className="p-2 text-blue-900 hover:bg-blue-50 rounded">
                                                <Eye size={18} />
                                            </button>
                                            <button className="p-2 text-gray-600 hover:bg-gray-100 rounded">
                                                <Edit size={18} />
                                            </button>
                                            <button className="p-2 text-red-600 hover:bg-red-50 rounded">
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminCapsuleDetail;