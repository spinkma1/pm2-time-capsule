import React, { useState } from 'react';
import { Search, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import {ApiService} from "../../api/api.js";

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    const handleSearch = async (query) => {
        setSearchQuery(query);

        if (query.length > 2) {
            try {
                const response = await ApiService.findEmails(query);
                console.log("API Response:", response);

                // Ensure response is an array
                if (Array.isArray(response)) {
                    setSearchResults(response.map(email => ({ email })));
                } else {
                    console.error("Unexpected response format:", response);
                    setSearchResults([]); // Reset results if unexpected format
                }
            } catch (error) {
                console.error("Error fetching emails:", error);
            }
        } else {
            setSearchResults([]);
        }
    };


    const handleUserSelect = async (email) => {
        try {
            // Assuming ApiService.getUserByEmail is working as expected:
            const response = await ApiService.getUserByEmail(email);
            console.warn("API Response:", response);
            navigate(`/admin/user/${email}`); // Ensure it uses email as the identifier
        } catch (error) {
            console.error('Error fetching user details:', error);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <button
                            onClick={() => navigate('/dashboard')}
                            className="flex items-center text-gray-600 hover:text-blue-900">
                            <ArrowLeft size={20} className="mr-2" />
                            Zpět na dashboard
                        </button>
                        <div className="text-2xl font-bold text-blue-900">Administrace</div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="max-w-3xl mx-auto">
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

                        {searchResults.length > 0 && (
                            <div className="mt-4 space-y-2">
                                {searchResults.map((user, index) => (
                                    <div
                                        key={index}
                                        className="flex items-center justify-between p-4 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100"
                                        onClick={() => handleUserSelect(user.email)}
                                    >
                                        <div className="flex items-center">
                                            <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center">
                                                {user.email.substring(0, 2).toUpperCase()}
                                            </div>
                                            <div className="ml-4">
                                                <div className="font-medium">{user.email}</div>
                                            </div>
                                        </div>
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
