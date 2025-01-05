import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react'; // Import ikony pro zpětné tlačítko

const User = () => {
    const navigate = useNavigate();

    // Stav pro profilová data
    const [user] = useState({
        initials: 'JD', // Výchozí iniciály
        name: 'Jan Novák',
        email: 'jan.novakasfasfasfasfasfasffasak@email.cz',
        bio: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. ligula ultricies lacinia. Nullam nec purus nec lig ligula ultricies lacinia. Nullam nec purus nec lig Nullam nec purus nec ligula ultricies lacinia. Nullam nec purus nec lig'
    });

    const capsules = [
        {
            id: 1,
            title: "Maturitní vzpomínky 2024",
            openDate: null,
            createdDate: "2024-01-01",
            status: "EDIT",
            contributorsAmount: 3,
            thumbnail: null,
            maxItems: 5,
            type: "own",
            public: true,
            contributors: [
                { id: 1, email: "jan.novak@seznam.cz", avatar: "JN" },
                { id: 2, email: "m.svoboda@gmail.com", avatar: "MS" },
                { id: 3, email: "petr.dvorak420@fel.cvut.cz", avatar: "PD" }
            ],
            items: [
                { id: 1, type: "image", title: "tridnifoto.jpg", addedBy: "Jan Novák", addedDate: "2024-01-15", thumbnail: "/api/placeholder/400/300" },
                { id: 2, type: "video", title: "posledni_zvoneni.mp4", addedBy: "Marie Svobodová", addedDate: "2024-01-16", thumbnail: "/api/placeholder/400/300" },
                { id: 3, type: "text", title: "vzkaz_pro_budouci_ja.txt", addedBy: "Petr Dvořák", addedDate: "2024-01-17" },
                { id: 4, type: "audio", title: "nase_oblibena_pisnicka.mp3", addedBy: "Jan Novák", addedDate: "2024-01-18" }
            ]
        },
        {
            id: 2,
            title: "Naše svatba",
            openDate: "2024-12-24",
            createdDate: "2024-01-01",
            status: "CLOSE",
            contributorsAmount: 3,
            thumbnail: null,
            maxItems: 3,
            type: "own",
            public: true,
            contributors: [
                { id: 1, email: "jan.novak@seznam.cz", avatar: "JN" },
                { id: 2, email: "m.svoboda@gmail.com", avatar: "MS" },
                { id: 3, email: "petr.dvorak420@fel.cvut.cz", avatar: "PD" }
            ],
            items: [
                { id: 1, type: "image", title: "tridnifoto.jpg", addedBy: "Jan Novák", addedDate: "2024-01-15", thumbnail: "/api/placeholder/400/300" },
                { id: 2, type: "video", title: "posledni_zvoneni.mp4", addedBy: "Marie Svobodová", addedDate: "2024-01-16", thumbnail: "/api/placeholder/400/300" },
                { id: 3, type: "text", title: "vzkaz_pro_budouci_ja.txt", addedBy: "Petr Dvořák", addedDate: "2024-01-17" },
                { id: 4, type: "audio", title: "nase_oblibena_pisnicka.mp3", addedBy: "Jan Novák", addedDate: "2024-01-18" }
            ]
        },
        {
            id: 3,
            title: "Rodinná historie",
            openDate: "2024-11-15",
            createdDate: "2024-01-01",
            status: "OPEN",
            contributorsAmount: 3,
            thumbnail: null,
            maxItems: 3,
            type: "shared",
            public: true,
            contributors: [
                { id: 1, email: "jan.novak@seznam.cz", avatar: "JN" },
                { id: 2, email: "m.svoboda@gmail.com", avatar: "MS" },
                { id: 3, email: "petr.dvorak420@fel.cvut.cz", avatar: "PD" }
            ],
            items: [
                { id: 1, type: "image", title: "tridnifoto.jpg", addedBy: "Jan Novák", addedDate: "2024-01-15", thumbnail: "/api/placeholder/400/300" },
                { id: 2, type: "video", title: "posledni_zvoneni.mp4", addedBy: "Marie Svobodová", addedDate: "2024-01-16", thumbnail: "/api/placeholder/400/300" },
                { id: 3, type: "text", title: "vzkaz_pro_budouci_ja.txt", addedBy: "Petr Dvořák", addedDate: "2024-01-17" },
                { id: 4, type: "audio", title: "nase_oblibena_pisnicka.mp3", addedBy: "Jan Novák", addedDate: "2024-01-18" }
            ]
        }
    ];

    // Funkce pro návrat zpět na přehled
    const handleNavigateBack = () => {
        navigate('/dashboard');
    };

    // Filtrujeme pouze kapsle s public: true
    const filteredCapsules = capsules.filter(capsule => capsule.public);

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={handleNavigateBack}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět na přehled
                    </button>
                </div>
            </header>

            <main className="container px-4 py-8">
                <div className="max-w-5xl mx-auto">
                    <div className="flex flex-col md:flex-row gap-6">
                        {/* Main Content */}
                        <div className="flex-1 bg-white rounded-lg shadow-sm p-6">
                            <div className="max-w-5xl mx-auto">
                                <h2 className="text-2xl font-bold text-gray-900 mb-6">Profil uživatele</h2>

                                {/* Profile Picture Section */}
                                <div className="flex items-center mb-8">
                                    <div className="relative">
                                        <div className="w-24 h-24 bg-blue-900 rounded-full flex items-center justify-center text-white text-2xl">
                                            {user.initials}
                                        </div>
                                    </div>
                                    <div className="ml-6 w-full">
                                        <h3 className="text-xl font-semibold text-gray-900 break-all">
                                            {user.email}
                                        </h3>
                                    </div>
                                </div>
                                {/* Follow Button */}
                                <div className="flex justify-start my-5">
                                    <button
                                        type="submit"
                                        className="px-6 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                    >
                                        Sledovat
                                    </button>
                                </div>

                                {/* Zobrazení profilových informací */}
                                <div className="space-y-6">
                                    {/* Bio */}
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            O mně
                                        </label>
                                        <div className="w-full px-4 py-2 border border-gray-300 rounded-lg text-gray-700">
                                            {user.bio}
                                        </div>
                                    </div>
                                </div>

                                <div className="my-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Veřejné kapsle
                                    </label>
                                    {/* Capsules List - zobrazení pouze těch, které mají public: true */}
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

                                        {filteredCapsules.map(capsule => (
                                            <div key={capsule.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                                                <div className="relative">
                                                    <img
                                                        src={capsule.thumbnail ? capsule.thumbnail : "/placeholder.png"}
                                                        alt={capsule.title}
                                                        className="w-full h-48 object-cover"
                                                    />
                                                </div>
                                                <div className="p-4">
                                                    <h3 className="font-semibold text-lg mb-2">{capsule.title}</h3>
                                                    <div className="flex items-center text-gray-600 text-sm mb-3">
                                                        {capsule.openDate !== null && (
                                                            <>
                                                                <span>Otevření: {new Date(capsule.openDate).toLocaleDateString()}</span>
                                                            </>
                                                        )}
                                                    </div>
                                                    <div className="flex items-center justify-between">
                                                        <div className="flex items-center text-gray-600 text-sm">
                                                            <span>{capsule.contributorsAmount} přispěvatelů</span>
                                                        </div>
                                                    
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default User;

