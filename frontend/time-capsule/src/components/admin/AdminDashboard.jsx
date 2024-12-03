import React, { useState } from 'react';
import { Search, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    // Mock data
    const mockUsers = [
        {
            id: 1,
            email: 'jan.novak@email.cz',
            capsuleCount: 5,
            status: 'active'
        },
        {
            id: 2,
            email: 'petra.svobodova@email.cz',
            capsuleCount: 3,
            status: 'blocked'
        }
    ];

    const handleSearch = (query) => {
        setSearchQuery(query);
        if (query.length > 2) {
            // Simulace vyhledávání
            const results = mockUsers.filter(user =>
                user.email.toLowerCase().includes(query.toLowerCase())
            );
            setSearchResults(results);
        } else {
            setSearchResults([]);
        }
    };

    const handleUserSelect = (userId) => {
        navigate(`/admin/user/${userId}`);
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
                            Zpět na dashboard
                        </button>
                        <div className="text-2xl font-bold text-blue-900">
                            Administrace
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="max-w-3xl mx-auto">
                    {/* Search Section */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold text-gray-900 mb-6">Vyhledat uživatele</h2>
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                            <input
                                type="text"
                                placeholder="Zadejte email uživatele..."
                                className="pl-10 pr-4 py-3 w-full border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                                value={searchQuery}
                                onChange={(e) => handleSearch(e.target.value)}
                            />
                        </div>

                        {/* Search Results */}
                        {searchResults.length > 0 && (
                            <div className="mt-4 space-y-2">
                                {searchResults.map((user) => (
                                    <div
                                        key={user.id}
                                        className="flex items-center justify-between p-4 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100"
                                        onClick={() => handleUserSelect(user.id)}
                                    >
                                        <div className="flex items-center">
                                            <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center">
                                                {user.email.substring(0, 2).toUpperCase()}
                                            </div>
                                            <div className="ml-4">
                                                <div className="font-medium">{user.email}</div>
                                                <div className="text-sm text-gray-500">
                                                    {user.capsuleCount} kapslí
                                                </div>
                                            </div>
                                        </div>
                                        <span className={`px-2 py-1 text-xs font-semibold rounded-full 
                      ${user.status === 'active'
                                            ? 'bg-green-100 text-green-800'
                                            : 'bg-red-100 text-red-800'}`}>
                      {user.status === 'active' ? 'Aktivní' : 'Blokován'}
                    </span>
                                    </div>
                                ))}
                            </div>
                        )}

                        {searchQuery.length > 2 && searchResults.length === 0 && (
                            <div className="mt-4 text-center text-gray-500">
                                Žádní uživatelé nenalezeni
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminDashboard;