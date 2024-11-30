import React, { useState } from 'react';
import {
    Search,
    Ban,
    ArrowLeft,
    Lock,
    Mail,
    Eye,
    Edit,
    Trash2,
    Clock,
    Share2,
    User,
    Calendar,
    Unlock
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import { useParams } from 'react-router-dom';

const AdminUserDetail = () => {
    const { userId } = useParams();
    const navigate = useNavigate();

    // Mock data
    const user = {
        id: 1,
        email: "jan.novak@email.cz",
        status: "active",
        registeredDate: "2024-01-15",
        lastLogin: "2024-03-20",
        capsuleCount: 5,
        accountType: "premium"
    };

    const capsules = [
        {
            id: 1,
            title: "Maturitní vzpomínky 2024",
            status: "closed",
            createdDate: "2024-01-15",
            openDate: "2024-12-24",
            contributorsCount: 5,
            itemsCount: 12
        },
        // ... další kapsle
    ];

    const [isBlocked, setIsBlocked] = useState(user.status === 'blocked');

    const handleBlockUser = () => {
        setIsBlocked(!isBlocked);
        // Implementace blokování uživatele
    };

    const handleSendEmail = () => {
        // Implementace odeslání emailu
    };

    const handleCapsuleAction = (capsuleId, action) => {
        console.log(`Performing ${action} on capsule ${capsuleId}`);
        if (action === 'view') {
            navigate(`/admin/capsule/${capsuleId}`);
        }
        // Implementace dalších akcí
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <button
                            onClick={() => navigate('/adminDashboard')}
                            className="flex items-center text-gray-600 hover:text-blue-900"
                        >
                            <ArrowLeft size={20} className="mr-2" />
                            Zpět na vyhledávání
                        </button>
                        <div className="text-2xl font-bold text-blue-900">
                            Detail uživatele
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* User Info Card */}
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <div className="flex justify-between items-start">
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-4">{user.email}</h2>
                            <div className="grid grid-cols-2 gap-4 text-sm">
                                <div>
                                    <span className="text-gray-500">Registrován: </span>
                                    <span>{user.registeredDate}</span>
                                </div>
                                <div>
                                    <span className="text-gray-500">Poslední přihlášení: </span>
                                    <span>{user.lastLogin}</span>
                                </div>
                                <div>
                                    <span className="text-gray-500">Typ účtu: </span>
                                    <span className="capitalize">{user.accountType}</span>
                                </div>
                                <div>
                                    <span className="text-gray-500">Počet kapslí: </span>
                                    <span>{user.capsuleCount}</span>
                                </div>
                            </div>
                        </div>

                        <div className="flex space-x-4">
                            <button
                                onClick={handleBlockUser}
                                className={`flex items-center px-4 py-2 rounded-lg ${
                                    isBlocked
                                        ? 'bg-green-600 text-white hover:bg-green-700'
                                        : 'bg-red-600 text-white hover:bg-red-700'
                                }`}
                            >
                                {isBlocked ? <Unlock size={20} className="mr-2" /> : <Ban size={20} className="mr-2" />}
                                {isBlocked ? 'Odblokovat účet' : 'Zablokovat účet'}
                            </button>
                            <button
                                onClick={handleSendEmail}
                                className="flex items-center px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                            >
                                <Mail size={20} className="mr-2" />
                                Poslat email
                            </button>
                        </div>
                    </div>
                </div>

                {/* Capsules List */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h3 className="text-xl font-bold text-gray-900 mb-6">Kapsle uživatele</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {capsules.map((capsule) => (
                            <div key={capsule.id} className="bg-gray-50 rounded-lg p-4 border border-gray-200">
                                <div className="flex justify-between items-start mb-4">
                                    <h4 className="font-semibold text-lg">{capsule.title}</h4>
                                    <span className={`px-2 py-1 text-xs font-semibold rounded-full 
                    ${capsule.status === 'opened' ? 'bg-green-100 text-green-800' :
                                        capsule.status === 'closed' ? 'bg-yellow-100 text-yellow-800' :
                                            'bg-blue-100 text-blue-800'}`}>
                    {capsule.status === 'opened' ? 'Otevřená' :
                        capsule.status === 'closed' ? 'Uzavřená' : 'Aktivní'}
                  </span>
                                </div>

                                <div className="space-y-2 text-sm text-gray-600 mb-4">
                                    <div className="flex items-center">
                                        <Calendar size={16} className="mr-2" />
                                        Vytvořeno: {capsule.createdDate}
                                    </div>
                                    <div className="flex items-center">
                                        <Clock size={16} className="mr-2" />
                                        Otevření: {capsule.openDate}
                                    </div>
                                    <div className="flex items-center">
                                        <Share2 size={16} className="mr-2" />
                                        {capsule.contributorsCount} přispěvatelů
                                    </div>
                                    <div className="flex items-center">
                                        <Eye size={16} className="mr-2" />
                                        {capsule.itemsCount} položek
                                    </div>
                                </div>

                                <div className="flex justify-end space-x-2">
                                    <button
                                        onClick={() => handleCapsuleAction(capsule.id, 'view')}
                                        className="p-2 text-blue-900 hover:bg-blue-50 rounded"
                                        title="Zobrazit detail"
                                    >
                                        <Eye size={20} />
                                    </button>
                                    <button
                                        onClick={() => handleCapsuleAction(capsule.id, 'edit')}
                                        className="p-2 text-gray-600 hover:bg-gray-100 rounded"
                                        title="Upravit"
                                    >
                                        <Edit size={20} />
                                    </button>
                                    <button
                                        onClick={() => handleCapsuleAction(capsule.id, 'delete')}
                                        className="p-2 text-red-600 hover:bg-red-50 rounded"
                                        title="Smazat"
                                    >
                                        <Trash2 size={20} />
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminUserDetail;