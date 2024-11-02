import {
    Clock,
    Plus,
    Lock,
    Unlock,
    Share2,
    Search,
    ChevronDown,
    Settings,
    Bell
} from 'lucide-react';

import React, { useState } from 'react';
import { Page, User, Capsule } from '../../types';

interface DashboardProps {
    setCurrentPage: (page: Page) => void;
    user: User;
    setSelectedCapsule: (capsule: Capsule) => void;
}

const Dashboard = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState('all');

    // Mock data pro demonstraci
    const stats = {
        totalCapsules: 12,
        pendingOpen: 5,
        sharedWithMe: 3
    };

    const capsules = [
        {
            id: 1,
            title: "Maturitní vzpomínky 2024",
            openDate: "2025-06-30",
            status: "pending",
            contributors: 5,
            thumbnail: "/api/placeholder/320/180",
            type: "own"
        },
        {
            id: 2,
            title: "Naše svatba",
            openDate: "2024-12-24",
            status: "pending",
            contributors: 8,
            thumbnail: "/api/placeholder/320/180",
            type: "own"
        },
        {
            id: 3,
            title: "Rodinná historie",
            openDate: "2024-11-15",
            status: "opened",
            contributors: 3,
            thumbnail: "/api/placeholder/320/180",
            type: "shared"
        }
    ];

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Horní navigační lišta */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                        <div className="flex items-center space-x-4">
                            <button className="p-2 text-gray-600 hover:text-blue-900">
                                <Bell size={20} />
                            </button>
                            <button className="p-2 text-gray-600 hover:text-blue-900">
                                <Settings size={20} />
                            </button>
                            <div className="flex items-center space-x-2">
                                <div className="w-8 h-8 rounded-full bg-blue-900 text-white flex items-center justify-center">
                                    JD
                                </div>
                                <ChevronDown size={16} />
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* Uvítací sekce */}
                <div className="mb-8">
                    <h1 className="text-2xl font-bold text-gray-900">Vítejte zpět, Jane!</h1>
                    <p className="text-gray-600">Máte {stats.pendingOpen} kapslí čekajících na otevření</p>
                </div>

                {/* Statistiky */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Celkem kapslí</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.totalCapsules}</div>
                    </div>
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Čeká na otevření</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.pendingOpen}</div>
                    </div>
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Sdíleno se mnou</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.sharedWithMe}</div>
                    </div>
                </div>

                {/* Vyhledávání a filtry */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 space-y-4 md:space-y-0">
                    <div className="relative flex-grow max-w-md">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Hledat kapsle..."
                            className="pl-10 pr-4 py-2 w-full border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                    </div>
                    <div className="flex space-x-4">
                        <select
                            className="border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-900"
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                        >
                            <option value="all">Všechny kapsle</option>
                            <option value="pending">Čekající</option>
                            <option value="opened">Otevřené</option>
                        </select>
                        <button className="bg-blue-900 text-white px-4 py-2 rounded-lg flex items-center space-x-2">
                            <Plus size={20} />
                            <span>Nová kapsle</span>
                        </button>
                    </div>
                </div>

                {/* Seznam kapslí */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {capsules.map((capsule) => (
                        <div key={capsule.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                            <div className="relative">
                                <img
                                    src={capsule.thumbnail}
                                    alt={capsule.title}
                                    className="w-full h-48 object-cover"
                                />
                                <div className="absolute top-2 right-2 bg-white rounded-full p-2">
                                    {capsule.status === 'pending' ? <Lock size={16} /> : <Unlock size={16} />}
                                </div>
                            </div>
                            <div className="p-4">
                                <h3 className="font-semibold text-lg mb-2">{capsule.title}</h3>
                                <div className="flex items-center text-gray-600 text-sm mb-3">
                                    <Clock size={16} className="mr-1" />
                                    <span>Otevření: {new Date(capsule.openDate).toLocaleDateString()}</span>
                                </div>
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center text-gray-600 text-sm">
                                        <Share2 size={16} className="mr-1" />
                                        <span>{capsule.contributors} přispěvatelů</span>
                                    </div>
                                    <button className="text-blue-900 hover:text-blue-700 font-medium">
                                        Zobrazit detail
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Prázdný stav */}
                {capsules.length === 0 && (
                    <div className="text-center py-12">
                        <div className="text-gray-400 mb-4">Zatím nemáte žádné kapsle</div>
                        <button className="bg-blue-900 text-white px-6 py-3 rounded-lg flex items-center space-x-2 mx-auto">
                            <Plus size={20} />
                            <span>Vytvořit první kapsli</span>
                        </button>
                    </div>
                )}
            </main>
        </div>
    );
};

export default Dashboard;